package experiments.latent.mixed;

import eu.amidst.extension.util.LogUtils;
import experiments.latent.LatentCrossValidationExperiment;
import methods.latent.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RunLatentMixedExperiments {

    public static void main(String[] args) throws Exception {

        long seed = 0;
        int run = 1;

        /* Preparamos los metodos que se van a utilizar en los experimentos */
        Set<LatentMixedMethod> methods = new LinkedHashSet<>();
//        methods.add(new VariationalLCM(seed));
//        methods.add(new ConstrainedIncrementalLearner(seed, 1, true, true, true, 3, false, false, new SimpleLocalVBEM()));
//        methods.add(new ConstrainedIncrementalLearner(seed, 10, true, true, true, 3, false, false, new SimpleLocalVBEM()));
//        methods.add(new IncrementalLearner(seed, false, true, true, new SimpleLocalVBEM()));
//        methods.add(new GLSL_Algorithm(seed, 3, 3, Integer.MAX_VALUE, 64, 1, GLSL_Algorithm.Initialization.EMPTY));
//        methods.add(new HC());

        /* Definimos los experimentos que se van a ejecutar */
        List<LatentCrossValidationExperiment> experimentList = new ArrayList<>();

        experimentList.add(new Exp_haberman(methods));
        experimentList.add(new Exp_iris(methods));
        experimentList.add(new Exp_user_knowledge(methods));
        experimentList.add(new Exp_vertebral(methods));
        experimentList.add(new Exp_ecoli(methods));
        experimentList.add(new Exp_planning_relax(methods));
        experimentList.add(new Exp_thoracic_surgery(methods));
        experimentList.add(new Exp_vehicle(methods));
        experimentList.add(new Exp_thyroid(methods));
        experimentList.add(new Exp_parkinsons(methods));
        experimentList.add(new Exp_autos(methods));
        experimentList.add(new Exp_ionosphere(methods));

        experimentList.add(new Exp_qsar_biodeg(methods));
        experimentList.add(new Exp_housing_prices(methods));
        experimentList.add(new Exp_census_india(methods));

        /* Ejecutamos los experimentos */
        for(LatentCrossValidationExperiment experiment: experimentList)
            experiment.runCrossValExperiment(seed, 10, run, LogUtils.LogLevel.INFO);

    }
}
