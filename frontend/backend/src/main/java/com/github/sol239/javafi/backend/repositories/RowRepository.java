package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Row entities.
 * Provides methods to perform CRUD operations on Row entities.
 */
@Repository
public interface RowRepository extends JpaRepository<Row, Long> {

    /**
     * Finds all Row entities that match the given extraData.
     * @param extraData the extra data to search for in Row entities
     * @return a list of Row entities that match the given extraData
     */
    List<Row> findByExtraData(String extraData);
}
