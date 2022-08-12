# Enrichment Table App for Cytoscape
The Enrichment Table App provides the functionality of functional enrichment analysis for any network loaded into Cytoscape using [g:Profiler's web service](https://biit.cs.ut.ee/gprofiler/gost).

The app creates a new table called the `Enrichment Table` which provides an icon to perform enrichment, as well icons for settings and filters. The app also adds a menu option under ***Tools*** > ***Enrichment Table*** > ***Perform Gene Enrichment***.

By default, the enrichment analysis is performed on all nodes of the current network using the genome as background. If nodes are selected, then enrichment is performed against just those nodes using the complete network as the background. You can arrange for any background you like by loading all background nodes into Cytoscape and selecting a subset for enrichment analysis.

The enrichment analysis is supported in automation use cases as well. The basic command syntax is `enrichment analysis`.  You can optionally choose the organism associated with the query genes with the `organism` parameter. You can also optionally select the node table column containing the gene symbols with the `geneID` parameter. All parameters are listed
[here](http://localhost:1234/v1/swaggerUI/swagger-ui/index.html?url=http%3A%2F%2Flocalhost%3A1234%2Fv1%2Fcommands%2Fswagger.json#!/enrichment/enrichment_analysis).

## Features in version 2.0
1. The application on startup predicts the possible organism by processing the data from network in columns [`species`,`organism`,`IntAct::species`]
2. The application on startup predicts gene id column by following ways:
   1. Retrieves `NODE_LABLE` from style for any generic network
   2. Selects `display name` for `stringapp` networks
3. Enhanced filtration functionality with option to filter by
   1. Categories
   2. Evidence code
4. Remove redundant terms in table by selecting appropriate redundancy (jaccard) cutoff, default is 0.5
5. Enrichment Table shows results based on nodes selected in the UI. If no nodes are selected, all terms are shown. On multiple nodes selecting, terms consisting all the selected nodes are shown (AND type) 
6. Ability to generate enrichment map
7. User friendly logging mechanism to resolve issues with developers
