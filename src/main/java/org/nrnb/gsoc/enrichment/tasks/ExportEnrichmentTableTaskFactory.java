package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;

/**
 * @author ighosh98
 */
public class ExportEnrichmentTableTaskFactory extends AbstractNetworkTaskFactory {
    final CyServiceRegistrar registrar;
    final CytoPanel cytoPanel;
    EnrichmentCytoPanel panel;

    public ExportEnrichmentTableTaskFactory(CyServiceRegistrar registrar) {
        this.registrar = registrar;
        CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
        cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
        if (cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment") > 0) {
            panel = (EnrichmentCytoPanel) cytoPanel.getComponentAt(
                    cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));
        }
    }
    public boolean isReady(CyNetwork network) {
        if (ModelUtils.getEnrichmentTables(registrar, network).size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public TaskIterator createTaskIterator(CyNetwork network) {
        return new TaskIterator(new ExportEnrichmentTableTask(registrar, network, panel,
                ModelUtils.getEnrichmentTable(registrar, network, TermSource.ALL.getTable())));
    }

}
