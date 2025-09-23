ALTER TABLE batting_innings ADD COLUMN innings_order INT NOT NULL DEFAULT 0;
ALTER TABLE bowling_innings ADD COLUMN innings_order INT NOT NULL DEFAULT 0;