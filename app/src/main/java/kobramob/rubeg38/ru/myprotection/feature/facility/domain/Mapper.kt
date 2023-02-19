package kobramob.rubeg38.ru.myprotection.feature.facility.domain

import kobramob.rubeg38.ru.myprotection.feature.facility.data.models.ResultDto
import kobramob.rubeg38.ru.myprotection.feature.facility.data.models.RenamingResultDto
import kobramob.rubeg38.ru.myprotection.feature.facility.domain.models.RenamingResult

fun RenamingResultDto.toDomain() = RenamingResult(
    success = result == "ok",
    name = name
)

fun ResultDto.toDomain() = result == "ok" || result == "start"