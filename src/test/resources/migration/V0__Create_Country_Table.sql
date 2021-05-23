CREATE TABLE country (
    id         uuid        CONSTRAINT country_id PRIMARY KEY,
    name       varchar(40) NOT NULL,
    created_at date        NOT NULL
);
