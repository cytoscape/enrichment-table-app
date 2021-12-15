package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.*;
import org.cytoscape.application.swing.*;
import org.cytoscape.model.*;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;
import org.nrnb.gsoc.enrichment.tasks.EnrichmentTask;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.json.JSONResult;


import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.TaskIterator;

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
    String displayValue;
    EnrichmentCytoPanel enrichmentPanel=null;
    public CyTable enrichmentTable;


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
        applicationManager = registrar.getService(CyApplicationManager.class);
        this.network = applicationManager.getCurrentNetwork();
        nodeTable = network.getDefaultNodeTable();
        this.scientificNametoID = ModelUtils.getOrganisms();
        List<String> speciesList = new ArrayList<>();
        this.enrichmentPanel = (EnrichmentCytoPanel) enrichmentPanel;
        if(scientificNametoID!=null) {
            for (Map.Entry<String, String> it : scientificNametoID.entrySet()) {
                speciesList.add(it.getKey());
                if(it.getValue().equals(ModelUtils.getNetOrganism(network))){
                  displayValue = it.getKey();
                }
            }
            organism = new ListSingleSelection<String>(speciesList);
            if(ModelUtils.getNetOrganism(network)!=null){
              organism.setSelectedValue(displayValue);
            } else{
              organism.setSelectedValue("Homo sapiens");
            }
            //ModelUtils.setNetOrganism(network,"hsapiens");
        }
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
    public void run(TaskMonitor monitor) throws Exception {
        CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
        CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
        enrichmentPanel = (EnrichmentCytoPanel) cytoPanel.getComponentAt(
      								cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));
        TaskManager<?, ?> tm = registrar.getService(TaskManager.class);
        if(network!=null) {
            // save values to network
            ModelUtils.setNetSignificanceThresholdMethod(network, significance_threshold_method.getSelectedValue());
            ModelUtils.setNetGeneIDColumn(network, geneID.getSelectedValue().toString());
            ModelUtils.setNetNoIEA(network, no_iea);
            ModelUtils.setNetUserThreshold(network, user_threshold.getValue());
            if(scientificNametoID.containsKey(organism.getSelectedValue())){
                //System.out.println(organism.getSelectedValue());
                ModelUtils.setNetOrganism(network,scientificNametoID.get(organism.getSelectedValue()));
                tm.execute(new TaskIterator(new EnrichmentTask(registrar, enrichmentPanel)));
            } else{
                monitor.setStatusMessage("Could not find organism. Please select one of the supported organisms.");
            }
            enrichmentTable = ModelUtils.getEnrichmentTable(registrar, network, TermSource.ALL.getTable());
        }
        return;
    }

    @ProvidesTitle
    public String getTitle() {
        return "Enrichment Settings";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getResults(Class type) {
        Long res = enrichmentTable.getSUID();
        return res;
    }

    @Override
    public List<Class<?>> getResultClasses() {
      return Arrays.asList(JSONResult.class, String.class, Long.class, CyTable.class);
    }
  }
