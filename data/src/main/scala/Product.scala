case class Product(price: Int) extends Priceable {
  override def getPrice: Int = price
}
