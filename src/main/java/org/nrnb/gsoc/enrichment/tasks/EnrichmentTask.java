package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.*;
import org.cytoscape.model.*;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.ListMultipleSelection;
import org.json.simple.JSONObject;
import org.nrnb.gsoc.enrichment.RequestEngine.HTTPRequestEngine;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

import java.util.*;

public class EnrichmentTask extends AbstractTask implements ObservableTask {
	final CyServiceRegistrar registrar;
	final CyApplicationManager applicationManager;
	final CyNetwork network;
	private boolean noSignificant;
	final CyNetworkView networkView;
	private static int MAX_NUMBER_OF_NODES = 2000;
	private boolean isLargeNetwork;
	public CyTable enrichmentTable = null;
	EnrichmentCytoPanel enrichmentPanel=null;
	private boolean show = true;

	@Tunable(description = "Select nodes",
			context = "nogui",
			//tooltip = "Select the enrichment categories to show in the table",
			//longDescription = "Select the enrichment categories to show in the table",
			//exampleStringValue = "GO Process",
			gravity = 1.0)
	public ListMultipleSelection<CyNode> nodesToFilterBy;

	final Map<String, Long> stringNodesMap;

	public EnrichmentTask(final CyServiceRegistrar registrar, CytoPanelComponent2 enrichmentPanel) {
		super();
		this.noSignificant = false;
		this.registrar = registrar;
		applicationManager = registrar.getService(CyApplicationManager.class);
		this.network = applicationManager.getCurrentNetwork();
		this.networkView = applicationManager.getCurrentNetworkView();
		nodesToFilterBy = new ListMultipleSelection<CyNode>(network.getNodeList());
		nodesToFilterBy.setSelectedValues(CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true));
		isLargeNetwork = false;
		stringNodesMap = new HashMap<>();
		this.enrichmentPanel = (EnrichmentCytoPanel) enrichmentPanel;
	}

	public EnrichmentTask(final CyServiceRegistrar registrar) {
		super();
		this.noSignificant = false;
		this.registrar = registrar;
		applicationManager = registrar.getService(CyApplicationManager.class);
		this.network = applicationManager.getCurrentNetwork();
		this.networkView = applicationManager.getCurrentNetworkView();
		nodesToFilterBy = new ListMultipleSelection<CyNode>(network.getNodeList());
		nodesToFilterBy.setSelectedValues(CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true));
		isLargeNetwork = false;
		stringNodesMap = new HashMap<>();
	}

	public void run(TaskMonitor monitor) {
		// Get services from registrar if needed
		System.out.println("Running the enrichment task...");
		List<CyNode> nodeList;
		Set<String> nodeNameList = new HashSet<String>();
		List<Long> nodesToFilter = new ArrayList<Long>();
		nodeList = nodesToFilterBy.getSelectedValues();
		if(nodeList.size()>0){
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
				if(canonicalName!=null){
					stringNodesMap.put(canonicalName, node.getSUID());
				}
			}
		}

		/**
		 * Check if request query is empty
		 */
		if(nodeNameList.isEmpty()){
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Task cannot be performed. No nodes selected for enrichment.");
			System.out.println("Task cannot be performed. No nodes selected for enrichment.");
			this.noSignificant = true;
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

		StringBuffer query = new StringBuffer("");
		Iterator<String> setIterator = nodeNameList.iterator();
		while(setIterator.hasNext()){
			query.append(setIterator.next());
			if(setIterator.hasNext()){
				query.append(" ");
			}
		}


		Map<String,String> parameters = generateQuery(query.toString());
		HTTPRequestEngine requestEngine = new HTTPRequestEngine();
		JSONObject result = requestEngine.makePostRequest("gost/profile/",parameters,monitor);
		StringBuffer responseBuffer = new StringBuffer("");
		if(result==null){
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Enrichment retrieval returned no results, possibly due to an error.");
			this.noSignificant = true;
			monitor.setProgress(1.0);
			return;
		}
		responseBuffer.append((result.get("result")).toString());
		if((responseBuffer.toString()).length()==2){
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Enrichment retrieval returned no valid results, possibly due to an invalid query request.");
			this.noSignificant = true;
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
		CyTableFactory tableFactory = registrar.getService(CyTableFactory.class);
		CyTableManager tableManager = registrar.getService(CyTableManager.class);
		enrichmentTable = tableFactory.createTable("Enrichment Results",EnrichmentTerm.colTermID,Long.class,false, true);
		enrichmentTable.setSavePolicy(SavePolicy.SESSION_FILE);
		tableManager.addTable(enrichmentTable);
		ModelUtils.setupEnrichmentTable(enrichmentTable);
		List<String> nodeNames = new ArrayList<String> (nodeNameList);
		List<EnrichmentTerm> processTerms = ModelUtils.getEnrichmentfromJSON(result,network,nodeNames,stringNodesMap) ;

		// populate table with data
		if(processTerms==null){
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Enrichment retrieval returned no valid results, possibly due to an invalid query request.");
			return;
		}
		if(processTerms.size()==0){
			CyRow row = enrichmentTable.getRow((long) 0);
			row.set(EnrichmentTerm.colNetworkSUID, network.getSUID());
		}

		//populate the result table
		for(int i=0;i<processTerms.size();i++){
			// populate all other values that need to be entered into the table
			EnrichmentTerm term = processTerms.get(i);
			CyRow row = enrichmentTable.getRow((long) i);
			row.set(EnrichmentTerm.colName, term.getName());
			row.set(EnrichmentTerm.colDescription, term.getDescription());
			row.set(EnrichmentTerm.colPvalue, term.getPValue());
			row.set(EnrichmentTerm.colChartColor, "");
			row.set(EnrichmentTerm.colGenes,term.getGenes());
		}
		System.out.println(enrichmentTable.getTitle());
		CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
		/**
		 * Check if we already show the cytopanel or not
		 */
		if(show){
			monitor.setStatusMessage("Show enrichment panel");
			System.out.println("Show enrichment panel");
			if(enrichmentPanel==null){
				enrichmentPanel =  new EnrichmentCytoPanel(registrar,noSignificant,enrichmentTable,result);
			} else{
				enrichmentPanel.setEnrichmentTable(enrichmentTable);
			}
			registrar.registerService(enrichmentPanel,CytoPanelComponent.class,new Properties());
			registrar.registerService(enrichmentPanel, RowsSetListener.class,new Properties());
			registrar.registerService(enrichmentPanel, SelectedNodesAndEdgesListener.class, new Properties());
			if (cytoPanel.getState() == CytoPanelState.HIDE)
				cytoPanel.setState(CytoPanelState.DOCK);
			cytoPanel.setSelectedIndex(
					cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));

		}
		monitor.setProgress(1.0);
		return;
	}

	private Map<String, String> generateQuery(String query) {
		HashMap<String,String> parameters = new HashMap<>();
		System.out.println(query);
		// TODO: add a box for taking this as an input
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