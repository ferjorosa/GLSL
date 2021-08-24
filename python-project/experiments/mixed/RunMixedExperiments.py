from experiments.mixed import Exp_autos
from experiments.mixed import Exp_census_india
from experiments.mixed import Exp_ecoli
from experiments.mixed import Exp_haberman
from experiments.mixed import Exp_housing_prices
from experiments.mixed import Exp_ionosphere
from experiments.mixed import Exp_iris
from experiments.mixed import Exp_parkinsons
from experiments.mixed import Exp_planning_relax
from experiments.mixed import Exp_qsar_biodeg
from experiments.mixed import Exp_thoracic_surgery
from experiments.mixed import Exp_thyroid
from experiments.mixed import Exp_user_knowledge
from experiments.mixed import Exp_vehicle
from experiments.mixed import Exp_vertebral


def main():
    run = 1
    n_folds = 10
    fold_log = True
    kde_bw = "normal_reference"

    exp = Exp_haberman.Exp_haberman("haberman")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_iris.Exp_iris("iris")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_user_knowledge.Exp_user_knowledge("user_knowledge")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_vertebral.Exp_vertebral("vertebral")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_ecoli.Exp_ecoli("ecoli")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_planning_relax.Exp_planning_relax("planning_relax")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_thoracic_surgery.Exp_thoracic_surgery("thoracic_surgery")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_vehicle.Exp_vehicle("vehicle")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_thyroid.Exp_thyroid("thyroid")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_parkinsons.Exp_parkinsons("parkinsons")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_autos.Exp_autos("autos")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_ionosphere.Exp_ionosphere("ionosphere")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_qsar_biodeg.Exp_qsar_biodeg("qsar_biodeg")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_housing_prices.Exp_housing_prices("housing_prices")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_census_india.Exp_census_india("census_india")
    exp.run(run, n_folds, fold_log, kde_bw)


if __name__ == "__main__":
    main()