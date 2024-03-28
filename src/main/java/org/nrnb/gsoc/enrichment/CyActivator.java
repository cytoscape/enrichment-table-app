package org.nrnb.gsoc.enrichment;

import java.util.Properties;

import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.SelectedNodesAndEdgesListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionLoadedListener;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.nrnb.gsoc.enrichment.actions.ShowAppTableAction;
import org.nrnb.gsoc.enrichment.tasks.EnrichmentTaskFactory;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.osgi.framework.BundleContext;

/**
 * @author ighosh98
 * {@code CyActivator} is a class that is a starting point for OSGi bundles.
 *
 * A quick overview of OSGi: The common currency of OSGi is the <i>service</i>.
 * A service is merely a Java interface, along with objects that implement the
 * interface. OSGi establishes a system of <i>bundles</i>. Most bundles import
 * services. Some bundles export services. Some do both. When a bundle exports a
 * service, it provides an implementation to the service's interface. Bundles
 * import a service by asking OSGi for an implementation. The implementation is
 * provided by some other bundle.
 *
 * When OSGi starts your bundle, it will invoke {@CyActivator}'s
 * {@code start} method. So, the {@code start} method is where
 * you put in all your code that sets up your app. This is where you import and
 * export services.
 *
 * Your bundle's {@code Bundle-Activator} manifest entry has a fully-qualified
 * path to this class. It's not necessary to inherit from
 * {@code AbstractCyActivator}. However, we provide this class as a convenience
 * to make it easier to work with OSGi.
 *
 * Note: AbstractCyActivator already provides its own {@code stop} method, which
 * {@code unget}s any services we fetch using getService().
 */
public class CyActivator extends AbstractCyActivator {
	/**
	 * This is the {@code start} method, which sets up your app. The
	 * {@code BundleContext} object allows you to communicate with the OSGi
	 * environment. You use {@code BundleContext} to import services or ask OSGi
	 * about the status of some service.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		// Get the services we're going to want to use
		CyServiceRegistrar registrar = getService(context, CyServiceRegistrar.class);

		// Configure the service properties first.
		Properties properties = new Properties();
		properties.put(ServiceProperties.PREFERRED_MENU, "Tools.Enrichment Table");
		properties.put(ServiceProperties.TITLE, "Perform Gene Enrichment");
		properties.put(ServiceProperties.COMMAND_NAMESPACE, "enrichment");
		properties.put(ServiceProperties.COMMAND, "analysis");


		CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
		CytoPanelComponent2 enrichmentPanel =  new EnrichmentCytoPanel(registrar,false,null);
		registrar.registerService(enrichmentPanel, SelectedNodesAndEdgesListener.class, new Properties());
		registrar.registerService(enrichmentPanel, NetworkAboutToBeDestroyedListener.class, new Properties());
		registrar.registerService(enrichmentPanel, SessionLoadedListener.class, new Properties());
		registrar.registerService(enrichmentPanel, SetCurrentNetworkListener.class, new Properties());
		//registrar.registerService(enrichmentPanel, SetCurrentNetworkListener.class, new Properties());
		//registrar.registerService(enrichmentPanel, NetworkAddedListener.class, new Properties());
		
		ShowAppTableAction showAppTableAction = new ShowAppTableAction(registrar);
		registerService(context, showAppTableAction, CyAction.class);

		if (cytoPanel.getState() == CytoPanelState.HIDE)
			cytoPanel.setState(CytoPanelState.DOCK);
			cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent("org.cytoscape.NodeTables"));
			TaskFactory myFactory = new EnrichmentTaskFactory(registrar,enrichmentPanel); // Implementation
			registerService(context, myFactory,
			TaskFactory.class, // Interface
			properties); // Service properties

	}
}
