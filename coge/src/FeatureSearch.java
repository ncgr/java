import org.coge.api.CoGe;
import org.coge.api.CoGeParameters;
import org.coge.api.CoGeResponse;
import org.coge.api.Feature;
import org.coge.api.Genome;
import org.coge.api.Metadata;

import java.util.List;

/**
 * Search for features.
 */
public class FeatureSearch {

    static String COGE_PROPERTIES_FILE = "coge.properties";

    public static void main(String[] args) {

        if (args.length!=1) {
            System.out.println("Usage: GenomeFeatures <search term>");
            System.exit(1);
        }

        try {

            String searchTerm = args[0];

            // get the CoGe auth params and initialize token
            CoGeParameters cogeParams = new CoGeParameters(COGE_PROPERTIES_FILE);

            // initialize Agave/CoGe token
            try {
                cogeParams.initializeToken();
            } catch (Exception e) {
                System.err.println("Error initializing token:");
                System.err.println(e.toString());
                System.exit(1);
            }
            
            if (cogeParams.hasToken()) {
                System.out.println("");
                System.out.println("CoGe baseURL:\t"+cogeParams.getBaseURL());
                System.out.println("CoGe Token:\t"+cogeParams.getToken());
            } else {
                System.err.println("Error: couldn't get CoGe token.");
                System.exit(1);
            }
            
            // instantiate our CoGe workhorse
            CoGe coge = new CoGe(cogeParams.getBaseURL(), cogeParams.getUser(), cogeParams.getToken());

            // search for features
            List<Feature> features = coge.searchFeature(searchTerm);
            System.out.println("");
            System.out.println(features.size()+" features found.");
            
        } catch (Exception e) {
            System.err.println(e);
        }

    }

}
