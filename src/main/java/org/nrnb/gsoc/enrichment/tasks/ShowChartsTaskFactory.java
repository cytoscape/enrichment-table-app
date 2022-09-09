package org.nrnb.gsoc.enrichment.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;

/**
 * Task Factory to start show chart task.
 *
 * @author <a href="https://github.com/AkMo3">Akash Mondal</a>
 */
public class ShowChartsTaskFactory extends AbstractTaskFactory {
    private final CyApplicationManager manager;
    private final CytoPanel cytoPanel;
    private EnrichmentCytoPanel panel;

    public ShowChartsTaskFactory(final CyServiceRegistrar registrar, final CyApplicationManager manager) {
        this.manager = manager;
        CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
        cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
    }

    public TaskIterator createTaskIterator() {
        if (!isReady())
            return new TaskIterator();
        return new TaskIterator(new ShowChartsTask(panel));
    }

    public boolean isReady() {
        CyNetwork net = manager.getCurrentNetwork();
        if (net == null)
            return false;

        if (cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment") < 0) {
            return false;
        }

        panel = (EnrichmentCytoPanel) cytoPanel.getComponentAt(
                cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));

        return panel != null;
    }
}

