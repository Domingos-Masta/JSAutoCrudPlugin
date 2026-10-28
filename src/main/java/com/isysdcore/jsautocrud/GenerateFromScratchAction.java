package com.isysdcore.jsautocrud;

import com.isysdcore.jsautocrud.util.ECreationMode;

/**
 * Generates entity, repository, service and controller from scratch.
 */
public class GenerateFromScratchAction extends GenerateClassAction {
    public GenerateFromScratchAction() {
        super(ECreationMode.FROM_SCRATCH);
    }
}
