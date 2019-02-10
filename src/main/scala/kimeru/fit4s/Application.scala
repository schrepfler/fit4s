/*
 * Copyright 2019 Srdan Srepfler
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

package kimeru.fit4s
import java.io.PrintWriter
import java.nio.file.{NoSuchFileException, _}

import caseapp._
import com.garmin.fit._

case class Options(
    skipHeader: Boolean = false,
    incompleteStream: Boolean = false
)

object Application extends CaseApp[Options] {

  val decode                  = new Decode
  val fitFileOptions: OpenOption = StandardOpenOption.READ
  val csvFileOptions: OpenOption = StandardOpenOption.CREATE

  def run(options: Options, arg: RemainingArgs): Unit = {

    if (options.skipHeader) decode.skipHeader()
    if (options.incompleteStream) decode.incompleteStream()

    arg.remaining.foreach(
      convertFitToCSV
    )

  }

  def convertFitToCSV(file: String): Unit = {
    val fitFilePath = Paths.get(file)
    val cvsFilePath = Paths.get(determineCsvFileName(file))

    try {
      val fis = Files.newInputStream(fitFilePath, fitFileOptions)
      val printWriter = new PrintWriter(Files.newOutputStream(cvsFilePath, csvFileOptions))
      val fitListener: FitListener = new FitListener(printWriter)

      val mesgBroadcaster = new MesgBroadcaster(decode)
      mesgBroadcaster.addListener(fitListener.asInstanceOf[FileIdMesgListener])
      mesgBroadcaster.addListener(fitListener.asInstanceOf[UserProfileMesgListener])
      mesgBroadcaster.addListener(fitListener.asInstanceOf[DeviceInfoMesgListener])
      mesgBroadcaster.addListener(fitListener.asInstanceOf[MonitoringMesgListener])
      mesgBroadcaster.addListener(fitListener.asInstanceOf[RecordMesgListener])

      printWriter.println(
        "secs,cad,hr,km,kph,nm,watts,alt,lon,lat,headwind,slope,temp,interval,lrbalance,lte,rte,lps,rps,smo2,thb,o2hb,hhb"
      )
      decode.addListener(fitListener)
      decode.read(fis, mesgBroadcaster, mesgBroadcaster)
      printWriter.flush()
      printWriter.close()
    } catch {
      case err: NoSuchFileException => {
        System.err.println(s"File ${err.getLocalizedMessage} not found.")
        System.exit(-1)
      }
    }
  }

  def determineCsvFileName(path: String): String ={
    var fileName = path
    val pos = fileName.lastIndexOf('.')
    if (pos > 0) {
      fileName = fileName.substring(0, pos) ++ ".csv"
    } else {
      fileName ++ ".csv"
    }
    fileName
  }

}
