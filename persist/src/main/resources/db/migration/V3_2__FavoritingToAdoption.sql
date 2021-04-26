DROP TABLE IF EXISTS favorite_sites;

CREATE TABLE IF NOT EXISTS adopted_sites (
    user_id INT NOT NULL,
    site_id INT NOT NULL,

    PRIMARY KEY (user_id, site_id),
    CONSTRAINT adopted_sites_users_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT adopted_sites_sites_fk FOREIGN KEY (site_id) REFERENCES sites (id)
);

ALTER TABLE stewardship DROP COLUMN duration;