import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import expon

# Parámetros (sin fijar semilla para que genere valores distintos cada vez)
n = 1000             
lambda_param = 2     

# 1. Generar variables uniformes U ~ U(0,1)
u = np.random.uniform(0, 1, n)

# 2. Aplicar la función inversa acumulada
x_simuladas = -np.log(1 - u) / lambda_param

# Visualización
plt.figure(figsize=(8, 5))
plt.hist(x_simuladas, bins=30, density=True, alpha=0.6, color='skyblue', edgecolor='white', label='Simulado')

x_teorico = np.linspace(0, max(x_simuladas), 200)
plt.plot(x_teorico, expon.pdf(x_teorico, scale=1/lambda_param), 'r-', lw=2, label='Teórico (expon.pdf)')

plt.title("Generación de Variable Exponencial (Python)")
plt.xlabel("x")
plt.ylabel("Densidad")
plt.legend()
plt.grid(True, alpha=0.3)
plt.show()