import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.ncgr.svm.Sample;
import org.ncgr.svm.SvmUtil;

import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameter;
import edu.berkeley.compbio.jlibsvm.ImmutableSvmParameterGrid;
import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblemImpl;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationProblem;
import edu.berkeley.compbio.jlibsvm.binary.BinaryClassificationSVM;
import edu.berkeley.compbio.jlibsvm.binary.C_SVC;
import edu.berkeley.compbio.jlibsvm.binary.SvmBinaryCrossValidationResults;
import edu.berkeley.compbio.jlibsvm.kernel.GaussianRBFKernel;
import edu.berkeley.compbio.jlibsvm.kernel.KernelFunction;
import edu.berkeley.compbio.jlibsvm.scaler.LinearScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.scaler.ScalingModelLearner;
import edu.berkeley.compbio.jlibsvm.util.SparseVector;

/**
 * Test the objects and methods in jlibsvm.
 */
public class SvmTester {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        if (args.length==0) {
            System.err.println("Usage: SvmTester <svm-file>");
            System.exit(1);
        }

        String svmFile = args[0];

        Class labelClass = String.class;
        Map<SparseVector,String> examples = new HashMap<>();
        Map<SparseVector,Integer> exampleIds = new HashMap<>();
        
        // NOTE: The SVM data should already be scaled!
        // 116386.0 case 1:-1.0 2:-1.0 3:-1.0 4:-1.0 ...
        // 123133.0 ctrl 1:0 2:0 3:0 4:0 ...
        int exampleId = 0;
        List<Sample> samples = SvmUtil.readSamples(svmFile);
        for (Sample sample : samples) {
            SparseVector example = new SparseVector(sample.values.size());
            int j = 0;
            for (int index : sample.values.keySet()) {
                example.indexes[j] = index;
                example.values[j] = sample.values.get(index).floatValue();
                j++;
            }
            examples.put(example, sample.label);
            exampleIds.put(example, exampleId++);
        }

        BinaryClassificationProblem<String,SparseVector> bcp = new BinaryClassificationProblemImpl<>(labelClass, examples, exampleIds);

        ImmutableSvmParameterGrid.Builder builder = new ImmutableSvmParameterGrid.Builder();
        // DEFAULTS
        builder.nu = 0.5f;
        builder.cache_size = 100;
        builder.eps = 1e-3f;
        builder.p = 0.1f;
        builder.shrinking = false; // true
        builder.probability = false;
        builder.redistributeUnbalancedC = false; // true
        builder.crossValidationFolds = 10;

        // NON-DEFAULTS
        float C = 0.03125f;
        builder.Cset = new HashSet<Float>();
        builder.Cset.add(C);
        float gamma = 0.125f;
        builder.kernelSet = new HashSet<KernelFunction>();
        builder.kernelSet.add(new GaussianRBFKernel(gamma));

        ImmutableSvmParameter param = builder.build();

        BinaryClassificationSVM svm = new C_SVC();
        svm.validateParam(param);

        SvmBinaryCrossValidationResults results = svm.performCrossValidation(bcp, param);

        // results methods:
        // absFalseBalance, accuracy, accuracyGivenClassified,
        // classNormalizedSensitivity, falseBalance, falseFalseRate, falseTrueRate,
        // getNumExamples, precisionA, precisionB, sensitivityA, sensitivityB,
        // toString, trueFalseRate, trueTrueRate, unknown

        System.out.println(results.toString());
    }
}
