package org.nrnb.gsoc.enrichment.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EVIDENCE_CODES {

    CORUM, HDA, HEP, HGI, HMP, HPO, IBA,
    IC, IDA, IEA, IEP, IGC, IGI,
    IMP, IPI, IRD, ISA, ISM, ISO,
    ISS, KEGG, miRTarBase, NA, NAS, ND, RCA,
    Reactome, TAS, TRANSFAC_TFBS;

    public static List<String> stringValue() {
        return Arrays.stream(values()).map(EVIDENCE_CODES::toString).collect(Collectors.toList());
    }
}
