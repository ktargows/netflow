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

import cn.ac.ict.acs.netflow.QueryDescription
import cn.ac.ict.acs.netflow.util.Utils

sealed trait MasterMessages extends Serializable

/** Contains messages seen only by the Master and its associated entities. */
object MasterMessages {

  // LeaderElectionAgent to Master

  case object AppointedAsLeader

  case object RevokedLeadership

  // Actor System to Master

  case object CheckForWorkerTimeOut

//  case class BeginRecovery(
//    storedApps: Seq[ApplicationInfo], storedWorkers: Seq[WorkerInfo])

  case object CompleteRecovery

  case object BoundPortsRequest extends MasterMessages

  case class BoundPortsResponse(actorPort: Int, webUIPort: Int)
    extends MasterMessages
}

sealed trait DeployMessage extends Serializable

/**
 * Contains Messages sent between deploy members
 */
object DeployMessages {

  // Worker to Master

  case class RegisterWorker(
      id: String,
      host: String,
      port: Int,
      cores: Int,
      memory: Int,
      webUiPort: Int,
      publicAddress: String)
    extends DeployMessage {
    Utils.checkHost(host, "Required hostname")
    assert (port > 0)
  }

  case class Heartbeat(workerId: String) extends DeployMessage

  // Master to Worker

  case class RegisteredWorker(masterUrl: String, masterWebUiUrl: String) extends DeployMessage

  case class RegisterWorkerFailed(message: String) extends DeployMessage

  case class ReconnectWorker(masterUrl: String) extends DeployMessage

}

object Messages {

  case object SendHeartbeat

  case class RegisterQuery

  case class RegisterQueryFailed

  case class LaunchQuery(
    masterUrl: String,
    queryId: String,
    queryDesc: QueryDescription)

  case class KillQuery(
    masterUrl: String,
    queryId: String)
}