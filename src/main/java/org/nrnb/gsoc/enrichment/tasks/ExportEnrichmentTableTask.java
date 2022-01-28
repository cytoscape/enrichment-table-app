package org.nrnb.gsoc.enrichment.tasks;


import java.io.File;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.write.ExportTableTaskFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;


import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;

/**
 * @author ighosh98
 * @description Allows users to save the file to a given location with a specific file name
 */
public class ExportEnrichmentTableTask extends AbstractTask {

    private EnrichmentCytoPanel enrichmentPanel;
    private CyTable selectedTable;

    @Tunable(description = "Save Table as", params = "input=false",
            tooltip="<html>Note: for convenience spaces are replaced by underscores.</html>", gravity = 2.0)
    public File prefix = null;
    final CyServiceRegistrar registrar;
    final CyNetwork network;

    public ExportEnrichmentTableTask(CyServiceRegistrar registrar, CyNetwork network, EnrichmentCytoPanel panel, CyTable table) {
        this.registrar = registrar;
        this.enrichmentPanel = panel;
        this.selectedTable = table;
        this.network = network;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Export Enrichment table");
        ExportTableTaskFactory exportTF = registrar.getService(ExportTableTaskFactory.class);
        if(network==null){
            return;
        }
        if (selectedTable != null && prefix != null) {
            File file = new File(prefix.getAbsolutePath());
            taskMonitor.showMessage(TaskMonitor.Level.INFO,
                    "export table " + selectedTable + " to " + file.getAbsolutePath());
            TaskIterator ti = exportTF.createTaskIterator(selectedTable, file);
            insertTasksAfterCurrentTask(ti);
        }
    }

    @ProvidesTitle
    public String getTitle() {
        return "Export Enrichment table";
    }
}
