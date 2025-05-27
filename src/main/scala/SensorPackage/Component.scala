package SensorPackage

trait Component {
    val componentType: String
    val componentName: String
    var componentValue: Any
    
    def isValidComponent: Boolean =
        componentType.nonEmpty && componentName.nonEmpty && componentValue != null
    def operation(): Unit  // genel işlev, örnek için
}