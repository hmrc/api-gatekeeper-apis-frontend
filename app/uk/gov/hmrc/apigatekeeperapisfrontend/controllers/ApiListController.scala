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

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.apigatekeeperapisfrontend.controllers.actions.GatekeeperRoleActions
import uk.gov.hmrc.apigatekeeperapisfrontend.services.ApmService
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.CsvHelper.ColumnDefinition
import uk.gov.hmrc.apigatekeeperapisfrontend.utils.{ApiDefinitionView, CsvHelper}
import uk.gov.hmrc.apigatekeeperapisfrontend.views.html.ApiListPage
import uk.gov.hmrc.apiplatform.modules.apis.domain.models._
import uk.gov.hmrc.apiplatform.modules.common.domain.models.Environment
import uk.gov.hmrc.apiplatform.modules.gkauth.controllers.GatekeeperBaseController
import uk.gov.hmrc.apiplatform.modules.gkauth.services.{LdapAuthorisationService, StrideAuthorisationService}

@Singleton
class ApiListController @Inject() (
    mcc: MessagesControllerComponents,
    strideAuthorisationService: StrideAuthorisationService,
    val ldapAuthorisationService: LdapAuthorisationService,
    apiListPage: ApiListPage,
    apmService: ApmService
  )(implicit override val ec: ExecutionContext
  ) extends GatekeeperBaseController(strideAuthorisationService, mcc) with GatekeeperRoleActions {

  val page: Action[AnyContent] = loggedInOnly() { implicit request =>
    apmService.fetchAllApis()
      .map(_.distinct)
      .map(_.sortBy(_.name.toLowerCase))
      .map(nameContext => Ok(apiListPage(nameContext)))

  }

  private def isTrial(apiVersion: ApiVersion): Boolean = {
    apiVersion.access match {
      case ApiAccess.Private(true) => true
      case _                       => false
    }
  }

  val csv: Action[AnyContent] = loggedInOnly() { implicit request =>
    val columnDefinitions: Seq[ColumnDefinition[ApiDefinitionView]] = Seq(
      ColumnDefinition("name", defView => defView.name),
      ColumnDefinition("serviceName", defView => defView.serviceName.value),
      ColumnDefinition("context", defView => defView.context.value),
      ColumnDefinition("version", defView => defView.versionNbr.value),
      ColumnDefinition("source", defView => defView.versionSource.toString),
      ColumnDefinition("status", defView => defView.status.toString),
      ColumnDefinition("access", defView => defView.access.displayText),
      ColumnDefinition("isTrial", defView => defView.isTrial.toString),
      ColumnDefinition("environment", defView => defView.environment.displayText),
      ColumnDefinition("lastPublishedAt", defView => defView.lastPublishedAt.map(_.toString).getOrElse(""))
    )

    apmService
      .fetchAllApis()
      .map(container =>
        container.sandbox.flatMap(apiDef =>
          apiDef.versionsAsList.map(v =>
            ApiDefinitionView(
              apiDef.name,
              apiDef.serviceName,
              apiDef.context,
              v.versionNbr,
              v.versionSource,
              v.status,
              v.access.accessType,
              isTrial(v),
              Environment.SANDBOX,
              apiDef.lastPublishedAt
            )
          )
        ) ++ container.production.flatMap(apiDef =>
          apiDef.versionsAsList.map(v =>
            ApiDefinitionView(
              apiDef.name,
              apiDef.serviceName,
              apiDef.context,
              v.versionNbr,
              v.versionSource,
              v.status,
              v.access.accessType,
              isTrial(v),
              Environment.PRODUCTION,
              apiDef.lastPublishedAt
            )
          )
        )
      )
      .map(views => Ok(CsvHelper.toCsvString(columnDefinitions, views)).as("text/csv"))
  }

}
