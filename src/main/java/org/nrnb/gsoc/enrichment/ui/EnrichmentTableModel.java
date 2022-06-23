package org.nrnb.gsoc.enrichment.ui;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.util.List;

/**
 * @author ighosh98
 * @description This class defines how the data fetched from the API request will be stored and displayed to the customer
 */
public class EnrichmentTableModel extends AbstractTableModel {
    private String[] columnNames;
    private CyTable cyTable;
    private Long[] rowNames;

    public EnrichmentTableModel(CyTable cyTable, String[] columnNames) {
        this.columnNames = columnNames;
        this.cyTable = cyTable;
        initData();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return rowNames.length;
    }

    public int getAllRowCount() {
        return cyTable.getRowCount();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Long[] getRowNames() {
        return rowNames;
    }

    /**
     * @description Fetch the value with a particular column name as an enum for a specific row
     * @param row
     * @param col
     * @return return value at the particular row and column of the enrichment table
     */
    public Object getValueAt(int row, int col) {
        final String colName = columnNames[col];
        final Long rowName = rowNames[row];
        if (colName.equals(EnrichmentTerm.colEffectiveDomainSize)) {
            return cyTable.getRow(rowName).get(colName, Integer.class);
        } else if (colName.equals(EnrichmentTerm.colIntersectionSize)) {
            return cyTable.getRow(rowName).get(colName, Integer.class);
        } else if (colName.equals(EnrichmentTerm.colQuerySize)) {
            return cyTable.getRow(rowName).get(colName, Integer.class);
        }  else if (colName.equals(EnrichmentTerm.colRecall)) {
            return cyTable.getRow(rowName).get(colName, Double.class);
        } else if (colName.equals(EnrichmentTerm.colGoshv)) {
            return cyTable.getRow(rowName).get(colName, Double.class);
        } else if (colName.equals(EnrichmentTerm.colTermID)) {
            return cyTable.getRow(rowName).get(colName, String.class);
        } else if (colName.equals(EnrichmentTerm.colGroupID)) {
            return cyTable.getRow(rowName).get(colName, Long.class);
        } else if (colName.equals(EnrichmentTerm.colTermSize)) {
            return cyTable.getRow(rowName).get(colName, Integer.class);
        } else if (colName.equals(EnrichmentTerm.colGenes)) {
            return cyTable.getRow(rowName).getList(colName, String.class);
        } else if (colName.equals(EnrichmentTerm.colSource)) {
            return cyTable.getRow(rowName).get(colName, String.class);
        } else if (colName.equals(EnrichmentTerm.colID)) {
            return cyTable.getRow(rowName).get(colName, Long.class);
        } else if (colName.equals(EnrichmentTerm.colPvalue)) {
            return cyTable.getRow(rowName).get(colName, String.class);
        }
        else if(colName.equals(EnrichmentTerm.colPrecision)){
            return cyTable.getRow(rowName).get(colName, Double.class);
        }
        else if (colName.equals(EnrichmentTerm.colIsSignificant)) {
            return cyTable.getRow(rowName).getList(colName, Boolean.class);
        }
        else if (colName.equals(EnrichmentTerm.colGenesSUID)) {
       return cyTable.getRow(rowName).getList(colName, Long.class);
     } else {
            return cyTable.getRow(rowName).get(colName, String.class);
        }
    }

    /**
     * @description Fetch the value with a particular column name as a string for a specific row
     * @param row
     * @param colName
     * @return
     */
    public Object getValueAt(int row, String colName) {
        final Long rowName = rowNames[row];

        if (colName.equals(EnrichmentTerm.colEffectiveDomainSize)) {
            return cyTable.getRow(rowName).get(colName, Integer.class);
        } else if (colName.equals(EnrichmentTerm.colIntersectionSize)) {
            return cyTable.getRow(rowName).get(colName, Integer.class);
        } else if (colName.equals(EnrichmentTerm.colQuerySize)) {
            return cyTable.getRow(rowName).get(colName, Integer.class);
        } else if (colName.equals(EnrichmentTerm.colSource)) {
            return cyTable.getRow(rowName).get(colName, String.class);
        }  else if (colName.equals(EnrichmentTerm.colRecall)) {
            return cyTable.getRow(rowName).get(colName, Double.class);
        } else if (colName.equals(EnrichmentTerm.colGoshv)) {
            return cyTable.getRow(rowName).get(colName, Double.class);
        } else if(colName.equals(EnrichmentTerm.colID)) {
            return cyTable.getRow(rowName).get(colName, Long.class);
        } else if (colName.equals(EnrichmentTerm.colTermID)) {
            return cyTable.getRow(rowName).get(colName, String.class);
        } else if (colName.equals(EnrichmentTerm.colGroupID)) {
            return cyTable.getRow(rowName).get(colName, String.class);
        } else if (colName.equals(EnrichmentTerm.colTermSize)) {
            return cyTable.getRow(rowName).get(colName, Integer.class);
        } else if (colName.equals(EnrichmentTerm.colGenes)) {
            return cyTable.getRow(rowName).getList(colName, String.class);
        } else if (colName.equals(EnrichmentTerm.colIsSignificant)) {
            return cyTable.getRow(rowName).getList(colName, Boolean.class);
        } else if(colName.equals(EnrichmentTerm.colIntersectionSize)){
            return cyTable.getRow(rowName).get(colName, Integer.class);
        }
        else if(colName.equals(EnrichmentTerm.colTermSize)){
            return cyTable.getRow(rowName).get(colName, Integer.class);

        }
        else if(colName.equals(EnrichmentTerm.colPrecision)){
            return cyTable.getRow(rowName).get(colName, Double.class);
         }
         else if (colName.equals(EnrichmentTerm.colGenesSUID)) {
   			return cyTable.getRow(rowName).getList(colName, Long.class);
   		}
        else {
            return cyTable.getRow(rowName).get(colName, String.class);
        }
    }

    public Class<?> getColumnClass(int c) {
        final String colName = columnNames[c];

        if (colName.equals(EnrichmentTerm.colEffectiveDomainSize)) {
            return Integer.class;
        } else if (colName.equals(EnrichmentTerm.colIntersectionSize)) {
            return Integer.class;
        } else if (colName.equals(EnrichmentTerm.colQuerySize)) {
            return Integer.class;
        } else if (colName.equals(EnrichmentTerm.colSource)) {
            return String.class;
        }  else if (colName.equals(EnrichmentTerm.colRecall)) {
            return Double.class;
        } else if (colName.equals(EnrichmentTerm.colGoshv)) {
            return Double.class;
        } else if (colName.equals(EnrichmentTerm.colID)) {
            return Long.class;
        } else if (colName.equals(EnrichmentTerm.colTermID)) {
            return String.class;
        } else if (colName.equals(EnrichmentTerm.colGroupID)) {
            return String.class;
        } else if (colName.equals(EnrichmentTerm.colTermSize)) {
            return Integer.class;
        } else if (colName.equals(EnrichmentTerm.colGenes)) {
            return String.class;
        } else if (colName.equals(EnrichmentTerm.colIsSignificant)) {
            return Boolean.class;
        } else if(colName.equals(EnrichmentTerm.colIntersectionSize)){
            return (Integer.class);
        }
        else if(colName.equals(EnrichmentTerm.colTermSize)){
            return Integer.class;
        }
        else if(colName.equals(EnrichmentTerm.colPrecision)){
            return Double.class;
        }
        else if (colName.equals(EnrichmentTerm.colGenesSUID)) {
		        return List.class;
		}
        else {
            return String.class;
        }
    }

    public boolean isCellEditable(int row, int col) {
            return false;
    }

    // filter by source and nodeSUID
    public void filterByNodeSUID(List<Long> nodesToFilterSUID, boolean annotateAllNodes,
                                 List<TermSource> sources) {
        filter(sources);
        filterByNodeSUID(nodesToFilterSUID, annotateAllNodes);
    }

    // Filter the table by node SUID
    public void filterByNodeSUID(List<Long> nodesToFilterSUID, boolean annotateAllNodes) {
        List<CyRow> rows = cyTable.getAllRows();
        List<Long> shownRows = Arrays.asList(rowNames);
        Long[] rowArray = new Long[rows.size()];
        int i = 0;
        for (CyRow row : rows) {
            Long rowID = row.get(EnrichmentTerm.colID, Long.class);
            if (!shownRows.contains(rowID)) {
                continue;
            }
            List<Long> genesSUID = new ArrayList<Long>(row.getList(EnrichmentTerm.colGenesSUID, Long.class));
            genesSUID.retainAll(nodesToFilterSUID);
            if ((genesSUID.size() > 0 && !annotateAllNodes) ||
                    (annotateAllNodes && genesSUID.size() == nodesToFilterSUID.size())) {
                rowArray[i] = rowID;
                i++;
            }
        }
        rowNames = Arrays.copyOf(rowArray, i);
        fireTableDataChanged();
    }

    // Filter the table by souce
    public void filter(List<TermSource> sources) {
        List<CyRow> rows = cyTable.getAllRows();
        Long[] rowArray = new Long[rows.size()];
        int i = 0;
        for (CyRow row : rows) {
            // implement this again
            String termSource = row.get(EnrichmentTerm.colSource, String.class);
            if (sources.size() == 0 || inSource(sources, termSource)) {
                rowArray[i] = row.get(EnrichmentTerm.colID, Long.class);
                i++;
            }
        }
        rowNames = Arrays.copyOf(rowArray, i);
        fireTableDataChanged();
    }

    public void filterByEvidenceCode(Set<String> evidenceCodes) {
        List<CyRow> rows = cyTable.getAllRows();
        Long[] rowArray = new Long[rows.size()];
        int i = 0;
        for (CyRow row : rows) {
            // implement this again
            String termSource = row.get(EnrichmentTerm.colTermID, String.class);
            if (evidenceCodes.size() == 0 || evidenceCodes.contains(termSource)) {
                rowArray[i] = row.get(EnrichmentTerm.colTermID, Long.class);
                i++;
            }
        }
        rowNames = Arrays.copyOf(rowArray, i);
        fireTableDataChanged();
    }

    private boolean inSource(List<TermSource> sources, String termName) {
        for (TermSource ts: sources) {
            if (ts.getName().equals(termName))
                return true;
        }
        return false;
    }


    /**
     * @description Initialize the data model
     */
    private void initData() {
        List<CyRow> rows = cyTable.getAllRows();
        rowNames = new Long[rows.size()];
        int i = 0;
        for (CyRow row : rows) {
            rowNames[i] = row.get(EnrichmentTerm.colID, Long.class);
            i++;
        }
    }
}
