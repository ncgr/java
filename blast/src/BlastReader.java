package org.ncgr.blast;

import javax.xml.bind.JAXBException;

/**
 * A simply stub main method to run BlastUtils.readBlastXML.
 *
 * @author Sam Hokin
 */
public class BlastReader {
    
    public static void main(String[] args) {

        if (args.length==0) {
            System.err.println("Usage: BlastReader <blast.xml>");
            System.exit(1);
        }
        
        try {
            BlastUtils.readBlastXML(args[0]);
        } catch (JAXBException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
            
    }
    
}
