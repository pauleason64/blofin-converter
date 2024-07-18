CREATE DATABASE IF NOT EXISTS crypto_trans;
USE crypto_trans;

CREATE TABLE IF NOT EXISTS krakenledgers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    refid VARCHAR(50),
    tradedt datetime,
    asset VARCHAR(50),
    tradetype VARCHAR(50),
    subtype VARCHAR(50),
    cost double,
    fee double,
    balance double

);


CREATE TABLE IF NOT EXISTS krakentrades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tradetype VARCHAR(50),
    tradedt datetime,
    pair VARCHAR(50),
    otype VARCHAR(50),
    volume double,
    price double,
    fee double,
    cost double,
	margin double,
    txid varchar(50),
    tradeid varchar(50),
    ledgers varchar (200)

);


CREATE TABLE IF NOT EXISTS cryptorates (
    rateDt datetime,
    fromCurr varchar(10),
    toCurr varchar(10),
    exrate double
);


CREATE TABLE IF NOT EXISTS fiatrates (
    rateDt datetime,
    baseCurr varchar(10),
    toCurr varchar(10),
    exrate double
);


CREATE TABLE IF NOT EXISTS blofintrades (
    otype varchar(10),
    fromCurr varchar(10),
    toCurr varchar(10),
    orderDt datetime,
    rate double,
    expectedrate varchar(25),
    fromVolume double,
    toVolume double,
	fee double
);
CREATE TABLE IF NOT EXISTS sources (
	sourceName varchar(20),
    apiKey varchar(100),
    apiPK  varchar (200),
    status varchar(15),
    fromDate datetime,
    toDate datetime,
    tradeCount integer,
    ledgerCount integer,
    autoRefresh boolean,
    nextRefreshDate datetime
);









