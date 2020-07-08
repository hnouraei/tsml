package tsml.transformers;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

public class DimensionIndependentTransformer implements Transformer {

    private Transformer transformer;
    // Need this to set the dataset on a newly created Instance.
    private Instances dataHeader;

    /**
     * DimensionIndependentTransformer - this class applies a given transformer
     * along each dimension given a multivariate time series.
     *
     * @param t - the transformer to be applied along each dimension.
     */
    public DimensionIndependentTransformer(Transformer t) {
        this.transformer = t;
    }

    @Override
    public Instance transform(Instance inst) {
        Instances dimensions = inst.relationalValue(0);
        Instances transformedInsts = transformer.transform(dimensions);
        Instance res = new DenseInstance(2);

        res.setDataset(dataHeader);
        int index = res.attribute(0).addRelation(transformedInsts);
        res.setValue(0,index);
        if (inst.classIndex() >= 0) {
            res.setClassValue(inst.classValue());
            res.setValue(1, inst.classValue());
        }
        return res;
    }

    @Override
    public Instances determineOutputFormat(Instances data) throws IllegalArgumentException {
        // Create the relation from the transformer
        Instances outputFormat = transformer.determineOutputFormat(data.attribute(0).relation());
        // Just 2 attributes, the relational attribute and the class value (if it has one).
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("relationalAtt",outputFormat));
        if(data.classIndex() >= 0) {
            attributes.add(data.classAttribute());
        }
        // Create the header to store the data in
        Instances result = new Instances("MultiDimensional_" + outputFormat.relationName(), attributes, data.numInstances());
        if (data.classIndex() >= 0) {
            result.setClassIndex(1);
        }
        this.dataHeader = result;
        return result;
    }
}

