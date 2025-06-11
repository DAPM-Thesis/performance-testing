
import os
import numpy as np

n_obs = 10000
script_dir = os.path.dirname(os.path.abspath(__file__))
data_root = os.path.join(script_dir, "../experiment_results/alignment")
source_path = os.path.normpath(os.path.join(data_root, "10k_source_1.txt"))
sink_path = os.path.normpath(os.path.join(data_root, "10k_sink_1.txt"))

source_output = np.loadtxt(source_path)
sink_output = np.loadtxt(sink_path)

def get_misaligned_count(output, expected):
    n_output, n_expected = len(output), len(expected)
    incorrect = abs(n_output - n_expected)
    for i in range(min(n_output, n_expected)):
        if output[i] != expected[i]:
            incorrect += 1
    return incorrect

expected = [i+1 for i in range(n_obs)]
incorrect_source = get_misaligned_count(source_output, expected)
incorrect_sink = get_misaligned_count(sink_output, expected)

print(f"\nno sleep results for {n_obs:,} messages:")
print(f"    # incorrects in the source: {incorrect_source}")
print(f"    # incorrects in the sink  : {incorrect_sink}\n")