import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;

public class Main extends JFrame {

    public Main() {
        setTitle("Simulador Integral: Variables Aleatorias (Discretas y Continuas)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. Concepto General", crearPanelGeneral());
        tabbedPane.addTab("2. Variable Discreta (Suma de Dados)", crearPanelDiscreto());
        tabbedPane.addTab("3. Variable Continua (Exponencial)", crearPanelContinuo());

        add(tabbedPane);
    }

    private JPanel crearPanelGeneral() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea txtInfo = new JTextArea(
                "¿QUÉ ES UNA VARIABLE ALEATORIA?\n\n" +
                        "Una variable aleatoria X es una función matemática que asigna un valor numérico " +
                        "a cada resultado de un experimento aleatorio.\n\n" +
                        "• Variables Discretas: Toman valores finitos o contables (ej. número de caras, suma de dados).\n" +
                        "  Se describen mediante la Función de Masa de Probabilidad (PMF): P(X = x).\n\n" +
                        "• Variables Continuas: Toman infinitos valores en un intervalo continuo (ej. tiempo, peso, temperatura).\n" +
                        "  Se describen mediante la Función de Densidad de Probabilidad (PDF): f(x) donde P(X = x) = 0.\n\n" +
                        "Selecciona las pestañas superiores para interactuar con cada tipo de variable."
        );
        txtInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtInfo.setEditable(false);
        txtInfo.setBackground(panel.getBackground());

