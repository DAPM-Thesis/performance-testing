
import os
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerPathCollection

script_dir = os.path.dirname(os.path.abspath(__file__))
data_path = os.path.normpath(os.path.join(script_dir, "../experiment_results/round2_3PE/scalability"))
experiment_name = "1_ms_sleep"
plot_title = "Scalability: 1 ms Sleep"
png_name = "scalability_" + experiment_name + ".png"

png_save_path = "VMs/graphs/" + png_name
n_runs = 3
nano_to_milli_factor = 10**3 / 10**9

experiment_length_minutes = 5
experiment_length_seconds = 60 * experiment_length_minutes

final_A_ms = []
final_B_ms = []
final_combined_ms = []

for run in range(1, n_runs+1):
    times_ms_A = []
    times_ms_B = []
    combined_ms = []
    path = os.path.normpath(os.path.join(data_path, f"{experiment_name}_{run}.txt"))
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

        final_A_ms.append(np.array(times_ms_A))
        final_B_ms.append(np.array(times_ms_B))
        final_combined_ms.append(np.array(combined_ms))

n_obs_A = [len(run) for run in final_A_ms]
n_obs_B = [len(run) for run in final_B_ms]
n_obs_total = np.array([len(run) for run in final_combined_ms])

mean_latency_A = [np.mean(exp) for exp in final_A_ms]
mean_latency_B = [np.mean(exp) for exp in final_B_ms]
mean_latency_combined = [np.mean(exp) for exp in final_combined_ms]

print()
print(f"Messages received from source in orgA: {n_obs_A}")
print(f"Messages received from source in orgB: {n_obs_B}")
print(f"Messages received by sink (total)    : {n_obs_total}")
print()
print(f"Mean sink processing rate: {int(np.mean(n_obs_total)/experiment_length_seconds)} messages/second.")
print(f"Mean latency in ms in (A, B, combined) = ({np.mean(mean_latency_A):.2f}, {np.mean(mean_latency_B):.2f}, {np.mean(mean_latency_combined):.2f})")
print()

x = [list(range(len(exp))) for exp in final_combined_ms]
for exp in range(n_runs):
    plt.scatter(x[exp], final_combined_ms[exp], s=4, label=f"run {exp + 1}")


#_____ COPIED FROM LATENCY_STATISTICS _____
messages_per_second = np.array([int(mps) for mps in (n_obs_total/300)])
mean_latency = np.array([np.mean(exp) for exp in final_combined_ms])

first_elems = max(np.mean(final_combined_ms[0][:100]), 0.0001)
last_elems = max(np.mean(final_combined_ms[0][-100:]), 0.0001)
quotient = first_elems / last_elems
if (quotient > 1): # Stabilized latency
    delay_threshold = np.array([min(500, np.min(n_obs_total)/80) for i in range(len(n_obs_total))])
    n_below_mean = np.array([0, 0, 0])
    min_len = min(n_obs_total)
    i = 0
    while (np.any(n_below_mean < delay_threshold) and i < min_len):
        for exp in range(n_runs):
            if (final_combined_ms[exp][i] < np.mean(mean_latency)):
                n_below_mean[exp] += 1
        i += 1
    if i == min_len:
        adjusted_start = 0
    else:
        adjusted_start = i
else: # message input rate exceeds throughput capacity
    adjusted_start = 0

delay_mean_latency = np.array([np.mean(exp[adjusted_start:]) for exp in final_combined_ms])
delay_variance = np.array([np.var(exp[adjusted_start:]) for exp in final_combined_ms])

x_max = np.max(n_obs_total)
y_max = np.max([np.max(exp) for exp in final_combined_ms])

# The factors in the following have been found through testing
if adjusted_start == 0:
    line_text = "mean"
    line_mean_latency = np.mean(mean_latency)
    text_x = int(-x_max*0.0317757)
    text_y = int(line_mean_latency+y_max*0.019850208)
else:
    line_text = "adjusted_mean"
    line_mean_latency = np.mean(delay_mean_latency)
    text_x = int(adjusted_start + x_max*0.033014)
    text_y = int(line_mean_latency + y_max*0.045)

plt.axhline(y=np.mean(line_mean_latency), color='r', linestyle='-')
plt.text(text_x, text_y, f"{line_text} = {line_mean_latency:.2f} ms", fontsize=14)


if adjusted_start == 0:
    box_x = int(x_max * 0.73618819)
    box_y = int(y_max * 1.0137)
else:
    box_x = int(x_max * 1.02)
    box_y = int(y_max * 0.75)
label_1 = "sent messages mean"
label_2 = "messages/second mean"
value_1 = f"{int(np.mean(n_obs_total)):,}"
value_2 = f"{int(np.mean(messages_per_second)):,}"
label_width = max(len(label_1), len(label_2))
value_width = max(len(value_1), len(value_2))
obs_count_str = f"{label_1:>{label_width}}: {value_1:>{value_width}}"
throughput_mean_str = f"{label_2:>{label_width}}: {value_2:>{value_width}}"
info_text = f"{obs_count_str}\n{throughput_mean_str}"

plt.text(
    box_x, box_y,info_text,
    ha='right', va='top',
    bbox=dict(boxstyle='round', facecolor='white', edgecolor='gray', alpha=0.85),
    fontsize=12, family='monospace'
)


#_____


plt.title(plot_title, fontsize=16)
plt.xlabel("observation number", fontsize=14)
plt.ylabel("latency in reaching sink [ms]", fontsize=14)
plt.legend(handler_map={plt.scatter([], [], s=4): HandlerPathCollection(marker_pad=0.3, numpoints=1)},markerscale=5, fontsize=12)
plt.xticks(fontsize=12)
plt.yticks(fontsize=12)
ax = plt.gca()
ax.xaxis.get_offset_text().set_fontsize(12)
ax.yaxis.get_offset_text().set_fontsize(12)

plt.savefig(png_save_path, dpi=300, bbox_inches='tight')
print(f"Saved image to {png_save_path}")
