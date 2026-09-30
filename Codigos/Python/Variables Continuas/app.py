import tkinter as tk
from tkinter import ttk
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

class AppVariableContinua:
    def __init__(self, root):
        self.root = root
        self.root.title("Variable Aleatoria Continua - Distribución Exponencial")
        self.root.geometry("900x650")

        # --- PANEL DE CONTROL (SUPERIOR) ---
        frame_control = ttk.LabelFrame(self.root, text=" Configuración del Experimento ", padding=10)
        frame_control.pack(side=tk.TOP, fill=tk.X, padx=10, pady=5)

        # Parámetro Lambda (Tasa / Rate)
        ttk.Label(frame_control, text="Tasa (λ):").grid(row=0, column=0, padx=5, pady=5)
        self.scale_lambda = ttk.Scale(frame_control, from_=0.1, to=3.0, value=1.0, command=self.actualizar_label_lambda)
        self.scale_lambda.grid(row=0, column=1, padx=5, pady=5, sticky="ew")
        self.lbl_lambda = ttk.Label(frame_control, text="1.0")
        self.lbl_lambda.grid(row=0, column=2, padx=5, pady=5)

        # Tamaño de la Muestra (N)
        ttk.Label(frame_control, text="Número de datos (N):").grid(row=0, column=3, padx=5, pady=5)
        self.combo_n = ttk.Combobox(frame_control, values=[100, 500, 1000, 5000, 10000], state="readonly", width=8)
        self.combo_n.set(1000)
        self.combo_n.grid(row=0, column=4, padx=5, pady=5)

        # Botón para Simular
        btn_simular = ttk.Button(frame_control, text="Generar Simulación", command=self.simular)
        btn_simular.grid(row=0, column=5, padx=15, pady=5)

        # Configurar expansión de columnas en el frame
        frame_control.columnconfigure(1, weight=1)

        # --- PANEL DE GRÁFICOS (CENTRO) ---
        frame_graficos = ttk.Frame(self.root)
        frame_graficos.pack(side=tk.TOP, fill=tk.BOTH, expand=True, padx=10, pady=5)

        # Figura de Matplotlib con 2 subgráficos
        self.fig, (self.ax_pdf, self.ax_cdf) = plt.subplots(1, 2, figsize=(9, 4.5))
        self.fig.tight_layout(pad=3.0)

        self.canvas = FigureCanvasTkAgg(self.fig, master=frame_graficos)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

        # --- PANEL DE ESTADÍSTICAS (INFERIOR) ---
        frame_stats = ttk.LabelFrame(self.root, text=" Comparativa de Resultados (Media E[X] = 1/λ) ", padding=10)
        frame_stats.pack(side=tk.BOTTOM, fill=tk.X, padx=10, pady=5)

        self.lbl_media_teorica = ttk.Label(frame_stats, text="Media Teórica E[X]: -", font=("Arial", 10, "bold"))
        self.lbl_media_teorica.pack(side=tk.LEFT, expand=True)

        self.lbl_media_muestral = ttk.Label(frame_stats, text="Media Muestral (Simulada): -", font=("Arial", 10, "bold"))
        self.lbl_media_muestral.pack(side=tk.LEFT, expand=True)

        # Ejecutar simulación inicial
        self.simular()

    def actualizar_label_lambda(self, val):
        self.lbl_lambda.config(text=f"{float(val):.2f}")

    def simular(self):
        lam = float(self.scale_lambda.get())
        n = int(self.combo_n.get())

        # Generar variable aleatoria continua exponencial: X ~ Exp(λ)
        # En NumPy el parámetro es la escala beta = 1/λ
        datos = np.random.exponential(scale=1/lam, size=n)

        # Estadísticas
        media_teorica = 1 / lam
        media_muestral = np.mean(datos)

        self.lbl_media_teorica.config(text=f"Media Teórica E[X]: {media_teorica:.4f}")
        self.lbl_media_muestral.config(text=f"Media Muestral: {media_muestral:.4f}")

        # Limpiar ejes
        self.ax_pdf.clear()
        self.ax_cdf.clear()

        x = np.linspace(0, max(datos) + 1, 500)

        # --- 1. Gráfico de Densidad de Probabilidad (PDF) ---
        # Histogramas normalizados representando la densidad observada
        self.ax_pdf.hist(datos, bins=30, density=True, alpha=0.5, color="#3498db", label="Muestra Simulada")
        
        # Curva Teórica PDF: f(x) = λ * e^(-λx)
        pdf_teorica = lam * np.exp(-lam * x)
        self.ax_pdf.plot(x, pdf_teorica, 'r-', lw=2, label="Densidad Teórica f(x)")
        
        self.ax_pdf.set_title("Función de Densidad de Probabilidad (PDF)")
        self.ax_pdf.set_xlabel("Tiempo / Valor (x)")
        self.ax_pdf.set_ylabel("Densidad f(x)")
        self.ax_pdf.legend()
        self.ax_pdf.grid(True, linestyle="--", alpha=0.5)

        # --- 2. Gráfico de Distribución Acumulada (CDF) ---
        # CDF Empírica
        x_ordenado = np.sort(datos)
        y_empirica = np.arange(1, n + 1) / n
        self.ax_cdf.plot(x_ordenado, y_empirica, color="#2ecc71", lw=1.5, label="CDF Empírica")

        # Curva Teórica CDF: F(x) = 1 - e^(-λx)
        cdf_teorica = 1 - np.exp(-lam * x)
        self.ax_cdf.plot(x, cdf_teorica, 'r--', lw=2, label="CDF Teórica F(x)")

        self.ax_cdf.set_title("Función de Distribución Acumulada (CDF)")
        self.ax_cdf.set_xlabel("Tiempo / Valor (x)")
        self.ax_cdf.set_ylabel("Probabilidad P(X ≤ x)")
        self.ax_cdf.legend()
        self.ax_cdf.grid(True, linestyle="--", alpha=0.5)

        # Redibujar canvas
        self.canvas.draw()

if __name__ == "__main__":
    root = tk.Tk()
    app = AppVariableContinua(root)
    root.mainloop()