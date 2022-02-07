package kobramob.rubeg38.ru.myprotection.feature.login.data.models

import com.google.gson.annotations.SerializedName

data class CitiesDataDto(
    @SerializedName("city")
    val cities: List<CityDto>
)