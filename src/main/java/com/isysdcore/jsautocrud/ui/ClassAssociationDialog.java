package com.isysdcore.jsautocrud.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.isysdcore.jsautocrud.model.*;
import com.isysdcore.jsautocrud.util.Utils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;


/**
 * @author domingos.fernando
 * @created 18/09/2024 - 11:34
 * @project SpringAutoCrudClassGenerator
 */
public class ClassAssociationDialog extends DialogWrapper {
    //Presentation painel
    private JLabel classToAssociateJlb;
    private JComboBox<ClassParameters> classParametersJCbx;
    private JLabel associationTypeJlb;
    private JComboBox<AssociationType> associationTypeJCbx;
    private JButton createAssociationJbt;
    private JButton removeAssociationJbt;
    private JLabel associationResumeJlb;
    private JTable associationsJtbl;
    private JScrollPane associationJsp;

    private List<ClassParameters> classParametersList;
    private ClassParameters mainEntity;
    private JPanel dialogPanel;
    private JPanel selectionPanel;
    private JPanel resumePanel;

    // Column Names
    String[] assocTHeaders = { "Assoc. Owner", "Association", "Associated" };

    public ClassAssociationDialog(@Nullable Project project, ClassParameters selectedClass, List<ClassParameters> classParameter) {
        super(project);
        this.mainEntity = selectedClass;
        setTitle("Create "+ mainEntity.className() +" Association with : ");
        this.classParametersList = classParameter;
//        setResizable(false);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        initializeComponents();
        addComponentsToPanel();
        addEvents();
        updateAssociations();
        return dialogPanel;
    }

    private void initializeComponents(){
        dialogPanel = new JPanel(new GridLayout(2,1));
        selectionPanel = new JPanel(new GridLayout(3,2, 1, 2));
//        selectionPanel = new JPanel(new GridLayout());
        resumePanel = new JPanel(new GridLayout(3,1,1, 2));

        classToAssociateJlb = new JLabel("Entity to Associate: ");
        DefaultComboBoxModel<ClassParameters> model = new DefaultComboBoxModel<>();
        model.addAll(classParametersList);
        classParametersJCbx = new ComboBox<>(model);
        classParametersJCbx.setRenderer(Utils.getCustomRender());

        associationTypeJlb = new JLabel("Association Type: ");
        DefaultComboBoxModel<AssociationType> eRelTypeModel = new DefaultComboBoxModel<>();
        eRelTypeModel.addAll(Utils.getAssociationType(mainEntity.className(), "Child"));
        associationTypeJCbx = new ComboBox<>(eRelTypeModel);
        associationTypeJCbx.setRenderer(Utils.getCustomCbxRender());

        removeAssociationJbt = new JButton("Remove");
        createAssociationJbt = new JButton("Associate");
        associationResumeJlb = new JLabel("Entity Associations: ");
        associationsJtbl = new JBTable();

    }

    private void addComponentsToPanel(){

        selectionPanel.add(classToAssociateJlb);
        selectionPanel.add(classParametersJCbx);

        selectionPanel.add(associationTypeJlb);
        selectionPanel.add(associationTypeJCbx);

        selectionPanel.add(new JLabel());
        selectionPanel.add(createAssociationJbt);

        resumePanel.add(associationResumeJlb);
        DefaultTableModel tableModel = new DefaultTableModel(assocTHeaders,0);
        associationsJtbl.setModel(tableModel);
        associationJsp = new JBScrollPane(associationsJtbl);
        associationJsp.setSize(50, 100);
        resumePanel.add(associationJsp);
        resumePanel.add(removeAssociationJbt);

        dialogPanel.add(selectionPanel);
        dialogPanel.add(resumePanel);
    }

    private void addEvents(){
        createAssociationJbt.addActionListener(e -> {
            ClassParameters parameters = (ClassParameters) classParametersJCbx.getSelectedItem();
            assert parameters != null;
            addEntityAssociation(parameters);
        });
        removeAssociationJbt.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) associationsJtbl.getModel();
            Object toRemove = model.getValueAt(associationsJtbl.getSelectedRow(),2);
            if(Utils.removeAssociation(mainEntity, toRemove.toString())){
                model.removeRow(associationsJtbl.getSelectedRow());
            }else {
                Messages.showErrorDialog("Error, Association between '"
                                +mainEntity.className()+"' and '"
                                +toRemove+"' do not exist or already removed.",
                        "Error, Not Detected Association");
                model.removeRow(associationsJtbl.getSelectedRow());
            }

        });

    }

    public void addEntityAssociation(ClassParameters child){
        if(Utils.checkDuplicatedAssociations(mainEntity, child) != EAssociationStatus.NOT_ASSOCIATION){
            Messages.showErrorDialog("Error, Association between '"
                            +mainEntity.className()+"' and '"
                            +child.className()+"' already exist on main entity, " +
                            "to make a change, remove it, and add again.",
                    "Error Duplicated Association Detected");
        }else{
            mainEntity.associations().add(new ClassAssociation(((AssociationType) Objects.requireNonNull(
                    associationTypeJCbx.getSelectedItem())).eRelType(),child.className(),
                    child.classId()));
            updateAssociations();
        }

    }

    public void updateAssociations(){
        DefaultTableModel model = (DefaultTableModel) associationsJtbl.getModel();
        model.setRowCount(0);
        mainEntity.associations().forEach(as -> {
            model.addRow(new Object[]{mainEntity.className(),Utils.getAssociationType(as.eRelType()),as.childRelatedClass()});
        });
    }


}
