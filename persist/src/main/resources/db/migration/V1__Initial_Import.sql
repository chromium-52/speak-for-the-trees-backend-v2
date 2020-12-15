CREATE TABLE IF NOT EXISTS users
(
    id              SERIAL      NOT NULL PRIMARY KEY,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    email           VARCHAR(36) NOT NULL,
    email_verified  BOOLEAN     NOT NULL DEFAULT FALSE,
    first_name      VARCHAR(36) NOT NULL,
    last_name       VARCHAR(36) NOT NULL,
    username        VARCHAR(36) NOT NULL,
    privilege_level INT         NOT NULL DEFAULT 0,
    password_hash   BYTEA       NOT NULL
);


CREATE TABLE IF NOT EXISTS blacklisted_refreshes
(
    refresh_hash VARCHAR(64) PRIMARY KEY,
    expires      TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS verification_keys
(
    id         VARCHAR(50) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    user_id    INT         NOT NULL,
    used       BOOLEAN              DEFAULT false,
    type       VARCHAR(16) NOT NULL,

    CONSTRAINT verification_keys_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS teams
(
    id          SERIAL          NOT NULL PRIMARY KEY,
    team_name   VARCHAR(100)    NOT NULL,
    bio         TEXT            NOT NULL,
    finished    BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS goals
(
    id              SERIAL      NOT NULL PRIMARY KEY,
    team_id         INT         NOT NULL,
    goal            INT         NOT NULL,
    complete_by     TIMESTAMP   NOT NULL,
    completed_at    TIMESTAMP,

    CONSTRAINT goals_team_id_fk FOREIGN KEY (team_id) REFERENCES teams (id)
);

CREATE TABLE IF NOT EXISTS users_teams
(
    user_id     INT NOT NULL PRIMARY KEY,
    team_id     INT NOT NULL PRIMARY KEY,
    team_role   INT NOT NULL,

    CONSTRAINT users_teams_users_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT users_teams_teams_fk FOREIGN KEY (team_id) REFERENCES teams (id)
);

CREATE TABLE IF NOT EXISTS neighborhoods
(
    id                  INT             NOT NULL PRIMARY KEY,
    neighborhood_name   VARCHAR(30)     NOT NULL,
    sqmiles             NUMERIC(5, 2)   NOT NULL,
    lat                 NUMERIC(17, 14) NOT NULL,
    lng                 NUMERIC(17, 14) NOT NULL,
    coords              TEXT            NOT NULL,
)

CREATE TABLE IF NOT EXISTS blocks
(
    id              INT             NOT NULL PRIMARY KEY,
    neighborhood_id INT             NOT NULL,
    lat             NUMERIC(17, 14) NOT NULL,
    lng             NUMERIC(17, 14) NOT NULL,
    coords          TEXT            NOT NULL,

    CONSTRAINT blocks_neighborhood_fk FOREIGN KEY (neighborhood_id) REFERENCES neighborhoods (id)
);

CREATE TABLE IF NOT EXISTS reservations
(
    id              SERIAL      NOT NULL PRIMARY KEY,
    user_id         INT         NOT NULL,
    block_id        INT         NOT NULL,
    reserved_at     TIMESTAMP   NOT NULL,
    completed_at    TIMESTAMP,
    deleted         BOOLEAN     NOT NULL DEFAULT FALSE,

    CONSTRAINT reservations_users_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT reservations_blocks_fk FOREIGN KEY (block_id) REFERENCES blocks (id)
);

/* [jooq ignore start] */
CREATE OR REPLACE FUNCTION func_set_updated_at_timestamp()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS bl_refs_trig_set_updated_at ON blacklisted_refreshes;
CREATE TRIGGER bl_refs_trig_set_updated_at
    BEFORE UPDATE
    ON blacklisted_refreshes
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();

DROP TRIGGER IF EXISTS ver_keys_trig_set_updated_at ON verification_keys;
CREATE TRIGGER ver_keys_trig_set_updated_at
    BEFORE UPDATE
    ON verification_keys
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();
/* [jooq ignore stop] */
