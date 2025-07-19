#!/bin/bash

# CPU Metrics Collector Script
# Make it executable with: chmod +x cpu_metrics.sh

echo "======================================"
echo "        CPU METRICS COLLECTOR"
echo "======================================"
echo "Collection Time: $(date)"
echo ""

# Function to get CPU usage percentage
get_cpu_usage() {
    echo "=== CPU USAGE ==="

    # Method 1: Using top command
    cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | sed 's/%us,//')
    echo "CPU Usage (top): ${cpu_usage}%"

    # Method 2: Using vmstat
    cpu_idle=$(vmstat 1 2 | tail -1 | awk '{print $15}')
    cpu_used=$((100 - cpu_idle))
    echo "CPU Usage (vmstat): ${cpu_used}%"

    # Method 3: Using /proc/stat
    cpu_line=$(head -n1 /proc/stat)
    cpu_times=($cpu_line)
    idle_time=${cpu_times[4]}
    total_time=0
    for time in "${cpu_times[@]:1}"; do
        total_time=$((total_time + time))
    done
    cpu_usage_proc=$((100 * (total_time - idle_time) / total_time))
    echo "CPU Usage (/proc/stat): ${cpu_usage_proc}%"
    echo ""
}

# Function to get detailed CPU information
get_cpu_info() {
    echo "=== CPU INFORMATION ==="
    echo "CPU Model: $(lscpu | grep 'Model name' | cut -d':' -f2 | xargs)"
    echo "Architecture: $(lscpu | grep 'Architecture' | cut -d':' -f2 | xargs)"
    echo "CPU Cores: $(nproc)"
    echo "CPU Threads: $(lscpu | grep '^CPU(s):' | cut -d':' -f2 | xargs)"
    echo "CPU Frequency: $(lscpu | grep 'CPU MHz' | cut -d':' -f2 | xargs) MHz"
    echo "CPU Cache L1d: $(lscpu | grep 'L1d cache' | cut -d':' -f2 | xargs)"
    echo "CPU Cache L1i: $(lscpu | grep 'L1i cache' | cut -d':' -f2 | xargs)"
    echo "CPU Cache L2: $(lscpu | grep 'L2 cache' | cut -d':' -f2 | xargs)"
    echo "CPU Cache L3: $(lscpu | grep 'L3 cache' | cut -d':' -f2 | xargs)"
    echo ""
}

# Function to get per-core CPU usage
get_per_core_usage() {
    echo "=== PER-CORE CPU USAGE ==="
    mpstat -P ALL 1 1 | grep -A $(nproc) "Average:" | grep -v "Average:" | while read line; do
        if [[ $line =~ ^[0-9] ]]; then
            core=$(echo $line | awk '{print $2}')
            usage=$(echo $line | awk '{print 100-$11}')
            printf "Core %s: %.2f%%\n" "$core" "$usage"
        fi
    done
    echo ""
}

# Function to get load average
get_load_average() {
    echo "=== LOAD AVERAGE ==="
    load_avg=$(uptime | awk -F'load average:' '{print $2}')
    echo "Load Average: ${load_avg}"
    echo ""
}

# Function to get CPU temperature (if available)
get_cpu_temperature() {
    echo "=== CPU TEMPERATURE ==="
    if command -v sensors &> /dev/null; then
        sensors | grep -E "(Core|Package)" | head -5
    elif [ -f /sys/class/thermal/thermal_zone0/temp ]; then
        temp=$(cat /sys/class/thermal/thermal_zone0/temp)
        temp_celsius=$((temp / 1000))
        echo "CPU Temperature: ${temp_celsius}°C"
    else
        echo "Temperature monitoring not available"
    fi
    echo ""
}

# Function to get memory usage
get_memory_usage() {
    echo "=== MEMORY USAGE ==="
    free -h | grep -E "(Mem|Swap)"
    echo ""
}

# Function to get process CPU usage
get_top_processes() {
    echo "=== TOP CPU CONSUMING PROCESSES ==="
    ps aux --sort=-%cpu | head -6
    echo ""
}

# Function to output JSON format (useful for your Java application)
output_json() {
    echo "=== JSON OUTPUT ==="
    cpu_usage=$(vmstat 1 2 | tail -1 | awk '{print 100-$15}')
    load_1=$(uptime | awk -F'load average:' '{print $2}' | awk -F',' '{print $1}' | xargs)
    load_5=$(uptime | awk -F'load average:' '{print $2}' | awk -F',' '{print $2}' | xargs)
    load_15=$(uptime | awk -F'load average:' '{print $2}' | awk -F',' '{print $3}' | xargs)

    cat << EOF
{
    "timestamp": "$(date -Iseconds)",
    "cpu_usage_percentage": ${cpu_usage},
    "cpu_cores": $(nproc),
    "load_average": {
        "1min": ${load_1},
        "5min": ${load_5},
        "15min": ${load_15}
    },
    "uptime_seconds": $(awk '{print int($1)}' /proc/uptime)
}
EOF
}

# Main execution
main() {
    case "${1:-all}" in
        "usage")
            get_cpu_usage
            ;;
        "info")
            get_cpu_info
            ;;
        "cores")
            get_per_core_usage
            ;;
        "temp")
            get_cpu_temperature
            ;;
        "json")
            output_json
            ;;
        "all"|*)
            get_cpu_usage
            get_cpu_info
            get_per_core_usage
            get_load_average
            get_cpu_temperature
            get_memory_usage
            get_top_processes
            output_json
            ;;
    esac
}

# Check for required tools
check_dependencies() {
    deps=("mpstat" "vmstat" "top" "lscpu")
    missing=()

    for dep in "${deps[@]}"; do
        if ! command -v "$dep" &> /dev/null; then
            missing+=("$dep")
        fi
    done

    if [ ${#missing[@]} -gt 0 ]; then
        echo "Warning: Missing dependencies: ${missing[*]}"
        echo "Install with: sudo apt-get install sysstat procps util-linux"
        echo ""
    fi
}

# Script usage
usage() {
    echo "Usage: $0 [option]"
    echo "Options:"
    echo "  all     - Show all metrics (default)"
    echo "  usage   - Show CPU usage percentage only"
    echo "  info    - Show CPU information only"
    echo "  cores   - Show per-core usage"
    echo "  temp    - Show CPU temperature"
    echo "  json    - Output in JSON format"
}

# Check if help is requested
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
    usage
    exit 0
fi

# Run dependency check and main function
check_dependencies
main "$1"