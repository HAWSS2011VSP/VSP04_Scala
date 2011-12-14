package vsp04

case class Frame(slots: List[Option[Slot]]) {
  def collisionOnSlot(slot: Byte) = {
    slots.filter(_.map(_.receivedSlot == slot).getOrElse(false)).size > 1
  }

  lazy val freeSlots = {
    def freeSlots(slots: List[Option[_]], result: List[Int] = List[Int](), count: Int = 0): List[Int] = {
      if (slots.isEmpty) {
        result
      } else if (slots.head.isEmpty) {
        freeSlots(slots.tail, count :: result, count + 1)
      } else {
        freeSlots(slots.tail, result, count + 1)
      }
    }
    freeSlots(slots)
  }
}
