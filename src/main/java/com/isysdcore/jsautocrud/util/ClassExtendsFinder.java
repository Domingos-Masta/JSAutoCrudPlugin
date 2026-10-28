package com.isysdcore.jsautocrud.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Locates classes that extend the generic-auto-crud base classes.
 */
public final class ClassExtendsFinder {

    private ClassExtendsFinder() {
    }

    public static List<PsiClass> findClassesExtending(Project project, String... superClassFqns) {
        List<PsiClass> extendingClasses = new ArrayList<>();

        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        List<PsiClass> superClasses = new ArrayList<>();
        for (String fqn : superClassFqns) {
            PsiClass superClass = psiFacade.findClass(fqn, GlobalSearchScope.allScope(project));
            if (superClass != null) {
                superClasses.add(superClass);
            }
        }

        if (superClasses.isEmpty()) {
            return extendingClasses;
        }

        PsiManager psiManager = PsiManager.getInstance(project);
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(fileOrDir -> {
            if (fileOrDir.isDirectory()) {
                return true;
            }
            PsiFile psiFile = psiManager.findFile(fileOrDir);
            if (psiFile instanceof PsiJavaFile javaFile) {
                for (PsiClass psiClass : javaFile.getClasses()) {
                    for (PsiClass superClass : superClasses) {
                        if (extendsClass(psiClass, superClass)) {
                            extendingClasses.add(psiClass);
                            break;
                        }
                    }
                }
            }
            return true;
        });
        return extendingClasses;
    }

    /**
     * Resolves the ID type argument declared on the extended generic entity
     * (for example {@code class Person extends GenericSqlEntity<UUID>} returns {@code java.util.UUID}).
     */
    public static PsiClass resolveIdClass(PsiClass psiClass) {
        if (psiClass.getExtendsList() == null) {
            return null;
        }
        PsiClassType[] extendsList = psiClass.getExtendsList().getReferencedTypes();
        if (extendsList.length > 0) {
            PsiClass resolved = extendsList[0].resolve();
            PsiType[] typeArguments = extendsList[0].getParameters();
            if (resolved != null && typeArguments.length > 0 && typeArguments[0] instanceof PsiClassType classType) {
                return classType.resolve();
            }
        }
        return null;
    }

    public static String resolveIdTypeName(PsiClass psiClass) {
        PsiClass idClass = resolveIdClass(psiClass);
        if (idClass == null) {
            return "String";
        }
        String qualifiedName = idClass.getQualifiedName();
        if (qualifiedName == null) {
            return idClass.getName() == null ? "String" : idClass.getName();
        }
        return switch (qualifiedName) {
            case "java.util.UUID" -> "UUID";
            case "java.lang.Long" -> "Long";
            case "java.lang.Integer" -> "Integer";
            case "java.lang.String" -> "String";
            default -> idClass.getName();
        };
    }

    private static boolean extendsClass(PsiClass psiClass, PsiClass superClass) {
        PsiClass superClassToCheck = psiClass;
        while (superClassToCheck != null) {
            if (superClassToCheck.equals(superClass)) {
                return true;
            }
            superClassToCheck = superClassToCheck.getSuperClass();
        }

        if (superClass.isInterface()) {
            for (PsiClassType interfaceType : psiClass.getImplementsListTypes()) {
                PsiClass interfaceClass = interfaceType.resolve();
                if (interfaceClass != null && (interfaceClass.equals(superClass)
                        || extendsClass(interfaceClass, superClass))) {
                    return true;
                }
            }
        }
        return false;
    }
}
