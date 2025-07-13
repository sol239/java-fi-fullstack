package com.github.sol239.javafi.backend.repositories;

import com.github.sol239.javafi.backend.entity.Chart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartRepository extends JpaRepository<Chart,Long> {
}
