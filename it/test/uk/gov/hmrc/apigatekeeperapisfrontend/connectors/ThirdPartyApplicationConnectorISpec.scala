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

package uk.gov.hmrc.apigatekeeperapisfrontend.connectors

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, stubFor, urlEqualTo}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.{Application, Configuration, Mode}
import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.apigatekeeperapisfrontend.utils.ApiDataTestData
import uk.gov.hmrc.apiplatform.modules.applications.core.domain.models.ApplicationWithCollaboratorsFixtures

class ThirdPartyApplicationConnectorISpec extends BaseConnectorIntegrationSpec with GuiceOneAppPerSuite with ApiDataTestData with ApplicationWithCollaboratorsFixtures {

  private val stubConfig = Configuration(
    "microservice.services.third-party-application.port" -> stubPort,
    "metrics.enabled"                                    -> "false"
  )

  override def fakeApplication(): Application = GuiceApplicationBuilder()
    .configure(stubConfig)
    .in(Mode.Test)
    .build()

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val connector = app.injector.instanceOf[ThirdPartyApplicationConnector]
  }

  "fetch api list" should {
    val url = s"/application?subscribesTo=${defaultContext.value}"
    "fetch all applications" in new Setup {
      stubFor(WireMock.get(urlEqualTo(url))
        .willReturn(aResponse()
          .withStatus(OK)
          .withBody(Json.toJson(List(standardApp)).toString())))

      val result = await(connector.fetchAllApplications(defaultContext))

      result shouldBe List(standardApp)
    }
  }

}
