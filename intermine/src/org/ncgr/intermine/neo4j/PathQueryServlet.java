package org.ncgr.intermine.neo4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handle PathQuery requests by querying Neo4j (when applicable) or InterMine (otherwise).
 *
 * @author Sam Hokin
 */
public class PathQueryServlet extends HttpServlet {

    String intermineServiceUrl;
    String neo4jBoltUrl;
    String neo4jBoltUser;
    String neo4jBoltPassword;

    /**
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // load the context parameters
        intermineServiceUrl = getServletContext().getInitParameter("intermine.service.url");
        neo4jBoltUrl = getServletContext().getInitParameter("neo4j.bolt.url");
        neo4jBoltUser = getServletContext().getInitParameter("neo4j.bolt.user");
        neo4jBoltPassword = getServletContext().getInitParameter("neo4j.bolt.password");

        
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
  
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // process the request
        if (request.getParameter("query")==null) return;

        String query = request.getParameter("query");

        // set the response content type
        response.setContentType("text/plain");
        
        // set some response headers
        // response.setHeader("Content-Disposition","attachment; filename="+makeFileName(1));
        // response.setHeader("Expires", "0");
        // response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        // response.setHeader("Pragma", "public");
	
        // write ByteArrayOutputStream to the ServletOutputStream
        // ServletOutputStream out = response.getOutputStream();
        // baos.writeTo(out);
        // out.flush();

        PrintWriter writer = response.getWriter();
        writer.println("InterMine service URL: "+intermineServiceUrl);
        writer.println("Neo4j Bolt URL: "+neo4jBoltUrl);
        writer.println("Your query:");
        writer.println(query);
        writer.flush();
        writer.close();

    }

    /**
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
    }


}
