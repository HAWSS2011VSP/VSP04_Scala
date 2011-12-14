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
      sender.start

      val delay = 1000 - (System.currentTimeMillis % 1000)
      Thread.sleep(delay)
      
      val firstFrame = getFirstFrame(receiver, slots)
      sink ! firstFrame
      val freeSlots = firstFrame.freeSlots(slots)
      val initialSlot = freeSlots(Random.nextInt(freeSlots.length))
      println("Initial slot is " + initialSlot + ".")
      sender.slot = initialSlot
      sender ! "foo"
      sender ! "bar"
      startDataSource(sender, stationNr)
      while (true) {
        val frame = receiver.receiveFrame
        sink ! frame
        if (frame.collisionOnSlot(sender.slot.toByte) && !sender.reserved) {
          val freeSlots = frame.freeSlots(slots)
          sender.slot = freeSlots(Random.nextInt(freeSlots.length))
          println("Collision detected!")
        } else if (!sender.reserved) {
          sender.reserved = true
          println("No collision detected, slot reserved.")
        }
      }
    }
  }

  def startDataSource(senderActor: Actor, stationNr: Int) {
    import Actor._
    actor {
      while(true) {
        senderActor ! "team 22-" + stationNr
        Thread.sleep(1000)
      }
    }.start
  }

  def getFirstFrame(receiver: MulticastReceiver, slots: Int): Frame = {
    val frame = receiver.receiveFrame
    if (frame.freeSlots(slots).length > 0) {
      frame
    } else {
      getFirstFrame(receiver, slots)
    }
  }
}
