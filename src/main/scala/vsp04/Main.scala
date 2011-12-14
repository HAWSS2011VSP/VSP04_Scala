package vsp04
import scala.util.Random
import java.util.Timer
import java.util.TimerTask
import scala.actors.Actor

object Main {
  def main(args: Array[String]) {
    if (args.length != 6) {
      println("Usage: <address> <port> <frameLength> <slotCount> <interface> <stationNr>")
    } else {
      val address = args(0)
      val port = args(1).toInt
      val frameLength = args(2).toInt
      val slots = args(3).toInt
      val interface = args(4)
      val stationNr = args(5).toInt

      val receiver = MulticastReceiver(address, port, frameLength, slots, interface)
      val sender = MulticastSender(address, port, frameLength, slots, interface)
      val sink = new DataSink
      sink.start

      val firstFrame = getFirstFrame(receiver)
      sink ! firstFrame
      val initialSlot = firstFrame.freeSlots(Random.nextInt(firstFrame.freeSlots.length))
      println("Initial slot is " + initialSlot + ".")
      sender.slot = initialSlot
      sender ! "foo"
      sender ! "bar"
      sender.start
      startDataSource(sender, stationNr)
      while (true) {
        val frame = receiver.receiveFrame
        sink ! frame
        if (frame.collisionOnSlot(sender.slot.toByte) && !sender.reserved) {
          sender.slot = frame.freeSlots(Random.nextInt(frame.freeSlots.length))
          println("Collision detected!")
        } else if (!sender.reserved) {
          sender.reserved = true
          println("No collision detected, slot reserved.")
        }
      }
    }
  }

  def startDataSource(sender: Actor, stationNr: Int) {
    val timer = new Timer
    timer.scheduleAtFixedRate(new TimerTask {
      def run {
        sender ! "team 22-" + stationNr
      }
    }, 0, 1000)
  }

  def getFirstFrame(receiver: MulticastReceiver): Frame = {
    val frame = receiver.receiveFrame
    if (frame.freeSlots.length > 0) {
      frame
    } else {
      getFirstFrame(receiver)
    }
  }
}