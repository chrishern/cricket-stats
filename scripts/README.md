# Cricket Stats Database Setup

This folder contains scripts to set up the MySQL database for the Cricket Stats application.

## Prerequisites

- MySQL Server installed and running
- MySQL root access

## Setup Instructions

### Option 1: Automated Setup (Recommended)

Run the automated setup script:

```bash
./scripts/setup-db.sh
```

This script will:
- Check if MySQL is installed and running
- Create the `cricket_stats` database
- Create the `cricket_user` with appropriate permissions
- Provide confirmation and next steps

### Option 2: Manual Setup

If you prefer to run the setup manually:

```bash
mysql -u root -p < scripts/setup-database.sql
```

## Database Configuration

The setup creates:

- **Database**: `cricket_stats`
- **Username**: `cricket_user`
- **Password**: `cricket_password`
- **Host**: `localhost`
- **Port**: `3306` (default)

## After Setup

1. **Run Flyway migrations** to create the database tables:
   ```bash
   mvn flyway:migrate
   ```

2. **Update credentials** (if needed) in `pom.xml`:
   ```xml
   <configuration>
       <url>jdbc:mysql://localhost:3306/cricket_stats</url>
       <user>your_username</user>
       <password>your_password</password>
   </configuration>
   ```

## Troubleshooting

### MySQL not running
- **Ubuntu/Debian**: `sudo systemctl start mysql`
- **macOS**: `brew services start mysql`
- **Windows**: Start MySQL service from Services panel

### Permission denied
- Ensure you have MySQL root access
- Check if the script is executable: `chmod +x scripts/setup-db.sh`

### Connection issues
- Verify MySQL is listening on port 3306
- Check firewall settings
- Ensure localhost connectivity

## Files in this directory

- `setup-database.sql` - SQL script to create database and user
- `setup-db.sh` - Automated bash script for database setup
- `README.md` - This documentation file