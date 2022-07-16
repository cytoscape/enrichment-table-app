package org.nrnb.gsoc.enrichment.utils;

import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskObserver;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;

import java.util.Map;

public class CommandTaskUtil {

    private CommandExecutorTaskFactory commandExecutorTaskFactory;

    public CommandTaskUtil(CyServiceRegistrar registrar) {
        commandExecutorTaskFactory = registrar.getService(CommandExecutorTaskFactory.class);
    }

    public TaskIterator getCommandTaskIterator(String namespace, String command,
                                               Map<String, Object> args, TaskObserver observer) {
        return commandExecutorTaskFactory.createTaskIterator(namespace, command, args, observer);
    }
}
