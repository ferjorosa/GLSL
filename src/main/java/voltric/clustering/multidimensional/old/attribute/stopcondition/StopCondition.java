package voltric.clustering.multidimensional.old.attribute.stopcondition;

/**
 * La condicion de parada utilizada en el AttributeGrouping. Por ejemplo el UD Test
 */
public interface StopCondition {

    boolean isTrue();

    boolean isFalse();
}
