CREATE TABLE Account
(
    card_num       varchar(16) NOT NULL PRIMARY KEY,
    itn            int         NOT NULL,
    FOREIGN KEY (itn) REFERENCES User (itn),
    expiration     date        NOT NULL,
    is_credit_card boolean     NOT NULL,
    amount         decimal     NOT NULL,
    amount_credit  decimal     NOT NULL,
    PIN            int         NOT NULL
);