package v1.post

import java.io.File
import java.nio.file.Paths
import java.util.UUID

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

  private def splitGif(file: File): String = {
    val hash = UUID.randomUUID().toString
    val outputDirPath = s"$splitDir/$hash"
    // Create directory using the hash value
    java.nio.file.Files.createDirectories(Paths.get(outputDirPath))
    val pathToGif = file.toPath
    if (fileExists(pathToGif.toString)) {
      s"ffmpeg -i ${pathToGif.toString} -vsync 0 $outputDirPath/output%03d.png -loglevel 16" .!
      logger.info(s"Gif splitted")
      java.nio.file.Files.deleteIfExists(pathToGif)
    }
    hash
  }

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { implicit request =>
    val outputHash = request.body.file("name").map { case FilePart(_, filename, contentType, file, fileSize, _) =>
      logger.info(s"Uploading $filename, contentType = $contentType, fileSize = $fileSize")
      splitGif(file)
    }
    Ok(s"${outputHash.getOrElse("Failed")}")
  }

  def download(id: String): Action[AnyContent] = Action { implicit request =>
    val stitched = s"ffmpeg -f image2 -i $stitchDir/$id/output%03d.png $stitchDir/$id/$outputFileName -loglevel 16 -y" .!
    if (stitched == 0) {
      val filePath = s"$stitchDir/$id/$outputFileName"
      if (fileExists(filePath))
        Ok.sendFile(new java.io.File(filePath))
      else
        Ok("Err")
    } else {
      Ok("Processing")
    }
  }

}
