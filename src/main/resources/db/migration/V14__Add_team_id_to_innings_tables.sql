ALTER TABLE batting_innings ADD COLUMN team_id INT NOT NULL;
ALTER TABLE batting_innings ADD CONSTRAINT fk_batting_innings_team_id FOREIGN KEY (team_id) REFERENCES team(id);

ALTER TABLE bowling_innings ADD COLUMN team_id INT NOT NULL;
ALTER TABLE bowling_innings ADD CONSTRAINT fk_bowling_innings_team_id FOREIGN KEY (team_id) REFERENCES team(id);