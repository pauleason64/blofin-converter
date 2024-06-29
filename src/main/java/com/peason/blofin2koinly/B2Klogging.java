package com.peason.blofin2koinly;

import java.util.Date;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class B2Klogging  {

 static class Filter implements java.util.logging.Filter {
        @Override
        public boolean isLoggable(LogRecord log) {
            //don't log CONFIG logs in file
            if (log.getLevel() == Level.CONFIG) return false;
            return true;
        }
    }

    static class Formatter extends java.util.logging.Formatter {
        @Override
        public String format(LogRecord record) {
            return   new Date(record.getMillis()) + "::"
                     + record.getSourceMethodName() + "::"
                    + record.getMessage() + "\n";
        }
    }
}
