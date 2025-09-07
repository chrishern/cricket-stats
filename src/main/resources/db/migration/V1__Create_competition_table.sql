CREATE TABLE competition (
    id INT AUTO_INCREMENT PRIMARY KEY,
    format VARCHAR(50) NOT NULL,
    start_year VARCHAR(4) NOT NULL,
    end_year VARCHAR(4),
    country VARCHAR(100) NOT NULL,
    international BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(255) NOT NULL
);