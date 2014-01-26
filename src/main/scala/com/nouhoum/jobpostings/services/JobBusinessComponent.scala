package com.nouhoum.jobpostings.services

import com.nouhoum.jobpostings.repos.JobRepositoryComponent
import scala.concurrent.Future
import com.nouhoum.jobpostings.JobPosting

trait JobBusinessComponent {
  this: JobRepositoryComponent =>

  def jobBusiness: JobBusiness = new JobBusiness {}

  trait JobBusiness {
    def get(id: Int): Future[Option[JobPosting]] = jobRepository.get(id)
    def delete(id: Int): Future[Option[JobPosting]] = jobRepository.delete(id)
    def getAll(): Future[List[JobPosting]] = jobRepository.getAll()
    def post(job: JobPosting): Future[JobPosting] = jobRepository.insert(job)
    def update(job: JobPosting): Future[JobPosting] = jobRepository.update(job)
  }
}
