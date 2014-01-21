package com.nouhoum.jobpostings.repos

import com.nouhoum.jobpostings.JobPosting
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

trait JobRepositoryComponent {
  def jobRepository: JobRepository

  trait JobRepository {
    def insert(job: JobPosting): Future[Unit]

    def update(job: JobPosting): Future[Unit]

    def get(id: Int): Future[Option[JobPosting]]

    def getAll(): Future[List[JobPosting]]

    def delete(id: String): Future[Unit]

    def getPublishedJobs(): Future[List[JobPosting]]
  }

}

trait InMemoryJobRepositoryComponent extends JobRepositoryComponent {
  def jobRepository: JobRepository = new InMemoryJobRepository

  class InMemoryJobRepository extends JobRepository {
    private val db = scala.collection.mutable.Map[Int, JobPosting]()

    db += (1 -> JobPosting(1, "Commercial H/F", "Boulot de commercial avec plein de chanllenges !"))

    def insert(job: JobPosting): Future[Unit] = Future {
      db + (job.id -> job)
    }

    def update(job: JobPosting): Future[Unit] = ???

    def get(id: Int): Future[Option[JobPosting]] = future {
      db.get(id)
    }

    def getAll(): Future[List[JobPosting]] = ???

    def delete(id: String): Future[Unit] = ???

    def getPublishedJobs(): Future[List[JobPosting]] = ???
  }

}
