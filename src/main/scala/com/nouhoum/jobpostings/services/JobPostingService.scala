package com.nouhoum.jobpostings.services

import spray.routing._
import akka.actor.{Props, Actor}
import com.nouhoum.jobpostings.repos.InMemoryJobRepositoryComponent
import com.nouhoum.jobpostings.JobPosting

class JobPostingServiceActor extends Actor with JobPostingService with JobBusinessComponent with InMemoryJobRepositoryComponent {
  def actorRefFactory = context

  def receive = runRoute(route)
}

object JobPostingServiceActor {
  def props: Props = Props(new JobPostingServiceActor)
}

import spray.json.DefaultJsonProtocol

object JsonFormats extends DefaultJsonProtocol {
  implicit val jobFormat = jsonFormat3(JobPosting)
}

trait JobPostingService extends HttpService with DefaultJsonProtocol {
  this: JobBusinessComponent =>

  implicit val jobFormat = jsonFormat3(JobPosting)

  val route = pathPrefix("jobs") {
    path(IntNumber) {
      jobId =>
        get {
          complete {
            s"Getting offer with id = $jobId"
            jobBusiness.get(jobId.toString).mapTo[Option[JobPosting]]
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