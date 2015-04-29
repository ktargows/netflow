/**
 * Copyright 2015 ICT.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ac.ict.acs.netflow.deploy

import scala.reflect.ClassTag

/**
 * Allows Master to persist any state that is necessary in order to recover from a failure.
 * The following semantics are required:
 *   - addApplication and addWorker are called before completing registration of a new app/worker.
 *   - removeApplication and removeWorker are called at any time.
 * Given these two requirements, we will have all apps and workers persisted, but
 * we might not have yet deleted apps or workers that finished (so their liveness must be verified
 * during recovery).
 *
 * The implementation of this trait defines how name-object pairs are stored or retrieved.
 */
trait PersistenceEngine {

  /**
   * Defines how the object is serialized and persisted. Implementation will
   * depend on the store used.
   */
  def persist(name: String, obj: Object)

  /**
   * Defines how the object referred by its name is removed from the store.
   */
  def unpersist(name: String)

  /**
   * Gives all objects, matching a prefix. This defines how objects are
   * read/deserialized back.
   */
  def read[T: ClassTag](prefix: String): Seq[T]

//  final def addApplication(app: ApplicationInfo): Unit = {
//    persist("app_" + app.id, app)
//  }
//
//  final def removeApplication(app: ApplicationInfo): Unit = {
//    unpersist("app_" + app.id)
//  }
//
  final def addWorker(worker: QueryWorkerInfo): Unit = {
    persist("worker_" + worker.id, worker)
  }

  final def removeWorker(worker: QueryWorkerInfo): Unit = {
    unpersist("worker_" + worker.id)
  }

  final def addQuery(query: QueryInfo): Unit = {
    persist("query_" + query.id, query)
  }

  final def removeQuery(query: QueryInfo): Unit = {
    unpersist("query_" + query.id)
  }

  final def addJob(job: Job): Unit = {
    persist("job_" + job.id, job)
  }

  final def removeJob(jobId: String): Unit = {
    unpersist("job_" + jobId)
  }

  /**
   * Returns the persisted data sorted by their respective ids (which implies that they're
   * sorted by time of creation).
   */
  final def readPersistedData(): (Seq[Job], Seq[QueryInfo], Seq[QueryWorkerInfo]) = {
    (read[Job]("job_"), read[QueryInfo]("query_"), read[QueryWorkerInfo]("worker_"))
  }

  def close() {}
}

private[netflow] class BlackHolePersistenceEngine extends PersistenceEngine {

  override def persist(name: String, obj: Object): Unit = {}

  override def unpersist(name: String): Unit = {}

  override def read[T: ClassTag](name: String): Seq[T] = Nil

}
