#!/bin/bash

# Modern Lineage II Benchmark
echo "🏁 Modern Lineage II Performance Benchmark"
echo "========================================="

# Тест CPU
echo "🖥️  CPU Benchmark..."
CPU_SCORE=$(dd if=/dev/zero bs=1M count=1024 2>/dev/null | wc -c)
echo "CPU Score: $CPU_SCORE"

# Тест памяти
echo "🧠 Memory Benchmark..."
MEM_SCORE=$(dd if=/dev/zero of=/tmp/benchmark bs=1M count=512 oflag=direct 2>&1 | grep "bytes" | awk '{print $1}')
rm -f /tmp/benchmark
echo "Memory Score: $MEM_SCORE"

# Тест диска
echo "💾 Disk Benchmark..."
DISK_SCORE=$(dd if=/dev/zero of=/tmp/disktest bs=1M count=100 oflag=direct 2>&1 | grep "MB/s" | awk '{print $10}')
rm -f /tmp/disktest
echo "Disk Score: ${DISK_SCORE} MB/s"

# Тест сети (если доступен сервер)
echo "🌐 Network Benchmark..."
PING_RESULT=$(ping -c 4 8.8.8.8 2>/dev/null | tail -1 | awk '{print $4}' | cut -d'/' -f2)
echo "Network Latency: ${PING_RESULT}ms"

# Общая оценка
echo ""
echo "📊 Benchmark Complete!"
echo "Рекомендуемые настройки будут сохранены в Config/BenchmarkResults.ini"

# Сохранение результатов
cat > Config/BenchmarkResults.ini << BENCH_EOF
[Benchmark Results]
CPUScore=$CPU_SCORE
MemoryScore=$MEM_SCORE
DiskScore=$DISK_SCORE
NetworkLatency=$PING_RESULT
BenchmarkDate=$(date)
BENCH_EOF
