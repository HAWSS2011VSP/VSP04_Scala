package vsp04
import java.net.{ MulticastSocket, NetworkInterface, InetAddress, DatagramPacket, SocketTimeoutException }

case class MulticastReceiver(address: String, port: Int, frameLength: Int, slots: Int, interface: String) {

  val slotSize = frameLength / slots

  lazy val socket = {
    val s = new MulticastSocket(port)
    s.setNetworkInterface(NetworkInterface.getByName(interface))
    s.joinGroup(InetAddress.getByName(address))
    s.setSoTimeout(frameLength)
    s
  }

  def receiveFrame = {
    Frame(receiveSlots(System.currentTimeMillis))
  }

  def receiveSlots(start: Long, result: List[Slot] = List[Slot]()): List[Slot] = {
    if(System.currentTimeMillis - start >= 1000) {
      result
    } else {
      val frameTimeLeft = 1000 - (System.currentTimeMillis % 1000)
      socket.setSoTimeout(frameTimeLeft.toInt)
      val buffer = Array.ofDim[Byte](33)
      val packet = new DatagramPacket(buffer,buffer.length)
      try {
        socket.receive(packet)
        receiveSlots(start, Slot(packet.getData,
          ((System.currentTimeMillis % frameLength) / slotSize).toByte) :: result)
      } catch {
        case ex: SocketTimeoutException => result
      }
    }
  }

  def waitUntilNextSlot {
    val delay = math.max(0, slotSize - (System.currentTimeMillis % slotSize))
    println("Waiting for next slot " + delay + " at " + System.currentTimeMillis)
    Thread.sleep(delay)
  }
}
