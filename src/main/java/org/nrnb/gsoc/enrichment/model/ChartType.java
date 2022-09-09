package org.nrnb.gsoc.enrichment.model;

public enum ChartType {
    SPLIT("Split donut"),
    FULL("Full donut"),
    TEETH("Donut slices only"),
    SPLIT_PIE("Split Pie Chart"),
    PIE("Pie Chart");

    String name;
    ChartType(String name) {
        this.name = name;
    }

    public String toString() { return name; }
}

