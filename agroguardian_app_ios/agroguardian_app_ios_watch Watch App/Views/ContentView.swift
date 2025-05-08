//
//  ContentView.swift
//  agroguardian_app_ios_watch Watch App
//
//  Created by Eren on 7.05.2025.
//

import SwiftUI
import WatchConnectivity

struct ContentView: View {
    @StateObject private var viewModel = WatchViewModel()
    @State private var showMessage = false
    @State private var alertMessage = ""
    @State private var showDetailView = false
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 15) {
                    telemetryDataGauge(
                        title: "Temperature",
                        value: viewModel.telemetry?.temperature ?? 0,
                        minValue: 0, maxValue: 40,
                        color: viewModel.telemetry?.temperatureColor() ?? .gray
                    )
                    
                    telemetryDataGauge(
                        title: "Humidity",
                        value: viewModel.telemetry?.humidity ?? 0,
                        minValue: 0, maxValue: 100,
                        color: viewModel.telemetry?.humidityColor() ?? .gray
                    )
                    
                    telemetryDataGauge(
                        title: "Soil Moisture",
                        value: viewModel.telemetry?.soilMoisture ?? 0, 
                        minValue: 0, maxValue: 100,
                        color: viewModel.telemetry?.soilMoistureColor() ?? .gray
                    )
                    
                    NavigationLink(destination: DetailView(viewModel: viewModel)) {
                        Text("History")
                            .font(.headline)
                            .frame(maxWidth: .infinity)
                    }
                    .buttonStyle(.borderedProminent)
                    .padding(.top, 5)
                    
                    controlButtons
                    
                    if showMessage {
                        Text(alertMessage)
                            .font(.footnote)
                            .padding(8)
                            .background(Color.black.opacity(0.7))
                            .foregroundColor(.white)
                            .cornerRadius(8)
                            .transition(.opacity)
                            .animation(.easeInOut, value: showMessage)
                    }
                }
                .padding(.horizontal, 5)
            }
            .navigationTitle("AgroGuardian")
            .onAppear {
                viewModel.connect()
            }
            .onReceive(viewModel.$commandResponse) { message in
                if let message = message {
                    showMessage = true
                    alertMessage = message.getAlertMessage()
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                        showMessage = false
                    }
                }
            }
        }
    }
    
    private var controlButtons: some View {
        VStack(spacing: 8) {
            Button(action: {
                viewModel.sendCommand(type: "START_IRRIGATION")
            }) {
                Text("Start Irrigation")
                    .font(.footnote)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.blue)
            
            Button(action: {
                viewModel.sendCommand(type: "START_VENTILATION")
            }) {
                Text("Start Ventilation")
                    .font(.footnote)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.green)
            
            Button(action: {
                viewModel.sendCommand(type: "START_SOIL_VENTILATION")
            }) {
                Text("Soil Ventilation")
                    .font(.footnote)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.orange)
        }
        .disabled(viewModel.isProcessing)
    }
        
    private func telemetryDataGauge(title: String, value: Double, minValue: Double, maxValue: Double, color: Color) -> some View {
        VStack(spacing: 2) {
            Text(title)
                .font(.caption)
            
            Gauge(value: value, in: minValue...maxValue) {
                // Empty label
            } currentValueLabel: {
                Text(formatDouble(value: value))
                    .font(.system(size: 12, weight: .semibold))
            }
            .gaugeStyle(.accessoryCircular)
            .tint(color)
            .scaleEffect(0.9)
        }
    }
        
    private func formatDouble(value: Double?) -> String {
        let formatter = NumberFormatter()
        formatter.minimumFractionDigits = 1
        formatter.maximumFractionDigits = 1
        return formatter.string(from: NSNumber(value: value ?? 0)) ?? "0.0"
    }
}

#Preview {
    ContentView()
}
