package org.nrnb.gsoc.enrichment.utils;

import org.cytoscape.model.*;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.property.AbstractConfigDirPropsReader;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.nrnb.gsoc.enrichment.RequestEngine.HTTPRequestEngine;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;

import java.util.*;

/**
 * @author ighosh98
 * @description
 */
public class ModelUtils {

    // Namespaces
    public static String ENRICHMENT_NAMESPACE = "EnrichmentTable";
    public static String NAMESPACE_SEPARATOR = "::";

    // Node information
    public static String CANONICAL = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "canonical name";
    public static String DISPLAY = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "display name";
    public static String FULLNAME = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "full name";
    public static String ID = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR +  "@id";
    public static String QUERYTERM = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR +  "query term";
    public static String PROFILERID = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "database identifier";
    public static Map<String,String> scientificNametoID = ModelUtils.getOrganisms();

    public static int MAX_SHORT_NAME_LENGTH = 15; // 15 characters, or 14 characters plus the dot
    public static int SECOND_SEGMENT_LENGTH = 3;


    // Network information
    public static String NET_ENRICHMENT_SETTINGS = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "enrichmentSettings";
    public static String NET_ORGANISMS = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "organism";
    public static String NET_GENE_ID_COLUMN = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "Gene ID Column";
    public static String NET_NO_IEA = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR +  "No IEA";
    public static String NET_DOMAIN_SCOPE =ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR +  "Domain Scope";
    public static String NET_SIGNIFICANCE_THRESHOLD_METHOD = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "Significance Threshold Method";
    public static String NET_BACKGROUND = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "Background";
    public static String NET_USER_THRESHOLD = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR +  "User Threshold";
    public static String  NET_ALL_RESULTS = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "All Results";
    public static String  NET_MEASURE_UNDERREPRESENTATION = ENRICHMENT_NAMESPACE + NAMESPACE_SEPARATOR + "Measure Underrepresentation";
    // Create network view size threshold
    // See https://github.com/cytoscape/cytoscape-impl/blob/develop/core-task-impl/
    // src/main/java/org/cytoscape/task/internal/loadnetwork/AbstractLoadNetworkTask.java
    public static int DEF_VIEW_THRESHOLD = 3000;

    /**
     *
     * @param network
     * @return
     */
    public static boolean haveQueryTerms(CyNetwork network) {
        if (network == null) return false;
        for (CyNode node: network.getNodeList()) {
            if (network.getRow(node).get(QUERYTERM, String.class) != null)
                return true;
        }
        return false;
    }

    /**
     *
     * @param network
     */
    public static void selectQueryTerms(CyNetwork network) {
        for (CyNode node: network.getNodeList()) {
            if (network.getRow(node).get(QUERYTERM, String.class) != null)
                network.getRow(node).set(CyNetwork.SELECTED, true);
            else
                network.getRow(node).set(CyNetwork.SELECTED, false);
        }
    }

    /**
     *
     * @param network
     * @return
     */
    public static String getExisting(CyNetwork network) {
        StringBuilder str = new StringBuilder();
        for (CyNode node : network.getNodeList()) {
            String profilerID = network.getRow(node).get(PROFILERID, String.class);
            if (profilerID != null && profilerID.length() > 0)
                str.append(profilerID + "\n");
        }
        return str.toString();
    }

    /**
     *
     * @param network
     * @param nodeView
     * @return
     */
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

    /**
     *
     * @param registrar
     * @param network
     * @param name
     * @return the latest enrichment table
     */
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

