package eu.amidst.extension.learn.structure.glsl.operator;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import eu.amidst.extension.learn.parameter.VBEM;
import eu.amidst.extension.learn.parameter.VBEMConfig;
import eu.amidst.extension.learn.structure.hillclimber.BayesianHcConfig;
import eu.amidst.extension.learn.structure.hillclimber.operator.BayesianHcAddArc;
import eu.amidst.extension.learn.structure.hillclimber.operator.BayesianHcOperator;
import eu.amidst.extension.learn.structure.hillclimber.operator.BayesianHcRemoveArc;
import eu.amidst.extension.learn.structure.hillclimber.operator.BayesianHcReverseArc;
import eu.amidst.extension.learn.structure.vbsem.InitializationVBSEM;
import eu.amidst.extension.learn.structure.vbsem.VBSEM_restrictions;
import eu.amidst.extension.util.GraphUtilsAmidst;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.PriorsFromData;
import eu.amidst.extension.util.tuple.Tuple2;
import eu.amidst.extension.util.tuple.Tuple3;

import java.util.*;
import java.util.stream.Collectors;

public class GLSL_RemoveLatent implements GLSL_Operator {

    private int maxNumberParents_latent;

    private int maxNumberParents_observed;

    private VBEMConfig vbemConfig;

    private BayesianHcConfig bayesianHcConfig;

    private InitializationVBSEM initializationVBSEM;

    public GLSL_RemoveLatent(int maxNumberParents_latent,
                             int maxNumberParents_observed,
                             VBEMConfig vbemConfig,
                             BayesianHcConfig bayesianHcConfig,
                             InitializationVBSEM initializationVBSEM) {
        this.maxNumberParents_latent = maxNumberParents_latent;
        this.maxNumberParents_observed = maxNumberParents_observed;
        this.vbemConfig = vbemConfig;
        this.bayesianHcConfig = bayesianHcConfig;
        this.initializationVBSEM = initializationVBSEM;
    }

    @Override
    public Tuple3<String, BayesianNetwork, Double> apply(DAG dag, DataOnMemory<DataInstance> data, LogUtils.LogLevel logLevel) {


        BayesianNetwork bestModel = null;
        double bestModelScore = -Double.MAX_VALUE;

        for(Variable variable: dag.getVariables()) {

            if (!variable.isObservable()
                    && variable.isDiscrete()) {

                Variables copyVariables = dag.getVariables().deepCopy();
                DAG copyDag = dag.deepCopy(copyVariables);
                Variable copyVariable = copyVariables.getVariableByName(variable.getName());

                for(Variable child: GraphUtilsAmidst.getChildren(copyVariable, copyDag))
                    copyDag.getParentSet(child).removeParent(copyVariable);

                copyDag.removeVariable(copyVariable);
                copyVariables.remove(copyVariable);
                for(int i = 0; i < copyVariables.getListOfVariables().size(); i++) { // Update varID accordingly
                    Variable copyVar = copyVariables.getListOfVariables().get(i);
                    copyVar.setVarID(i);
                }

                /* Apply VBEM to generate a base model for VBSEM */
                Map<String, double[]> priors = PriorsFromData.generate(data, 1);
                VBEM vbem = new VBEM(vbemConfig);
                double baseScore = vbem.learnModelWithPriorUpdate(data, copyDag, priors);
                BayesianNetwork baseModel = vbem.getLearntBayesianNetwork();

                /* Apply VBSEM to generate a new model */
                Tuple2<BayesianNetwork, Double> result;
                try {
                    Set<BayesianHcOperator> bayesianHcOperators = initializeBayesianHcOperators(baseModel);
                    VBSEM_restrictions vbsem_restrictions = new VBSEM_restrictions(vbemConfig, bayesianHcConfig, initializationVBSEM, 100);
                    result = vbsem_restrictions.learnModel(baseModel, baseScore, data, bayesianHcOperators, LogUtils.LogLevel.NONE, LogUtils.LogLevel.NONE);
                } catch(IllegalStateException e) { // NaN value (there is a bug inside AMIDST for inference with EF_BaseDistribution_MultinomialParents.getExpectedNaturalToParent(EF_BaseDistribution_MultinomialParents.java:440))
                    result = new Tuple2<>(null, -Double.MAX_VALUE);
                }
                LogUtils.info("RL " + copyVariable + " -> " + result.getSecond(), logLevel);

                if(result.getSecond() > bestModelScore) {
                    bestModel = result.getFirst();
                    bestModelScore = result.getSecond();
                }
            }
        }

        return new Tuple3<>("RemoveLatent", bestModel, bestModelScore);
    }

    /** Initialize Hc operators with the default arc restrictions discussed at the beginning of the file */
    private Set<BayesianHcOperator> initializeBayesianHcOperators(BayesianNetwork model) {
        List<Variable> latentVars = model.getVariables().getListOfVariables().stream().filter(x->!x.isObservable()).collect(Collectors.toList());
        List<Variable> observedVars = model.getVariables().getListOfVariables().stream().filter(x->x.isObservable()).collect(Collectors.toList());
        Map<Variable, List<Variable>> cannotAddOrReverseArcs = new HashMap<>();
        for(Variable observedVar: observedVars)
            cannotAddOrReverseArcs.put(observedVar, latentVars);

        Map<Variable, Integer> maxNumberParents = new HashMap<>();
        for(Variable latentVar: latentVars)
            maxNumberParents.put(latentVar, this.maxNumberParents_latent);
        for(Variable observedVar: observedVars)
            maxNumberParents.put(observedVar, this.maxNumberParents_observed);

        BayesianHcAddArc bayesianHcAddArc = new BayesianHcAddArc(bayesianHcConfig, model.getVariables(), cannotAddOrReverseArcs, maxNumberParents);
        BayesianHcRemoveArc bayesianHcRemoveArc = new BayesianHcRemoveArc(bayesianHcConfig, new HashMap<>());
        BayesianHcReverseArc bayesianHcReverseArc = new BayesianHcReverseArc(bayesianHcConfig, model.getVariables(), cannotAddOrReverseArcs, maxNumberParents);

        Set<BayesianHcOperator> bayesianHcOperators = new LinkedHashSet<>(3);
        bayesianHcOperators.add(bayesianHcAddArc);
        bayesianHcOperators.add(bayesianHcRemoveArc);
        bayesianHcOperators.add(bayesianHcReverseArc);

        return bayesianHcOperators;
    }
}
