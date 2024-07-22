import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.0.0"
  val apiDomainVersion         = "0.16.0"
  val commonDomainVersion      = "0.14.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"   % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"   % "10.5.0",
    "uk.gov.hmrc"       %% "internal-auth-client-play-30" % "3.0.0",
    "org.apache.commons" % "commons-csv"                  % "1.10.0",
    "uk.gov.hmrc"       %% "api-platform-api-domain"      % apiDomainVersion
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30"          % bootstrapVersion,
    "org.jsoup"    % "jsoup"                           % "1.17.2",
    "uk.gov.hmrc" %% "api-platform-test-common-domain" % commonDomainVersion
  ).map(_ % "test")
}
