CREATE TABLE bowling_competition_statistics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    competition INT NOT NULL,
    player INT NOT NULL,
    overs DOUBLE NOT NULL DEFAULT 0.0,
    maidens INT NOT NULL DEFAULT 0,
    runs INT NOT NULL DEFAULT 0,
    wickets INT NOT NULL DEFAULT 0,
    dots INT NOT NULL DEFAULT 0,
    no_balls INT NOT NULL DEFAULT 0,
    wides INT NOT NULL DEFAULT 0,
    fours_conceded INT NOT NULL DEFAULT 0,
    sixes_conceded INT NOT NULL DEFAULT 0,
    economy DOUBLE NOT NULL DEFAULT 0.0,
    strike_rate DOUBLE NOT NULL DEFAULT 0.0,
    average DOUBLE NOT NULL DEFAULT 0.0,
    FOREIGN KEY (competition) REFERENCES competition(id),
    FOREIGN KEY (player) REFERENCES player(id)
);