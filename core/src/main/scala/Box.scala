import scala.collection.mutable.ArrayBuffer

case class Box(contents: ArrayBuffer[Priceable], price: Int = 0) extends Priceable {

  def getPrice: Int = contents.map(_.getPrice).sum

  def add(p: Priceable) {
    contents.append(p)
  }

  def removeLast(): Unit = {
    contents.remove(contents.size - 1)
  }

}
