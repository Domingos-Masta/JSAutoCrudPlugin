package com.isysdcore.jsautocrud.util;

import com.isysdcore.jsautocrud.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author domingos.fernando
 * @created 21/09/2024 - 22:56
 * @project JSAutoCrud
 */
public class Utils {
    public static ListCellRenderer<? super ClassParameters> getCustomRender() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof ClassParameters) {
                    // Here value will be of the Type 'CD'
                    ((JLabel) renderer).setText(((ClassParameters) value).className());
                }
                return renderer;
            }
        };
    }

    public static ListCellRenderer<? super AssociationType> getCustomCbxRender() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof AssociationType) {
                    // Here value will be of the Type 'CD'
                    ((JLabel) renderer).setText(((AssociationType) value).relDescription());
                }
                return renderer;
            }
        };
    }

    public static EAssociationStatus checkDuplicatedAssociations(ClassParameters parentClass, ClassParameters child){
        if(!parentClass.associations().isEmpty()){
            for(ClassAssociation storedAs : parentClass.associations()){
                if (storedAs.childClassId().equals(child.classId())){
                    return EAssociationStatus.PARENT_CONTAIN_CHILD;
                }
            }
        }
        if(!child.associations().isEmpty()){
            for(ClassAssociation storedAs : child.associations()){
                if (storedAs.childClassId().equals(parentClass.classId())){
                    return EAssociationStatus.PARENT_ALREADY_CONTAINED_AS_CHILD;
                }
            }
        }
        return EAssociationStatus.NOT_ASSOCIATION;
    }

    public static boolean removeAssociation(ClassParameters classParameters, String association){
        for(ClassAssociation storedAs : classParameters.associations()){
            if (storedAs.childRelatedClass().equalsIgnoreCase(association)){
                classParameters.associations().remove(storedAs);
                return true;
            }
        }
        return false;
    }

    public static boolean containsSpecialCharacter(String input) {
        // Regular expression to match non-alphanumeric characters
        String regex = "[^a-zA-Z0-9,]";
        // Compile the regular expression
        Pattern pattern = Pattern.compile(regex);
        // Check if the string contains any special character
        return pattern.matcher(input).find();
    }


    public static List<AssociationType> getAssociationType(String parent, String child){
        List<AssociationType> associationTypes = new ArrayList<>();
        Arrays.asList(ERelType.values()).forEach(eRelType -> {
            assert getDescriptions(eRelType) != null;
            String desc = getDescriptions(eRelType).replace(Constants.PARENT, parent);
            desc = desc.replace(Constants.CHILD, child);
            associationTypes.add(new AssociationType(eRelType, desc));
        });
        return associationTypes;
    }

    private static String getDescriptions(ERelType eRelType){
            switch (eRelType){
                case UNIDIRECTIONAL_ONE_TO_ONE -> {return Constants.UNIDIRECTIONAL_ONE_TO_ONE;}
                case UNIDIRECTIONAL_ONE_TO_MANY -> {return Constants.UNIDIRECTIONAL_ONE_TO_MANY;}
                case UNIDIRECTIONAL_MANY_TO_ONE -> {return Constants.UNIDIRECTIONAL_MANY_TO_ONE;}
                case UNIDIRECTIONAL_MANY_TO_MANY -> {return Constants.UNIDIRECTIONAL_MANY_TO_MANY;}
                case BIDIRECTIONAL_ONE_TO_ONE -> {return Constants.BIDIRECTIONAL_ONE_TO_ONE;}
                case BIDIRECTIONAL_ONE_TO_MANY -> {return Constants.BIDIRECTIONAL_ONE_TO_MANY;}
                case BIDIRECTIONAL_MANY_TO_MANY -> {return Constants.BIDIRECTIONAL_MANY_TO_MANY;}
            }
            return null;
    }

    public static String getAssociationType(ERelType eRelType){
        switch (eRelType){
            case UNIDIRECTIONAL_ONE_TO_ONE -> {return "( 1 -> 1 )";}
            case UNIDIRECTIONAL_ONE_TO_MANY -> {return "( 1 -> N )";}
            case UNIDIRECTIONAL_MANY_TO_ONE -> {return "( N -> 1 )";}
            case UNIDIRECTIONAL_MANY_TO_MANY -> {return "( N -> N )";}
            case BIDIRECTIONAL_ONE_TO_ONE -> {return "( 1 <-> 1 )";}
            case BIDIRECTIONAL_ONE_TO_MANY -> {return "( 1 <-> N )";}
            case BIDIRECTIONAL_MANY_TO_MANY -> {return "( N <-> N )";}
        }
        return "";
    }
}
