import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;


public class GUI {
    // Class constants
    private static final int TEXT_FIELD_COLUMN_SIZE = 10;

    private BPlusTree<String, String> tree;

    // Section Labels.
    private JLabel insertSectionLabel;
    private JLabel searchDeleteSectionLabel;
    private JLabel loadSaveSectionLabel;
    private JLabel modifySectionLabel;

    // Tree stats labels.
    private JLabel treeDepthLabel;
    private JLabel treeTotalFusionLabel;
    private JLabel treeParentFusionLabel;
    private JLabel treeLeafFusionLabel;
    private JLabel treeTotalSplitLabel;
    private JLabel treeParentSplitLabel;
    private JLabel treeLeafSplitLabel;

    // Tree stats value labels.
    private JLabel depthLabel;
    private JLabel totalFusionLabel;
    private JLabel parentFusionLabel;
    private JLabel leafFusionLabel;
    private JLabel totalSplitLabel;
    private JLabel parentSplitLabel;
    private JLabel leafSplitLabel;

    // Text fields for user inputs.
    private JTextField addKeyText;
    private JTextField addValueText;
    private JTextField deleteKeyText;
    private JTextField searchKeyText;
    private JTextField modifyKeyText;
    private JTextField modifyValueText;

    // Action buttons.
    private JButton addBtn;
    private JButton deleteBtn;
    private JButton searchBtn;
    private JButton loadBtn;
    private JButton saveBtn;
    private JButton modifyBtn;

    public GUI(BPlusTree<String, String> tree) {
        this.tree = tree;
    }

    public void initializeGui() {
        initializeSectionLabels();
        initializeTextFields();
        initializeButtons();
        assignActions();
        JPanel middlePanel = createPanel();

        // Create frame and make it visible.
        JFrame window = new JFrame();
        window.add(middlePanel, "Center");
        window.pack();
        window.setLayout(null);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("BPlusTree");
        window.setVisible(true);
    }

    private void initializeSectionLabels() {
        insertSectionLabel = new JLabel("Enter a key followed by a value:");
        searchDeleteSectionLabel = new JLabel("Enter a key and submit:");
        loadSaveSectionLabel = new JLabel("Load file to begin and save when finished:");
        modifySectionLabel = new JLabel("Enter a key to modify followed by the new value: ");

        treeDepthLabel = new JLabel("Tree Depth: ");
        treeTotalSplitLabel = new JLabel("Total Number of Splits: ");
        treeParentSplitLabel = new JLabel("Total Parent Splits: ");
        treeLeafSplitLabel = new JLabel("Total Leaf Splits: ");
        treeTotalFusionLabel = new JLabel("Total Number of Fusions: ");
        treeParentFusionLabel = new JLabel("Total Parent Fusions: ");
        treeLeafFusionLabel = new JLabel("Total Leaf Fusions: ");

        depthLabel = new JLabel("0");
        totalSplitLabel = new JLabel("0");
        parentSplitLabel = new JLabel("0");
        leafSplitLabel = new JLabel("0");
        totalFusionLabel = new JLabel("0");
        parentFusionLabel = new JLabel("0");
        leafFusionLabel = new JLabel("0");
    }

    private void initializeTextFields() {
        addKeyText = new JTextField(TEXT_FIELD_COLUMN_SIZE);
        addValueText = new JTextField(TEXT_FIELD_COLUMN_SIZE);
        deleteKeyText = new JTextField(TEXT_FIELD_COLUMN_SIZE);
        searchKeyText = new JTextField(TEXT_FIELD_COLUMN_SIZE);
        modifyKeyText = new JTextField(TEXT_FIELD_COLUMN_SIZE);
        modifyValueText = new JTextField(TEXT_FIELD_COLUMN_SIZE);
    }

