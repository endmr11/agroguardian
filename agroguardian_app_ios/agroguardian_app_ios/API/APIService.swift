//
//  APIService.swift
//  agroguardian_app_ios
//
//  Created by Eren on 7.05.2025.
//

import Foundation

enum APIError: Error {
    case invalidURL
    case requestFailed(Error)
    case invalidResponse
    case decodingError(Error)
}

class APIService {
    static let shared = APIService()
    private let baseURL = "http://127.0.0.1:8080/api/devices"
    
    private init() {}
    
    private func performRequest<T: Decodable>(path: String) async -> Result<T, APIError> {
        guard let url = URL(string: "\(baseURL)\(path)") else {
            return .failure(.invalidURL)
        }
        do {
            let (data, response) = try await URLSession.shared.data(from: url)
            guard let httpResponse = response as? HTTPURLResponse,
                  (200...299).contains(httpResponse.statusCode) else {
                return .failure(.invalidResponse)
            }
            let decoded = try JSONDecoder().decode(T.self, from: data)
            return .success(decoded)
        } catch let decodingError as DecodingError {
            return .failure(.decodingError(decodingError))
        } catch {
            return .failure(.requestFailed(error))
        }
    }
    
    func fetchTelemetry(for deviceName: String) async -> Result<[TelemetryData], APIError> {
        return await performRequest(path: "/\(deviceName)/telemetry")
    }
    
    func fetchLastCommands(for deviceName: String) async -> Result<[CommandData], APIError> {
        return await performRequest(path: "/\(deviceName)/commands")
    }
    
    func sendCommand(to deviceName: String, command: Command) async -> Result<Void, APIError> {
        guard let url = URL(string: "\(baseURL)/\(deviceName)/commands") else {
            return .failure(.invalidURL)
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        do {
            request.httpBody = try JSONEncoder().encode(command)
            _ = try await URLSession.shared.data(for: request)
            return .success(())
        } catch {
            return .failure(.requestFailed(error))
        }
    }
}
