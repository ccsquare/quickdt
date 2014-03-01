package quickdt.experiments.crossValidation;

import com.google.common.base.*;
import com.google.common.collect.Iterables;
import quickdt.*;

/**
 * Created by ian on 2/28/14.
 */
public class CrossValidator {
    private final Predicate<AbstractInstance> splitter;
    private final Supplier<? extends CrossValScorer<?>> scorerSupplier;

    /**
     * Create a new CrossValidator using an RMSECrossValScorer, generating a test
     * dataset from 1 in 10 instances selected randomly based on the has of
     * the Attributes in each Instance.
     */
    public CrossValidator() {
        this(new AttributesHashSplitter(10), RMSECrossValScorer.supplier);
    }

    /**
     * Create a new CrossValidator
     * @param splitter A Predicate which should return true if an instance should
     *                 be in the test dataset, and false if it should be in the
     *                 training dataset
     * @param scorerSupplier A supplier of CrossValScorers that should be used to test the
     *                       predictive model's performance
     */
    public CrossValidator(final Predicate<AbstractInstance> splitter, Supplier<? extends CrossValScorer<?>> scorerSupplier) {
        this.splitter = splitter;
        this.scorerSupplier = scorerSupplier;
    }

    public CrossValScorer<?> test(PredictiveModelBuilder<?> predictiveModelBuilder, Iterable<Instance> data) {
        Iterable<Instance> trainingData = Iterables.filter(data, Predicates.not(splitter));
        Iterable<Instance> testingData = Iterables.filter(data, splitter);
        PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingData);
        CrossValScorer<?> scorer = scorerSupplier.get();
        for (AbstractInstance instance : testingData) {
            scorer.score(predictiveModel.getProbability(instance.getAttributes(), instance.getClassification()));
        }
        return scorer;
    }
}