# ObServe Backend 🔍

[![Java](https://img.shields.io/badge/Java-17+-red.svg)](https://www.oracle.com/java/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.x-blue.svg)](https://quarkus.io/)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-green.svg)](https://gradle.org/)

> **ObServe** - Monitor, analyze, and observe your server infrastructure with ease.

## 🚀 Overview

ObServe Backend is a powerful server monitoring solution built with Quarkus, designed to provide real-time insights into your system's performance. Track CPU usage, memory consumption, disk space, and more with our comprehensive monitoring platform.

This backend seamlessly connects with the **ObServe iOS app** for mobile monitoring on-the-go. Additionally, developers can implement their own custom frontends using our well-documented API endpoints, making ObServe adaptable to any monitoring interface you envision.

## ✨ Features

- 📊 **Real-time Metrics**: Live monitoring of system resources
- 💾 **Resource Tracking**: CPU, RAM, and disk usage monitoring
- 🔧 **Health Checks**: Built-in health monitoring endpoints
- 📈 **Prometheus Integration**: Native metrics export for monitoring
- 🐳 **Docker Support**: Containerized deployment ready
- ⚡ **High Performance**: Built on Quarkus for lightning-fast startup

## 🛠️ Tech Stack

- **Framework**: Quarkus (Supersonic Subatomic Java)
- **Build Tool**: Gradle
- **Monitoring**: Prometheus
- **Database**: PostgreSQL
- **Containerization**: Docker & Docker Compose

## 📦 Installation

### Prerequisites

- **Server with node-exporter**: A target server with Prometheus node-exporter installed is required for system metrics collection
- **Ansible (Optional)**: For automated node-exporter installation on Linux systems
- **Docker & Docker Compose**: For running the ObServe backend and related services

### Step 1: Install node-exporter on Target Server

For Linux systems, you can use the provided Ansible playbook:

```bash
# Navigate to the playbooks directory
cd playbooks/

# Run the node-exporter installation playbook
ansible-playbook -i inventory.ini node_exporter_playbook.yml
```

Alternatively, install node-exporter manually on your target server following the [official documentation](https://prometheus.io/docs/guides/node-exporter/).

### Step 2: Configure and Run Services

1. **Configure ports**: If node-exporter is not running on the default port (9100), update the configuration in `docker-compose.yml` to match your node-exporter port.

2. **Start all services** using Docker Compose:

```bash
# For production
docker-compose up -d
```

The Docker Compose setup will start all necessary services including the ObServe backend, database, and monitoring components.

## 📚 Documentation

### API Documentation

The ObServe Backend API is fully documented and available in two formats:

- **OpenAPI Specification**: Available as an OpenAPI file in the `docs/` folder
- **Interactive Swagger UI**: *(Coming Soon)* Live documentation will be available at [swagger-observe.marco-brandt.com](https://swagger-observe.marco-brandt.com)

The Swagger interface will allow you to explore all available endpoints, test API calls directly, and understand request/response formats for building your own frontend applications.

## 🔧 Configuration

### Environment Variables

The application is configured through environment variables defined in the `docker-compose.yml` file:

#### Database Configuration
- `QUARKUS_DATASOURCE_JDBC_URL`: PostgreSQL connection URL (default: `jdbc:postgresql://postgres:5432/observe_db`)
- `QUARKUS_DATASOURCE_USERNAME`: Database username (default: `observer`)
- `QUARKUS_DATASOURCE_PASSWORD`: Database password (default: `theOverlookingEagle`)

#### Monitoring Configuration
- `PROMETHEUS_BASE_URL`: Prometheus server URL for metrics collection (default: `http://prometheus:9090`)

### Custom PostgreSQL Database

While the Docker Compose setup includes a PostgreSQL container, you can configure ObServe to use your own external PostgreSQL database by modifying the database environment variables.

⚠️ **Important**: When using a custom database setup, handle networking configuration with care. Docker networking can break if not properly configured, especially when mixing containerized and external services. Ensure proper network connectivity and firewall rules are in place.

## 🤝 Contributing

We welcome contributions! While you can directly submit a Pull Request, it's preferred to get in touch first to discuss your ideas:

- **Email**: [brandt.marco05@gmail.com](mailto:brandt.marco05@gmail.com)
- **GitHub**: Contact methods available on my GitHub profile

This helps ensure your contribution aligns with the project's direction and avoids duplicate work.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions, please open an issue in the GitHub repository.

---

<p align="center">Made with ❤️ by the ObServe Team</p>