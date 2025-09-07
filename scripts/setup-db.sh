#!/bin/bash

# Cricket Stats Database Setup Script
# This script sets up the MySQL database for the Cricket Stats application

set -e  # Exit on any error

echo "🏏 Cricket Stats Database Setup"
echo "==============================="

# Check if MySQL is installed and running
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL is not installed. Please install MySQL first."
    echo "   Ubuntu/Debian: sudo apt-get install mysql-server"
    echo "   macOS: brew install mysql"
    echo "   Windows: Download from https://dev.mysql.com/downloads/mysql/"
    exit 1
fi

# Check if MySQL service is running
if ! mysqladmin ping -h localhost --silent; then
    echo "❌ MySQL service is not running. Please start MySQL service first."
    echo "   Ubuntu/Debian: sudo systemctl start mysql"
    echo "   macOS: brew services start mysql"
    echo "   Windows: Start MySQL service from Services panel"
    exit 1
fi

echo "✅ MySQL is installed and running"

# Prompt for MySQL root password
echo ""
echo "Please enter your MySQL root password when prompted."
echo "Running database setup script..."

# Run the setup script
mysql -u root -p < "$(dirname "$0")/setup-database.sql"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Database setup completed successfully!"
    echo ""
    echo "Database Details:"
    echo "  Database Name: cricket_stats"
    echo "  Username: cricket_user"
    echo "  Password: cricket_password"
    echo "  Host: localhost"
    echo "  Port: 3306"
    echo ""
    echo "Next steps:"
    echo "1. Run 'mvn flyway:migrate' to create the database tables"
    echo "2. Update the database credentials in pom.xml if needed"
    echo ""
else
    echo "❌ Database setup failed. Please check the error messages above."
    exit 1
fi