package com.peason.utils;
public class DateUtil {

    public final static String DB2_TIMESTAMP = "yyyy-MM-dd-HH.mm.ss.SSSSSS";
    public final static String DB2_TIMESTAMP_WITHOUT_NANOS = "yyyy-MM-dd-HH.mm.ss";
    public final static String FTM_TIMESTAMP = "yyyyMMddHHmmss";
    public final static String SQL_TIMESTAMP_WITHOUT_NANOS = "yyyy-MM-dd'T'HH:mm:ss";

    public static String SQLTimestampStringToDB2FormatString(String timestamp) throws java.text.ParseException {
        //this is crude implementation but it works.
        //Source format will be like 2021-03-11 15.05.30.12.123Z , need to return DB2 format
        //eg 2021-03-11-15.05:30:12.123000
        timestamp=timestamp.replace("Z","");
        int idx=timestamp.lastIndexOf(".",19);
        timestamp=idx>-1 ? timestamp.concat("000000").substring(0,26) : timestamp.concat(".000000");
        return timestamp;
    }

    public static String DB2TimestampStringToSQLFormatString(String timestamp) throws java.text.ParseException {
        //this is crude implementation but it works.
        //Source format will be like 2021-03-11 15.05.30.12.123000 , need to return DB2 format
        //eg 2021-03-11 15:05:30.12.123000
        int idx=timestamp.lastIndexOf(".",19);
        String dt=idx>-1 ? timestamp.substring(0,idx-1) : timestamp;
        String ms=idx>-1 ? String.format("%1$.6s",timestamp.substring(idx)) : "000000";
        String response=timestamp.replace("-"," ").replace(".",":").concat(".").concat(ms);
        return  response;
    }

}

