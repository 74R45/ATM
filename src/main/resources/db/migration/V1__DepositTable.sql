CREATE TABLE Deposit
(
    id              uuid    NOT NULL PRIMARY KEY,
    itn             integer NULL,
    FOREIGN KEY (itn) REFERENCES User (itn),
    expiration_     date    NOT NULL,
    deposited_money integer NOT NULL,
    accrued_money   integer NOT NULL
);