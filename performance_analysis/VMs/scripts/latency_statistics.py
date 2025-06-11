
import os
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerPathCollection

file_number_strs = ["033"]
pretty_number_strs = ["0.33"]
#file_number_strs = ["1", "075", "05", "033", "025"]
#pretty_number_strs = ["1", "0.75", "0.5", "0.33", "0.25"]

for (file_number_str, pretty_number_str) in zip(file_number_strs, pretty_number_strs):
    print(f"\n\nComputing for: {pretty_number_str}")
    experiment_name = f"{file_number_str}ms_sleep"
    plot_title = f"Latency: {pretty_number_str} ms Sleep"
    png_name = f"{file_number_str}-ms-throughput-experiments.png"

    png_save_path = "VMs/graphs/round2_3PE/" + png_name

    nano_per_milli = 10e9 / 10e3
    min_exp = 1
    max_exp = 3
    n_exps = max_exp - min_exp + 1
    minute_count = 5
    duration_seconds = 60 * minute_count


    script_dir = os.path.dirname(os.path.abspath(__file__))
    data_root = os.path.join(script_dir, "../experiment_results/round2_3PE")
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
    first_elems = max(np.mean(experiments[0][:100]), 0.0001)
    last_elems = max(np.mean(experiments[0][-100:]), 0.0001)
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
    plt.title(plot_title, fontsize=16)

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

    if pretty_number_str == "0.25":
        text_x, text_y = (625000, 2875)
    
    if pretty_number_str != '0.33':
        plt.axhline(y=np.mean(line_mean_latency), color='r', linestyle='-')
        plt.text(text_x, text_y, f"{line_text} = {line_mean_latency:.2f} ms", fontsize = 14)

    # text box
    if adjusted_start == 0:
        box_x = int(x_max * 0.825)
        box_y = int(y_max * 1.0001)
    else:
        box_x = int(x_max * 1.02)
        box_y = int(y_max * 0.75)
    label_1 = "sent messages mean"
    label_2 = "messages/second mean"
    value_1 = f"{int(np.mean(n_obs)):,}"
    value_2 = f"{int(np.mean(messages_per_second)):,}"
    label_width = max(len(label_1), len(label_2))
    value_width = max(len(value_1), len(value_2))
    obs_count_str = f"{label_1:>{label_width}}: {value_1:>{value_width}}"
    throughput_mean_str = f"{label_2:>{label_width}}: {value_2:>{value_width}}"
    info_text = f"{obs_count_str}\n{throughput_mean_str}"

    if pretty_number_str != '0.33':
        plt.text(
            box_x, box_y,info_text,
            ha='right', va='top',
            bbox=dict(boxstyle='round', facecolor='white', edgecolor='gray', alpha=0.85),
            fontsize=12, family='monospace'
        )

    plt.xlabel("Observation number", fontsize=14)
    plt.ylabel("latency [ms]", fontsize=14)
    plt.legend(handler_map={plt.scatter([], [], s=4): HandlerPathCollection(marker_pad=0.3, numpoints=1)},markerscale=5, fontsize=12)
    
    if pretty_number_str == '0.33':
        plt.xticks([0, 200000, 400000, 600000], fontsize=12)
    else:
        plt.xticks(fontsize=12)
    
    
    plt.yticks(fontsize=12)
    ax = plt.gca()
    ax.xaxis.get_offset_text().set_fontsize(12)
    ax.yaxis.get_offset_text().set_fontsize(12)

    plt.savefig(png_save_path, dpi=300, bbox_inches='tight')
    print(f"Saved image to {png_save_path}")
    plt.clf()

