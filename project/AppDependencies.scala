import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.22.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28"   % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc"           % "7.20.0-play-28",
    "uk.gov.hmrc" %% "internal-auth-client-play-28" % "1.2.0",
    "uk.gov.hmrc" %% "api-platform-api-domain"      % "0.7.0"
  )

  val test = Seq(
    "uk.gov.hmrc"           %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.jsoup"              % "jsoup"                    % "1.13.1",
    "org.mockito"           %% "mockito-scala-scalatest"  % "1.16.46",
    "com.vladsch.flexmark"   % "flexmark-all"             % "0.36.8",
    "com.github.tomakehurst" % "wiremock-jre8-standalone" % "2.31.0"
  ).map(_ % "test, it")
}
