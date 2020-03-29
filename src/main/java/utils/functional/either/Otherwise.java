package utils.functional.either;

import utils.functional.either.combiner.Combiner;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

class Otherwise<OTHERWISE, VALUE> implements Either<OTHERWISE, VALUE> {
    private final OTHERWISE otherwise;

    Otherwise(OTHERWISE otherwise) {
        Objects.requireNonNull(otherwise, "Error cannot be null.");
        if (otherwise instanceof Throwable && !(otherwise instanceof RuntimeException)) {
            throw new IllegalArgumentException("Either can only wrap RuntimeExceptions");
        }
        this.otherwise = otherwise;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Otherwise<OTHERWISE, T> map(Function<VALUE, T> mapper) {
        return (Otherwise<OTHERWISE, T>) this;
    }

    @Override
    public VALUE get() {
        if (otherwise instanceof RuntimeException) {
            throw (RuntimeException) otherwise;
        } else {
            throw new NoSuchElementException("No value present");
        }
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public void consume(Consumer<VALUE> valueConsumer, Consumer<OTHERWISE> otherwiseConsumer) {
        otherwiseConsumer.accept(otherwise);
    }

    @Override
    public void combine(Combiner<OTHERWISE, VALUE, ?, ?> combiner) {
        combiner.combineError(otherwise);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Otherwise))
            return false;
        Otherwise<?, ?> other = (Otherwise<?, ?>) o;
        return Objects.equals(this.otherwise, other.otherwise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(otherwise);
    }

    @Override
    public String toString() {
        return "Otherwise{" +
                "error=" + otherwise +
                '}';
    }
}
