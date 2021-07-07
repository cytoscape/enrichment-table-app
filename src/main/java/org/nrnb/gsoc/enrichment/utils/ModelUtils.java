package org.nrnb.gsoc.enrichment.utils;

import org.cytoscape.model.*;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.property.AbstractConfigDirPropsReader;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;

import java.util.*;
import java.util.regex.Pattern;

public class ModelUtils {

    // Namespaces
    public static String PROFILERDB_NAMESPACE = "stringdb";
    public static String NAMESPACE_SEPARATOR = "::";

    // Node information
    public static String CANONICAL = PROFILERDB_NAMESPACE + NAMESPACE_SEPARATOR + "canonical name";
    public static String DISPLAY = "display name";
    public static String FULLNAME = PROFILERDB_NAMESPACE + NAMESPACE_SEPARATOR + "full name";
    public static String ID = "@id";
    public static String DESCRIPTION = PROFILERDB_NAMESPACE + NAMESPACE_SEPARATOR + "description";
    public static String NAMESPACE = PROFILERDB_NAMESPACE + NAMESPACE_SEPARATOR + "namespace";
    public static String QUERYTERM = "query term";
    public static String SEQUENCE = PROFILERDB_NAMESPACE + NAMESPACE_SEPARATOR + "sequence";
    public static String PROFILERID = PROFILERDB_NAMESPACE + NAMESPACE_SEPARATOR + "database identifier";
    public static String STYLE = PROFILERDB_NAMESPACE + NAMESPACE_SEPARATOR + "PROFILER style";
    public static String TYPE = PROFILERDB_NAMESPACE + NAMESPACE_SEPARATOR + "node type";

    public static String TISSUE_NAMESPACE = "tissue";
    public static String COMPARTMENT_NAMESPACE = "compartment";
    // public static String TM_LINKOUT = "TextMining Linkout";
    public static List<String> ignoreKeys = new ArrayList<String>(Arrays.asList("image", "canonical", "@id", "description"));


    public static int MAX_SHORT_NAME_LENGTH = 15; // 15 characters, or 14 characters plus the dot
    public static int SECOND_SEGMENT_LENGTH = 3;
    public static int FIRST_SEGMENT_LENGTH = MAX_SHORT_NAME_LENGTH - SECOND_SEGMENT_LENGTH - 2;


    // Network information
    public static String CONFIDENCE = "confidence score";
    public static String NET_ENRICHMENT_SETTINGS = "enrichmentSettings";


    // Create network view size threshold
    // See https://github.com/cytoscape/cytoscape-impl/blob/develop/core-task-impl/
    // src/main/java/org/cytoscape/task/internal/loadnetwork/AbstractLoadNetworkTask.java
    public static int DEF_VIEW_THRESHOLD = 3000;
    public static String VIEW_THRESHOLD = "viewThreshold";

    public static String EMPTYLINE = "--------";

    public static String REQUERY_TITLE = "Re-query network?";

    public static boolean haveQueryTerms(CyNetwork network) {
        if (network == null) return false;
        for (CyNode node: network.getNodeList()) {
            if (network.getRow(node).get(QUERYTERM, String.class) != null)
                return true;
        }
        return false;
    }

    public static void selectQueryTerms(CyNetwork network) {
        for (CyNode node: network.getNodeList()) {
            if (network.getRow(node).get(QUERYTERM, String.class) != null)
                network.getRow(node).set(CyNetwork.SELECTED, true);
            else
                network.getRow(node).set(CyNetwork.SELECTED, false);
        }
    }

    public static String getExisting(CyNetwork network) {
        StringBuilder str = new StringBuilder();
        for (CyNode node : network.getNodeList()) {
            String profilerID = network.getRow(node).get(PROFILERID, String.class);
            if (profilerID != null && profilerID.length() > 0)
                str.append(profilerID + "\n");
        }
        return str.toString();
    }

    public static String getSelected(CyNetwork network, View<CyNode> nodeView) {
        StringBuilder selectedStr = new StringBuilder();
        if (nodeView != null) {
            String profilerID = network.getRow(nodeView.getModel()).get(PROFILERID, String.class);
            selectedStr.append(profilerID + "\n");
        }

        for (CyNode node : network.getNodeList()) {
            if (network.getRow(node).get(CyNetwork.SELECTED, Boolean.class)) {
                String profilerID = network.getRow(node).get(PROFILERID, String.class);
                if (profilerID != null && profilerID.length() > 0)
                    selectedStr.append(profilerID + "\n");
            }
        }
        return selectedStr.toString();
    }

