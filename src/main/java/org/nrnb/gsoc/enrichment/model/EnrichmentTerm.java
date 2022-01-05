package org.nrnb.gsoc.enrichment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ighosh98
 * @description Class that structures and stores data fetched from making the API request to gProfiler's server
 */
public class EnrichmentTerm implements Comparable<EnrichmentTerm> {

    /**
     * @description Data fields to be fetched from GOst API response
     * source
     * goshv
     * significant
     * effective_domain_size -> total number of genes
     * intersection_size: number of genes in the query that are annotated to the corresponding term
     * term_size
     * query_size
     * precision
     * recall
     * source_order -> might be used to generate manhattan plots
     * group_id
     * intersections -> list of actual gene symbols
     */
    String name;
    String description;
    String source;
    String pvalue;
    double goshv;
    boolean isSignificant;
    int effectiveDomainSize;
    int intersectionSize;
    int termSize;
    double precision;
    double recall;


    String termID;//same as native


    List<String> genes;
    List<Long> nodes;

    public static enum TermSource {
        ALL("All", "All", "Enrichment: All"),
        ALLFILTERED("AllFilt", "All Filtered", "Enrichment: All Filtered"),
        GOPROCESS("Process", "Gene Ontology Biological Process", "Enrichment: GO Biological Process"),
        GOCOMPONENT("Component", "Gene Ontology Cellular Component branch", "Enrichment: GO Cellular Component"),
        GOFUNCTION("Function", "Gene Ontology Molecular Function", "Enrichment: GO Molecular Function"),
        KEGG("KEGG", "KEGG", "Enrichment: KEGG Pathways"),
        REACTOME("RCTM", "Reactome pathways", "Enrichment: Reactome Pathways"),
        WIKIPATHWAYS("WikiPathways", "WikiPathways", "Enrichment: WikiPathways"),
        TF("TF","Transfac transcription factor binding site predictions","Enrichment: TF"),
        MIRNA("MIRNA","mirTarBase miRNA targets","MIRNA"),
        HPA("HPA","Human Protein Atlas","Enrichment: HPA"),
        CORUM("CORUM"," Manually annotated protein complexes from mammalian organisms","CORUM"),
        HP("HP","Human Phenotype Ontology", "Enrichment: Human Phenotype Ontology");

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
            for (TermSource ts: values()) {
                cats.add(ts.getKey());
            }
            return cats;
        }

        // return only the categories that should/could be filtered (exclude publications and all)
        static public List<TermSource> getValues() {
            List<TermSource> cats = new ArrayList<TermSource>();
            for (TermSource ts: values()) {
                if (ts != ALL && ts != ALLFILTERED)
                    cats.add(ts);
            }
            return cats;
        }

        static public List<String> getTables() {
            List<String> tables = new ArrayList<String>();
            for (TermSource ts: values()) {
                tables.add(ts.getTable());
            }
            return tables;
        }

        static public boolean containsKey(String key) {
            for (TermSource ts: values()) {
                if (ts.getKey().equals(key))
                    return true;
            }
            return false;
        }

        static public String getName(String key) {
            for (TermSource ts: values()) {
                if (ts.getKey().equals(key))
                    return ts.getName();
            }
            return null;
        }
    }

    public static final int nodeSUIDColumn = 12;
    public static final int chartColumnCol = 1;

    public static final String colSource = "source";
    public static final String colID = "id";
    public static final String colTermID = "term id"; //native
    public static final String colName = "term name";
    public static final String colDescription = "description";
    public static final String colPvalue = "adjusted p-value";
    public static final String colGoshv = "goshv";
    public static final String colIsSignificant = "significant";
    public static final String colEffectiveDomainSize = "background size";
    public static final String colIntersectionSize = "intersection size";
    public static final String colTermSize = "term size";
    public static final String colQuerySize = "query size";
    public static final String colPrecision = "precision";
    public static final String colRecall = "recall";
    public static final String colGroupID = "group id";
    public static final String colGenes = "intersecting genes"; // list of genes
    public static final String colGenesSUID = "nodes.SUID";//session unique id
    public static final String colNetworkSUID = "network.SUID";// session unique id


    // enrichment table master schema
    public static final String[] swingColumnsEnrichment = new String[] {  colSource, colTermID, colName, colDescription, colPvalue, colQuerySize, colEffectiveDomainSize,colTermSize,colIntersectionSize,colPrecision,colRecall, colGenes, colGenesSUID};

    public static final int nameColumn = 2;
    public static final int pvalueColumn = 4;

    public EnrichmentTerm() {
        this.name = "";
        this.description = "";
        this.source = "";
        this.pvalue = "0.0";
        this.goshv=-1.0;
        this.isSignificant = true;
        this.effectiveDomainSize=0;
        this.intersectionSize =0;
        this.termSize = 0;
        this.precision =0.0;
        this.recall = 0.0;
    }

    public EnrichmentTerm(String enrichmentSource) {
        this.name = "";
        this.description = "";
        this.source = enrichmentSource;
        this.pvalue = "0.0";
    }



    /**
     * All getters and setters of the API response fields
     */
    public int getQuerySize() {
        return querySize;
    }

    public void setQuerySize(int querySize) {
        this.querySize = querySize;
    }

    int querySize;

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
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

    public String getPValue() {
        return pvalue;
    }

    public void setPValue(String pvalue) {
        this.pvalue = pvalue;
    }

    public double getGoshv() {
        return goshv;
    }

    public void setGoshv(double goshv) {
        this.goshv = goshv;
    }

    public boolean isSignificant() {
        return isSignificant;
    }

    public void setSignificant(boolean significant) {
        isSignificant = significant;
    }

    public int getEffectiveDomainSize() {
        return effectiveDomainSize;
    }

    public void setEffectiveDomainSize(int effectiveDomainSize) {
        this.effectiveDomainSize = effectiveDomainSize;
    }

    public int getIntersectionSize() {
        return intersectionSize;
    }

    public void setIntersectionSize(int intersectionSize) {
        this.intersectionSize = intersectionSize;
    }

    public int getTermSize() {
        return termSize;
    }

    public void setTermSize(int termSize) {
        this.termSize = termSize;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public void setGenes(List<String> genes) {
        this.genes = genes;
    }

    public List<String> getGenes() {
        return genes;
    }

    public void setNodesSUID(List<Long> nodes) {
        this.nodes = nodes;
    }

    public List<Long> getNodesSUID(){
        return nodes;
    }

    public String toString() {
        return name + "\t" + pvalue;
    }

    public int compareTo(EnrichmentTerm et) {
        return this.pvalue.compareTo(et.getPValue());
    }

}
