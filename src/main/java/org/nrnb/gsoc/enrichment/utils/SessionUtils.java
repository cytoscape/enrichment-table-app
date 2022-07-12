package org.nrnb.gsoc.enrichment.utils;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.nrnb.gsoc.enrichment.model.EnrichmentTerm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionUtils {

    private static final Map<String, Object> sessionObjectMap = new HashMap<>();

    public static void setSelectedEvidenceCode(CyNetwork network, CyTable model,
                                               List<String> evidenceCode) {
        sessionObjectMap.put("selectedEvidenceCodes" + generateHashMap(network, model),
                evidenceCode);
    }

    public static List<String> getSelectedEvidenceCode(CyNetwork network, CyTable model) {
        try {
            return (List<String>) sessionObjectMap.get("selectedEvidenceCodes" + generateHashMap(network, model));
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
            return (List<EnrichmentTerm.TermSource>)
                    sessionObjectMap.get("selectedCategories" + generateHashMap(network, model));
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
            return (double) sessionObjectMap.get("removeRedundantCutOff" + generateHashMap(network, model));
        }
        catch (Exception e) {
            return 0.5;
        }
    }

    private static String generateHashMap(CyNetwork network, CyTable model) {
        return network.hashCode() + "" + model.hashCode();
    }
}
