package kobramob.rubeg38.ru.myprotection.feature.applications.data.models

import com.google.gson.annotations.SerializedName

data class PredefinedApplication( // TODO: Rename
    @SerializedName("n_abs")
    val id: Int,
    @SerializedName("desc")
    val description: String
)