package org.nrnb.gsoc.enrichment.actions;

import static org.nrnb.gsoc.enrichment.utils.IconUtil.APP_ICON_COLORS;
import static org.nrnb.gsoc.enrichment.utils.IconUtil.APP_ICON_LAYERS;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.TextIcon;
import org.nrnb.gsoc.enrichment.utils.IconUtil;

@SuppressWarnings("serial")
public class ShowAppTableAction extends AbstractCyAction {

	private final CyServiceRegistrar serviceRegistrar;

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
		var swingApp = serviceRegistrar.getService(CySwingApplication.class);
		var cytoPanel = swingApp.getCytoPanel(CytoPanelName.SOUTH);

		if (cytoPanel.getState() == CytoPanelState.HIDE)
			cytoPanel.setState(CytoPanelState.DOCK);

		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));
	}
}
