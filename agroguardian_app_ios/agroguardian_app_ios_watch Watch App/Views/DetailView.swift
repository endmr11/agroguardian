import SwiftUI

struct DetailView: View {
    @ObservedObject var viewModel: WatchViewModel
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            telemetryDataView
                .tag(0)
            
            commandHistoryView
                .tag(1)
        }
        .tabViewStyle(.page)
        .onAppear {
            viewModel.loadTelemetryData()
            viewModel.loadCommandData()
        }
    }
    
    private var telemetryDataView: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10) {
                Text("Telemetry History")
                    .font(.headline)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.top, 5)
                
                if viewModel.telemetryData.isEmpty {
                    ProgressView("Loading data...")
                        .frame(maxWidth: .infinity, alignment: .center)
                        .padding()
                } else {
                    ForEach(viewModel.telemetryData.prefix(5)) { data in
                        telemetryDataRow(data: data)
                    }
                }
            }
            .padding(.horizontal, 10)
        }
    }
    
    private func telemetryDataRow(data: TelemetryData) -> some View {
        VStack(alignment: .leading, spacing: 5) {
            Text(formatTime(date: data.time))
                .font(.caption2)
                .foregroundColor(.gray)
            
            HStack {
                telemetryValue(value: data.temperature, unit: "°C", color: .red)
                telemetryValue(value: data.humidity, unit: "%", color: .blue)
                telemetryValue(value: data.soilMoisture, unit: "%", color: .green)
            }
            
            Divider()
        }
        .padding(.vertical, 2)
    }
    
    private func telemetryValue(value: Double, unit: String, color: Color) -> some View {
        HStack(spacing: 0) {
            Text(String(format: "%.1f", value))
                .font(.system(size: 13, weight: .medium))
            Text(unit)
                .font(.system(size: 10))
        }
        .foregroundColor(color)
        .frame(maxWidth: .infinity)
    }
    
    private var commandHistoryView: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10) {
                Text("Command History")
                    .font(.headline)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.top, 5)
                
                if viewModel.commandData.isEmpty {
                    ProgressView("Loading data...")
                        .frame(maxWidth: .infinity, alignment: .center)
                        .padding()
                } else {
                    ForEach(viewModel.commandData.prefix(8)) { data in
                        HStack {
                            Text(formatTime(date: data.time))
                                .font(.caption2)
                                .foregroundColor(.gray)
                            
                            Spacer()
                            
                            Text(data.type)
                                .font(.system(size: 11))
                                .foregroundColor(.primary)
                        }
                        
                        Divider()
                    }
                }
            }
            .padding(.horizontal, 10)
        }
    }
    
    private func formatTime(date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss"
        return formatter.string(from: date)
    }
}

#Preview {
    DetailView(viewModel: WatchViewModel())
} 
