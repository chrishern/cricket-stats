CREATE TABLE game (
    id INT AUTO_INCREMENT PRIMARY KEY,
    competition INT NOT NULL,
    home_team INT NOT NULL,
    away_team INT NOT NULL,
    result VARCHAR(100) NOT NULL,
    FOREIGN KEY (competition) REFERENCES competition(id),
    FOREIGN KEY (home_team) REFERENCES team(id),
    FOREIGN KEY (away_team) REFERENCES team(id)
);