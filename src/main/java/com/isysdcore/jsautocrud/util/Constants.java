package com.isysdcore.jsautocrud.util;

/**
 * Central place for library coordinates, package names and file templates.
 */
public class Constants {

    private Constants() {
    }

    // ---------------------------------------------------------------- library
    public static final String LIB_GROUP_ID = "io.github.isys-dcore";
    public static final String LIB_ARTIFACT_ID = "generic-auto-crud";
    public static final String LIB_MIN_VERSION = "0.7.4";
    public static final String LIB_LATEST_VERSION = "0.7.5";

    // -------------------------------------------------------------- packages
    public static final String PKG_GENERICS = "io.github.isysdcore.genericAutoCrud.generics";
    public static final String PKG_SQL = PKG_GENERICS + ".sql";
    public static final String PKG_SQL_DTO = PKG_SQL + ".dto";
    public static final String PKG_NOSQL = PKG_GENERICS + ".nosql";
    public static final String PKG_NOSQL_DTO = PKG_NOSQL + ".dto";
    public static final String PKG_DTO = PKG_GENERICS + ".dto";
    public static final String PKG_TEST = "io.github.isysdcore.genericAutoCrud.test.generics";

    // -------------------------------------------------------------- classes
    public static final String CLASS_SQL_ENTITY = "GenericSqlEntity";
    public static final String CLASS_SQL_REPOSITORY = "GenericSqlRepository";
    public static final String CLASS_SQL_SERVICE = "GenericSqlRestServiceAbstract";
    public static final String CLASS_SQL_DTO_SERVICE = "GenericSqlRestServiceAbstractDto";
    public static final String CLASS_NOSQL_ENTITY = "GenericNoSqlEntity";
    public static final String CLASS_NOSQL_REPOSITORY = "GenericNoSqlRepository";
    public static final String CLASS_NOSQL_SERVICE = "GenericNoSqlRestServiceAbstract";
    public static final String CLASS_NOSQL_DTO_SERVICE = "GenericNoSqlRestServiceAbstractDto";
    public static final String CLASS_CONTROLLER = "GenericRestControllerAbstract";
    public static final String CLASS_DTO_CONTROLLER = "GenericRestControllerAbstractDto";
    public static final String CLASS_DTO = "GenericDto";
    public static final String CLASS_DTO_MAPPER = "GenericDTOMapper";
    public static final String CLASS_TEST_BASE = "GenericRestIntegrationTestsImpl";

    // -------------------------------------------------------------- misc
    public static final String API_BASE_PATH = "/api";
    public static final String CLASS_EXTENSION = ".java";
    public static final String REPOSITORY_SUFFIX = "Repository";
    public static final String SERVICE_SUFFIX = "ServiceImpl";
    public static final String CONTROLLER_SUFFIX = "RestController";
    public static final String DTO_SUFFIX = "Dto";
    public static final String DTO_MAPPER_SUFFIX = "DtoMapper";
    public static final String DTO_SERVICE_SUFFIX = "ServiceImplDto";
    public static final String DTO_CONTROLLER_SUFFIX = "RestControllerDto";
    public static final String TEST_SUFFIX = "ControllerTest";

    // ---------------------------------------------------------- file keys
    public static final String ENTITY_KEY = "ENTITY_KEY";
    public static final String REPOSITORY_KEY = "REPOSITORY_KEY";
    public static final String SERVICE_KEY = "SERVICE_KEY";
    public static final String CONTROLLER_KEY = "CONTROLLER_KEY";
    public static final String DTO_KEY = "DTO_KEY";
    public static final String DTO_MAPPER_KEY = "DTO_MAPPER_KEY";
    public static final String DTO_SERVICE_KEY = "DTO_SERVICE_KEY";
    public static final String DTO_CONTROLLER_KEY = "DTO_CONTROLLER_KEY";
    public static final String TEST_KEY = "TEST_KEY";

    public static final String F_NAME_KEY = "FNAME";
    public static final String F_CONTENT_KEY = "FCONTENT";

    // ------------------------------------------------------- placeholders
    public static final String ENTITY_CC_PLACEHOLDER = "<ENTITY>";
    public static final String ENTITY_LC_PLACEHOLDER = "<entity>";
    public static final String ENTITY_ID_PLACEHOLDER = "<ID>";
    public static final String ENTITY_ID_IMPORT_PLACEHOLDER = "<ID_IMPORT>";
    public static final String ENTITY_ASSOCIATIONS_PLACEHOLDER = "<ASSOCIATIONS>";

