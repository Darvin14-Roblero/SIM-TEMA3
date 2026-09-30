package com.darvinsistems.app.variablesaleatorias;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class GeneradorVariables extends Application {

    private final Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Generación de Variables Aleatorias (Exponencial)");

        // Configuración de ejes del gráfico
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Intervalos (Rangos de valores)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frecuencia");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Distribución Exponencial (Método Inversión Transformada)");

        Button btnGenerar = new Button("Generar 1,000 Variables Aleatorias");
        Label lblEstado = new Label("Presiona el botón para generar datos.");

        btnGenerar.setOnAction(e -> {
            barChart.getData().clear();

            double lambda = 1.0;
            int n = 1000;
            int numBins = 10;
            int[] bins = new int[numBins];

            // Generación mediante la Inversa de la CDF: X = -ln(1 - U) / lambda
            for (int i = 0; i < n; i++) {
                double u = random.nextDouble(); // U ~ Uniforme(0, 1)
                double x = -Math.log(1 - u) / lambda;

                // Asignar a un intervalo
                int binIndex = (int) (x * 2); // Ancho de intervalo ~0.5
                if (binIndex >= numBins) {
                    binIndex = numBins - 1;
                }
                bins[binIndex]++;
            }

            // Llenar el gráfico con las frecuencias
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Variables Generadas");

            for (int i = 0; i < numBins; i++) {
                String rango = String.format("%.1f - %.1f", i * 0.5, (i + 1) * 0.5);
                if (i == numBins - 1) rango = String.format("> %.1f", i * 0.5);
                series.getData().add(new XYChart.Data<>(rango, bins[i]));
            }

            barChart.getData().add(series);
            lblEstado.setText("Se generaron 1,000 números aleatorios exponencialmente distribuidos.");
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(btnGenerar, lblEstado, barChart);

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}