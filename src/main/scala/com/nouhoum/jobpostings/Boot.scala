package com.nouhoum.jobpostings

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.nouhoum.jobpostings.services.JobPostingServiceActor
import scala.concurrent.duration._
import spray.can.Http

object Boot extends App {
  println("Booting the service...")
  implicit val system = ActorSystem("job-posting-service-sys")

  val service = system.actorOf(JobPostingServiceActor.props, "job-posting-service")

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8081)
}
