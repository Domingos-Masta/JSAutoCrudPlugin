package com.isysdcore.jsautocrud.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.isysdcore.jsautocrud.model.*;
import com.isysdcore.jsautocrud.util.Utils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * Modal dialog used to define JPA associations between two SQL entities.
 */
public class ClassAssociationDialog extends DialogWrapper {

    private final ClassParameters mainEntity;
    private final List<ClassParameters> classParametersList;

    private JComboBox<ClassParameters> classParametersJCbx;
    private JComboBox<AssociationType> associationTypeJCbx;
    private JTable associationsJtbl;

    private static final String[] TABLE_HEADERS = {"Association Owner", "Type", "Associated Entity"};

    public ClassAssociationDialog(@Nullable Project project, ClassParameters selectedClass, List<ClassParameters> classParameters) {
        super(project);
        this.mainEntity = selectedClass;
        this.classParametersList = classParameters.stream()
                .filter(cp -> !cp.classId().equals(selectedClass.classId()))
                .toList();
        setTitle("Associations for " + mainEntity.className());
        setOKButtonText("Done");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBPanel<?> root = new JBPanel<>(new BorderLayout(0, 12));
        root.setBorder(JBUI.Borders.empty(12));

        root.add(buildSelectionPanel(), BorderLayout.NORTH);
        root.add(buildAssociationsPanel(), BorderLayout.CENTER);

        return root;
    }

    private JComponent buildSelectionPanel() {
        DefaultComboBoxModel<ClassParameters> entityModel = new DefaultComboBoxModel<>();
        entityModel.addAll(classParametersList);
        classParametersJCbx = new ComboBox<>(entityModel);
        classParametersJCbx.setRenderer(Utils.getCustomRender());

        DefaultComboBoxModel<AssociationType> associationModel = new DefaultComboBoxModel<>();
        associationModel.addAll(Utils.getAssociationType(mainEntity.className(), "Child"));
        associationTypeJCbx = new ComboBox<>(associationModel);
        associationTypeJCbx.setRenderer(Utils.getCustomCbxRender());

        JButton associateButton = new JButton("Associate");
        associateButton.addActionListener(e -> {
            ClassParameters child = (ClassParameters) classParametersJCbx.getSelectedItem();
            if (child != null) {
                addEntityAssociation(child);
            }
        });

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonRow.add(associateButton);

        JPanel form = FormBuilder.createFormBuilder()
                .addLabeledComponent("Entity to associate:", classParametersJCbx)
                .addLabeledComponent("Association type:", associationTypeJCbx)
                .addComponent(buttonRow)
                .getPanel();
        form.setBorder(BorderFactory.createTitledBorder("New association"));
        return form;
    }

    private JComponent buildAssociationsPanel() {
        DefaultTableModel tableModel = new DefaultTableModel(TABLE_HEADERS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        associationsJtbl = new JBTable(tableModel);
        associationsJtbl.setRowSelectionAllowed(true);
        associationsJtbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JBScrollPane scrollPane = new JBScrollPane(associationsJtbl);
        scrollPane.setPreferredSize(new Dimension(480, 180));

        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeSelectedAssociation());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        buttonRow.add(removeButton);

        JBPanel<?> panel = new JBPanel<>(new BorderLayout(0, 8));
        panel.add(new JBLabel("Defined associations:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonRow, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createTitledBorder("Associations"));
        return panel;
    }

    private void addEntityAssociation(ClassParameters child) {
        if (Utils.checkDuplicatedAssociations(mainEntity, child) != EAssociationStatus.NOT_ASSOCIATION) {
            Messages.showErrorDialog("The association between '" + mainEntity.className() + "' and '"
                            + child.className() + "' already exists.\nRemove it first to change its type.",
                    "Duplicate Association");
            return;
        }
        mainEntity.associations().add(new ClassAssociation(
                ((AssociationType) Objects.requireNonNull(associationTypeJCbx.getSelectedItem())).eRelType(),
                child.className(),
                child.classId()));
        updateAssociations();
    }

    private void removeSelectedAssociation() {
        int selectedRow = associationsJtbl.getSelectedRow();
        if (selectedRow < 0) {
            Messages.showInfoMessage("Select an association to remove.", "No Selection");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) associationsJtbl.getModel();
        Object toRemove = model.getValueAt(selectedRow, 2);
        if (Utils.removeAssociation(mainEntity, toRemove.toString())) {
            model.removeRow(selectedRow);
        }
    }

    private void updateAssociations() {
        DefaultTableModel model = (DefaultTableModel) associationsJtbl.getModel();
        model.setRowCount(0);
        mainEntity.associations().forEach(association -> model.addRow(new Object[]{
                mainEntity.className(),
                Utils.getAssociationType(association.eRelType()),
                association.childRelatedClass()
        }));
    }
}
