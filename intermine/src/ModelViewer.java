import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import org.intermine.metadata.AttributeDescriptor;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.CollectionDescriptor;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.metadata.Model;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

/**
 * Query and view an InterMine model. Enter the IM service URL as a parameter.
 * 
 * @author Sam Hokin
 */
public class ModelViewer {
    
    /**
     * Perform the model query and print out the results.
     * @param args command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        if (args.length!=1) {
            System.out.println("Usage: ModelViewer <intermine service URL>");
            System.exit(0);
        }

        String intermineServiceUrl = args[0];
        
        ServiceFactory factory = new ServiceFactory(intermineServiceUrl);
        Model model = factory.getModel();

        Set<ClassDescriptor> classDescriptors = model.getClassDescriptors();
        for (ClassDescriptor cd : classDescriptors) {

            String simpleName = cd.getSimpleName();
            Set<String> superclassNames = new HashSet<String>();
            for (ClassDescriptor superclassDescriptor : cd.getAllSuperDescriptors()) {
                String superclassName = superclassDescriptor.getSimpleName();
                if (!superclassName.equals(simpleName)) superclassNames.add(superclassName);
            }

            System.out.println("----------------------------------------------------------------------------");
            System.out.println(simpleName+":"+superclassNames);

            Set<AttributeDescriptor> attributeDescriptors = cd.getAttributeDescriptors();
            Set<String> attrNames = new HashSet<String>();
            for (AttributeDescriptor attDescriptor : attributeDescriptors) {
                attrNames.add(attDescriptor.getName());
            }
            if (attrNames.size()>0) System.out.println("Attributes:"+attrNames);

            Set<ReferenceDescriptor> referenceDescriptors = cd.getReferenceDescriptors();
            Set<String> refNames = new HashSet<String>();
            for (ReferenceDescriptor refDescriptor : referenceDescriptors) {
                refNames.add(refDescriptor.getName());
            }
            if (refNames.size()>0) System.out.println("References:"+refNames);

            Set<CollectionDescriptor> collectionDescriptors = cd.getCollectionDescriptors();
            Set<String>collNames = new HashSet<String>();
            for (CollectionDescriptor collDescriptor : collectionDescriptors) {
                collNames.add(collDescriptor.getName());
            }
            if (collNames.size()>0) System.out.println("Collections:"+collNames);

        }

    }

}

