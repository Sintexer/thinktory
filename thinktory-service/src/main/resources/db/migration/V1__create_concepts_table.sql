CREATE TABLE concepts (
    id BIGINT NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    theory TEXT,
    labels TEXT[] DEFAULT '{}'
);

CREATE SEQUENCE concepts_seq START WITH 1 INCREMENT BY 50;