from experiments.mixed import MixedExperiment
from spn.structure.StatisticalTypes import MetaType


class Exp_housing_prices(MixedExperiment.MixedExperiment):

    # 66 attributes after filtering with 10 folds
    meta_types = [MetaType.REAL, MetaType.DISCRETE, MetaType.REAL, MetaType.DISCRETE, MetaType.DISCRETE,
                  MetaType.DISCRETE, MetaType.DISCRETE, MetaType.DISCRETE, MetaType.DISCRETE, MetaType.REAL,
                  MetaType.REAL, MetaType.REAL, MetaType.REAL, MetaType.DISCRETE, MetaType.DISCRETE,
                  MetaType.DISCRETE, MetaType.REAL, MetaType.DISCRETE, MetaType.DISCRETE, MetaType.DISCRETE,
                  MetaType.DISCRETE, MetaType.DISCRETE, MetaType.DISCRETE, MetaType.DISCRETE, MetaType.REAL,
                  MetaType.DISCRETE, MetaType.REAL, MetaType.REAL, MetaType.REAL, MetaType.DISCRETE,
                  MetaType.DISCRETE, MetaType.DISCRETE, MetaType.DISCRETE, MetaType.REAL, MetaType.REAL,
                  MetaType.REAL, MetaType.REAL, MetaType.REAL, MetaType.REAL, MetaType.REAL,
                  MetaType.REAL, MetaType.REAL, MetaType.REAL, MetaType.DISCRETE, MetaType.REAL,
                  MetaType.DISCRETE, MetaType.REAL, MetaType.DISCRETE, MetaType.REAL, MetaType.DISCRETE,
                  MetaType.REAL, MetaType.REAL, MetaType.DISCRETE, MetaType.DISCRETE, MetaType.DISCRETE,
                  MetaType.REAL, MetaType.REAL, MetaType.REAL, MetaType.REAL, MetaType.REAL,
                  MetaType.REAL, MetaType.REAL, MetaType.DISCRETE, MetaType.DISCRETE,  MetaType.DISCRETE,
                  MetaType.REAL]
    var_types_string = "cucuuuuuuccccuuucuuuuuuucucccuuuuccccccccccucucucuccuuucccccccuuuc"

    def run(self, run: int, n_folds: int, fold_log: bool, kde_bw: str):
        print("\n------------------------------------------------------------------")
        print("------------------------------------------------------------------")
        print("-------------------------- HOUSING_PRICES ------------------------")
        print("------------------------------------------------------------------")
        print("------------------------------------------------------------------\n")

        super().run(run, n_folds, fold_log, kde_bw)


def main():
    run = 1
    n_folds = 10
    kde_bw = "normal_reference"
    data_name = "housing_prices"
    fold_log = True
    exp = Exp_housing_prices(data_name)
    exp.run(run, n_folds, fold_log, kde_bw)


if __name__ == "__main__":
    main()

