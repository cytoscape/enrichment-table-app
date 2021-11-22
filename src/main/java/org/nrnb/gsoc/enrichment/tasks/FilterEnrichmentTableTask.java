package org.nrnb.gsoc.enrichment.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
  private List<TermSource> categoryFilter = TermSource.getValues();


  @Tunable(description = "Select categories",
	         tooltip = "Select the enrichment categories to show in the table",
	         longDescription = "Select the enrichment categories to show in the table. Use \"All\" to remove the filtering.",
	         exampleStringValue = "GO Process",
	         gravity = 1.0)
	public ListMultipleSelection<TermSource> categories = new ListMultipleSelection<>(TermSource.getValues());

	//public boolean removeOverlapping = false;

	//public BoundedDouble overlapCutoff = new BoundedDouble(0.0, 0.5, 1.0, false, false);

	public FilterEnrichmentTableTask(final CyServiceRegistrar registrar, EnrichmentCytoPanel panel) {
    this.registrar = registrar;
		applicationManager = registrar.getService(CyApplicationManager.class);
		this.network = applicationManager.getCurrentNetwork();
		this.enrichmentPanel = panel;
	}



	@Override
	public void run(TaskMonitor monitor) throws Exception {
		monitor.setTitle("Filter Enrichment table");

		List<TermSource> categoryList = categories.getSelectedValues();
    if (enrichmentPanel == null) {
      CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
      CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
      if (cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment") != -1)
        enrichmentPanel = (EnrichmentCytoPanel) cytoPanel.getComponentAt(
            cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));
      else return;
    }
        EnrichmentTableModel tableModel = enrichmentPanel.getTableModel();
        tableModel.filter(categoryList);

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
