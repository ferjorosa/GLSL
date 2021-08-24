package experiments.latent.discrete;

import eu.amidst.extension.util.LogUtils;
import experiments.latent.LatentCrossValidationExperiment;
import methods.latent.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RunLatentDiscreteExperiments {

    public static void main(String[] args) throws Exception {

        long seed = 0;
        int kFolds = 10;
        int run = 1;

        /* Preparamos los metodos que se van a utilizar en los experimentos */
        Set<LatentDiscreteMethod> methods = new LinkedHashSet<>();
//        methods.add(new BinA(seed, BLFM_BinA.LinkageType.AVERAGE));
//        methods.add(new LCM(seed));
//        methods.add(new ConstrainedIncrementalLearner(seed, 10, false, true, true, 3, false, false, new SimpleLocalVBEM()));
//        methods.add(new ConstrainedIncrementalLearner(seed, 1, false, true, true, 3, false, false, new SimpleLocalVBEM()));
//        methods.add(new IncrementalLearner(seed, false, true, true, new SimpleLocalVBEM()));
//        methods.add(new GLSL_Algorithm(seed, 3, 3, Integer.MAX_VALUE, 64, 1, GLSL_Algorithm.Initialization.CIL_1));
//        methods.add(new GLSL_Algorithm(seed, 3, 3, Integer.MAX_VALUE, 64, 1, GLSL_Algorithm.Initialization.EMPTY));
//        methods.add(new HC());

        /* Definimos los experimentos que se van a ejecutar */
        List<LatentCrossValidationExperiment> experimentList = new ArrayList<>();

        experimentList.add(new Exp_hiv_test(methods));
        experimentList.add(new Exp_hayes_roth(methods));
        experimentList.add(new Exp_balance_scale(methods));
        experimentList.add(new Exp_car_evaluation(methods));
        experimentList.add(new Exp_nursery(methods));
        experimentList.add(new Exp_breast_cancer(methods));
        experimentList.add(new Exp_web_phishing(methods));
        experimentList.add(new Exp_solar_flare(methods));
        experimentList.add(new Exp_zoo(methods));
        experimentList.add(new Exp_vote(methods));
        experimentList.add(new Exp_spect_heart(methods));

        experimentList.add(new Exp_alarm(methods));
        experimentList.add(new Exp_coil_42(methods));
        experimentList.add(new Exp_webkb_336(methods));

        /* Ejecutamos los experimentos */
        for(LatentCrossValidationExperiment experiment: experimentList)
            experiment.runCrossValExperiment(seed, kFolds, run, LogUtils.LogLevel.INFO);

    }
}
