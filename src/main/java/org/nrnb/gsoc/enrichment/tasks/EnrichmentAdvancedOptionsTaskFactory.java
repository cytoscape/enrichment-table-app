package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class EnrichmentAdvancedOptionsTaskFactory extends AbstractNetworkTaskFactory {
    final CyServiceRegistrar registrar;

    public EnrichmentAdvancedOptionsTaskFactory(CyServiceRegistrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public TaskIterator createTaskIterator(CyNetwork network) {
        return new TaskIterator(new EnrichmentAdvancedOptionsTask(registrar));
    }
}
