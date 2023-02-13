package org.nrnb.gsoc.enrichment.tasks;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class OrganismAndGeneIdAssertionTask extends AbstractTask {

    private static final Logger logger = Logger.getLogger(CyUserLog.NAME);
    private static final Map<String,String> scientificNameToID = ModelUtils.getOrganisms();
    private static String initialOrganism = "hsapiens";
    private static String geneId = "";


    public OrganismAndGeneIdAssertionTask() {
    }

    /**
     * @description Process data from network to predict organism. The process involves getting data from column
     * present in enum {@code OrganismNetworkEntry}.
     * It matches the data with the information we retrieved from GProfiler organism information.
     * It checks if the for any value in GProfiler organisms, the organism is contained inside the column data,
     * and if present selects the organism.
     *
     * @param currentNetwork The current loaded network.
     */

    public static String getOrganismPrediction() {
         return initialOrganism;
     }

     public static String getGeneIdPrediction() {
          return geneId;
      }

    public static void setOrganism(final CyNetwork currentNetwork) {
        if (currentNetwork == null) {
            logger.error("[Enrichment Table] No Network selected");
            return;
        }
        // Get current organism from network if present
        String currentOrganism = ModelUtils.getNetOrganism(currentNetwork);

        // If the network table already has organism, don't look further
        if (currentOrganism != null) {
            return;
        }

        // Getting information from other parameters in current network
        OrganismNetworkEntry[] otherNetworks = OrganismNetworkEntry.values();
        ArrayList<String> otherNetworkParameters = new ArrayList<>();
        for (OrganismNetworkEntry entry: otherNetworks) {
            otherNetworkParameters.addAll(getList(currentNetwork, entry.toString()));
        }
        for (String possibleOrganism: otherNetworkParameters) {
            if(possibleOrganism != null) {
                final String trimmedPossibleOrganism = possibleOrganism.trim().toLowerCase();
                Optional<String> gProfilerOrganism = scientificNameToID.keySet().stream()
                        .filter(gProfilerName -> trimmedPossibleOrganism.contains(gProfilerName.toLowerCase()))
                        .findFirst();
                if (gProfilerOrganism.isPresent()) {
                    initialOrganism = scientificNameToID.get(gProfilerOrganism.get());
                    logger.info("[Enrichment Table] Organism predicted to be [" + initialOrganism + "] for enrichment");
                    break;
                }
                else {
                    Optional<Map.Entry<String, String>> gProfilerScientificId = scientificNameToID
                            .entrySet().stream().filter(keyValuePair -> trimmedPossibleOrganism.contains(
                                    keyValuePair.getValue().toLowerCase())).findFirst();
                    if (gProfilerScientificId.isPresent()) {
                        initialOrganism = gProfilerScientificId.get().getValue();
                        logger.info("[Enrichment Table] Organism predicted to be [" + initialOrganism  + "] for enrichment");
                        break; // Breaking loop to optimize
                    }
                }
            }
        }
        logger.info("[Enrichment Table] Using default organism [" + initialOrganism + "] for enrichment ");
        //ModelUtils.setNetOrganism(currentNetwork, initialOrganism);
    }

    public static String getActualNameFromCodeName(String codeName) {
        Optional<Map.Entry<String, String>> actualName = scientificNameToID.entrySet().stream().filter(stringStringEntry ->
                stringStringEntry.getValue().equals(codeName)).findFirst();
        return actualName.map(Map.Entry::getKey).orElse(null);
    }

    public static void setGeneId(CyNetwork network, CyServiceRegistrar registrar) {

        if (registrar == null) {
            logger.error("[Enrichment Table] Service registrar is null");
            return;
        }

        if (network == null) {
            logger.error("[Enrichment Table] Network is null");
            return;
        }

        // Get current geneID from network if present
        geneId = ModelUtils.getNetGeneIDColumn(network);
        if (geneId != null) return;

        // Predict gene id by network type
        geneId = getGeneIdFromNetworkName(network);

        if (geneId != null) {
            //ModelUtils.setNetGeneIDColumn(network, geneId);
            logger.info("[Enrichment Table] Using column [" + geneId + "] for enrichment ");
        }
        else {
            // Predict gene id from style
            CyNetworkViewManager cyNetworkViewManager = registrar.getService(CyNetworkViewManager.class);
            final VisualMappingManager visualMappingManager = registrar.getService(VisualMappingManager.class);
            Collection<CyNetworkView> views = cyNetworkViewManager.getNetworkViews(network);
            boolean isGeneSetFromStyle = false;
            for (CyNetworkView view : views) {
                final VisualStyle visualStyle = visualMappingManager.getVisualStyle(view);
                final VisualMappingFunction<?, String> mappingFunction =
                        visualStyle.getVisualMappingFunction(BasicVisualLexicon.NODE_LABEL);
                if (mappingFunction != null) {
                    isGeneSetFromStyle = true;
                    geneId = mappingFunction.getMappingColumnName();
                    logger.info("[Enrichment Table] Using column [" + mappingFunction.getMappingColumnName()
                            + "] for enrichment ");
                    break;
                }
            }

            if (!isGeneSetFromStyle) {
                geneId = "name";
                logger.info("[Enrichment Table] Using default column [name] for enrichment ");
            }
        }

    }

    private static List<String> getList(CyNetwork network, String columnName) {
        final Optional<CyColumn> cyColumn = Optional.ofNullable(network.getDefaultNetworkTable().getColumn(columnName));
        return cyColumn.map(column -> Arrays.stream(column.getValues(String.class).get(0).split(","))
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private static String getGeneIdFromNetworkName(final CyNetwork network) {
        String geneId = null;
        final CyColumn networkNameColumn = network.getDefaultNetworkTable().getColumn("name");
        if (networkNameColumn != null && networkNameColumn.getValues(String.class) != null
                && !networkNameColumn.getValues(String.class).isEmpty()) {

            String networkName = networkNameColumn.getValues(String.class).get(0);
            // If STRING network type
            if (networkName.contains("STRING")) geneId = "display name";
        }
        return geneId;
    }

    @Override
    public void run(TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Organism and Gene id assertion");
    }

    private enum OrganismNetworkEntry {
        STRINGAPP("species"),
        NDEX("organism"),
        IntAct("IntAct::species");

        private final String columnName;

        OrganismNetworkEntry(String columnName) {
            this.columnName = columnName;
        }

        public String toString() {
            return this.columnName;
        }
    }
}
