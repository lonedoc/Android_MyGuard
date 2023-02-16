package kobramob.rubeg38.ru.myprotection.data.models

import com.google.gson.annotations.SerializedName

data class FacilityDto(
    @SerializedName("n_abs")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("status")
    val statusCode: String,
    @SerializedName("statuss")
    val statusDescription: String,
    @SerializedName("p")
    val perimeterOnly: Int?,
    @SerializedName("sam")
    val isSelfServiceEnabled: Int,
    @SerializedName("online")
    val hasOnlineChannel: Int,
    @SerializedName("onlineon")
    val isOnlineChannelEnabled: Int,
    @SerializedName("bohr")
    val isArmingDisabled: Int?,
    @SerializedName("tk")
    val isAlarmButtonEnabled: Int?,
    @SerializedName("akb")
    val batteryMalfunction: Int?,
    @SerializedName("220")
    val powerSupplyMalfunction: Int?,
    @SerializedName("password")
    val passcode: String?,
    @SerializedName("zay")
    val isApplicationsEnabled: Int?,
    @SerializedName("device")
    val devices: List<DeviceDto>?,
    @SerializedName("vvstate")
    val armState:String?,
    @SerializedName("vvsec")
    val armTime:Int?,

    // account 1
    @SerializedName("i1clic")
    val account1: String?,
    @SerializedName("i1csum")
    val monthlyPayment1: Double?,
    @SerializedName("company1")
    val guardServiceName1: String?,
    @SerializedName("company1_url")
    val paymentSystemUrl1: String?,

    // account 2
    @SerializedName("i1clic2")
    val account2: String?,
    @SerializedName("i1csum2")
    val monthlyPayment2: Double?,
    @SerializedName("company2")
    val guardServiceName2: String?,
    @SerializedName("company2_url")
    val paymentSystemUrl2: String?,

    // account 3
    @SerializedName("i1clic3")
    val account3: String?,
    @SerializedName("i1csum3")
    val monthlyPayment3: Double?,
    @SerializedName("company3")
    val guardServiceName3: String?,
    @SerializedName("company3_url")
    val paymentSystemUrl3: String?,
)