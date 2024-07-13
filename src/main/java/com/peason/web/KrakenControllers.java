package com.peason.web;

import com.peason.database.KrakenLedgers;
import com.peason.database.KrakenTrades;
import com.peason.database.interfaces.KrakenLedgerRepository;
import com.peason.database.interfaces.KrakenTradesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class KrakenControllers {

    @Autowired
    private KrakenLedgerRepository krakenLedgerRepository;

    @Autowired
    private KrakenTradesRepository krakenTradesRepository;

    @GetMapping("/ledgers")
    public Page<KrakenLedgers> getRecordsA(@RequestParam int page, @RequestParam int size) {
        return krakenLedgerRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/trades")
    public Page<KrakenTrades> getRecordsB(@RequestParam int page, @RequestParam int size) {
        return krakenTradesRepository.findAll(PageRequest.of(page, size));
    }
}

