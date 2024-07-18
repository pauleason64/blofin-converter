package com.peason.services;

import com.peason.databasetables.Ledger;
import com.peason.databasetables.Trade;
import com.peason.model.DBTable;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class KrakenDAO extends DAO{
    public void insertLedgers(HashMap<String, Ledger> ledgers, String tableName) {
        String sql =dbServer.getTable(tableName).getInsertStatement();
        ArrayList<Object[]> sqlArgs = new ArrayList<>();
        for (Map.Entry<String,Ledger> ledger : ledgers.entrySet()) {
            ledger.getValue().setLedgerKey(ledger.getKey());
            //URGENT todo: fix the profile passing
            ledger.getValue().setProfileid(1);
            ledger.getValue().setSourceid(1);
            sqlArgs.add(ledger.getValue().getFieldsForInsertQuery());
            //break;
        }
        try {
            jdbcTemplate.batchUpdate(sql, sqlArgs);
        } catch (DataAccessException de) {
            System.out.println(de.getStackTrace());
        }

   }

    public void insertTrades(HashMap<String, Trade> trades, String tableName) {
        String sql =dbServer.getTable(tableName).getInsertStatement();
        ArrayList<Object[]> sqlArgs = new ArrayList<>();
        for (Map.Entry<String,Trade> trade : trades.entrySet()) {
            trade.getValue().setTradeKey(trade.getKey());
            //URGENT todo: fix the profile passing
            trade.getValue().setProfileid(1);
            trade.getValue().setSourceid(1);
            sqlArgs.add(trade.getValue().getFieldsForInsertQuery());
        }
        jdbcTemplate.batchUpdate(sql,sqlArgs);
    }

    public  List<Ledger> refreshLedgersFromDB() {
        //todo add filters
        DBTable table=dbServer.getTable("ledgers");
        String sql=table.getSelectStatement();
        //parameterise later
        sql+= " where profileid=1 and sourceid=1";
        List<Ledger> ledgers=jdbcTemplate.query(sql,new BeanPropertyRowMapper<Ledger>(Ledger.class));
        return ledgers;
    }

    public  List<Trade> refreshTradesFromDB() {
        //todo add filters
        DBTable table=dbServer.getTable("trades");
        String sql=table.getSelectStatement();
        //parameterise later
        sql+= " where profileid=1 and sourceid=1";
        List<Trade> trades=jdbcTemplate.query(sql,new BeanPropertyRowMapper<Trade>(Trade.class));
        return trades;
    }
}
