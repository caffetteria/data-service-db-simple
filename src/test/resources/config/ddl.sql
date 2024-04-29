CREATE TABLE IF NOT EXISTS  data_service_db_simple (
                                        id NUMBER(11),
                                        content BLOB,
                                        primary key (ID)
);

CREATE SEQUENCE IF NOT EXISTS  data_service_db_simple_seq START WITH 1 INCREMENT BY 1;