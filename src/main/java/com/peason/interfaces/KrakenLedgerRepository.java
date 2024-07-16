package com.peason.interfaces;

import com.peason.databasetables.KRAKENLEDGERS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KrakenLedgerRepository extends JpaRepository<KRAKENLEDGERS, Long> {
}

