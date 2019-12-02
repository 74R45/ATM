CREATE TABLE person
(
    itn              varchar(12)  NOT NULL PRIMARY KEY,
    first_name       varchar(50)  NOT NULL,
    surname          varchar(50)  NOT NULL,
    patronymic       varchar(50)  NULL,
    login            varchar(30)  NOT NULL,
    password         varchar(64)  NOT NULL,
    control_question varchar(200) NOT NULL,
    answer_on_cq     varchar(64)  NOT NULL
);

CREATE TABLE administrator
(
    login    varchar(30) NOT NULL PRIMARY KEY,
    password varchar(64) NOT NULL
);

CREATE TABLE account
(
    card_num       varchar(16) NOT NULL PRIMARY KEY,
    itn            varchar(12) NOT NULL REFERENCES person (itn),
    expiration     timestamp   NOT NULL,
    is_credit_card boolean     NOT NULL,
    is_blocked     boolean     NOT NULL,
    deletion_time  timestamp   NULL,
    amount         decimal     NOT NULL,
    amount_credit  decimal     NOT NULL,
    credit_limit   decimal     NOT NULL,
    next_credit_time timestamp NULL,
    PIN            varchar(64) NOT NULL,
    attempts_left  int         NOT NULL
);

CREATE TABLE transaction
(
    id               uuid        NOT NULL PRIMARY KEY,
    sum              decimal     NOT NULL,
    date_time        timestamp   NOT NULL,
    card_number_from varchar(16) NOT NULL
        REFERENCES account(card_num) ON DELETE CASCADE,
    card_number_to   varchar(16) NOT NULL
);

CREATE TABLE deposit
(
    id              uuid        NOT NULL PRIMARY KEY,
    itn             varchar(12) NOT NULL REFERENCES person (itn),
    expiration      timestamp   NOT NULL,
    deposited_money decimal     NOT NULL
);