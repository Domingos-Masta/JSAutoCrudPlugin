package com.isysdcore.jsautocrud;

import com.isysdcore.jsautocrud.util.ECreationMode;

/**
 * Generates only the service for existing entities.
 */
public class GenerateServicesAction extends GenerateClassAction {
    public GenerateServicesAction() {
        super(ECreationMode.ONLY_SERVICES);
    }
}
