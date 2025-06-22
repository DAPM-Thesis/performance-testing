
import numpy as np

log_500k_times = np.array([124865, 123941, 126194])
no_log_500k_times = np.array([99966, 99360, 99040])

log_mean = int(np.mean(log_500k_times))
no_log_mean = int(np.mean(no_log_500k_times))

print("\nAverage time taken to log 500,000 messages over 3 experiments:")
print(f"without logging: {no_log_mean} ms")
print(f"with logging   : {log_mean} ms\n")

overhead_pct = ((log_mean - no_log_mean) / no_log_mean) * 100
print(f"Logging adds approximately {overhead_pct:.1f}% overhead.\n")