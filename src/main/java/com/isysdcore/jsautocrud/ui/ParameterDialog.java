package com.isysdcore.jsautocrud.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author domingos.fernando
 * @created 18/09/2024 - 11:34
 * @project SpringAutoCrudClassGenerator
 */
public class ParameterDialog extends DialogWrapper {

    //Presentation painel
    private JLabel entityLbl;
    private JTextField entityNameTf;
    private JLabel dataTypeLbl;
    private JComboBox<String> idDataTypes;
    private JLabel generateCtrlLbl;
    private JCheckBox generateController;
    private JLabel generateServiceLbl;
    private JCheckBox generateServices;

    private JPanel dialogPanel;


    public ParameterDialog(@Nullable Project project) {
        super(project);
        setTitle("Generate JS Auto Crud");
        setResizable(false);
        setSize(400, 200);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {


//        JLabel label = new JLabel("Testing");
//        label.setPreferredSize(new Dimension(100, 100));
//        dialogPanel.add(label);
        initializeComponents();
        addComponentsToPanel();
        addEvents();

        return dialogPanel;
    }

    private void initializeComponents(){
        dialogPanel = new JPanel(new GridLayout(5,2));

        entityLbl= new JLabel("Entity Name: ");
        dataTypeLbl= new JLabel("ID Datatype: ");
        generateCtrlLbl= new JLabel("Generate Rest Controller: ");
        generateServiceLbl= new JLabel("Generate Rest Service: ");

        entityNameTf = new JTextField();
        idDataTypes = new ComboBox<>(new String[]{"UUID", "Long"});
        generateController = new JCheckBox();
        generateServices  = new JCheckBox();
    }

    private void addComponentsToPanel(){
        dialogPanel.add(entityLbl);
        dialogPanel.add(entityNameTf);

        dialogPanel.add(dataTypeLbl);
        dialogPanel.add(idDataTypes);

        dialogPanel.add(generateCtrlLbl);
        dialogPanel.add(generateController);

        dialogPanel.add(generateServiceLbl);
        dialogPanel.add(generateServices);
    }

    private void addEvents(){
        generateController.addChangeListener(e -> {
            if(generateController.isSelected()){
                generateServices.setSelected(true);
                // Disable other checks
                generateServices.setEnabled(false);
            }else{
                generateServices.setSelected(false);
                // Enable other checks
                generateServices.setEnabled(true);
            }
        });

    }

    public String getEntityNameTf() {
        return entityNameTf.getText();
    }

    public String getIdDataTypes() {
        return idDataTypes.getSelectedItem().toString();
    }

    public Boolean getGenerateController() {
        return generateController.isSelected();
    }

    public Boolean getGenerateServices() {
        return generateServices.isSelected();
    }

}
