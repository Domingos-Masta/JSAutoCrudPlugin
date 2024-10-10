package com.isysdcore.jsautocrud.util;

/**
 * @author domingos.fernando
 * @created 18/09/2024 - 00:01
 * @project SpringAutoCrudClassGenerator
 */
public class Constants {

    public static final String UNIDIRECTIONAL_ONE_TO_ONE = "-> ONE PARENT TO ONE CHILD";
    public static final String UNIDIRECTIONAL_MANY_TO_ONE = "-> MANY PARENT TO ONE CHILD";
    public static final String UNIDIRECTIONAL_ONE_TO_MANY = "-> ONE PARENT TO MANY CHILD";
    public static final String UNIDIRECTIONAL_MANY_TO_MANY = "-> MANY PARENT TO MANY CHILD";
    public static final String BIDIRECTIONAL_ONE_TO_ONE = "<-> ONE PARENT TO ONE CHILD";
    public static final String BIDIRECTIONAL_ONE_TO_MANY = "<-> ONE PARENT TO MANY CHILD";
    public static final String BIDIRECTIONAL_MANY_TO_MANY = "<-> MANY PARENT TO MANY CHILD";
    public static final String PARENT = "PARENT";
    public static final String CHILD = "CHILD";
    public static final String ENTITY_PARENT_ASSOCIATION_PLACEHOLDER = "<EPAP>";
    public static final String ENTITY_CHILD_ASSOCIATION_PLACEHOLDER = "<ECAP>";
    public static final int PARENT_CONTAIN_CHILD = 1;
    public static final int NOT_ASSOCIATION = 0;
    public static final int PARENT_ALREADY_CONTAINED_AS_CHILD = -1;

    public static final int RESUME_TABLE_CONTROLLER_COLUMN = 1;
    public static final int RESUME_TABLE_SERVICE_COLUMN = 2;

    public static final String LIB_GROUP_ID = "io.github.isys-dcore";
    public static final String LIB_ARTIFACT_ID = "generic-auto-crud";
    public static final String F_NAME_KEY = "FNAME";
    public static final String F_CONTENT_KEY = "FCONTENT";
    public static final String ENTITY_CC_PLACEHOLDER = "<ENTITY>";
    public static final String ENTITY_LC_PLACEHOLDER = "<entity>";
    public static final String ENTITY_ID_PLACEHOLDER = "<ID>";
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
            "public class "+ENTITY_CC_PLACEHOLDER+"  extends GenericEntity<"+ENTITY_ID_PLACEHOLDER+"> {\n" +
            "//Just Declare your class attributes Here, as private fields\n" +
            "//The Getter and Setter Methods will be provided by Lombok\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n" +
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
            "public interface "+ENTITY_CC_PLACEHOLDER + REPOSITORY_NAME+" extends GenericRepository<"+ENTITY_CC_PLACEHOLDER+", "+ENTITY_ID_PLACEHOLDER+"> {\n" +
            "//All Methods to manage database already created and implemented\n" +
            "//If you want to modify some behavior, check the methods by control + click on GenericRepository\n" +
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
            "public class "+ENTITY_CC_PLACEHOLDER+ SERVICE_NAME +" extends GenericRestServiceAbstract<"+ENTITY_CC_PLACEHOLDER+", "+ENTITY_CC_PLACEHOLDER+REPOSITORY_NAME+", "+ENTITY_ID_PLACEHOLDER+"> {\n" +
            "//All Methods to iterate with repository are already created and implemented\n" +
            "//If you want to modify some behavior, check the methods by control + click on GenericRestServiceAbstract\n" +
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
            "@RequestMapping(\"/"+ENTITY_LC_PLACEHOLDER+"\")\n" +
            "public class "+ENTITY_CC_PLACEHOLDER+CONTROLLER_NAME +" extends GenericRestControllerAbstract<"+ENTITY_CC_PLACEHOLDER+", "+ENTITY_CC_PLACEHOLDER+SERVICE_NAME+", "+ENTITY_ID_PLACEHOLDER+"> {\n" +
            "    public "+ENTITY_CC_PLACEHOLDER+CONTROLLER_NAME+"("+ENTITY_CC_PLACEHOLDER+SERVICE_NAME+" serviceImpl) {\n" +
            "        super(serviceImpl);\n" +
            "    }\n" +
            "//All Methods to iterate with Service are already created and implemented\n" +
            "//If you want to modify some behavior, check the methods by control + click on GenericRestServiceAbstract\n" +
            "//To see the existent methods and use @Override to Override it and write your own here in this class.\n" +
            "}\n";

