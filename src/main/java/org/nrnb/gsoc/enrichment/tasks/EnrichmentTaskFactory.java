package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;

/**
 * @author ighosh98
 */
public class EnrichmentTaskFactory extends AbstractTaskFactory {

	final CyServiceRegistrar registrar;
	final CyNetworkManager netManager;
	final CytoPanelComponent2 enrichmentPanel;
	public EnrichmentTaskFactory(final CyServiceRegistrar registrar, CytoPanelComponent2 enrichmentPanel) {
		super();
		this.registrar = registrar;
		netManager = registrar.getService(CyNetworkManager.class);
		this.enrichmentPanel = enrichmentPanel;
	}

	public TaskIterator createTaskIterator () {
		return new TaskIterator(
			new EnrichmentTask(registrar,this.enrichmentPanel));
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