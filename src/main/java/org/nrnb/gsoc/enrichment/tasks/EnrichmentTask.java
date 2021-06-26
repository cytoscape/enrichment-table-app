package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import java.util.*;

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
		Set<String> selectedNodes = new HashSet<String>(){{
			add("CASQ2");
			add("CASQ1");
			add("GSTO1");
			add("DMD");
			add("GSTM2");
		}};

		selectedNodes.add("Homo sapiens");
		StringBuffer query = new StringBuffer("");

		Iterator<String> setIterator = selectedNodes.iterator();
		query.append("\"");
		while(setIterator.hasNext()){
			query.append(setIterator.next());
			query.append(" ");
		}
		query.append("\"");
		Map<String,String> parameters = generateQuery(query.toString());
		StringBuffer responseBuffer = new StringBuffer("");
		System.out.println(responseBuffer.toString());
		System.out.println("Tasks completed");
		System.out.println("Task output");
		monitor.setProgress(1.0);
	}

	private Map<String, String> generateQuery(String query) {
		HashMap<String,String> parameters = new HashMap<>();
		parameters.put("organism","hsapiens");
		parameters.put("query",query);
		return parameters;
	}

	public void cancel() {
		this.cancelled = true;
	}
}