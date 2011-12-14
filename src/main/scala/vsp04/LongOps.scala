package vsp04

class LongOps(long: Long) {
  def getBytes = {
    (for (i <- (0 to 7).toArray) yield ((long & (255L << i * 8)) >> i * 8).toByte)
  }
}

object LongOps {
  implicit def long2LongOps(long: Long) = new LongOps(long)

  def fromBytes(data: Array[Byte]) = {
    data.foldLeft((0L, 0L)) { (transport, value) =>
      val (count, result) = transport
      (count + 1, result | ((value & 0xFF).toLong << count * 8))
    }._2
  }
}
