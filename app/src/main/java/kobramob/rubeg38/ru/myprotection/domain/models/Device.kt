package kobramob.rubeg38.ru.myprotection.domain.models

import android.os.Parcelable

abstract class Device(
    val deviceType: DeviceType,
    val number: Int,
    val deviceDescription: String
) : Parcelable
