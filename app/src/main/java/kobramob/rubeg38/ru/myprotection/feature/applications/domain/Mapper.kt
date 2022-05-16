package kobramob.rubeg38.ru.myprotection.feature.applications.domain

import kobramob.rubeg38.ru.myprotection.feature.applications.data.models.ApplicationResult

fun ApplicationResult.toDomain() = this.result == "ok"