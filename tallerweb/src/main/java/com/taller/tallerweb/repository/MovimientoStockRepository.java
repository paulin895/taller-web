package com.taller.tallerweb.repository;

import com.taller.tallerweb.model.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    List<MovimientoStock> findByTallerId(Long tallerId);
}