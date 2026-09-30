
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

public class AppVariableContinua extends JFrame {

    private JSlider sliderLambda;
    private JLabel lblLambdaVal;
    private JComboBox<Integer> comboN;
    private JLabel lblMediaTeorica;
    private JLabel lblMediaMuestral;
    private PanelGraficos panelGraficos;

    private double lambda = 1.0;
    private int sampleSize = 1000;
    private double[] datos;

    public AppVariableContinua() {
        setTitle("Variable Aleatoria Continua - Distribución Exponencial (Java)");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PANEL SUPERIOR: CONTROLES ---
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelControl.setBorder(BorderFactory.createTitledBorder("Configuración del Experimento"));

        panelControl.add(new JLabel("Tasa (λ):"));
        sliderLambda = new JSlider(1, 30, 10); // Representa 0.1 a 3.0
        lblLambdaVal = new JLabel("1.0");
        panelControl.add(sliderLambda);
        panelControl.add(lblLambdaVal);

        panelControl.add(new JLabel("Número de datos (N):"));
        Integer[] opcionesN = {100, 500, 1000, 5000, 10000};
        comboN = new JComboBox<>(opcionesN);
        comboN.setSelectedItem(1000);
        panelControl.add(comboN);

        JButton btnSimular = new JButton("Simular Nuevamente");
        panelControl.add(btnSimular);

        add(panelControl, BorderLayout.NORTH);

        // --- PANEL CENTRAL: GRÁFICOS ---
        panelGraficos = new PanelGraficos();
        add(panelGraficos, BorderLayout.CENTER);

        // --- PANEL INFERIOR: ESTADÍSTICAS ---
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        panelStats.setBorder(BorderFactory.createTitledBorder("Comparativa de Resultados"));

        lblMediaTeorica = new JLabel("Media Teórica E[X]: -");
        lblMediaTeorica.setFont(new Font("Arial", Font.BOLD, 12));
        lblMediaMuestral = new JLabel("Media Muestral: -");
        lblMediaMuestral.setFont(new Font("Arial", Font.BOLD, 12));

        panelStats.add(lblMediaTeorica);
        panelStats.add(lblMediaMuestral);

        add(panelStats, BorderLayout.SOUTH);

        // --- EVENTOS ---
        sliderLambda.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lambda = sliderLambda.getValue() / 10.0;
                lblLambdaVal.setText(String.format("%.1f", lambda));
            }
        });

        btnSimular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sampleSize = (Integer) comboN.getSelectedItem();
                ejecutarSimulacion();
            }
        });

        // Simulación inicial
        ejecutarSimulacion();
    }

    private void ejecutarSimulacion() {
        Random rand = new Random();
        datos = new double[sampleSize];

        // Generar muestras mediante Transformada Inversa: X = -ln(1 - U) / λ
        double suma = 0.0;
        for (int i = 0; i < sampleSize; i++) {
            double u = rand.nextDouble();
            datos[i] = -Math.log(1.0 - u) / lambda;
            suma += datos[i];
        }

        double mediaTeorica = 1.0 / lambda;
        double mediaMuestral = suma / sampleSize;

        lblMediaTeorica.setText(String.format("Media Teórica E[X]: %.4f", mediaTeorica));
        lblMediaMuestral.setText(String.format("Media Muestral (Simulada): %.4f", mediaMuestral));

        panelGraficos.actualizarDatos(datos, lambda);
    }

    // --- CLASE INTERNA PARA DIBUJAR PDF Y CDF ---
    private class PanelGraficos extends JPanel {
        private double[] datosLocal;
        private double lamLocal = 1.0;

        public void actualizarDatos(double[] datos, double lambda) {
            this.datosLocal = datos.clone();
            Arrays.sort(this.datosLocal);
            this.lamLocal = lambda;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datosLocal == null || datosLocal.length == 0) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() / 2;
            int h = getHeight();

            dibujarPDF(g2, 0, 0, w, h);
            dibujarCDF(g2, w, 0, w, h);
        }

        private void dibujarPDF(Graphics2D g2, int xOff, int yOff, int w, int h) {
            int padding = 40;
            int graphW = w - 2 * padding;
            int graphH = h - 2 * padding;

            double maxVal = Math.max(4.0, datosLocal[datosLocal.length - 1]);
            double maxDensidad = lamLocal * 1.2;

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Función de Densidad de Probabilidad (PDF)", xOff + padding, yOff + 20);

            // Ejes
            g2.drawLine(xOff + padding, yOff + h - padding, xOff + padding + graphW, yOff + h - padding);
            g2.drawLine(xOff + padding, yOff + padding, xOff + padding, yOff + h - padding);

            // Histograma (Empírico)
            int numBins = 25;
            int[] bins = new int[numBins];
            double binWidth = maxVal / numBins;

            for (double d : datosLocal) {
                int idx = (int) (d / binWidth);
                if (idx >= numBins) idx = numBins - 1;
                bins[idx]++;
            }

            g2.setColor(new Color(52, 152, 219, 150));
            for (int i = 0; i < numBins; i++) {
                double densidadObs = (double) bins[i] / (datosLocal.length * binWidth);
                int binH = (int) ((densidadObs / maxDensidad) * graphH);
                int bx = xOff + padding + (int) ((i * binWidth / maxVal) * graphW);
                int bw = (int) (binWidth / maxVal * graphW);
                int by = yOff + h - padding - binH;

                g2.fillRect(bx, by, bw - 1, binH);
            }

            // Curva Teórica PDF: f(x) = λ e^(-λx)
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            int prevX = -1, prevY = -1;
            for (int p = 0; p <= graphW; p++) {
                double xVal = (p / (double) graphW) * maxVal;
                double yVal = lamLocal * Math.exp(-lamLocal * xVal);

                int currX = xOff + padding + p;
                int currY = yOff + h - padding - (int) ((yVal / maxDensidad) * graphH);

                if (prevX != -1) {
                    g2.drawLine(prevX, prevY, currX, currY);
                }
                prevX = currX;
                prevY = currY;
            }
        }

        private void dibujarCDF(Graphics2D g2, int xOff, int yOff, int w, int h) {
            int padding = 40;
            int graphW = w - 2 * padding;
            int graphH = h - 2 * padding;

            double maxVal = Math.max(4.0, datosLocal[datosLocal.length - 1]);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Función de Distribución Acumulada (CDF)", xOff + padding, yOff + 20);

            // Ejes
            g2.drawLine(xOff + padding, yOff + h - padding, xOff + padding + graphW, yOff + h - padding);
            g2.drawLine(xOff + padding, yOff + padding, xOff + padding, yOff + h - padding);

            // CDF Empírica
            g2.setColor(new Color(46, 204, 113));
            g2.setStroke(new BasicStroke(1.5f));
            int prevX = xOff + padding;
            int prevY = yOff + h - padding;

            int step = Math.max(1, datosLocal.length / 200);
            for (int i = 0; i < datosLocal.length; i += step) {
                double xVal = datosLocal[i];
                double yVal = (double) (i + 1) / datosLocal.length;

                int currX = xOff + padding + (int) ((xVal / maxVal) * graphW);
                int currY = yOff + h - padding - (int) (yVal * graphH);

                g2.drawLine(prevX, prevY, currX, currY);
                prevX = currX;
                prevY = currY;
            }

            // Curva Teórica CDF: F(x) = 1 - e^(-λx)
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            prevX = -1; prevY = -1;
            for (int p = 0; p <= graphW; p++) {
                double xVal = (p / (double) graphW) * maxVal;
                double yVal = 1.0 - Math.exp(-lamLocal * xVal);

                int currX = xOff + padding + p;
                int currY = yOff + h - padding - (int) (yVal * graphH);

                if (prevX != -1) {
                    g2.drawLine(prevX, prevY, currX, currY);
                }
                prevX = currX;
                prevY = currY;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppVariableContinua().setVisible(true);
            }
        });
    }
}