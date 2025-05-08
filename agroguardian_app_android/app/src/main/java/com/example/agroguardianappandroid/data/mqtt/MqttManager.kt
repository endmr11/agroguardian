package com.example.agroguardianappandroid.data.mqtt

import android.content.Context
import android.util.Log
import cloud.deepblue.mqttfix.mqtt.MqttAndroidClient
import com.example.agroguardianappandroid.data.models.Command
import com.example.agroguardianappandroid.data.models.CommandResponse
import com.example.agroguardianappandroid.data.models.Telemetry
import com.example.agroguardianappandroid.data.remote.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.UUID

class MqttManager(
    private val context: Context,
    private val onTelemetryReceived: (Telemetry) -> Unit,
    private val onCommandResponseReceived: (CommandResponse) -> Unit
) {
    private var mqttClient: MqttAndroidClient? = null
    private val serverUri = "tcp://10.0.2.2:1883" // Android emülatörde localhost'a erişim
    private val clientId = "AndroidClient-${UUID.randomUUID().toString().substring(0, 6)}"

    private val telemetryTopic = "agro/devices/device-001/telemetry"
    private val commandsTopic = "agro/devices/device-001/commands"
    private val responsesTopic = "agro/devices/device-001/responses"

    fun connect() {
        mqttClient = MqttAndroidClient(context, serverUri, clientId)
        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.d("MQTT", "Connection lost: ${cause?.message}")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                message?.payload?.let { payload ->
                    val msg = String(payload)
                    when (topic) {
                        telemetryTopic -> {
                            try {
                                val telemetry = Gson().fromJson(msg, Telemetry::class.java)
                                onTelemetryReceived(telemetry)
                            } catch (e: Exception) {
                                Log.e("MQTT", "Telemetry parsing error: ${e.message}")
                            }
                        }
                        responsesTopic -> {
                            try {
                                val response = Gson().fromJson(msg, CommandResponse::class.java)
                                onCommandResponseReceived(response)
                            } catch (e: Exception) {
                                Log.e("MQTT", "Command response parsing error: ${e.message}")
                            }
                        }

                        else -> {}
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("MQTT", "Delivery complete")
            }
        })

        val options = MqttConnectOptions()
        options.isAutomaticReconnect = true;
        options.isCleanSession = false;

        try {
            mqttClient?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT", "Connection success")
                    subscribe(telemetryTopic)
                    subscribe(commandsTopic)
                    subscribe(responsesTopic)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("MQTT", "Connection failure: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            Log.e("MQTT", "MQTT exception: ${e.message}")
        }
    }

    private fun subscribe(topic: String) {
        try {
            mqttClient?.subscribe(topic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTT", "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("MQTT", "Subscribe failed: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            Log.e("MQTT", "MQTT exception: ${e.message}")
        }
    }

    fun sendCommand(command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiService.create().sendCommand("device-001", Command(type = command))
                Log.d("MQTT", "Command sent successfully")
            } catch (e: Exception) {
                Log.e("MQTT", "Error sending command: ${e.message}")
            }
        }
    }

    fun disconnect() {
        try {
            mqttClient?.disconnect()
        } catch (e: MqttException) {
            Log.e("MQTT", "MQTT exception: ${e.message}")
        }
    }
}