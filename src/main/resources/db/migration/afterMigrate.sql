CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DELETE FROM account;
DELETE FROM person;

INSERT INTO person(itn, first_name, surname, patronymic, login, password, control_question, answer_on_cq)
VALUES('573829562815', 'name', 'surname', null, 'login', 'password', 'what do you want from me', 'nothin');

INSERT INTO account(card_num, itn, expiration, is_credit_card, is_blocked, amount, amount_credit, credit_limit, PIN)
VALUES('5168122309583265','573829562815','2020-06-22 19:10:25-07',false,false,1234.56,0.00,0.00,'this should be hashed but whatever');