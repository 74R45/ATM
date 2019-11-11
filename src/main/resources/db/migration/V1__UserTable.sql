CREATE TABLE User
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