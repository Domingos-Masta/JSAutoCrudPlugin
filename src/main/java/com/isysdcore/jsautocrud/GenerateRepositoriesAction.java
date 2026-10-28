package com.isysdcore.jsautocrud;

import com.isysdcore.jsautocrud.util.ECreationMode;

/**
 * Generates only the repository for existing entities.
 */
public class GenerateRepositoriesAction extends GenerateClassAction {
    public GenerateRepositoriesAction() {
        super(ECreationMode.ONLY_REPOSITORIES);
    }
}
