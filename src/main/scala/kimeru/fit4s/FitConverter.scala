/*
 * Copyright 2019 Kimeru
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
import java.io._
import java.nio.file._

import com.garmin.fit._

class FitConverter(skipHeader: Boolean = false, incompleteStream: Boolean = false) {

  val decode                     = new Decode
  val csvFileOptions: OpenOption = StandardOpenOption.CREATE

  if (skipHeader) decode.skipHeader()
  if (incompleteStream) decode.incompleteStream()

  private def setupMesgBroadcaster(decode: Decode, fitListener: FitListener): MesgBroadcaster = {
    val mesgBroadcaster = new MesgBroadcaster(decode)
    mesgBroadcaster.addListener(fitListener.asInstanceOf[FileIdMesgListener])
    mesgBroadcaster.addListener(fitListener.asInstanceOf[UserProfileMesgListener])
    mesgBroadcaster.addListener(fitListener.asInstanceOf[DeviceInfoMesgListener])
    mesgBroadcaster.addListener(fitListener.asInstanceOf[MonitoringMesgListener])
    mesgBroadcaster.addListener(fitListener.asInstanceOf[RecordMesgListener])
    mesgBroadcaster
  }

  private def runConversion(is: InputStream,
                            bufferedWriter: BufferedWriter,
                            includeHeader: Boolean = true) = {
    val fitListener: FitListener = new FitListener(bufferedWriter)
    val broadcaster              = setupMesgBroadcaster(decode, fitListener)
    decode.addListener(fitListener)

    if (includeHeader) {
      bufferedWriter.write(
        "secs,cad,hr,km,kph,nm,watts,alt,lon,lat,headwind,slope,temp,interval,lrbalance,lte,rte,lps,rps,smo2,thb,o2hb,hhb\n"
      )
    }

    decode.read(is, broadcaster, broadcaster)
  }

  def FitToCsvByteArrayIO(inputFit: Array[Byte]): Array[Byte] = {
    // allocate buffers
    val bis            = new ByteArrayInputStream(inputFit)
    val baos           = new ByteArrayOutputStream()
    val bufferedWriter = new BufferedWriter(new OutputStreamWriter(baos))

    // run conversion
    runConversion(bis, bufferedWriter)
    // closing resources
    bufferedWriter.flush()
    bis.close()
    baos.toByteArray
  }

  def extractFitFileToCsvFile(fitFile: String, csvFile: String): Unit = {
    val fitFilePath = Paths.get(fitFile)
    val cvsFilePath = Paths.get(csvFile)

    try {
      // allocate file handles
      val inputFitByteArray = Files.readAllBytes(fitFilePath)
      val printWriter: PrintWriter = new PrintWriter(
        Files.newOutputStream(cvsFilePath, csvFileOptions)
      )
      // run conversion
      val outputBytes: Array[Byte] = FitToCsvByteArrayIO(inputFitByteArray)
      printWriter.write(outputBytes.map(_.toChar))

      // closing resources
      printWriter.flush()
      printWriter.close()
    } catch {
      case err: NoSuchFileException => {
        System.err.println(s"File ${err.getLocalizedMessage} not found.")
        System.exit(-1)
      }
    }
  }

  def autoCsvFileName(path: String): String = {
    var fileName = path
    val pos      = fileName.lastIndexOf('.')
    if (pos > 0) {
      fileName = fileName.substring(0, pos) ++ ".csv"
    } else {
      fileName ++ ".csv"
    }
    fileName
  }

}
