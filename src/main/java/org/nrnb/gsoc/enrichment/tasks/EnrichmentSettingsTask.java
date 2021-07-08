package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.*;
import org.nrnb.gsoc.enrichment.ui.EnrichmentSettings;

public class EnrichmentSettingsTask extends AbstractTask {
    final CyServiceRegistrar registrar;
    @ContainsTunables
    public EnrichmentSettings enrichmentSettings;

    @Tunable(description = "Make these settings the default",
            longDescription = "Unless this is set to true, these settings only apply to the current network",
            tooltip = "<html>Unless this is set to true, these settings only apply to the current network.</html>")
    public boolean makeDefault = false;

    public EnrichmentSettingsTask(CyServiceRegistrar registrar) {
        this.registrar = registrar;
        enrichmentSettings = new EnrichmentSettings(registrar);


    }
    @Override
    public void run(TaskMonitor arg0) throws Exception {
        arg0.setTitle("Enrichment settings");
        // TODO: Implement scenario where values must be options value must be stored as default

        // TODO: maybe this is a way to automatically apply settings?
        TaskManager<?, ?> tm = (TaskManager<?, ?>) registrar.getService(TaskManager.class);
    }

    @ProvidesTitle
    public String getTitle() {
        return "Network-specific settings for gProfiler Enrichment table";
    }

}
