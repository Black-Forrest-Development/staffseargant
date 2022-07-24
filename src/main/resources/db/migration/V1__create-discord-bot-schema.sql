/*************************************************************
  PERMISSION SCHEME
 *************************************************************/

CREATE SEQUENCE permission_scheme_seq;
CREATE TABLE permission_scheme
(
    id          BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('permission_scheme_seq'::regclass),
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT         NOT NULL
);

CREATE SEQUENCE permission_scheme_entry_seq;
CREATE TABLE permission_scheme_entry
(
    id        BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('permission_scheme_entry_seq'::regclass),
    action    VARCHAR(255) NOT NULL,
    scheme_id BIGINT       NOT NULL,

    CONSTRAINT fk_permission_scheme_entry_permission_scheme FOREIGN KEY (scheme_id)
        REFERENCES permission_scheme (id) MATCH SIMPLE
);

/*************************************************************
  SERVER CONFIG
 *************************************************************/

CREATE SEQUENCE server_config_seq;
CREATE TABLE server_config
(
    id       BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('server_config_seq'::regclass),
    name     VARCHAR(255) NOT NULL UNIQUE,
    host     VARCHAR(255) NOT NULL,
    port     INT          NOT NULL,
    password VARCHAR(255) NOT NULL
);
