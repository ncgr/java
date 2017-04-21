<!DOCTYPE html>
<html>
    
    <head>
        <title>NCGR Neo4j PathQuery Endpoint Development</title>
    </head>

    <body>

        <h1>Welcome to the NCGR Neo4j PathQuery service.</h1>

        <p>
            The concept here is to mimic an InterMine web service by consuming PathQuery XML and querying either a Neo4j database (for most bio objects) or perhaps
            the InterMine web service for stuff that isn't in the Neo4j database.
        </p>

        <p>
            The syntax is identical to that used by InterMine. Simply aim your HTTP request at <b><%=request.getRequestURI()%>service</b> instead.
        </p>

        <form action="service" method="POST">
            <div style="text-align:center">
                <p>
                    For your convenience, here's a place where you can paste XML and test this out:
                </p>
                <textarea style="width:600px; height:200px; border:2px solid red;" name="query"></textarea>
                <br/>
                <input type="submit" value="SEND"/>
            </div>
        </form>

    </body>

</html>

