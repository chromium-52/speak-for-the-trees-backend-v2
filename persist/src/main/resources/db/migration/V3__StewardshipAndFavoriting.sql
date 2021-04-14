CREATE TABLE IF NOT EXISTS stewardship (
    id          SERIAL      NOT NULL PRIMARY KEY,
    user_id     INT         NOT NULL,
    site_id     INT         NOT NULL,
    date_time   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    duration    INT,
    watered     BOOLEAN     DEFAULT FALSE,
    mulched     BOOLEAN     DEFAULT FALSE,
    cleaned     BOOLEAN     DEFAULT FALSE,
    weeded      BOOLEAN     DEFAULT FALSE,

    CONSTRAINT stewardship_users_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT stewardship_sites_fk FOREIGN KEY (site_id) REFERENCES sites (id)
);

CREATE TABLE IF NOT EXISTS favorite_sites (
    user_id INT NOT NULL,
    site_id INT NOT NULL,

    PRIMARY KEY (user_id, site_id),
    CONSTRAINT favorite_sites_users_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT favorite_sites_sites_fk FOREIGN KEY (site_id) REFERENCES sites (id)
);