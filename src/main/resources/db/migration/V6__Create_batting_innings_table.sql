CREATE TABLE batting_innings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    game INT NOT NULL,
    player INT NOT NULL,
    runs INT NOT NULL DEFAULT 0,
    balls INT NOT NULL DEFAULT 0,
    dots INT NOT NULL DEFAULT 0,
    fours_scored INT NOT NULL DEFAULT 0,
    sixes_scored INT NOT NULL DEFAULT 0,
    minutes_batted INT NOT NULL DEFAULT 0,
    strike_rate DOUBLE NOT NULL DEFAULT 0.0,
    FOREIGN KEY (game) REFERENCES game(id),
    FOREIGN KEY (player) REFERENCES player(id)
);