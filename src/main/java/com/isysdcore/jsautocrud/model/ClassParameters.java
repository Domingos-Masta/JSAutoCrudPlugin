package com.isysdcore.jsautocrud.model;

import java.util.List;
import java.util.UUID;

/**
 * @author domingos.fernando
 * @created 18/09/2024 - 13:03
 * @project SpringAutoCrudClassGenerator
 */
public record ClassParameters(UUID classId, String className, String idDataType, Boolean createCtrl, Boolean createSrvs, List<ClassAssociation> associations){
    public ClassParameters withControllerAndService(Boolean createCtrl, Boolean createSrvs) {
        return new ClassParameters(classId(), className(), idDataType(),createCtrl,createSrvs, associations());
    }
}
