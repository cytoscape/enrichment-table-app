# Enrichment Table App for Cytoscape
The Enrichment Table App provides the functionality of functional enrichment analysis for any network loaded into Cytoscape using [g:Profiler's web service](https://biit.cs.ut.ee/gprofiler/gost).

The app creates a new table called the `Enrichment Table` which provides an icon to perform enrichment, as well icons for settings and filters. The app also adds a menu option under ***Tools*** > ***Enrichment Table*** > ***Perform Gene Enrichment***.

By default, the enrichment analysis is performed on all nodes of the current network using the genome as background. If nodes are selected, then enrichment is performed against just those nodes using the complete network as the background. You can arrange for any background you like by loading all background nodes into Cytoscape and selecting a subset for enrichment analysis.

The enrichment analysis is supported in automation use cases as well. The basic command syntax is `enrichment analysis`.  You can optionally choose the organism associated with the query genes with the `organism` parameter. You can also optionally select the node table column containing the gene symbols with the `geneID` parameter. All parameters are listed
[here](http://localhost:1234/v1/swaggerUI/swagger-ui/index.html?url=http%3A%2F%2Flocalhost%3A1234%2Fv1%2Fcommands%2Fswagger.json#!/enrichment/enrichment_analysis).
