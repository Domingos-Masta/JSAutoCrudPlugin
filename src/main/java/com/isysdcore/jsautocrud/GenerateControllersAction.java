package com.isysdcore.jsautocrud;

import com.isysdcore.jsautocrud.util.ECreationMode;

/**
 * Generates only the REST controller for existing entities.
 */
public class GenerateControllersAction extends GenerateClassAction {
    public GenerateControllersAction() {
        super(ECreationMode.ONLY_CONTROLLERS);
    }
}
