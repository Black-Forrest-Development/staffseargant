/*************************************************************
  DISCORD
 *************************************************************/

CREATE TABLE guild_reference
(
    guild_id BIGINT       NOT NULL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL
);

CREATE SEQUENCE guild_processor_settings_entry_seq;
CREATE TABLE guild_processor_settings_entry
(
    id       BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('guild_processor_settings_entry_seq'::regclass),
    guild_id BIGINT       NOT NULL,
    option   VARCHAR(255) NOT NULL,
    enabled  BOOL         NOT NULL
);


/*************************************************************
  SERVER CONFIG
 *************************************************************/

CREATE TABLE member_notified_role_entry
(
    id        VARCHAR(255) NOT NULL PRIMARY KEY,
    member_id BIGINT       NOT NULL,
    role      VARCHAR(255) NOT NULL
);
