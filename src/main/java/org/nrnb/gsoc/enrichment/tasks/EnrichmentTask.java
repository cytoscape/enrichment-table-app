package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.ListMultipleSelection;
import org.nrnb.gsoc.enrichment.RequestEngine.HTTPRequestEngine;

import java.util.*;

public class EnrichmentTask extends AbstractTask implements ObservableTask {
	final CyServiceRegistrar registrar;
	final CyApplicationManager applicationManager;
	final CyNetwork network;
	final CyNetworkView networkView;
	private static int MAX_NUMBER_OF_NODES = 2000;
	private boolean isLargeNetwork;
	@Tunable(description = "Select nodes",
			context = "nogui",
			//tooltip = "Select the enrichment categories to show in the table",
			//longDescription = "Select the enrichment categories to show in the table",
			//exampleStringValue = "GO Process",
			gravity = 1.0)
	public ListMultipleSelection<CyNode> nodesToFilterBy;

	@Tunable(description = "Query will run only for selected nodes",
			longDescription="By default, a query run for all nodes in the network",
			exampleStringValue="false")
	public boolean checkSelectedNodes = false;
	// store the value as a property?

	public EnrichmentTask(final CyServiceRegistrar registrar) {
		super();
		this.registrar = registrar;
		applicationManager = registrar.getService(CyApplicationManager.class);
		this.network = applicationManager.getCurrentNetwork();
		this.networkView = applicationManager.getCurrentNetworkView();
		nodesToFilterBy = new ListMultipleSelection<CyNode>(network.getNodeList());
		nodesToFilterBy.setSelectedValues(CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true));
		isLargeNetwork = false;
	}

	public void run(TaskMonitor monitor) {
		// Get services from registrar if needed
		System.out.println("Running the enrichment task...");
		List<CyNode> nodeList;
		Set<String> nodeNameList = new HashSet<String>();
		List<Long> nodesToFilter = new ArrayList<Long>();
		if(this.checkSelectedNodes){
			nodeList = nodesToFilterBy.getSelectedValues();
			for (CyNode node : nodeList) {
				nodesToFilter.add(node.getSUID());
				String canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
				nodeNameList.add(canonicalName);
			}
		} else{
			nodeList = network.getNodeList();
			for(CyNode  node:nodeList){
				nodesToFilter.add(node.getSUID());
				String canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
				nodeNameList.add(canonicalName);
			}
		}
		/**
		 * Check if request query is empty
		 */
		if(nodeNameList.isEmpty()){
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Task cannot be performed. No nodes selected for enrichment.");
			System.out.println("Task cannot be performed. No nodes selected for enrichment.");
		}

		/**
		 * Upper limit on the number of nodes that can be queried
		 */
		if(nodeNameList.size()>MAX_NUMBER_OF_NODES){
			isLargeNetwork = true;
			monitor.setStatusMessage("Cannot run query as size of query is too large");
			monitor.setProgress(1.0);
			return;
		}

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

		Iterator<String> setIterator = nodeNameList.iterator();
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
		if((responseBuffer.toString()).length()==0){
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Enrichment retrieval returned no results, possibly due to an error.");
			monitor.setProgress(1.0);
			return;
		}
		System.out.println(responseBuffer.toString());
		System.out.println("Tasks completed");
		System.out.println("Task output");
		System.out.println(nodeNameList.size());
		for(String node : nodeNameList){
			System.out.print(node+" ");
		}
		monitor.setProgress(1.0);
		return;
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

	@Override
	@SuppressWarnings("unchecked")
	public <R> R getResults(Class<? extends R> clzz) {
		if (clzz.equals(String.class)) {
			return (R)"";
		} else if (clzz.equals(JSONResult.class)) {
			JSONResult res = () -> {
				return "{}";
			};
			return (R)res;
		}
		return null;
	}

	@Override
	public List<Class<?>> getResultClasses() {
		return Arrays.asList(JSONResult.class, String.class);
	}
}