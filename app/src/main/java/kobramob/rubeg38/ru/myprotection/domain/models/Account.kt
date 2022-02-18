package kobramob.rubeg38.ru.myprotection.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    val id: String,
    val monthlyPayment: Double?,
    val guardServiceName: String?,
    val paymentSystemUrl: String
) : Parcelable