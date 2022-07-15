package org.nrnb.gsoc.enrichment.ui;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
        if (getRowCount() == 0) return null;
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
            return cyTable.getRow(rowName).get(colName, Double.class);
        }
        else if(colName.equals(EnrichmentTerm.colPrecision)){
            return cyTable.getRow(rowName).get(colName, Double.class);
        }
        else if (colName.equals(EnrichmentTerm.colIsSignificant)) {
            return cyTable.getRow(rowName).getList(colName, Boolean.class);
        }
        else if (colName.equals(EnrichmentTerm.colGenesSUID)) {
            return cyTable.getRow(rowName).getList(colName, Long.class);
        }
        else if (colName.equals(EnrichmentTerm.colGenesEvidenceCode)) {
            return cyTable.getRow(rowName).getList(colName, String.class);
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
        } else {
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
        else if (colName.equals(EnrichmentTerm.colPvalue)) {
            return Double.class;
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
//        filter(sources);
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
    public void filter(List<TermSource> sources, List<String> evidenceList, boolean removeOverlapping,
                       double cutoff) {
        filterBySource(sources);
        int length = filterByEvidenceCode(evidenceList);
        if (removeOverlapping && length > 0) {
            rowNames = removeRedundancy(length, cutoff);
        }
        fireTableDataChanged();
    }

    private void filterBySource(List<TermSource> sources) {
        List<CyRow> rows = cyTable.getAllRows();
        Long[] rowArray = new Long[rows.size()];

        int rowCount = 0;
        for (CyRow row : rows) {
            // implement this again
            String termSource = row.get(EnrichmentTerm.colSource, String.class);
            if (sources.isEmpty() || inSource(sources, termSource)) {
                rowArray[rowCount] = row.get(EnrichmentTerm.colID, Long.class);
                rowCount++;
            }
        }
        rowNames = Arrays.copyOf(rowArray, rowCount);
    }

    public int filterByEvidenceCode(List<String> evidenceCodes) {
        List<CyRow> rows = cyTable.getAllRows();
        HashSet<Long> shownRows = new HashSet<>(Arrays.asList(rowNames));
        Long[] rowArray = new Long[rows.size()];
        int rowCount = 0;
        for (CyRow row : rows) {
            Long rowID = row.get(EnrichmentTerm.colID, Long.class);
            if (!shownRows.contains(rowID)) {
                continue;
            }
            List<String> termSource = row.get(EnrichmentTerm.colGenesEvidenceCode, List.class);
            if (evidenceCodes.isEmpty() ||
                    evidenceCodes.stream().allMatch(e -> termSource.contains("\"" + e + "\""))) {
                rowArray[rowCount++] = row.get(EnrichmentTerm.colID, Long.class);
            }
        }
        rowNames = Arrays.copyOf(rowArray, rowCount);
        fireTableDataChanged();
        return rowCount;
    }

    private boolean inSource(List<TermSource> sources, String termName) {
        for (TermSource ts: sources) {
            if (ts.getName().equals(termName))
                return true;
        }
        return false;
    }

    private Long[] removeRedundancy(int length, double cutoff) {
        // Sort by pValue
        Long[] sortedArray = pValueSort(rowNames, length);

        // Initialize with the most significant term
        List<Long> currentTerms = new ArrayList<Long>();
        currentTerms.add(sortedArray[0]);
        for (int i = 1; i < length; i++) {
            if (jaccard(currentTerms, sortedArray[i]) < cutoff)
                currentTerms.add(sortedArray[i]);
        }
        return(currentTerms.toArray(new Long[1]));
    }

    private Long[] pValueSort(Long[] rowArray, int length) {
        // @TODO: Sort by pValue if not (ideally should be already sorted in table)
        return Arrays.copyOf(rowArray, length);
    }

    // Two versions of jaccard similarity calculation.  This one
    // looks at the maximum jaccard between the currently selected
    // terms and the new term.
    private double jaccard(List<Long> currentTerms, Long term) {
        double maxJaccard = 0;
        for (Long currentTerm: currentTerms)
            maxJaccard = Math.max(maxJaccard, jaccard(currentTerm, term));
        return maxJaccard;
    }

    // This version of the jaccard calculation returns the jaccard between
    // all currently selected nodes and the nodes of the new term.
    private double jaccard2(List<Long> currentTerms, Long term) {
        Set<Long> currentNodes = new HashSet<Long>();
        for (Long currentTerm: currentTerms) {
            List<Long> nodes = cyTable.getRow(currentTerm).getList(EnrichmentTerm.colGenesSUID, Long.class);
            currentNodes.addAll(nodes);
        }
        List<Long> newNodes = cyTable.getRow(term).getList(EnrichmentTerm.colGenesSUID, Long.class);
        return jaccard2(currentNodes, newNodes);
    }

    private double jaccard2(Set<Long> currentNodes, List<Long> newNodes) {
        int intersection = 0;
        for (Long cn: newNodes) {
            if (currentNodes.contains(cn))
                intersection++;
        }
        double j = ((double)intersection) / (double)(currentNodes.size()+newNodes.size()-intersection);
        return j;
    }

    private double jaccard(Long currentTerm, Long term) {
        List<Long> currentNodes = cyTable.getRow(currentTerm).getList(EnrichmentTerm.colGenesSUID, Long.class);
        List<Long> newNodes = cyTable.getRow(term).getList(EnrichmentTerm.colGenesSUID, Long.class);
        if (currentNodes == null || newNodes == null)
            return 0;

        int intersection = 0;
        for (Long cn: currentNodes) {
            if (newNodes.contains(cn))
                intersection++;
        }
        double j = ((double)intersection) / (double)(currentNodes.size()+newNodes.size()-intersection);
        return j;
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
