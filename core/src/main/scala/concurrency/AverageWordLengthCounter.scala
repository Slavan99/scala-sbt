package concurrency

import java.util.concurrent.Executors

import cats.effect._
import cats.syntax.parallel._

import scala.concurrent.ExecutionContext
import scala.io.BufferedSource

object AverageWordLengthCounter extends App {
  // Подсчёт общей суммы слов для отдельной части массива
  def getWordLengths(words: Array[String], startPos: Int, endPos: Int): Int =
    words.slice(startPos, endPos).map(_.length).sum

  private val executorService = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors())
  // executors
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executorService)
  implicit val contextShift: ContextShift[IO] = IO.contextShift(implicitly[ExecutionContext])

  // здесь считываем какой-то большой файл
  val source: BufferedSource = scala.io.Source.fromFile("bigtext.txt")
  val text: String = source.mkString
  source.close()

  // преобразуем строку в массив слов, для больших текстов очень долго
  val words: Array[String] = text.split("[\n.,\\-\";!? ]+").map(_.trim)

  val wordsLength: Int = words.length

  // разделитель массива на arrayDenominator частей, столько же будет IO-задач
  val arrayDenominator = 5

  // ниже кусок кода с однопоточным вычислением средней длины
  val timestamp: Long = System.currentTimeMillis / 1000

  private val singleThreadResult = words.map(_.length).sum * 1.0 / wordsLength

  println("Time of single thread calculation : " + (System.currentTimeMillis / 1000 - timestamp) + " s")

  println("Average word length : " + singleThreadResult)


  // ниже кусок кода с многопоточным вычислением средней длины
  val timestamp1: Long = System.currentTimeMillis / 1000

  // разделяем массив, каждой IO-задаче передаём подсчёт суммы слов части массива с соотв-ими индексами
  val ioCounters: Seq[IO[Int]] = for (
    i <- 0 until arrayDenominator; dividedArrayLength = wordsLength / arrayDenominator)
    yield if (i < arrayDenominator - 1) IO {
      getWordLengths(words, dividedArrayLength * i, dividedArrayLength * (i + 1))
    }
    else IO {
      getWordLengths(words, dividedArrayLength * i, wordsLength)
    }

  // сделал, как было в примере в лекции
  val ioCountersParallel = ioCounters.map(io => IO.shift *> io)

  // нашёл метод parSequence и вызвал его, чтобы запускать unsafeRunSync только один раз
  private val result = ioCountersParallel.parSequence.unsafeRunSync().sum * 1.0 / wordsLength

  println("Time of multi thread calculation : " + (System.currentTimeMillis / 1000 - timestamp1) + " s")

  println("Average word length : " + result)

  // Завершает работу executor
  executorService.shutdown()
}
