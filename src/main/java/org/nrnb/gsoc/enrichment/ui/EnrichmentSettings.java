package org.nrnb.gsoc.enrichment.ui;


import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.RequestsUIHelper;
import org.cytoscape.work.swing.TunableUIHelper;
import org.cytoscape.work.swing.util.UserAction;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.BoundedInteger;
import org.cytoscape.work.util.ListSingleSelection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

    @Tunable(description = "Make these settings the default",
            longDescription = "Unless this is set to true, these settings only apply to the current network",
            tooltip = "<html>Unless this is set to true, these settings only apply to the current network.</html>")
    public boolean makeDefault = false;

    public EnrichmentSettings(CyServiceRegistrar registrar) {
        this.registrar = registrar;
        applicationManager = registrar.getService(CyApplicationManager.class);
        this.network = applicationManager.getCurrentNetwork();

    }

    public void actionPerformed(ActionEvent e) {

    }

    public void setUIHelper(TunableUIHelper helper) {
        parent = helper.getParent();
    }
}
