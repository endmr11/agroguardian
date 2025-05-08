//
//  CommandData.swift
//  agroguardian_app_ios
//
//  Created by Eren on 8.05.2025.
//


import Foundation

struct CommandData:Encodable,Decodable,Identifiable {
    let id:Int
    let type:String
    let timestamp: String

    private static let isoFormatter: ISO8601DateFormatter = {
        let formatter = ISO8601DateFormatter()
        return formatter
    }()

    var time: Date {
        CommandData.isoFormatter.date(from: timestamp) ?? Date()
    }
}