    public static final String PARENT_ASSOCIATION_NAME_PLACEHOLDER = "<Parent>";
    public static final String PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER = "<parent>";
    public static final String CHILD_ASSOCIATION_NAME_PLACEHOLDER = "<Child>";
    public static final String CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER = "<child>";

    public static final String TEMPLATE_U_ONE_TO_ONE = "@OneToOne\n" +
            "    @JoinColumn(name = \""+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_id\")\n" +
            "    private "+CHILD_ASSOCIATION_NAME_PLACEHOLDER+" "+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+";\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";

    public static final String TEMPLATE_U_MANY_TO_ONE = " @ManyToOne\n" +
            "    @JoinColumn(name = \""+PARENT_ASSOCIATION_NAME_PLACEHOLDER+"_id\")\n" +
            "    private "+PARENT_ASSOCIATION_NAME_PLACEHOLDER+" "+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+";\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";

    public static final String TEMPLATE_U_ONE_TO_MANY = " @OneToMany\n" +
            "    @JoinColumn(name = \""+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_id\")\n" +
            "    private List<"+CHILD_ASSOCIATION_NAME_PLACEHOLDER+"> "+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"s;\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";

    public static final String TEMPLATE_U_MANY_TO_MANY = "@ManyToMany\n" +
            "    @JoinTable(name = \""+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_"+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"\",\n" +
            "            joinColumns = @JoinColumn(name = \""+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_id\"),\n" +
            "            inverseJoinColumns = @JoinColumn(name = \""+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_id\"))\n" +
            "    private Set<"+CHILD_ASSOCIATION_NAME_PLACEHOLDER+"> "+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"s;\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";

    public static final String TEMPLATE_PARENT_B_ONE_TO_ONE = "@OneToOne(cascade = CascadeType.ALL)\n" +
            "    @JoinColumn(name = \""+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_id\", referencedColumnName = \"id\")\n" +
            "    private "+CHILD_ASSOCIATION_NAME_PLACEHOLDER+" "+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+";\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";
    public static final String TEMPLATE_CHILD_B_ONE_TO_ONE = "@OneToOne(mappedBy = \""+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"\")\n" +
            "    private "+PARENT_ASSOCIATION_NAME_PLACEHOLDER+" "+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+";\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";

    public static final String TEMPLATE_PARENT_B_ONE_TO_MANY = "@OneToMany(mappedBy = \""+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"\")\n" +
            "    private List<"+CHILD_ASSOCIATION_NAME_PLACEHOLDER+"> "+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"s;";
    public static final String TEMPLATE_CHILD_B_ONE_TO_MANY = "@ManyToOne\n" +
            "    @JoinColumn(name = \""+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_id\")\n" +
            "    private "+PARENT_ASSOCIATION_NAME_PLACEHOLDER+" "+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+";\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";

    public static final String TEMPLATE_PARENT_B_MANY_TO_MANY = "@ManyToMany(mappedBy = \""+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"s\")\n" +
            "    private List<"+CHILD_ASSOCIATION_NAME_PLACEHOLDER+"> "+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"s;\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";
    public static final String TEMPLATE_CHILD_B_MANY_TO_MANY = "@ManyToMany\n" +
            "    @JoinTable(name = \""+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_"+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"\",\n" +
            "        joinColumns = @JoinColumn(name = \""+CHILD_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_id\"),\n" +
            "        inverseJoinColumns = @JoinColumn(name = \""+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"_id\"))\n" +
            "    private List<"+PARENT_ASSOCIATION_NAME_PLACEHOLDER+"> "+PARENT_ASSOCIATION_FIELD_NAME_PLACEHOLDER+"s;\n" +
            ENTITY_PARENT_ASSOCIATION_PLACEHOLDER + "\n" +
            ENTITY_CHILD_ASSOCIATION_PLACEHOLDER + "\n";
}
