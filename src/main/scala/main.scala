package myscada_app

import Services.KafkaService

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@main
def main(): Unit = {
  val kafkaService = new KafkaService()
  kafkaService.startConsuming();
  val mainDevice = kafkaService.getMainDevice;
  println("Server listening...");
}