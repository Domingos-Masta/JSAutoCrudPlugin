package com.isysdcore.jsautocrud.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.isysdcore.jsautocrud.model.ClassAssociation;
import com.isysdcore.jsautocrud.model.ClassParameters;
import com.isysdcore.jsautocrud.util.Constants;
import com.isysdcore.jsautocrud.util.Utils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author domingos.fernando
 * @created 20/09/2024 - 17:15
 * @project JSAutoCrud
 */
public class ClassDefinitionsDialog extends DialogWrapper {

    private static final Logger log = LoggerFactory.getLogger(ClassDefinitionsDialog.class);
    private CardLayout cardLayout;
    // Create the main panel with CardLayout
    private JPanel mainPanel;
    // Step 1 panel
    private JPanel entityMetaPanel;
    private JLabel entityLbl;
    private JTextField entityNamesTf;
    private JLabel dataTypeLbl;
    private JComboBox<String> idDataTypes;
    private JLabel emptyLbl1;
    private JButton eMtdBtNext;
    // Step 2 panel
    private JPanel entityAssocPanel;
    private JLabel manageRelationsLbl;
    private JList<ClassParameters> entitiesToCreate;
    private DefaultListModel<ClassParameters> modelToCreate;
    private JLabel classRelationsLbl;
    private JList<ClassParameters> entitiesRelationCreate;
    private DefaultListModel<ClassParameters> modelAssociations;
    private JLabel emptyLbl2;
    private JButton associationBtn;
    private JButton eRelBtNext;
    private JLabel emptyLbl3;
    private JButton eRelBtPrev;
    private JScrollPane spStepCreateJsp;
    private JScrollPane spStepAssocJsp;
    // Step 3 panel
    private JPanel entityOptPanel;
    private JButton eOptBtSubmit;
    private JButton eOptBtPrev;
    private JScrollPane spStep3Jsp;
    private JTable resumeOptJtbl;
    // Column Names
    String[] optTableHeaders = { "Entity Name", "Controller", "Service", "Associations" };

    //Presentation painel
    Map<UUID, ClassDefinitionsDialog> definitionsMap;
    private List<ClassParameters> entitiesToCreateList;
    Project project;

