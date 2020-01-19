package vertxteste

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

fun bla() {
  val instant = Instant.now().toEpochMilli()
  println(Long.MAX_VALUE)
  println(Int.MAX_VALUE)
  println("Data e horário é: $instant")

  val localDate: LocalDate = LocalDate.now()
  println("A data é: $localDate")

  val localTime: LocalTime = LocalTime.now()
  println("horário: $localTime")


}
