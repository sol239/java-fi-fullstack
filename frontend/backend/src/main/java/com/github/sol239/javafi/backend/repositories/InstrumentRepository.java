package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstrumentRepository extends JpaRepository<Row, Long> {
    List<Row> findByExtraData(String extraData);
}
