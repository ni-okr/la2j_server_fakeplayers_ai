#!/bin/bash
while true; do
    echo "$(date): CPU: $(top -bn1 | grep "Cpu(s)" | awk '{print $2}'), RAM: $(free -h | awk '/^Mem:/ {print $3"/"$2}')" >> performance.log
    sleep 30
done
