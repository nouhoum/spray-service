package com.nouhoum.jobpostings.services

import spray.routing._
import akka.actor.{Props, Actor}
import com.nouhoum.jobpostings.repos.InMemoryJobRepositoryComponent
import com.nouhoum.jobpostings.JobPosting

import scala.concurrent.ExecutionContext.Implicits.global
import spray.httpx.SprayJsonSupport

class JobPostingServiceActor extends Actor with JobPostingService with JobBusinessComponent with InMemoryJobRepositoryComponent {
  def actorRefFactory = context

  def receive = runRoute(route)
}

object JobPostingServiceActor {
  def props: Props = Props(new JobPostingServiceActor)
}

import spray.json.DefaultJsonProtocol

object JsonFormats extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val jobFormat = jsonFormat3(JobPosting)
}

import JsonFormats._

trait JobPostingService extends HttpService {
  this: JobBusinessComponent =>

  val route = pathPrefix("jobs") {
    path(IntNumber) {
      jobId =>
        get {
          s"Getting offer with id = $jobId"
          onSuccess(jobBusiness.get(jobId)) {job =>
            complete(job)
          }
        } ~
          delete {
            complete {
              s"Deleting offer with id = $jobId"
            }
          } ~
          put {
            complete {
              s"Updating offer with id = $jobId"
            }
          }
    } ~
      get {
        complete {
          "Getting all job offers"
        }
      } ~
      post {
        complete {
          "Creating a job offer"
        }
      }
  }
}