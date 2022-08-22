package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.*;
import org.nrnb.gsoc.enrichment.utils.SessionUtils;

public class EnrichmentSettingsTask extends AbstractTask {

    private CyServiceRegistrar registrar;
    private CyApplicationManager manager;
    private CyNetwork network;
    private final CyTable table;

    @ContainsTunables
    public EnrichmentSettings enrichmentSettings;

    public EnrichmentSettingsTask(CyServiceRegistrar registrar, CyApplicationManager manager, CyTable table) {
        this.network = manager.getCurrentNetwork();
        this.manager = manager;
        this.registrar = registrar;
        this.table = table;
        enrichmentSettings = new EnrichmentSettings(manager, registrar, network, table);
    }

    @Override
    public void run(TaskMonitor arg0) {
        arg0.setTitle("Enrichment settings");
        SessionUtils.setTopTerms(network, table, enrichmentSettings.nTerms.getValue());
        SessionUtils.setEnrichmentPalette(network, table, enrichmentSettings.defaultEnrichmentPalette.getSelectedValue());
        SessionUtils.setChartType(network, table, enrichmentSettings.chartType.getSelectedValue());

        // TODO: maybe this is a way to automatically apply settings?
        TaskManager<?, ?> tm = (TaskManager<?, ?>) registrar.getService(TaskManager.class);
        tm.execute(new ShowChartsTaskFactory(registrar, manager).createTaskIterator());
    }

    @ProvidesTitle
    public String getTitle() {
        return "Network-specific settings for STRING Enrichment table";
    }
}