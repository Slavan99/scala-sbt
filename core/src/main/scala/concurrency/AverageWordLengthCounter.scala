package concurrency

import java.util.concurrent.Executors

import cats.effect._
import cats.syntax.parallel._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.BufferedSource

object AverageWordLengthCounter extends App {
  // Подсчёт общей суммы слов для массива (сюда передаются части массива)
  def getWordLengths(words: Array[String]): Int = {
    val wordLengths = words.map(_.length)
    wordLengths.sum
  }

  // функция, которая считает результат IO и время его работы, и оборачивает в ещё один IO
  def measure[T](io: IO[T]): IO[(T, Long)] = {

    val timestamp: Long = System.currentTimeMillis / 1000

    val t = io.unsafeRunSync()

    val time = System.currentTimeMillis / 1000 - timestamp

    IO {
      (t, time)
    }


  }

  private val executorService = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors())
  // executors
  implicit val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(executorService)
  implicit val contextShift: ContextShift[IO] = IO.contextShift(implicitly[ExecutionContext])

  // здесь считываем какой-то большой файл
  var source: BufferedSource = _
  try {
    source = scala.io.Source.fromFile("demo.txt")
    val text: String = source.mkString

    // преобразуем строку в массив слов, для больших текстов очень долго
    val words: Array[String] = text.split("[\n.,\\-\";!? ]+").map(_.trim)

    val wordsLength: Int = words.length

    // разделитель массива на arrayDenominator частей, столько же будет IO-задач
    val arrayDenominator = 5

    // ниже кусок кода с однопоточным вычислением средней длины
    val timestamp: Long = System.currentTimeMillis / 1000

    val singleThreadResult = words.map(_.length).sum * 1.0 / wordsLength

    println("Time of single thread calculation : " + (System.currentTimeMillis / 1000 - timestamp) + " s")

    println("Average word length : " + singleThreadResult)


    // использую итератор sliding для разделения массива, каждой IO передаю просто подмассив
    val ioCounters: Seq[IO[Int]] = {
      val slidingWordsIterator = words.sliding(arrayDenominator, arrayDenominator)
      slidingWordsIterator.map(subList => IO {
        getWordLengths(subList)
      }).toList
    }

    // сделал, как было в примере в лекции
    val ioCountersParallel = ioCounters.map(io => IO.shift *> io)

    // вызвал для каждого IO соотв. задачу для замера времени работы
    val subListMeasureResult: Seq[IO[(Int, Long)]] = ioCountersParallel.map(x => measure(x))

    val resultAndTime = for {
      mes <- subListMeasureResult
    } yield mes.unsafeRunSync()

    val result = resultAndTime.map(_._1).sum * 1.0 / wordsLength

    /* // нашёл метод parSequence и вызвал его, чтобы запускать unsafeRunSync только один раз
     val result = ioCountersParallel.parSequence.unsafeRunSync().sum * 1.0 / wordsLength*/

    println("Time of multi thread calculation : " + resultAndTime.map(_._2).sum + " s")

    println("Average word length : " + result)

    executorService.shutdown()
  } finally {
    source.close()
  }
}
