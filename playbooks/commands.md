# Install 
```bash
ansible-playbook -i inventory.ini node_exporter_playbook.yml -e action=install
```

# Start the service
```bash
ansible-playbook -i inventory.ini node_exporter_playbook.yml -e action=start
```

# Stop the service
```bash
ansible-playbook -i inventory.ini node_exporter_playbook.yml -e action=stop
```

# Restart the service
```bash
ansible-playbook -i inventory.ini node_exporter_playbook.yml -e action=restart
```

# Completely uninstall Node Exporter
```bash
ansible-playbook -i inventory.ini node_exporter_playbook.yml -e action=uninstall
```

# Check the status of Node Exporter
```bash
# Check service status - should show "inactive (dead)"
sudo systemctl status node_exporter

# Quick check - should return "inactive"
sudo systemctl is-active node_exporter
```