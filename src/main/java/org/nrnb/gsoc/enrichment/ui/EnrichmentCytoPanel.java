package org.nrnb.gsoc.enrichment.ui;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.command.AvailableCommands;
import org.cytoscape.model.*;
import org.cytoscape.model.events.*;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.CyColorPaletteChooserFactory;
import org.cytoscape.util.swing.IconManager;
import org.cytoscape.util.swing.TextIcon;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;
import org.nrnb.gsoc.enrichment.tasks.ExportEnrichmentTableTask;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static org.nrnb.gsoc.enrichment.utils.IconUtils.*;

public class EnrichmentCytoPanel extends JPanel
        implements CytoPanelComponent2, ListSelectionListener, ActionListener, RowsSetListener, TableModelListener, SelectedNodesAndEdgesListener {
    EnrichmentTableModel tableModel;
    final CyColorPaletteChooserFactory colorChooserFactory;
    public final static String showTable = TermSource.ALL.getTable();
    JLabel labelRows;
    JButton butDrawCharts;
    JButton butResetCharts;
    JButton butAnalyzedNodes;
    JButton butExportTable;
    JButton butFilter;
    JButton butEnrichmentMap;
    JMenuItem menuItemReset;
    JPopupMenu popupMenu;
    CyTable filteredEnrichmentTable = null;
    boolean clearSelection = false;

    private static final Icon chartIcon = new ImageIcon(
            EnrichmentCytoPanel.class.getResource("/images/chart20.png"));
    final Font iconFont;
    final CyServiceRegistrar registrar;
    private static final Icon icon = new TextIcon(ENRICH_LAYERS, getIconFont(20.0f), STRING_COLORS, 14, 14);
    Map<String, JTable> enrichmentTables;
    final CyApplicationManager applicationManager;

    public EnrichmentCytoPanel(CyServiceRegistrar registrar) {
        this.registrar = registrar;
        this.setLayout(new BorderLayout());
        this.colorChooserFactory = registrar.getService(CyColorPaletteChooserFactory.class);
        IconManager iconManager = registrar.getService(IconManager.class);
        this.iconFont = iconManager.getIconFont(22.0f);
        applicationManager = registrar.getService(CyApplicationManager.class);
    }
    // TODO: Rewrite a simpler implementation with less fancy stuff

    /**
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        CyNetwork network = applicationManager.getCurrentNetwork();
        TaskManager<?, ?> tm = registrar.getService(TaskManager.class);

        if (e.getSource().equals(butExportTable)) {
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

        CyTableFactory tableFactory = registrar.getService(CyTableFactory.class);
        CyTableManager tableManager = registrar.getService(CyTableManager.class);
        filteredEnrichmentTable = tableFactory.createTable(TermSource.ALLFILTERED.getTable(),
                EnrichmentTerm.colTermID, Long.class, false, true);
        filteredEnrichmentTable.setTitle("STRING Enrichment: filtered");
        filteredEnrichmentTable.setSavePolicy(SavePolicy.DO_NOT_SAVE);
        tableManager.addTable(filteredEnrichmentTable);
        ModelUtils.setupEnrichmentTable(filteredEnrichmentTable);

        updateFilteredEnrichmentTable();

        return filteredEnrichmentTable;
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

        //currentTable.getModel().setValueAt(Color.OPAQUE, currentTable.convertRowIndexToModel(currentRow),
        //		EnrichmentTerm.chartColumnCol);
        for (CyRow row : enrichmentTable.getAllRows()) {
            if (enrichmentTable.getColumn(EnrichmentTerm.colName) != null
                    && row.get(EnrichmentTerm.colName, String.class) != null
                    && row.get(EnrichmentTerm.colName, String.class).equals(termName)) {
                row.set(EnrichmentTerm.colChartColor, "");
            }
        }
        tableModel.fireTableDataChanged();

        // re-draw charts if the user changed the color
        Map<EnrichmentTerm, String> preselectedTerms = getUserSelectedTerms();
/*
        if (preselectedTerms.size() > 0)
            ViewUtils.drawCharts(manager, preselectedTerms, manager.getChartType(network));
*/
    }
    private Map<EnrichmentTerm, String> getUserSelectedTerms() {
        Map<EnrichmentTerm, String> selectedTerms = new LinkedHashMap<EnrichmentTerm, String>();
        CyNetwork network = applicationManager.getCurrentNetwork();
        if (network == null)
            return selectedTerms;

        // Set<CyTable> currTables = ModelUtils.getEnrichmentTables(manager, network);
        // for (CyTable currTable : currTables) {
        CyTable currTable = ModelUtils.getEnrichmentTable(registrar, network,
                TermSource.ALL.getTable());
        // currTable.getColumn(EnrichmentTerm.colShowChart) == null ||
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
                    enrTerm.setNodesSUID(row.getList(EnrichmentTerm.colGenesSUID, Long.class));
                    selectedTerms.put(enrTerm, row.get(EnrichmentTerm.colChartColor, String.class));
                }
            }
        }
        // System.out.println(selectedTerms);
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
        // initPanel();
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
            // System.out.println("filtered:" + labelTxt);
        } else {
            labelTxt = tableModel.getAllRowCount() + " rows";
            // System.out.println("total rows: " + labelTxt);
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
//            initPanel(selectedNetwork, false);
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
