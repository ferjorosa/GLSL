package experiments.missing.mixed;

import eu.amidst.extension.learn.parameter.InitializationTypeVBEM;
import eu.amidst.extension.learn.parameter.InitializationVBEM;
import eu.amidst.extension.learn.parameter.VBEMConfig;
import eu.amidst.extension.learn.parameter.penalizer.NoPenalizer;
import eu.amidst.extension.learn.structure.hillclimber.BayesianHcConfig;
import eu.amidst.extension.util.LogUtils;
import methods.missing.MeanImputation;
import methods.missing.MissingMixedMethod;

import java.util.LinkedHashSet;
import java.util.Set;

public class Run_mixed_experiments {

    public static void main(String[] args) throws Exception {
        long seed = 0;
        int nRuns = 5;
        boolean storeModels = false;

        LogUtils.LogLevel logLevel = LogUtils.LogLevel.INFO;
        int maxNumberOfParents = 3;
        Set<MissingMixedMethod> methods = new LinkedHashSet<>();

        /* Missing VBSEM configuration */
        InitializationVBEM initializationVBEM = new InitializationVBEM(InitializationTypeVBEM.PYRAMID, 64, 16, false);
        VBEMConfig vbemConfig = new VBEMConfig(seed, 0.01, 100, initializationVBEM, new NoPenalizer());
        BayesianHcConfig bayesianHcConfig = new BayesianHcConfig(seed, 0.01, 100);

        /* Methods */
        //methods.add(new MissingVBSEM(vbemConfig, bayesianHcConfig, 100, maxNumberOfParents));
        methods.add(new MeanImputation());

        /* Run experiments */
        for(double missingPercentage = 0.1; missingPercentage < 0.6; missingPercentage += 0.1) {

            Exp_haberman exp_haberman = new Exp_haberman(methods);
            exp_haberman.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_iris exp_iris = new Exp_iris(methods);
            exp_iris.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_user_knowledge exp_user_knowledge = new Exp_user_knowledge(methods);
            exp_user_knowledge.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_vertebral exp_vertebral = new Exp_vertebral(methods);
            exp_vertebral.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_ecoli exp_ecoli = new Exp_ecoli(methods);
            exp_ecoli.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_planning_relax exp_planning_relax = new Exp_planning_relax(methods);
            exp_planning_relax.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_thoracic_surgery exp_thoracic_surgery = new Exp_thoracic_surgery(methods);
            exp_thoracic_surgery.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_vehicle exp_vehicle = new Exp_vehicle(methods);
            exp_vehicle.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_thyroid exp_thyroid = new Exp_thyroid(methods);
            exp_thyroid.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_parkinsons exp_parkinsons = new Exp_parkinsons(methods);
            exp_parkinsons.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_autos exp_autos = new Exp_autos(methods);
            exp_autos.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_ionosphere exp_ionosphere = new Exp_ionosphere(methods);
            exp_ionosphere.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_qsar_biodeg exp_qsar_biodeg = new Exp_qsar_biodeg(methods);
            exp_qsar_biodeg.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_housing_prices exp_housing_prices = new Exp_housing_prices(methods);
            exp_housing_prices.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_census_india exp_census_india = new Exp_census_india(methods);
            exp_census_india.runExperiment(missingPercentage, nRuns, storeModels, logLevel);
        }
    }
}
