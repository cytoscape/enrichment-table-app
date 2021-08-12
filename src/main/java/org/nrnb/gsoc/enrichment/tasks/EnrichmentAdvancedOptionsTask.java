package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.CyApplicationManager;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.*;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListSingleSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ighosh98
 * @description Creates the Advanced Options Panel for fine tuning the query parameters
 */
public class EnrichmentAdvancedOptionsTask extends AbstractTask {
    final CyServiceRegistrar registrar;
    final CyApplicationManager applicationManager;
    final CyNetwork network;
    final CyTable nodeTable;

    @Tunable(description = "Adjusted p-value threshold",
            tooltip = "<html>This is the custom significance threshold.<br/>"+
                    "Values below the threshold will be excluded.</html>",
            longDescription = "float between 0 and 1, used to define custom significance threshold.",
            exampleStringValue = "0.05",
            groups = {"Optional settings"},
            params="slider=false", gravity = 109.0)
    public BoundedDouble user_threshold = new BoundedDouble(0.0, 0.05, 1.0, false, false);

    @Tunable(description = "Decides if electronic annotations should be excluded or not",
            longDescription = "Unless this is set to true, we only show electronic annotations should be included.",
            groups = {"Optional settings"},
            tooltip = "<html>Unless this is set to true, we only show results above the significance threshold.</html>")
    public boolean no_iea = true;

    @Tunable(description = "Type of testing correction method",
            tooltip = "Set the type of testing correction method",
            longDescription = "Set the type of testing correction method",
            exampleStringValue = "g_SCS",
            groups = {"Optional settings"},
            gravity = 100.0)
    public ListSingleSelection<String> significance_threshold_method;

    @Tunable(description="Organism",
            longDescription="Default species for network queries.",
            exampleStringValue = "Homo Sapiens",
            params="lookup=begins", groups={"Required settings"}, gravity=10.0)
    public ListSingleSelection<String> organism;

    @Tunable(description="Gene ID Column",
            longDescription="Column to choose for getting GeneIDs.",
            exampleStringValue = "LABEL",
            params="lookup=begins", groups={"Required settings"}, gravity=10.0)
    public ListSingleSelection<String> geneID;

    public Map<String,String> scientificNametoID;

    public EnrichmentAdvancedOptionsTask(CyServiceRegistrar registrar) {
        this.registrar = registrar;
        applicationManager = registrar.getService(CyApplicationManager.class);
        this.network = applicationManager.getCurrentNetwork();
        nodeTable = network.getDefaultNodeTable();
        this.scientificNametoID = ModelUtils.getOrganisms();
        List<String> speciesList = new ArrayList<>();
        if(scientificNametoID!=null) {
            for (Map.Entry<String, String> it : scientificNametoID.entrySet()) {
                speciesList.add(it.getKey());
            }
            organism = new ListSingleSelection<String>(speciesList);
            organism.setSelectedValue("Homo Sapiens");
            ModelUtils.setNetOrganism(network,"hsapiens");
        }
        List<String> stringCol = new ArrayList<String>();
        for (CyColumn col : nodeTable.getColumns()) {
            if (col.getType().equals(String.class)) {
                stringCol.add(col.getName());
            }
        }
        geneID = new ListSingleSelection<String>(stringCol);
        geneID.setSelectedValue("name");
        ModelUtils.setNetGeneIDColumn(network,"name");
        significance_threshold_method = new ListSingleSelection<String>(new ArrayList<String>(){
            {
                add("g_SCS");
                add("bonferroni");
                add("fdr");
            }
        });
        //default value
        significance_threshold_method.setSelectedValue("g_SCS");
    }

    //user sets the cycol -> update default -> the run the query
    @Override
    public void run(TaskMonitor monitor) throws Exception {
        monitor.setTitle("Enrichment settings");
        /**
         * The values must be stored and used
         */
        if(network!=null) {
            // save values to network
            ModelUtils.setNetSignificanceThresholdMethod(network, significance_threshold_method.getSelectedValue());
            ModelUtils.setNetGeneIDColumn(network, geneID.getSelectedValue().toString());
            ModelUtils.setNetNoIEA(network, no_iea);
            ModelUtils.setNetUserThreshold(network, user_threshold.getValue());
            if(scientificNametoID.containsKey(organism.getSelectedValue())){
                ModelUtils.setNetOrganism(network,scientificNametoID.get(organism.getSelectedValue()));
            } else{
                monitor.setStatusMessage("Could not find organism. Your entry is incorrect");
            }
        }
        return;
    }

    @ProvidesTitle
    public String getTitle() {
        return "Settings";
    }
}
