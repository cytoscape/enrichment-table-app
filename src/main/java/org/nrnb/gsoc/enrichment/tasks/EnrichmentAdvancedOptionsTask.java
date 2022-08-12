package org.nrnb.gsoc.enrichment.tasks;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListSingleSelection;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import java.util.*;

/**
 * @author ighosh98
 * @description Creates the Advanced Options Panel for fine tuning the query parameters
 */
public class EnrichmentAdvancedOptionsTask extends AbstractTask implements ObservableTask{
    final CyServiceRegistrar registrar;
    final CyApplicationManager applicationManager;
    final CyNetwork network;
    final CyTable nodeTable;
    private String displayValue;
    EnrichmentCytoPanel enrichmentPanel=null;
    public CyTable enrichmentTable;

    private static final Logger logger = Logger.getLogger(CyUserLog.NAME);

    @Tunable(description = "Adjusted p-value threshold",
            tooltip = "<html>Values above this threshold will be excluded.</html>",
            longDescription = "A float value between 0 and 1, used to define a significance threshold for filtering returned results. Default value is 0.05.",
            exampleStringValue = "0.05",
            groups = {"Optional settings"},
            params="slider=false", gravity = 109.0)
    public BoundedDouble user_threshold = new BoundedDouble(0.0, 0.05, 1.0, false, false);

    @Tunable(description = "Include inferred GO annotations (IEA)",
            longDescription = "The default (true) is to include inferred electronic annotations from Gene Ontology.",
            groups = {"Optional settings"},
            tooltip = "<html>Uncheck to exclude inferred GO annotations.</html>")
    public boolean no_iea = false;

    @Tunable(description = "Multiple testing correction",
            tooltip = "Select the multiple testing correction method.",
            longDescription = "The following multiple testing correction methods are supported: g_SCS (default), bonferroni and fdr.",
            exampleStringValue = "g_SCS",
            groups = {"Optional settings"},
            gravity = 100.0)
    public ListSingleSelection<String> significance_threshold_method;

    @Tunable(description="Organism",
            tooltip = "The organism associated with the query genes.",
            longDescription="The organism associated with the query genes, e.g,. Homo sapiens.",
            exampleStringValue = "Homo sapiens",
            params="lookup=begins", groups={"Required settings"}, gravity=10.0)
    public ListSingleSelection<String> organism;

    @Tunable(description="Gene ID Column",
            tooltip = "<html>Select the <b>Node Table</b> column with the query genes.</html>",
            longDescription="The Node Table column containing the gene symbols or identifiers to be queried.",
            exampleStringValue = "LABEL",
            params="lookup=begins", groups={"Required settings"}, gravity=10.0)
    public ListSingleSelection<String> geneID;

    public Map<String,String> scientificNametoID;

    public EnrichmentAdvancedOptionsTask(CyServiceRegistrar registrar) {
        this.registrar = registrar;
        this.applicationManager = registrar.getService(CyApplicationManager.class);
        this.network = applicationManager.getCurrentNetwork();
        this.nodeTable = network.getDefaultNodeTable();
        this.setOrganism(network);
        List<String> stringCol = new ArrayList<String>();
        for (CyColumn col : nodeTable.getColumns()) {
            if (col.getType().equals(String.class)) {
                stringCol.add(col.getName());
            }
        }
        geneID = new ListSingleSelection<String>(stringCol);
        if(ModelUtils.getNetGeneIDColumn(network)!=null){
            geneID.setSelectedValue(ModelUtils.getNetGeneIDColumn(network));
        } else{
            geneID.setSelectedValue("name");
        }
        //ModelUtils.setNetGeneIDColumn(network,"name");
        significance_threshold_method = new ListSingleSelection<String>(new ArrayList<String>(){
            {
                add("g_SCS");
                add("bonferroni");
                add("fdr");
            }
        });
        //default value
        significance_threshold_method.setSelectedValue("g_SCS");
        ModelUtils.setNetSignificanceThresholdMethod(network,"g_SCS");
        ModelUtils.setNetUserThreshold(network,0.05);

    }

