package org.ncgr.pubag;

import org.ncgr.pubag.xml.Doc;
import org.ncgr.pubag.xml.Response;
import org.ncgr.pubag.xml.Result;

import java.io.IOException;

import java.math.BigInteger;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Static methods and command-line utility for populating Abstract objects from PubAg.
 */
public class Pubag {

    /**
     * Unmarshal a Response PubAg URI. If no Docs are available, return null.
     *
     * @param uri full PubAg search URI
     * @return a Response
     */
    public static Response getResponse(String uri) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Response.class);
        try {
            return (Response) context.createUnmarshaller().unmarshal(new StreamSource(uri));
        } catch (javax.xml.bind.UnmarshalException ex) {
            // assume we just ran out of content for this URI
            return null;
        }
    }

    /**
     * Return the BigInt numFound from a Response, else null.
     *
     * @param response the Response
     * @return the numFound
     */
    static BigInteger getNumFound(Response response) {
        for (Object o : response.getLstOrResult()) {
            if (o instanceof Result) {
                return ((Result) o).getNumFound();
            }
        }
        return null;
    }

    /**
     * Return a List of Docs from a given Response, null if none exist.
     *
     * @param response the Response
     * @return a List of Doc objects
     */
    static List<Doc> getDocs(Response response) {
        for (Object o : response.getLstOrResult()) {
            if (o instanceof Result) {
                return ((Result) o).getDoc();
            }
        }
        return null;
    }

    /**
     * Return a List of Abstracts that match a given search term in the abstract OR title text. Provide the page and per_page limit.
     *
     * @param term the search term
     * @param page the page to be retrieved
     * @param perpage the number per page to be retrieved
     * @param apikey PubAg API key
     * @return a List of Abstract, empty if none found
     */
    public static List<Abstract> searchAbstractOrTitleText(String term, int page, int perpage, String apikey) throws JAXBException, IOException {
        List<Abstract> abstracts = new ArrayList<>();
        String uri = "https://api.nal.usda.gov/pubag/rest/search/?format=xml&page=" + page + "&per_page=" + perpage + "&api_key=" + apikey +
            "&query=title:" + URLEncoder.encode(term,"UTF-8") + "%20OR%20abstract:" + URLEncoder.encode(term,"UTF-8");
        Response response = getResponse(uri);
        if (response != null) {
            List<Doc> docs = getDocs(response);
            for (Doc doc : docs) {
                abstracts.add(new Abstract(doc));
            }
        }
        return abstracts;
    }

    /**
     * Command-line utility.
     */
    public static void main(String[] args) throws JAXBException, IOException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option apikeyOption = new Option("key", "apikey", true, "PubAg API key");
        apikeyOption.setRequired(true);
        options.addOption(apikeyOption);
        
        Option termOption = new Option("t", "term", true, "value of term for PubAg search");
        termOption.setRequired(true);
        options.addOption(termOption);

        Option pageOption = new Option("p", "page", true, "page number to retrieve [1]");
        pageOption.setRequired(false);
        options.addOption(pageOption);
        
        Option perpageOption = new Option("n", "perpage", true, "number of records per page [20]");
        perpageOption.setRequired(false);
        options.addOption(perpageOption);
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("Pubag", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("Pubag", options);
            System.exit(1);
            return;
        }

        String apikey = cmd.getOptionValue("apikey");

        int page = 1;
        if (cmd.hasOption("page")) page = Integer.parseInt(cmd.getOptionValue("page"));

        int perpage = 20;
        if (cmd.hasOption("perpage")) perpage = Integer.parseInt(cmd.getOptionValue("perpage"));
        
        String term = cmd.getOptionValue("term");

        List<Abstract> abstracts = searchAbstractOrTitleText(term, page, perpage, apikey);

        // output only complete abstracts
        int count = 0;
        for (Abstract a : abstracts) {
            if (a.isComplete()) {
                System.out.println(a.toString());
                count++;
            }
        }

        // concluding diagnostics
        if (abstracts.size()==0) {
            System.err.println(perpage + ":" + page + " " + term + " NONE");
        } else {
            System.err.println(perpage + ":" + page + " " + term + " " + abstracts.size() + ":" + count);
        }
    }
}
