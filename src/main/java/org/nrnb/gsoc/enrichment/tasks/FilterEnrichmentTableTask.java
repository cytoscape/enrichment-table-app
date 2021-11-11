package org.nrnb.gsoc.enrichment.tasks;

import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.service.util.CyServiceRegistrar;


import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.nrnb.gsoc.enrichment.ui.EnrichmentTableModel;


public class FilterEnrichmentTableTask extends AbstractTask implements ObservableTask {

  private CyApplicationManager applicationManager;
	private EnrichmentCytoPanel enrichmentPanel;
	private CyNetwork network;
	private CyTable filteredEnrichmentTable;
  private CyServiceRegistrar registrar;



	public ListMultipleSelection<TermSource> categories = new ListMultipleSelection<>(TermSource.getValues());


	public boolean removeOverlapping = false;


	public BoundedDouble overlapCutoff = new BoundedDouble(0.0, 0.5, 1.0, false, false);

	public FilterEnrichmentTableTask(final CyServiceRegistrar registrar, EnrichmentCytoPanel panel) {
    this.registrar = registrar;
		applicationManager = registrar.getService(CyApplicationManager.class);
		this.network = applicationManager.getCurrentNetwork();
		this.enrichmentPanel = panel;

	}

	@Override
	public void run(TaskMonitor monitor) throws Exception {
		monitor.setTitle("Filter Enrichment table");

		// Filter the current list
		List<TermSource> categoryList = categories.getSelectedValues();
		//SwingUtilities.invokeLater(new Runnable() {
			//public void run() {
				// when using commands, we need to get the enrichment panel again


        EnrichmentTableModel tableModel = enrichmentPanel.getTableModel();

			//}
		//});
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R> R getResults(Class<? extends R> clzz) {
		if (clzz.equals(String.class)) {
			if (filteredEnrichmentTable != null)
				return (R)("\"EnrichmentTable\": "+filteredEnrichmentTable.getSUID());
			return (R)"";
		} else if (clzz.equals(JSONResult.class)) {
			JSONResult res = () -> {
				if (filteredEnrichmentTable != null)
					return "{\"EnrichmentTable\": "+filteredEnrichmentTable.getSUID()+"}";
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

	@ProvidesTitle
	public String getTitle() {
		return "Filter Enrichment table";
	}
}
