//
//  TelemetryChartView.swift
//  agroguardian_app_ios
//
//  Created by Eren on 7.05.2025.
//
import SwiftUI
import Charts

struct TelemetryChartView: View {
    let telemetryData: [TelemetryData]
    
    enum MetricType: String, CaseIterable, Identifiable {
        case temperature
        case humidity
        case soilMoisture

        var id: String { rawValue }
        var displayName: String {
            switch self {
            case .temperature: return "Temperature"
            case .humidity: return "Humidity"
            case .soilMoisture: return "Soil Moisture"
            }
        }
        var color: Color {
            switch self {
            case .temperature: return .red
            case .humidity: return .blue
            case .soilMoisture: return .green
            }
        }
    }

    private var chartData: [TelemetryChartData] {
        telemetryData.flatMap { reading in
            MetricType.allCases.map { TelemetryChartData(telemetryData: reading, metricType: $0) }
        }
    }
    
    var body: some View {
        Chart(chartData) { data in
            LineMark(
                x: .value("Time", data.timestamp),
                y: .value("Value", data.value)
            )
            .foregroundStyle(by: .value("Metric", data.metricType.displayName))
            .symbol(by: .value("Metric", data.metricType.displayName))
            .interpolationMethod(.catmullRom)
        }
        .chartXAxis {
            AxisMarks(values: .stride(by: .hour)) { value in
                AxisGridLine()
                AxisTick()
                if let date = value.as(Date.self) {
                    AxisValueLabel {
                        Text(date, style: .time)
                    }
                }
            }
        }
        .chartYAxis {
            AxisMarks(position: .leading)
        }
        .chartLegend(position: .top, alignment: .center, spacing: 20)
        .frame(height: 300)
        .padding()
    }
}

struct TelemetryChartData: Identifiable {
    let id = UUID()
    let timestamp: Date
    let metricType: TelemetryChartView.MetricType
    let value: Double

    init(telemetryData: TelemetryData, metricType: TelemetryChartView.MetricType) {
        self.timestamp = telemetryData.time
        self.metricType = metricType
        switch metricType {
        case .temperature:
            self.value = telemetryData.temperature
        case .humidity:
            self.value = telemetryData.humidity
        case .soilMoisture:
            self.value = telemetryData.soilMoisture
        }
    }
}

struct LegendItem: View {
    let color: Color
    let label: String
    
    var body: some View {
        HStack(spacing: 4) {
            Circle()
                .fill(color)
                .frame(width: 10, height: 10)
            Text(label)
                .font(.system(size: 12))
        }
    }
}
