package kobramob.rubeg38.ru.myprotection.feature.login.data.models

import com.google.gson.annotations.SerializedName

data class CitiesResponseDto(
    @SerializedName("\$c$")
    val command: String,
    @SerializedName("data")
    val citiesData: CitiesDataDto
)