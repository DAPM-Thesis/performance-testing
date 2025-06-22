
import numpy as np
import matplotlib.pyplot as plt

experiment_number = 2
data = np.loadtxt(f"experiment_{experiment_number}.txt")
nano_per_milli = 10e9 / 10e3
data = data / nano_per_milli

n_obs = len(data)
if experiment_number < 6:
    minute_count = 2
elif experiment_number == 6:
    minute_count = 1
else:
    minute_count = 5
duration_seconds = 60 * minute_count

messages_per_second = int(n_obs/duration_seconds)
mean_latency = np.mean(data)
variance = np.var(data)


print()
print(f"number of observations: {n_obs}")
print(f"throughput            : {messages_per_second} messages/s")
print(f"mean latency          : {mean_latency:.2f} ms")
print(f"latency variance      : {variance:.2f} ms")

x = [i for i in range(len(data))]
plt.scatter(x, data)
plt.axhline(y=mean_latency, color='r', linestyle='-')
plt.text(42120, 173, f"mean = {int(mean_latency)} ms")

plt.title(f"Latency Experiment {experiment_number}: {int(duration_seconds/60)} minutes, 5 ms sleep")
plt.xlabel("Observation number")
plt.ylabel("latency [ms]")
plt.show()