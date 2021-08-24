package experiments.missing.continuous;

import eu.amidst.extension.learn.parameter.InitializationTypeVBEM;
import eu.amidst.extension.learn.parameter.InitializationVBEM;
import eu.amidst.extension.learn.parameter.VBEMConfig;
import eu.amidst.extension.learn.parameter.penalizer.NoPenalizer;
import eu.amidst.extension.learn.structure.hillclimber.BayesianHcConfig;
import eu.amidst.extension.util.LogUtils;
import methods.missing.MeanImputation;
import methods.missing.MissingContinuousMethod;

import java.util.LinkedHashSet;
import java.util.Set;

public class Run_continuous_experiments {

    public static void main(String[] args) throws Exception {

        long seed = 0;
        int nRuns = 5;
        boolean storeModels = false;

        LogUtils.LogLevel logLevel = LogUtils.LogLevel.INFO;
        int maxNumberOfParents = 3;
        Set<MissingContinuousMethod> methods = new LinkedHashSet<>();

        /* Missing VBSEM configuration */
        InitializationVBEM initializationVBEM = new InitializationVBEM(InitializationTypeVBEM.PYRAMID, 64, 16, false);
        VBEMConfig vbemConfig = new VBEMConfig(seed, 0.01, 100, initializationVBEM, new NoPenalizer());
        BayesianHcConfig bayesianHcConfig = new BayesianHcConfig(seed, 0.01, 100);

        /* Methods */
        //methods.add(new MissingVBSEM(vbemConfig, bayesianHcConfig, 100, maxNumberOfParents));
        methods.add(new MeanImputation());

        for(double missingPercentage = 0.1; missingPercentage < 0.6; missingPercentage += 0.1) {

            /* Run experiments */

            Exp_real_state_valuation exp_real_state_valuation = new Exp_real_state_valuation(methods);
            exp_real_state_valuation.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_buddymove exp_buddymove = new Exp_buddymove(methods);
            exp_buddymove.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_qsar_fish_toxicity exp_qsar_fish_toxicity = new Exp_qsar_fish_toxicity(methods);
            exp_qsar_fish_toxicity.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_qsar_aqua_toxicity exp_qsar_aqua_toxicity = new Exp_qsar_aqua_toxicity(methods);
            exp_qsar_aqua_toxicity.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_ilpd exp_ilpd = new Exp_ilpd(methods);
            exp_ilpd.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_alcohol exp_alcohol = new Exp_alcohol(methods);
            exp_alcohol.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_travel_reviews exp_travel_reviews = new Exp_travel_reviews(methods);
            exp_travel_reviews.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_wine_quality_white exp_wine_quality_white = new Exp_wine_quality_white(methods);
            exp_wine_quality_white.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_wine exp_wine = new Exp_wine(methods);
            exp_wine.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_leaf exp_leaf = new Exp_leaf(methods);
            exp_leaf.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_nba exp_nba = new Exp_nba(methods);
            exp_nba.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_wdbc exp_wdbc = new Exp_wdbc(methods);
            exp_wdbc.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_waveform exp_waveform = new Exp_waveform(methods);
            exp_waveform.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_geo_music exp_geo_music = new Exp_geo_music(methods);
            exp_geo_music.runExperiment(missingPercentage, nRuns, storeModels, logLevel);

            Exp_100_plants exp_100_plants = new Exp_100_plants(methods);
            exp_100_plants.runExperiment(missingPercentage, nRuns, storeModels, logLevel);
        }
    }
}
