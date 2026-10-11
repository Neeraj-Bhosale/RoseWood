package com.restaurant.controller;

import com.restaurant.service.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.Map;

public class ReportController {

    @FXML private BarChart<String, Number> categorySalesBarChart;
    @FXML private PieChart paymentMethodsPieChart;
    @FXML private BarChart<String, Number> topItemsBarChart;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        loadCharts();
    }

    public void loadCharts() {
        loadCategorySalesChart();
        loadPaymentMethodsChart();
        loadTopItemsChart();
    }

    private void loadCategorySalesChart() {
        categorySalesBarChart.getData().clear();
        Map<String, Double> data = reportService.getSalesByCategory();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue (₹)");

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        categorySalesBarChart.getData().add(series);
    }

    private void loadPaymentMethodsChart() {
        paymentMethodsPieChart.getData().clear();
        Map<String, Double> data = reportService.getPaymentMethodBreakdown();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey() + " (₹" + entry.getValue() + ")", entry.getValue()));
        }

        paymentMethodsPieChart.setData(pieData);
    }

    private void loadTopItemsChart() {
        topItemsBarChart.getData().clear();
        Map<String, Integer> data = reportService.getMostPopularItems(5);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantity Sold");

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        topItemsBarChart.getData().add(series);
    }

    @FXML
    public void handleRefresh() {
        loadCharts();
    }
}
