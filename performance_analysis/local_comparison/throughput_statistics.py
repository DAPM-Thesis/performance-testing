
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerPathCollection

results_path = r"C:\Users\chris\Dropbox\Christian\DTU\Kandidat\Thesis\experiments\local_comparison\results\\"
save_base_path = r"C:\Users\chris\Dropbox\Christian\DTU\Kandidat\Thesis\experiments\local_comparison\plots\\"

setups = ["desktop", "laptop", "vms"]
experiments = [("5", "5", 1), ("1", "1", 1), ("075", "0.75", 1), ("05", "0.5", 2), ("033", "0.33", 2), ("025", "0.25", 2)]

nanos_to_millis_divisor = 10**6

for (sleep, sleep_pretty, run_number) in experiments:
    results = []

    # load the data for all three setups with the sleep time
    for i in range(len(setups)):
        if setups[i] == 'vms':
            run_number = 1
        path = results_path + fr"{setups[i]}\{sleep}ms_sleep_{run_number}.txt"
        results.append(np.loadtxt(path) / nanos_to_millis_divisor)
        if (sleep, setups[i]) in [('033', 'vms'), ('075', 'desktop'), ('5', 'laptop')]:
            print()
            print(setups[i] + ': ' + str(len(results[i]) / (5*60)))
            print()

        x = [i+1 for i in range(len(results[i]))]
        plt.scatter(x, results[i], s=4, label=setups[i])

    plt.title(f"Comparative Latency: {sleep_pretty} ms Sleep", fontsize=16)
    plt.xlabel("Observation number", fontsize=14)
    plt.ylabel("latency [ms]", fontsize=14)
    plt.legend(handler_map={plt.scatter([], [], s=4): HandlerPathCollection(marker_pad=0.3, numpoints=1)}, markerscale=5, fontsize=12)
    plt.xticks(fontsize=12)
    plt.yticks(fontsize=12)

    png_save_path = save_base_path + f"{sleep}_aggregated_2.png"

    plt.savefig(png_save_path, dpi=300, bbox_inches='tight')
    print(f"Saved image to {png_save_path}")
    plt.clf()
