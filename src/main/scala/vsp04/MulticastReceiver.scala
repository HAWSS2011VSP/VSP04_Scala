package vsp04
import java.net.{ MulticastSocket, NetworkInterface, InetAddress, DatagramPacket, SocketTimeoutException }

case class MulticastReceiver(address: String, port: Int, frameLength: Int, slots: Int, interface: String) {

  lazy val socket = {
    val s = new MulticastSocket(port)
    s.setNetworkInterface(NetworkInterface.getByName(interface))
    s.joinGroup(InetAddress.getByName(address))
    s.setSoTimeout(frameLength / slots)
    s
  }

  def receiveFrame = {
    waitUntilFrameStarts
    val now = System.currentTimeMillis
    Frame((for (i <- 0 until slots) yield {
      try {
        val buffer = Array.ofDim[Byte](33)
        val packet = new DatagramPacket(buffer, buffer.length)
        socket.receive(packet)
        Option(Slot(packet.getData, i.toByte))
      } catch {
        case e: SocketTimeoutException => {
          None
        }
      }
    }).toList)
  }

  def waitUntilFrameStarts {
    val delay = 1000 - (System.currentTimeMillis % 1000)
    Thread.sleep(delay)
  }
}