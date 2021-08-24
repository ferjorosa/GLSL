package experiments.missing.discrete;

import eu.amidst.extension.learn.parameter.InitializationTypeVBEM;
import eu.amidst.extension.learn.parameter.InitializationVBEM;
import eu.amidst.extension.learn.parameter.VBEMConfig;
import eu.amidst.extension.learn.parameter.penalizer.NoPenalizer;
import eu.amidst.extension.learn.structure.hillclimber.BayesianHcConfig;
import eu.amidst.extension.util.LogUtils;
import methods.missing.MeanImputation;
import methods.missing.MissingDiscreteMethod;

import java.util.LinkedHashSet;
import java.util.Set;

public class Run_discrete_experiments {

    public static void main(String[] args) throws Exception {
        long seed = 0;
        int nRuns = 5;
        boolean storeModels = false;

        LogUtils.LogLevel logLevel = LogUtils.LogLevel.INFO;
        int maxNumberOfParents = 3;
        Set<MissingDiscreteMethod> methods = new LinkedHashSet<>();

        /* Missing VBSEM configuration */
        InitializationVBEM initializationVBEM = new InitializationVBEM(InitializationTypeVBEM.PYRAMID, 64, 16, false);
        VBEMConfig vbemConfig = new VBEMConfig(seed, 0.01, 100, initializationVBEM, new NoPenalizer());
        BayesianHcConfig bayesianHcConfig = new BayesianHcConfig(seed, 0.01, 100);

        /* Methods */
        //methods.add(new MissingVBSEM(vbemConfig, bayesianHcConfig, 100, maxNumberOfParents));
        methods.add(new MeanImputation());

        for(double missingPercentage = 0.1; missingPercentage < 0.6; missingPercentage += 0.1) {

            /* Run experiments */
            Exp_hiv_test exp_hiv_test = new Exp_hiv_test(methods);
            exp_hiv_test.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_hayes_roth exp_hayes_roth = new Exp_hayes_roth(methods);
            exp_hayes_roth.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_balance_scale exp_balance_scale = new Exp_balance_scale(methods);
            exp_balance_scale.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_car_evaluation exp_car_evaluation = new Exp_car_evaluation(methods);
            exp_car_evaluation.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_nursery exp_nursery = new Exp_nursery(methods);
            exp_nursery.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_breast_cancer exp_breast_cancer = new Exp_breast_cancer(methods);
            exp_breast_cancer.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_web_phishing exp_web_phishing = new Exp_web_phishing(methods);
            exp_web_phishing.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_solar_flare exp_solar_flare = new Exp_solar_flare(methods);
            exp_solar_flare.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_zoo exp_zoo = new Exp_zoo(methods);
            exp_zoo.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_vote exp_vote = new Exp_vote(methods);
            exp_vote.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_spect_heart exp_spect_heart = new Exp_spect_heart(methods);
            exp_spect_heart.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_alarm exp_alarm = new Exp_alarm(methods);
            exp_alarm.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_coil_42 exp_coil_42 = new Exp_coil_42(methods);
            exp_coil_42.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_news_100 exp_news_100 = new Exp_news_100(methods);
            exp_news_100.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_webkb_336 exp_webkb_336 = new Exp_webkb_336(methods);
            exp_webkb_336.runExperiment(missingPercentage, nRuns, storeModels, logLevel);
        }
    }
}