        panel.add(txtInfo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelDiscreto() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Simulación Discreta"));

        JButton btnLanzar1 = new JButton("Lanzar 1 vez");
        JButton btnLanzar1000 = new JButton("Simular 1,000 lanzamientos");
        JButton btnReiniciar = new JButton("Reiniciar");
        JLabel lblStats = new JLabel("Lanzamientos totales: 0");

        topPanel.add(btnLanzar1);
        topPanel.add(btnLanzar1000);
        topPanel.add(btnReiniciar);
        topPanel.add(lblStats);

        PanelGraficoDiscreto graficoDiscreto = new PanelGraficoDiscreto();

        int[] conteos = new int[13];
        final int[] totalLanzamientos = {0};

        ActionListener simularDiscreto = e -> {
            int veces = (e.getSource() == btnLanzar1) ? 1 : 1000;
            Random r = new Random();
            for (int i = 0; i < veces; i++) {
                int dado1 = r.nextInt(6) + 1;
                int dado2 = r.nextInt(6) + 1;
                conteos[dado1 + dado2]++;
                totalLanzamientos[0]++;
            }
            lblStats.setText("Lanzamientos totales: " + totalLanzamientos[0]);
            graficoDiscreto.actualizar(conteos, totalLanzamientos[0]);
        };

        btnLanzar1.addActionListener(simularDiscreto);
        btnLanzar1000.addActionListener(simularDiscreto);
        btnReiniciar.addActionListener(e -> {
            Arrays.fill(conteos, 0);
            totalLanzamientos[0] = 0;
            lblStats.setText("Lanzamientos totales: 0");
            graficoDiscreto.actualizar(conteos, 0);
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(graficoDiscreto, BorderLayout.CENTER);

        return panel;
    }

    private class PanelGraficoDiscreto extends JPanel {
        private int[] conteos = new int[13];
        private int total = 0;
        private final double[] probTeorica = {0, 0, 1/36., 2/36., 3/36., 4/36., 5/36., 6/36., 5/36., 4/36., 3/36., 2/36., 1/36.};

        public void actualizar(int[] conteos, int total) {
            this.conteos = conteos.clone();
            this.total = total;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 50;

            g2.drawLine(padding, h - padding, w - padding, h - padding);
            g2.drawLine(padding, padding, padding, h - padding);

            int barWidth = (w - 2 * padding) / 11;

            for (int x = 2; x <= 12; x++) {
                int bx = padding + (x - 2) * barWidth + 5;
                double probObs = (total == 0) ? 0 : (double) conteos[x] / total;

                int barHeight = (int) (probObs * (h - 2 * padding) * 3);
                int by = h - padding - barHeight;

                g2.setColor(new Color(52, 152, 219, 180));
                g2.fillRect(bx, by, barWidth - 10, barHeight);

                int teoY = h - padding - (int) (probTeorica[x] * (h - 2 * padding) * 3);
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(bx - 2, teoY, bx + barWidth - 8, teoY);

                g2.setColor(Color.BLACK);
                g2.drawString(String.valueOf(x), bx + (barWidth - 10) / 2 - 3, h - padding + 20);
            }
        }
    }

    private JPanel crearPanelContinuo() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Simulación Continua (Exponencial)"));

        topPanel.add(new JLabel("Tasa (λ):"));
        JSlider sliderLambda = new JSlider(1, 30, 10);
        JLabel lblLambdaVal = new JLabel("1.0");
        topPanel.add(sliderLambda);
        topPanel.add(lblLambdaVal);

        topPanel.add(new JLabel("Muestra (N):"));
        JComboBox<Integer> comboN = new JComboBox<>(new Integer[]{100, 500, 1000, 5000});
        comboN.setSelectedItem(1000);
        topPanel.add(comboN);

        JButton btnSimular = new JButton("Generar Simulación");
        topPanel.add(btnSimular);

        PanelGraficoContinuo graficoContinuo = new PanelGraficoContinuo();

        Runnable simularContinuo = () -> {
            double lambda = sliderLambda.getValue() / 10.0;
            int n = (Integer) comboN.getSelectedItem();
            double[] datos = new double[n];
            Random r = new Random();
            for (int i = 0; i < n; i++) {
                datos[i] = -Math.log(1.0 - r.nextDouble()) / lambda;
            }
            Arrays.sort(datos);
            graficoContinuo.actualizar(datos, lambda);
        };

        sliderLambda.addChangeListener(e -> {
            lblLambdaVal.setText(String.format("%.1f", sliderLambda.getValue() / 10.0));
        });

        btnSimular.addActionListener(e -> simularContinuo.run());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(graficoContinuo, BorderLayout.CENTER);

        simularContinuo.run();

        return panel;
    }

    private class PanelGraficoContinuo extends JPanel {
        private double[] datos;
        private double lambda = 1.0;

        public void actualizar(double[] datos, double lambda) {
            this.datos = datos;
            this.lambda = lambda;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datos == null || datos.length == 0) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 40;
            int graphW = w - 2 * padding;
            int graphH = h - 2 * padding;

            double maxVal = Math.max(4.0, datos[datos.length - 1]);
            double maxDensidad = lambda * 1.2;

            g2.setColor(Color.BLACK);
            g2.drawLine(padding, h - padding, padding + graphW, h - padding);
            g2.drawLine(padding, padding, padding, h - padding);

            int numBins = 30;
            int[] bins = new int[numBins];
            double binWidth = maxVal / numBins;

            for (double d : datos) {
                int idx = (int) (d / binWidth);
                if (idx >= numBins) idx = numBins - 1;
                bins[idx]++;
            }

            g2.setColor(new Color(46, 204, 113, 160));
            for (int i = 0; i < numBins; i++) {
                double densidadObs = (double) bins[i] / (datos.length * binWidth);
                int binH = (int) ((densidadObs / maxDensidad) * graphH);
                int bx = padding + (int) ((i * binWidth / maxVal) * graphW);
                int bw = (int) (binWidth / maxVal * graphW);
                int by = h - padding - binH;

                g2.fillRect(bx, by, bw - 1, binH);
            }

            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            int prevX = -1, prevY = -1;
            for (int p = 0; p <= graphW; p++) {
                double xVal = (p / (double) graphW) * maxVal;
                double yVal = lambda * Math.exp(-lambda * xVal);

                int currX = padding + p;
                int currY = h - padding - (int) ((yVal / maxDensidad) * graphH);

                if (prevX != -1) {
                    g2.drawLine(prevX, prevY, currX, currY);
                }
                prevX = currX;
                prevY = currY;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}