package com.github.sol239.javafi.backend.utils.instrument;

import com.github.sol239.javafi.backend.entity.Instrument;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.lang.reflect.Modifier;
import java.util.Set;
import org.reflections.Reflections;

import com.github.sol239.javafi.backend.utils.instrument.JavaInstrument;
import com.github.sol239.javafi.backend.utils.instrument.instruments.ValidateInstrument;

public class InstrumentValidator {

    public JavaInstrument[] availableInstruments;

    public InstrumentValidator() {


    }

    public void checkInstrumentAnnotations() {
        ServiceLoader<JavaInstrument> loader = ServiceLoader.load(JavaInstrument.class);

        for (JavaInstrument instrument : loader) {
            boolean foundAnnotation = false;
            for (Annotation a : instrument.getClass().getAnnotations()) {
                if (a.annotationType().equals(ValidateInstrument.class)) {
                    System.out.println("Instrument " + instrument.getName() + " is annotated with @ValidateInstrument. ✅");
                    foundAnnotation = true;
                    break;
                }
            }
            if (!foundAnnotation) {
                System.out.println("Instrument " + instrument.getName() + " is not annotated with @ValidateInstrument. ⛔");
            }
        }
    }

    public void validateInstruments(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> instrumentClasses = reflections.getTypesAnnotatedWith(ValidateInstrument.class);




        // for each available instrument, check if it has the ValidateInstrument annotation
        if (instrumentClasses.isEmpty()) {
            System.out.println("No instrument classes found in package: " + basePackage);
            return;
        }

        for (Class<?> clazz : instrumentClasses) {
            System.out.println("Validating: " + clazz.getName());

            if (!JavaInstrument.class.isAssignableFrom(clazz)) {
                throw new RuntimeException(clazz.getName() + " must implement JavaInstrument. ⛔");
            }

            try {
                JavaInstrument instance = (JavaInstrument) clazz.getDeclaredConstructor().newInstance();

                if (instance.getName() == null || instance.getName().isBlank()) {
                    System.out.println("- " + clazz.getName() + ": getName() must return a non-empty string. ⛔");
                } else {
                    System.out.println("- Instrument name is valid ✅.");
                }

                if (instance.getDescription() == null || instance.getDescription().isBlank()) {
                    System.out.println("- " + clazz.getName() + ": getDescription() must return a non-empty string. ⛔");
                } else {
                    System.out.println("- Instrument description is valid ✅.");
                }

                if (instance.getColumnNames() == null || instance.getColumnNames().length == 0) {
                    System.out.println("- " + clazz.getName() + ": getColumnNames() must return a non-empty array. ⛔");
                } else {
                    System.out.println("- Instrument column names are valid ✅.");
                }


            } catch (Exception e) {
                throw new RuntimeException("Validation failed for " + clazz.getName() + ": " + e.getMessage() + " ⛔", e);
            }
        }

    }

    public static void main(String[] args) {
        InstrumentValidator validator = new InstrumentValidator();

        String basePackage = "com.github.sol239.javafi.backend.utils.instrument.instruments";

        validator.checkInstrumentAnnotations();

        System.out.println("-----------------------------------------------------------------------");

        validator.validateInstruments(basePackage);

        System.out.println("-----------------------------------------------------------------------");

    }

}


