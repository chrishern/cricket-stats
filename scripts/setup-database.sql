-- MySQL Database Setup Script for Cricket Stats Application
-- Run this script as MySQL root user to set up the database and user

-- Create the database
CREATE DATABASE IF NOT EXISTS cricket_stats 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Create the application user
CREATE USER IF NOT EXISTS 'cricket_user'@'localhost' IDENTIFIED BY 'cricket_password';

-- Grant all privileges on the cricket_stats database to the user
GRANT ALL PRIVILEGES ON cricket_stats.* TO 'cricket_user'@'localhost';

-- Flush privileges to ensure changes take effect
FLUSH PRIVILEGES;

-- Display confirmation
SELECT 'Database cricket_stats and user cricket_user created successfully!' as Status;