package Services

import SensorPackage.*
import play.api.libs.json.*
import play.api.libs.functional.syntax.*

class ComponentConverter {
  implicit val sensorReads: Reads[Sensor] = (
    (JsPath \ "type").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "value").readNullable[String].map(_.orNull)
    )(Sensor.apply _)

  def fromJson(json: JsValue): Component = {
    val cType = (json \ "type").as[String]
    val cName = (json \ "name").as[String]

    cType match {
      case "sensor" =>
        val value = (json \ "value").asOpt[String].orNull
        Sensor(cType, cName, value)

      case "group" | "device" =>
        val childrenJson = (json \ "components").asOpt[JsArray].getOrElse(Json.arr())
        val children = childrenJson.value.map(fromJson).toList
        val comp = Composite(cType, cName)
        children.foreach(comp.add)
        comp

      case _ =>
        throw new RuntimeException(s"Unknown component type: $cType")
    }
  }
}
