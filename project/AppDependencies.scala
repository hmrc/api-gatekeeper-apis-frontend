import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.22.0"
  val apiDomainVersion = "0.11.0"
  val commonDomainVersion = "0.10.0"

  val compile = Seq(
    "uk.gov.hmrc"        %% "bootstrap-frontend-play-28"   % bootstrapVersion,
    "uk.gov.hmrc"        %% "play-frontend-hmrc"           % "7.20.0-play-28",
    "uk.gov.hmrc"        %% "internal-auth-client-play-28" % "1.6.0",
    "org.apache.commons" %  "commons-csv"                  % "1.10.0",
    "uk.gov.hmrc"        %% "api-platform-api-domain"      % apiDomainVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"           % bootstrapVersion,
    "org.jsoup"                % "jsoup"                            % "1.13.1",
    "com.vladsch.flexmark"     % "flexmark-all"                     % "0.36.8",
    "org.mockito"             %% "mockito-scala-scalatest"          % "1.17.29",
    "com.github.tomakehurst"  %  "wiremock-jre8-standalone"         % "2.27.1",
    "org.scalatest"           %% "scalatest"                        % "3.2.17",
    "uk.gov.hmrc"             %% "api-platform-test-common-domain"  % commonDomainVersion,
  ).map(_ % "test, it")
}
