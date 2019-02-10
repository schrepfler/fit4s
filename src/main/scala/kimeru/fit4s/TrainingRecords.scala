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

case class TrainingRecords(
    secs: Int,
    cad: Double,
    hr: Double,
    km: Double,
    kph: Double,
    nm: Double,
    watts: Double,
    alt: Double,
    lon: Double,
    lat: Double,
    headwind: Double,
    slope: Double,
    temp: Double,
    interval: Double,
    lrbalance: Double,
    lte: Double,
    rte: Double,
    lps: Double,
    rps: Double,
    smo2: Double,
    thb: Double,
    o2hb: Double,
    hhb: Double
)
