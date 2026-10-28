package com.isysdcore.jsautocrud;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.isysdcore.jsautocrud.ui.ClassDefinitionsDialog;
import com.isysdcore.jsautocrud.util.Constants;
import com.isysdcore.jsautocrud.util.ECreationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entry point action that validates the project and opens the generation wizard.
 */
public class GenerateClassAction extends AnAction {

    private static final Logger log = LoggerFactory.getLogger(GenerateClassAction.class);

    private final ECreationMode creationMode;

    protected GenerateClassAction(ECreationMode creationMode) {
        this.creationMode = creationMode;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        String dependencyVersion = findDependencyVersion(project);
        if (dependencyVersion == null) {
            Messages.showErrorDialog(project,
                    "The dependency '" + Constants.LIB_GROUP_ID + ":" + Constants.LIB_ARTIFACT_ID + "' was not found in pom.xml.\n\n"
                            + "Add the generic-auto-crud library to your project before using this plugin:\n"
                            + "<dependency>\n"
                            + "    <groupId>" + Constants.LIB_GROUP_ID + "</groupId>\n"
                            + "    <artifactId>" + Constants.LIB_ARTIFACT_ID + "</artifactId>\n"
                            + "    <version>" + Constants.LIB_LATEST_VERSION + "</version>\n"
                            + "</dependency>",
                    "Dependency Missing");
            return;
        }

        if (isResolvableVersion(dependencyVersion) && !isVersionAtLeast(dependencyVersion, Constants.LIB_MIN_VERSION)) {
            Messages.showWarningDialog(project,
                    "Your project uses generic-auto-crud " + dependencyVersion + ", "
                            + "but this plugin is aligned with version " + Constants.LIB_MIN_VERSION + " or newer.\n\n"
                            + "Please update the dependency to benefit from the latest generated class names and features.",
                    "Outdated Dependency");
        } else if (!isResolvableVersion(dependencyVersion)) {
            log.warn("Unable to resolve generic-auto-crud version '{}' from pom.xml properties; skipping version check.",
                    dependencyVersion);
        }

        ClassDefinitionsDialog dialog = new ClassDefinitionsDialog(project, creationMode);
        dialog.show();
        if (dialog.isOK() && dialog.getEntitiesToCreateList() != null) {
            new CreateAndManageFiles().runLoadingTask(project, creationMode, dialog.getEntitiesToCreateList());
        }
    }

    private String findDependencyVersion(Project project) {
        try {
            File pomFile = new File(project.getBasePath(), "pom.xml");
            if (!pomFile.exists()) {
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(pomFile);
            document.getDocumentElement().normalize();

            Map<String, String> properties = readMavenProperties(document);
            Element projectElement = document.getDocumentElement();
            String projectVersion = directChildText(projectElement, "version");
            if (projectVersion != null) {
                properties.putIfAbsent("project.version", resolvePlaceholders(projectVersion, properties, 0));
            }

            Element dependenciesElement = directChildElement(projectElement, "dependencies");
            if (dependenciesElement == null) {
                return null;
            }
            NodeList dependencies = dependenciesElement.getChildNodes();
            for (int i = 0; i < dependencies.getLength(); i++) {
                Node node = dependencies.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE || !"dependency".equals(node.getNodeName())) {
                    continue;
                }
                Element dependency = (Element) node;
                String groupId = resolvePlaceholders(directChildText(dependency, "groupId"), properties, 0);
                String artifactId = resolvePlaceholders(directChildText(dependency, "artifactId"), properties, 0);
                if (Constants.LIB_GROUP_ID.equals(groupId) && Constants.LIB_ARTIFACT_ID.equals(artifactId)) {
                    String version = directChildText(dependency, "version");
                    if (version != null) {
                        return resolvePlaceholders(version, properties, 0);
                    }
                    return findManagedDependencyVersion(document, properties, groupId, artifactId);
                }
            }
        } catch (Exception ex) {
            log.error("Error validating dependencies", ex);
        }
        return null;
    }

    private String findManagedDependencyVersion(Document document, Map<String, String> properties,
                                                String groupId, String artifactId) {
        Element management = directChildElement(document.getDocumentElement(), "dependencyManagement");
        if (management == null) {
            return null;
        }
        Element dependenciesElement = directChildElement(management, "dependencies");
        if (dependenciesElement == null) {
            return null;
        }
        NodeList dependencies = dependenciesElement.getChildNodes();
        for (int i = 0; i < dependencies.getLength(); i++) {
            Node node = dependencies.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE || !"dependency".equals(node.getNodeName())) {
                continue;
            }
            Element dependency = (Element) node;
            String managedGroupId = resolvePlaceholders(directChildText(dependency, "groupId"), properties, 0);
            String managedArtifactId = resolvePlaceholders(directChildText(dependency, "artifactId"), properties, 0);
            if (groupId.equals(managedGroupId) && artifactId.equals(managedArtifactId)) {
                String version = directChildText(dependency, "version");
                return version == null ? null : resolvePlaceholders(version, properties, 0);
            }
        }
        return null;
    }

    private Map<String, String> readMavenProperties(Document document) {
        Map<String, String> properties = new LinkedHashMap<>();
        Element propertiesElement = directChildElement(document.getDocumentElement(), "properties");
        if (propertiesElement == null) {
            return properties;
        }
        NodeList children = propertiesElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                properties.put(node.getNodeName(), node.getTextContent().trim());
            }
        }
        return properties;
    }

    private String resolvePlaceholders(String value, Map<String, String> properties, int depth) {
        if (value == null || !value.contains("${") || depth > 5) {
            return value;
        }
        String resolved = value;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String propertyValue = entry.getValue();
            if (propertyValue == null) {
                continue;
            }
            resolved = resolved.replace("${" + entry.getKey() + "}", propertyValue);
        }
        if (resolved.equals(value)) {
            return resolved;
        }
        return resolvePlaceholders(resolved, properties, depth + 1);
    }

    private Element directChildElement(Element parent, String tagName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && tagName.equals(node.getNodeName())) {
                return (Element) node;
            }
        }
        return null;
    }

    private String directChildText(Element parent, String tagName) {
        Element element = directChildElement(parent, tagName);
        return element == null ? null : element.getTextContent().trim();
    }

    private boolean isResolvableVersion(String version) {
        return version != null && !version.contains("${");
    }

    private boolean isVersionAtLeast(String current, String required) {
        int[] currentParts = parseVersion(current);
        int[] requiredParts = parseVersion(required);
        int length = Math.max(currentParts.length, requiredParts.length);
        for (int i = 0; i < length; i++) {
            int a = i < currentParts.length ? currentParts[i] : 0;
            int b = i < requiredParts.length ? requiredParts[i] : 0;
            if (a != b) {
                return a > b;
            }
        }
        return true;
    }

    private int[] parseVersion(String version) {
        String[] raw = version.replaceAll("[^0-9.]", "").split("\\.");
        int[] parts = new int[raw.length];
        for (int i = 0; i < raw.length; i++) {
            try {
                parts[i] = Integer.parseInt(raw[i]);
            } catch (NumberFormatException ignored) {
                parts[i] = 0;
            }
        }
        return parts;
    }
}
