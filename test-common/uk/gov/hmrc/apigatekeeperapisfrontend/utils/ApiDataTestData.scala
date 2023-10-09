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

import uk.gov.hmrc.apiplatform.modules.apis.domain.models.{ApiAccess, ApiCategory, ApiData, ApiStatus, ApiVersion, ApiVersionSource, ServiceName}
import uk.gov.hmrc.apiplatform.modules.common.domain.models.{ApiContext, ApiVersionNbr}

import java.time.Instant

trait ApiDataTestData {
  val defaultVersion = ApiVersionNbr("1.0")
  val defaultContext = ApiContext("hello")
  val defaultPublish = Instant.parse("2022-10-12T19:00:00.000Z")

  def anApiDataMap(): ApiData.ApiDefinitionMap = {

    Map(defaultContext -> ApiData(
      ServiceName("helloworld"),
      "a.com/",
      "Hello World",
      "This is a test api",
      defaultContext,
      Map(defaultVersion -> ApiVersion(defaultVersion, ApiStatus.STABLE, ApiAccess.PUBLIC, List(), true, None, ApiVersionSource.OAS)),
      false,
      false,
      Some(defaultPublish),
      List(ApiCategory.OTHER)
    ))
  }
}
