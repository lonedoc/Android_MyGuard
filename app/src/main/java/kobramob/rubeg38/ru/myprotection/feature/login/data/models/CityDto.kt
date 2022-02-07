package kobramob.rubeg38.ru.myprotection.feature.login.data.models

import com.google.gson.annotations.SerializedName

data class CityDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("pr")
    val guardServices: List<GuardServiceDto>
)