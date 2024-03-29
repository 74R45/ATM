CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DELETE FROM transaction;
DELETE FROM account;
DELETE FROM deposit;
DELETE FROM person;
DELETE FROM administrator;

INSERT INTO person(itn, first_name, surname, patronymic, login, password, control_question, answer_on_cq)
VALUES('573829562815', 'name', 'surname', null, 'login', 'password', 'what is your favourite colour', 'indigo');

INSERT INTO account(card_num, itn, expiration, is_credit_card, is_blocked, deletion_time, amount, amount_credit, credit_limit, next_credit_time, PIN, attempts_left)
VALUES('7474122309583265','573829562815','2020-06-22 19:10:25-07',false,false,null,1234.56,0.00,0.00,null,'1111', 3),
      ('7474122309583266','573829562815','2020-06-22 19:10:25-07',false,false,null,500.50,0.00,0.00,null,'1111', 3);

INSERT INTO administrator(login, password)
VALUES('admin','admin');

INSERT INTO deposit(id, itn, expiration, deposited_money)
VALUES(uuid_generate_v4(), '573829562815', '2020-06-22 19:10:25-07', 1500.00);

-- INSERT INTO account(card_num, itn, expiration, is_credit_card, is_blocked, amount, amount_credit, credit_limit, PIN, attempts_left)
-- VALUES('5168122309583267','573829562815','2020-06-22 19:10:25-07',true,false,777.77,500.50,4000.00,'this should be hashed but whatever', 3);
