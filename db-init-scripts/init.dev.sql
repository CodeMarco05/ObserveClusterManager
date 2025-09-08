-- Create databases
CREATE DATABASE observe_db;
CREATE DATABASE observe_db_test;

CREATE DATABASE keycloak;

-- Create users
CREATE USER observer WITH ENCRYPTED PASSWORD 'theOverlookingEagle';
CREATE USER observer_test WITH ENCRYPTED PASSWORD 'theOverlookingEagle_test';

CREATE USER keycloak WITH ENCRYPTED PASSWORD 'keycloak';

-- Grant privileges to databases
GRANT CONNECT ON DATABASE observe_db TO observer;
GRANT CONNECT ON DATABASE observe_db_test TO observer_test;

GRANT CONNECT ON DATABASE keycloak TO keycloak;

-- Work inside observe_db
\c observe_db
GRANT USAGE, CREATE ON SCHEMA public TO observer;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO observer;

-- Work inside observe_db_test
\c observe_db_test
GRANT USAGE, CREATE ON SCHEMA public TO observer_test;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO observer_test;

-- Work inside keycloak
\c keycloak
GRANT USAGE, CREATE ON SCHEMA public TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO keycloak;
