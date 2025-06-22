
import os
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerPathCollection

experiment_name = "025ms_sleep"
plot_title = f"0.25 ms sleep"
png_name = f"025-ms-throughput-experiments.png"

png_save_path = "single_VM/graphs/" + png_name

nano_per_milli = 10e9 / 10e3
min_exp = 1
max_exp = 3
n_exps = max_exp - min_exp + 1
minute_count = 5
duration_seconds = 60 * minute_count


script_dir = os.path.dirname(os.path.abspath(__file__))
data_root = os.path.join(script_dir, "../experiment_results")
data_path = os.path.normpath(os.path.join(data_root, "throughput"))

experiments = []
for experiment_number in range(min_exp, max_exp+1):
    data = np.loadtxt(os.path.normpath(os.path.join(data_path, f"{experiment_name}_{experiment_number}.txt")))
    data = data / nano_per_milli
    experiments.append(data)

    
experiments = np.array(experiments, dtype=object)

n_obs = np.array([len(experiments[i]) for i in range(n_exps)])

messages_per_second = np.array([int(mps) for mps in (n_obs/duration_seconds)])
mean_latency = np.array([np.mean(exp) for exp in experiments])
variance = np.array([np.var(exp) for exp in experiments])

# only start computing mean after initial dip: from 'adjusted_start'. Method: find 500 (or fewer for small datasets) datapoints below unadjusted mean.
first_elems = np.mean(experiments[0][:50])
last_elems = np.mean(experiments[0][-50:])
quotient = first_elems / last_elems
if (quotient > 1): # Stabilized latency
    delay_threshold = np.array([min(500, np.min(n_obs)/80) for i in range(len(n_obs))])
    n_below_mean = np.array([0, 0, 0])
    min_len = min(n_obs)
    i = 0
    while (np.any(n_below_mean < delay_threshold) and i < min_len):
        for exp in range(n_exps):
            if (experiments[exp][i] < np.mean(mean_latency)):
                n_below_mean[exp] += 1
        i += 1
    if i == min_len:
        adjusted_start = 0
    else:
        adjusted_start = i
else: # message input rate exceeds throughput capacity
    adjusted_start = 0

delay_mean_latency = np.array([np.mean(exp[adjusted_start:]) for exp in experiments])
delay_variance = np.array([np.var(exp[adjusted_start:]) for exp in experiments])

print()
print(f"number of observations: {n_obs}")
print(f"throughput            : {messages_per_second} messages/s")
print()
print(f"mean latency          : [{' '.join(f'{lat:.2f}' for lat in mean_latency)}] ms")
print(f"latency variance      : [{' '.join(f'{var:.2f}' for var in variance)}] ms")
print()
if adjusted_start != 0:
    print(f"When only counting measurements after the initialization dip, i.e. after {adjusted_start} messages,")
    print(f"adjusted mean latency : [{' '.join(f'{lat:.2f}' for lat in delay_mean_latency)}] ms")
    print(f"adjusted variance     : [{' '.join(f'{var:.2f}' for var in delay_variance)}] ms")
    print()
else:
    print("Latency was increasing, so the mean wasn't adjusted.\n")

# plots
x = [list(range(len(exp))) for exp in experiments]
for exp in range(n_exps):
    plt.scatter(x[exp], experiments[exp], s=4, label=f"run {exp + 1}")
plt.title(plot_title)

# horizontal lines
x_max = np.max(n_obs)
y_max = np.max([np.max(exp) for exp in experiments])

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
plt.text(text_x, text_y, f"{line_text} = {line_mean_latency:.2f} ms")

# text box
if adjusted_start == 0:
    box_x = int(x_max * 0.73618819)
    box_y = int(y_max * 1.0137)
else:
    box_x = int(x_max * 1.027)
    box_y = int(y_max * 0.785)
label_1 = "sent messages mean"
label_2 = "messages/second mean"
value_1 = f"{int(np.mean(n_obs)):,}"
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
    fontsize=9, family='monospace'
)

plt.xlabel("Observation number")
plt.ylabel("latency [ms]")
plt.legend(handler_map={plt.scatter([], [], s=4): HandlerPathCollection(marker_pad=0.3, numpoints=1)},markerscale=5)


plt.savefig(png_save_path, dpi=300, bbox_inches='tight')
plt.show()

