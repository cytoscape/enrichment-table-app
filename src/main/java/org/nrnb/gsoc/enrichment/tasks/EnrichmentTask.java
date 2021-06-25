package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class EnrichmentTask extends AbstractTask {
	final CyServiceRegistrar registrar;
	public EnrichmentTask(final CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
	}

	public void run(TaskMonitor monitor) { 

		// Get services from registrar if needed
		
		
		// TODO: Prepare query and make web service call to gProfiler
		System.out.println("Running the enrichment task...");


	}
}
