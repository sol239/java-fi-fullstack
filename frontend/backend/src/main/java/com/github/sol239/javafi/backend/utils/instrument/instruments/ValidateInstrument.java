package com.github.sol239.javafi.backend.utils.instrument.instruments;

import java.lang.annotation.*;

/**
 * Annotation to mark an instrument for validation.
 * This annotation can be used to indicate that the instrument should be validated
 * before execution or processing.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateInstrument {
}
