package controllers

import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import org.specs2.mutable.Specification

class UserControllerSpec extends Specification {
  "End user" should {
    "acquire a list of all users formatted as JSON" in new WithApplication {
      val result = UserController.users(FakeRequest())
      status(result) shouldEqual OK
      contentType(result) should beSome("application/json")
    }
  }
}