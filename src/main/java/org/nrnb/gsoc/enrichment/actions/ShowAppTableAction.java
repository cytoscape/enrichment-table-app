package org.nrnb.gsoc.enrichment.actions;

import static org.nrnb.gsoc.enrichment.utils.IconUtil.APP_ICON_COLORS;
import static org.nrnb.gsoc.enrichment.utils.IconUtil.APP_ICON_LAYERS;

import java.awt.event.ActionEvent;
import java.util.Properties;

import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.util.swing.TextIcon;
import org.cytoscape.work.TaskFactory;
import org.nrnb.gsoc.enrichment.tasks.EnrichmentTaskFactory;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.nrnb.gsoc.enrichment.utils.IconUtil;

@SuppressWarnings("serial")
public class ShowAppTableAction extends AbstractCyAction {

	private final CyServiceRegistrar serviceRegistrar;
	CytoPanelComponent2 enrichmentPanel;

	public ShowAppTableAction(CyServiceRegistrar serviceRegistrar) {
		super("Enrichment Table");
		this.serviceRegistrar = serviceRegistrar;

		inToolBar = true;
		insertToolbarSeparatorBefore = true;
		toolbarGravity = 11.0f;
		
		var iconFont = IconUtil.getIconFont(30.0f);
		var icon = new TextIcon(APP_ICON_LAYERS, iconFont, APP_ICON_COLORS, 32, 32, 1);
		
		putValue(SHORT_DESCRIPTION, "Show Enrichment Table"); // Tooltip's short description
		putValue(LARGE_ICON_KEY, icon);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CySwingApplication swingApplication = serviceRegistrar.getService(CySwingApplication.class);
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
		if (enrichmentPanel == null){
		enrichmentPanel =  new EnrichmentCytoPanel(serviceRegistrar,false,null);
		serviceRegistrar.registerService(enrichmentPanel, CytoPanelComponent.class,new Properties());
		serviceRegistrar.registerService(enrichmentPanel, SelectedNodesAndEdgesListener.class, new Properties());
		serviceRegistrar.registerService(enrichmentPanel, NetworkAboutToBeDestroyedListener.class, new Properties());
		serviceRegistrar.registerService(enrichmentPanel, SessionLoadedListener.class, new Properties());
		serviceRegistrar.registerService(enrichmentPanel, SetCurrentNetworkListener.class, new Properties());
		//registrar.registerService(enrichmentPanel, SetCurrentNetworkListener.class, new Properties());
		//registrar.registerService(enrichmentPanel, NetworkAddedListener.class, new Properties());
		if (cytoPanel.getState() == CytoPanelState.HIDE)
		cytoPanel.setState(CytoPanelState.DOCK);
	cytoPanel.setSelectedIndex(
			cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));

		} else {
			if (cytoPanel.getState() == CytoPanelState.HIDE)
			cytoPanel.setState(CytoPanelState.DOCK);
		cytoPanel.setSelectedIndex(
				cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));

		}	
	}
}
