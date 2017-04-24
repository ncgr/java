<!DOCTYPE html>
<html>
    
    <head>
        <title>NCGR Neo4j PathQuery Endpoint Development</title>
        <link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style"/>
    </head>

    <body>

        <h1>Welcome to the NCGR Neo4j PathQuery service.</h1>

        <p>
            The concept here is to mimic an InterMine web service by consuming PathQuery XML and querying either a Neo4j database (for most bio objects) or perhaps
            the InterMine web service for stuff that isn't in the Neo4j database.
        </p>

        <p>
            The syntax is identical to that used by InterMine. Simply aim your HTTP request at <b><%=request.getRequestURI()%></b> instead of, say, /beanmine/.
        </p>

        <form action="service/query/results" method="POST">
            <input type="hidden" name="format" value="tab"/>
            <p>
                For your convenience, here's a place where you can paste XML and test this out, or submit the fairly generic query provided:
            </p>
            <textarea style="width:700px; height:200px;" name="query"><query name="" model="genomic" view="Gene.primaryIdentifier Gene.goAnnotation.ontologyTerm.identifier Gene.goAnnotation.ontologyTerm.name" longDescription="" sortOrder="Gene.goAnnotation.ontologyTerm.name asc">
    <constraint path="Gene.goAnnotation.ontologyTerm.identifier" op="=" value="GO:0022891"/>
</query></textarea>
            <br/>
            <input type="submit" value="SEND"/>
        </form>

    </body>

</html>

