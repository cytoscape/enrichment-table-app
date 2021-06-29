package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.nrnb.gsoc.enrichment.RequestEngine.HTTPRequestEngine;

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
			add("MLXIPL");
			add("SMARCB1");
			add("PIH1D1");
			add("SMARCA4");
			add("AGER");
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
		HTTPRequestEngine requestEngine = new HTTPRequestEngine();
		List<String> response = requestEngine.makePostRequest("gost/profile/",parameters,monitor);
		StringBuffer responseBuffer = new StringBuffer("");
		for(String it : response){
			responseBuffer.append(it);
		}
		System.out.println(responseBuffer.toString());
		System.out.println("Tasks completed");
		System.out.println("Task output");
		monitor.setProgress(1.0);
	}

	private Map<String, String> generateQuery(String query) {
		HashMap<String,String> parameters = new HashMap<>();
		System.out.println(query);
		parameters.put("organism","hsapiens");
		parameters.put("query",query);
		return parameters;
	}

	public void cancel() {
		this.cancelled = true;
	}
}