    //user sets the cycol -> update default -> the run the query
    @Override
    public void run(TaskMonitor monitor) {
        CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
        CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
        enrichmentPanel = (EnrichmentCytoPanel) cytoPanel.getComponentAt(
                cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));
        TaskManager<?, ?> tm = registrar.getService(TaskManager.class);
        if(network!=null) {
            monitor.setTitle("Enrichment Advanced Options");
            // Check if new values selected
            if (!Objects.equals(ModelUtils.getNetOrganism(network), scientificNametoID.get(organism.getSelectedValue()))) {
                logger.info("[Enrichment Map] Selected new organism is: [" + scientificNametoID.get(organism.getSelectedValue()) + "]");
            }

            if (!Objects.equals(ModelUtils.getNetGeneIDColumn(network), geneID.getSelectedValue())) {
                logger.info("[Enrichment Map] Selected new gene is: [" + geneID.getSelectedValue() + "]");
            }

            if (!Objects.equals(ModelUtils.getNetUserThreshold(network), user_threshold.getValue())) {
                logger.info("[Enrichment Map] Selected new user threshold is: [" + user_threshold.getValue() + "]");
            }

            if (!Objects.equals(ModelUtils.getNetNoIEA(network), no_iea)) {
                logger.info("[Enrichment Map] Selected new IEA is: [" + no_iea + "]");
            }

            if (!Objects.equals(ModelUtils.getNetSignificanceThresholdMethod(network),
                    significance_threshold_method.getSelectedValue())) {
                logger.info("[Enrichment Map] Selected new significance threshold is: [" +
                        significance_threshold_method.getSelectedValue() + "]");
            }

            // save values to network
            ModelUtils.setNetSignificanceThresholdMethod(network, significance_threshold_method.getSelectedValue());
            ModelUtils.setNetGeneIDColumn(network, geneID.getSelectedValue());
            ModelUtils.setNetNoIEA(network, no_iea);
            ModelUtils.setNetUserThreshold(network, user_threshold.getValue());
            if(scientificNametoID.containsKey(organism.getSelectedValue())){
                System.out.println("Organism selected value: " + organism.getSelectedValue());
                ModelUtils.setNetOrganism(network,scientificNametoID.get(organism.getSelectedValue()));
                tm.execute(new TaskIterator(new EnrichmentTask(registrar, enrichmentPanel)));
            } else{
                monitor.setStatusMessage("Could not find organism. Please select one of the supported organisms.");
            }
            enrichmentTable = ModelUtils.getEnrichmentTable(registrar, network, TermSource.ALL.getTable());
        }
    }

    private void setOrganism(final CyNetwork network) {
        // Setting values to list selection
        this.scientificNametoID = ModelUtils.getOrganisms();
        List<String> speciesList = new ArrayList<>();
        String currentOrganism = ModelUtils.getNetOrganism(network);
        displayValue = currentOrganism;
        System.out.println("Display Value: " + displayValue);

        if (scientificNametoID != null) {
            for (Map.Entry<String, String> it : scientificNametoID.entrySet()) {
                speciesList.add(it.getKey());
                if (it.getValue().equals(currentOrganism)) {
                    displayValue = it.getKey();
                }
            }
            Collections.sort(speciesList);
            organism = new ListSingleSelection<>(speciesList);
        }

        // If the network table already has organism
        if (speciesList.contains(displayValue)) organism.setSelectedValue(displayValue);
    }

    @ProvidesTitle
    public String getTitle() {
        return "Enrichment Settings";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getResults(Class type) {
        return enrichmentTable.getSUID();
    }

    @Override
    public List<Class<?>> getResultClasses() {
        return Arrays.asList(JSONResult.class, String.class, Long.class, CyTable.class);
    }
}
