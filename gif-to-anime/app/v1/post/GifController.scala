package v1.post

import java.io.File
import java.nio.file.Paths

import play.api.libs.Files
import scala.concurrent.ExecutionContext
import scala.sys.process._

import javax.inject.Inject
import play.api.Logger
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, MultipartFormData}
import play.api.mvc.MultipartFormData.FilePart

case class PostFormInput(title: String, body:String )

class GifController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
    extends BaseController {

  private val logger = Logger(getClass)
  private val splitDir = "/tmp/UnProcessed"
  private val stitchDir = "/tmp/Processed"
  private val outputFileName = "out.gif"
  java.nio.file.Files.createDirectories(Paths.get(splitDir))
  java.nio.file.Files.createDirectories(Paths.get(stitchDir))

  def fileExists(name: String): Boolean = Seq("test", "-f", name).! == 0

  private def splitGif(file: File): Unit = {
    Seq("/bin/sh", "-c", s"rm $splitDir/*.png").! // Empty the splitDir before processing a new file
    logger.info("Deleted split images")
    val pathToGif = file.toPath
    if (fileExists(pathToGif.toString)) {
      s"ffmpeg -i ${pathToGif.toString} -vsync 0 $splitDir/output%03d.png -loglevel 16" .!
      logger.info(s"Gif splitted")
      java.nio.file.Files.deleteIfExists(pathToGif)
    }
  }

  private def stitchGif(): Int = {
    logger.info(s"Gif stitched")
    s"ffmpeg -f image2 -i $splitDir/output%03d.png $stitchDir/$outputFileName -loglevel 16 -y" .!
  }

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    request.body.file("name").foreach { case FilePart(key, filename, contentType, file, fileSize, dispositionType) =>
      logger.info(s"Uploading $filename, contentType = $contentType, fileSize = $fileSize")
      splitGif(file)
    }
    Ok(s"Success")
  }

  def download: Action[AnyContent] = Action { implicit request =>
    if (stitchGif() == 0) {
      val filePath = s"$stitchDir/$outputFileName"
      if (fileExists(filePath))
        Ok.sendFile(new java.io.File(filePath))
      else
        Ok("Processing")
    } else {
      Ok("Err")
    }
  }

}
