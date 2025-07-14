package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.Chart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
    List<Chart> findByExtraData(String extraData);
}
