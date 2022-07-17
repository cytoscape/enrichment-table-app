package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class OrganismAndGeneIdAssertionTask {

    private static final Map<String,String> scientificNameToID = ModelUtils.getOrganisms();

    public static void setOrganism(final CyNetwork network) {
        // Get current organism from network if present
        String currentOrganism = ModelUtils.getNetOrganism(network);

        // If the network table already has organism, don't look further
        if (currentOrganism != null) {
            return;
        }

        String initialOrganism = "hsapiens";
        // Getting information from other parameters in current network
        OrganismNetworkEntry[] otherNetworks = OrganismNetworkEntry.values();
        ArrayList<String> otherNetworkParameters = new ArrayList<>();
        for (OrganismNetworkEntry entry: otherNetworks) {
            otherNetworkParameters.addAll(getList(network, entry.toString()));
        }
        for (String e: otherNetworkParameters) {
            if(e != null) {
                e = e.trim();
                if (scientificNameToID.containsKey(e)) {
                    initialOrganism = scientificNameToID.get(e);
                    break;
                }
                else if (scientificNameToID.containsValue(e)) {
                    initialOrganism = e;
                    break; // Breaking loop to optimize
                }
            }
        }
        ModelUtils.setNetOrganism(network, initialOrganism);
    }

    public static String getActualNameFromCodeName(String codeName) {
        return scientificNameToID.entrySet().stream().filter(stringStringEntry ->
                stringStringEntry.getValue().equals(codeName)).findFirst().get().getKey();
    }

    public static void setGeneId(final CyNetwork network, final CyServiceRegistrar registrar) {
        // Get current geneID from network if present
        String geneId = ModelUtils.getNetGeneIDColumn(network);
        if (geneId != null) return;

        // Predict gene id by network type
        geneId = getGeneIdFromNetworkName(network);

        if (geneId != null) ModelUtils.setNetGeneIDColumn(network, geneId);
        else {
            // Predict gene id from style
            final VisualMappingManager visualMappingManager = registrar.getService(VisualMappingManager.class);
            final VisualStyle visualStyle = visualMappingManager.getCurrentVisualStyle();
            final VisualMappingFunction<?, String> mappingFunction =
                    visualStyle.getVisualMappingFunction(BasicVisualLexicon.NODE_LABEL);
            if (mappingFunction != null)
                ModelUtils.setNetGeneIDColumn(network, mappingFunction.getMappingColumnName());
            else ModelUtils.setNetGeneIDColumn(network, "name");
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

    private enum OrganismNetworkEntry {
        STRINGAPP("species"),
        NDEX("organism"),
        PSIQCUIC("Taxonomy ID"),
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
