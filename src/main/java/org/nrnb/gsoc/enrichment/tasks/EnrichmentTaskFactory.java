package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


public class EnrichmentTaskFactory extends AbstractTaskFactory {

	final CyServiceRegistrar registrar;
	final CyNetworkManager netManager;

	public EnrichmentTaskFactory(final CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
		netManager = registrar.getService(CyNetworkManager.class);
	}

	public TaskIterator createTaskIterator () {
		return new TaskIterator(
			new EnrichmentTask(registrar));
	}

	public boolean isReady() {
		// check if the network exists
		if(netManager.getNetworkSet()!=null && netManager.getNetworkSet().size()>0){
			return true;
		}
		//network does not exist
		return false;
	}
}