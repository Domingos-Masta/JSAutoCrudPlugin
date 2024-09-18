package com.isysdcore.jsautocrud.springautocrudclassgenerator;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * @author domingos.fernando
 * @created 17/09/2024 - 21:28
 * @project SpringAutoCrudClassGenerator
 */
public class GenerateClassAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        String className = Messages.showInputDialog(
                project,
                "Enter the class name:",
                "Generate Class",
                Messages.getQuestionIcon()
        );

        if (className != null && !className.isEmpty()) {
            createClass(project, className);
        }
    }

    private void createClass(Project project, String className) {
        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        VirtualFile srcFolder = getSrcFolder(project);
        String packageName = findGroupName(project.getBasePath());
        if (directory == null) {
            return;
        }
        if (packageName == null) {
            return;
        }
        // Create the class content
        String classContent = Constants.ENTITY_CLASS_CONTENT.replace(Constants.ENTITY_CC_PLACEHOLDER, className);
        String repositoryContent = Constants.REPOSITORY_CLASS_CONTENT.replace(Constants.ENTITY_CC_PLACEHOLDER, className);
        String serviceContent = Constants.SERVICE_CLASS_CONTENT.replace(Constants.ENTITY_CC_PLACEHOLDER, className);
        String controllerContent = Constants.CONTROLLER_CLASS_CONTENT.replace(Constants.ENTITY_CC_PLACEHOLDER, className)
                .replace(Constants.ENTITY_LC_PLACEHOLDER, className.toLowerCase());

        // Run the action inside a write command
        // Execute this in a write command context
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                // Get PsiManager from the project
                PsiManager psiManager = PsiManager.getInstance(project);
                PsiDirectory psiDirectory = psiManager.findDirectory(srcFolder);

                if (psiDirectory != null) {
                    String finalPackageNAme = packageName + "." + className.toLowerCase().trim();
                    // Split package name into directories
                    String[] packageParts = finalPackageNAme.split("\\.");
                    PsiDirectory currentDirectory = psiDirectory;
                    // Loop through and create subdirectories
                    for (String part : packageParts) {
                        PsiDirectory subdirectory = currentDirectory.findSubdirectory(part);
                        if (subdirectory == null) {
                            subdirectory = currentDirectory.createSubdirectory(part);
                        }
                        currentDirectory = subdirectory;
                    }
                    System.out.println("Package " + packageName + " created.");

                    PsiJavaFile javaEntityFile = (PsiJavaFile) fileFactory.createFileFromText(
                            className + Constants.CLASS_EXTENSION,
                            JavaFileType.INSTANCE,
                            "package " + finalPackageNAme + ";\n"
                                    + classContent
                    );
                    PsiJavaFile javaRepoFile = (PsiJavaFile) fileFactory.createFileFromText(
                            className + Constants.REPOSITORY_NAME + Constants.CLASS_EXTENSION,
                            JavaFileType.INSTANCE,
                            "package " + finalPackageNAme + ";\n"
                                    + repositoryContent
                    );
                    PsiJavaFile javaServiceFile = (PsiJavaFile) fileFactory.createFileFromText(
                            className + Constants.SERVICE_NAME + Constants.CLASS_EXTENSION,
                            JavaFileType.INSTANCE,
                            "package " + finalPackageNAme + ";\n"
                                    + serviceContent
                    );
                    PsiJavaFile javaControllerFile = (PsiJavaFile) fileFactory.createFileFromText(
                            className + Constants.CONTROLLER_NAME + Constants.CLASS_EXTENSION,
                            JavaFileType.INSTANCE,
                            "package " + finalPackageNAme + ";\n"
                                    + controllerContent
                    );
                    currentDirectory.add(javaEntityFile);
                    currentDirectory.add(javaRepoFile);
                    currentDirectory.add(javaServiceFile);
                    currentDirectory.add(javaControllerFile);
                }
            } catch (Exception e) {
                System.err.println("Failed to create package: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public String findGroupName(String pathToPom){
        try {
            // Path to the pom.xml file
            File pomFile = new File(pathToPom + "/pom.xml");
            // Create a document builder
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pomFile);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get the groupId element
            NodeList nodeList = doc.getElementsByTagName("groupId");
            NodeList parent = doc.getElementsByTagName("project");
            for (int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getParentNode().isEqualNode(parent.item(0))){
                    String groupId = node.getTextContent();
                    return groupId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public VirtualFile getSrcFolder(Project project) {
        VirtualFile baseDir = project.getBaseDir(); // Get the project base directory
        if (baseDir != null) {
            baseDir = baseDir.findChild("src");
            assert baseDir != null;
            baseDir = baseDir.findChild("main");
            assert baseDir != null;
            return baseDir.findChild("java"); // Find the "src" folder
        }
        return null;
    }
}
