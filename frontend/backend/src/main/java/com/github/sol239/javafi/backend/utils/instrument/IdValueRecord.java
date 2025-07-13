/**
 * The package provides the instrument utils.
 */
package com.github.sol239.javafi.backend.utils.instrument;

/**
 * Record class with two fields: id and value.
 * @param id represents the id column from the table - id = 14 representes 14th row in the table
 * @param value represents the value on the given row - id
 */
public record IdValueRecord(Long id, Double value) {

    /**
     * Returns a string representation of the IdValueRecord.
     * @return a string representation of the IdValueRecord
     */
    @Override
    public String toString() {
        return "ID=" + id + ", VALUE=" + value;
    }
}
