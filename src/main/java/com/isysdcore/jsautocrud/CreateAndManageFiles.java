package com.isysdcore.jsautocrud;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.isysdcore.jsautocrud.model.ClassAssociation;
import com.isysdcore.jsautocrud.model.ClassParameters;
import com.isysdcore.jsautocrud.model.EDbType;
import com.isysdcore.jsautocrud.model.ERelType;
import com.isysdcore.jsautocrud.model.EntityClassFilesContent;
import com.isysdcore.jsautocrud.util.Constants;
import com.isysdcore.jsautocrud.util.ECreationMode;
import com.isysdcore.jsautocrud.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates the Spring Boot CRUD classes on disk using the generic-auto-crud library.
 */
public class CreateAndManageFiles {

    private static final Logger log = LoggerFactory.getLogger(CreateAndManageFiles.class);

    public void runLoadingTask(Project project, ECreationMode creationMode, List<ClassParameters> classParametersList) {
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Generating JSAutoCrud classes...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                List<EntityClassFilesContent> filesContent = prepareFilesContent(creationMode, classParametersList);
                List<GeneratedFile> generated = createClasses(project, filesContent, indicator);
                notifyResult(project, generated);
            }
        };
        ProgressManager.getInstance().run(backgroundTask);
    }

    // ------------------------------------------------------------------ plan

    private List<EntityClassFilesContent> prepareFilesContent(ECreationMode mode, List<ClassParameters> classParametersList) {
        Map<UUID, String> associationBlocks = buildAssociationBlocks(classParametersList);
        List<EntityClassFilesContent> result = new ArrayList<>();

        for (ClassParameters cp : classParametersList) {
            Map<String, Map<String, String>> files = new LinkedHashMap<>();

            boolean doEntity = mode == ECreationMode.FROM_SCRATCH;
            boolean doRepository;
            boolean doService;
            boolean doController;

            switch (mode) {
                case ONLY_REPOSITORIES -> {
                    doRepository = true;
                    doService = false;
                    doController = false;
                }
                case ONLY_SERVICES -> {
                    doRepository = false;
                    doService = true;
                    doController = false;
                }
                case ONLY_CONTROLLERS -> {
                    doRepository = false;
                    doService = false;
                    doController = true;
                }
                case FROM_MODEL -> {
                    doService = cp.createSrvs() || cp.createCtrl() || cp.generateDto();
                    doController = cp.createCtrl() || cp.generateDto();
                    doRepository = doService || doController || cp.generateDto();
                }
                default -> { // FROM_SCRATCH
                    doRepository = true;
                    doService = cp.createSrvs() || cp.createCtrl() || cp.generateDto();
                    doController = cp.createCtrl() || cp.generateDto();
                }
            }

            if (doEntity) {
                String entityTemplate = cp.dbType() == EDbType.MONGODB
                        ? Constants.ENTITY_MONGO_CLASS_CONTENT
                        : Constants.ENTITY_SQL_CLASS_CONTENT;
                Map<String, String> entity = prepareFileContent(entityTemplate, cp, "");
                String withAssociations = entity.get(Constants.F_CONTENT_KEY)
                        .replace(Constants.ENTITY_ASSOCIATIONS_PLACEHOLDER, associationBlocks.getOrDefault(cp.classId(), ""));
                entity.put(Constants.F_CONTENT_KEY, withAssociations);
                files.put(Constants.ENTITY_KEY, entity);
            }

            if (doRepository) {
                String template = cp.dbType() == EDbType.MONGODB
                        ? Constants.REPOSITORY_MONGO_CLASS_CONTENT
                        : Constants.REPOSITORY_SQL_CLASS_CONTENT;
                files.put(Constants.REPOSITORY_KEY, prepareFileContent(template, cp, Constants.REPOSITORY_SUFFIX));
            }

            if (doService) {
                String template = cp.dbType() == EDbType.MONGODB
                        ? Constants.SERVICE_MONGO_CLASS_CONTENT
                        : Constants.SERVICE_SQL_CLASS_CONTENT;
                files.put(Constants.SERVICE_KEY, prepareFileContent(template, cp, Constants.SERVICE_SUFFIX));
            }

            if (doController) {
                files.put(Constants.CONTROLLER_KEY, prepareFileContent(Constants.CONTROLLER_CLASS_CONTENT, cp, Constants.CONTROLLER_SUFFIX));
            }

            if (cp.generateDto()) {
                files.put(Constants.DTO_KEY, prepareFileContent(Constants.DTO_CLASS_CONTENT, cp, Constants.DTO_SUFFIX));
                files.put(Constants.DTO_MAPPER_KEY, prepareFileContent(Constants.DTO_MAPPER_CLASS_CONTENT, cp, Constants.DTO_MAPPER_SUFFIX));
                if (doService) {
                    String template = cp.dbType() == EDbType.MONGODB
                            ? Constants.DTO_SERVICE_MONGO_CLASS_CONTENT
                            : Constants.DTO_SERVICE_SQL_CLASS_CONTENT;
                    files.put(Constants.DTO_SERVICE_KEY, prepareFileContent(template, cp, Constants.DTO_SERVICE_SUFFIX));
                }
                if (doController) {
                    files.put(Constants.DTO_CONTROLLER_KEY, prepareFileContent(Constants.DTO_CONTROLLER_CLASS_CONTENT, cp, Constants.DTO_CONTROLLER_SUFFIX));
                }
            }

            if (cp.generateTest()) {
                files.put(Constants.TEST_KEY, prepareFileContent(Constants.TEST_CLASS_CONTENT, cp, Constants.TEST_SUFFIX));
            }

            result.add(new EntityClassFilesContent(cp, files));
        }
        return result;
    }

    // ----------------------------------------------------------- associations

    private Map<UUID, String> buildAssociationBlocks(List<ClassParameters> classParametersList) {
        Map<UUID, ClassParameters> byId = classParametersList.stream()
                .collect(Collectors.toMap(ClassParameters::classId, cp -> cp));
        Map<UUID, StringBuilder> blocks = new HashMap<>();
        classParametersList.forEach(cp -> blocks.put(cp.classId(), new StringBuilder()));

        for (ClassParameters parent : classParametersList) {
            // JPA association annotations only apply to SQL entities.
            if (parent.dbType() == EDbType.MONGODB) {
                continue;
            }
            for (ClassAssociation association : parent.associations()) {
                ClassParameters child = byId.get(association.childClassId());
                if (child == null) {
                    continue;
                }
                String parentSnippet = associationSnippet(association.eRelType(), parent.className(), child.className(), true);
                String childSnippet = associationSnippet(association.eRelType(), parent.className(), child.className(), false);
                if (parentSnippet != null) {
                    blocks.get(parent.classId()).append(parentSnippet);
                }
                if (childSnippet != null) {
                    blocks.get(child.classId()).append(childSnippet);
                }
            }
        }

        Map<UUID, String> result = new HashMap<>();
        blocks.forEach((id, builder) -> result.put(id, builder.toString()));
        return result;
    }

    private String associationSnippet(ERelType type, String parentName, String childName, boolean isParent) {
        String parentField = Utils.decapitalize(parentName);
        String childField = Utils.decapitalize(childName);
        String indent = "    ";

        if (isParent) {
            return switch (type) {
                case UNIDIRECTIONAL_ONE_TO_ONE -> indent + "@OneToOne\n"
                        + indent + "@JoinColumn(name = \"" + childField + "_id\")\n"
                        + indent + "private " + childName + " " + childField + ";\n\n";
                case UNIDIRECTIONAL_ONE_TO_MANY -> indent + "@OneToMany\n"
                        + indent + "@JoinColumn(name = \"" + parentField + "_id\")\n"
                        + indent + "private List<" + childName + "> " + Utils.pluralize(childField) + ";\n\n";
                case UNIDIRECTIONAL_MANY_TO_ONE -> indent + "@ManyToOne\n"
                        + indent + "@JoinColumn(name = \"" + childField + "_id\")\n"
                        + indent + "private " + childName + " " + childField + ";\n\n";
                case UNIDIRECTIONAL_MANY_TO_MANY -> indent + "@ManyToMany\n"
                        + indent + "@JoinTable(name = \"" + parentField + "_" + childField + "\",\n"
                        + indent + "        joinColumns = @JoinColumn(name = \"" + parentField + "_id\"),\n"
                        + indent + "        inverseJoinColumns = @JoinColumn(name = \"" + childField + "_id\"))\n"
                        + indent + "private Set<" + childName + "> " + Utils.pluralize(childField) + ";\n\n";
                case BIDIRECTIONAL_ONE_TO_ONE -> indent + "@OneToOne(cascade = CascadeType.ALL)\n"
                        + indent + "@JoinColumn(name = \"" + childField + "_id\", referencedColumnName = \"id\")\n"
                        + indent + "private " + childName + " " + childField + ";\n\n";
                case BIDIRECTIONAL_ONE_TO_MANY -> indent + "@OneToMany(mappedBy = \"" + parentField + "\")\n"
                        + indent + "private List<" + childName + "> " + Utils.pluralize(childField) + ";\n\n";
                case BIDIRECTIONAL_MANY_TO_MANY -> indent + "@ManyToMany\n"
                        + indent + "@JoinTable(name = \"" + parentField + "_" + childField + "\",\n"
                        + indent + "        joinColumns = @JoinColumn(name = \"" + parentField + "_id\"),\n"
                        + indent + "        inverseJoinColumns = @JoinColumn(name = \"" + childField + "_id\"))\n"
                        + indent + "private Set<" + childName + "> " + Utils.pluralize(childField) + ";\n\n";
            };
        }

        // Child side only exists for bidirectional relationships.
        return switch (type) {
            case BIDIRECTIONAL_ONE_TO_ONE -> indent + "@OneToOne(mappedBy = \"" + childField + "\")\n"
                    + indent + "private " + parentName + " " + parentField + ";\n\n";
            case BIDIRECTIONAL_ONE_TO_MANY -> indent + "@ManyToOne\n"
                    + indent + "@JoinColumn(name = \"" + parentField + "_id\")\n"
                    + indent + "private " + parentName + " " + parentField + ";\n\n";
            case BIDIRECTIONAL_MANY_TO_MANY -> indent + "@ManyToMany(mappedBy = \"" + Utils.pluralize(childField) + "\")\n"
                    + indent + "private List<" + parentName + "> " + Utils.pluralize(parentField) + ";\n\n";
            default -> null;
        };
    }

    // ----------------------------------------------------------- file writing

    private Map<String, String> prepareFileContent(String template, ClassParameters parameters, String suffix) {
        Map<String, String> fileContent = new HashMap<>();
        fileContent.put(Constants.F_NAME_KEY, parameters.className() + suffix);

        String idImport = "UUID".equals(parameters.idDataType()) ? "import java.util.UUID;\n" : "";

        String content = template.replace(Constants.ENTITY_CC_PLACEHOLDER, parameters.className());
        content = content.replace(Constants.ENTITY_ID_PLACEHOLDER, parameters.idDataType());
        content = content.replace(Constants.ENTITY_ID_IMPORT_PLACEHOLDER, idImport);
        if (content.contains(Constants.ENTITY_LC_PLACEHOLDER)) {
            content = content.replace(Constants.ENTITY_LC_PLACEHOLDER, parameters.className().toLowerCase());
        }
        fileContent.put(Constants.F_CONTENT_KEY, Constants.CLASS_HEADER + content);
        return fileContent;
    }

    private List<GeneratedFile> createClasses(Project project, List<EntityClassFilesContent> contents, ProgressIndicator indicator) {
        List<GeneratedFile> generated = new ArrayList<>();
        String basePackage = detectBasePackage(project);
        int total = contents.stream().mapToInt(c -> c.filesToCreate().size()).sum();
        final int[] count = {0};

        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (EntityClassFilesContent content : contents) {
                for (Map<String, String> file : content.filesToCreate().values()) {
                    count[0]++;
                    boolean created = writeFile(project, content.parameters(), file, basePackage);
                    generated.add(new GeneratedFile(file.get(Constants.F_NAME_KEY), created));
                    indicator.setFraction((double) count[0] / Math.max(total, 1));
                    indicator.setText("Generating " + file.get(Constants.F_NAME_KEY) + ".java");
                }
            }
        });
        return generated;
    }

    private boolean writeFile(Project project, ClassParameters parameters, Map<String, String> file, String basePackage) {
        if (basePackage == null) {
            return false;
        }
        PsiDirectory targetDirectory = getOrCreatePackage(project, basePackage + "." + parameters.className().toLowerCase());
        if (targetDirectory == null) {
            return false;
        }

        String fileName = file.get(Constants.F_NAME_KEY) + Constants.CLASS_EXTENSION;
        if (targetDirectory.findFile(fileName) != null) {
            log.info("Skipping already existing file: {}", fileName);
            return false;
        }

        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
        PsiJavaFile javaFile = (PsiJavaFile) fileFactory.createFileFromText(
                fileName,
                JavaFileType.INSTANCE,
                "package " + basePackage + "." + parameters.className().toLowerCase() + ";\n\n"
                        + file.get(Constants.F_CONTENT_KEY));
        targetDirectory.add(javaFile);
        return true;
    }

    // ------------------------------------------------------- package handling

    private String detectBasePackage(Project project) {
        VirtualFile srcFolder = getSrcFolder(project);
        if (srcFolder != null) {
            List<String> packages = new ArrayList<>();
            collectJavaPackages(srcFolder, "", packages);
            if (!packages.isEmpty()) {
                String common = packages.get(0);
                for (String pkg : packages) {
                    common = commonPrefix(common, pkg);
                }
                if (common != null && !common.isEmpty()) {
                    return common;
                }
            }
        }
        return fallbackPackage(project);
    }

    private void collectJavaPackages(VirtualFile directory, String prefix, List<String> packages) {
        for (VirtualFile child : directory.getChildren()) {
            if (child.isDirectory()) {
                String next = prefix.isEmpty() ? child.getName() : prefix + "." + child.getName();
                collectJavaPackages(child, next, packages);
            } else if (child.getName().endsWith(".java")) {
                packages.add(prefix);
            }
        }
    }

    private String commonPrefix(String a, String b) {
        String[] partsA = a.split("\\.");
        String[] partsB = b.split("\\.");
        int length = Math.min(partsA.length, partsB.length);
        int index = 0;
        while (index < length && partsA[index].equals(partsB[index])) {
            index++;
        }
        return String.join(".", Arrays.copyOf(partsA, index));
    }

    private String fallbackPackage(Project project) {
        String groupId = readPomValue(project, "groupId");
        String artifactId = readPomValue(project, "artifactId");
        String candidate = sanitizePackage((groupId == null ? "com.example" : groupId)
                + "." + (artifactId == null ? "app" : artifactId));
        return candidate.isEmpty() ? "com.example.app" : candidate;
    }

    private String sanitizePackage(String value) {
        String[] parts = value.split("[^a-zA-Z0-9]+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (Character.isDigit(part.charAt(0))) {
                part = "p" + part;
            }
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(part.toLowerCase());
        }
        return builder.toString();
    }

    private VirtualFile getSrcFolder(Project project) {
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir == null) {
            return null;
        }
        VirtualFile src = baseDir.findChild("src");
        if (src == null) {
            return null;
        }
        VirtualFile main = src.findChild("main");
        if (main == null) {
            return null;
        }
        return main.findChild("java");
    }

    private PsiDirectory getOrCreatePackage(Project project, String packageName) {
        VirtualFile srcFolder = getSrcFolder(project);
        if (srcFolder == null) {
            return null;
        }
        PsiDirectory root = PsiManager.getInstance(project).findDirectory(srcFolder);
        if (root == null) {
            return null;
        }
        PsiDirectory current = root;
        for (String part : packageName.split("\\.")) {
            PsiDirectory subdirectory = current.findSubdirectory(part);
            if (subdirectory == null) {
                subdirectory = current.createSubdirectory(part);
            }
            current = subdirectory;
        }
        return current;
    }

    private String readPomValue(Project project, String tagName) {
        try {
            File pomFile = new File(project.getBasePath(), "pom.xml");
            if (!pomFile.exists()) {
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(pomFile);
            document.getDocumentElement().normalize();

            Element projectElement = document.getDocumentElement();
            NodeList nodes = document.getElementsByTagName(tagName);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getParentNode() != null && nodes.item(i).getParentNode().equals(projectElement)) {
                    return nodes.item(i).getTextContent().trim();
                }
            }
        } catch (Exception e) {
            log.warn("Unable to read pom.xml value {}", tagName, e);
        }
        return null;
    }

    // ------------------------------------------------------------- feedback

    private void notifyResult(Project project, List<GeneratedFile> generated) {
        long created = generated.stream().filter(GeneratedFile::created).count();
        long skipped = generated.size() - created;
        ApplicationManager.getApplication().invokeLater(() -> {
            String message = "Generated " + created + " class(es) successfully.";
            if (skipped > 0) {
                message += "\nSkipped " + skipped + " existing file(s) to avoid overwriting your code.";
            }
            Messages.showInfoMessage(project, message, "JSAutoCrud");
        });
    }

    private record GeneratedFile(String name, boolean created) {
    }
}
