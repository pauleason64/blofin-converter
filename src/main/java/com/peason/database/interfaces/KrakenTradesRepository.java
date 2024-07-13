package com.peason.database.interfaces;

import com.peason.database.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KrakenTradesRepository extends JpaRepository<KrakenTrades, Long> {
}

