package com.isysdcore.jsautocrud;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.isysdcore.jsautocrud.model.ClassParameters;
import com.isysdcore.jsautocrud.ui.ParameterDialog;
import com.isysdcore.jsautocrud.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author domingos.fernando
 * @created 17/09/2024 - 21:28
 * @project SpringAutoCrudClassGenerator
 */
public class GenerateClassAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        ParameterDialog parameterDialog;
        ClassParameters classParameters;
        boolean sentinel;
        if (project == null) {
            return;
        }

        do {
            parameterDialog = new ParameterDialog(project);
            parameterDialog.show();
            classParameters = new ClassParameters(parameterDialog.getEntityNameTf(), parameterDialog.getIdDataTypes(),
                    parameterDialog.getGenerateController(), parameterDialog.getGenerateServices());
            sentinel = false;
            if(parameterDialog.isOK()){
                if (validateInParameters(classParameters, project)) {
                    createClass(project, classParameters);
                }else{
                    sentinel = true;
                }
            }
        } while (sentinel);
    }

    private void createClass(Project project, ClassParameters classParameters) {
        // Create the class content
        Map<String, String> persistClassContent = prepareFileContent(Constants.ENTITY_CLASS_CONTENT,
                classParameters, "");
        Map<String, String> repositoryContent = prepareFileContent(Constants.REPOSITORY_CLASS_CONTENT,
                classParameters, Constants.REPOSITORY_NAME);
        Map<String, String> serviceContent = prepareFileContent(Constants.SERVICE_CLASS_CONTENT,
                classParameters, Constants.SERVICE_NAME);
        Map<String, String> controllerContent = prepareFileContent(Constants.CONTROLLER_CLASS_CONTENT,
                classParameters, Constants.CONTROLLER_NAME);
        // Run the action inside a write command
        // Execute this in a write command context
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                generateClass(classParameters, persistClassContent, project);
                generateClass(classParameters, repositoryContent, project);
                if(classParameters.createCtrl()){
                    generateClass(classParameters, controllerContent, project);
                    generateClass(classParameters, serviceContent, project);
                }else if(classParameters.createSrvs()){
                    generateClass(classParameters, serviceContent, project);
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

    private boolean validateInParameters(ClassParameters classParameters, Project project){
        if(classParameters.className() == null || classParameters.className().trim().isEmpty()){
            Messages.showErrorDialog(project, "Error, Class name can not be empty", "Class Name Error");
            return false;
        }else if(classParameters.className().trim().contains(" ")){
            Messages.showErrorDialog(project, "Error, Class name can not contains empty spaces", "Class Name Error");
            return false;
        }
        return true;
    }

    private void generateClass(ClassParameters classParameters, Map<String, String> classContent, Project project){
        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
        String packageName = findGroupName(project.getBasePath());
        PsiDirectory currentDirectory = getPackageDirectory(classParameters, packageName, project);
        if (packageName == null) {
            return;
        }
        PsiJavaFile javaEntityFile = (PsiJavaFile) fileFactory.createFileFromText(
                classContent.get(Constants.F_NAME_KEY) + Constants.CLASS_EXTENSION,
                JavaFileType.INSTANCE,
                "package " + packageName + "." + classParameters.className().toLowerCase().trim() + ";\n"
                        + classContent.get(Constants.F_CONTENT_KEY)
        );
        currentDirectory.add(javaEntityFile);
    }

    private PsiDirectory getPackageDirectory(ClassParameters classParameters, String packageName, Project project){
        VirtualFile srcFolder = getSrcFolder(project);
        try {
            // Get PsiManager from the project
            PsiManager psiManager = PsiManager.getInstance(project);
            PsiDirectory psiDirectory = psiManager.findDirectory(srcFolder);
            if (psiDirectory != null){
                return getCurrentPsiDirectory(packageName + "." + classParameters.className().toLowerCase().trim()
                        , psiDirectory);
            }
    } catch (Exception e) {
        System.err.println("Failed to create package: " + e.getMessage());
        e.printStackTrace();
    }
        return null;
    }

    private static PsiDirectory getCurrentPsiDirectory(String packageName, PsiDirectory psiDirectory) {
        // Split package name into directories
        String[] packageParts = packageName.split("\\.");
        PsiDirectory currentDirectory = psiDirectory;
        // Loop through and create subdirectories
        for (String part : packageParts) {
            PsiDirectory subdirectory = currentDirectory.findSubdirectory(part);
            if (subdirectory == null) {
                subdirectory = currentDirectory.createSubdirectory(part);
            }
            currentDirectory = subdirectory;
        }
        return currentDirectory;
    }

    private Map<String, String> prepareFileContent(String template, ClassParameters parameters, String suffix){
        Map<String, String> fileContent = new HashMap<>();
        fileContent.put(Constants.F_NAME_KEY, parameters.className() + suffix);

        String fContent = template.replace(Constants.ENTITY_CC_PLACEHOLDER, parameters.className());
        fContent = fContent.replace(Constants.ENTITY_ID_PLACEHOLDER, parameters.idType());
        if(fContent.contains(Constants.ENTITY_LC_PLACEHOLDER))
            fContent = fContent.replace(Constants.ENTITY_LC_PLACEHOLDER, parameters.className().trim().toLowerCase());
        fileContent.put(Constants.F_CONTENT_KEY, fContent);

        return fileContent;
    }
}
