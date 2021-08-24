package experiments.latent;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.extension.data.DataUtils;
import eu.amidst.extension.util.BnSampler;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public interface LatentSyntheticExperiment {

    BayesianNetwork createModel(Random random, double multinomialThreshold, double normalThreshold);

    default DataOnMemory<DataInstance> sampleData(BayesianNetwork network, int nSamples, Random random) {
        DataOnMemory<DataInstance> sampledData = BnSampler.newSample(network, nSamples, random);
        List<Attribute> attributesForProjection = sampledData.getAttributes().getFullListOfAttributes()
                .stream().filter(x-> x.getName().startsWith("X")
                )
                .collect(Collectors.toList());

        return DataUtils.project(sampledData, attributesForProjection);
    }
}
