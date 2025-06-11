
import os
import numpy as np
import matplotlib.pyplot as plt

script_dir = os.path.dirname(os.path.abspath(__file__))
data_path = os.path.normpath(os.path.join(script_dir, "../experiment_results/scalability"))
experiment_name = "075_ms_sleep"

min_exp, max_exp = (1,3)
nano_to_milli_factor = 10**3 / 10**9

times_ms_A = []
times_ms_B = []
combined_ms = []

experiment_length_minutes = 5
experiment_length_seconds = 60 * experiment_length_minutes
path = os.path.normpath(os.path.join(data_path, f"{experiment_name}_{1}.txt"))
with open(path, 'r') as file:
    contents = file.readlines()
    for line in contents:
        if len(line) <= 2:
            continue

        latency_ms = int(int(line[2:]) * nano_to_milli_factor)
        combined_ms.append(latency_ms)
        match line[0]:
            case '0': times_ms_A.append(latency_ms)
            case '1': times_ms_B.append(latency_ms)
            case _: raise ValueError("unexpected port")

    times_ms_A = np.array(times_ms_A)
    times_ms_B = np.array(times_ms_B)
    combined_ms = np.array(combined_ms)

n_obs_A = len(times_ms_A)
n_obs_B = len(times_ms_B)
n_obs_total = len(combined_ms)

mean_latency_A = np.mean(times_ms_A)
mean_latency_B = np.mean(times_ms_B)
mean_latency_combined = np.mean(combined_ms)

print(f"Messages received from source in orgA: {n_obs_A}")
print(f"Messages received from source in orgB: {n_obs_B}")
print(f"Messages received by sink (total)    : {n_obs_total}")
print()
print(f"Mean sink processing rate: {int(n_obs_total/experiment_length_seconds)} messages/second.")
print(f"Mean latency in ms in (A, B, combined) = ({mean_latency_A:.2f}, {mean_latency_B:.2f}, {mean_latency_combined:.2f})")

x = [i+1 for i in range(len(combined_ms))]
plt.scatter(x, combined_ms, s = 1)

plt.axhline(y=mean_latency_combined, color='r', linestyle='-')
plt.text(66400, 450, f"mean = {mean_latency_combined:.2f} ms")

plt.title("Scalability Latency")
plt.xlabel("observation number")
plt.ylabel("latency in reaching sink [ms]")
plt.show()