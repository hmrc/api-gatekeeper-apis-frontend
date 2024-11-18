/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apigatekeeperapisfrontend.utils

import java.time.Instant

import uk.gov.hmrc.apigatekeeperapisfrontend.models.DisplayApiEvent
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Environment.SANDBOX
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApiContext, ApiVersionNbr}
import uk.gov.hmrc.apiplatform.modules.common.utils.FixedClock

trait ApiDataTestData extends FixedClock {
  val defaultVersion: ApiVersionNbr   = ApiVersionNbr("1.0")
  val defaultContext: ApiContext      = ApiContext("test/hello")
  val defaultPublish: Instant         = Instant.parse("2022-10-12T19:00:00.000Z")
  val defaultServiceName: ServiceName = ServiceName("helloworld")
  val defaultEvent: DisplayApiEvent   = DisplayApiEvent(defaultServiceName, instant, "API Version Change", SANDBOX, List.empty)

  val defaultApiDefinition: ApiDefinition = ApiDefinition(
    serviceName = defaultServiceName,
    serviceBaseUrl = "a.com/",
    name = "Hello World",
    description = "This is a test api",
    context = defaultContext,
    versions = Map(defaultVersion -> ApiVersion(defaultVersion, ApiStatus.STABLE, ApiAccess.PUBLIC, List(), true, None, ApiVersionSource.OAS)),
    isTestSupport = false,
    lastPublishedAt = Some(defaultPublish),
    categories = List(ApiCategory.OTHER)
  )
}
