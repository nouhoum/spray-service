package com.nouhoum.jobpostings.services

import spray.routing._
import akka.actor.{Props, Actor}
import com.nouhoum.jobpostings.repos.InMemoryJobRepositoryComponent
import com.nouhoum.jobpostings.JobPosting

import spray.httpx.SprayJsonSupport
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import spray.http.StatusCodes._
import spray.http.MediaTypes._
import scala.concurrent.Future


class JobPostingServiceActor extends Actor
                                with JobPostingService
                                with JobBusinessComponent
                                with InMemoryJobRepositoryComponent {
  def actorRefFactory = context

  def receive = runRoute(route)
}

object JobPostingServiceActor {
  def props: Props = Props(new JobPostingServiceActor)
}

case class JobData(title:String, description: String) {
  def toJobPosting = JobPosting(None, title, description)
}

object JsonFormats extends spray.json.DefaultJsonProtocol with SprayJsonSupport {
  implicit val jobFormat = jsonFormat3(JobPosting)
  implicit val jobDataFormat = jsonFormat2(JobData)
}

import JsonFormats._

trait JobPostingService extends HttpService {
  this: JobBusinessComponent =>

  val route =
    pathPrefix("jobs") {
      path(IntNumber) { jobId =>
          get {
            onSuccess(jobBusiness.get(jobId)) { job =>
                complete(job)
            }
          } ~
          delete {
            onComplete(jobBusiness.delete(jobId)) {
              case Success(job) => complete(NoContent)
              case Failure(error) => complete(BadRequest)
            }
          } ~
          put {
            entity(as[JobData]) {
              jobData =>
                val resultF: Future[JobPosting] = jobBusiness.get(jobId).flatMap(
                  _.map(
                    job => jobBusiness.update(job.copy(title = jobData.title, description = jobData.description))
                  ).getOrElse (Future.failed(new IllegalArgumentException))
                )

                onComplete(resultF) {
                  case Success(job) => complete("Updated with sucesss")
                  case Failure(_) => complete(BadRequest)
                }
            }
          }
      } ~
      get {
        onSuccess(jobBusiness.getAll()) { jobs =>
          complete(jobs)
        }
      } ~
      post {
        entity(as[JobData]) { jobData =>
          onSuccess(jobBusiness.post(jobData.toJobPosting)) { createdJob =>
            respondWithMediaType(`application/json`) {
              complete(Created, s"{ Location: http://localhost:8080/jobs/${createdJob.id.get} }")
            }
          }
        }
      }
    }
}