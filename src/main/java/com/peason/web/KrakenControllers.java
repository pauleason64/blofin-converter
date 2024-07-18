package com.peason.web;

import com.peason.databasetables.LEDGERS;
import com.peason.databasetables.TRADES;
import com.peason.persistance.ServersAndTablesRepository;
import com.peason.interfaces.KrakenLedgerRepository;
import com.peason.interfaces.KrakenTradesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class KrakenControllers {

    @Autowired
    ServersAndTablesRepository serversAndTablesRepository;

    //@Autowired
    private KrakenLedgerRepository krakenLedgerRepository;

    //@Autowired
    private KrakenTradesRepository krakenTradesRepository;

    @GetMapping("/ledgers")
    public Page<LEDGERS> getRecordsA(@RequestParam int page, @RequestParam int size) {
        return krakenLedgerRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/trades")
    public Page<TRADES> getRecordsB(@RequestParam int page, @RequestParam int size) {
        return krakenTradesRepository.findAll(PageRequest.of(page, size));
    }
}

