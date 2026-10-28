package com.isysdcore.jsautocrud.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.isysdcore.jsautocrud.model.ClassAssociation;
import com.isysdcore.jsautocrud.model.ClassParameters;
import com.isysdcore.jsautocrud.model.EDbType;
import com.isysdcore.jsautocrud.util.ClassExtendsFinder;
import com.isysdcore.jsautocrud.util.Constants;
import com.isysdcore.jsautocrud.util.ECreationMode;
import com.isysdcore.jsautocrud.util.Utils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Modern wizard that collects the options needed to generate CRUD classes.
 */
public class ClassDefinitionsDialog extends DialogWrapper {

    private static final int TABLE_COL_CONTROLLER = 1;
    private static final int TABLE_COL_SERVICE = 2;
    private static final int TABLE_COL_DTO = 3;
    private static final int TABLE_COL_TEST = 4;
    private static final int TABLE_COL_ASSOCIATIONS = 5;

    private static final String CARD_ENTITY = "entity";
    private static final String CARD_ASSOCIATIONS = "associations";
    private static final String CARD_OPTIONS = "options";

    private final Project project;

    private CardLayout cardLayout;
    private JPanel cardsPanel;

    private JButton backButton;
    private JButton nextButton;

    // Step 1 - entity details
    private JLabel entityNamesLbl;
    private JBTextField entityNamesTf;
    private JLabel dbTypeLbl;
    private JComboBox<EDbType> dbTypeJcb;
    private JLabel idTypeLbl;
    private JComboBox<String> idTypeJcb;
    private JLabel existingEntityLbl;
    private JComboBox<PsiClass> existingEntityJcb;
    private JLabel resolvedIdLbl;

    // Step 2 - associations
    private JBList<ClassParameters> entitiesToCreateList2;
    private DefaultListModel<ClassParameters> modelToCreate;
    private JBList<ClassParameters> entitiesWithAssociationsList;
    private DefaultListModel<ClassParameters> modelAssociations;

    // Step 3 - options
    private JTable optionsTable;

    private ECreationMode creationMode = ECreationMode.FROM_SCRATCH;
    private EDbType dbType = EDbType.SQL;
    private String idDataType = "UUID";
    private PsiClass existingEntity;
    private boolean associationsStepUsed;
    private boolean updatingTable;

    private List<ClassParameters> entitiesToCreateList = new ArrayList<>();

    public ClassDefinitionsDialog(@Nullable Project project) {
        this(project, ECreationMode.FROM_SCRATCH);
    }

    public ClassDefinitionsDialog(@Nullable Project project, ECreationMode creationMode) {
        super(project);
        this.project = project;
        this.creationMode = creationMode;
        setTitle("JSAutoCrud - Generate CRUD Classes");
        setOKButtonText("Generate");
        setOKActionEnabled(false);
        init();
        prepareEntityCardForMode();
        showCard(CARD_ENTITY);
    }

    public List<ClassParameters> getEntitiesToCreateList() {
        syncOptionsFromTable();
        return entitiesToCreateList;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBPanel<?> root = new JBPanel<>(new BorderLayout(0, 12));
        root.setBorder(JBUI.Borders.empty(16));
        root.setPreferredSize(new Dimension(640, 440));

        root.add(buildHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.add(buildEntityCard(), CARD_ENTITY);
        cardsPanel.add(buildAssociationsCard(), CARD_ASSOCIATIONS);
        cardsPanel.add(buildOptionsCard(), CARD_OPTIONS);
        root.add(cardsPanel, BorderLayout.CENTER);

        root.add(buildNavigationBar(), BorderLayout.SOUTH);
        return root;
    }

    // ------------------------------------------------------------------ cards

    private JComponent buildHeader() {
        JBPanel<?> header = new JBPanel<>(new BorderLayout());
        JBLabel title = new JBLabel("JSAutoCrud Class Generator");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JBLabel subtitle = new JBLabel("Generate Spring Boot CRUD layers powered by generic-auto-crud");
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JComponent buildEntityCard() {
        entityNamesTf = new JBTextField();
        entityNamesTf.setToolTipText("Insert entity names without spaces, separated by commas. Example: Person,Address");

        dbTypeJcb = new ComboBox<>(new DefaultComboBoxModel<>(EDbType.values()));
        idTypeJcb = new ComboBox<>(new String[]{"UUID", "Long", "Integer", "String"});

        existingEntityJcb = new ComboBox<>(new DefaultComboBoxModel<>());
        existingEntityJcb.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getName());
            label.setOpaque(isSelected);
            return label;
        });
        existingEntityJcb.addActionListener(e -> onExistingEntitySelected());

