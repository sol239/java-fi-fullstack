package com.github.sol239.javafi.backend.utils.instrument;

import java.util.ArrayList;
import java.util.List;

// TODO: More of validation will be added as part of advanced java course
//  - validation of instruments
//  - validation before instrument is created
//  - name validation, ...

/**
 * Instrument validator.
 */
public class InstrumentValidator {
    public static List<JavaInstrument> getInstrumentHavingDuplicateName(JavaInstrument[] instrumentNames) {
        List<JavaInstrument> duplicates = new ArrayList<>();
        for (int i = 0; i < instrumentNames.length; i++) {
            for (int j = i + 1; j < instrumentNames.length; j++) {
                if (instrumentNames[i].getName().equals(instrumentNames[j].getName())) {
                    duplicates.add(instrumentNames[i]);
                    duplicates.add(instrumentNames[j]);
                }
            }
        }
        return duplicates;
    }

    /**
     * Check if instrument names are unique.
     * @param instrumentNames the instrument names
     * @return true if instrument names are unique, false otherwise
     */
    public static boolean areInstrumentNamesUnique(JavaInstrument[] instrumentNames) {
        List<JavaInstrument> duplicates = getInstrumentHavingDuplicateName(instrumentNames);
        return duplicates.isEmpty();
    }


}
