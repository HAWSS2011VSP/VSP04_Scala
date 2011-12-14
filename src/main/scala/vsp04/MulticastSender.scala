package vsp04
import scala.actors.Actor

import java.net.{ MulticastSocket, NetworkInterface, InetAddress, DatagramPacket, SocketTimeoutException }

case class MulticastSender(address: String, port: Int, frameLength: Int, slots: Int, interface: String) extends Actor {

  @volatile var slot: Int = -1
  @volatile var reserved = false

  private val slotLength = (frameLength / slots).toInt
  private val sock = new MulticastSocket

  def act {
    loop {
      receive {
        case s: String => {
          val delay = math.max(0, (slotLength * slot + (slotLength / 2).toInt) - (System.currentTimeMillis % 1000))
          Thread.sleep(delay)
          val msg = new Slot(s, slot.toByte, System.currentTimeMillis)
          sock.send(new DatagramPacket(msg.getBytes, 33, InetAddress.getByName(address), port))
          val timeToNextFrame = math.max(0, 1000 - (System.currentTimeMillis % 1000))
          Thread.sleep(timeToNextFrame)
        }
        case _ =>
      }
    }
  }
}
