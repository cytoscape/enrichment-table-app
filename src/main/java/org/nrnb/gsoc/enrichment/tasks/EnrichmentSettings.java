package org.nrnb.gsoc.enrichment.tasks;


import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.color.BrewerType;
import org.cytoscape.util.color.Palette;
import org.cytoscape.util.color.PaletteProvider;
import org.cytoscape.util.color.PaletteProviderManager;
import org.cytoscape.util.swing.CyColorPaletteChooser;
import org.cytoscape.util.swing.CyColorPaletteChooserFactory;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.RequestsUIHelper;
import org.cytoscape.work.swing.TunableUIHelper;
import org.cytoscape.work.swing.util.UserAction;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.BoundedInteger;
import org.cytoscape.work.util.ListSingleSelection;
import org.nrnb.gsoc.enrichment.model.ChartType;
import org.nrnb.gsoc.enrichment.utils.SessionUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class EnrichmentSettings implements ActionListener, RequestsUIHelper {
    private final CyServiceRegistrar registrar;
    private final CyApplicationManager manager;
    private final CyNetwork network;
    private Component parent;
    private final CyTable enrichmentTable;

    @Tunable(description = "Type of chart to draw",
            tooltip = "Set the desired chart type",
            longDescription = "Set the desired chart type",
            exampleStringValue = "Split donut",
            groups = {"Enrichment Defaults"},
            gravity = 100.0)
    public ListSingleSelection<ChartType> chartType;

    @Tunable(description = "Number of terms to chart",
            tooltip = "Set the default number of terms to use for charts",
            longDescription = "Set the default number of terms to use for charts",
            exampleStringValue = "5",
            groups = {"Enrichment Defaults"},
            gravity = 101.0, params="slider=true")
    public BoundedInteger nTerms = new BoundedInteger(1, 5, 8, false, false);

    @Tunable(description = "Default Brewer palette for enrichment charts",
            longDescription = "Set the default Brewer palette for enrichment charts",
            exampleStringValue = "ColorBrewer Paired colors",
            groups = {"Enrichment Defaults"},
            gravity = 102.0, context="nogui")
    public ListSingleSelection<Palette> defaultEnrichmentPalette;

    @Tunable(description = "Change enrichment color palette",
            tooltip = "Set the default Brewer color palette for enrichment charts",
            groups = {"Enrichment Defaults"},
            gravity = 103.0, context="gui")
    public UserAction paletteChooserEnrichment = new UserAction(this);

    public EnrichmentSettings(CyApplicationManager manager, CyServiceRegistrar registrar,
                              CyNetwork network, CyTable table) {
        this.manager = manager;
        this.network = network;
        this.registrar = registrar;
        this.enrichmentTable = table;

        PaletteProviderManager pm = registrar.getService(PaletteProviderManager.class);
        List<PaletteProvider> providers = pm.getPaletteProviders(BrewerType.QUALITATIVE, false);
        List<Palette> palettes = new ArrayList<>();
        for (PaletteProvider provider: providers) {
            List<String> paletteList = provider.listPaletteNames(BrewerType.QUALITATIVE, false);
            for (String id: paletteList) {
                palettes.add(provider.getPalette(id));
            }

        }
        // ColorBrewer[] palettes = ColorBrewer.getQualitativeColorPalettes(false);
        defaultEnrichmentPalette = new ListSingleSelection<Palette>(palettes);
        defaultEnrichmentPalette.setSelectedValue(SessionUtils.getEnrichmentPalette(network, enrichmentTable));

        nTerms.setValue(SessionUtils.getTopTerms(network, enrichmentTable));
        chartType = new ListSingleSelection<ChartType>(ChartType.values());
        chartType.setSelectedValue(SessionUtils.getChartType(network, enrichmentTable));
    }

    public void actionPerformed(ActionEvent e) {
        CyColorPaletteChooser paletteChooser =
                registrar.getService(CyColorPaletteChooserFactory.class).getColorPaletteChooser(BrewerType.QUALITATIVE, true);
        Palette palette = paletteChooser.showDialog(parent, "Palette for Enrichment Colors",
                SessionUtils.getEnrichmentPalette(network, enrichmentTable), nTerms.getValue());
        if (palette != null) {
            SessionUtils.setEnrichmentPalette(network, enrichmentTable, palette);
            defaultEnrichmentPalette.setSelectedValue(palette);
        }
    }

    public void setUIHelper(TunableUIHelper helper) {
        parent = helper.getParent();
    }
}
