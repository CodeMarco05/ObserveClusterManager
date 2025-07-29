CREATE DATABASE observe_db;

CREATE USER observer WITH ENCRYPTED PASSWORD 'theOverlookingEagle';

connect observe_db
GRANT CONNECT ON DATABASE observe_db TO observer;

GRANT USAGE, CREATE ON SCHEMA public TO observer;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO observer;