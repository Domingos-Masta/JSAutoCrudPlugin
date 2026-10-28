package com.isysdcore.jsautocrud;

import com.isysdcore.jsautocrud.util.ECreationMode;

/**
 * Generates layers for an entity that already exists in the project.
 */
public class GenerateFromModelAction extends GenerateClassAction {
    public GenerateFromModelAction() {
        super(ECreationMode.FROM_MODEL);
    }
}
