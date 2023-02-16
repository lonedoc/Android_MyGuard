package kobramob.rubeg38.ru.myprotection.domain

import kobramob.rubeg38.ru.myprotection.data.models.DeviceDto
import kobramob.rubeg38.ru.myprotection.data.models.FacilityDto
import kobramob.rubeg38.ru.myprotection.data.models.LoginResponseDto
import kobramob.rubeg38.ru.myprotection.domain.models.*

fun LoginResponseDto.toDomain() = LoginResponse(
    token = token,
    user = User(
        id = userId,
        name = userName
    ),
    guardService = GuardService(
        name = "",
        displayedName = guardServiceName,
        phoneNumber = guardServicePhoneNumber,
        addresses = emptyList()
    )
)

fun FacilityDto.toDomain() = Facility(
    id = id,
    name = name,
    address = address,
    statusCodes = getStatusCodes(statusCode, perimeterOnly),
    statusDescription = statusDescription,
    isSelfServiceEnabled = isSelfServiceEnabled == 1,
    hasOnlineChannel = hasOnlineChannel == 1,
    isOnlineChannelEnabled = isOnlineChannelEnabled == 1,
    isArmingEnabled = isArmingDisabled != 1,
    isAlarmButtonEnabled = isAlarmButtonEnabled == 1,
    batteryMalfunction = batteryMalfunction == 1,
    powerSupplyMalfunction = powerSupplyMalfunction == 1,
    passcode = passcode,
    isApplicationsEnabled = isApplicationsEnabled == 1,
    accounts = parseAccounts(this),
    devices = devices?.map(DeviceDto::toDomain) ?: emptyList(),
    armState = armState,
    armTime = armTime
)

private fun getStatusCodes(statusCodeStr: String, perimeterOnly: Int?): List<StatusCode> =
    when (statusCodeStr) {
        "1" -> listOf(StatusCode.ALARM)
        "2" -> listOf(StatusCode.MALFUNCTION)
        "3" -> {
            val statusCodes = mutableListOf(StatusCode.GUARDED)

            if (perimeterOnly == 1) {
                statusCodes.add(StatusCode.PERIMETER_ONLY)
            }

            statusCodes
        }
        "4" -> listOf(StatusCode.NOT_GUARDED)
        "5" -> listOf(StatusCode.ALARM, StatusCode.WITH_HANDLING, StatusCode.NOT_GUARDED)
        "6" -> listOf(StatusCode.ALARM)
        "7" -> {
            val statusCodes = mutableListOf(StatusCode.ALARM)

            if (perimeterOnly == 1) {
                statusCodes.add(StatusCode.PERIMETER_ONLY)
            }

            statusCodes
        }
        "8" -> {
            val statusCodes = mutableListOf(StatusCode.ALARM)

            if (perimeterOnly == 1) {
                statusCodes.add(StatusCode.PERIMETER_ONLY)
            }

            statusCodes
        }
        "9" -> listOf(StatusCode.MALFUNCTION, StatusCode.NOT_GUARDED)
        "10" -> {
            val statusCodes = mutableListOf(StatusCode.MALFUNCTION, StatusCode.GUARDED)

            if (perimeterOnly == 1) {
                statusCodes.add(StatusCode.PERIMETER_ONLY)
            }

            statusCodes
        }
        else -> listOf(StatusCode.UNKNOWN)
    }

private fun parseAccounts(facilityDto: FacilityDto): List<Account> {
    val accounts = mutableListOf<Account>()

    if (facilityDto.account1 != null && facilityDto.paymentSystemUrl1 != null) {
        accounts.add(
            Account(
                id = facilityDto.account1,
                monthlyPayment = facilityDto.monthlyPayment1,
                guardServiceName = facilityDto.guardServiceName1,
                paymentSystemUrl = facilityDto.paymentSystemUrl1
            )
        )
    }

    if (facilityDto.account2 != null && facilityDto.paymentSystemUrl2 != null) {
        accounts.add(
            Account(
                id = facilityDto.account2,
                monthlyPayment = facilityDto.monthlyPayment2,
                guardServiceName = facilityDto.guardServiceName2,
                paymentSystemUrl = facilityDto.paymentSystemUrl2
            )
        )
    }

    if (facilityDto.account3 != null && facilityDto.paymentSystemUrl3 != null) {
        accounts.add(
            Account(
                id = facilityDto.account3,
                monthlyPayment = facilityDto.monthlyPayment3,
                guardServiceName = facilityDto.guardServiceName3,
                paymentSystemUrl = facilityDto.paymentSystemUrl3
            )
        )
    }

    return accounts
}

fun DeviceDto.toDomain(): Device {
    val deviceType = DeviceType.values().firstOrNull { it.id == type }
        ?: throw IllegalStateException()

    return when (deviceType) {
        DeviceType.CERBER_TEMPERATURE -> {
            if (value == null) {
                throw IllegalStateException()
            }

            CerberThermostat(
                deviceType = deviceType,
                number = number,
                deviceDescription = deviceDescription,
                isOnline = isOnline == 1,
                temperature = value
            )
        }
    }
}