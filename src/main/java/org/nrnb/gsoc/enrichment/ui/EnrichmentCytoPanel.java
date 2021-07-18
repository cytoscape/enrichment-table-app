package org.nrnb.gsoc.enrichment.ui;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.*;
import org.cytoscape.model.events.*;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.CyColorPaletteChooserFactory;
import org.cytoscape.util.swing.IconManager;
import org.cytoscape.util.swing.TextIcon;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.json.simple.JSONObject;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;
import org.nrnb.gsoc.enrichment.tasks.EnrichmentAdvancedOptionsTask;
import org.nrnb.gsoc.enrichment.tasks.EnrichmentTask;
import org.nrnb.gsoc.enrichment.tasks.ExportEnrichmentTableTask;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static org.nrnb.gsoc.enrichment.utils.IconUtils.*;

public class EnrichmentCytoPanel extends JPanel
        implements CytoPanelComponent2, ListSelectionListener, ActionListener, RowsSetListener, TableModelListener, SelectedNodesAndEdgesListener {
    private CyTable enrichmentTable;
    EnrichmentTableModel tableModel;
    Map<String, JTable> enrichmentTables;
    // TODO: Advanced settings panel option
    JPanel topPanel;
    JPanel mainPanel;
    JScrollPane scrollPane;
    List<String> availableTables;
    final CyColorPaletteChooserFactory colorChooserFactory;
    public final static String showTable = TermSource.ALL.getTable();
    JLabel labelRows;
    JButton butSettings;
    JButton butDrawCharts;
    JButton butResetTable;
    JButton butAnalyzedNodes;
    JButton butExportTable;
    JButton butFilter;
    JButton butRunProfiler;
    JButton butEnrichmentMap;
    JMenuItem menuItemReset;
    JPopupMenu popupMenu;
    CyTable filteredEnrichmentTable = null;
    boolean clearSelection = false;
    /**
     * TODO: Quick settings options -> Drop down to select column and auto complete species
     */

    private static final Icon chartIcon = new ImageIcon(
            EnrichmentCytoPanel.class.getResource("/images/chart20.png"));
    final Font iconFont;
    final CyServiceRegistrar registrar;
    private static final Icon icon = new TextIcon(ENRICH_LAYERS, getIconFont(20.0f), PROFILER_COLORS, 14, 14);
    final CyApplicationManager applicationManager;

    final String butSettingsName = "Network-specific enrichment panel settings";
    final String butFilterName = "Filter enrichment table";
    final String butDrawChartsName = "Draw charts using default color palette";
    final String butResetTableName = "Reset result table";
    final String butEnrichmentMapName = "Create EnrichmentMap";
    final String butAnalyzedNodesName = "Select all analyzed nodes";
    final String butExportTableDescr = "Export enrichment table";
    final String butRunProfilerName = "Filter enrichment table";
    private boolean noSignificant;
    private JSONObject result;
    CyTableFactory tableFactory;
    CyTableManager tableManager;

    public EnrichmentCytoPanel(CyServiceRegistrar registrar, boolean noSignificant,CyTable enrichmentTable, JSONObject result) {
        this.registrar = registrar;
        this.result = result;
        this.setLayout(new BorderLayout());
        this.colorChooserFactory = registrar.getService(CyColorPaletteChooserFactory.class);
        IconManager iconManager = registrar.getService(IconManager.class);
        this.iconFont = iconManager.getIconFont(22.0f);
        applicationManager = registrar.getService(CyApplicationManager.class);
        enrichmentTables = new HashMap<String, JTable>();
        this.enrichmentTable = enrichmentTable;
        this.noSignificant = noSignificant;
        initPanel(this.noSignificant);
    }
    public void setEnrichmentTable(CyTable enrichmentTable){
        this.enrichmentTable = enrichmentTable;
        initPanel(this.noSignificant);
    }
    @Override
    public String getIdentifier() {
        return "org.nrnb.gsoc.enrichment";
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.SOUTH;
    }

    @Override
    public String getTitle() {
        return "gProfiler Enrichment";
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    // TODO: Rewrite a simpler implementation with less fancy stuff
    /**
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        CyNetwork network = applicationManager.getCurrentNetwork();
        TaskManager<?, ?> tm = registrar.getService(TaskManager.class);
        if (e.getSource().equals(butRunProfiler)) {
            tm.execute(new TaskIterator(new EnrichmentTask(registrar,this)));
        }
        else if (e.getSource().equals(butResetTable)) {
            tm.execute(new TaskIterator(new EnrichmentTask(registrar)));
        } else if (e.getSource().equals(butExportTable)) {
            if (network != null) {
                if (tableModel.getAllRowCount() != tableModel.getRowCount())
                    tm.execute(new TaskIterator(new ExportEnrichmentTableTask(registrar, network, this, ModelUtils.getEnrichmentTable(registrar, network,
                            TermSource.ALL.getTable()), true)));
                else
                    tm.execute(new TaskIterator(new ExportEnrichmentTableTask(registrar, network, this, ModelUtils.getEnrichmentTable(registrar, network,
                            TermSource.ALL.getTable()), false)));
            }
        } else if (e.getSource().equals(menuItemReset)) {
            // System.out.println("reset color now");
            Component c = (Component) e.getSource();
            JPopupMenu popup = (JPopupMenu) c.getParent();
            JTable table = (JTable) popup.getInvoker();
            // System.out.println("action listener: " + table.getSelectedRow() + " : " + table.getSelectedColumn());
            if (table.getSelectedRow() > -1) {
                resetColor(table.getSelectedRow());
            } else if(e.getSource().equals(butExportTable)){
                if (tableModel.getAllRowCount() != tableModel.getRowCount())
                    tm.execute(new TaskIterator(new ExportEnrichmentTableTask(registrar, network, this, ModelUtils.getEnrichmentTable(registrar, network,
                            TermSource.ALL.getTable()), true)));
                else
                    tm.execute(new TaskIterator(new ExportEnrichmentTableTask(registrar, network, this, ModelUtils.getEnrichmentTable(registrar, network,
                            TermSource.ALL.getTable()), false)));
            } else if (e.getSource().equals(butSettings)) {
                tm.execute(new TaskIterator(new EnrichmentAdvancedOptionsTask(registrar)));
            }
        }

    }

    public CyTable getFilteredTable() {
        //Map<EnrichmentTerm, String> selectedTerms = new LinkedHashMap<EnrichmentTerm, String>();
        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null || tableModel == null)
            //return selectedTerms;
            return null;

        if (filteredEnrichmentTable != null) return filteredEnrichmentTable;

        CyTable currTable = ModelUtils.getEnrichmentTable(registrar, network, TermSource.ALL.getTable());

        if (currTable == null || currTable.getRowCount() == 0) {
            return null;
        }



        updateFilteredEnrichmentTable();

        return filteredEnrichmentTable;
    }

    public void initPanel(boolean noSignificant) {
        CyNetwork network = applicationManager.getCurrentNetwork();
        /**
         * Initialise the top panel design
         */
        JPanel buttonsPanelLeft = new JPanel();
        BoxLayout layoutLeft = new BoxLayout(buttonsPanelLeft, BoxLayout.LINE_AXIS);
        buttonsPanelLeft.setLayout(layoutLeft);

        butRunProfiler = new JButton(IconManager.ICON_REFRESH);
        butRunProfiler.setFont(iconFont);
        butRunProfiler.addActionListener(this);
        butRunProfiler.setToolTipText(butRunProfilerName);
        butRunProfiler.setBorderPainted(false);
        butRunProfiler.setContentAreaFilled(false);
        butRunProfiler.setFocusPainted(false);
        butRunProfiler.setBorder(BorderFactory.createEmptyBorder(2,10,2,10));

        butFilter = new JButton(IconManager.ICON_FILTER);
        butFilter.setFont(iconFont);
        butFilter.addActionListener(this);
        butFilter.setToolTipText(butFilterName);
        butFilter.setBorderPainted(false);
        butFilter.setContentAreaFilled(false);
        butFilter.setFocusPainted(false);
        butFilter.setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
        butFilter.setEnabled(false);

        butDrawCharts = new JButton(chartIcon);
        butDrawCharts.addActionListener(this);
        butDrawCharts.setToolTipText(butDrawChartsName);
        butDrawCharts.setBorderPainted(false);
        butDrawCharts.setContentAreaFilled(false);
        butDrawCharts.setFocusPainted(false);
        butDrawCharts.setBorder(BorderFactory.createEmptyBorder(2,4,2,10));
        butDrawCharts.setEnabled(false);

        /**
         * JComboBox for setting the default value of the node to be chosen for performing the query
         */

        butResetTable = new JButton(IconManager.ICON_CIRCLE_O);
        butResetTable.setFont(iconFont);
        butResetTable.addActionListener(this);
        butResetTable.setToolTipText(butResetTableName);
        butResetTable.setBorderPainted(false);
        butResetTable.setContentAreaFilled(false);
        butResetTable.setFocusPainted(false);
        butResetTable.setBorder(BorderFactory.createEmptyBorder(2,4,2,10));
        butResetTable.setEnabled(false);
        // Add enrichment map button here if EnrichmentMap is loaded
        butEnrichmentMap = new JButton(new ImageIcon(getClass().getClassLoader().getResource("/images/em_logo.png")));
        butEnrichmentMap.addActionListener(this);
        butEnrichmentMap.setToolTipText(butEnrichmentMapName);
        butEnrichmentMap.setBorderPainted(false);
        butEnrichmentMap.setContentAreaFilled(false);
        butEnrichmentMap.setFocusPainted(false);
        butEnrichmentMap.setBorder(BorderFactory.createEmptyBorder(2,4,2,20));
        butEnrichmentMap.setEnabled(false);

        buttonsPanelLeft.add(butRunProfiler);
        buttonsPanelLeft.add(butFilter);

        buttonsPanelLeft.add(butDrawCharts);
        buttonsPanelLeft.add(butResetTable);
        buttonsPanelLeft.add(butEnrichmentMap);

        // JPanel buttonsPanelRight = new JPanel(new GridLayout(1, 3));
        JPanel buttonsPanelRight = new JPanel();
        BoxLayout layoutRight = new BoxLayout(buttonsPanelRight, BoxLayout.LINE_AXIS);
        buttonsPanelRight.setLayout(layoutRight);
        butAnalyzedNodes = new JButton(IconManager.ICON_CHECK_SQUARE_O);
        butAnalyzedNodes.addActionListener(this);
        butAnalyzedNodes.setFont(iconFont);
        butAnalyzedNodes.setToolTipText(butAnalyzedNodesName);
        butAnalyzedNodes.setBorderPainted(false);
        butAnalyzedNodes.setContentAreaFilled(false);
        butAnalyzedNodes.setFocusPainted(false);
        butAnalyzedNodes.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 10));
        butAnalyzedNodes.setEnabled(false);

        butExportTable = new JButton(IconManager.ICON_SAVE);
        butExportTable.addActionListener(this);
        butExportTable.setFont(iconFont);
        butExportTable.setToolTipText(butExportTableDescr);
        butExportTable.setBorderPainted(false);
        butExportTable.setContentAreaFilled(false);
        butExportTable.setFocusPainted(false);
        butExportTable.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 10));
        butExportTable.setEnabled(false);

        butSettings = new JButton(IconManager.ICON_COG);
        butSettings.setFont(iconFont);
        butSettings.addActionListener(this);
        butSettings.setToolTipText(butSettingsName);
        butSettings.setBorderPainted(false);
        butSettings.setContentAreaFilled(false);
        butSettings.setFocusPainted(false);
        butSettings.setBorder(BorderFactory.createEmptyBorder(2,4,2,10));

        buttonsPanelRight.add(butAnalyzedNodes);
        buttonsPanelRight.add(butExportTable);
        buttonsPanelRight.add(butSettings);
        topPanel = new JPanel(new BorderLayout());
        topPanel.add(buttonsPanelLeft, BorderLayout.WEST);
        topPanel.add(buttonsPanelRight, BorderLayout.EAST);
        // topPanel.add(boxTables, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);
        if (network == null)
            return;
        initPanel(network, noSignificant);
    }

    public void initPanel(CyNetwork network, boolean noSignificant) {
        this.removeAll();

        availableTables = new ArrayList<String>();
        createJTable(enrichmentTable);
        // Check if values are being received correctly
        List<CyRow> rows = enrichmentTable.getAllRows();
        System.out.println(rows.size());
/*
        for(CyRow row: rows){
            Map<String,Object> mp = row.getAllValues();
            for (Map.Entry<String,Object> entry : mp.entrySet())   {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }

            System.out.println(mp.size());
        }
*/
        availableTables.add(enrichmentTable.getTitle());
        /**
         * Initialise the top panel design
         */
        JPanel buttonsPanelLeft = new JPanel();
        BoxLayout layoutLeft = new BoxLayout(buttonsPanelLeft, BoxLayout.LINE_AXIS);
        buttonsPanelLeft.setLayout(layoutLeft);
        butFilter = new JButton(IconManager.ICON_FILTER);
        butFilter.setFont(iconFont);
        butFilter.addActionListener(this);
        butFilter.setToolTipText(butFilterName);
        butFilter.setBorderPainted(false);
        butFilter.setContentAreaFilled(false);
        butFilter.setFocusPainted(false);
        butFilter.setBorder(BorderFactory.createEmptyBorder(2,10,2,10));
        butFilter.setEnabled(false);

        butDrawCharts = new JButton(chartIcon);
        butDrawCharts.addActionListener(this);
        butDrawCharts.setToolTipText(butDrawChartsName);
        butDrawCharts.setBorderPainted(false);
        butDrawCharts.setContentAreaFilled(false);
        butDrawCharts.setFocusPainted(false);
        butDrawCharts.setBorder(BorderFactory.createEmptyBorder(2,4,2,10));
        butDrawCharts.setEnabled(false);

        /**
         * JComboBox for setting the default value of the node to be chosen for performing the query
         */

        butResetTable = new JButton(IconManager.ICON_CIRCLE_O);
        butResetTable.setFont(iconFont);
        butResetTable.addActionListener(this);
        butResetTable.setToolTipText(butResetTableName);
        butResetTable.setBorderPainted(false);
        butResetTable.setContentAreaFilled(false);
        butResetTable.setFocusPainted(false);
        butResetTable.setBorder(BorderFactory.createEmptyBorder(2,4,2,10));
        butResetTable.setEnabled(false);
        // Add enrichment map button here if EnrichmentMap is loaded
        butEnrichmentMap = new JButton(new ImageIcon(getClass().getClassLoader().getResource("/images/em_logo.png")));
        butEnrichmentMap.addActionListener(this);
        butEnrichmentMap.setToolTipText(butEnrichmentMapName);
        butEnrichmentMap.setBorderPainted(false);
        butEnrichmentMap.setContentAreaFilled(false);
        butEnrichmentMap.setFocusPainted(false);
        butEnrichmentMap.setBorder(BorderFactory.createEmptyBorder(2,4,2,20));
        butEnrichmentMap.setEnabled(false);

        buttonsPanelLeft.add(butRunProfiler);
        buttonsPanelLeft.add(butFilter);
        buttonsPanelLeft.add(butDrawCharts);
        buttonsPanelLeft.add(butResetTable);
        buttonsPanelLeft.add(butEnrichmentMap);

        // JPanel buttonsPanelRight = new JPanel(new GridLayout(1, 3));
        JPanel buttonsPanelRight = new JPanel();
        BoxLayout layoutRight = new BoxLayout(buttonsPanelRight, BoxLayout.LINE_AXIS);
        buttonsPanelRight.setLayout(layoutRight);
        butAnalyzedNodes = new JButton(IconManager.ICON_CHECK_SQUARE_O);
        butAnalyzedNodes.addActionListener(this);
        butAnalyzedNodes.setFont(iconFont);
        butAnalyzedNodes.setToolTipText(butAnalyzedNodesName);
        butAnalyzedNodes.setBorderPainted(false);
        butAnalyzedNodes.setContentAreaFilled(false);
        butAnalyzedNodes.setFocusPainted(false);
        butAnalyzedNodes.setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 10));
        butAnalyzedNodes.setEnabled(false);

        butExportTable = new JButton(IconManager.ICON_SAVE);
        butExportTable.addActionListener(this);
        butExportTable.setFont(iconFont);
        butExportTable.setToolTipText(butExportTableDescr);
        butExportTable.setBorderPainted(false);
        butExportTable.setContentAreaFilled(false);
        butExportTable.setFocusPainted(false);
        butExportTable.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 10));
        butExportTable.setEnabled(false);

        butSettings = new JButton(IconManager.ICON_COG);
        butSettings.setFont(iconFont);
        butSettings.addActionListener(this);
        butSettings.setToolTipText(butSettingsName);
        butSettings.setBorderPainted(false);
        butSettings.setContentAreaFilled(false);
        butSettings.setFocusPainted(false);
        butSettings.setBorder(BorderFactory.createEmptyBorder(2,4,2,10));

        buttonsPanelRight.add(butAnalyzedNodes);
        buttonsPanelRight.add(butExportTable);
        buttonsPanelRight.add(butSettings);
        topPanel = new JPanel(new BorderLayout());
        topPanel.add(buttonsPanelLeft, BorderLayout.WEST);
        topPanel.add(buttonsPanelRight, BorderLayout.EAST);
        // topPanel.add(boxTables, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);
        if (noSignificant) {
            mainPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Enrichment retrieval returned no results that met the criteria.",
                    SwingConstants.CENTER);
            mainPanel.add(label, BorderLayout.CENTER);
            this.add(mainPanel, BorderLayout.CENTER);
        } else if (availableTables.size() == 0) {
            mainPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("No enrichment has been retrieved for this network.",
                    SwingConstants.CENTER);
            mainPanel.add(label, BorderLayout.CENTER);
            this.add(mainPanel, BorderLayout.CENTER);
        } else {

            butFilter.setEnabled(true);
            butDrawCharts.setEnabled(true);
            butExportTable.setEnabled(false);
            butAnalyzedNodes.setEnabled(false);
            butResetTable.setEnabled(false);
            butEnrichmentMap.setEnabled(false);

            JTable currentTable = enrichmentTables.get(enrichmentTable.getTitle());

            if (tableModel != null) {
                updateFilteredEnrichmentTable();
            }
            labelRows = new JLabel("");
            updateLabelRows();
            labelRows.setHorizontalAlignment(JLabel.RIGHT);
            Font labelFont = labelRows.getFont();
            labelRows.setFont(labelFont.deriveFont((float)(labelFont.getSize() * 0.8)));



            mainPanel = new JPanel(new BorderLayout());
            scrollPane = new JScrollPane(currentTable);
            mainPanel.setLayout(new GridLayout(1, 1));
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            this.add(mainPanel, BorderLayout.CENTER);
        }

        this.revalidate();
        this.repaint();
    }

    private void createJTable(CyTable cyTable) {
        tableModel = new EnrichmentTableModel(enrichmentTable, EnrichmentTerm.swingColumnsEnrichment);
        System.out.println(tableModel.getColumnCount());
        JTable jTable = new JTable(tableModel);
        TableColumnModel tcm = jTable.getColumnModel();
        tcm.getColumn(EnrichmentTerm.pvalueColumn).setCellRenderer(new DecimalFormatRenderer());
        jTable.setFillsViewportHeight(true);
        jTable.setAutoCreateRowSorter(true);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable.getSelectionModel().addListSelectionListener(this);
        jTable.getModel().addTableModelListener(this);
        jTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
        CyNetwork network = applicationManager.getCurrentNetwork();
        //jTable.setDefaultEditor(Color.class, new ColorEditor(registrar, this, colorChooserFactory, network));
        popupMenu = new JPopupMenu();
        menuItemReset = new JMenuItem("Remove color");
        menuItemReset.addActionListener(this);
        popupMenu.add(menuItemReset);
        jTable.setComponentPopupMenu(popupMenu);
        jTable.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());
                    if (!source.isRowSelected(row)) {
                        source.changeSelection(row, column, false, false);
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());
                    if (!source.isRowSelected(row)) {
                        source.changeSelection(row, column, false, false);
                    }
                }
            }
        });
        enrichmentTables.put(enrichmentTable.getTitle(), jTable);

    }
    static class DecimalFormatRenderer extends DefaultTableCellRenderer {
        private static final DecimalFormat formatter = new DecimalFormat("0.#####E0");

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            try {
                if (value != null && (double) value < 0.001) {
                    value = formatter.format((Number) value);
                }
            } catch (Exception ex) {
                // ignore and return original value
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
        }
    }

    public void updateFilteredEnrichmentTable() {
        if (filteredEnrichmentTable == null)
            getFilteredTable();

        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null || tableModel == null)
            return;

        CyTable currTable = ModelUtils.getEnrichmentTable(registrar, network, TermSource.ALL.getTable());
        if (currTable == null) return;

        filteredEnrichmentTable.deleteRows(filteredEnrichmentTable.getPrimaryKey().getValues(Long.class));

        Long[] rowNames = tableModel.getRowNames();
        for (int i = 0; i < rowNames.length; i++) {
            CyRow row = currTable.getRow(rowNames[i]);
            CyRow filtRow = filteredEnrichmentTable.getRow(rowNames[i]);
            filtRow.set(EnrichmentTerm.colName, row.get(EnrichmentTerm.colName, String.class));
            filtRow.set(EnrichmentTerm.colDescription, row.get(EnrichmentTerm.colDescription, String.class));
            filtRow.set(EnrichmentTerm.colPvalue, row.get(EnrichmentTerm.colPvalue, Double.class));
            filtRow.set(EnrichmentTerm.colGenes, row.getList(EnrichmentTerm.colGenes, String.class));
            filtRow.set(EnrichmentTerm.colGenesSUID, row.getList(EnrichmentTerm.colGenesSUID, Long.class));
            filtRow.set(EnrichmentTerm.colNetworkSUID, row.get(EnrichmentTerm.colNetworkSUID, Long.class));
            filtRow.set(EnrichmentTerm.colChartColor, "");
        }
    }

    public void resetColor(int currentRow) {
        JTable currentTable = enrichmentTables.get(showTable);
        // currentRow = currentTable.getSelectedRow();
        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null || tableModel == null)
            return;
        CyTable enrichmentTable = ModelUtils.getEnrichmentTable(registrar, network,
                TermSource.ALL.getTable());
        Color color = (Color)currentTable.getModel().getValueAt(
                currentTable.convertRowIndexToModel(currentRow),
                EnrichmentTerm.chartColumnCol);
        String termName = (String)currentTable.getModel().getValueAt(
                currentTable.convertRowIndexToModel(currentRow),
                EnrichmentTerm.nameColumn);
        if (color == null || termName == null)
            return;

        for (CyRow row : enrichmentTable.getAllRows()) {
            if (enrichmentTable.getColumn(EnrichmentTerm.colName) != null
                    && row.get(EnrichmentTerm.colName, String.class) != null
                    && row.get(EnrichmentTerm.colName, String.class).equals(termName)) {
                row.set(EnrichmentTerm.colChartColor, "");
            }
        }
        tableModel.fireTableDataChanged();

    }

    private Map<EnrichmentTerm, String> getUserSelectedTerms() {
        Map<EnrichmentTerm, String> selectedTerms = new LinkedHashMap<EnrichmentTerm, String>();
        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null)
            return selectedTerms;

        CyTable currTable = ModelUtils.getEnrichmentTable(registrar, network,
                TermSource.ALL.getTable());
        if (currTable == null || currTable.getRowCount() == 0) {
            return selectedTerms;
        }
        for (CyRow row : currTable.getAllRows()) {
            if (currTable.getColumn(EnrichmentTerm.colChartColor) != null
                    && row.get(EnrichmentTerm.colChartColor, String.class) != null
                    && !row.get(EnrichmentTerm.colChartColor, String.class).equals("")
                    && !row.get(EnrichmentTerm.colChartColor, String.class).equals("#ffffff")) {
                String selTerm = row.get(EnrichmentTerm.colName, String.class);
                if (selTerm != null) {
                    EnrichmentTerm enrTerm = new EnrichmentTerm(selTerm,
                            row.get(EnrichmentTerm.colDescription, String.class),
                            row.get(EnrichmentTerm.colSource, String.class),
                            row.get(EnrichmentTerm.colPvalue, Double.class),
                            row.get(EnrichmentTerm.colGoshv, Double.class),
                            row.get(EnrichmentTerm.colIsSignificant, Boolean.class),
                            row.get(EnrichmentTerm.colEffectiveDomainSize, Integer.class),
                            row.get(EnrichmentTerm.colIntersectionSize, Integer.class),
                            row.get(EnrichmentTerm.colTermSize, Integer.class),
                            row.get(EnrichmentTerm.colPrecision, Double.class),
                            row.get(EnrichmentTerm.colRecall, Double.class));
                    selectedTerms.put(enrTerm, row.get(EnrichmentTerm.colChartColor, String.class));
                }
            }
        }
        return selectedTerms;
    }
    public void resetCharts() {
        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null || tableModel == null)
            return;

        CyTable nodeTable = network.getDefaultNodeTable();
        // replace columns
        ModelUtils.replaceListColumnIfNeeded(nodeTable, String.class,
                EnrichmentTerm.colEnrichmentTermsNames);
        ModelUtils.replaceListColumnIfNeeded(nodeTable, Integer.class,
                EnrichmentTerm.colEnrichmentTermsIntegers);
        ModelUtils.replaceColumnIfNeeded(nodeTable, String.class,
                EnrichmentTerm.colEnrichmentPassthrough);

        // remove colors from table?
        CyTable currTable = ModelUtils.getEnrichmentTable(registrar, network,
                TermSource.ALL.getTable());
        if (currTable == null || currTable.getRowCount() == 0) {
            return;
        }
        for (CyRow row : currTable.getAllRows()) {
            if (currTable.getColumn(EnrichmentTerm.colChartColor) != null
                    && row.get(EnrichmentTerm.colChartColor, String.class) != null
                    && !row.get(EnrichmentTerm.colChartColor, String.class).equals("")) {
                row.set(EnrichmentTerm.colChartColor, "");
            }
        }
        tableModel.fireTableDataChanged();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;

        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null)
            return;
        // TODO: clear table selection when switching
        JTable table = enrichmentTables.get(showTable);
        if (table.getSelectedColumnCount() == 1 && table.getSelectedRow() > -1) {
            if (table.getSelectedColumn() != EnrichmentTerm.chartColumnCol) {
                for (int row: table.getSelectedRows()) {
                    Object cellContent =
                            table.getModel().getValueAt(table.convertRowIndexToModel(row),
                                    EnrichmentTerm.nodeSUIDColumn);
                    if (cellContent instanceof java.util.List) {
                        java.util.List<Long> nodeIDs = (List<Long>) cellContent;
                        for (Long nodeID : nodeIDs) {
                            network.getDefaultNodeTable().getRow(nodeID).set(CyNetwork.SELECTED, true);
                        }
                    }
                }
            }
        }
    }

    public void updateLabelRows() {
        if (tableModel == null)
            return;
        String labelTxt = "";
        if (tableModel.getAllRowCount() != tableModel.getRowCount()) {
            labelTxt = tableModel.getRowCount() + " rows ("+tableModel.getAllRowCount()+" before filtering)";
        } else {
            labelTxt = tableModel.getAllRowCount() + " rows";
        }
        if (labelRows != null)
            labelRows.setText(labelTxt);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int column = e.getColumn();
        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null)
            return;

        updateLabelRows();
        JTable currentTable = enrichmentTables.get(showTable);
        currentTable.tableChanged(e);
    }


    @Override
    public void handleEvent(RowsSetEvent rse) {
        CyNetworkManager networkManager = registrar.getService(CyNetworkManager.class);
        CyNetwork selectedNetwork = null;
        if (rse.containsColumn(CyNetwork.SELECTED)) {
            Collection<RowSetRecord> columnRecords = rse.getColumnRecords(CyNetwork.SELECTED);
            for (RowSetRecord rec : columnRecords) {
                CyRow row = rec.getRow();
                if (row.toString().indexOf("FACADE") >= 0)
                    continue;
                Long networkID = row.get(CyNetwork.SUID, Long.class);
                Boolean selectedValue = (Boolean) rec.getValue();
                if (selectedValue && networkManager.networkExists(networkID)) {
                    selectedNetwork = networkManager.getNetwork(networkID);
                }
            }
        }
        if (selectedNetwork != null) {
            initPanel(selectedNetwork, false);
            return;
        }
    }

    @Override
    public void handleEvent(SelectedNodesAndEdgesEvent event) {
        JTable table = enrichmentTables.get(showTable);
        if (table.getSelectedRow() > -1 &&
                table.getSelectedColumnCount() == 1 &&
                table.getSelectedColumn() != EnrichmentTerm.chartColumnCol)
            return;

        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null)
            return;
        List<Long> nodesToFilterSUID = new ArrayList<Long>();
        for (final CyNode node : event.getSelectedNodes()) {
            nodesToFilterSUID.add(node.getSUID());
        }
        updateLabelRows();
    }
}
