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
import org.json.simple.JSONObject;
import org.nrnb.gsoc.enrichment.RequestEngine.HTTPRequestEngine;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ighosh98
 * @description Creates the Advanced Options Panel for fine tuning the query
 */
public class EnrichmentAdvancedOptionsTask extends AbstractTask {
    final CyServiceRegistrar registrar;
    final CyApplicationManager applicationManager;
    final CyNetwork network;


    final CyTable nodeTable;

    @Tunable(description = "Custom significance threshold",
            tooltip = "<html>This is the custom significance threshold.<br/>"+
                    "Values below the threshold will be excluded.</html>",
            longDescription = "float between 0 and 1, used to define custom significance threshold.",
            exampleStringValue = "0.5",
            groups = {"Default Parameter Values"},
            params="slider=true", gravity = 109.0)
    public BoundedDouble user_threshold = new BoundedDouble(0.0, 0.5, 1.0, false, false);

    @Tunable(description = "Return all results",
            longDescription = "Boolean. Default false. If true, the API also returns results that are below the significance threshold.",
            groups = {"Default Parameter Values"},
            tooltip = "<html>Default false. If true, the API also returns results that are below the significance threshold.</html>")
    public boolean all_results = false;
    @Tunable(description = "Decides if electronic annotations should be excluded or not",
            longDescription = "Unless this is set to true, we only show electronic annotations should be included.",
            groups = {"Default Parameter Values"},
            tooltip = "<html>Unless this is set to true, we only show results above the significance threshold.</html>")
    public boolean no_iea = false;
    @Tunable(description = "Decides if we should return under-represented functional terms or not",
            longDescription = "Unless this is set to true, we only show electronic annotations should be included.",
            groups = {"Default Parameter Values"},
            tooltip = "<html>Unless this is set to true, we only show results above the significance threshold.</html>")
    public boolean measure_underrepresentation = false;

    @Tunable(description = "Type of testing correction method",
            tooltip = "Set the type of testing correction method",
            longDescription = "Set the type of testing correction method",
            exampleStringValue = "g_SCS",
            groups = {"Default Parameter Values"},
            gravity = 100.0)
    public ListSingleSelection<String> significance_threshold_method;

    @Tunable(description="Organism",
            longDescription="Default species for network queries.",
            exampleStringValue = "Homo Sapiens",
            params="lookup=begins", groups={"Query Defaults (take effect after restarting Cytoscape)"}, gravity=10.0)
    public ListSingleSelection<String> organism;

    @Tunable(description="Gene ID Column",
            longDescription="Column to choose for getting GeneIDs.",
            exampleStringValue = "LABEL",
            params="lookup=begins", groups={"Query Defaults (take effect after restarting Cytoscape)"}, gravity=10.0)
    public ListSingleSelection<CyColumn> geneID;

    public Map<String,String> scientificNametoID;

    /**
     *
     * @param registrar
     */
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
        }
        geneID = new ListSingleSelection<CyColumn>(new ArrayList<CyColumn>(nodeTable.getColumns()));
        significance_threshold_method = new ListSingleSelection<String>(new ArrayList<String>(){
            {
                add("g_SCS");
                add("bonferroni");
                add("fdr");
            }
        });
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
            ModelUtils.setNetAllResults(network, all_results);
            ModelUtils.setNetMeasureUnderrepresentation(network, measure_underrepresentation);
            ModelUtils.setNetNoIEA(network, no_iea);
            ModelUtils.setNetUserThreshold(network, user_threshold.getValue());
        }
        return;
    }

    @ProvidesTitle
    public String getTitle() {
        return "Advanced Options";
    }

}
