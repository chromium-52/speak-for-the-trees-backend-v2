CREATE TABLE IF NOT EXISTS sites (
    id                  SERIAL          NOT NULL PRIMARY KEY,
    block_id            INT,
    lat                 NUMERIC(17, 14),
    lng                 NUMERIC(17, 14),
    city                VARCHAR(30),
    zip                 VARCHAR(5),
    address             VARCHAR(100),
    deleted_at          TIMESTAMP,

    CONSTRAINT sites_block_id_fk FOREIGN KEY (block_id) REFERENCES blocks (id)
);

CREATE TABLE IF NOT EXISTS site_entries (
    id                      SERIAL          NOT NULL    PRIMARY KEY,
    site_id                 INT             NOT NULL,
    user_id                 INT,
    updated_at              TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,
    qa                      BOOLEAN                     DEFAULT FALSE,
    tree_present            BOOLEAN,
    status                  VARCHAR(30),
    genus                   VARCHAR(30),
    species                 VARCHAR(30),
    common_name             VARCHAR(50),
    confidence              VARCHAR(30),
    multistem               BOOLEAN,
    diameter                DOUBLE PRECISION,
    circumference           DOUBLE PRECISION,
    coverage                VARCHAR(100),
    pruning                 VARCHAR(30),
    condition               VARCHAR(30),
    discoloring             BOOLEAN,
    leaning                 BOOLEAN,
    constricting_gate       BOOLEAN,
    wounds                  BOOLEAN,
    pooling                 BOOLEAN,
    stakes_with_wires       BOOLEAN,
    stakes_without_wires    BOOLEAN,
    light                   BOOLEAN,
    bicycle                 BOOLEAN,
    bag_empty               BOOLEAN,
    bag_filled              BOOLEAN,
    tape                    BOOLEAN,
    sucker_growth           BOOLEAN,
    site_type               VARCHAR(30),
    sidewalk_width          VARCHAR(30),
    site_width              DOUBLE PRECISION,
    site_length             DOUBLE PRECISION,
    material                VARCHAR(50),
    raised_bed              BOOLEAN,
    fence                   BOOLEAN,
    trash                   BOOLEAN,
    wires                   BOOLEAN,
    grate                   BOOLEAN,
    stump                   BOOLEAN                     DEFAULT FALSE,
    tree_notes              TEXT,
    site_notes              TEXT,
    melnea_cass_trees       VARCHAR(30),
    mcb_number              INTEGER,
    tree_dedicated_to       VARCHAR(100),

    CONSTRAINT site_entries_site_id_fk FOREIGN KEY (site_id) REFERENCES sites (id),
    CONSTRAINT site_entries_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS entry_usernames (
    entry_id INT            NOT NULL PRIMARY KEY,
    username VARCHAR(100),

    CONSTRAINT entry_usernames_site_entries_fk FOREIGN KEY (entry_id) REFERENCES site_entries (id)
);

CREATE TABLE IF NOT EXISTS potential_sites (
    id                  SERIAL          NOT NULL PRIMARY KEY,
    block_id            INT             NOT NULL,
    user_id             INT             NOT NULL,
    lat                 NUMERIC(17, 14) NOT NULL,
    lng                 NUMERIC(17, 14) NOT NULL,
    city                VARCHAR(30)     NOT NULL,
    zip                 VARCHAR(5)      NOT NULL,
    address             VARCHAR(100)    NOT NULL,
    light_pole          BOOLEAN         NOT NULL,
    driveway            BOOLEAN         NOT NULL,
    hydrant             BOOLEAN         NOT NULL,
    intersection        BOOLEAN         NOT NULL,
    building_entrance   BOOLEAN         NOT NULL,
    notes               TEXT,
    deleted_at          TIMESTAMP,

    CONSTRAINT potential_sites_block_id_fk FOREIGN KEY (block_id) REFERENCES blocks (id),
    CONSTRAINT potential_sites_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

/* [jooq ignore start] */
ALTER SEQUENCE sites_id_seq RESTART WITH 3331216;
/* [jooq ignore stop] */