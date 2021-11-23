# Enrichment Table App for Cytoscape
Enrichment Table App is a Cytoscape app. It provides the functionality to create a new table for performing, storing and viewing functional enrichment analysis.

This app adds a menu option under ***Tools > Enrichment Tool for Analysis*** to ***Perform Gene Enrichment*** in the current network. 

The selection operation is supported in automation use cases as well. The basic command syntax is `enrichment analysis`.  You can optionally choose the organism associated with the query genes with the `organism` parameter. You can also optionally select the node table column containing the gene symbols with the `geneID` parameter. All parameters are listed
[here](http://localhost:1234/v1/swaggerUI/swagger-ui/index.html?url=http%3A%2F%2Flocalhost%3A1234%2Fv1%2Fcommands%2Fswagger.json#!/enrichment/enrichment_analysis).
