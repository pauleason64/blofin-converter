package com.peason.interfaces;

import com.peason.databasetables.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KrakenTradesRepository extends JpaRepository<Trade, Long> {
}

