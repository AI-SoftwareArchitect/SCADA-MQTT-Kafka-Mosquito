package SensorPackage

import SensorPackage.DeviceState.Maintenance

class Device(
              override val componentType: String = "Device",
              override val componentName: String
            ) extends Composite(componentType, componentName) {

      private var state: DeviceState = DeviceState.On

      def turnOn(): Unit = state = DeviceState.On

      def turnOff(): Unit = state = DeviceState.Off

      def turnMaintenance(): Unit = state = Maintenance
  
}