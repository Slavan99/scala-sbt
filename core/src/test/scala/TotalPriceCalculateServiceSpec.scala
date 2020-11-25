import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.collection.mutable.ArrayBuffer

class TotalPriceCalculateServiceSpec extends AnyFlatSpec with should.Matchers {

  "TotalPriceCalculateService" should "calculate total price for given amount of products" in {
    val product1 = Product(4)
    val product2 = Product(5)
    val box1 = Box(ArrayBuffer(product1, product2))
    val product3 = Product(2)
    val box2 = Box(ArrayBuffer(product3))
    box1.add(box2)
    TotalPriceCalculateService.calculateTotalPrice(ArrayBuffer(box1)) should be(11)
  }

  it should "return zero if boxes are empty" in {
    val box1 = Box(ArrayBuffer())
    val box2 = Box(ArrayBuffer())
    TotalPriceCalculateService.calculateTotalPrice(ArrayBuffer(box1, box2)) should be (0)
  }

}
