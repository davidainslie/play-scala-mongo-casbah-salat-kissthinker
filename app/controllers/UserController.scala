package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json

trait UserController extends Controller {
}

object UserController extends UserController {
  def users = Action {
    Ok(Json.obj(
      "users" -> Json.arr(
        Json.obj(
          "name" -> "lightening",
          "password" -> "mcqueen"
        ),
        Json.obj(
          "name" -> "tow",
          "password" -> "mater"
        )
      )
    ))
  }
}