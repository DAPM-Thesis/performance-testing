
import os
import numpy as np
import matplotlib.pyplot as plt

n_runs = 3
n_elems = [3, 5, 9, 13, 17]
text_sleep = '075'

png_save_path = "VMs/graphs/round2_3PE/" + f"kafka_overhead_plot_{text_sleep}.png"

script_dir = os.path.dirname(os.path.abspath(__file__))
data_root = os.path.join(script_dir, f"../experiment_results/round2_3PE/kafka_overhead/{text_sleep}_ms_sleep")

files_no_enum_no_extension = [f"{num}_elem_{text_sleep}_ms_sleep" for num in n_elems]

experiment_means = []
for file_name in files_no_enum_no_extension:
    experiment_mean = 0
    for run in range(1, n_runs+1):
        file = file_name + f"_{run}.txt"
        run_mean = np.mean(np.loadtxt(os.path.normpath(os.path.join(data_root, file))))
        experiment_mean += run_mean
    experiment_means.append(experiment_mean / n_runs)

assert len(experiment_means) == len(n_elems), "Something is fishy. experiment_means should have same number of elements as experiments"

print(list(zip(n_elems, experiment_means)))

plt.scatter(n_elems, experiment_means, s=80)

plt.grid(alpha=0.5)
plt.xlabel("Pipeline Length", fontsize=14)
plt.ylabel("Mean Latency [ns]", fontsize=14)
plt.title("Source to Sink Latency by Length", fontsize=16)
plt.xticks(fontsize=12)
plt.yticks(fontsize=12)
ax = plt.gca()
ax.xaxis.get_offset_text().set_fontsize(12)
ax.yaxis.get_offset_text().set_fontsize(12)

plt.savefig(png_save_path, dpi=300, bbox_inches='tight')
print(f"Saved image to {png_save_path}")