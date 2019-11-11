CREATE TABLE Transaction
(
    id               uuid        NOT NULL PRIMARY KEY,
    sum              decimal(40) NOT NULL,
    date_time        date        NOT NULL,
    card_number_from integer     NOT NULL,
    FOREIGN KEY (card_number_from) REFERENCES Account(card_num),
    card_number_to   integer     NOT NULL
);