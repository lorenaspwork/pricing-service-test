CREATE TABLE BRAND (
                       ID INTEGER PRIMARY KEY,
                       NAME VARCHAR(50) NOT NULL
);

CREATE TABLE PRICES (
                        ID INTEGER PRIMARY KEY,
                        BRAND_ID INTEGER NOT NULL,
                        PRODUCT_ID INTEGER NOT NULL,
                        START_DATE TIMESTAMP NOT NULL,
                        END_DATE TIMESTAMP NOT NULL,
                        PRICE_LIST INTEGER NOT NULL,
                        PRIORITY INTEGER NOT NULL,
                        PRICE DECIMAL(10,2) NOT NULL,
                        CURRENCY_ISO_CODE VARCHAR(3) NOT NULL,

                        CONSTRAINT FK_PRICES_BRAND
                            FOREIGN KEY (BRAND_ID)
                                REFERENCES BRAND(ID),

                        CONSTRAINT CK_PRICES_DATE_RANGE
                            CHECK (END_DATE >= START_DATE),

                        CONSTRAINT CK_PRICES_PRICE
                            CHECK (PRICE >= 0)
);

CREATE INDEX IDX_PRICES_PRIORITY
    ON PRICES (PRODUCT_ID, BRAND_ID, PRIORITY DESC);
