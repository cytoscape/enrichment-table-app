package org.nrnb.gsoc.enrichment.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListMultipleSelection;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.utils.CommandTaskUtil;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;
import org.nrnb.gsoc.enrichment.utils.SessionUtils;

public class EnrichmentMapAdvancedTask extends AbstractTask implements TaskObserver{

    private final CyTable filteredEnrichmentTable;
    private final CyNetwork network;
    private final CyTable unfilteredTable;
    private final CyServiceRegistrar registrar;

    private double defaultSimCutoff = 0.8;

    private CyTable customTable;
    private final String geneName = "gene name";
    private final String geneDescription = "gene description";

    // Network name
    @Tunable(description="Enrichment Map name")
    public String mapName = "Enrichment Map - String Network";
    // Similarity cutoff
    @Tunable(description="Connectivity cutoff (Jaccard similarity)",
            longDescription="The cutoff for the lowest Jaccard similarity between terms.  "+
                    "Higher values mean more sparse connectivity while lower "
                    + "values mean more dense network cobnnectivity. ",
            //+ "Good values for disease networks are 0.8 or 0.9.",
            tooltip="<html>The cutoff for the lowest Jaccard similarity of terms. <br /> "+
                    "Higher values mean more sparse connectivity while lower <br /> "
                    + "values mean more dense network cobnnectivity. <br /> "
                    //+ "Good values for disease networks are 0.8 or 0.9."
                    + "</html>", params="slider=true")
    public BoundedDouble similarity = new BoundedDouble(0.0, defaultSimCutoff, 1.0, true, true);


    public EnrichmentMapAdvancedTask(final CyNetwork network, final CyTable filteredEnrichmentTable,
                                     final CyTable originalEnrichmentTable,
                                     boolean filtered,
                                     final CyServiceRegistrar registrar) {
        this.filteredEnrichmentTable = filteredEnrichmentTable;
        this.network = network;
        this.registrar = registrar;
        this.unfilteredTable = originalEnrichmentTable;
        if (filtered) {
            similarity.setBounds(0.0, SessionUtils.getRemoveRedundantCutoff(network, unfilteredTable));
            similarity.setValue(defaultSimCutoff * SessionUtils.getRemoveRedundantCutoff(network, unfilteredTable));
        }
        String netName = network.getRow(network).get(CyNetwork.NAME, String.class);
        this.mapName = "Enrichment Map - " + netName;
    }

    @Override
    public void run(TaskMonitor arg0) throws Exception {
        // build map with arguments
        Map<String, Object> args = new HashMap<>();
        args.put("networkName", mapName);
        args.put("pvalueColumn", EnrichmentTerm.colPvalue);
        args.put("genesColumn", EnrichmentTerm.colGenes);
        args.put("nameColumn", EnrichmentTerm.colName);
        args.put("table","SUID:" + filteredEnrichmentTable.getSUID());
        args.put("coefficients", "JACCARD");
        args.put("similaritycutoff", String.valueOf(similarity.getValue()));
        args.put("pvalue",0.05);

        args.put("descriptionColumn", EnrichmentTerm.colName);

        // run task
        CommandTaskUtil commandTaskUtil = new CommandTaskUtil(registrar);
        insertTasksAfterCurrentTask(commandTaskUtil.getCommandTaskIterator("enrichmentmap",
                "build-table", args, null));

    }

    @ProvidesTitle
    public String getTitle() {
        return "Create EnrichmentMap network";
    }

    @Override
    public void taskFinished(ObservableTask task) {
    }

    @Override
    public void allFinished(FinishStatus arg0) {
    }
}
