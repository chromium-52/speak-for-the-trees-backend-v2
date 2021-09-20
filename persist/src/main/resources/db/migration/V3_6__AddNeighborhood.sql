ALTER TABLE sites ADD COLUMN neighborhood_id INTEGER default NULL;

ALTER TABLE sites
ADD CONSTRAINT sites_neighborhood_id_fk FOREIGN KEY (neighborhood_id) REFERENCES neighborhoods (id);
