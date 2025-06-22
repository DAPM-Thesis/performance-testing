
import numpy as np

source_output = np.loadtxt("source_experiment_2.txt")
sink_output = np.loadtxt("sink_experiment_2.txt")
obs_count = 5200

def get_misaligned_count(output, expected):
    n_output, n_expected = len(output), len(expected)
    incorrect = abs(n_output - n_expected)
    for i in range(min(n_output, n_expected)):
        if output[i] != expected[i]:
            incorrect += 1
    return incorrect

expected = [i+1 for i in range(obs_count)]
incorrect_source = get_misaligned_count(source_output, expected)
incorrect_sink = get_misaligned_count(sink_output, expected)

print("no sleep results:")
print(f"    # incorrects in the source: {incorrect_source}")
print(f"    # incorrects in the sink  : {incorrect_sink}")