    private void initializeButtons() {
        addBtn = new JButton("Add");
        addBtn.setEnabled(false);

        deleteBtn = new JButton("Delete");
        deleteBtn.setEnabled(false);

        searchBtn = new JButton("Search");
        searchBtn.setEnabled(false);

        loadBtn = new JButton("Load Tree From Stored File");
        loadBtn.setEnabled(true);

        saveBtn = new JButton("Save Tree To File");
        saveBtn.setEnabled(false);

        modifyBtn = new JButton("Modify");
        modifyBtn.setEnabled(false);
    }

    private void assignActions() {
        loadBtn.addActionListener(e -> {
            try {
                Utils.loadBPlusTreeFromFile(Utils.INPUT_FILE, tree);
                addBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
                searchBtn.setEnabled(true);
                saveBtn.setEnabled(true);
                modifyBtn.setEnabled(true);
                updateStats();
                JOptionPane.showMessageDialog(null,
                        "File load was Successful.");
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(null,
                        "Error while loading the file.\n" + Arrays.toString(exception.getStackTrace()));
            }
        });

        addBtn.addActionListener(e -> {
            String addKey = addKeyText.getText();
            String addValue = addValueText.getText();
            String message;

            if (addKey.isEmpty() || addValue.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Both key and value is required.");
            } else {
                tree.insert(addKey, addValue);

                String findResult = tree.find(addKey);
                if (findResult != null) {
                    message = "Successfully added  [" + addKey + " : " + addValue + "]";
                } else {
                    message = "Key and Value were not added.";
                }

                addKeyText.setText("");
                addValueText.setText("");
                updateStats();
                JOptionPane.showMessageDialog(null, message);
            }
        });

        deleteBtn.addActionListener(e -> {
            String userDeleteInput = deleteKeyText.getText(); //emptied after button click
            String removedResponse;

            if (userDeleteInput.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cannot delete empty value.");
            } else {
                if (tree.remove(userDeleteInput)) {
                    removedResponse = " was deleted. ";
                } else removedResponse = " not found. ";

                deleteKeyText.setText("");
                updateStats();
                JOptionPane.showMessageDialog(null, userDeleteInput + removedResponse);
            }
        });

        searchBtn.addActionListener(e -> {
            String searchKey = searchKeyText.getText();

            if (searchKey.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Key is required for search.");
            } else {
                StringBuilder result = new StringBuilder("\n");
                Iterator<Pair<String, String>> iterator = tree.getNKeyValPair(
                        searchKey, 10).iterator();
                if (iterator.hasNext()) {
                    while (iterator.hasNext()) {
                        result.append(iterator.next()).append("                     ").append("\n");
                    }
                } else {
                    result = new StringBuilder(searchKey + " not found.");
                }

                searchKeyText.setText("");
                JOptionPane.showMessageDialog(null, result.toString());
            }
        });

        modifyBtn.addActionListener(e -> {
            String modifyKey = modifyKeyText.getText();
            String modifyValue = modifyValueText.getText();
            String modifySuccess;

            if(modifyKey.isEmpty() || modifyValue.isEmpty()){
                JOptionPane.showMessageDialog(null, "Both key and value is required.");
            } else {
                if (tree.modify(modifyKey, modifyValue)) {
                    modifySuccess = "Successfully changed the value of Key " + modifyKey + " to " + modifyValue;
                } else {
                    modifySuccess = "Key=" + modifyKey + "not found in tree.";
                }
                modifyKeyText.setText("");
                modifyValueText.setText("");
                updateStats();
                JOptionPane.showMessageDialog(null, modifySuccess);
            }
        });

        saveBtn.addActionListener(e -> {
            try {
                Utils.saveBPlusTreeFromFile(Utils.INPUT_FILE, tree);
                JOptionPane.showMessageDialog(null, "Successfully saved the File.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error while saving the file.\n" + Arrays.toString(ex.getStackTrace()));
            }
        });
    }

