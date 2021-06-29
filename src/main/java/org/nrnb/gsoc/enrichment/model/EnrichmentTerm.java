package org.nrnb.gsoc.enrichment.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnrichmentTerm implements Comparable<EnrichmentTerm> {
    String name;
    String description;
    int year;
    String category;
    double pvalue;
    int genesBG;
    List<String> genes;
    List<Long> nodes;

    public static final String enrichmentURLTest = "http://gamma.string-db.org/cgi/webservices/enrichmentWrapper.pl";
    public static final String enrichmentURL = "http://version-10.string-db.org/cgi/webservices/enrichmentWrapper.pl";

    // Change to an enum?
    public static enum TermCategory {
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
        TermCategory(String key, String name, String table) {
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
            for (TermCategory tc: values()) {
                cats.add(tc.getKey());
            }
            return cats;
        }
        // return only the categories that should/could be filtered (exclude publications and all)
        static public List<TermCategory> getValues() {
            List<TermCategory> cats = new ArrayList<TermCategory>();
            for (TermCategory tc: values()) {
                if (tc != ALL && tc != ALLFILTERED)
                    cats.add(tc);
            }
            return cats;
        }
        static public List<String> getTables() {
            List<String> tables = new ArrayList<String>();
            for (TermCategory tc: values()) {
                tables.add(tc.getTable());
            }
            return tables;
        }
        static public boolean containsKey(String key) {
            for (TermCategory tc: values()) {
                if (tc.getKey().equals(key))
                    return true;
            }
            return false;
        }
        static public String getName(String key) {
            for (TermCategory tc: values()) {
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
    public static final int nodeSUIDColumn = 8;
    public static final int chartColumnCol = 1;
    public static final int nameColumn = 2;

    public static final int nodeSUIDColumnPubl = 7;
    public static final int fdrColumnPubl = 3;
    public static final int idColumnPubl = 0;

    public EnrichmentTerm() {
        this.name = "";
        this.year = 0;
        this.description = "";
        this.category = "";
        this.pvalue = -1.0;
        this.pvalue = -1.0;
        this.genesBG = 0;
        this.genes = new ArrayList<String>();
        this.nodes = new ArrayList<Long>();

    }

    public EnrichmentTerm(String enrichmentCategory) {
        this.name = "";
        this.year = 0;
        this.description = "";
        this.category = enrichmentCategory;
        this.pvalue = -1.0;
        this.genesBG = 0;
        this.genes = new ArrayList<String>();
        this.nodes = new ArrayList<Long>();

    }

    public EnrichmentTerm(String name, int year, String description, String category,
                          double pvalue, int genesBG) {
        this.name = name;
        this.year = year;
        this.description = description;
        this.category = category;
        this.pvalue = pvalue;
        this.pvalue = pvalue;
        this.genesBG = genesBG;
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

    public void setDescription(String desc) {
        this.description = desc;
        if (desc.length() > 5 && desc.substring(1,5).matches("^\\d{4}")) {
            this.description = desc.substring(6);
            this.year = Integer.parseInt(desc.substring(1,5));
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPValue() {
        return pvalue;
    }

    public void setPValue(double pvalue) {
        this.pvalue = pvalue;
    }

    public double getFDRPValue() {
        return pvalue;
    }

    public void setFDRPValue(double pvalue) {
        this.pvalue = pvalue;
    }

    public int getGenesBG() {
        return genesBG;
    }

    public void setGenesBG(int genesBG) {
        this.genesBG = genesBG;
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
        if (this.pvalue < et.getFDRPValue()) {
            return -1;
        } else if (this.pvalue == et.getFDRPValue()) {
            return 0;
        }
        return 1;
    }

}