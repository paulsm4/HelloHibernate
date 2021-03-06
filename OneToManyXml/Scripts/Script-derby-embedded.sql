--
-- Derby Embedded syntax
--
CREATE TABLE APP.stock (
  STOCK_ID integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  STOCK_CODE varchar(10) NOT NULL,
  STOCK_NAME varchar(20) NOT NULL,
  PRIMARY KEY (STOCK_ID),
  UNIQUE (STOCK_NAME, STOCK_CODE)
) 

CREATE TABLE  APP.stock_daily_record (
  RECORD_ID integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  PRICE_OPEN float DEFAULT NULL,
  PRICE_CLOSE float DEFAULT NULL,
  PRICE_CHANGE float DEFAULT NULL,
  VOLUME bigint DEFAULT NULL,
  DATE date NOT NULL,
  STOCK_ID integer NOT NULL,
  PRIMARY KEY (RECORD_ID),
  CONSTRAINT FK_STOCK_TRANSACTION_STOCK_ID FOREIGN KEY (STOCK_ID)
  REFERENCES APP.stock (STOCK_ID) ON DELETE CASCADE
)