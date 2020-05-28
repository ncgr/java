import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;

import java.text.DecimalFormat;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomTree;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
 
public class WekaTest {

    public static int KFOLD = 10;
    public static DecimalFormat pf = new DecimalFormat("0.0%");

    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;
        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }
        return inputReader;
    }
 
    public static Evaluation classify(Classifier model, Instances trainingSet, Instances testingSet) throws Exception {
        Evaluation evaluation = new Evaluation(trainingSet);
        model.buildClassifier(trainingSet);
        evaluation.evaluateModel(model, testingSet);
        return evaluation;
    }
 	
    public static Map<Double,Integer> calculateCorrect(ArrayList predictions) {
	Map<Double,Integer> correct = new HashMap<>();
	correct.put(0.0, 0);
	correct.put(1.0, 0);
        for (int i=0; i<predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.get(i);
            if (np.predicted()==np.actual()) {
		correct.put(np.actual(), correct.get(np.actual())+1);
	    }
        }
        return correct;
    }
    
    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
        Instances[][] split = new Instances[2][numberOfFolds];
        for (int i = 0; i < numberOfFolds; i++) {
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }
        return split;
    }
 
    public static void main(String[] args) throws Exception {
        if (args.length!=1) {
            System.err.println("Usage: WekaTest <arff filename>");
            System.exit(1);
        }

        String arffFile = args[0];
        BufferedReader datafile = readDataFile(arffFile);
 
        Instances data = new Instances(datafile);
        // remove the ID attribute
        data.deleteAttributeAt(0);
        // set the class attribute index
        data.setClassIndex(data.numAttributes() - 1);

	// store case/control attributes
	Attribute classAttribute = data.classAttribute();
	Map<String,Integer> classAttributes = new HashMap<>();
	classAttributes.put(classAttribute.value(0),0);
	classAttributes.put(classAttribute.value(1),1);

	// get totals per case/control
	Map<Double,Integer> classTotals = new HashMap<>();
	classTotals.put(0.0, 0);
	classTotals.put(1.0, 0);
	for (Enumeration<Instance> e = data.enumerateInstances(); e.hasMoreElements();) {
	    double classValue = e.nextElement().classValue();
	    classTotals.put(classValue, classTotals.get(classValue)+1);
	}
 
        // Do KFOLD-split cross validation
        Instances[][] split = crossValidationSplit(data, KFOLD);
 
        // Separate split into training and testing arrays
        Instances[] trainingSplits = split[0];
        Instances[] testingSplits = split[1];
 
        // Use a set of classifiers
        Classifier[] models = { 
            new J48(),           // a decision tree
            new PART(),          // PART decision list
            new DecisionTable(), // decision table majority classifier
            new DecisionStump(), // one-level decision tree
            new RandomTree()     // random tree
        };

        // Run for each model
	boolean first = true;
        for (int j = 0; j < models.length; j++) {
            // Collect every group of predictions for current model in a ArrayList
            ArrayList<Prediction> predictions = new ArrayList<>();
            // For each training-testing split pair, train and test the classifier
            for (int i = 0; i < trainingSplits.length; i++) {
                Evaluation validation = classify(models[j], trainingSplits[i], testingSplits[i]);
                predictions.addAll(validation.predictions());
                // Uncomment to see the summary for each training-testing pair.
                // System.err.println(models[j].toString());
            }
            // calculate overall correct of current classifier on all splits
            Map<Double,Integer> correct = calculateCorrect(predictions);
            // Print current classifier's name and correct in a complicated, but nice-looking way.
	    double caseKey = classAttributes.get("case");
	    double ctrlKey = classAttributes.get("ctrl");
	    int caseCorrect = correct.get(caseKey);
	    int ctrlCorrect = correct.get(ctrlKey);
	    int caseTotal = classTotals.get(caseKey);
	    int ctrlTotal = classTotals.get(ctrlKey);
	    double caseFraction = (double)caseCorrect / (double)caseTotal;
	    double ctrlFraction = (double)ctrlCorrect / (double)ctrlTotal;
	    int totalCorrect = caseCorrect + ctrlCorrect;
	    int totalTotal = caseTotal + ctrlTotal;
	    double totalFraction = (double)totalCorrect / (double)totalTotal;
	    String modelName = models[j].getClass().getSimpleName();
	    if (first) {
		// header
		System.out.println("WekaTest\ttotal\tcase\tcontrol\ttotal\tcase\tcontrol");
		first = false;
	    }
	    if (modelName.length()<6) modelName += "\t";
            System.out.println(modelName+"\t"+
			       totalCorrect+"/"+totalTotal+"\t"+
			       caseCorrect+"/"+caseTotal+"\t"+
			       ctrlCorrect+"/"+ctrlTotal+"\t"+
			       pf.format(totalFraction)+"\t"+
			       pf.format(caseFraction)+"\t"+
			       pf.format(ctrlFraction));
	}
    }
}
