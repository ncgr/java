<!DOCTYPE html>
<html>
    
    <head>
        <title>NCGR Neo4j PathQuery Endpoint Development</title>
        <link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style"/>
    </head>

    <body>

        <h1>Welcome to the NCGR Neo4j PathQuery service.</h1>

        <p>
            The concept here is to mimic an InterMine web service by reverse engineering PathQuery XML and querying either a Neo4j database with Cypher (for most bio objects) or perhaps
            the InterMine web service with the PathQuery for stuff that isn't in the Neo4j database.
        </p>

        <p>
            The syntax is identical to that used by InterMine. Simply aim your HTTP request at <b><%=request.getRequestURI()%></b> instead of /beanmine/.
        </p>

        <p>
            NOTE: the PathQuery&rightarrow;Cypher translation is under <em>very early development</em>. Many PathQueries will fail, especially those with JOINs.
        </p>

        <form action="service/query/results" method="POST">
            <input type="hidden" name="format" value="tab"/>
            <p>
                For your convenience, here's a place where you can paste XML and test this out, or submit the sample query provided:
            </p>
            <textarea style="width:700px; height:200px;" name="query"><query name="" model="genomic" view="Gene.id Gene.primaryIdentifier Gene.goAnnotation.ontologyTerm.identifier Gene.goAnnotation.ontologyTerm.name" longDescription="" sortOrder="Gene.id asc">
    <constraint path="Gene.goAnnotation.ontologyTerm.identifier" op="=" value="GO:0008270"/>
</query></textarea>
            <br/>
            <input type="submit" value="SEND"/>
        </form>

    </body>

</html>

