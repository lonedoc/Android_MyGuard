package kobramob.rubeg38.ru.myprotection.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GuardService(
    val name: String,
    val displayedName: String?,
    val phoneNumber: String?,
    val addresses: List<String>
) : Parcelable