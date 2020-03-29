package utils.functional.either;

import org.junit.Assert;
import org.junit.Test;
import utils.functional.either.combiner.Combiner;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class StreamCombinerTest {
    @Test
    public void testCollectStreamToLists() {
        Stream<Either<String, Integer>> stream = Stream.of(Either.value(3), Either.value(4), Either.value(5));
        Either<List<String>, List<Integer>> result = stream.collect(new Collecting());

        Assert.assertEquals(Either.value(asList(3, 4, 5)), result);
    }

    private class Collecting implements MyCollector<String, Integer, List<String>, List<Integer>> {
        @Override
        public Supplier<MyCombiner<String, Integer, List<String>, List<Integer>>> supplier() {
            return () -> new MyCombiner<>(
                    new LinkedList<>(),
                    new LinkedList<>(),
                    List::add,
                    List::add,
                    Predicate.not(List::isEmpty));
        }

        @Override
        public BiConsumer<MyCombiner<String, Integer, List<String>, List<Integer>>, Either<String, Integer>> accumulator() {
            return (combiner, either) -> either.combine(combiner);
        }

        @Override
        public BinaryOperator<MyCombiner<String, Integer, List<String>, List<Integer>>> combiner() {
            return (combiner1, combiner2) -> {
                combiner1.errorAccumulator.addAll(combiner2.errorAccumulator);
                combiner1.valueAccumulator.addAll(combiner2.valueAccumulator);
                return combiner1;
            };
        }

        @Override
        public Function<MyCombiner<String, Integer, List<String>, List<Integer>>, Either<List<String>, List<Integer>>> finisher() {
            return Combiner::result;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

    interface MyCollector<ERROR, VALUE, R_ERROR, R_VALUE> extends
            Collector<Either<ERROR, VALUE>, MyCombiner<ERROR, VALUE, R_ERROR, R_VALUE>, Either<R_ERROR, R_VALUE>> {
    }

    private static class MyCombiner<ERROR, VALUE, R_ERROR, R_VALUE> implements
            Combiner<ERROR, VALUE, R_ERROR, R_VALUE> {
        private R_ERROR errorAccumulator;
        private R_VALUE valueAccumulator;
        private BiConsumer<R_ERROR, ERROR> accumulateError;
        private BiConsumer<R_VALUE, VALUE> accumulateValue;
        private Predicate<R_ERROR> hasError;

        public MyCombiner(
                R_ERROR errorAccumulator,
                R_VALUE valueAccumulator,
                BiConsumer<R_ERROR, ERROR> accumulateError,
                BiConsumer<R_VALUE, VALUE> accumulateValue,
                Predicate<R_ERROR> hasError) {
            this.errorAccumulator = errorAccumulator;
            this.valueAccumulator = valueAccumulator;
            this.accumulateError = accumulateError;
            this.accumulateValue = accumulateValue;
            this.hasError = hasError;
        }

        @Override
        public void combineValue(VALUE value) {
            accumulateValue.accept(valueAccumulator, value);
        }

        @Override
        public void combineError(ERROR error) {
            accumulateError.accept(errorAccumulator, error);
        }

        @Override
        public Either<R_ERROR, R_VALUE> result() {
            if (hasError.test(errorAccumulator)) {
                return Either.otherwise(errorAccumulator);
            } else {
                return Either.value(valueAccumulator);
            }
        }
    }
}
