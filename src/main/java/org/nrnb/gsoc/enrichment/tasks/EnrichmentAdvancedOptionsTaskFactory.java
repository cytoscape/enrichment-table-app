package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


/**
 * @author ighosh98
 */
public class EnrichmentAdvancedOptionsTaskFactory extends AbstractTaskFactory {
    final CyServiceRegistrar registrar;
    public EnrichmentAdvancedOptionsTaskFactory(CyServiceRegistrar registrar) {
        this.registrar = registrar;
    }

    public TaskIterator createTaskIterator() {
        return new TaskIterator(new EnrichmentAdvancedOptionsTask(registrar));
    }
}
