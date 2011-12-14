package vsp04

class Slot(val msg: String, val slot: Byte, val timestamp: Long, val receivedAt: Long = -1, val receivedSlot: Byte = -1) {
  def getBytes = {
    import LongOps._

    (msg.getBytes.padTo(24, 0.toByte) :+ slot) ++ timestamp.getBytes.reverse
  }
}

object Slot {
  def apply(data: Array[Byte], receivedSlot: Byte) = {
    new Slot((new String(data.slice(0, 24)).trim),
      data(24),
      LongOps.fromBytes(data.slice(25, 33).reverse),
      System.currentTimeMillis,
      receivedSlot)
  }
}
