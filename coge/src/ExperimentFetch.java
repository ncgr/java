import org.coge.api.CoGe;
import org.coge.api.CoGeParameters;
import org.coge.api.CoGeResponse;
import org.coge.api.Experiment;
import org.coge.api.Genome;
import org.coge.api.Metadata;

import java.util.List;
import java.util.Map;

/**
 * Fetch an experiment.
 */
public class ExperimentFetch {

    static String COGE_PROPERTIES_FILE = "coge.properties";

    public static void main(String[] args) {

        if (args.length!=1) {
            System.out.println("Usage: ExperimentFetch <id>");
            System.exit(1);
        }

        try {

            int id = Integer.parseInt(args[0]);

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

            Experiment expt = coge.fetchExperiment(id);
            System.out.println("");
            System.out.println(expt.getId()+"\t"+expt.getName()+"\t"+expt.getDescription());
            System.out.println("link="+expt.getLink());
            System.out.println("version="+expt.getVersion());
            System.out.println("genome id="+expt.getGenomeId());
            System.out.println("source="+expt.getSource());
            System.out.println("restricted="+expt.isRestricted());
            Map<String,String> types = expt.getTypes();
            if (types!=null) {
                for (String key : types.keySet()) {
                    System.out.println(key+":"+types.get(key));
                }
            }
            List<Metadata> additionalMetadata = expt.getAdditionalMetadata();
            if (additionalMetadata!=null) {
                for (Metadata meta : additionalMetadata) {
                    System.out.println("typeGroup="+meta.getTypeGroup()+"\ttype="+meta.getType()+"\ttext="+meta.getText()+"\tlink="+meta.getLink());
                }
            }
            
        } catch (Exception e) {
            System.err.println(e);
        }

    }

}
