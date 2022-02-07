package kobramob.rubeg38.ru.myprotection.data.models

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("\$c$")
    val command: String,
    @SerializedName("usern_abs")
    val userId: Int,
    @SerializedName("guid")
    val token: String,
    @SerializedName("name")
    val userName: String,
    @SerializedName("factory")
    val guardServiceName: String,
    @SerializedName("factorytel")
    val guardServicePhoneNumber: String
)