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

package uk.gov.hmrc.apigatekeeperapisfrontend.controllers

import scala.concurrent.ExecutionContext.Implicits.global

import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._

import uk.gov.hmrc.apigatekeeperapisfrontend.services.{ApmServiceMockModule, TpaServiceMockModule}
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.AsyncHmrcSpec
import uk.gov.hmrc.apigatekeeperapisfrontend.views.html.{ApiDetailsPage, ApiEventsPage, ErrorTemplate}
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._
import uk.gov.hmrc.apiplatform.modules.gkauth.domain.models.GatekeeperRoles
import uk.gov.hmrc.apiplatform.modules.gkauth.services.{LdapAuthorisationServiceMockModule, StrideAuthorisationServiceMockModule}

class ApiDetailsControllerSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  trait Setup extends MockitoSugar
      with ArgumentMatchersSugar
      with StrideAuthorisationServiceMockModule
      with LdapAuthorisationServiceMockModule
      with ApmServiceMockModule
      with TpaServiceMockModule {
    val detailsPage = app.injector.instanceOf[ApiDetailsPage]
    val eventsPage  = app.injector.instanceOf[ApiEventsPage]
    val error       = app.injector.instanceOf[ErrorTemplate]
    val mcc         = app.injector.instanceOf[MessagesControllerComponents]
    val serviceName = ServiceName("fish")

    val controller = new ApiDetailsController(
      mcc,
      StrideAuthorisationServiceMock.aMock,
      LdapAuthorisationServiceMock.aMock,
      detailsPage,
      eventsPage,
      error,
      ApmServiceMock.aMock,
      TpaServiceMock.aMock
    )
  }

  "GET /api-details/:service-name" should {
    val fakeRequest = FakeRequest("GET", "/api-details/fish")

    "return 200 with stride auth" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)
      ApmServiceMock.FetchApi.returnsSingleApi(serviceName)
      TpaServiceMock.FetchAllApplications.returnsData()

      val result = controller.page(serviceName)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 with ldap auth" in new Setup {
      StrideAuthorisationServiceMock.Auth.sessionRecordNotFound()
      LdapAuthorisationServiceMock.Auth.succeeds
      ApmServiceMock.FetchApi.returnsSingleApi(serviceName)
      TpaServiceMock.FetchAllApplications.returnsData()

      val result = controller.page(serviceName)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)
      ApmServiceMock.FetchApi.returnsSingleApi(serviceName)
      TpaServiceMock.FetchAllApplications.returnsData()

      val result = controller.page(serviceName)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "return Error Template" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)
      ApmServiceMock.FetchApi.returnsNoSingleApi(serviceName)

      val result = controller.page(serviceName)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
      contentAsString(result) should include("API Not Found")
    }

    "return 403 for InsufficientEnrolments" in new Setup {
      StrideAuthorisationServiceMock.Auth.hasInsufficientEnrolments()
      LdapAuthorisationServiceMock.Auth.notAuthorised
      val result = controller.page(serviceName)(fakeRequest)
      status(result) shouldBe Status.FORBIDDEN
    }

    "return 303 for SessionRecordNotFound" in new Setup {
      StrideAuthorisationServiceMock.Auth.sessionRecordNotFound()
      LdapAuthorisationServiceMock.Auth.notAuthorised
      val result = controller.page(serviceName)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "GET /api-details/:service-name/events" should {
    val fakeRequest = FakeRequest("GET", "/api-details/fish/events")
      .withFormUrlEncodedBody("includeNoChange" -> "true")

    "return 200 with stride auth" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)
      ApmServiceMock.FetchApi.returnsSingleApi(serviceName)
      ApmServiceMock.FetchApiEvents.returnsEvent(serviceName)

      val result = controller.events(serviceName)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 with ldap auth" in new Setup {
      StrideAuthorisationServiceMock.Auth.sessionRecordNotFound()
      LdapAuthorisationServiceMock.Auth.succeeds
      ApmServiceMock.FetchApi.returnsSingleApi(serviceName)
      ApmServiceMock.FetchApiEvents.returnsEvent(serviceName)

      val result = controller.events(serviceName)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return list of events" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)
      ApmServiceMock.FetchApi.returnsSingleApi(serviceName)
      ApmServiceMock.FetchApiEvents.returnsEvent(serviceName)

      val result = controller.events(serviceName)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
      contentAsString(result) should include("API Version Change")
    }

    "return list of events, excluding no change events" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)
      ApmServiceMock.FetchApi.returnsSingleApi(serviceName)
      ApmServiceMock.FetchApiEvents.returnsEvent(serviceName, includeNoChange = false)

      val request = FakeRequest("GET", "/api-details/fish/events")
        .withFormUrlEncodedBody("includeNoChange" -> "false")

      val result = controller.events(serviceName)(request)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
      contentAsString(result) should include("API Version Change")
    }

    "return no changes found" in new Setup {
      StrideAuthorisationServiceMock.Auth.succeeds(GatekeeperRoles.USER)
      ApmServiceMock.FetchApi.returnsSingleApi(serviceName)
      ApmServiceMock.FetchApiEvents.returnsNoEvents(serviceName)

      val result = controller.events(serviceName)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
      contentAsString(result) should include("There are no matching API changes.")
    }

    "return 403 for InsufficientEnrolments" in new Setup {
      StrideAuthorisationServiceMock.Auth.hasInsufficientEnrolments()
      LdapAuthorisationServiceMock.Auth.notAuthorised
      val result = controller.events(serviceName)(fakeRequest)
      status(result) shouldBe Status.FORBIDDEN
    }

    "return 303 for SessionRecordNotFound" in new Setup {
      StrideAuthorisationServiceMock.Auth.sessionRecordNotFound()
      LdapAuthorisationServiceMock.Auth.notAuthorised
      val result = controller.events(serviceName)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

}
