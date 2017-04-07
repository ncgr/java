import org.coge.api.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.Resty;
import us.monoid.web.JSONResource;

/**
 * Test the CoGe class.
 */
public class CoGeTester {

    static String COGE_PROPERTIES_FILE = "coge.properties";
        
    public static void main(String[] args) {
        
        try {

            // get the CoGe auth params and initialize token
            CoGeParameters cogeParams = new CoGeParameters(COGE_PROPERTIES_FILE);

            try {
                cogeParams.initializeToken();
            } catch (Exception e) {
                System.err.println("Error initializing token:");
                System.err.println(e.toString());
                System.exit(1);
            }

            if (!cogeParams.hasToken()) {
                System.err.println("Error: couldn't get CoGe token.");
                System.exit(1);
            } else {
                System.out.println("Token: "+cogeParams.getToken());
            }
            
            // instantiate our CoGe workhorse
            CoGe coge = new CoGe(cogeParams.getBaseURL(), cogeParams.getUser(), cogeParams.getToken());

            // THIS PARTICULAR TEST FOLLOWS

            String name = "Vigna radiata (mung bean)";
            
            String description = "cellular organisms; Eukaryota; Viridiplantae; Streptophyta; Streptophytina; Embryophyta; Tracheophyta; Euphyllophyta; Spermatophyta; Magnoliophyta; Mesangiospermae; eudicotyledons; Gunneridae; Pentapetalae; rosids; fabids; Fabales; Fabaceae; Papilionoideae; Phaseoleae; Vigna";

            System.out.println("Attempting to add organism:\t"+name);
            System.out.println("\t\tDescription:\t"+description);
            System.out.println("");
            
            CoGeResponse response = coge.addOrganism(name, description);

            System.out.println(response.toString());
            
        } catch (Exception ex) {

            System.err.println(ex.toString());
            System.exit(1);

        }

    }

}
        
        

