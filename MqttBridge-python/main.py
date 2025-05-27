import json
from paho.mqtt.client import Client as MQTTClient
from confluent_kafka import Producer

# MQTT Ayarları
MQTT_BROKER = "localhost"
MQTT_PORT = 1883
MQTT_TOPIC = "iot/sensor/data"

# Kafka Ayarları
KAFKA_BROKER = "localhost:9092"
KAFKA_TOPIC = "sensor_data"

# Kafka Producer config
producer_conf = {
    'bootstrap.servers': KAFKA_BROKER,
}

producer = Producer(producer_conf)

def kafka_delivery_report(err, msg):
    if err is not None:
        print(f"Kafkaya mesaj gönderme hatası: {err}")
    else:
        print(f"Kafka mesajı gönderildi: {msg.topic()} [{msg.partition()}] @ offset {msg.offset()}")

def on_connect(client, userdata, flags, rc):
    print(f"MQTT Broker'a bağlandı, sonuç kodu: {rc}")
    client.subscribe(MQTT_TOPIC)
    print(f"{MQTT_TOPIC} topicine abone olundu.")

def on_message(client, userdata, msg):
    try:
        payload = msg.payload.decode('utf-8')
        print(f"MQTT mesajı alındı: {payload}")

        # İstersen payloadu json.loads() ile işleyebilirsin
        # Ama biz direkt Kafka'ya string olarak gönderiyoruz
        producer.produce(KAFKA_TOPIC, value=payload, callback=kafka_delivery_report)
        producer.poll(0)
    except Exception as e:
        print(f"Mesaj işleme hatası: {e}")

def main():
    mqtt_client = MQTTClient()
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_BROKER, MQTT_PORT, 60)
    mqtt_client.loop_forever()

if __name__ == "__main__":
    main()
