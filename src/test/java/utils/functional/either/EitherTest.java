package utils.functional.either;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class EitherTest {
    @Test(expected = NullPointerException.class)
    public void valueCannotBeNull() {
        Either.value(null);
    }

    @Test(expected = NullPointerException.class)
    public void otherwiseCannotBeNull() {
        Either.otherwise(null);
    }

    @Test
    public void valueCanBeRetrievedFromEither() {
        assertThat(Either.value(2).get(), is(2));
    }

    @Test(expected = NoSuchElementException.class)
    public void otherwiseDoesNotHaveValue() {
        Either.otherwise(2).get();
    }

    @Test(expected = RuntimeException.class)
    public void otherwiseCanThrowCustomExceptions() {
        Either.otherwise(new RuntimeException()).get();
    }

    @Test(expected = IllegalArgumentException.class)
    public void otherwiseCanOnlyContainRuntimeExceptions() {
        Either.otherwise(new Throwable()).get();
    }

    @Test
    public void mapIsAppliedToEitherValue() {
        assertThat(Either.value(BigDecimal.ZERO).map(BigDecimal.ONE::add).get(), is(BigDecimal.ONE));
    }

    @Test
    public void mapHasNoEffectIfThereIsNoValue() {
        AtomicBoolean isExecuted = new AtomicBoolean(false);
        Either<BigDecimal, BigDecimal> result = Either.<BigDecimal, BigDecimal>otherwise(BigDecimal.ZERO)
                .map(number -> {
                    isExecuted.set(true);
                    return BigDecimal.ONE.add(number);
                });
        assertFalse(isExecuted.get());
        assertFalse(result.hasValue());
    }

    @Test
    public void accessEitherValue() {
        AtomicBoolean hasValue = new AtomicBoolean(false);
        Either.value(2).consume((value) -> hasValue.set(true));
        assertTrue(hasValue.get());
    }

    @Test
    public void accessEitherInnerState_1() {
        AtomicBoolean hasValue = new AtomicBoolean(false);
        AtomicBoolean hasOther = new AtomicBoolean(false);
        Either.value(2).consume((value) -> hasValue.set(true), (otherwise) -> hasOther.set(true));
        assertTrue(hasValue.get());
        assertFalse(hasOther.get());
    }

    @Test
    public void accessEitherInnerState_2() {
        AtomicBoolean hasValue = new AtomicBoolean(false);
        AtomicBoolean hasOther = new AtomicBoolean(false);
        Either.otherwise(2).consume((value) -> hasValue.set(true), (otherwise) -> hasOther.set(true));
        assertFalse(hasValue.get());
        assertTrue(hasOther.get());
    }
}
