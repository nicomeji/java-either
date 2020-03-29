package utils.functional.either;

import utils.functional.either.combiner.Combiner;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Either<OTHERWISE, VALUE> {
    static <OTHERWISE, VALUE> Either<OTHERWISE, VALUE> value(VALUE value) {
        return new Value<>(value);
    }

    static <OTHERWISE, VALUE> Either<OTHERWISE, VALUE> otherwise(OTHERWISE u) {
        return new Otherwise<>(u);
    }

    <R> Either<OTHERWISE, R> map(Function<VALUE, R> mapper);

    VALUE get();

    boolean hasValue();

    void consume(Consumer<VALUE> valueConsumer, Consumer<OTHERWISE> errorConsumer);

    default void consume(Consumer<VALUE> valueConsumer) {
        consume(valueConsumer, (u) -> {});
    }

    void combine(Combiner<OTHERWISE, VALUE, ?, ?> combiner);
}
