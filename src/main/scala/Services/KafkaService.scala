package Services

import SensorPackage.{Composite, Sensor}

import java.util.{Collections, Properties}
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.Json

class KafkaService(
                    bootstrapServers: String = "localhost:9092",
                    groupId: String = "scala-consumer-group",
                    topic: String = "iot/sensor/data"
                  ) {

  private val consumer: KafkaConsumer[String, String] = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    new KafkaConsumer[String, String](props)
  }

  private val converter = new ComponentConverter()

  // Ana composite device (örneğin)
  private val mainDevice = Composite("device", "MainDevice")

  def startConsuming(): Unit = {
    consumer.subscribe(Collections.singletonList(topic))
    println(s"KafkaService başladı: '$topic' konusundan veri alınıyor...")

    while (true) {
      val records = consumer.poll(java.time.Duration.ofMillis(100))
      records.forEach { record =>
        println(s"Offset: ${record.offset()}, Key: ${record.key()}, Value: ${record.value()}")

        try {
          val json = Json.parse(record.value())
          val component = converter.fromJson(json)

          component match {
            case sensor: Sensor =>
              mainDevice.add(sensor) // sensör ise direkt ekle
            case composite: Composite =>
              composite.getAll.foreach(mainDevice.add) // composite ise alt elemanlarını ekle
            case _ =>
              println("Bilinmeyen component türü, eklenmedi.")
          }

        } catch {
          case ex: Exception =>
            println(s"Hata: Mesaj işlenemedi: ${ex.getMessage}")
        }
      }
    }
    stopConsuming();
  }

  private def stopConsuming(): Unit = {
    consumer.close()
    println("KafkaService durduruldu.")
  }
  
  // Dışarıdan mainDevice erişimi için
  def getMainDevice: Composite = mainDevice
}
