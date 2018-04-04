package org.ncgr.motifs.servlet;

import org.ncgr.motifs.Matrix;
import org.ncgr.motifs.MotifScanner;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Searches known motifs of the same size as the query and outputs the scores and associated proteins and family names.
 *
 * @author Sam Hokin
 */
public class MotifSearchServlet extends HttpServlet {

    // DB connection parameters from web context
    String driver;
    String url;
    String user;
    String password;

    /**
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // initialize DB connection parameters from web context
        driver = getServletContext().getInitParameter("db.driver");
        url = getServletContext().getInitParameter("db.url");
        user = getServletContext().getInitParameter("db.user");
        password = getServletContext().getInitParameter("db.password");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletOutputStream out = response.getOutputStream();
        
        try {

            // our input
            String query = request.getParameter("query");

            // do the motif scan
            MotifScanner ms = new MotifScanner(driver,url,user,password);
            Map<Matrix,Double> hitMap = ms.scan(query);
            
            // setting the content type
            response.setContentType("application/json;charset=UTF-8");
            
            // setting some response headers
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            
            // write JSON to the ServletOutputStream
            Map<Integer,JSONObject> jsonMap = new HashMap<Integer,JSONObject>();
            for (Matrix m : hitMap.keySet()) {
                JSONObject json = m.getJSON();
                json.put("score", hitMap.get(m));
                jsonMap.put(m.getId(),json);
            }
            JSONObject output = new JSONObject(jsonMap);
            out.print(output.toString());
            
        } catch (ClassNotFoundException e) {
            out.print(e.toString());
        } catch (SQLException e) {
            out.print(e.toString());
        }

        // always remember to flush!
        out.flush();
            
    }
 
    /**
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
    }

}
