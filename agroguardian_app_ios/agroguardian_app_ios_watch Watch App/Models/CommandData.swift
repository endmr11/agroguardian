import Foundation
import SwiftUI

struct CommandData: Decodable, Identifiable {
    let id: Int
    let type: String
    let timestamp: String

    private static let isoFormatter: ISO8601DateFormatter = {
        let formatter = ISO8601DateFormatter()
        return formatter
    }()

    var time: Date {
        CommandData.isoFormatter.date(from: timestamp) ?? Date()
    }
} 
