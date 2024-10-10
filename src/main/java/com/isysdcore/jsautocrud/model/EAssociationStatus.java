package com.isysdcore.jsautocrud.model;

import com.isysdcore.jsautocrud.util.Constants;

/**
 * @author domingos.fernando
 * @created 09/10/2024 - 19:41
 * @project JSAutoCrud
 */
public enum EAssociationStatus {
    PARENT_CONTAIN_CHILD(Constants.PARENT_CONTAIN_CHILD),
    NOT_ASSOCIATION(Constants.NOT_ASSOCIATION),
    PARENT_ALREADY_CONTAINED_AS_CHILD (Constants.PARENT_ALREADY_CONTAINED_AS_CHILD);
    int status;
    EAssociationStatus(int status){
        this.status = status;
    }
}