    // -------------------------------------------------- association labels
    public static final String UNIDIRECTIONAL_ONE_TO_ONE = "-> ONE PARENT TO ONE CHILD";
    public static final String UNIDIRECTIONAL_MANY_TO_ONE = "-> MANY PARENT TO ONE CHILD";
    public static final String UNIDIRECTIONAL_ONE_TO_MANY = "-> ONE PARENT TO MANY CHILD";
    public static final String UNIDIRECTIONAL_MANY_TO_MANY = "-> MANY PARENT TO MANY CHILD";
    public static final String BIDIRECTIONAL_ONE_TO_ONE = "<-> ONE PARENT TO ONE CHILD";
    public static final String BIDIRECTIONAL_ONE_TO_MANY = "<-> ONE PARENT TO MANY CHILD";
    public static final String BIDIRECTIONAL_MANY_TO_MANY = "<-> MANY PARENT TO MANY CHILD";
    public static final String PARENT = "PARENT";
    public static final String CHILD = "CHILD";
    public static final int PARENT_CONTAIN_CHILD = 1;
    public static final int NOT_ASSOCIATION = 0;
    public static final int PARENT_ALREADY_CONTAINED_AS_CHILD = -1;

    public static final int RESUME_TABLE_CONTROLLER_COLUMN = 1;
    public static final int RESUME_TABLE_SERVICE_COLUMN = 2;

    public static final String CLASS_HEADER = "/*\n"
            + " * Generated by the JSAutoCrud IntelliJ plugin.\n"
            + " * Powered by generic-auto-crud (" + LIB_GROUP_ID + ":" + LIB_ARTIFACT_ID + ").\n"
            + " * Review and extend the generated classes to fit your domain.\n"
            + " */\n\n";

