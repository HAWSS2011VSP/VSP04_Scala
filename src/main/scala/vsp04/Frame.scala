package vsp04

case class Frame(slots: List[Slot]) {
  def collisionOnSlot(slot: Byte) = {
    slots.filter(_.receivedSlot == slot).size > 1
  }

  lazy val usedSlots = {
    slots.map(_.slot)
  }
  
  def freeSlots(length: Int) = {
    (0 until length).toList.diff(usedSlots)
  }
}
