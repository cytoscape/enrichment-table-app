package org.nrnb.gsoc.enrichment.tasks;

import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.json.JSONResult;

import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;

public class ShowChartsTask extends AbstractTask implements ObservableTask {

    private final EnrichmentCytoPanel cytoPanel;

    public ShowChartsTask(EnrichmentCytoPanel cytoPanel) {
        this.cytoPanel = cytoPanel;
    }

    @Override
    public void run(TaskMonitor arg0) {
        arg0.setTitle("Show enrichment charts");
        // Filter the current list
        SwingUtilities.invokeLater(cytoPanel::drawCharts);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R getResults(Class<? extends R> clzz) {
        if (clzz.equals(String.class)) {
            return (R)"";
        } else if (clzz.equals(JSONResult.class)) {
            JSONResult res = () -> {
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

}

