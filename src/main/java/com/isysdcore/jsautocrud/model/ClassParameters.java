package com.isysdcore.jsautocrud.model;

import java.util.List;
import java.util.UUID;

/**
 * Captures every option the user selected for one entity.
 */
public record ClassParameters(
        UUID classId,
        String className,
        String idDataType,
        Boolean createCtrl,
        Boolean createSrvs,
        Boolean generateDto,
        Boolean generateTest,
        EDbType dbType,
        List<ClassAssociation> associations) {

    public ClassParameters withOptions(Boolean createCtrl, Boolean createSrvs, Boolean generateDto, Boolean generateTest) {
        return new ClassParameters(
                classId(), className(), idDataType(),
                createCtrl, createSrvs, generateDto, generateTest,
                dbType(), associations());
    }

    public boolean generateEntity() {
        return dbType() != null;
    }

    public boolean generateRepository() {
        return generateEntity() || Boolean.TRUE.equals(createSrvs) || Boolean.TRUE.equals(createCtrl)
                || Boolean.TRUE.equals(generateDto) || Boolean.TRUE.equals(generateTest);
    }

    public boolean generateService() {
        return Boolean.TRUE.equals(createSrvs) || Boolean.TRUE.equals(createCtrl)
                || Boolean.TRUE.equals(generateDto) || Boolean.TRUE.equals(generateTest);
    }

    public boolean generateController() {
        return Boolean.TRUE.equals(createCtrl) || Boolean.TRUE.equals(generateDto)
                || Boolean.TRUE.equals(generateTest);
    }
}
