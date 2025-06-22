
import numpy as np

vm_base_path = r"C:\Users\chris\Dropbox\Christian\DTU\Kandidat\Thesis\experiments\VMs\experiment_results\throughput\\"
save_base_path = r"C:\Users\chris\Dropbox\Christian\DTU\Kandidat\Thesis\experiments\local_comparison\results\vms\\"

#text_times = ["025", "033", "05"]
#text_times = ["075", "1"]
text_times = ["5"]

print()
for text_time in text_times:
    print("starting " + text_time)
    run_data = []
    for run in range(1, 4):
        run_data.append(np.loadtxt(vm_base_path + fr"{text_time}ms_sleep_{run}.txt"))
    max_len = np.max([len(exp) for exp in run_data])
    
    print("fetched the data")

    run_sum = np.zeros(max_len)
    n_i_obs = np.zeros(max_len)
    for exp_results in run_data:
        for i in range(len(exp_results)):
            
            n_i_obs[i] += 1
            run_sum[i] += exp_results[i]

    assert np.all(n_i_obs > 0), "something went wrong in counting"

    run_mean = run_sum / n_i_obs
    int_mean = [int(val) for val in run_mean]
    print("computed the mean")

    save_path = save_base_path + fr"{text_time}ms_sleep_1.txt"
    with open(save_path, 'w') as save_file:
        [save_file.write(f"{val}\n") for val in int_mean]
    
    print("saved to: " + save_path)
    print()

