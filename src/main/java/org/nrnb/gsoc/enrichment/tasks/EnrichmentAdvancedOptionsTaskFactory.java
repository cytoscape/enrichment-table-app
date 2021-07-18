package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * @author ighosh98
 */
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
