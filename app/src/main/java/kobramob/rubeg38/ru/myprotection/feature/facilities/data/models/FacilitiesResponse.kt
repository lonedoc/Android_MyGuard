package kobramob.rubeg38.ru.myprotection.feature.facilities.data.models

import com.google.gson.annotations.SerializedName
import kobramob.rubeg38.ru.myprotection.data.models.FacilityDto

data class FacilitiesResponse(
    @SerializedName("\$c$")
    val client: String,
    @SerializedName("com")
    val command: String,
    @SerializedName("result")
    val result: String,
    @SerializedName("data")
    val facilities: List<FacilityDto>
)