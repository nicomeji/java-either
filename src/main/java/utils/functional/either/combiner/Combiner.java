package utils.functional.either.combiner;

import utils.functional.either.Either;

/**
 * @param <ERROR> the Either's error type. Input elements to the error reduction operation.
 * @param <VALUE> the Either's value type. Input elements to the value reduction operation.
 * @param <R_ERROR> the result type of the error reduction operation
 * @param <R_VALUE> the result type of the value reduction operation
 */
public interface Combiner<ERROR, VALUE, R_ERROR, R_VALUE> {
    void combineValue(VALUE value);

    void combineError(ERROR error);

    Either<R_ERROR, R_VALUE> result();
}