    // ----------------------------------------------------------- templates
    public static final String ENTITY_SQL_CLASS_CONTENT =
            "import " + PKG_SQL + "." + CLASS_SQL_ENTITY + ";\n"
            + "import jakarta.persistence.*;\n"
            + "import lombok.*;\n"
            + "import java.util.List;\n"
            + "import java.util.Set;\n"
            + ENTITY_ID_IMPORT_PLACEHOLDER
            + "\n"
            + "@Entity\n"
            + "@Table\n"
            + "@Data\n"
            + "@EqualsAndHashCode(callSuper = true)\n"
            + "@NoArgsConstructor\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + " extends " + CLASS_SQL_ENTITY + "<" + ENTITY_ID_PLACEHOLDER + "> {\n"
            + "\n"
            + "    // Declare your entity attributes here as private fields.\n"
            + "    // Getters, setters and the ID / audit fields are provided by Lombok and the base class.\n"
            + ENTITY_ASSOCIATIONS_PLACEHOLDER
            + "}\n";

    public static final String ENTITY_MONGO_CLASS_CONTENT =
            "import " + PKG_NOSQL + "." + CLASS_NOSQL_ENTITY + ";\n"
            + "import lombok.*;\n"
            + "import org.springframework.data.mongodb.core.mapping.Document;\n"
            + "\n"
            + "@Document\n"
            + "@Data\n"
            + "@EqualsAndHashCode(callSuper = true)\n"
            + "@NoArgsConstructor\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + " extends " + CLASS_NOSQL_ENTITY + " {\n"
            + "\n"
            + "    // Declare your document attributes here as private fields.\n"
            + "    // Getters, setters and the ID / audit fields are provided by Lombok and the base class.\n"
            + ENTITY_ASSOCIATIONS_PLACEHOLDER
            + "}\n";

    public static final String REPOSITORY_SQL_CLASS_CONTENT =
            "import " + PKG_SQL + "." + CLASS_SQL_REPOSITORY + ";\n"
            + "import org.springframework.stereotype.Repository;\n"
            + ENTITY_ID_IMPORT_PLACEHOLDER
            + "\n"
            + "@Repository\n"
            + "public interface " + ENTITY_CC_PLACEHOLDER + REPOSITORY_SUFFIX
            + " extends " + CLASS_SQL_REPOSITORY + "<" + ENTITY_CC_PLACEHOLDER + ", " + ENTITY_ID_PLACEHOLDER + "> {\n"
            + "}\n";

    public static final String REPOSITORY_MONGO_CLASS_CONTENT =
            "import " + PKG_NOSQL + "." + CLASS_NOSQL_REPOSITORY + ";\n"
            + "import org.springframework.stereotype.Repository;\n"
            + "\n"
            + "@Repository\n"
            + "public interface " + ENTITY_CC_PLACEHOLDER + REPOSITORY_SUFFIX
            + " extends " + CLASS_NOSQL_REPOSITORY + "<" + ENTITY_CC_PLACEHOLDER + "> {\n"
            + "}\n";

    public static final String SERVICE_SQL_CLASS_CONTENT =
            "import " + PKG_SQL + "." + CLASS_SQL_SERVICE + ";\n"
            + "import org.springframework.stereotype.Service;\n"
            + ENTITY_ID_IMPORT_PLACEHOLDER
            + "\n"
            + "@Service\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + SERVICE_SUFFIX
            + " extends " + CLASS_SQL_SERVICE + "<" + ENTITY_CC_PLACEHOLDER + ", "
            + ENTITY_CC_PLACEHOLDER + REPOSITORY_SUFFIX + ", " + ENTITY_ID_PLACEHOLDER + "> {\n"
            + "}\n";

    public static final String SERVICE_MONGO_CLASS_CONTENT =
            "import " + PKG_NOSQL + "." + CLASS_NOSQL_SERVICE + ";\n"
            + "import org.springframework.stereotype.Service;\n"
            + "\n"
            + "@Service\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + SERVICE_SUFFIX
            + " extends " + CLASS_NOSQL_SERVICE + "<" + ENTITY_CC_PLACEHOLDER + ", "
            + ENTITY_CC_PLACEHOLDER + REPOSITORY_SUFFIX + "> {\n"
            + "\n"
            + "    public " + ENTITY_CC_PLACEHOLDER + SERVICE_SUFFIX + "() {\n"
            + "        super(" + ENTITY_CC_PLACEHOLDER + ".class);\n"
            + "    }\n"
            + "}\n";

    public static final String CONTROLLER_CLASS_CONTENT =
            "import " + PKG_GENERICS + "." + CLASS_CONTROLLER + ";\n"
            + "import org.springframework.web.bind.annotation.RequestMapping;\n"
            + "import org.springframework.web.bind.annotation.RestController;\n"
            + ENTITY_ID_IMPORT_PLACEHOLDER
            + "\n"
            + "@RestController\n"
            + "@RequestMapping(\"" + API_BASE_PATH + "/" + ENTITY_LC_PLACEHOLDER + "\")\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + CONTROLLER_SUFFIX
            + " extends " + CLASS_CONTROLLER + "<" + ENTITY_CC_PLACEHOLDER + ", "
            + ENTITY_CC_PLACEHOLDER + SERVICE_SUFFIX + ", " + ENTITY_ID_PLACEHOLDER + "> {\n"
            + "\n"
            + "    public " + ENTITY_CC_PLACEHOLDER + CONTROLLER_SUFFIX + "("
            + ENTITY_CC_PLACEHOLDER + SERVICE_SUFFIX + " serviceImpl) {\n"
            + "        super(serviceImpl);\n"
            + "    }\n"
            + "}\n";

    public static final String DTO_CLASS_CONTENT =
            "import " + PKG_DTO + "." + CLASS_DTO + ";\n"
            + "import lombok.Builder;\n"
            + "import lombok.Data;\n"
            + ENTITY_ID_IMPORT_PLACEHOLDER
            + "\n"
            + "@Data\n"
            + "@Builder\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + " implements " + CLASS_DTO + "<" + ENTITY_ID_PLACEHOLDER + "> {\n"
            + "\n"
            + "    private " + ENTITY_ID_PLACEHOLDER + " id;\n"
            + "\n"
            + "    // Add DTO fields mirroring your entity attributes.\n"
            + "}\n";

    public static final String DTO_MAPPER_CLASS_CONTENT =
            "import " + PKG_DTO + "." + CLASS_DTO_MAPPER + ";\n"
            + "import org.springframework.stereotype.Component;\n"
            + "import java.util.List;\n"
            + "import java.util.stream.Collectors;\n"
            + "\n"
            + "@Component\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + DTO_MAPPER_SUFFIX
            + " implements " + CLASS_DTO_MAPPER + "<" + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + ", " + ENTITY_CC_PLACEHOLDER + "> {\n"
            + "\n"
            + "    @Override\n"
            + "    public " + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + " toDto(" + ENTITY_CC_PLACEHOLDER + " entity) {\n"
            + "        return " + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + ".builder()\n"
            + "                .id(entity.getId())\n"
            + "                .build();\n"
            + "    }\n"
            + "\n"
            + "    @Override\n"
            + "    public " + ENTITY_CC_PLACEHOLDER + " toEntity(" + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + " dto) {\n"
            + "        " + ENTITY_CC_PLACEHOLDER + " entity = new " + ENTITY_CC_PLACEHOLDER + "();\n"
            + "        entity.setId(dto.getId());\n"
            + "        return entity;\n"
            + "    }\n"
            + "\n"
            + "    @Override\n"
            + "    public List<" + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + "> toDtoList(List<" + ENTITY_CC_PLACEHOLDER + "> entities) {\n"
            + "        return entities.stream().map(this::toDto).collect(Collectors.toList());\n"
            + "    }\n"
            + "\n"
            + "    @Override\n"
            + "    public List<" + ENTITY_CC_PLACEHOLDER + "> toEntityList(List<" + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + "> dtos) {\n"
            + "        return dtos.stream().map(this::toEntity).collect(Collectors.toList());\n"
            + "    }\n"
            + "}\n";

    public static final String DTO_SERVICE_SQL_CLASS_CONTENT =
            "import " + PKG_SQL_DTO + "." + CLASS_SQL_DTO_SERVICE + ";\n"
            + "import org.springframework.stereotype.Service;\n"
            + ENTITY_ID_IMPORT_PLACEHOLDER
            + "\n"
            + "@Service\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + DTO_SERVICE_SUFFIX
            + " extends " + CLASS_SQL_DTO_SERVICE + "<" + ENTITY_CC_PLACEHOLDER + ", "
            + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + ", " + ENTITY_CC_PLACEHOLDER + REPOSITORY_SUFFIX + ", "
            + ENTITY_CC_PLACEHOLDER + DTO_MAPPER_SUFFIX + ", " + ENTITY_ID_PLACEHOLDER + "> {\n"
            + "\n"
            + "    public " + ENTITY_CC_PLACEHOLDER + DTO_SERVICE_SUFFIX + "("
            + ENTITY_CC_PLACEHOLDER + DTO_MAPPER_SUFFIX + " mapper) {\n"
            + "        super(mapper);\n"
            + "    }\n"
            + "}\n";

    public static final String DTO_SERVICE_MONGO_CLASS_CONTENT =
            "import " + PKG_NOSQL_DTO + "." + CLASS_NOSQL_DTO_SERVICE + ";\n"
            + "import org.springframework.stereotype.Service;\n"
            + "\n"
            + "@Service\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + DTO_SERVICE_SUFFIX
            + " extends " + CLASS_NOSQL_DTO_SERVICE + "<" + ENTITY_CC_PLACEHOLDER + ", "
            + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + ", " + ENTITY_CC_PLACEHOLDER + REPOSITORY_SUFFIX + ", "
            + ENTITY_CC_PLACEHOLDER + DTO_MAPPER_SUFFIX + "> {\n"
            + "\n"
            + "    public " + ENTITY_CC_PLACEHOLDER + DTO_SERVICE_SUFFIX + "("
            + ENTITY_CC_PLACEHOLDER + DTO_MAPPER_SUFFIX + " mapper) {\n"
            + "        super(mapper, " + ENTITY_CC_PLACEHOLDER + ".class);\n"
            + "    }\n"
            + "}\n";

    public static final String DTO_CONTROLLER_CLASS_CONTENT =
            "import " + PKG_GENERICS + "." + CLASS_DTO_CONTROLLER + ";\n"
            + "import org.springframework.web.bind.annotation.RequestMapping;\n"
            + "import org.springframework.web.bind.annotation.RestController;\n"
            + ENTITY_ID_IMPORT_PLACEHOLDER
            + "\n"
            + "@RestController\n"
            + "@RequestMapping(\"" + API_BASE_PATH + "/" + ENTITY_LC_PLACEHOLDER + "\")\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + DTO_CONTROLLER_SUFFIX
            + " extends " + CLASS_DTO_CONTROLLER + "<" + ENTITY_CC_PLACEHOLDER + ", "
            + ENTITY_CC_PLACEHOLDER + DTO_SUFFIX + ", " + ENTITY_CC_PLACEHOLDER + DTO_SERVICE_SUFFIX + ", "
            + ENTITY_ID_PLACEHOLDER + "> {\n"
            + "\n"
            + "    public " + ENTITY_CC_PLACEHOLDER + DTO_CONTROLLER_SUFFIX + "("
            + ENTITY_CC_PLACEHOLDER + DTO_SERVICE_SUFFIX + " serviceImpl) {\n"
            + "        super(serviceImpl);\n"
            + "    }\n"
            + "}\n";

    public static final String TEST_CLASS_CONTENT =
            "import " + PKG_TEST + "." + CLASS_TEST_BASE + ";\n"
            + "import org.springframework.boot.test.context.SpringBootTest;\n"
            + ENTITY_ID_IMPORT_PLACEHOLDER
            + "\n"
            + "@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)\n"
            + "public class " + ENTITY_CC_PLACEHOLDER + TEST_SUFFIX
            + " extends " + CLASS_TEST_BASE + "<" + ENTITY_CC_PLACEHOLDER + ", " + ENTITY_ID_PLACEHOLDER + "> {\n"
            + "\n"
            + "    public " + ENTITY_CC_PLACEHOLDER + TEST_SUFFIX + "() {\n"
            + "        super(\"" + API_BASE_PATH + "/" + ENTITY_LC_PLACEHOLDER + "\", new " + ENTITY_CC_PLACEHOLDER + "());\n"
            + "    }\n"
            + "\n"
            + "    @Override\n"
            + "    public void authenticationManagement() throws Exception {\n"
            + "        // Configure authentication here if your API requires it.\n"
            + "    }\n"
            + "}\n";
}
