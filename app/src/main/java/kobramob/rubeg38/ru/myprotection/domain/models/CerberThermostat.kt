package kobramob.rubeg38.ru.myprotection.domain.models

import android.os.Parcel
import android.os.Parcelable

class CerberThermostat(
    deviceType: DeviceType,
    number: Int,
    deviceDescription: String,
    val isOnline: Boolean,
    val temperature: Double
) : Device(deviceType, number, deviceDescription) {

    companion object CREATOR : Parcelable.Creator<CerberThermostat> {
        override fun createFromParcel(parcel: Parcel): CerberThermostat {
            return CerberThermostat(parcel)
        }

        override fun newArray(size: Int): Array<CerberThermostat?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        deviceType = DeviceType.values().first { it.id == parcel.readString() },
        number = parcel.readInt(),
        deviceDescription = parcel.readString() ?: "",
        isOnline = parcel.readByte() != 0.toByte(),
        temperature = parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceType.id)
        parcel.writeInt(number)
        parcel.writeString(deviceDescription)
        parcel.writeByte(if (isOnline) 1 else 0)
        parcel.writeDouble(temperature)
    }

    override fun describeContents(): Int = 0

}
