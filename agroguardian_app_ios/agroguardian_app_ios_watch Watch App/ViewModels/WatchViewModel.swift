import Foundation
import WatchConnectivity

class WatchViewModel: NSObject, ObservableObject {
    private var session: WCSession?
    
    @Published var telemetry: Telemetry?
    @Published var commandResponse: MqttCommandResponse?
    @Published var isProcessing = false
    
    @Published var telemetryData: [TelemetryData] = []
    @Published var commandData: [CommandData] = []
    
    let deviceId = "device-001"
    
    override init() {
        super.init()
        setupWCSession()
    }
    
    private func setupWCSession() {
        if WCSession.isSupported() {
            self.session = WCSession.default
            WCSession.default.delegate = self
            WCSession.default.activate()
        }
    }
    
    func connect() {
        requestDataFromPhone()
    }
    
    func requestDataFromPhone() {
        let message = ["request": "telemetry", "deviceId": deviceId]
        session?.sendMessage(message, replyHandler: nil, errorHandler: { error in
            print("Error sending message: \(error.localizedDescription)")
        })
    }
    
    func sendCommand(type: String) {
        guard !isProcessing else { return }
        
        isProcessing = true
        
        let message = [
            "command": type,
            "deviceId": deviceId
        ]
        
        session?.sendMessage(message, replyHandler: { reply in
            print("Command reply: \(reply)")
        }, errorHandler: { error in
            print("Error sending command: \(error.localizedDescription)")
            DispatchQueue.main.async {
                self.isProcessing = false
            }
        })
    }
    
    func loadTelemetryData() {
        let message = ["request": "telemetryHistory", "deviceId": deviceId]
        session?.sendMessage(message, replyHandler: nil, errorHandler: { error in
            print("Error requesting telemetry history: \(error.localizedDescription)")
        })
    }
    
    func loadCommandData() {
        let message = ["request": "commandHistory", "deviceId": deviceId]
        session?.sendMessage(message, replyHandler: nil, errorHandler: { error in
            print("Error requesting command history: \(error.localizedDescription)")
        })
    }
}

extension WatchViewModel: WCSessionDelegate {
    func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        if let error = error {
            print("WCSession activation failed: \(error.localizedDescription)")
        } else {
            print("WCSession activated with state: \(activationState.rawValue)")
            requestDataFromPhone()
        }
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String : Any]) {
        DispatchQueue.main.async {
            if let telemetryData = message["telemetry"] as? Data {
                if let telemetry = try? JSONDecoder().decode(Telemetry.self, from: telemetryData) {
                    self.telemetry = telemetry
                }
            }
            
            if let commandResponseData = message["commandResponse"] as? Data {
                if let response = try? JSONDecoder().decode(MqttCommandResponse.self, from: commandResponseData) {
                    self.commandResponse = response
                    if response.isIrrigationCompleted() || response.isIrrigationStopped() ||
                       response.isVentilationCompleted() || response.isVentilationStopped() ||
                       response.isSoilVentilationCompleted() || response.isSoilVentilationStopped() {
                        self.isProcessing = false
                    }
                }
            }
            
            if let telemetryHistoryData = message["telemetryHistory"] as? Data {
                if let history = try? JSONDecoder().decode([TelemetryData].self, from: telemetryHistoryData) {
                    self.telemetryData = history.sorted { $0.time < $1.time }
                }
            }
            
            if let commandHistoryData = message["commandHistory"] as? Data {
                if let history = try? JSONDecoder().decode([CommandData].self, from: commandHistoryData) {
                    self.commandData = history.sorted { $0.time < $1.time }
                }
            }
        }
    }
} 
