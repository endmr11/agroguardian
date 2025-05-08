package org.example.agroguardian_backend.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import org.example.agroguardian_backend.device.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FirebaseService {
    @Autowired
    private DeviceService deviceService;

    @PostConstruct
    public void init() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("firebase-service-key.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    public void sendNotification(String deviceId, String message) {
        String deviceToken = deviceService.getDeviceByName(deviceId).getToken();
        Message firebaseMessage = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("Uyarı")
                        .setBody(message)
                        .build()
                )
                .setToken(deviceToken)
                .build();

        try {
            FirebaseMessaging.getInstance().send(firebaseMessage);
            System.out.println("Notification sent successfully to device: " + deviceId);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}

