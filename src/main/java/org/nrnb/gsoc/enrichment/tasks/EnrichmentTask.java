package org.nrnb.gsoc.enrichment.tasks;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.CyUserLog;
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
import org.nrnb.gsoc.enrichment.RequestEngine.ScheduledRequestEngine;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author ighosh98
 * @description Runs the gProfiler task to fetch data and populate the table
 */
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
	Long res;
	private final Logger logger = Logger.getLogger(CyUserLog.NAME);

	@Tunable(description="Organism",context="nogui",required=true,
			longDescription="The organism associated with the query genes, e.g,. hsapiens. List of possible ID-s can be seen at https://biit.cs.ut.ee/gprofiler/page/organism-list",
				exampleStringValue = "hsapiens")
	public String organism;

	@Tunable(description="Gene ID Column",context="nogui",required=true,
					longDescription="The Node Table column containing the gene symbols or identifiers to be queried.",
					exampleStringValue = "name")
	public String geneID;

	@Tunable(description = "Adjusted p-value threshold",context="nogui",
					longDescription = "A float value between 0 and 1, used to define a significance threshold for filtering returned results. Default is 0.05.",
					exampleStringValue = "0.05")
	public String user_threshold;

	@Tunable(description = "Include inferred GO annotations (IEA)",context="nogui",
					longDescription = "The default is false. If true, g:GOSt excludes electronic annotations from GO terms.",
					exampleStringValue = "false")
	public String no_iea;

	@Tunable(description = "Multiple testing correction",context="nogui",
					longDescription = "The following multiple testing correction methods are supported: g_SCS (default), bonferroni and fdr.",
					exampleStringValue = "g_SCS")
	public String significance_threshold_method;

	public ListMultipleSelection<CyNode> nodesToFilterBy;

	final Map<String, String> colSourceMap;
	final Map<String, Long> enrichmentNodesMap;

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
		Long res;
		enrichmentNodesMap = new HashMap<>();
		this.enrichmentPanel = (EnrichmentCytoPanel) enrichmentPanel;
		/**
		 * GO:MF - Gene Ontology Molecular Function branch
		 * GO:BP - Gene Ontology Biological Process branch
		 * GO:CC - Gene Ontology Cellular Component branch
		 * KEGG - KEGG pathways
		 * REAC - Reactome pathways
		 * WP - WikiPathways
		 * TF - Transfac transcription factor binding site predictions
		 * MIRNA - mirTarBase miRNA targets
		 * HPA - Human Protein Atlas expression data
		 * CORUM - Manually annotated protein complexes from mammalian organisms.
		 * HP - Human Phenotype Ontology, a standardized vocabulary of phenotypic abnormalities encountered in human disease.
		 */
		colSourceMap = new HashMap<String, String>(){{
			put("GO:MF","Gene Ontology Molecular Function");
			put("GO:BP","Gene Ontology Biological Process");
			put("GO:CC", "Gene Ontology Cellular Component branch");
			put("KEGG", "KEGG");
			put("WP","WikiPathways");
			put("REAC" ,"Reactome pathways");
			put("TF" ,"Transfac transcription factor binding site predictions");
			put("MIRNA" ,"mirTarBase miRNA targets");
			put("HPA" ,"Human Protein Atlas");
			put("CORUM"," Manually annotated protein complexes from mammalian organisms");
			put("HP","Human Phenotype Ontology");
		}};
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
		enrichmentNodesMap = new HashMap<>();
		/**
		 * GO:MF - Gene Ontology Molecular Function branch
		 * GO:BP - Gene Ontology Biological Process branch
		 * GO:CC - Gene Ontology Cellular Component branch
		 * KEGG - KEGG pathways
		 * REAC - Reactome pathways
		 * WP - WikiPathways
		 * TF - Transfac transcription factor binding site predictions
		 * MIRNA - mirTarBase miRNA targets
		 * HPA - Human Protein Atlas expression data
		 * CORUM - Manually annotated protein complexes from mammalian organisms.
		 * HP - Human Phenotype Ontology, a standardized vocabulary of phenotypic abnormalities encountered in human disease.
		 */
		colSourceMap = new HashMap<String, String>(){{
			put("GO:MF","GO:Molecular Function");
			put("GO:BP","GO:Biological Process");
			put("GO:CC", "GO:Cellular Component");
			put("KEGG", "KEGG");
			put("WP","WikiPathways");
			put("REAC" ,"Reactome");
			put("TF" ,"Transfac");
			put("MIRNA" ,"mirTarBase");
			put("HPA" ,"Human Protein Atlas");
			put("CORUM","CORUM");
			put("HP","Human Phenotype Ontology");
		}};
	}

	public void run(TaskMonitor monitor) {

		// Get services from registrar if needed
		List<CyNode> nodeList;
		Set<String> nodeNameList = new HashSet<String>();
		List<Long> nodesToFilter = new ArrayList<Long>();
		nodeList = nodesToFilterBy.getSelectedValues();
		monitor.setTitle("gProfiler Enrichment Analysis");
		logger.info("Enrichment Task Started");

		if(nodeList.size()>0){
			for (CyNode node : nodeList) {
				nodesToFilter.add(node.getSUID());
				String canonicalName;
				if(ModelUtils.getNetGeneIDColumn(network)==null){
					canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
				} else{
					canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(ModelUtils.getNetGeneIDColumn(network), String.class);
				}
				nodeNameList.add(canonicalName);
				enrichmentNodesMap.put(canonicalName, node.getSUID());
			}
		} else{
			nodeList = network.getNodeList();
			for(CyNode  node:nodeList){
				nodesToFilter.add(node.getSUID());
				String canonicalName;
				if (geneID != null){
					ModelUtils.setNetGeneIDColumn(network,geneID);
					canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(geneID, String.class);
			} else {
				if(ModelUtils.getNetGeneIDColumn(network)==null){
					canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
				} else{
					canonicalName = network.getDefaultNodeTable().getRow(node.getSUID()).get(ModelUtils.getNetGeneIDColumn(network), String.class);
				}
			}
				if(canonicalName!=null && canonicalName.length()>0){
					nodeNameList.add(canonicalName);
					enrichmentNodesMap.put(canonicalName, node.getSUID());
				}
			}
		}

		//ModelUtils.setNetUserThreshold(network,user_threshold);
		//ModelUtils.setNetNoIEA(network, no_iea);
		//ModelUtils.setNetSignificanceThresholdMethod(network,significance_threshold_method);
		/**
		 * @description Check if request query is empty
		 */
		if(nodeNameList.isEmpty()){
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Task cannot be performed. No nodes selected for enrichment.");
			this.noSignificant = true;
		}

		/**
		 * @description Upper limit on the number of nodes that can be queried
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


		Map<String,Object> parameters = generateQuery(query.toString());

		HTTPRequestEngine requestEngine = new HTTPRequestEngine();
		JSONObject result = requestEngine.makePostRequest(network,"gost/profile/",parameters,monitor,nodeList.isEmpty());
		StringBuffer responseBuffer = new StringBuffer("");
		CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
		CyTableFactory tableFactory = registrar.getService(CyTableFactory.class);
		CyTableManager tableManager = registrar.getService(CyTableManager.class);
		enrichmentTable = tableFactory.createTable(TermSource.ALL.getTable(),EnrichmentTerm.colID,Long.class,false, true);
		enrichmentTable.setSavePolicy(SavePolicy.SESSION_FILE);
		tableManager.addTable(enrichmentTable);
		if(result==null){
			res = null;
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Enrichment retrieval returned no results, possibly due to an error.");
			monitor.setStatusMessage("Enrichment retrieval returned no results, due to invalid Query Parameters");
			this.noSignificant = true;

			if(enrichmentPanel==null){
					enrichmentPanel =  new EnrichmentCytoPanel(registrar,noSignificant,result);
				} else{
					enrichmentPanel.initPanel(true);
				}
			monitor.setProgress(1.0);
			return;
		}
		res = enrichmentTable.getSUID();
		responseBuffer.append((result.get("result")).toString());
		System.out.println("GProfiler Response: \n"  +responseBuffer);
		if((responseBuffer.toString()).length()==2){
			monitor.showMessage(TaskMonitor.Level.ERROR,
					"Enrichment retrieval returned no valid results, possibly due to an invalid query request.");
			this.noSignificant = true;

			if(enrichmentPanel==null){
					enrichmentPanel =  new EnrichmentCytoPanel(registrar,noSignificant,result);
				} else{
					enrichmentPanel.initPanel(true);
				}
			monitor.setProgress(1.0);
			return;
		}
		ModelUtils.deleteEnrichmentTables(registrar, network);
		ModelUtils.setupEnrichmentTable(enrichmentTable);

		List<String> nodeNames = new ArrayList<String> (nodeNameList);
		List<EnrichmentTerm> processTerms = ModelUtils.getEnrichmentfromJSON(result,network,nodeNames,enrichmentNodesMap) ;

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
			if(colSourceMap.containsKey(term.getSource()))
				row.set(EnrichmentTerm.colSource,colSourceMap.get(term.getSource()));
			else
				row.set(EnrichmentTerm.colSource,term.getSource());
			row.set(EnrichmentTerm.colTermID,term.getTermID());
			row.set(EnrichmentTerm.colName, term.getName());
			row.set(EnrichmentTerm.colDescription, term.getDescription());
			row.set(EnrichmentTerm.colPvalue, term.getPValue());
			row.set(EnrichmentTerm.colQuerySize,term.getQuerySize());
			row.set(EnrichmentTerm.colEffectiveDomainSize,term.getEffectiveDomainSize());
			row.set(EnrichmentTerm.colTermSize,term.getTermSize());
			row.set(EnrichmentTerm.colIntersectionSize,term.getIntersectionSize());
			row.set(EnrichmentTerm.colPrecision,term.getPrecision());
			row.set(EnrichmentTerm.colRecall,term.getRecall());
			row.set(EnrichmentTerm.colGenes,term.getGenes());
			row.set(EnrichmentTerm.colGenesSUID, term.getNodesSUID());
			row.set(EnrichmentTerm.colNetworkSUID, network.getSUID());
		}
		System.out.println("Enrichment Table Title: " + enrichmentTable.getTitle());
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
		if (cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment") >= 0) {
			int compIndex = cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment");
			Component panel = cytoPanel.getComponentAt(compIndex);
			if (panel instanceof CytoPanelComponent2) {
				registrar.unregisterService(panel, CytoPanelComponent.class);
				registrar.unregisterService(panel, RowsSetListener.class);
				registrar.unregisterService(panel, SelectedNodesAndEdgesListener.class);
				}
		}
		if(show){

		enrichmentPanel.setEnrichmentTable(enrichmentTable);

	registrar.registerService(enrichmentPanel,CytoPanelComponent.class,new Properties());
	registrar.registerService(enrichmentPanel, RowsSetListener.class,new Properties());
	registrar.registerService(enrichmentPanel, SelectedNodesAndEdgesListener.class, new Properties());
	if (cytoPanel.getState() == CytoPanelState.HIDE)
		cytoPanel.setState(CytoPanelState.DOCK);
	cytoPanel.setSelectedIndex(
			cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));
enrichmentPanel.setEnrichmentTable(enrichmentTable);

}

		monitor.setProgress(1.0);
		return;
	}

	/**
	 *  @description generates a basic query structure
	 * @param query field that needs to be added
	 * @return map with all parameters required to fire an API request
	 */
	private Map<String, Object> generateQuery(String query) {
		HashMap<String,Object> parameters = new HashMap<>();
		// TODO: add a box for taking this as an input
		if (organism != null){
			parameters.put("organism",organism);
			ModelUtils.setNetOrganism(network,organism);
	} else {
		if(ModelUtils.getNetOrganism(network)!=null){
			parameters.put("organism", ModelUtils.getNetOrganism(network));
		} else{
			parameters.put("organism","hsapiens");
		}
	}
		if(query==null){
			parameters.put("query","");
 		} else{
			parameters.put("query",query);
		}
		if (ModelUtils.getNetUserThreshold(network)==null && user_threshold == null) {
			ModelUtils.setNetUserThreshold(network,0.05);
		}
		 else if (ModelUtils.getNetUserThreshold(network)!=null && user_threshold == null){
			ModelUtils.setNetUserThreshold(network,ModelUtils.getNetUserThreshold(network));
		} else{
		ModelUtils.setNetUserThreshold(network,Double.parseDouble(user_threshold));
		}

		if (ModelUtils.getNetNoIEA(network)==null && no_iea == null) {
			ModelUtils.setNetNoIEA(network, false);
		}
		else if (ModelUtils.getNetNoIEA(network)!=null && no_iea == null){
			ModelUtils.setNetNoIEA(network,ModelUtils.getNetNoIEA(network));
		} else{
		ModelUtils.setNetNoIEA(network,Boolean.parseBoolean(no_iea));
		}

		if (ModelUtils.getNetSignificanceThresholdMethod(network)==null && significance_threshold_method == null) {
			ModelUtils.setNetSignificanceThresholdMethod(network, "g_SCS");
		}
		 else if (ModelUtils.getNetSignificanceThresholdMethod(network)!=null && significance_threshold_method == null){
			ModelUtils.setNetSignificanceThresholdMethod(network,ModelUtils.getNetSignificanceThresholdMethod(network));
		} else{
		ModelUtils.setNetSignificanceThresholdMethod(network,significance_threshold_method);
		}

		return parameters;
	}

	@Override
    public void cancel() {
        ScheduledRequestEngine.stopPostRequest();
        this.cancelled = true;
    }

	@Override
	@SuppressWarnings("unchecked")
	public Object getResults(Class type) {
			return res;
	}

	@Override
	public List<Class<?>> getResultClasses() {
		return Arrays.asList(JSONResult.class, String.class, Long.class, CyTable.class);
	}
}
