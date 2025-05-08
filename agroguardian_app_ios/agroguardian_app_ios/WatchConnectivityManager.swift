import Foundation
import WatchConnectivity

class WatchConnectivityManager: NSObject, ObservableObject {
    static let shared = WatchConnectivityManager()
    private var session: WCSession?
    
    @Published var lastMessage: [String: Any] = [:]
    
    override init() {
        super.init()
        setupWCSession()
    }
    
    private func setupWCSession() {
        if WCSession.isSupported() {
            session = WCSession.default
            session?.delegate = self
            session?.activate()
        }
    }
    
    func sendTelemetryData(_ telemetry: Telemetry) {
        guard let session = session, session.activationState == .activated else { return }
        
        do {
            let data = try JSONEncoder().encode(telemetry)
            let message = ["telemetry": data]
            session.sendMessage(message, replyHandler: nil) { error in
                print("Error sending telemetry to Watch: \(error.localizedDescription)")
            }
        } catch {
            print("Failed to encode telemetry: \(error)")
        }
    }
    
    func sendTelemetryHistory(_ telemetryData: [TelemetryData]) {
        guard let session = session, session.activationState == .activated else { return }
        
        do {
            let data = try JSONEncoder().encode(telemetryData)
            let message = ["telemetryHistory": data]
            session.sendMessage(message, replyHandler: nil) { error in
                print("Error sending telemetry history to Watch: \(error.localizedDescription)")
            }
        } catch {
            print("Failed to encode telemetry history: \(error)")
        }
    }
    
    func sendCommandHistory(_ commandData: [CommandData]) {
        guard let session = session, session.activationState == .activated else { return }
        
        do {
            let data = try JSONEncoder().encode(commandData)
            let message = ["commandHistory": data]
            session.sendMessage(message, replyHandler: nil) { error in
                print("Error sending command history to Watch: \(error.localizedDescription)")
            }
        } catch {
            print("Failed to encode command history: \(error)")
        }
    }
    
    func sendMessage(_ message: [String: Any]) {
        guard let session = session, session.activationState == .activated else { return }
        
        session.sendMessage(message, replyHandler: nil) { error in
            print("Error sending message to Watch: \(error.localizedDescription)")
        }
    }
}

extension WatchConnectivityManager: WCSessionDelegate {
    func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        print("WCSession activation completed with state: \(activationState.rawValue)")
        if let error = error {
            print("WCSession activation failed with error: \(error.localizedDescription)")
        }
    }
    
    func sessionDidBecomeInactive(_ session: WCSession) {
        print("WCSession became inactive")
    }
    
    func sessionDidDeactivate(_ session: WCSession) {
        print("WCSession deactivated")
        WCSession.default.activate()
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String: Any]) {
        DispatchQueue.main.async {
            self.lastMessage = message
            self.handleWatchMessage(message)
        }
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String: Any], replyHandler: @escaping ([String: Any]) -> Void) {
        DispatchQueue.main.async {
            self.lastMessage = message
            self.handleWatchMessage(message)
            replyHandler(["response": "received"])
        }
    }
    
    private func handleWatchMessage(_ message: [String: Any]) {
        if let request = message["request"] as? String, let deviceId = message["deviceId"] as? String {
            switch request {
            case "telemetry":
                fetchAndSendTelemetry(for: deviceId)
            case "telemetryHistory":
                fetchAndSendTelemetryHistory(for: deviceId)
            case "commandHistory":
                fetchAndSendCommandHistory(for: deviceId)
            default:
                break
            }
        } else if let command = message["command"] as? String, let deviceId = message["deviceId"] as? String {
            sendCommand(type: command, deviceId: deviceId)
        }
    }
    
    private func fetchAndSendTelemetry(for deviceId: String) {
        Task {
            let result = await APIService.shared.fetchTelemetry(for: deviceId)
            switch result {
            case .success(let telemetryArray):
                if let lastTelemetry = telemetryArray.last {
                    do {
                        let data = try JSONEncoder().encode(lastTelemetry)
                        session?.sendMessage(["telemetry": data], replyHandler: nil, errorHandler: { error in
                            print("Error sending telemetry to Watch: \(error.localizedDescription)")
                        })
                    } catch {
                        print("Failed to encode telemetry: \(error)")
                    }
                }
            case .failure(let error):
                print("Error fetching telemetry: \(error)")
            }
        }
    }
    
    private func fetchAndSendTelemetryHistory(for deviceId: String) {
        Task {
            let result = await APIService.shared.fetchTelemetry(for: deviceId)
            switch result {
            case .success(let telemetryArray):
                do {
                    let data = try JSONEncoder().encode(telemetryArray)
                    session?.sendMessage(["telemetryHistory": data], replyHandler: nil, errorHandler: { error in
                        print("Error sending telemetry history to Watch: \(error.localizedDescription)")
                    })
                } catch {
                    print("Failed to encode telemetry history: \(error)")
                }
            case .failure(let error):
                print("Error fetching telemetry history: \(error)")
            }
        }
    }
    
    private func fetchAndSendCommandHistory(for deviceId: String) {
        Task {
            let result = await APIService.shared.fetchLastCommands(for: deviceId)
            switch result {
            case .success(let commands):
                do {
                    let data = try JSONEncoder().encode(commands)
                    session?.sendMessage(["commandHistory": data], replyHandler: nil, errorHandler: { error in
                        print("Error sending command history to Watch: \(error.localizedDescription)")
                    })
                } catch {
                    print("Failed to encode command history: \(error)")
                }
            case .failure(let error):
                print("Error fetching command history: \(error)")
            }
        }
    }
    
    private func sendCommand(type: String, deviceId: String) {
        Task {
            let command = Command(type: type)
            let result = await APIService.shared.sendCommand(to: deviceId, command: command)
            switch result {
            case .success:
                session?.sendMessage(["commandResponse": ["status": "success", "command": type]], replyHandler: nil, errorHandler: { error in
                    print("Error sending command response to Watch: \(error.localizedDescription)")
                })
            case .failure(let error):
                print("Error sending command: \(error)")
                session?.sendMessage(["commandResponse": ["status": "error", "message": error.localizedDescription]], replyHandler: nil, errorHandler: { error in
                    print("Error sending command error to Watch: \(error.localizedDescription)")
                })
            }
        }
    }
} 
