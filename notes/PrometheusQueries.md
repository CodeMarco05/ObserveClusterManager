# Prometheus queries for monitoring

This document contains various Prometheus queries that can be used for monitoring purposes.

# CPU metrics
### CPU usage in percentage for all instances
```
avg by (instance,mode) (irate(node_cpu_seconds_total{mode!='idle'}[15s]))
```

# Memory metrics
### Memory usage in gb for all instances
```
(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100
```

### Available memory in gb for all instances
```
node_memory_MemAvailable_bytes / 1024 / 1024 / 1024
```

# Disk metrics
### Disk usage in gb
```
(1 - (node_filesystem_avail_bytes / node_filesystem_size_bytes)) * 100
```

### Availabe disk space in gb
```
node_filesystem_avail_bytes / 1024 / 1024 / 1024
```

# Network metrics
### Incomming bytes avaraged over the last 5 seconds
```
rate(node_network_receive_bytes_total[5s])
```

### Incomming in mb/s avaraged over the last 5 seconds
```
sum(rate(node_network_receive_bytes_total{device!="lo"}[5s])) / 1024 / 1024
```

### Outgoing bytes avaraged over the last 5 seconds
```
rate(node_network_transmit_bytes_total[5s])
```

### Outgoing in mb/s avaraged over the last 5 seconds
```
sum(rate(node_network_transmit_bytes_total[5s])) / 1024 / 1024
```

# General metrics over the host system
### Uptime in hours
```
(time() - node_boot_time_seconds) / 3600
```