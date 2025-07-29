package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing instrument data.
 */
@Repository
public interface InstrumentRepository extends JpaRepository<Row, Long> {

    /**
     * Finds all rows with the specified extra data.
     * @param extraData the extra data to search for
     * @return a list of rows that match the extra data
     */
    List<Row> findByExtraData(String extraData);
}
