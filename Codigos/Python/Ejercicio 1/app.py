import streamlit as st
import numpy as np
import matplotlib.pyplot as plt

# Configuración de la página completa
st.set_page_config(page_title="Simulador Integrador de Variables Aleatorias", layout="wide")

st.title("Simulador Integral: Variables Aleatorias en Python")

# Pestañas para los 3 temas
tab1, tab2, tab3 = st.tabs([
    "1. Concepto General", 
    "2. Variable Discreta (Suma de Dados)", 
    "3. Variable Continua (Exponencial)"
])

# =============================================================================
# 1. TEMA 1: CONCEPTO GENERAL
# =============================================================================
with tab1:
    st.header("¿Qué es una Variable Aleatoria?")
    st.write(
        "Una **variable aleatoria $X$** es una función que asigna un valor numérico "
        "a cada resultado posible de un experimento aleatorio."
    )
    st.markdown("---")
    
    col1, col2 = st.columns(2)
    with col1:
        st.subheader("Variables Discretas")
        st.write(
            "• Toman valores finitos o contables (ej. lanzar dados, conteo de defectos).\n"
            "• Se caracterizan por su **Función de Masa de Probabilidad (PMF)**: $P(X = x)$."
        )
    with col2:
        st.subheader("Variables Continuas")
        st.write(
            "• Toman infinitos valores dentro de un intervalo continuo (ej. tiempo, peso, estatura).\n"
            "• Se caracterizan por su **Función de Densidad de Probabilidad (PDF)**: $f(x)$."
        )

# =============================================================================
# 2. TEMA 2: VARIABLE DISCRETA (Suma de Dados)
# =============================================================================
with tab2:
    st.header("Variable Aleatoria Discreta: Suma de 2 Dados")
    
    col_ctrl, col_plot = st.columns([1, 2])
    
    with col_ctrl:
        st.subheader("Configuración")
        n_lanzamientos = st.slider("Número de lanzamientos:", min_value=100, max_value=50000, value=1000, step=100)
        
        # Simulación de dados
        dado1 = np.random.randint(1, 7, size=n_lanzamientos)
        dado2 = np.random.randint(1, 7, size=n_lanzamientos)
        suma_dados = dado1 + dado2
        
        media_obs = np.mean(suma_dados)
        st.metric("Media Muestral / Observada", f"{media_obs:.4f}")
        st.metric("Media Teórica E[X]", "7.0000")

    with col_plot:
        fig, ax = plt.subplots(figsize=(8, 4.5))
        
        valores, conteos = np.unique(suma_dados, return_counts=True)
        frec_rel = conteos / n_lanzamientos
        
        ax.bar(valores, frec_rel, color="#3498db", alpha=0.7, edgecolor="black", label="Frecuencia Observada")
        
        x_teorico = np.arange(2, 13)
        prob_teorica = np.array([1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1]) / 36.0
        ax.plot(x_teorico, prob_teorica, 'r--o', linewidth=2, label="Probabilidad Teórica P(X = x)")
        
        ax.set_xticks(x_teorico)
        ax.set_xlabel("Suma de los 2 Dados (x)")
        ax.set_ylabel("Probabilidad P(X = x)")
        ax.set_title("Función de Masa de Probabilidad (PMF)")
        ax.legend()
        ax.grid(axis='y', linestyle='--', alpha=0.7)
        
        st.pyplot(fig)

# =============================================================================
# 3. TEMA 3: VARIABLE CONTINUA (Distribución Exponencial)
# =============================================================================
with tab3:
    st.header("Variable Aleatoria Continua: Distribución Exponencial")
    
    col_ctrl_c, col_plot_c = st.columns([1, 2])
    
    with col_ctrl_c:
        st.subheader("Configuración")
        lambda_val = st.slider("Tasa (λ):", min_value=0.1, max_value=3.0, value=1.0, step=0.1)
        n_continua = st.select_slider("Número de datos (N):", options=[100, 500, 1000, 5000, 10000], value=1000)
        
        datos_exp = np.random.exponential(scale=1.0/lambda_val, size=n_continua)
        
        st.metric("Media Muestral / Observada", f"{np.mean(datos_exp):.4f}")
        st.metric("Media Teórica E[X]", f"{1.0/lambda_val:.4f}")

    with col_plot_c:
        fig_cont, (ax_pdf, ax_cdf) = plt.subplots(2, 1, figsize=(8, 7))
        
        # Gráfico PDF
        ax_pdf.hist(datos_exp, bins=30, density=True, color="#3498db", alpha=0.6, edgecolor="white", label="Muestra Simulada")
        x_seq = np.linspace(0, np.max(datos_exp) + 1, 500)
        pdf_teorica = lambda_val * np.exp(-lambda_val * x_seq)
        ax_pdf.plot(x_seq, pdf_teorica, 'r-', linewidth=2, label="Densidad Teórica f(x)")
        ax_pdf.set_title("Función de Densidad de Probabilidad (PDF)")
        ax_pdf.set_xlabel("Tiempo / Valor (x)")
        ax_pdf.set_ylabel("Densidad f(x)")
        ax_pdf.legend()
        ax_pdf.grid(True, linestyle='--', alpha=0.5)
        
        # Gráfico CDF
        x_sorted = np.sort(datos_exp)
        y_cdf = np.arange(1, n_continua + 1) / n_continua
        ax_cdf.step(x_sorted, y_cdf, color="#2ecc71", where="post", label="CDF Empírica")
        cdf_teorica = 1.0 - np.exp(-lambda_val * x_seq)
        ax_cdf.plot(x_seq, cdf_teorica, 'r--', linewidth=2, label="CDF Teórica F(x)")
        ax_cdf.set_title("Función de Distribución Acumulada (CDF)")
        ax_cdf.set_xlabel("Tiempo / Valor (x)")
        ax_cdf.set_ylabel("Probabilidad P(X ≤ x)")
        ax_cdf.legend()
        ax_cdf.grid(True, linestyle='--', alpha=0.5)
        
        plt.tight_layout()
        st.pyplot(fig_cont)