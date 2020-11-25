import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn

object Main extends App {

  def printOption(): Unit = print("What product is being added now? B - box, P - simple product, \"\" - end adding : ")

  def printElementsAmount(): Unit = print("How many elements do you want to add in the box : ")

  printOption()
  var productArray = ArrayBuffer[Priceable]()
  var option = StdIn.readLine
  while (!"".equals(option)) {
    while ("B".equals(option)) {
      val box = Box(ArrayBuffer())
      printOption()
      option = StdIn.readLine
    }
  }

}
