
import numpy as np
import matplotlib.pyplot as plt

data_in_ms = np.loadtxt("latency_of_experiment_2.txt")

x = [i for i in range(len(data_in_ms))]


plt.scatter(x, data_in_ms)
plt.title("Backpressure Latency")
plt.xlabel("(every 2500th) observation #")
plt.ylabel("latency in ms")
plt.show()