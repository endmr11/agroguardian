//
//  MQTTManager.swift
//  agroguardian_app_ios
//
//  Created by Eren on 6.05.2025.
//

import CocoaMQTT
import Foundation

class MQTTManager: NSObject, ObservableObject {
    private var mqtt: CocoaMQTT?
    @Published var telemetry: Telemetry?
    @Published var commandResponse: MqttCommandResponse?
    
    var telemetryTopic:String = "agro/devices/device-001/telemetry"
    var commandsTopic:String = "agro/devices/device-001/commands"
    var responsesTopic:String = "agro/devices/device-001/responses"
    
    func connect() {
        let clientID = "SwiftUIClient-\(UUID().uuidString.prefix(6))"
        mqtt = CocoaMQTT(clientID: clientID, host: "localhost", port: 1883)
        mqtt?.delegate = self
        var _ = mqtt?.connect()
    }
    
    func subscribe(topic: String) {
        mqtt?.subscribe(topic)
    }
    
    func sendCommand(command: String) {
        Task {
            let result = await APIService.shared.sendCommand(to: "device-001", command: Command(type: command))
            
            if case .success = result {
                let response = MqttCommandResponse(status: command)
                do {
                    let data = try JSONEncoder().encode(response)
                    WatchConnectivityManager.shared.sendMessage(["commandResponse": data])
                } catch {
                    print("Failed to encode command response for Watch: \(error)")
                }
            }
            
            switch result {
            case .success:
                print("Command sent successfully")
            case .failure(let error):
                print("Error sending command: \(error)")
            }
        }
    }
    
    func disconnect() {
        mqtt?.disconnect()
    }
}

extension MQTTManager: CocoaMQTTDelegate {
    func mqtt(_ mqtt: CocoaMQTT, didConnectAck ack: CocoaMQTTConnAck) {
        subscribe(topic: telemetryTopic)
        subscribe(topic: commandsTopic)
        subscribe(topic: responsesTopic)
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishMessage message: CocoaMQTTMessage, id: UInt16) {
        
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishAck id: UInt16) {
        
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didReceiveMessage message: CocoaMQTTMessage, id: UInt16) {
        let msg = String(bytes: message.payload, encoding: .utf8) ?? "Invalid"
        DispatchQueue.main.async {
            if message.topic == "agro/devices/device-001/telemetry" {
                if let jsonData = msg.data(using: .utf8) {
                    do {
                        self.telemetry = try JSONDecoder().decode(Telemetry.self, from: jsonData)
                    } catch {
                        print("Decoding error: \(error)")
                    }
                }
            }
            if message.topic == "agro/devices/device-001/responses" {
                print(msg)
                if let jsonData = msg.data(using: .utf8) {
                    do {
                        self.commandResponse = try JSONDecoder().decode(MqttCommandResponse.self, from: jsonData)
                        
                        do {
                            let data = try JSONEncoder().encode(self.commandResponse)
                            WatchConnectivityManager.shared.sendMessage(["commandResponse": data])
                        } catch {
                            print("Failed to encode command response for Watch: \(error)")
                        }
                    } catch {
                        print("Decoding error: \(error)")
                    }
                }
            }
        }
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopics success: NSDictionary, failed: [String]) {
        
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didUnsubscribeTopics topics: [String]) {
        
    }
    
    func mqttDidPing(_ mqtt: CocoaMQTT) {
        
    }
    
    func mqttDidReceivePong(_ mqtt: CocoaMQTT) {
        
    }
    
    func mqttDidDisconnect(_ mqtt: CocoaMQTT, withError err: (any Error)?) {
        print("Disconnected")
    }
}