        entityNamesLbl = new JBLabel("Entity names:");
        dbTypeLbl = new JBLabel("Database type:");
        idTypeLbl = new JBLabel("ID datatype:");
        existingEntityLbl = new JBLabel("Project entity:");
        resolvedIdLbl = new JBLabel("");

        JPanel form = FormBuilder.createFormBuilder()
                .addLabeledComponent(entityNamesLbl, entityNamesTf)
                .addLabeledComponent(dbTypeLbl, dbTypeJcb)
                .addLabeledComponent(idTypeLbl, idTypeJcb)
                .addLabeledComponent(existingEntityLbl, existingEntityJcb)
                .addLabeledComponent("", resolvedIdLbl)
                .getPanel();
        form.setBorder(BorderFactory.createTitledBorder("Entity details"));

        JBPanel<?> card = new JBPanel<>(new BorderLayout());
        card.add(form, BorderLayout.NORTH);
        return card;
    }

    private JComponent buildAssociationsCard() {
        modelToCreate = new DefaultListModel<>();
        entitiesToCreateList2 = new JBList<>(modelToCreate);
        entitiesToCreateList2.setCellRenderer(Utils.getCustomRender());
        entitiesToCreateList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        modelAssociations = new DefaultListModel<>();
        entitiesWithAssociationsList = new JBList<>(modelAssociations);
        entitiesWithAssociationsList.setCellRenderer(Utils.getCustomRender());
        entitiesWithAssociationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton associateButton = new JButton("Associate...");
        associateButton.addActionListener(e -> openAssociationDialog());

        JPanel lists = new JPanel(new GridLayout(1, 3, 12, 0));
        lists.add(new JBScrollPane(entitiesToCreateList2));
        lists.add(wrapButton(associateButton));
        lists.add(new JBScrollPane(entitiesWithAssociationsList));

        JBPanel<?> card = new JBPanel<>(new BorderLayout(0, 8));
        card.add(new JBLabel("Select an entity, then press Associate to define its relationships with other entities."),
                BorderLayout.NORTH);
        card.add(lists, BorderLayout.CENTER);
        card.setBorder(BorderFactory.createTitledBorder("Entity associations (SQL only)"));
        return card;
    }

    private JComponent buildOptionsCard() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Entity", "Controller", "Service", "DTO", "Test", "Associations"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == TABLE_COL_CONTROLLER || column == TABLE_COL_SERVICE
                        || column == TABLE_COL_DTO || column == TABLE_COL_TEST;
            }
        };

        optionsTable = new JBTable(model) {
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case TABLE_COL_CONTROLLER, TABLE_COL_SERVICE, TABLE_COL_DTO, TABLE_COL_TEST -> Boolean.class;
                    default -> String.class;
                };
            }
        };
        optionsTable.setRowSelectionAllowed(false);
        optionsTable.getModel().addTableModelListener(this::onOptionsChanged);

        JBScrollPane scrollPane = new JBScrollPane(optionsTable);
        scrollPane.setPreferredSize(new Dimension(580, 220));

        JBCheckBox generateTestsCheckbox = new JBCheckBox("Also generate integration test classes");
        generateTestsCheckbox.addActionListener(e -> applyGlobalTestOption(generateTestsCheckbox.isSelected()));

        JBPanel<?> card = new JBPanel<>(new BorderLayout(0, 8));
        card.add(new JBLabel("Review the layers to generate. Controller automatically enables Service."), BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(generateTestsCheckbox, BorderLayout.SOUTH);
        card.setBorder(BorderFactory.createTitledBorder("Generation options"));
        return card;
    }

    private JComponent buildNavigationBar() {
        backButton = new JButton("Back");
        nextButton = new JButton("Next");

        backButton.addActionListener(e -> navigateBack());
        nextButton.addActionListener(e -> navigateNext());

        JPanel bar = new JPanel(new BorderLayout());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.add(nextButton);
        bar.add(backButton, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ------------------------------------------------------------- navigation

    private void showCard(String card) {
        showCardInternal(card);
    }

    private void navigateBack() {
        syncOptionsFromTable();
        switch (currentCard()) {
            case CARD_ASSOCIATIONS -> showCard(CARD_ENTITY);
            case CARD_OPTIONS -> showCard(associationsStepUsed ? CARD_ASSOCIATIONS : CARD_ENTITY);
            default -> showCard(CARD_ENTITY);
        }
    }

    private void navigateNext() {
        switch (currentCard()) {
            case CARD_ENTITY -> {
                if (!buildEntitiesFromEntityStep()) {
                    return;
                }
                associationsStepUsed = creationMode == ECreationMode.FROM_SCRATCH
                        && entitiesToCreateList.size() > 1
                        && dbType == EDbType.SQL;
                if (associationsStepUsed) {
                    refreshAssociationLists();
                    showCard(CARD_ASSOCIATIONS);
                } else {
                    refreshOptionsTable();
                    showCard(CARD_OPTIONS);
                }
            }
            case CARD_ASSOCIATIONS -> {
                refreshAssociationLists();
                refreshOptionsTable();
                showCard(CARD_OPTIONS);
            }
            default -> {
            }
        }
    }

    private String currentCard() {
        return activeCard;
    }

    private String activeCard = CARD_ENTITY;

    private void showCardInternal(String card) {
        cardLayout.show(cardsPanel, card);
        activeCard = card;
        boolean first = CARD_ENTITY.equals(card);
        boolean last = CARD_OPTIONS.equals(card);
        backButton.setVisible(!first);
        nextButton.setVisible(!last);
        setOKActionEnabled(last);
    }

    // ------------------------------------------------------- step preparation

    private void prepareEntityCardForMode() {
        boolean fromModel = creationMode != ECreationMode.FROM_SCRATCH;
        entityNamesLbl.setVisible(!fromModel);
        entityNamesTf.setVisible(!fromModel);
        dbTypeLbl.setVisible(!fromModel);
        dbTypeJcb.setVisible(!fromModel);
        idTypeLbl.setVisible(!fromModel);
        idTypeJcb.setVisible(!fromModel);
        existingEntityLbl.setVisible(fromModel);
        existingEntityJcb.setVisible(fromModel);
        resolvedIdLbl.setVisible(fromModel);

        if (fromModel) {
            loadExistingEntities();
        }
    }

    private void loadExistingEntities() {
        List<PsiClass> classes = ClassExtendsFinder.findClassesExtending(project,
                Constants.PKG_SQL + "." + Constants.CLASS_SQL_ENTITY,
                Constants.PKG_NOSQL + "." + Constants.CLASS_NOSQL_ENTITY);
        DefaultComboBoxModel<PsiClass> model = (DefaultComboBoxModel<PsiClass>) existingEntityJcb.getModel();
        model.removeAllElements();
        model.addAll(classes);
        if (!classes.isEmpty()) {
            existingEntityJcb.setSelectedIndex(0);
            onExistingEntitySelected();
        }
    }

    private void onExistingEntitySelected() {
        existingEntity = (PsiClass) existingEntityJcb.getSelectedItem();
        if (existingEntity == null) {
            return;
        }
        dbType = detectDbType(existingEntity);
        idDataType = ClassExtendsFinder.resolveIdTypeName(existingEntity);
        resolvedIdLbl.setText("Detected ID type: " + idDataType + "  |  Database: " + dbType.label());
    }

    private EDbType detectDbType(PsiClass psiClass) {
        PsiClass current = psiClass;
        while (current != null) {
            String qualifiedName = current.getQualifiedName();
            if ((Constants.PKG_SQL + "." + Constants.CLASS_SQL_ENTITY).equals(qualifiedName)) {
                return EDbType.SQL;
            }
            if ((Constants.PKG_NOSQL + "." + Constants.CLASS_NOSQL_ENTITY).equals(qualifiedName)) {
                return EDbType.MONGODB;
            }
            current = current.getSuperClass();
        }
        return EDbType.SQL;
    }

    private boolean buildEntitiesFromEntityStep() {
        if (creationMode == ECreationMode.FROM_SCRATCH) {
            dbType = (EDbType) dbTypeJcb.getSelectedItem();
            if (dbType == null) {
                dbType = EDbType.SQL;
            }
            idDataType = (String) idTypeJcb.getSelectedItem();
            if (dbType == EDbType.MONGODB) {
                idDataType = "String";
            }
            String content = entityNamesTf.getText().trim();
            if (!validateEntityNames(content)) {
                return false;
            }

            List<ClassParameters> parsed = new ArrayList<>();
            for (String name : content.split(",")) {
                String trimmed = name.trim();
                parsed.add(new ClassParameters(UUID.randomUUID(), trimmed, idDataType, true, true, false, false,
                        dbType, new ArrayList<>()));
            }
            entitiesToCreateList = parsed;
            return true;
        }

        existingEntity = (PsiClass) existingEntityJcb.getSelectedItem();
        if (existingEntity == null) {
            Messages.showErrorDialog(project, "Select an entity from the project.", "No Entity Selected");
            return false;
        }
        dbType = detectDbType(existingEntity);
        idDataType = dbType == EDbType.MONGODB ? "String" : ClassExtendsFinder.resolveIdTypeName(existingEntity);
        entitiesToCreateList = new ArrayList<>();
        entitiesToCreateList.add(new ClassParameters(UUID.randomUUID(), existingEntity.getName(), idDataType,
                creationMode == ECreationMode.ONLY_CONTROLLERS || creationMode == ECreationMode.FROM_MODEL,
                creationMode == ECreationMode.ONLY_SERVICES || creationMode == ECreationMode.FROM_MODEL,
                false, false, dbType, new ArrayList<>()));
        return true;
    }

    private boolean validateEntityNames(String content) {
        if (content.isEmpty()) {
            Messages.showErrorDialog(project, "Insert at least one entity name.", "Entity Name Required");
            return false;
        }
        if (content.startsWith(",") || content.endsWith(",")) {
            Messages.showErrorDialog(project, "Entity names cannot start or end with ','.", "Entity Name Error");
            return false;
        }
        if (Utils.containsSpecialCharacter(content)) {
            Messages.showErrorDialog(project, "Entity names can only contain letters, digits and commas.", "Entity Name Error");
            return false;
        }
        for (String name : content.split(",")) {
            String trimmed = name.trim();
            if (trimmed.length() < 3 || trimmed.contains(" ")) {
                Messages.showErrorDialog(project, "Each entity name must have at least 3 characters and no spaces.", "Entity Name Error");
                return false;
            }
            if (!Character.isJavaIdentifierStart(trimmed.charAt(0))) {
                Messages.showErrorDialog(project, "Entity name '" + trimmed + "' must start with a letter or underscore.", "Entity Name Error");
                return false;
            }
            for (int i = 1; i < trimmed.length(); i++) {
                if (!Character.isJavaIdentifierPart(trimmed.charAt(i))) {
                    Messages.showErrorDialog(project, "Entity name '" + trimmed + "' contains invalid characters.", "Entity Name Error");
                    return false;
                }
            }
        }
        return true;
    }

    // ------------------------------------------------------------ associations

    private void openAssociationDialog() {
        ClassParameters selected = entitiesToCreateList2.getSelectedValue();
        if (selected == null) {
            Messages.showInfoMessage(project, "Select an entity to associate.", "No Entity Selected");
            return;
        }
        ClassAssociationDialog dialog = new ClassAssociationDialog(project, selected, entitiesToCreateList);
        dialog.show();
        if (dialog.isOK()) {
            refreshAssociationLists();
        }
    }

    private void refreshAssociationLists() {
        modelToCreate.clear();
        modelToCreate.addAll(entitiesToCreateList);

        modelAssociations.clear();
        entitiesToCreateList.stream()
                .filter(entity -> !entity.associations().isEmpty())
                .forEach(modelAssociations::addElement);
    }

    // ----------------------------------------------------------------- options

    private void refreshOptionsTable() {
        DefaultTableModel model = (DefaultTableModel) optionsTable.getModel();
        model.setRowCount(0);
        for (ClassParameters cp : entitiesToCreateList) {
            StringBuilder associations = new StringBuilder();
            for (ClassAssociation association : cp.associations()) {
                associations.append(association.childRelatedClass())
                        .append(" ").append(Utils.getAssociationType(association.eRelType())).append("; ");
            }
            model.addRow(new Object[]{
                    cp.className(),
                    cp.createCtrl(),
                    cp.createSrvs(),
                    cp.generateDto(),
                    cp.generateTest(),
                    associations.toString().trim()
            });
        }
    }

    private void onOptionsChanged(TableModelEvent e) {
        if (updatingTable || e.getType() != TableModelEvent.UPDATE) {
            return;
        }
        int row = e.getFirstRow();
        int column = e.getColumn();
        if (row < 0 || column < 0 || row >= optionsTable.getRowCount()) {
            return;
        }

        updatingTable = true;
        try {
            if (column == TABLE_COL_CONTROLLER) {
                boolean controller = Boolean.TRUE.equals(optionsTable.getValueAt(row, column));
                if (controller) {
                    optionsTable.setValueAt(true, row, TABLE_COL_SERVICE);
                }
            } else if (column == TABLE_COL_SERVICE) {
                boolean service = Boolean.TRUE.equals(optionsTable.getValueAt(row, column));
                if (!service) {
                    optionsTable.setValueAt(false, row, TABLE_COL_CONTROLLER);
                }
            }
            syncOptionsFromTable();
        } finally {
            updatingTable = false;
        }
    }

    private void syncOptionsFromTable() {
        if (optionsTable == null || optionsTable.getRowCount() == 0) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) optionsTable.getModel();
        List<ClassParameters> updated = new ArrayList<>();
        for (int row = 0; row < model.getRowCount(); row++) {
            String entityName = model.getValueAt(row, 0).toString();
            boolean controller = Boolean.TRUE.equals(model.getValueAt(row, TABLE_COL_CONTROLLER));
            boolean service = Boolean.TRUE.equals(model.getValueAt(row, TABLE_COL_SERVICE));
            boolean dto = Boolean.TRUE.equals(model.getValueAt(row, TABLE_COL_DTO));
            boolean test = Boolean.TRUE.equals(model.getValueAt(row, TABLE_COL_TEST));

            for (ClassParameters cp : entitiesToCreateList) {
                if (cp.className().equalsIgnoreCase(entityName)) {
                    updated.add(cp.withOptions(controller, service, dto, test));
                    break;
                }
            }
        }
        if (updated.size() == entitiesToCreateList.size()) {
            entitiesToCreateList = updated;
        }
    }

    private void applyGlobalTestOption(boolean generateTests) {
        updatingTable = true;
        try {
            List<ClassParameters> updated = new ArrayList<>();
            for (int row = 0; row < optionsTable.getRowCount(); row++) {
                optionsTable.setValueAt(generateTests, row, TABLE_COL_TEST);
            }
            for (ClassParameters cp : entitiesToCreateList) {
                updated.add(cp.withOptions(cp.createCtrl(), cp.createSrvs(), cp.generateDto(), generateTests));
            }
            entitiesToCreateList = updated;
        } finally {
            updatingTable = false;
        }
    }

    private JPanel wrapButton(JButton button) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(button);
        return wrapper;
    }
}
