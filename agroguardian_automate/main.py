import paho.mqtt.client as mqtt
import time
import json

DEVICE_ID = "device-001"
MQTT_BROKER = "localhost"
MQTT_PORT = 1883
COMMAND_TOPIC = f"agro/devices/{DEVICE_ID}/commands"
RESPONSE_TOPIC = f"agro/devices/{DEVICE_ID}/responses"
IS_PROCESSING = False

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"MQTT connection success and subscribed to {COMMAND_TOPIC}")
        client.subscribe(COMMAND_TOPIC)
    else:
        print(f"MQTT broker connection error! Code: {rc}")

def on_message(client, userdata, msg):
    global IS_PROCESSING

    if msg.topic == COMMAND_TOPIC:
        if IS_PROCESSING:
            return
        try:
            command_payload = msg.payload.decode("utf-8")
            print(f"'{COMMAND_TOPIC}' - {command_payload}")
            IS_PROCESSING = True
            if command_payload == "START_IRRIGATION":
                print("Starting irrigation...")
                response_payload = json.dumps({"status": "IRRIGATION_STARTED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()
                time.sleep(10)
                print("Started irrigation.")
                response_payload = json.dumps({"status": "IRRIGATION_COMPLETED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()
            elif command_payload == "STOP_IRRIGATION":
                print("Stopping irrigation...")
                response_payload = json.dumps({"status": "IRRIGATION_STOPPED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()
            elif command_payload == "START_VENTILATION":
                print("Starting Ventilation...")
                response_payload = json.dumps({"status": "VENTILATION_STARTED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()
                time.sleep(10)
                print("Started Ventilation.")
                response_payload = json.dumps({"status": "VENTILATION_COMPLETED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()
            elif command_payload == "STOP_VENTILATION":
                print("Stopping Ventilation...")
                response_payload = json.dumps({"status": "VENTILATION_STOPPED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()

            elif command_payload == "START_SOIL_VENTILATION":
                print("Starting Soil Ventilation...")
                response_payload = json.dumps({"status": "SOIL_VENTILATION_STARTED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()
                time.sleep(10)
                print("Started Soil Ventilation.")
                response_payload = json.dumps({"status": "SOIL_VENTILATION_COMPLETED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()
            elif command_payload == "STOP_SOIL_VENTILATION":
                print("Stopping Soil Ventilation...")
                response_payload = json.dumps({"status": "SOIL_VENTILATION_STOPPED"})
                client.publish(RESPONSE_TOPIC, response_payload)
                client.loop()
            else:
                print(f"Unknown: {command_payload}")
        except Exception as e:
            print(f"Error: {e}")
        finally:
            IS_PROCESSING = False

client_command = mqtt.Client()
client_command.on_connect = on_connect
client_command.on_message = on_message

try:
    client_command.connect(MQTT_BROKER, MQTT_PORT, 60)
    client_command.loop_forever()

except KeyboardInterrupt:
    print("Stopping MQTT client...")
finally:
    client_command.disconnect()
