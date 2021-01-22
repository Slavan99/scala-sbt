package ru.primetalk.study.akka

import akka.actor.{Actor, ActorRef}
import ru.primetalk.study.akka.Philosopher._

// Философ имеет имя и ссылку на обслуживающего официанта
class Philosopher(waiter: ActorRef, name: String) extends Actor {

  // Сколько философ ест и думает
  val timeOfEating = 1000
  val timeOfThinking = 2000

  // В блоке инициализации - он регистрируется у официанта, садится за стол и начинает "думать"
  waiter ! RegistrationMessage(name)
  think()


  // Если получил сообщение, что вилки свободны, берёт вилки, ест
  // Иначе продолжает "думать"
  override def receive: Receive = {
    case ForksAvailable =>
      eat()
      sender() ! FinishedEating
      think()
    case NoForksAvailable =>
      think()
  }

  // Во время обеда и размышлений поток засыпает, а потом выводит информацию

  def eat(): Unit = {
    Thread.sleep(timeOfEating)
    println(s"Philosopher $name finished eating")
  }

  def think(): Unit = {
    Thread.sleep(timeOfThinking)
    waiter ! Hungry
    println(s"Philosopher $name wants to eat")

  }

}

// Компаньон, содержащий сообщения для передачи
object Philosopher {

  // Сообщение для регистрации
  case class RegistrationMessage(name: String)

  case object ForksAvailable

  case object NoForksAvailable

  case object Hungry

  case object FinishedEating

}