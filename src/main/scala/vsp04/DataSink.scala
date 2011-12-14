package vsp04
import scala.actors.Actor

class DataSink extends Actor {

  def act {
    loop {
      receive {
        case frame: Frame => {
          frame.slots.foreach {
            case Some(slot) =>
              println(slot.msg + "(" + slot.timestamp + ", next slot: " + slot.slot + ") at " + slot.receivedAt)
            case _ =>
          }
        }
        case _ =>
      }
    }
  }

}