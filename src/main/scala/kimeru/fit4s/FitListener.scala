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
import java.io.BufferedWriter
import java.text.DecimalFormat

import com.garmin.fit._

class FitListener(bufferedWriter: BufferedWriter)
    extends FileIdMesgListener
    with UserProfileMesgListener
    with DeviceInfoMesgListener
    with MonitoringMesgListener
    with RecordMesgListener
    with DeveloperFieldDescriptionListener {

  var second = 0
  override def onMesg(fileIdMesg: FileIdMesg): Unit = {
//    println(s"fileIdMesg $fileIdMesg")
  }
  override def onMesg(userProfileMesg: UserProfileMesg): Unit = {
//    println(s"userProfileMesg $userProfileMesg")
  }
  override def onMesg(deviceInfoMesg: DeviceInfoMesg): Unit = {
//    println(s"deviceInfoMesg $deviceInfoMesg")
  }
  override def onMesg(monitoringMesg: MonitoringMesg): Unit = {
//    println(s"monitoringMesg $monitoringMesg")
  }
  override def onMesg(recordMesg: RecordMesg): Unit = {

    val distance = new DecimalFormat("0.00000").format(recordMesg.getDistance.floatValue() * 0.001)
    val speed    = new DecimalFormat("0.0000").format(recordMesg.getEnhancedSpeed.floatValue() * 3.6) // m/s to km/h

    // unknown: nm, headwind, slope, interval, o2hb, hhb

    bufferedWriter.write(
      s"$second,${recordMesg.getCadence},${recordMesg.getHeartRate}," +
      s"$distance,$speed,0,${recordMesg.getPower},${recordMesg.getAltitude},${degrees(recordMesg.getPositionLong)}," +
      s"${degrees(recordMesg.getPositionLat)},0,0,0,0," +
      s"0,0," +
      s"0,0," +
      s"0,0,0,0,0\n"
    )
    second += 1
  }
  override def onDescription(developerFieldDescription: DeveloperFieldDescription): Unit = {
//    println(s"developerFieldDescription $developerFieldDescription")
  }

  def degrees(semicircles: Int): Float =
    semicircles / 11930464.71f
}
