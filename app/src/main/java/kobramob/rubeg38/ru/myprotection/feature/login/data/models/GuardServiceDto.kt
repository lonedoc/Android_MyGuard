package kobramob.rubeg38.ru.myprotection.feature.login.data.models

import com.google.gson.annotations.SerializedName

data class GuardServiceDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("ip")
    val addresses: List<String>
)