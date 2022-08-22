package org.nrnb.gsoc.enrichment.tasks;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.ListMultipleSelection;
import org.nrnb.gsoc.enrichment.constants.EVIDENCE_CODES;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm.TermSource;
import org.nrnb.gsoc.enrichment.ui.EnrichmentCytoPanel;
import org.nrnb.gsoc.enrichment.ui.EnrichmentTableModel;
import org.nrnb.gsoc.enrichment.utils.ModelUtils;
import org.nrnb.gsoc.enrichment.utils.SessionUtils;
import org.nrnb.gsoc.enrichment.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class FilterEnrichmentTableTask extends AbstractTask implements ObservableTask {

    @Tunable(description = "Select categories",
            tooltip = "Select the enrichment categories to show in the table",
            longDescription = "Select the enrichment categories to show in the table. Use \"All\" to remove the filtering.",
            exampleStringValue = "GO Process",
            gravity = 1.0)
    public ListMultipleSelection<TermSource> categories = new ListMultipleSelection<>(TermSource.getValues());

    @Tunable(description = "Select evidence code",
            tooltip = "Select the evidence codes to show in the table. If multiple codes are selected, those terms which " +
                    "contains all the selected codes will be present in result",
            exampleStringValue = "GO:0005737",
            gravity = 1.0
    )
    public ListMultipleSelection<String> evidenceCodes;

    @Tunable(description = "Remove redundant terms",
            tooltip = "Removes terms whose enriched genes significantly overlap with already selected terms",
            longDescription = "Removes terms whose enriched genes significantly overlap with already selected terms",
            exampleStringValue = "true",
            gravity = 8.0)
    public boolean removeOverlapping;

    @Tunable(description = "Redundancy cutoff",
            tooltip = "<html>This is the maximum Jaccard similarity that will be allowed <br/>"
                    + "between a less significant term and a more significant term such that <br/>"
                    + "the less significant term is kept in the list</html>",
            longDescription = "This is the maximum Jaccard similarity that will be allowed "
                    + "between a less significant term and a more significant term such that "
                    + "the less significant term is kept in the list",
            exampleStringValue="0.5",
            params="slider=true", dependsOn="removeOverlapping=true", gravity = 9.0)
    public BoundedDouble overlapCutoff = new BoundedDouble(0.0, 0.5, 1.0, false, false);

    private CyApplicationManager applicationManager;
    private EnrichmentCytoPanel enrichmentPanel;
    private CyNetwork network;
    private CyTable filteredEnrichmentTable;
    private CyServiceRegistrar registrar;
    private List<TermSource> categoryFilter = TermSource.getValues();
    private final Logger logger = Logger.getLogger(CyUserLog.NAME);

    public FilterEnrichmentTableTask(final CyServiceRegistrar registrar, EnrichmentCytoPanel panel) {
        this.registrar = registrar;
        this.applicationManager = registrar.getService(CyApplicationManager.class);
        this.network = applicationManager.getCurrentNetwork();
        this.enrichmentPanel = panel;
        this.filteredEnrichmentTable = ModelUtils.getEnrichmentTable(registrar, network,
                TermSource.ALL.getTable());
        this.setCategoriesAndEvidenceCodesAtStartup();
        List<String> evidenceCodesEnum = EVIDENCE_CODES.stringValue();
        categories.setSelectedValues(SessionUtils.getSelectedCategories(network, filteredEnrichmentTable));
        this.evidenceCodes = new ListMultipleSelection<>(evidenceCodesEnum);
        evidenceCodes.setSelectedValues(SessionUtils.getSelectedEvidenceCode(network, filteredEnrichmentTable));
        removeOverlapping = SessionUtils.getRemoveRedundantStatus(network, filteredEnrichmentTable);
        overlapCutoff.setValue(SessionUtils.getRemoveRedundantCutoff(network, filteredEnrichmentTable));
    }

    private void setCategoriesAndEvidenceCodesAtStartup() {
        List<TermSource> selectedCategories = SessionUtils.getSelectedCategories(network, filteredEnrichmentTable);
        if (Objects.isNull(selectedCategories)) {
            SessionUtils.setSelectedCategories(network, filteredEnrichmentTable, Collections.emptyList());
        }
        else if (!selectedCategories.isEmpty()) {
            TermSource termSource = selectedCategories.get(0);
            if (Objects.isNull(termSource)) SessionUtils.setSelectedCategories(network,
                    filteredEnrichmentTable, Collections.emptyList());
        }
        List<String> selectedEvidenceCodes = SessionUtils.getSelectedEvidenceCode(network,
                filteredEnrichmentTable);
        if (Objects.isNull(selectedEvidenceCodes)) {
            SessionUtils.setSelectedEvidenceCode(network, filteredEnrichmentTable, Collections.emptyList());
        }
        else if (!selectedEvidenceCodes.isEmpty()) {
            String termSource = selectedEvidenceCodes.get(0);
            if (Objects.isNull(termSource) || termSource.isEmpty())
                SessionUtils.setSelectedEvidenceCode(network, filteredEnrichmentTable, Collections.emptyList());
        }

        try {
            // Do not remove assignment to variable. Not able to cast causes exception
            boolean toRemoveRedundancy = SessionUtils.getRemoveRedundantStatus(network, filteredEnrichmentTable);
        }
        catch (NullPointerException exception) {
            SessionUtils.setRemoveRedundantStatus(network, filteredEnrichmentTable, false);
        }

        try {
            double overlapCutoff = SessionUtils.getRemoveRedundantCutoff(network, filteredEnrichmentTable);
        }
        catch (NullPointerException exception) {
            SessionUtils.setRemoveRedundantCutoff(network, filteredEnrichmentTable, 0.5);
        }
    }


    @Override
    public void run(TaskMonitor monitor) throws Exception {
        monitor.setTitle("Filter Enrichment table");
        logger.info(("Filtering results based on selected value: categories: "
                + categories.getSelectedValues()) + " , evidence code: "
                + evidenceCodes.getSelectedValues());
        List<TermSource> categoryList = categories.getSelectedValues();
        List<String> evidenceList = new ArrayList<>(evidenceCodes.getSelectedValues());
        SessionUtils.setRemoveRedundantStatus(network, filteredEnrichmentTable, removeOverlapping);
        SessionUtils.setRemoveRedundantCutoff(network, filteredEnrichmentTable, overlapCutoff.getValue());
        SessionUtils.setSelectedEvidenceCode(network, filteredEnrichmentTable, evidenceList);
        SessionUtils.setSelectedCategories(network, filteredEnrichmentTable, categoryList);
        if (enrichmentPanel == null) {
            CySwingApplication swingApplication = registrar.getService(CySwingApplication.class);
            CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.SOUTH);
            if (cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment") != -1)
                enrichmentPanel = (EnrichmentCytoPanel) cytoPanel.getComponentAt(
                        cytoPanel.indexOfComponent("org.nrnb.gsoc.enrichment"));
            else return;
        }
        EnrichmentTableModel tableModel = enrichmentPanel.getTableModel();
        if (tableModel == null) {
            monitor.showMessage(TaskMonitor.Level.ERROR, "Unable to find enrichment table!");
            logger.error("Unable to find enrichment table!");
            return;
        }
        tableModel.filter(categoryList, evidenceList, removeOverlapping, overlapCutoff.getValue());
        if (enrichmentPanel.getIsChartEnabled()) {
            ViewUtils.resetCharts(applicationManager, registrar, tableModel);
            enrichmentPanel.drawCharts();
        }
        logger.info("Filtering results completed");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R getResults(Class<? extends R> clzz) {
        if (clzz.equals(String.class)) {
            if (filteredEnrichmentTable != null)
                return (R) ("\"EnrichmentTable\": " + filteredEnrichmentTable.getSUID());
            return (R) "";
        } else if (clzz.equals(JSONResult.class)) {
            JSONResult res = () -> {
                if (filteredEnrichmentTable != null)
                    return "{\"EnrichmentTable\": " + filteredEnrichmentTable.getSUID() + "}";
                return "{}";
            };
            return (R) res;
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
