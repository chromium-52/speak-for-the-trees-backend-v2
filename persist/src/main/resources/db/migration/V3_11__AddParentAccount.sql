CREATE TABLE IF NOT EXISTS parent_accounts (
    parent_id     INT     NOT NULL,
    child_id      INT     NOT NULL,

    PRIMARY KEY (parent_id, child_id),
    CONSTRAINT parent_accounts_parent_id_fk FOREIGN KEY (parent_id) REFERENCES users (id),
    CONSTRAINT parent_accounts_child_id_fk FOREIGN KEY (child_id) REFERENCES users (id)
);
