package vsp04
import scala.actors.Actor

class DataSink extends Actor {

  def act {
    loop {
      receive {
        case frame: Frame => {
          frame.slots.foreach { slot =>
              println(slot.msg + "(" + slot.timestamp + ", next slot: "
                + slot.slot + ") at " + slot.receivedAt + " on slot "
                + slot.receivedSlot)
          }
        }
        case _ =>
      }
    }
  }

}
