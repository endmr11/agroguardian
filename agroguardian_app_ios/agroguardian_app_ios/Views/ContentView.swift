//
//  ContentView.swift
//  agroguardian_app_ios
//
//  Created by Eren on 6.05.2025.
//

import SwiftUI
import RiveRuntime


struct ContentView: View {
    @StateObject var mqttManager = MQTTManager()
    @State private var showMessage = false
    @State private var alertMessage = ""
    @State private var showIrrigationAnim = false
    @State private var showVentilationAnim = false
    @State private var windmillView = RiveViewModel(fileName: "windmill")
    @State private var waterView = RiveViewModel(fileName: "water")
    @State private var isProcessing = false
    
    var body: some View {
        NavigationStack{
            VStack(spacing: 20) {
                
                telemetryDataCard(title: "Temperature:",
                               value: mqttManager.telemetry?.temperature,
                               color: mqttManager.telemetry?.temperatureColor() ?? .gray)
                
                telemetryDataCard(title: "Humidity:",
                               value: mqttManager.telemetry?.humidity,
                               color: mqttManager.telemetry?.humidityColor() ?? .gray)
                
                telemetryDataCard(title: "Soil Moisture:",
                               value: mqttManager.telemetry?.soilMoisture,
                               color: mqttManager.telemetry?.soilMoistureColor() ?? .gray)
                HStack{
                    NavigationLink{
                        DetailView()
                    } label: {
                        Text("More...")
                            .font(.title)
                    }
                }
                HStack {
                    if showIrrigationAnim {
                        waterView.view()
                            .frame(width: 100, height: 100)
                            .onAppear { waterView.play() }
                    }
                    
                    if showVentilationAnim {
                        windmillView.view()
                            .frame(width: 100, height: 100)
                            .onAppear { windmillView.play() }
                    }
                }
                
                
                if showMessage {
                    Text(alertMessage)
                        .padding()
                        .background(Color.black.opacity(0.7))
                        .foregroundColor(.white)
                        .cornerRadius(10)
                        .transition(.opacity)
                        .animation(.easeInOut, value: showMessage)
                }
                Spacer()
                controlButtons
            }
            .padding()
            .onAppear {
                mqttManager.connect()
                Task{
                    await getAuthStatus()
                }
            }
            .onReceive(mqttManager.$commandResponse) { message in
                handleCommandResponse(message)
            }
            .onReceive(mqttManager.$telemetry) { telemetry in
                if let telemetry = telemetry {
                    WatchConnectivityManager.shared.sendTelemetryData(telemetry)
                }
            }
            .navigationTitle("Device: device-001")
            .navigationBarTitleDisplayMode(.large)
        }
        
    }
    
    
    
    private var controlButtons: some View {
        HStack(spacing: 10) {
            controlButton(title: "Start Irrigation",
                          action: {
                
                Task {
                    let result = await APIService.shared.sendCommand(to: "device-001", command: Command(type: "START_IRRIGATION"))
                    switch result {
                    case .success:
                        print("Command sent successfully")
                    case .failure(let error):
                        print("Error sending command: \(error)")
                    }
                }
            })
            
            controlButton(title: "Start Ventilation",
                          action: {
                
                Task {
                    let result = await APIService.shared.sendCommand(to: "device-001", command: Command(type: "START_VENTILATION"))
                    switch result {
                    case .success:
                        print("Command sent successfully")
                    case .failure(let error):
                        print("Error sending command: \(error)")
                    }
                }
            })
            
            controlButton(title: "Start Soil Ventilation",
                          action: {
                
                
                Task {
                    let result = await APIService.shared.sendCommand(to: "device-001", command: Command(type: "START_SOIL_VENTILATION"))
                    switch result {
                    case .success:
                        print("Command sent successfully")
                    case .failure(let error):
                        print("Error sending command: \(error)")
                    }
                }
            })
        }
    }
    
    
    private func telemetryDataCard(title: String, value: Double?, color: Color) -> some View {
        ZStack(alignment: .center) {
            RoundedRectangle(cornerRadius: 20)
                .frame(width: 370, height: 80)
                .foregroundColor(color)
            
            HStack {
                Text(title)
                    .foregroundStyle(.white)
                    .font(.title2)
                    .padding(.leading)
                
                Spacer()
                
                Text(formatDouble(value: value))
                    .foregroundStyle(.white)
                    .font(.title2)
                    .padding(.trailing)
            }
        }
    }
    
    private func controlButton(title: String, action: @escaping () -> Void) -> some View {
        Button(action: {
            guard !isProcessing else { return }
            action()
        }) {
            Text(title)
                .frame(minWidth: 100, maxWidth: .infinity, minHeight: 50)
                .font(.system(size: 14, weight: .medium))
        }
        .disabled(isProcessing)
        .buttonStyle(.borderedProminent)
        .cornerRadius(10)
    }
    
    
    private func formatDouble(value: Double?) -> String {
        let formatter = NumberFormatter()
        formatter.minimumFractionDigits = 1
        formatter.maximumFractionDigits = 1
        return formatter.string(from: NSNumber(value: value ?? 0)) ?? "0.0"
    }
    
    
    private func handleCommandResponse(_ message: MqttCommandResponse?) {
        guard let message = message else { return }
        
        showMessage = true
        alertMessage = message.getAlertMessage()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            showMessage = false
        }
        
        if message.isIrrigationStarted() {
            showIrrigationAnim = true
            isProcessing = true
        } else if message.isIrrigationCompleted() || message.isIrrigationStopped() {
            isProcessing = false
            showIrrigationAnim = false
        }
        
        if message.isVentilationStarted() || message.isSoilVentilationStarted() {
            isProcessing = true
            showVentilationAnim = true
        } else if message.isVentilationCompleted() || message.isVentilationStopped() ||
                    message.isSoilVentilationCompleted() || message.isSoilVentilationStopped() {
            isProcessing = false
            showVentilationAnim = false
        }
    }
    
    func request() async{
        do {
            try await UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound])
            await getAuthStatus()
        } catch{
            print(error)
        }
    }
    
    func getAuthStatus() async {
        let _ = await UNUserNotificationCenter.current().notificationSettings()
    }
}


#Preview {
    ContentView()
}


