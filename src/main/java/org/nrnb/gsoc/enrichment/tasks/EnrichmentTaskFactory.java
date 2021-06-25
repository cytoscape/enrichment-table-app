package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

import org.nrnb.gsoc.enrichment.tasks.EnrichmentTask;

public class EnrichmentTaskFactory extends AbstractTaskFactory {
	final CyServiceRegistrar registrar;
	
	public EnrichmentTaskFactory(final CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}

	public TaskIterator createTaskIterator () {
		return new TaskIterator(
			new EnrichmentTask(registrar));
	}

	public boolean isReady() { return true; }
}