    public static CyTable getEnrichmentTable(CyServiceRegistrar registrar, CyNetwork network, String name) {
        CyTableManager tableManager = registrar.getService(CyTableManager.class);
        Set<CyTable> currTables = tableManager.getAllTables(true);
        for (CyTable current : currTables) {
            if (name.equals(current.getTitle())
                    && current.getColumn(EnrichmentTerm.colNetworkSUID) != null
                    && current.getAllRows().size() > 0) {
                CyRow tempRow = current.getAllRows().get(0);
                if (tempRow.get(EnrichmentTerm.colNetworkSUID, Long.class) != null && tempRow
                        .get(EnrichmentTerm.colNetworkSUID, Long.class).equals(network.getSUID())) {
                    return current;
                }
            }
        }
        return null;
    }



    public static void setupEnrichmentTable(CyTable enrichmentTable) {
        if (enrichmentTable.getColumn(EnrichmentTerm.colGenesSUID) == null) {
            enrichmentTable.createListColumn(EnrichmentTerm.colGenesSUID, Long.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colNetworkSUID) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colNetworkSUID, Long.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colName) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colName, String.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colPvalue) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colPvalue, Double.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colGenes) == null) {
            enrichmentTable.createListColumn(EnrichmentTerm.colGenes, String.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colChartColor) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colChartColor, String.class, false);
        }
    }

    public static double getMaxFdrLogValue(List<EnrichmentTerm> terms) {
        double maxValue = 0;
        for (EnrichmentTerm term : terms) {
            double termValue = -Math.log10(term.getPValue());
            if (termValue > maxValue)
                maxValue = termValue;
        }
        if (maxValue > 10.0)
            return 10.0;
        return maxValue;
    }


    public static String listToString(List<?> list) {
        String str = "";
        if (list == null || list.size() == 0) return str;
        for (int i = 0; i < list.size()-1; i++) {
            str += list.get(i)+",";
        }
        return str + list.get(list.size()-1).toString();
    }

    public static List<String> stringToList(String string) {
        if (string == null || string.length() == 0) return new ArrayList<String>();
        String [] arr = string.split(",");
        return Arrays.asList(arr);
    }

    public static void updateEnrichmentSettings(CyNetwork network, Map<String, String> settings) {
        String setting = "";
        int index = 0;
        for (String key: settings.keySet()) {
            if (index > 0) {
                setting += ";";
            }
            setting += key+"="+settings.get(key);
            index ++;
        }
        createColumnIfNeeded(network.getDefaultNetworkTable(), String.class, NET_ENRICHMENT_SETTINGS);
        network.getRow(network).set(NET_ENRICHMENT_SETTINGS, setting);
    }
    public static void createColumnIfNeeded(CyTable table, Class<?> clazz, String columnName) {
        if (table.getColumn(columnName) != null)
            return;

        table.createColumn(columnName, clazz, false);
    }


    public static Map<String, String> getEnrichmentSettings(CyNetwork network) {
        Map<String, String> settings = new HashMap<String, String>();
        String setting = network.getRow(network).get(NET_ENRICHMENT_SETTINGS, String.class);
        if (setting == null || setting.length() == 0)
            return settings;

        String[] settingArray = setting.split(";");
        for (String s: settingArray) {
            String[] pair = s.split("=");
            if (pair.length == 2) {
                settings.put(pair[0], pair[1]);
            }
        }
        return settings;
    }
    public static Set<CyTable> getEnrichmentTables(CyServiceRegistrar registrar, CyNetwork network) {
        CyTableManager tableManager = registrar.getService(CyTableManager.class);
        Set<CyTable> netTables = new HashSet<CyTable>();
        Set<String> tableNames = new HashSet<String>(TermSource.getTables());
        Set<CyTable> currTables = tableManager.getAllTables(true);
        for (CyTable current : currTables) {
            if (tableNames.contains(current.getTitle())
                    && current.getColumn(EnrichmentTerm.colNetworkSUID) != null
                    && current.getAllRows().size() > 0) {
                CyRow tempRow = current.getAllRows().get(0);
                if (tempRow.get(EnrichmentTerm.colNetworkSUID, Long.class) != null && tempRow
                        .get(EnrichmentTerm.colNetworkSUID, Long.class).equals(network.getSUID())) {
                    netTables.add(current);
                }
            }
        }
        return netTables;
    }


    public static class ConfigPropsReader extends AbstractConfigDirPropsReader {
        ConfigPropsReader(SavePolicy policy, String name) {
            super(name, "gProfiler.props", policy);
        }
    }


    // Method to convert terms entered in search text to
    // appropriate newline-separated string to send to server
    public static String convertTerms(String terms, boolean splitComma, boolean splitSpaces) {
        String regexSp = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)";
        String regexComma = "[,]+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)";
        if (splitSpaces) {
            // Substitute newlines for space
            terms = terms.replaceAll(regexSp, "\n");
        }

        if (splitComma) {
            // Substitute newlines for commas
            terms = terms.replaceAll(regexComma, "\n");
        }

        // Strip off any blank lines
        terms = terms.replaceAll("(?m)^\\s*", "");
        return terms;
    }
    public static void replaceListColumnIfNeeded(CyTable table, Class<?> clazz, String columnName) {
        if (table.getColumn(columnName) != null)
            table.deleteColumn(columnName);

        table.createListColumn(columnName, clazz, false);
    }
    public static void replaceColumnIfNeeded(CyTable table, Class<?> clazz, String columnName) {
        if (table.getColumn(columnName) != null)
            table.deleteColumn(columnName);

        table.createColumn(columnName, clazz, false);
    }

    public static void copyRow(CyTable fromTable, CyTable toTable, CyIdentifiable from, CyIdentifiable to, List<String> columnsCreated) {
        for (CyColumn col: fromTable.getColumns()) {
            // TODO: Is it OK to not check for this?
            //if (!columnsCreated.contains(col.getName()))
            //	continue;
            if (col.getName().equals(CyNetwork.SUID))
                continue;
            if (from.getClass().equals(CyNode.class) && col.getName().equals(CyNetwork.NAME))
                continue;
            if (col.getName().equals(CyNetwork.SELECTED))
                continue;
            if (from.getClass().equals(CyNode.class) && col.getName().equals(CyRootNetwork.SHARED_NAME))
                continue;
            if (col.getName().equals(ModelUtils.QUERYTERM) || col.getName().equals(ModelUtils.DISPLAY) || col.getName().equals(ModelUtils.ID)) {
                Object v = fromTable.getRow(from.getSUID()).getRaw(col.getName());
                toTable.getRow(to.getSUID()).set(col.getName() + ".copy", v);
                continue;
            }
            // TODO: Is it OK to overwrite interaction type?
            //if (from.getClass().equals(CyEdge.class) && col.getName().equals(CyRootNetwork.SHARED_INTERACTION))
            //	continue;
            //if (from.getClass().equals(CyEdge.class) && col.getName().equals(CyEdge.INTERACTION))
            //	continue;
            Object v = fromTable.getRow(from.getSUID()).getRaw(col.getName());
            toTable.getRow(to.getSUID()).set(col.getName(), v);
        }
    }

    public static void copyNodes(CyNetwork fromNetwork, CyNetwork toNetwork, Map<String, CyNode> nodeMap,
                                 String keyColumn, List<String> toColumns) {
        for (CyNode node: fromNetwork.getNodeList()) {
            String key = fromNetwork.getRow(node).get(keyColumn, String.class);
            // TODO: double-check what happens when key == null
            if (key != null && !nodeMap.containsKey(key)) {
                CyNode newNode = toNetwork.addNode();
                nodeMap.put(key, newNode);
                toNetwork.getRow(newNode).set(CyNetwork.NAME, key);
                for (String col: toColumns) {
                    toNetwork.getRow(newNode).set(col, key);
                }
            }
        }
    }

    public static void createNodeMap(CyNetwork network, Map<String, CyNode> nodeMap, String column) {
        // Get all of the nodes in the network
        for (CyNode node: network.getNodeList()) {
            String key = network.getRow(node).get(column, String.class);
            nodeMap.put(key, node);
        }
    }

    public static List<String> copyColumns(CyTable fromTable, CyTable toTable) {
        List<String> columns = new ArrayList<String>();
        for (CyColumn col: fromTable.getColumns()) {
            String fqn = col.getName();
            // Does that column already exist in our target?
            if (toTable.getColumn(fqn) == null) {
                // No, create it.
                if (col.getType().equals(List.class)) {
                    // There is no easy way to handle this, unfortunately...
                    // toTable.createListColumn(fqn, col.getListElementType(), col.isImmutable(), (List<?>)col.getDefaultValue());
                    if (col.getListElementType().equals(String.class))
                        toTable.createListColumn(fqn, String.class, col.isImmutable(),
                                (List<String>)col.getDefaultValue());
                    else if (col.getListElementType().equals(Long.class))
                        toTable.createListColumn(fqn, Long.class, col.isImmutable(),
                                (List<Long>)col.getDefaultValue());
                    else if (col.getListElementType().equals(Double.class))
                        toTable.createListColumn(fqn, Double.class, col.isImmutable(),
                                (List<Double>)col.getDefaultValue());
                    else if (col.getListElementType().equals(Integer.class))
                        toTable.createListColumn(fqn, Integer.class, col.isImmutable(),
                                (List<Integer>)col.getDefaultValue());
                    else if (col.getListElementType().equals(Boolean.class))
                        toTable.createListColumn(fqn, Boolean.class, col.isImmutable(),
                                (List<Boolean>)col.getDefaultValue());
                } else {
                    toTable.createColumn(fqn, col.getType(), col.isImmutable(), col.getDefaultValue());
                    columns.add(fqn);
                }
            } else if (fqn.equals(ModelUtils.QUERYTERM) || fqn.equals(ModelUtils.DISPLAY) || fqn.equals(ModelUtils.ID)) {
                toTable.createColumn(fqn + ".copy", col.getType(), col.isImmutable(), col.getDefaultValue());
                columns.add(fqn + ".copy");
            }
        }
        return columns;
    }

    public static CyNetworkView getNetworkView(CyServiceRegistrar registrar, CyNetwork network) {
        Collection<CyNetworkView> views =
                registrar.getService(CyNetworkViewManager.class).getNetworkViews(network);

        // At some point, figure out a better way to do this
        for (CyNetworkView view: views) {
            return view;
        }
        return null;
    }

    /**
     * Parse through the response received and return a structured list to populate the result table
     *
     * @param response JSON response received by making API call
     * @return structured list of data which can be used to populate result table
     */
    public static List<EnrichmentTerm> getEnrichmentfromJSON(JSONObject response){
        JSONArray enrichmentArray = getResultsFromJSON(response, JSONArray.class);
        if (enrichmentArray == null) {
            return null;
        }
        List<EnrichmentTerm> results = new ArrayList<>();
        for(Object enrObject : enrichmentArray){
            JSONObject enr = (JSONObject) enrObject;
            EnrichmentTerm currTerm = new EnrichmentTerm();
            if(enr.containsKey("description")){
                currTerm.setDescription((String) enr.get("description"));
            }
            if(enr.containsKey("intersection_size")){
                currTerm.setIntersectionSize(((Number) enr.get("intersection_size")).intValue());
            }
            if(enr.containsKey("effective_domain_size")){
                currTerm.setEffectiveDomainSize(((Number) enr.get("effective_domain_size")).intValue());
            }
            if(enr.containsKey("p_value")){
                currTerm.setPValue(((Number) enr.get("p_value")).intValue());
            }
            if(enr.containsKey("precision")){
                currTerm.setPrecision(((Number) enr.get("precision")).intValue());
            }
            if(enr.containsKey("recall")){
                currTerm.setRecall(((Number) enr.get("recall")).intValue());
            }
            if(enr.containsKey("goshv")){
                currTerm.setGoshv(((Number) enr.get("goshv")).intValue());
            }
            if(enr.containsKey("term_size")){
                currTerm.setTermSize(((Number) enr.get("term_size")).intValue());
            }
            if(enr.containsKey("significant")){
                currTerm.setSignificant(((Boolean) enr.get("significant")).booleanValue());
            }
            if(enr.containsKey("source")){
                currTerm.setSource((String) enr.get("source"));
            }
            if(enr.containsKey("name")){
                currTerm.setName((String) enr.get("name"));
            }
            results.add(currTerm);
            System.out.println(currTerm.getName());
        }
        return results;
    }

    public static <T> T getResultsFromJSON(JSONObject json, Class<? extends T> clazz) {
        if (json == null)
            return null;

        // System.out.println("json: " + json.toJSONString());

        Object result = json.get("result");
        if (!clazz.isAssignableFrom(result.getClass()))
            return null;

        return (T) result;
    }

    public static void copyNodeAttributes(CyNetwork from, CyNetwork to,
                                          Map<String, CyNode> nodeMap, String column) {
        // System.out.println("copyNodeAttributes");
        List<String> columnsCreated = copyColumns(from.getDefaultNodeTable(), to.getDefaultNodeTable());
        for (CyNode node: from.getNodeList()) {
            String nodeKey = from.getRow(node).get(column, String.class);
            if (!nodeMap.containsKey(nodeKey))
                continue;
            CyNode newNode = nodeMap.get(nodeKey);
            copyRow(from.getDefaultNodeTable(), to.getDefaultNodeTable(), node, newNode, columnsCreated);
        }
    }

}
