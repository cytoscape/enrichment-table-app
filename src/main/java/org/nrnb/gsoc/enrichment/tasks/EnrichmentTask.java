package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.command.AvailableCommands;
import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskMonitor;
import org.nrnb.gsoc.enrichment.RequestEngine.HTTPRequestEngine;

import java.net.http.HttpResponse;
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
		monitor.setProgress(-1.0);
		//perform activity
		SynchronousTaskManager taskManager = registrar.getService(SynchronousTaskManager.class);
		AvailableCommands commands = registrar.getService(AvailableCommands.class);
		CommandExecutorTaskFactory taskFactory = registrar.getService(CommandExecutorTaskFactory.class);

		Set<String> selectedNodes = new HashSet<>(){{
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
		HTTPRequestEngine requestEngine = new HTTPRequestEngine();
		HttpResponse<String> response = requestEngine.makePostRequest("gost/profile/",parameters);
		StringBuffer responseBuffer = new StringBuffer("");
		if(response!=null)
			responseBuffer.append(response.body());
		System.out.println(responseBuffer.toString());
		System.out.println("Tasks completed");
		monitor.setProgress(1.0);
		return;

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
