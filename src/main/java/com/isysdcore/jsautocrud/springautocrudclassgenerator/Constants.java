package com.isysdcore.jsautocrud.springautocrudclassgenerator;

/**
 * @author domingos.fernando
 * @created 18/09/2024 - 00:01
 * @project SpringAutoCrudClassGenerator
 */
public class Constants {
    public static final String ENTITY_CC_PLACEHOLDER = "<Entity>";
    public static final String ENTITY_LC_PLACEHOLDER = "<entity>";
    public static final String CLASS_EXTENSION = ".java";
    public static final String REPOSITORY_NAME = "Repository";
    public static final String SERVICE_NAME = "ServiceImpl";
    public static final String CONTROLLER_NAME = "RestController";
    public static final String ENTITY_CLASS_CONTENT = "/**\n" +
            "* This project is part of author personal assets and tools to help with productivity\n" +
            "* any part of this projects belong to the author, use as you own risk.\n" +
            "* You are free to use in your project without change anything. (;-)\n"+
            "* @author domingos.fernando\n" +
            "* @created 16/09/2024 - 23:19\n" +
            "* @project SpringAutoCrud\n" +
            "*/\n" +
            "import io.github.isysdcore.genericAutoCrud.generics.GenericEntity;\n" +
            "import jakarta.persistence.*;\n" +
            "import java.util.UUID;\n" +
            "import lombok.*;\n" +
            "@Entity\n" +
            "@Table\n" +
            "@Data\n" +
            "@EqualsAndHashCode(callSuper = true)\n" +
            "@NoArgsConstructor\n" +
            "public class  <Entity>  extends GenericEntity<UUID> {\n" +
            "//Just Declare your class attributes Here, as private fields\n" +
            "//The Getter and Setter Methods will be provided by Lombok\n" +
            "\n}";
    public static final String REPOSITORY_CLASS_CONTENT = "/**\n" +
            "* This project is part of author personal assets and tools to help with productivity\n" +
            "* any part of this projects belong to the author, use as you own risk.\n" +
            "* You are free to use in your project without change anything. (;-)\n"+
            "* @author domingos.fernando\n" +
            "* @created 16/09/2024 - 23:19\n" +
            "* @project SpringAutoCrud\n" +
            "*/\n" +
            "import io.github.isysdcore.genericAutoCrud.generics.GenericRepository;\n" +
            "import org.springframework.stereotype.Repository;\n" +
            "\n" +
            "import java.util.UUID;\n" +
            "@Repository\n" +
            "public interface <Entity>"+ REPOSITORY_NAME +" extends GenericRepository<<Entity>, UUID> {\n" +
            "//All Methods to manage database already created and implemented\n" +
            "//If you want to modify some behavior, check the methods by control + click on GenericRepository" +
            "//To see the existent method and use @Override to Override it and write your own here in this class.\n" +
            "}\n";
    public static final String SERVICE_CLASS_CONTENT = "/**\n" +
            "* This project is part of author personal assets and tools to help with productivity\n" +
            "* any part of this projects belong to the author, use as you own risk.\n" +
            "* You are free to use in your project without change anything. (;-)\n"+
            "* @author domingos.fernando\n" +
            "* @created 16/09/2024 - 23:19\n" +
            "* @project SpringAutoCrud\n" +
            "*/\n" +
            "import io.github.isysdcore.genericAutoCrud.generics.GenericRestServiceAbstract;\n" +
            "import org.springframework.stereotype.Service;\n" +
            "\n" +
            "import java.util.UUID;\n" +
            "@Service\n" +
            "public class <Entity>"+ SERVICE_NAME +" extends GenericRestServiceAbstract<<Entity>, <Entity>Repository, UUID> {\n" +
            "//All Methods to iterate with repository are already created and implemented\n" +
            "//If you want to modify some behavior, check the methods by control + click on GenericRestServiceAbstract" +
            "//To see the existent methods and use @Override to Override it and write your own here in this class.\n" +
            "}\n";
    public static final String CONTROLLER_CLASS_CONTENT = "/**\n" +
            "* This project is part of author personal assets and tools to help with productivity\n" +
            "* any part of this projects belong to the author, use as you own risk.\n" +
            "* You are free to use in your project without change anything. (;-)\n"+
            "* @author domingos.fernando\n" +
            "* @created 16/09/2024 - 23:19\n" +
            "* @project SpringAutoCrud\n" +
            "*/\n" +
            "import io.github.isysdcore.genericAutoCrud.generics.GenericRestControllerAbstract;\n" +
            "import org.springframework.web.bind.annotation.RequestMapping;\n" +
            "import org.springframework.web.bind.annotation.RestController;\n" +
            "\n" +
            "import java.util.UUID;\n" +
            "@RestController\n" +
            "@RequestMapping(\"/<entity>\")\n" +
            "public class <Entity>"+ CONTROLLER_NAME +" extends GenericRestControllerAbstract<<Entity>, <Entity>ServiceImpl, UUID> {\n" +
            "    public <Entity>RestController(<Entity>ServiceImpl serviceImpl) {\n" +
            "        super(serviceImpl);\n" +
            "    }\n" +
            "//All Methods to iterate with Service are already created and implemented\n" +
            "//If you want to modify some behavior, check the methods by control + click on GenericRestServiceAbstract" +
            "//To see the existent methods and use @Override to Override it and write your own here in this class.\n" +
            "}\n";
}
