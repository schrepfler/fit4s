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
import java.nio.file.{ Files, Paths }
import java.util
import java.util.stream.Collectors
import scala.collection.JavaConversions._

import org.scalatest.{ BeforeAndAfterEach, FunSuite }

class FitConverterTest extends FunSuite with BeforeAndAfterEach {

  val fitConverter = new FitConverter()
  val inputFile    = "2018_10_09_15_24_50.fit"
  val outputFile   = fitConverter.autoCsvFileName(inputFile)

  override def beforeEach() {}
  override def afterEach(): Unit =
    Files.deleteIfExists(Paths.get(outputFile))
  test("testExtractFitFileToCsvFile") {

    require(Files.exists(Paths.get(inputFile)), "Given fit file")
    require(!Files.exists(Paths.get(outputFile)), "Given there's no pre-existing converted file")

    fitConverter.extractFitFileToCsvFile(inputFile, outputFile)

    assert(outputFile == "2018_10_09_15_24_50.csv",
           "Then the converted file name uses the basic convention")
    assert(Files.exists(Paths.get(outputFile)), "Then a converted file has been created")

    val lines: util.List[String] = Files.lines(Paths.get(outputFile)).collect(Collectors.toList())
    val header                   = lines.get(0)
    assert(
      header == "secs,cad,hr,km,kph,nm,watts,alt,lon,lat,headwind,slope,temp,interval,lrbalance,lte,rte,lps,rps,smo2,thb,o2hb,hhb",
      "Header must be present"
    )

    val line1 = lines.get(1)
    assert(line1 == "0,0,91,0.00000,0.0000,0,0,34.4,-5.483009,36.17939,0,0,0,0,0,0,0,0,0,0,0,0,0",
           "Line 1 must match")

    val line2 = lines.get(2)
    assert(line2 == "1,0,91,0.00021,0.7560,0,0,34.4,-5.4830103,36.17939,0,0,0,0,0,0,0,0,0,0,0,0,0",
           "Line 2 must match")

    val line3 = lines.get(3)
    assert(line3 == "2,0,91,0.00083,2.2320,0,0,34.4,-5.483017,36.179386,0,0,0,0,0,0,0,0,0,0,0,0,0",
           "Line 3 must match")

    val line4 = lines.get(4)
    assert(line4 == "3,0,92,0.00294,4.4208,0,0,34.4,-5.4830403,36.17939,0,0,0,0,0,0,0,0,0,0,0,0,0",
           "Line 4 must match")

    val line5 = lines.get(5)
    assert(line5 == "4,0,92,0.00514,6.5268,0,0,34.0,-5.483063,36.179382,0,0,0,0,0,0,0,0,0,0,0,0,0",
           "Line 5 must match")

    val line6 = lines.get(6)
    assert(line6 == "5,0,92,0.00795,9.0792,0,0,33.4,-5.483094,36.17938,0,0,0,0,0,0,0,0,0,0,0,0,0",
           "Line 6 must match")

    val line7 = lines.get(7)
    assert(
      line7 == "6,0,92,0.01160,11.5164,0,0,33.6,-5.4831295,36.179394,0,0,0,0,0,0,0,0,0,0,0,0,0",
      "Line 7 must match"
    )

    val line8 = lines.get(8)
    assert(line8 == "7,0,92,0.01560,13.3596,0,0,33.8,-5.483166,36.179417,0,0,0,0,0,0,0,0,0,0,0,0,0",
           "Line 8 must match")

    val line9 = lines.get(9)
    assert(line9 == "8,0,89,0.01973,14.6628,0,0,33.6,-5.483204,36.179436,0,0,0,0,0,0,0,0,0,0,0,0,0",
           "Line 9 must match")

    assert(lines.exists(_.startsWith("6269,65,121")))
    assert(lines.exists(_.startsWith("6270")))
    assert(lines.exists(_.startsWith("6419"))) //expected last row
  }

  test("testFitToCsvByteArrayIO - should start correctly") {

    require(Files.exists(Paths.get(inputFile)), "Given fit file")
    require(!Files.exists(Paths.get(outputFile)), "Given there's no pre-existing converted file")

    val input: Array[Byte] = Files.readAllBytes(Paths.get(inputFile))
    val outputBytes        = fitConverter.FitToCsvByteArrayIO(input)

    val outputString = outputBytes.map(_.toChar).mkString

    assert(
      outputString.startsWith(
        """secs,cad,hr,km,kph,nm,watts,alt,lon,lat,headwind,slope,temp,interval,lrbalance,lte,rte,lps,rps,smo2,thb,o2hb,hhb
                                   |0,0,91,0.00000,0.0000,0,0,34.4,-5.483009,36.17939,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |1,0,91,0.00021,0.7560,0,0,34.4,-5.4830103,36.17939,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |2,0,91,0.00083,2.2320,0,0,34.4,-5.483017,36.179386,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |3,0,92,0.00294,4.4208,0,0,34.4,-5.4830403,36.17939,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |4,0,92,0.00514,6.5268,0,0,34.0,-5.483063,36.179382,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |5,0,92,0.00795,9.0792,0,0,33.4,-5.483094,36.17938,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |6,0,92,0.01160,11.5164,0,0,33.6,-5.4831295,36.179394,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |7,0,92,0.01560,13.3596,0,0,33.8,-5.483166,36.179417,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |8,0,89,0.01973,14.6628,0,0,33.6,-5.483204,36.179436,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |9,0,85,0.02486,16.9524,0,0,33.6,-5.4832525,36.179462,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |10,0,85,0.03041,18.9324,0,0,33.4,-5.483306,36.179485,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |11,0,85,0.03645,20.6856,0,0,32.8,-5.483366,36.17951,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |12,0,85,0.04385,24.0300,0,0,32.8,-5.4834423,36.179535,0,0,0,0,0,0,0,0,0,0,0,0,0
                                   |13,65,85,0.05129,25.4952,0,184,32.4,-5.4835167,36.17956,0,0,0,0,0,0,0,0,0,0,0,0,0""".stripMargin
      )
    )

  }

  test("must contain rows beyond 6269") {

    require(Files.exists(Paths.get(inputFile)), "Given fit file")
    require(!Files.exists(Paths.get(outputFile)), "Given there's no pre-existing converted file")

    val input: Array[Byte] = Files.readAllBytes(Paths.get(inputFile))
    val outputBytes        = fitConverter.FitToCsvByteArrayIO(input)

    val outputString = outputBytes.map(_.toChar).mkString

//    print(outputString)

    assert(
      outputString.contains("6269,65,121")
    )

    assert(
      outputString.contains("6270,65,121")
    )

  }

}
