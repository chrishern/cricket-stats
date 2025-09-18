ALTER TABLE player ADD COLUMN full_name VARCHAR(255);

CREATE INDEX idx_player_full_name ON player(full_name);