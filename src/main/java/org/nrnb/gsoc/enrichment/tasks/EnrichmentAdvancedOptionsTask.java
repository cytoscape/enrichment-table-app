package org.nrnb.gsoc.enrichment.tasks;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.*;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListSingleSelection;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

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

    @Tunable(description = "Overlap cutoff",
            tooltip = "<html>This is the custom significance threshold.<br/>"+
                    "Values below the threshold will be excluded.</html>",
            longDescription = "This is the custom significance threshold. Values below the threshold will be excluded.",
            exampleStringValue = "0.5",
            groups = {"Enrichment Defaults"},
            params="slider=true", gravity = 109.0)
    public BoundedDouble user_threshold = new BoundedDouble(0.0, 0.5, 1.0, false, false);

    @Tunable(description = "Decides what results should be shown.",
            longDescription = "Unless this is set to true, we only show results above the significance threshold.",
            groups = {"Enrichment Defaults"},
            tooltip = "<html>Unless this is set to true, we only show results above the significance threshold.</html>")
    public boolean all_results = false;
    @Tunable(description = "Decides if electronic annotations should be excluded or not",
            longDescription = "Unless this is set to true, we only show electronic annotations should be included.",
            groups = {"Enrichment Defaults"},
            tooltip = "<html>Unless this is set to true, we only show results above the significance threshold.</html>")
    public boolean no_iea = false;
    @Tunable(description = "Decides if we should return under-represented functional terms or not",
            longDescription = "Unless this is set to true, we only show electronic annotations should be included.",
            groups = {"Enrichment Defaults"},
            tooltip = "<html>Unless this is set to true, we only show results above the significance threshold.</html>")
    public boolean measure_underrepresentation = false;
    @Tunable(description = "Type of domain scope",
            tooltip = "Set the desired domain scope",
            longDescription = "Set the desired domain scope",
            exampleStringValue = "annotated",
            groups = {"Enrichment Defaults"},
            gravity = 100.0)
    public ListSingleSelection<String> domain_scope;

    @Tunable(description = "Type of testing correction method",
            tooltip = "Set the type of testing correction method",
            longDescription = "Set the type of testing correction method",
            exampleStringValue = "g_SCS",
            groups = {"Enrichment Defaults"},
            gravity = 100.0)
    public ListSingleSelection<String> significance_threshold_method;

//    @Tunable(description="Organism",
//            longDescription="Default species for network queries.",
//            exampleStringValue = "Homo Sapiens",
//            params="lookup=begins", groups={"Query Defaults (take effect after restarting Cytoscape)"}, gravity=10.0)
//    public ListSingleSelection<String> organism;

    @Tunable(description="Gene ID Column",
            longDescription="Column to choose for getting GeneIDs.",
            exampleStringValue = "LABEL",
            params="lookup=begins", groups={"Query Defaults (take effect after restarting Cytoscape)"}, gravity=10.0)
    public ListSingleSelection<CyColumn> geneID;

    public Map<String,String> scientificNametoID;
    /**
     * evidence codes -> Hard code it as true
     */

    /**
     *
     * @param registrar
     */
    public EnrichmentAdvancedOptionsTask(CyServiceRegistrar registrar) {
        this.registrar = registrar;
        applicationManager = registrar.getService(CyApplicationManager.class);
        this.network = applicationManager.getCurrentNetwork();
        nodeTable = network.getDefaultNodeTable();
//        scientificNametoID = ModelUtils.getOrganisms();
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
            ModelUtils.setNetGeneIDColumn(network, geneID.toString());
            ModelUtils.setNetAllResults(network, all_results);
            ModelUtils.setNetDomainScope(network, domain_scope.getSelectedValue());
            ModelUtils.setNetMeasureUnderrepresentation(network, measure_underrepresentation);
            ModelUtils.setNetNoIEA(network, no_iea);
            ModelUtils.setNetUserThreshold(network, user_threshold.getValue());
        }
        return;
    }

    @ProvidesTitle
    public String getTitle() {
        return "Network-specific settings for gProfiler Enrichment table";
    }

}
