import paho.mqtt.client as mqtt
import time
import random
import json
from datetime import datetime

DEVICE_NAME = "device-001"
MQTT_BROKER = "localhost"
MQTT_PORT = 1883
TELEMETRY_TOPIC = f"agro/devices/{DEVICE_NAME}/telemetry"

def generate_sensor_data():
    temperature = round(random.uniform(15, 35), 2)
    humidity = round(random.uniform(40, 85), 2)
    soil_moisture = round(random.uniform(0, 70), 2)
    timestamp = datetime.now().isoformat()
    return {
        "device_name": DEVICE_NAME,
        "temperature": temperature,
        "humidity": humidity,
        "soil_moisture": soil_moisture,
        "timestamp": timestamp
    }

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"MQTT broker connected.")
    else:
        print(f"MQTT broker connection error. Code: {rc}")

client_telemetry = mqtt.Client()
client_telemetry.on_connect = on_connect

try:
    client_telemetry.connect(MQTT_BROKER, MQTT_PORT, 60)
    client_telemetry.loop_start()

    while True:
        payload = json.dumps(generate_sensor_data())
        client_telemetry.publish(TELEMETRY_TOPIC, payload)
        print(f"Sending.. Topic: {TELEMETRY_TOPIC} Payload: {payload}")
        time.sleep(10)

except KeyboardInterrupt:
    print("Telemetri simülasyonu durduruldu.")
finally:
    client_telemetry.loop_stop()
    client_telemetry.disconnect()