package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import java.util.Map;

/**
 * @author ighosh98
 */
public class EnrichmentAdvancedOptionsTaskFactory extends AbstractNetworkTaskFactory {
    final CyServiceRegistrar registrar;
    Map<String,String> scientificNametoID;
    public EnrichmentAdvancedOptionsTaskFactory(CyServiceRegistrar registrar,Map<String,String> scientificNametoID) {
        this.registrar = registrar;
        this.scientificNametoID=scientificNametoID;
    }

    @Override
    public TaskIterator createTaskIterator(CyNetwork network) {
        return new TaskIterator(new EnrichmentAdvancedOptionsTask(registrar,scientificNametoID));
    }
}
