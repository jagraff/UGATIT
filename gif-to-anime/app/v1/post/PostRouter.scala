package v1.post

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class PostRouter @Inject()(controller: GifController) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/") =>
      controller.download
    case POST(p"/") =>
      controller.upload
  }

}
