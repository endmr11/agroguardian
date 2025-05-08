//
//  TelemetryData.swift
//  agroguardian_app_ios
//
//  Created by Eren on 8.05.2025.
//


import Foundation

struct TelemetryData:Encodable, Decodable,Identifiable {
    var id: String { timestamp }
    let deviceName: String
    let temperature: Double
    let humidity: Double
    let soilMoisture: Double
    let timestamp: String

    private static let isoFormatter: ISO8601DateFormatter = {
        let formatter = ISO8601DateFormatter()
        return formatter
    }()

    var time: Date {
        TelemetryData.isoFormatter.date(from: timestamp) ?? Date()
    }
}
