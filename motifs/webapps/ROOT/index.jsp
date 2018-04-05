<!DOCTYPE html>
<%@ page import="org.ncgr.motifs.*" contentType="text/html;charset=UTF-8" %>
<html>

    <head>
        <title>NCGR Motif Search</title>
    </head>

    <body>
        <h1>Experimental motif search tool</h1>
        <p>
            This tool will search for motifs of the <i>same length</i> from the JASPAR2018 database, and score them according to their MEME matrix.
        </p>

        <form action="motifSearch" method="GET">
            <input type="text" name="query" size="12"/>
            <input type="submit" name="submit" value="SUBMIT"/>
        </form>

    </body>

</html>
