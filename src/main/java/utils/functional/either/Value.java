package utils.functional.either;

import utils.functional.either.combiner.Combiner;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

class Value<OTHERWISE, VALUE> implements Either<OTHERWISE, VALUE> {
    private final VALUE value;

    Value(VALUE value) {
        Objects.requireNonNull(value, "Value cannot be null.");
        this.value = value;
    }

    @Override
    public <R> Either<OTHERWISE, R> map(Function<VALUE, R> mapper) {
        return new Value<>(mapper.apply(value));
    }

    @Override
    public VALUE get() {
        return value;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public void consume(Consumer<VALUE> valueConsumer, Consumer<OTHERWISE> otherwiseConsumer) {
        valueConsumer.accept(value);
    }

    @Override
    public void combine(Combiner<OTHERWISE, VALUE, ?, ?> combiner) {
        combiner.combineValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Value))
            return false;
        Value<?, ?> value1 = (Value<?, ?>) o;
        return Objects.equals(value, value1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Value{" +
                "value=" + value +
                '}';
    }
}
