package com.nouhoum.jobpostings.repos

import com.nouhoum.jobpostings.JobPosting
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

trait JobRepositoryComponent {
  def jobRepository: JobRepository

  trait JobRepository {
    def insert(job: JobPosting): Future[JobPosting]

    def update(job: JobPosting): Future[JobPosting]

    def get(id: Int): Future[Option[JobPosting]]

    def getAll(): Future[List[JobPosting]]

    def delete(id: Int): Future[Option[JobPosting]]

  }

}

trait InMemoryJobRepositoryComponent extends JobRepositoryComponent {
  def jobRepository: JobRepository = new InMemoryJobRepository

  object InMemoryJobRepository {
    private val db = scala.collection.mutable.Map[Int, JobPosting]()
    db += (1 -> JobPosting(Some(1), "Commercial H/F", "Boulot de commercial avec plein de chanllenges !"))
  }

  class InMemoryJobRepository extends JobRepository {
    import InMemoryJobRepository._

    def insert(job: JobPosting): Future[JobPosting] = future {
      val id  = db.size + 1
      val createdJob = job.copy(id = Some(id))
      db += (id -> createdJob)
      createdJob
    }

    def update(job: JobPosting): Future[JobPosting] = future {
      db.update(job.id.get, job)
      job
    }

    def get(id: Int): Future[Option[JobPosting]] = future {
      db.get(id)
    }

    def getAll(): Future[List[JobPosting]] = Future.successful(db.values.toList)

    def delete(id: Int): Future[Option[JobPosting]] =
      db.remove(id) match {
        case None => Future.failed(new IllegalArgumentException)
        case jobOpt => Future.successful(jobOpt)
      }

  }
}
