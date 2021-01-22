package ru.primetalk.study.akka

import akka.actor.{Actor, ActorRef}
import ru.primetalk.study.akka.Philosopher._

import scala.collection.mutable.ListBuffer

// Официант
class Waiter extends Actor {

  // Нужно иметь связь между философами-акторами и философами-сущностями у официанта
  case class PhilosopherRef(name: String, isEating: Boolean, philosopher: ActorRef)

  // Список философов у официанта
  val philosophers: ListBuffer[PhilosopherRef] = ListBuffer()


  override def receive: Receive = {
    // При регистрации нового философа он добавляется в список
    case RegistrationMessage(name) =>
      println(s"$name added to the waiter's list")
      philosophers += PhilosopherRef(name, isEating = false, sender())
    // Если философ голоден
    case Hungry =>
      // В списке находится философ, и смотрится, свободны ли вилки слева и справа от него
      val phil = findPhilosopher(sender())
      val ind = philosophers.indexOf(phil)
      val leftNeighbor =
        if (ind == 0) philosophers.last
        else philosophers(ind - 1)
      val rightNeighbor = philosophers((ind + 1) % philosophers.size)
      // Если соседние философы не едят, то философ может взять вилки
      if (!leftNeighbor.isEating && !rightNeighbor.isEating) {
        // У философа надо изменять статус, но кейс-классы лучше не менять, поэтому копируется
        phil.copy(isEating = true)
        sender() ! ForksAvailable
        println(s"Forks were given to a ${phil.name}")
      }
      // Иначе он продолжает ждать
      else {
        sender() ! NoForksAvailable
      }
      // Если философ перестал есть, то это отмечается
    case FinishedEating => findPhilosopher(sender()).copy(isEating = false)
  }

    // Метод находит философа в списке официанта по пришедшей ссылке актора
  private def findPhilosopher(phil: ActorRef): PhilosopherRef =
    philosophers.find(_.philosopher == phil).getOrElse(throw new NoSuchElementException("Philosopher not found"))
}
