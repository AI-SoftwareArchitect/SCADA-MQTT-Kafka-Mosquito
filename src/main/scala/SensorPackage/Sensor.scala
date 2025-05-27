package SensorPackage

class Sensor(
              val componentType: String,
              val componentName: String,
              var componentValue: Any
            ) extends Component {
  override def isValidComponent: Boolean =
    super.isValidComponent
  def operation(): Unit = println(s"Sensor $componentName value: $componentValue")
}
