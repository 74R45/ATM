CREATE TABLE "User"
(
    itn              int          NOT NULL PRIMARY KEY,
    first_name       varchar(50)  NOT NULL,
    surname          varchar(50)  NOT NULL,
    patronymic       varchar(50)  NULL,
    login            varchar(30)  NOT NULL,
    password         varchar(30)  NOT NULL,
    control_question varchar(200) NOT NULL,
    answer_on_cq     varchar(50)  NOT NULL
);

CREATE TABLE "Administrator"
(
    login    varchar(30) NOT NULL PRIMARY KEY,
    password varchar(50) NOT NULL
);

CREATE TABLE "Account"
(
    card_num       varchar(16) NOT NULL PRIMARY KEY,
    itn            int         NOT NULL,
    FOREIGN KEY (itn) REFERENCES "User" (itn),
    expiration     date        NOT NULL,
    is_credit_card boolean     NOT NULL,
    amount         decimal     NOT NULL,
    amount_credit  decimal     NOT NULL,
    PIN            int         NOT NULL
);

CREATE TABLE "Transaction"
(
    id               uuid        NOT NULL PRIMARY KEY,
    sum              decimal(40) NOT NULL,
    date_time        date        NOT NULL,
    card_number_from varchar(16)     NOT NULL,
    FOREIGN KEY (card_number_from) REFERENCES "Account" (card_num),
    card_number_to   varchar(16)     NOT NULL
);

CREATE TABLE "Deposit"
(
    id              uuid    NOT NULL PRIMARY KEY,
    itn             integer NULL,
    FOREIGN KEY (itn) REFERENCES "User" (itn),
    expiration_     date    NOT NULL,
    deposited_money integer NOT NULL,
    accrued_money   integer NOT NULL
);