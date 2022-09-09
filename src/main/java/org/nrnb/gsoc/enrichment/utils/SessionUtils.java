package org.nrnb.gsoc.enrichment.utils;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.util.color.Palette;
import org.nrnb.gsoc.enrichment.model.ChartType;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to store data in memory as map instead of in default network table.
 * Consists of methods to set and fetch parameters unique to {@link CyNetwork} and {@link CyTable} combination.
 *
 * @author <a href="https://github.com/AkMo3">Akash Mondal</a>
 */
public class SessionUtils {

    private static final Map<String, Object> sessionObjectMap = new HashMap<>();

    /**
     * Method to set evidence code specific to a network and table.
     *
     * @param network Network with respect to be stored
     * @param model   CyTable with respect to be stored
     * @param evidenceCode List of evidence codes to be stored
     */
    public static void setSelectedEvidenceCode(CyNetwork network, CyTable model,
                                               List<String> evidenceCode) {
        sessionObjectMap.put("selectedEvidenceCodes" + generateHashMap(network, model),
                evidenceCode);
    }

    /**
     * Method to get evidence code specific to a network and table.
     *
     * @param network Network with respect is stored
     * @param model   CyTable with respect is stored
     */
    public static List<String> getSelectedEvidenceCode(CyNetwork network, CyTable model) {
        try {
            List<String> result = (List<String>) sessionObjectMap.get("selectedEvidenceCodes" +
                    generateHashMap(network, model));
            return Objects.isNull(result) ? Collections.emptyList() : result;
        }
        catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Method to set categories specific to a network and table.
     *
     * @param network Network with respect to be stored
     * @param model   CyTable with respect to be stored
     * @param categories List of categories to be stored
     */
    public static void setSelectedCategories(CyNetwork network, CyTable model,
                                               List<EnrichmentTerm.TermSource> categories) {
        sessionObjectMap.put("selectedCategories" + generateHashMap(network, model),
                categories);
    }

    /**
     * Method to get categories specific to a network and table.
     *
     * @param network Network with respect is stored
     * @param model   CyTable with respect is stored
     */
    public static List<EnrichmentTerm.TermSource> getSelectedCategories(CyNetwork network, CyTable model) {
        try {
            List<EnrichmentTerm.TermSource> result = (List<EnrichmentTerm.TermSource>)
                    sessionObjectMap.get("selectedCategories" + generateHashMap(network, model));
            return Objects.isNull(result) ? Collections.emptyList() : result;
        }
        catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Method to set status of redundancy removal.
     *
     * @param network Network with respect to be stored
     * @param model   CyTable with respect to be stored
     * @param status  Status of redundancy removal
     */
    public static void setRemoveRedundantStatus(CyNetwork network, CyTable model,
                                                        boolean status) {
        sessionObjectMap.put("removeRedundantStatus" + generateHashMap(network, model), status);
    }

    /**
     * Method to get status of redundancy removal.
     *
     * @param network Network with respect is stored
     * @param model   CyTable with respect is stored
     */
    public static boolean getRemoveRedundantStatus(CyNetwork network, CyTable model) {
        try {
            return (boolean) sessionObjectMap.get("removeRedundantStatus" + generateHashMap(network, model));
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to set cutoff of redundancy removal.
     *
     * @param network Network with respect to be stored
     * @param model   CyTable with respect to be stored
     * @param cutoff  Cutoff of redundancy removal
     */
    public static void setRemoveRedundantCutoff(CyNetwork network, CyTable model,
                                                double cutoff) {
        sessionObjectMap.put("removeRedundantCutOff" + generateHashMap(network, model), cutoff);
    }

    /**
     * Method to get cutoff of redundancy removal.
     *
     * @param network Network with respect is stored
     * @param model   CyTable with respect is stored
     */
    public static double getRemoveRedundantCutoff(CyNetwork network, CyTable model) {
        try {
            Double result = (Double) sessionObjectMap.get("removeRedundantCutOff" + generateHashMap(network, model));
            return Objects.isNull(result) ? 0.5 : result;
        }
        catch (Exception e) {
            return 0.5;
        }
    }

    /**
     * Method to get chart type for a network and table.
     *
     * @param network Network with respect is stored
     * @param model   CyTable with respect is stored
     */
    public static ChartType getChartType(CyNetwork network, CyTable model) {
        try {
            ChartType result = (ChartType) sessionObjectMap.get("chartType" + generateHashMap(network, model));
            return Objects.isNull(result) ? ChartType.SPLIT : result;
        }
        catch (Exception e) {
            return ChartType.SPLIT;
        }
    }

    /**
     * Method to set chart type for a network and table.
     *
     * @param network Network with respect to be stored
     * @param model   CyTable with respect to be stored
     * @param type    Chart type to be set.
     */
    public static void setChartType(CyNetwork network, CyTable model, ChartType type) {
         sessionObjectMap.put("chartType" + generateHashMap(network, model), type);
    }

    /**
     * Method to set number of top terms for a network and table.
     *
     * @param network Network with respect to be stored
     * @param model   CyTable with respect to be stored
     * @param topTerms Number of top terms.
     */
    public static void setTopTerms(CyNetwork network, CyTable model, int topTerms) {
        sessionObjectMap.put("topTerms" + generateHashMap(network, model), topTerms);
    }

    /**
     * Method to get number of top terms for a network and table.
     *
     * @param network Network with respect is stored
     * @param model   CyTable with respect is stored
     */
    public static int getTopTerms(CyNetwork network, CyTable model) {
        try {
            return (int) sessionObjectMap.get("topTerms" + generateHashMap(network, model));
        }
        catch (Exception e) {
            return 5;
        }
    }

    /**
     * Method to set color palette for a network and table.
     *
     * @param network Network with respect to be stored
     * @param model   CyTable with respect to be stored
     * @param palette Color palette
     */
    public static void setEnrichmentPalette(CyNetwork network, CyTable model, Palette palette) {
        sessionObjectMap.put("enrichmentPalette" + generateHashMap(network, model), palette);
    }

    /**
     * Method to get color palette for a network and table.
     *
     * @param network Network with respect is stored
     * @param model   CyTable with respect is stored
     */
    public static Palette getEnrichmentPalette(CyNetwork network, CyTable model) {
        try {
            Palette palette = (Palette) sessionObjectMap.get("enrichmentPalette" + generateHashMap(network, model));
            return Objects.isNull(palette) ? null : palette;
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Generates unique string for a network and table to be used as key.
     *
     * @param network {@link CyNetwork} to be used for creating hash
     * @param model {@link CyTable} to be used for creating hash
     * @return String unique to network and table combination.
     */
    private static String generateHashMap(CyNetwork network, CyTable model) {
        return network.hashCode() + "" + model.hashCode();
    }
}
