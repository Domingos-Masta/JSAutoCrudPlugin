package com.isysdcore.jsautocrud.model;

import java.util.UUID;

/**
 * @author domingos.fernando
 * @created 20/09/2024 - 18:09
 * @project JSAutoCrud
 */
public record ClassAssociation(ERelType eRelType, String childRelatedClass, UUID childClassId) {
}