    public ClassDefinitionsDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle("Generate JS Auto Crud");
//        setResizable(false);
        setSize(400, 200);
        init();
    }

    public void initializeComponents(){
        mainPanel = new JPanel(new CardLayout());
        entityMetaPanel = new JPanel(new GridLayout(3, 3));
        entityMetaPanel.setSize(400, 200);
        entityAssocPanel = new JPanel(new GridLayout(3, 3));
        entityAssocPanel.setSize(400, 200);
        entityOptPanel = new JPanel(new GridLayout(3, 2));
        entityOptPanel.setSize(400, 200);

        eMtdBtNext = new JButton("Next");
        eRelBtNext = new JButton("Next");
        eRelBtPrev = new JButton("Prev");
        eOptBtPrev = new JButton("Prev");

        entityLbl= new JLabel("Entity Names: ");
        dataTypeLbl= new JLabel("ID Datatype: ");

        entityNamesTf = new JTextField();
        entityNamesTf.setToolTipText("Insert the Entities names without spaces and separated by ','");
        idDataTypes = new ComboBox<>(new String[]{"UUID", "Long", "Integer"});
        emptyLbl1 = new JLabel("");

        manageRelationsLbl = new JLabel("Entities To Create");
        modelToCreate = new DefaultListModel();
        entitiesToCreate = new JBList<>(modelToCreate);
        entitiesToCreate.setCellRenderer(Utils.getCustomRender());
        entitiesToCreate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        classRelationsLbl = new JLabel("Associates Entities");
        modelAssociations = new DefaultListModel();
        entitiesRelationCreate = new JBList<>(modelAssociations);
        entitiesRelationCreate.setCellRenderer(Utils.getCustomRender());

        emptyLbl2 = new JLabel("");
        associationBtn = new JButton("Associate");
        emptyLbl3 = new JLabel("");

    }

    public void addComponentsToWindows(){


        entityMetaPanel.add(entityLbl);

        entityMetaPanel.add(entityNamesTf);
        entityMetaPanel.add(dataTypeLbl);
        entityMetaPanel.add(idDataTypes);
        entityMetaPanel.add(emptyLbl1);
        entityMetaPanel.add(eMtdBtNext);

        entityAssocPanel.add(manageRelationsLbl);
        entityAssocPanel.add(emptyLbl2);
        entityAssocPanel.add(classRelationsLbl);

        spStepCreateJsp = new JBScrollPane(entitiesToCreate);
        entityAssocPanel.add(spStepCreateJsp);

        entityAssocPanel.add(associationBtn);

        spStepAssocJsp = new JBScrollPane(entitiesRelationCreate);
        entityAssocPanel.add(spStepAssocJsp);

        entityAssocPanel.add(eRelBtPrev);
        entityAssocPanel.add(emptyLbl3);
        entityAssocPanel.add(eRelBtNext);

        DefaultTableModel tableModel = new DefaultTableModel(optTableHeaders,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only the "First Name" and "Last Name" columns editable (column 1 and 2)
                // The "ID" column (column 0) will be non-editable
                return column != 0 && column != 3; // Set column 0 as non-editable
            }
        };
        resumeOptJtbl = new JBTable(tableModel){
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 1, 2 -> {return Boolean.class;}
                    default -> {return String.class;}
                }
            }
        };
        resumeOptJtbl.setPreferredScrollableViewportSize(resumeOptJtbl.getPreferredSize());
        spStep3Jsp = new JBScrollPane(resumeOptJtbl);;
        entityOptPanel.add(spStep3Jsp);
        entityOptPanel.add(eOptBtPrev);
        // Add all the panels to the CardLayout
        mainPanel.add(entityMetaPanel, "Step 1");
        mainPanel.add(entityAssocPanel, "Step 2");
        mainPanel.add(entityOptPanel, "Step 3");
        // Set up navigation between steps
        cardLayout = (CardLayout) mainPanel.getLayout();
    }

    public void addEventsToComponents(){
        this.setOKActionEnabled(false);
        eMtdBtNext.addActionListener(e -> {
            String content = entityNamesTf.getText().trim();
            if(content.isEmpty()){
                Messages.showErrorDialog(project, "Error, Class name can not contains empty spaces", "Class Name Error");
            } else if (content.contains(" ")) {
                Messages.showErrorDialog(project, "Error, Class name can not be empty and can't contains empty spaces", "Class Name Error");
            }else if (content.startsWith(",") || content.endsWith(",")){
                Messages.showErrorDialog(project, "Error, Class name can not start or ends with ','. \nUse ',' only to separate class names Ex: 'ClassOne,ClassTwo'.", "Class Name Error");
            }else if (Utils.containsSpecialCharacter(content)){
                Messages.showErrorDialog(project, "Error, Class name can not contains special characters", "Class Name Error");
            }
            else{
                String[] cont = content.split(",");
                entitiesToCreateList = new ArrayList<>();
                for(String tmp : cont){
                    if(tmp.contains(" ") || tmp.length() < 4){
                        Messages.showErrorDialog(project, "Error, Class name can not be empty", "Class Name Error");
                        return;
                    }else{
                        entitiesToCreateList.add(new ClassParameters(UUID.randomUUID(), tmp,idDataTypes.getSelectedItem().toString(),
                                true, true, new ArrayList<>()));
                    }
                }
                modelToCreate.clear();
                modelToCreate.addAll(entitiesToCreateList);
                cardLayout.show(mainPanel, "Step 2");
                this.setOKActionEnabled(true);
            }
        });
        eRelBtNext.addActionListener(e -> {
            updateAssociations();
            cardLayout.show(mainPanel, "Step 3");
        });
        eRelBtPrev.addActionListener(e -> {
            this.setOKActionEnabled(false);
            cardLayout.show(mainPanel, "Step 1");
        });
        eOptBtPrev.addActionListener(e -> {
            cardLayout.show(mainPanel, "Step 2");
        });
        associationBtn.addActionListener(e -> {
            ClassParameters selectedClass = entitiesToCreate.getSelectedValue();
            ClassAssociationDialog classAssociation = new ClassAssociationDialog(project, selectedClass, entitiesToCreateList);
            classAssociation.show();
            if(classAssociation.isOK()){
                updateAssociationList();
            }
        });
    }

    public void updateAssociations(){
        resumeOptJtbl.setRowSelectionAllowed(false);
        DefaultTableModel model = (DefaultTableModel) resumeOptJtbl.getModel();
        model.setRowCount(0);
        entitiesToCreateList.forEach(as -> {
            StringBuilder assoc = new StringBuilder();
            if (!as.associations().isEmpty()){
                for(ClassAssociation association: as.associations()){
                    assoc.append(association.childRelatedClass()).append(" ").append(Utils.getAssociationType(association.eRelType())).append(";\n");
                }
            }
            model.addRow(new Object[]{as.className(),
                    as.createCtrl(),
                    as.createSrvs(),
                    assoc});
        });
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if(column == Constants.RESUME_TABLE_CONTROLLER_COLUMN){
                    if (row != -1) {
                        Object controller = resumeOptJtbl.getValueAt(row, column);
                        if(controller.toString().equalsIgnoreCase("true")){
                            resumeOptJtbl.setValueAt(true,row, column + 1);
                        }
                        Object service = resumeOptJtbl.getValueAt(row, column + 1);
                        updateClassDefinitions(resumeOptJtbl.getValueAt(row, 0).toString(),
                                Boolean.parseBoolean(controller.toString()), Boolean.parseBoolean(service.toString()));
                    }
                }else if (column == Constants.RESUME_TABLE_SERVICE_COLUMN){
                    if (row != -1) {
                        Object service = resumeOptJtbl.getValueAt(row, column);
                        if(service.toString().equalsIgnoreCase("false")){
                            resumeOptJtbl.setValueAt(false, row, column - 1);
                        }
                        Object controller = resumeOptJtbl.getValueAt(row, column -1);
                        updateClassDefinitions(resumeOptJtbl.getValueAt(row, 0).toString(),
                                Boolean.parseBoolean(controller.toString()), Boolean.parseBoolean(service.toString()));
                    }
                }
            }
        });

    }

    public void updateClassDefinitions(String entityName, boolean controller, boolean service){
        entitiesToCreateList.forEach(as -> {
            if(as.className().equalsIgnoreCase(entityName)){
                as = as.withControllerAndService(service && controller,service);
            }
        });

    }

    public void updateAssociationList(){
        modelAssociations.clear();
        entitiesToCreateList.forEach(entity -> {
            if(!entity.associations().isEmpty()){
                modelAssociations.addElement(entity);
            }
        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        initializeComponents();
        addComponentsToWindows();
        addEventsToComponents();
        return mainPanel;
    }

    public List<ClassParameters> getEntitiesToCreateList() {
        return entitiesToCreateList;
    }
}
