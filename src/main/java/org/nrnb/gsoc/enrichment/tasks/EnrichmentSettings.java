package org.nrnb.gsoc.enrichment.tasks;


import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.RequestsUIHelper;
import org.cytoscape.work.swing.TunableUIHelper;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListSingleSelection;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * @author ighosh98
 * @description Creates the settings panel
 */
public class EnrichmentSettings implements ActionListener, RequestsUIHelper {
    private final CyApplicationManager applicationManager;
    private CyServiceRegistrar registrar;
    private CyNetwork network;
    private Component parent;

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

    public EnrichmentSettings(CyServiceRegistrar registrar, CyTable nodeTable) {
        this.registrar = registrar;
        applicationManager = registrar.getService(CyApplicationManager.class);
        this.network = applicationManager.getCurrentNetwork();
        geneID = new ListSingleSelection<CyColumn>(ModelUtils.getProfilerColumn(nodeTable));
        scientificNametoID = ModelUtils.getOrganisms();
        organism   = new ListSingleSelection<String>(ModelUtils.getOrganismsName(scientificNametoID));

    }

    public void actionPerformed(ActionEvent e) {

    }

    public void setUIHelper(TunableUIHelper helper) {
        parent = helper.getParent();
    }
}
