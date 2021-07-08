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


    @Tunable(description = "Number of terms to chart",
            tooltip = "Set the default number of terms to use for charts",
            longDescription = "Set the default number of terms to use for charts",
            exampleStringValue = "5",
            groups = {"Enrichment Defaults"},
            gravity = 101.0, params="slider=true")
    public BoundedInteger nTerms = new BoundedInteger(1, 5, 8, false, false);

    @Tunable(description = "Overlap cutoff",
            tooltip = "<html>This is the maximum Jaccard similarity that will be allowed.<br/>"+
                    "Values larger than this cutoff will be excluded.</html>",
            longDescription = "This is the maximum Jaccard similarity that will be allowed."+
                    "Values larger than this cutoff will be excluded.",
            exampleStringValue = "0.5",
            groups = {"Enrichment Defaults"},
            params="slider=true", gravity = 109.0)
    public BoundedDouble overlapCutoff = new BoundedDouble(0.0, 0.5, 1.0, false, false);

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