    private JPanel createPanel() {
        // Grid layout.
        //add spacing in consideration of 4 spaces a row.
        GridLayout gridLayout = new GridLayout(12, 2, 10, 15);
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(gridLayout);
        middlePanel.setPreferredSize(new Dimension(1200, 700));
        gridLayout.preferredLayoutSize(middlePanel);

        // Panel section that goes inside the pop-up *ORDER MATTERS*
        middlePanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 10));

        // Load and save section.
        middlePanel.add(loadSaveSectionLabel);
        middlePanel.add(loadBtn);
        middlePanel.add(saveBtn);

        // Insert section.
        middlePanel.add(insertSectionLabel);
        middlePanel.add(new JPanel());
        middlePanel.add(new JPanel());
        middlePanel.add(addKeyText);
        middlePanel.add(addValueText);
        middlePanel.add(addBtn);

        // Modify section.
        middlePanel.add(modifySectionLabel);
        middlePanel.add(new JPanel());
        middlePanel.add(new JPanel());
        middlePanel.add(modifyKeyText);
        middlePanel.add(modifyValueText);
        middlePanel.add(modifyBtn);

        // Search and delete section.
        middlePanel.add(searchDeleteSectionLabel);
        middlePanel.add(new JPanel());
        middlePanel.add(new JPanel());
        middlePanel.add(deleteKeyText);
        middlePanel.add(deleteBtn);
        middlePanel.add(new JPanel());
        middlePanel.add(searchKeyText);
        middlePanel.add(searchBtn);
        middlePanel.add(new JPanel());

        // Panels for each stats.
        JPanel treeDepthPanel = new JPanel();
        treeDepthPanel.setLayout(new GridLayout(1, 2, 1, 1));
        treeDepthPanel.add(treeDepthLabel);
        treeDepthPanel.add(depthLabel);

        JPanel splitPanel = new JPanel();
        splitPanel.setLayout(new GridLayout(1, 2, 1, 1));
        splitPanel.add(treeTotalSplitLabel);
        splitPanel.add(totalSplitLabel);

        JPanel parentSplitPanel = new JPanel();
        parentSplitPanel.setLayout(new GridLayout(1, 2, 1, 1));
        parentSplitPanel.add(treeParentSplitLabel);
        parentSplitPanel.add(parentSplitLabel);

        JPanel leafSplitPanel = new JPanel();
        leafSplitPanel.setLayout(new GridLayout(1, 2, 1, 1));
        leafSplitPanel.add(treeLeafSplitLabel);
        leafSplitPanel.add(leafSplitLabel);

        JPanel fusionPanel = new JPanel();
        fusionPanel.setLayout(new GridLayout(1, 2, 1, 1));
        fusionPanel.add(treeTotalFusionLabel);
        fusionPanel.add(totalFusionLabel);

        JPanel parentFusionPanel = new JPanel();
        parentFusionPanel.setLayout(new GridLayout(1, 2, 1, 1));
        parentFusionPanel.add(treeParentFusionLabel);
        parentFusionPanel.add(parentFusionLabel);

        JPanel leafFusionPanel = new JPanel();
        leafFusionPanel.setLayout(new GridLayout(1, 2, 1, 1));
        leafFusionPanel.add(treeLeafFusionLabel);
        leafFusionPanel.add(leafFusionLabel);

        // Append panel of each stats.
        middlePanel.add(treeDepthPanel);
        middlePanel.add(new JPanel());
        middlePanel.add(new JPanel());
        middlePanel.add(splitPanel);
        middlePanel.add(parentSplitPanel);
        middlePanel.add(leafSplitPanel);
        middlePanel.add(fusionPanel);
        middlePanel.add(parentFusionPanel);
        middlePanel.add(leafFusionPanel);

        return middlePanel;
    }

    private void updateStats(){
        depthLabel.setText(String.valueOf(tree.getHeight()));
        totalSplitLabel.setText(String.valueOf(tree.getSplits()));
        parentSplitLabel.setText(String.valueOf(tree.getInternalNodeSplits()));
        leafSplitLabel.setText(String.valueOf(tree.getLeafNodeSplits()));
        totalFusionLabel.setText(String.valueOf(tree.getFusions()));
        parentFusionLabel.setText(String.valueOf(tree.getInternalNodeFusions()));
        leafFusionLabel.setText(String.valueOf(tree.getLeafNodeFusions()));
    }

} //close class




