# Possible system stats to collect

- CPU info: `/proc/cpuinfo`
- CPU usage: `/proc/stat`
- Memory info: `/proc/meminfo`
- Swap info: `/proc/swaps`

| Option            | Description                                                                      | Typical Use Case                                                  |
|-------------------|----------------------------------------------------------------------------------|-------------------------------------------------------------------|
| `none`            | No automatic schema management. Hibernate does nothing.                          | Production environment with externally managed DB schema          |
| `validate`        | Validates the schema against entities; fails startup if discrepancies are found. | Staging or testing environments to verify schema correctness      |
| `create`          | Creates tables if they do not exist; does not drop existing tables.              | Development or starting with an empty database                    |
| `drop`            | Drops all tables on startup; does not create new tables.                         | Debugging purposes or explicitly clearing the database            |
| `drop-and-create` | Drops existing tables and recreates them based on entity mappings.               | Development mode, integration tests, or automated resets          |
| `update`          | Attempts to incrementally update the existing schema to match entities.          | Caution advised; may cause issues; not recommended for production |