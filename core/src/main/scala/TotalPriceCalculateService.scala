import scala.collection.mutable.ArrayBuffer

object TotalPriceCalculateService {

  def calculateTotalPrice(products: ArrayBuffer[Priceable]): Int = products.map(_.getPrice).sum

}