    /**
     * @description Initialize the Enrichment Table
     * @param enrichmentTable
     */
    public static void setupEnrichmentTable(CyTable enrichmentTable) {
        if (enrichmentTable.getColumn(EnrichmentTerm.colName) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colName, String.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colPvalue) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colPvalue, Double.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colSource) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colSource, String.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colDescription) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colDescription, String.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colTermID) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colTermID, String.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colIntersectionSize) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colIntersectionSize, Integer.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colEffectiveDomainSize) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colEffectiveDomainSize, Integer.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colTermSize) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colTermSize, Integer.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colPrecision) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colPrecision, Double.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colQuerySize) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colQuerySize, Integer.class, false);
        }
        if (enrichmentTable.getColumn(EnrichmentTerm.colRecall) == null) {
            enrichmentTable.createColumn(EnrichmentTerm.colRecall, Double.class, false);
        }

        if (enrichmentTable.getColumn(EnrichmentTerm.colGenes) == null) {
            enrichmentTable.createListColumn(EnrichmentTerm.colGenes, String.class, false);
        }
    }

    /**
     *
     * @param nodeTable
     * @return
     */
    public static List<CyColumn> getProfilerColumn(CyTable nodeTable) {
        List<CyColumn> profilerColumnNames = new ArrayList<CyColumn>();
        for (CyColumn col : nodeTable.getColumns()) {
            if (col.getType().equals(String.class)) {
                profilerColumnNames.add(col);
            }
        }
        return profilerColumnNames;
    }

    /**
     *
     * @return a mapping of organisms actual names to it's code names [ Example "Homo Sapien" : "hsapiens"
     * Reference: https://stackoverflow.com/questions/17037340/converting-jsonarray-to-arraylist/34081506
     */
    public static Map<String,String> getOrganisms() {
        if(scientificNametoID!=null){
            return scientificNametoID;
        }
        HTTPRequestEngine requestEngine = new HTTPRequestEngine();
        JSONArray result = requestEngine.makeGetRequest("util/organisms_list/");
        if(result==null){
            return scientificNametoID;
        }
        JSONArray jsonArrayScientificName = new JSONArray();
        JSONArray jsonArrayID= new JSONArray();
        for(int i=0;i<result.size();i++){
            JSONObject jsonObject = (JSONObject) result.get(i);
            jsonArrayScientificName.add(jsonObject.get("scientific_name"));
            jsonArrayID.add(jsonObject.get("id"));
        }

        scientificNametoID = new HashMap<>();
        if(jsonArrayID!=null && jsonArrayScientificName!=null){
            for(int i=0;i<jsonArrayID.size();i++){
                scientificNametoID.put(jsonArrayScientificName.get(i).toString(),jsonArrayID.get(i).toString());
            }
        }
        return scientificNametoID;
    }

    /**
     * @description returns the name of the organism
     * @param scientificNametoID
     * @return
     */
    public static List<String> getOrganismsName(Map<String,String> scientificNametoID ) {
        List<String> allSpecies = new ArrayList<String>();
        for(String name : scientificNametoID.keySet()){
            allSpecies.add(name);
        }
        return allSpecies;
    }



    /**
     * @description converts list to strinf
     * @param list
     * @return {String} Comma separated List
     */
    public static String listToString(List<?> list) {
        String str = "";
        if (list == null || list.size() == 0) return str;
        for (int i = 0; i < list.size()-1; i++) {
            str += list.get(i)+",";
        }
        return str + list.get(list.size()-1).toString();
    }

    /**
     * @description Convert comma separated strign to list
     * @param string
     * @return list
     */
    public static List<String> stringToList(String string) {
        if (string == null || string.length() == 0) return new ArrayList<String>();
        String [] arr = string.split(",");
        return Arrays.asList(arr);
    }

    /**
     *
     * @param network
     * @param settings
     */
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

    /**
     * @description Helper function to create column in the enrichment table if there is a need
     */
    public static void createColumnIfNeeded(CyTable table, Class<?> clazz, String columnName) {
        if (table.getColumn(columnName) != null)
            return;

        table.createColumn(columnName, clazz, false);
    }

    /**
     *
     * @param network
     * @return Enrichment Settings
     */
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

    /**
     *
     * @param registrar
     * @param network
     * @return Set of Enrichment tables registered corresponding to a network
     */
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
                //System.out.println(current.getColumn(EnrichmentTerm.colNetworkSUID));
            }
        }
        return netTables;
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
    public static List<EnrichmentTerm> getEnrichmentfromJSON(JSONObject response, CyNetwork network, List<String> nodeNameList,Map<String, Long> stringNodesMap){
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
                String content = enr.get("p_value").toString();
                double pValue = Double.parseDouble(content);
                currTerm.setPValue(pValue);
            }
            if(enr.containsKey("precision")){
                String content = enr.get("precision").toString();
                double precision = Double.parseDouble(content);
                currTerm.setPrecision(precision);
            }
            if(enr.containsKey("recall")){
                String content = enr.get("recall").toString();
                double recall = Double.parseDouble(content);
                currTerm.setRecall(recall);
            }
            if(enr.containsKey("native")){
                String content = enr.get("native").toString();
                currTerm.setTermID(content);
            }
            if(enr.containsKey("goshv")){
                String content = enr.get("goshv").toString();
                double goshv = Double.parseDouble(content);
                //currTerm.setRecall(goshv);
            }
            if(enr.containsKey("term_size")){
                String content = enr.get("term_size").toString();
                int termSize = Integer.parseInt(content);
                currTerm.setTermSize(termSize);
            }
            if(enr.containsKey("query_size")){
                String content = enr.get("query_size").toString();
                int termSize = Integer.parseInt(content);
                currTerm.setQuerySize(termSize);
            }
            if(enr.containsKey("significant")){
                currTerm.setSignificant(((Boolean) enr.get("significant")).booleanValue());
            }
            if(enr.containsKey("source")){
                String content = enr.get("source").toString();
                if(content==null || content.isEmpty()){
                    currTerm.setSource((String) "");
                } else{
                    currTerm.setSource((String) content);
                }
            }
            if(enr.containsKey("name")){
                currTerm.setName((String) enr.get("name"));
            } if(enr.containsKey("intersections")){
                List<String> currGeneList = new ArrayList<String>();
                List<Long> currNodeList = new ArrayList<Long>();
                JSONArray genes = (JSONArray)enr.get("intersections");
                for(int i=0;i<genes.size();i++){
                    if((genes.get(i)).toString().length()>2){
                        String enrGeneEnsemblID = (String)nodeNameList.get(i);
                        String enrGeneNodeName = enrGeneEnsemblID;
                        final Long nodeSUID = stringNodesMap.get(enrGeneNodeName);
                        currNodeList.add(nodeSUID);
                        currGeneList.add(nodeNameList.get(i));
                    }
                }
                currTerm.setGenes(currGeneList);
                currTerm.setNodesSUID(currNodeList);
            }
            results.add(currTerm);
            //System.out.println(currTerm.getDescription());
        }
        return results;
    }

    //Network information getters and setters
    public static void setNetOrganism(CyNetwork network, String species) {
        createColumnIfNeeded(network.getDefaultNetworkTable(), String.class, NET_ORGANISMS);
        network.getRow(network).set(NET_ORGANISMS, species);
    }

    /**
     *
     * @param network
     * @return Column to be chosen for fetching GeneID
     */
    public static String getNetGeneIDColumn(CyNetwork network) {
        if (network.getDefaultNetworkTable().getColumn(NET_GENE_ID_COLUMN) == null){
            return null;
        }
        return network.getRow(network).get(NET_GENE_ID_COLUMN, String.class);
    }

    /**
     * Sets the column which is to be used to choose GeneID
     * @param network
     * @param column
     */
    public static void setNetGeneIDColumn(CyNetwork network, String column) {
        createColumnIfNeeded(network.getDefaultNetworkTable(), String.class, NET_GENE_ID_COLUMN);
        network.getRow(network).set(NET_GENE_ID_COLUMN, column);
    }

    public static String getNetOrganism(CyNetwork network) {
        if (network.getDefaultNetworkTable().getColumn(NET_ORGANISMS) == null)
            return null;
        return network.getRow(network).get(NET_ORGANISMS, String.class);
    }

    /**
     * @description @see <a href="https://biit.cs.ut.ee/gprofiler/page/apis"> API Documentation </a> for gProfiler for more details
     */

    /**
     * background setter
     */
    public static void setNetBackground(CyNetwork network, List<String> background) {
        createColumnIfNeeded(network.getDefaultNetworkTable(), String.class, NET_BACKGROUND);
        network.getRow(network).set(NET_BACKGROUND, background);
    }

    /**
     * background getter
     */
    public static List<String> getNetBackground(CyNetwork network) {
        if (network.getDefaultNetworkTable().getColumn(NET_DOMAIN_SCOPE) == null)
            return null;
        return network.getRow(network).get(NET_BACKGROUND, List.class);
    }

    /**
     * user_threshold setter
     */
    public static void setNetUserThreshold(CyNetwork network, Double userThreshold) {
        createColumnIfNeeded(network.getDefaultNetworkTable(), Double.class, NET_USER_THRESHOLD);
        network.getRow(network).set(NET_USER_THRESHOLD, userThreshold);
    }

    /**
     * significance_threshold_method getter
     */
    public static Double getNetUserThreshold(CyNetwork network) {
        if (network.getDefaultNetworkTable().getColumn(NET_USER_THRESHOLD) == null)
            return null;
        return network.getRow(network).get(NET_USER_THRESHOLD, Double.class);
    }

    /**
     * significance_threshold_method setter
     */
    public static void setNetSignificanceThresholdMethod(CyNetwork network, String significanceThresholdMethod) {
        if(network.getDefaultNetworkTable()==null){
            System.out.println("No default network table");
            return;
        }
        createColumnIfNeeded(network.getDefaultNetworkTable(), String.class, NET_SIGNIFICANCE_THRESHOLD_METHOD);
        network.getRow(network).set(NET_SIGNIFICANCE_THRESHOLD_METHOD, significanceThresholdMethod);
    }

    /**
     * significance_threshold_method getter
     */
    public static String getNetSignificanceThresholdMethod(CyNetwork network) {
        if (network.getDefaultNetworkTable().getColumn(NET_SIGNIFICANCE_THRESHOLD_METHOD) == null)
            return null;
        return network.getRow(network).get(NET_SIGNIFICANCE_THRESHOLD_METHOD, String.class);
    }

    /**
     * domain_scope setter
     */
    public static void setNetDomainScope(CyNetwork network, String domainScope) {
        createColumnIfNeeded(network.getDefaultNetworkTable(), String.class, NET_DOMAIN_SCOPE);
        network.getRow(network).set(NET_DOMAIN_SCOPE, domainScope);
    }

    /**
     * no_iea getter
     */
    public static String getNetDomainScope(CyNetwork network) {
        if (network.getDefaultNetworkTable().getColumn(NET_DOMAIN_SCOPE) == null)
            return null;
        return network.getRow(network).get(NET_DOMAIN_SCOPE, String.class);
    }

    /**
     * no_iea setter
     */
    public static void setNetNoIEA(CyNetwork network, Boolean noIEA) {
        createColumnIfNeeded(network.getDefaultNetworkTable(), Boolean.class, NET_NO_IEA);
        network.getRow(network).set(NET_NO_IEA, noIEA);
    }

    /**
     * no_iea getter
     */
    public static Boolean getNetNoIEA(CyNetwork network) {
        if (network.getDefaultNetworkTable().getColumn(NET_NO_IEA) == null)
            return null;
        return network.getRow(network).get(NET_NO_IEA, Boolean.class);
    }

    /**
     * all_results setter
     */
    public static void setNetAllResults(CyNetwork network, Boolean allResults) {
        createColumnIfNeeded(network.getDefaultNetworkTable(), Boolean.class, NET_ALL_RESULTS);
        network.getRow(network).set(NET_ALL_RESULTS, allResults);
    }

    /**
     * all_results getter
     */
    public static Boolean getNetAllResults(CyNetwork network) {
        if (network.getDefaultNetworkTable().getColumn(NET_ALL_RESULTS) == null)
            return null;
        return network.getRow(network).get(NET_ALL_RESULTS, Boolean.class);
    }

    public static <T> T getResultsFromJSON(JSONObject json, Class<? extends T> clazz) {
        if (json == null)
            return null;

        Object result = json.get("result");
        if (!clazz.isAssignableFrom(result.getClass()))
            return null;

        return (T) result;
    }

    public static void copyNodeAttributes(CyNetwork from, CyNetwork to,
                                          Map<String, CyNode> nodeMap, String column) {
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
