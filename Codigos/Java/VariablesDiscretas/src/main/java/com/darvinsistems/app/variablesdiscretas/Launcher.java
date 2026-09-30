package com.darvinsistems.app.variablesdiscretas;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class VariableDiscretaGUI extends JFrame {

    public VariableDiscretaGUI() {
        setTitle("Variable Aleatoria Discreta - Suma de 2 Dados");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Calcular distribución de probabilidad de X = suma de 2 dados
        Map<Integer, Double> probabilidades = calcularDistribucion();

        // 2. Crear dataset para el gráfico
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] columnas = {"Suma (x)", "Casos Favorables", "P(X = x)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnas, 0);

        for (int x = 2; x <= 12; x++) {
            double prob = probabilidades.get(x);
            dataset.addValue(prob, "Probabilidad", String.valueOf(x));

            // Casos favorables de 36 totales
            int casos = (int) Math.round(prob * 36);
            tableModel.addRow(new Object[]{x, casos + "/36", String.format("%.4f", prob)});
        }

        // 3. Crear gráfico de barras
        JFreeChart chart = ChartFactory.createBarChart(
                "Distribución de Probabilidad P(X = x)",
                "Suma de Dados (X)",
                "Probabilidad",
                dataset
        );

        // 4. Construir Interfaz
        setLayout(new BorderLayout());
        add(new ChartPanel(chart), BorderLayout.CENTER);

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 150));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private Map<Integer, Double> calcularDistribucion() {
        Map<Integer, Double> map = new HashMap<>();
        for (int d1 = 1; d1 <= 6; d1++) {
            for (int d2 = 1; d2 <= 6; d2++) {
                int suma = d1 + d2;
                map.put(suma, map.getOrDefault(suma, 0.0) + 1.0);
            }
        }
        // Convertir conteo a probabilidad (dividir entre 36)
        for (int key : map.keySet()) {
            map.put(key, map.get(key) / 36.0);
        }
        return map;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VariableDiscretaGUI().setVisible(true);
        });
    }
}