import json
import time
import random
import paho.mqtt.client as mqtt

MQTT_BROKER = "localhost"  # Docker'dan publish edilen port kullanılacak
MQTT_PORT = 1883
MQTT_TOPIC = "iot/sensor/data"

def generate_sensor_data():
    return {
        "device": "weather_station_1",
        "components": [
            {
                "type": "sensor",
                "name": "temperature",
                "value": round(random.uniform(20.0, 30.0), 2)
            },
            {
                "type": "group",
                "name": "environment",
                "components": [
                    {
                        "type": "sensor",
                        "name": "humidity",
                        "value": round(random.uniform(40.0, 60.0), 2)
                    },
                    {
                        "type": "sensor",
                        "name": "pressure",
                        "value": round(random.uniform(1000, 1025), 2)
                    }
                ]
            }
        ]
    }

def on_connect(client, userdata, flags, reason_code, properties=None):
    print("MQTT bağlantısı başarılı, reason code:", reason_code)

def main():
    # Yeni API kullanım
    client = mqtt.Client(callback_api_version=mqtt.CallbackAPIVersion.VERSION2)
    client.on_connect = on_connect

    print(f"Broker'a bağlanılıyor: {MQTT_BROKER}:{MQTT_PORT}...")
    client.connect(MQTT_BROKER, MQTT_PORT, keepalive=60)
    client.loop_start()

    try:
        while True:
            payload = json.dumps(generate_sensor_data())
            client.publish(MQTT_TOPIC, payload)
            print(f"Published: {payload}")
            time.sleep(5)
    except KeyboardInterrupt:
        print("Durduruluyor...")
    finally:
        client.loop_stop()
        client.disconnect()

if __name__ == "__main__":
    main()
