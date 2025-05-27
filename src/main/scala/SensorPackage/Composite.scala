package SensorPackage

class Composite(
                 val componentType: String,
                 val componentName: String
               ) extends Component {

    private var children: List[Component] = List()
    var componentValue: Any = 0  // Composite için spesifik değer tutabilir ya da default olabilir

    def add(component: Component): Unit = {
        children = children :+ component
    }
    
    def remove(component: Component): Unit = {
        children = children.filterNot(_ == component)
    }

    def getAll: List[Component] = children

    def getByName(name: String): Option[Component] = children.find(_.componentName == name)

    def update(name: String, newValue: Any): Boolean = {
        getByName(name) match {
            case Some(comp) =>
                comp.componentValue = newValue
                true
            case None => false
        }
    }

    override def isValidComponent: Boolean =
        componentType.nonEmpty && componentName.nonEmpty

    def operation(): Unit = {
        println(s"Composite $componentName çalışıyor.")
        children.foreach(_.operation())
    }
}
