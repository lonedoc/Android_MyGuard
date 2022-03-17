package kobramob.rubeg38.ru.myprotection.feature.testmode.domain

import kobramob.rubeg38.ru.myprotection.feature.testmode.data.models.StartTestingResponseDto
import kobramob.rubeg38.ru.myprotection.feature.testmode.data.models.TestingStatusDto
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.models.StartTestingResult
import kobramob.rubeg38.ru.myprotection.feature.testmode.domain.models.TestingStatus

fun StartTestingResponseDto.toDomain() = StartTestingResult(
    isStarted = result == "ok",
    timeLeft = getTimeLeft(timeLeft)
)

fun TestingStatusDto.toDomain() = TestingStatus(
    isAlarmButtonPressed = isAlarmButtonPressed == 1,
    isAlarmButtonReinstalled = isAlarmButtonReinstalled == 1
)

private fun getTimeLeft(timeStr: String): Long {
    val parts = timeStr.split(':')
    val hours = parts[0].toLong()
    val minutes = parts[1].toLong()
    val seconds = parts[2].toLong()
    return hours * 3600 + minutes * 60 + seconds
}