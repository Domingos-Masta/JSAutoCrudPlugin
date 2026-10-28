package com.isysdcore.jsautocrud.model;

import java.util.Map;

/**
 * @author domingos.fernando
 * @created 11/10/2024 - 18:42
 * @project JSAutoCrud
 */
public record EntityClassFilesContent(ClassParameters parameters, Map<String, Map<String, String>> filesToCreate) {
}
