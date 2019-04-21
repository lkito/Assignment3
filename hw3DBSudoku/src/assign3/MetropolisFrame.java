package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MetropolisFrame extends JFrame {
    private JTextArea metropolisTA;
    private JTextArea continentTa;
    private JTextArea populationTA;
    private JButton searchButton;
    private JButton addButton;
    private JComboBox nameCombo;
    private JComboBox populationCombo;
    private MetropolisDataBase MDB;
    private MyTable mt;
    private JTable table;
    private JScrollPane scrollpane;

    private static String[] columnTitles = { "Metropolis", "Continent", "Population"};

    public MetropolisFrame(){
        super("Metropolis database");
        MDB = new MetropolisDataBase();
        initLayout();
        initListeners();
    }

    private class MyTable extends AbstractTableModel {
        private String[] columnNames;

        public MyTable(String[] columns){
            super();
            columnNames = new String[columns.length];
            for(int i = 0; i < columns.length; i++){
                columnNames[i] = columns[i];
            }
        }

        /**
         * returns name of the column on specified index
         * @param column index of column
         */
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        /**
         * returns amount of rows in database
         */
        @Override
        public int getRowCount() {
            return MDB.getRows();
        }

        /**
         * returns amount of columns in database
         */
        @Override
        public int getColumnCount() {
            return MDB.getCols();
        }

        /**
         * returns value at specified row and column
         * @param i index of row
         * @param j index of column
         */
        @Override
        public Object getValueAt(int i, int j) {
            List<SingleEntry> data = MDB.getCurrentData();
            if(j == 0){
                return data.get(i).getMetropolis();
            } else if(j == 1){
                return data.get(i).getContinent();
            } else return data.get(i).getPopulation();
        }
    }

    private void initLayout(){
        mt = new MyTable(columnTitles);
        table = new JTable(mt);
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout(4, 4));

        JPanel northPanel = new JPanel(new GridLayout(1, 6));
        JPanel eastPanel = new JPanel(new GridLayout(4, 1));
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3));
        JPanel comboPanel = new JPanel(new GridLayout(2, 1));
        comboPanel.setBorder(new TitledBorder("Search options"));

        metropolisTA = new JTextArea(1, 5);
        continentTa = new JTextArea(1, 5);
        populationTA = new JTextArea(1, 5);

        searchButton = new JButton("Search");
        addButton = new JButton("Add");

        String[] popStrings = {MDB.POPULATION_MORE_THAN_NAME, MDB.POPULATION_LESS_OR_EQUAL_NAME};
        populationCombo = new JComboBox(popStrings);

        String[] nameStrings = {MDB.MATCH_TYPE_EXACT_NAME, MDB.MATCH_TYPE_PARTIAL_NAME};
        nameCombo = new JComboBox(nameStrings);

        northPanel.add(new JLabel("      Metropolis: "));
        northPanel.add(metropolisTA);
        northPanel.add(new JLabel("      Continent: "));
        northPanel.add(continentTa);
        northPanel.add(new JLabel("      Population: "));
        northPanel.add(populationTA);

        buttonPanel.add(new JLabel(""));
        buttonPanel.add(addButton);
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(searchButton);
        buttonPanel.add(new JLabel(""));

        comboPanel.add(populationCombo);
        comboPanel.add(nameCombo);

        eastPanel.add(buttonPanel);
        eastPanel.add(comboPanel);

        add(northPanel, BorderLayout.NORTH);
        add(eastPanel, BorderLayout.EAST);
        scrollpane = new JScrollPane(table);
        add(scrollpane, BorderLayout.CENTER);

        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void initListeners(){
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                long pop;
                if(populationTA.getText().isEmpty()){
                    pop = 0;
                } else pop = Long.parseLong(populationTA.getText());
                MDB.addData(metropolisTA.getText(), continentTa.getText(), pop);
                mt.fireTableDataChanged();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                long pop;
                if(populationTA.getText().isEmpty()){
                    pop = -1;
                } else pop = Long.parseLong(populationTA.getText());
                int popCond;
                int nameCond;
                if(nameCombo.getSelectedIndex() == 0){
                    nameCond = MDB.MATCH_TYPE_EXACT;
                } else nameCond = MDB.MATCH_TYPE_PARTIAL;
                if(populationCombo.getSelectedIndex() == 0){
                    popCond = MDB.POPULATION_MORE_THAN;
                } else popCond = MDB.POPULATION_LESS_OR_EQUAL;
                MDB.searchData(metropolisTA.getText(),
                        continentTa.getText(), pop, popCond, nameCond);
                mt.fireTableDataChanged();
            }
        });
    }

    public static void main(String[] args) {
        // GUI Look And Feel
        // Do this incantation at the start of main() to tell Swing
        // to use the GUI LookAndFeel of the native platform. It's ok
        // to ignore the exception.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        MetropolisFrame frame = new MetropolisFrame();
    }
}
