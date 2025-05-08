//
//  DetailView.swift
//  agroguardian_app_ios
//
//  Created by Eren on 7.05.2025.
//

import SwiftUI

struct DetailView: View {
    
    @State var telemetryData: [TelemetryData] = []
    @State var commandData: [CommandData] = []
    
    private let maxDataPoints = 50
    private let chartHeight: CGFloat = 300
    
    var body: some View {
        VStack{
            if telemetryData.isEmpty {
                ProgressView("Loading data...")
                    .frame(height: chartHeight)
            } else {
                TelemetryChartView(telemetryData: downsampledData)
                    .frame(height: chartHeight)
                    .padding(.vertical)
                Text("Last 3 Data Points")
                    .font(.headline)
                VStack(alignment: .leading,spacing:20) {
                    
                    ForEach(telemetryData.prefix(3)) { data in
                        HStack {
                            Text(data.time, style: .time)
                            Text("\(data.temperature, specifier: "%.1f")°C")
                                .foregroundColor(.red)
                            Text("\(data.humidity, specifier: "%.0f")%")
                                .foregroundColor(.blue)
                            Text("\(data.soilMoisture, specifier: "%.0f")%")
                                .foregroundColor(.green)
                        }
                        .font(.caption)
                    }
                }
                .padding()
                Text("Last 3 Commands")
                    .font(.headline)
                VStack(alignment: .leading,spacing:20){
                    
                    ForEach(commandData.prefix(3)) { data in
                        HStack(alignment: .center,spacing: 20 ){
                            Text(data.time,style: .time)
                            Text(data.type)
                                .foregroundColor(.secondary)
                        }
                        .font(.caption)
                        
                    }
                }
                .padding()
            }
            
            
        }
        .onAppear{
            loadInitialData()
        }
    }
    
    private var downsampledData: [TelemetryData] {
        guard telemetryData.count > maxDataPoints else { return telemetryData }
        
        let step = telemetryData.count / maxDataPoints
        return stride(from: 0, to: telemetryData.count, by: step).map {
            telemetryData[$0]
        }
    }
    
    private func loadInitialData() {
        Task {
            let fetchTelemetryRes = await APIService.shared.fetchTelemetry(for: "device-001")
            switch fetchTelemetryRes {
            case .success(let telemetry):
                self.telemetryData = telemetry.sorted { $0.time < $1.time }
                
                WatchConnectivityManager.shared.sendTelemetryHistory(self.telemetryData)
            case .failure(let error):
                print("Error fetching telemetry: \(error)")
            }
            
            let fetchCommandsRes = await APIService.shared.fetchLastCommands(for: "device-001")
            switch fetchCommandsRes {
            case .success(let commands):
                self.commandData = commands.sorted { $0.time < $1.time }
                
                WatchConnectivityManager.shared.sendCommandHistory(self.commandData)
            case .failure(let error):
                print("Error fetching commands: \(error)")
            }
        }
    }
}

#Preview {
    DetailView()
}

