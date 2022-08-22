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

public class SessionUtils {

    private static final Map<String, Object> sessionObjectMap = new HashMap<>();

    public static void setSelectedEvidenceCode(CyNetwork network, CyTable model,
                                               List<String> evidenceCode) {
        sessionObjectMap.put("selectedEvidenceCodes" + generateHashMap(network, model),
                evidenceCode);
    }

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

    public static void setSelectedCategories(CyNetwork network, CyTable model,
                                               List<EnrichmentTerm.TermSource> evidenceCode) {
        sessionObjectMap.put("selectedCategories" + generateHashMap(network, model),
                evidenceCode);
    }

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

    public static void setRemoveRedundantStatus(CyNetwork network, CyTable model,
                                                        boolean status) {
        sessionObjectMap.put("removeRedundantStatus" + generateHashMap(network, model), status);
    }

    public static boolean getRemoveRedundantStatus(CyNetwork network, CyTable model) {
        try {
            return (boolean) sessionObjectMap.get("removeRedundantStatus" + generateHashMap(network, model));
        }
        catch (Exception e) {
            return false;
        }
    }

    public static void setRemoveRedundantCutoff(CyNetwork network, CyTable model,
                                                double cutoff) {
        sessionObjectMap.put("removeRedundantCutOff" + generateHashMap(network, model), cutoff);
    }

    public static double getRemoveRedundantCutoff(CyNetwork network, CyTable model) {
        try {
            Double result = (Double) sessionObjectMap.get("removeRedundantCutOff" + generateHashMap(network, model));
            return Objects.isNull(result) ? 0.5 : result;
        }
        catch (Exception e) {
            return 0.5;
        }
    }

    public static ChartType getChartType(CyNetwork network, CyTable model) {
        try {
            ChartType result = (ChartType) sessionObjectMap.get("chartType" + generateHashMap(network, model));
            return Objects.isNull(result) ? ChartType.SPLIT : result;
        }
        catch (Exception e) {
            return ChartType.SPLIT;
        }
    }

    public static void setChartType(CyNetwork network, CyTable model, ChartType type) {
         sessionObjectMap.put("chartType" + generateHashMap(network, model), type);
    }

    public static void setTopTerms(CyNetwork network, CyTable model, int topTerms) {
        sessionObjectMap.put("topTerms" + generateHashMap(network, model), topTerms);
    }

    public static int getTopTerms(CyNetwork network, CyTable model) {
        try {
            return (int) sessionObjectMap.get("topTerms" + generateHashMap(network, model));
        }
        catch (Exception e) {
            return 5;
        }
    }

    public static void setEnrichmentPalette(CyNetwork network, CyTable model, Palette palette) {
        sessionObjectMap.put("enrichmentPalette" + generateHashMap(network, model), palette);
    }

    public static Palette getEnrichmentPalette(CyNetwork network, CyTable model) {
        try {
            Palette palette = (Palette) sessionObjectMap.get("enrichmentPalette" + generateHashMap(network, model));
            return Objects.isNull(palette) ? null : palette;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static String generateHashMap(CyNetwork network, CyTable model) {
        return network.hashCode() + "" + model.hashCode();
    }
}
