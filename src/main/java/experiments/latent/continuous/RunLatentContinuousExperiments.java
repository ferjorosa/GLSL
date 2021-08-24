package experiments.latent.continuous;

import eu.amidst.extension.util.LogUtils;
import experiments.latent.LatentCrossValidationExperiment;
import methods.latent.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RunLatentContinuousExperiments {

    public static void main(String[] args) throws Exception {
        long seed = 0;
        int kFolds = 10;
        int run = 1;
        LogUtils.LogLevel logLevel = LogUtils.LogLevel.INFO;

        Set<LatentContinuousMethod> methods = new LinkedHashSet<>();
//        methods.add(new GaussianLCM(seed));
//        methods.add(new ConstrainedIncrementalLearner(seed, 1, true, true, true, 3, false, false, new SimpleLocalVBEM()));
//        methods.add(new ConstrainedIncrementalLearner(seed, 10, true, true, true, 3, false, false, new SimpleLocalVBEM()));
//        methods.add(new GS(seed));
//        methods.add(new IncrementalLearner(seed, false, true, true, new SimpleLocalVBEM()));
//        methods.add(new GEAST("geast_settings.xml", seed));
//        methods.add(new GLSL_Algorithm(seed, 3, 3, Integer.MAX_VALUE, 64, 1, GLSL_Algorithm.Initialization.EMPTY));
//        methods.add(new HC());

        /* Definimos los experimentos que se van a ejecutar */
        List<LatentCrossValidationExperiment> experimentList = new ArrayList<>();

        experimentList.add(new Exp_real_state_valuation(methods));
        experimentList.add(new Exp_buddymove(methods));
        experimentList.add(new Exp_qsar_fish_toxicity(methods));
        experimentList.add(new Exp_qsar_aqua_toxicity(methods));
        experimentList.add(new Exp_ilpd(methods));
        experimentList.add(new Exp_alcohol(methods));
        experimentList.add(new Exp_travel_reviews(methods));
        experimentList.add(new Exp_wine_quality_white(methods));
        experimentList.add(new Exp_wine(methods));
        experimentList.add(new Exp_leaf(methods));
        experimentList.add(new Exp_nba(methods));
        experimentList.add(new Exp_wdbc(methods));

        experimentList.add(new Exp_waveform(methods));
        experimentList.add(new Exp_100_plants(methods));
        experimentList.add(new Exp_geo_music(methods));

        /* Ejecutamos los experimentos */
        for(LatentCrossValidationExperiment experiment: experimentList)
            experiment.runCrossValExperiment(seed, kFolds, run, LogUtils.LogLevel.INFO);
    }
}
