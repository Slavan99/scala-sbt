package ru.primetalk.study.akka

import akka.actor.{ActorSystem, Props}

// Для запуска
object DiningPhilosophers extends App {

  // создаём акторную систему
  val system = ActorSystem("dining_philosophers")

  // Создаём одного официанта
  val waiter = system.actorOf(Props[Waiter])

  // Пять философов зашли пообедать
  system.actorOf(Props(new Philosopher(waiter, "Phil-1")))
  system.actorOf(Props(new Philosopher(waiter, "Phil-2")))
  system.actorOf(Props(new Philosopher(waiter, "Phil-3")))
  system.actorOf(Props(new Philosopher(waiter, "Phil-4")))
  system.actorOf(Props(new Philosopher(waiter, "Phil-5")))

  // Программа работает 10 секунд, после чего акторная система завершает работу
  Thread.sleep(10000)
  system.terminate()
  println("-----------------")
  println("System terminated")
  println("-----------------")


}

