package org.nrnb.gsoc.enrichment.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnrichmentTerm implements Comparable<EnrichmentTerm> {
    String name;
    String description;
    String source;
    double pvalue;
    List<String> genes;
    List<Long> nodes;

    public static final String enrichmentURLTest = "http://gamma.string-db.org/cgi/webservices/enrichmentWrapper.pl";
    public static final String enrichmentURL = "http://version-10.string-db.org/cgi/webservices/enrichmentWrapper.pl";

    // Change to an enum?
    public static enum TermSource {
        ALL("All", "All", "Enrichment: All"),
        ALLFILTERED("AllFilt", "All Filtered", "Enrichment: All Filtered"),
        //GO:BP -> Biological process [Default]
        GOPROCESS("Process", "GO Process", "Enrichment: GO Biological Process"),
        // GO:CC
        GOCOMPONENT("Component", "GO Component", "Enrichment: GO Cellular Component"),
        // GO::MF
        GOFUNCTION("Function", "GO Function", "Enrichment: GO Molecular Function"),

        KEGG("KEGG", "KEGG Pathways", "Enrichment: KEGG Pathways"),
        REACTOME("RCTM", "Reactome Pathways", "Enrichment: Reactome Pathways"),
        WIKIPATHWAYS("WikiPathways", "WikiPathways", "Enrichment: WikiPathways"),
        MIRNA("MIRNA","MIRNA","MIRNA"),
        CORUM("CORUM","CORUM","CORUM");

        String key, name, table;
        TermSource(String key, String name, String table) {
            this.key = key;
            this.name = name;
            this.table = table;
        }

        public String getKey() { return key; }
        public String getName() { return name; }
        public String getTable() { return table; }
        public String toString() { return name; }
        static public List<String> getCategories() {
            List<String> cats = new ArrayList<String>();
            for (TermSource tc: values()) {
                cats.add(tc.getKey());
            }
            return cats;
        }
        // return only the categories that should/could be filtered (exclude publications and all)
        static public List<TermSource> getValues() {
            List<TermSource> cats = new ArrayList<TermSource>();
            for (TermSource tc: values()) {
                if (tc != ALL && tc != ALLFILTERED)
                    cats.add(tc);
            }
            return cats;
        }
        static public List<String> getTables() {
            List<String> tables = new ArrayList<String>();
            for (TermSource tc: values()) {
                tables.add(tc.getTable());
            }
            return tables;
        }
        static public boolean containsKey(String key) {
            for (TermSource tc: values()) {
                if (tc.getKey().equals(key))
                    return true;
            }
            return false;
        }
        static public String getName(String key) {
            for (TermSource tc: values()) {
                if (tc.getKey().equals(key))
                    return tc.getName();
            }
            return null;
        }
    }

    /**
     * source
     * goshv
     * significant
     * effective_domain_size -> total number of genes
     * intersection_size ->
     * term_size
     * query_size
     * precision
     * recall
     * source_order -> might be used to generate manhattan plots
     * group_id
     * intersections -> list of actual gene symbols
     */

    public static final String colSource = "source";
    public static final String colTermID = "term id"; //native
    public static final String colName = "term name";
    public static final String colDescription = "description";
    public static final String colPvalue = "p-value";
    public static final String colGoshv = "goshv";
    public static final String colIsSignificant = "Significant";
    public static final String colEffectiveDomainSize = "# genes";
    public static final String colIntersectionSize = " Intersection Size";
    public static final String colTermSize = "Term Size";
    public static final String colQuerySize = "Query Size";
    public static final String colPrecision = "precision";
    public static final String colRecall = "recall";
    public static final String colSourceOrder = "Source Order";
    public static final String colGroupID = "Group ID";
    public static final String colGenes = "genes"; // list of genes

    public static final String colGenesSUID = "nodes.SUID";//session unique id
    public static final String colEffectiveDomainSizeOld = "# enriched genes";

    public static final String colNetworkSUID = "network.SUID";// session unique id
    // public static final String colShowChart = "showInPieChart";

    public static final String colChartColor = "chart color"; // Data visualization option -> takes the enrichment table and visualize


    public static final String[] swingColumnsEnrichment = new String[] { colChartColor, colName, colDescription, colPvalue,
            colEffectiveDomainSize,  colGenes, colGenesSUID };
    public static final String[] swingColumnsEnrichmentOld = new String[] { colChartColor, colName, colDescription, colPvalue,
            colEffectiveDomainSizeOld, colGenesSUID };


    public EnrichmentTerm() {
        this.name = "";
        this.description = "";
        this.source = "";
        this.pvalue = -1.0;
        this.pvalue = -1.0;
        this.genes = new ArrayList<String>();
        this.nodes = new ArrayList<Long>();

    }

    public EnrichmentTerm(String enrichmentSource) {
        this.name = "";
        this.description = "";
        this.source = enrichmentSource;
        this.pvalue = -1.0;
        this.genes = new ArrayList<String>();
        this.nodes = new ArrayList<Long>();

    }

    public EnrichmentTerm(String name, String description, String source,
                          double pvalue) {
        this.name = name;
        this.description = description;
        this.source = source;
        this.pvalue = pvalue;
        this.pvalue = pvalue;
        this.genes = new ArrayList<String>();
        this.nodes = new ArrayList<Long>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getPValue() {
        return pvalue;
    }

    public void setPValue(double pvalue) {
        this.pvalue = pvalue;
    }


    public int getNumberGenes() {
        return genes.size();
    }

    public List<String> getGenes() {
        return genes;
    }

    public void setGenes(List<String> genes) {
        this.genes = genes;
    }

    public List<Long> getNodesSUID() {
        return nodes;
    }

    public void setNodesSUID(List<Long> nodes) {
        this.nodes = nodes;
    }

    public String toString() {
        return name + "\t" + getNumberGenes() + "\t" + pvalue;
    }

    public int compareTo(EnrichmentTerm et) {
        // if (t.toString() == null) return 1;
        if (this.pvalue < et.getPValue()) {
            return -1;
        } else if (this.pvalue == et.getPValue()) {
            return 0;
        }
        return 1;
    }

}