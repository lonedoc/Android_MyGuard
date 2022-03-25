package kobramob.rubeg38.ru.myprotection.feature.facilities.ui

import kobramob.rubeg38.ru.myprotection.domain.models.Facility
import kobramob.rubeg38.ru.myprotection.domain.models.StatusCode

abstract class FacilityComparator : Comparator<Facility> {

    protected fun compareByAddress(lhs: Facility, rhs: Facility) = when {
        lhs.address < rhs.address -> -1
        lhs.address > rhs.address -> 1
        else -> null
    }

    protected fun compareByName(lhs: Facility, rhs: Facility) = when {
        lhs.name < rhs.name -> -1
        lhs.name > rhs.name -> 1
        else -> null
    }

    protected fun compareByStatus(lhs: Facility, rhs: Facility): Int? {
        val lhsImportance = getStatusImportance(lhs.statusCodes)
        val rhsImportance = getStatusImportance(rhs.statusCodes)

        return when {
            lhsImportance < rhsImportance -> 1
            lhsImportance > rhsImportance -> -1
            else -> null
        }
    }

    protected fun reverse(result: Int?): Int? {
        if (result == null) {
            return null
        }

        return result * -1
    }

    private fun getStatusImportance(statusCodes: List<StatusCode>) = when {
        statusCodes.contains(StatusCode.ALARM) && !statusCodes.contains(StatusCode.GUARDED) -> 6
        statusCodes.contains(StatusCode.ALARM) -> 5
        statusCodes.contains(StatusCode.MALFUNCTION) -> 4
        statusCodes.contains(StatusCode.NOT_GUARDED) -> 3
        statusCodes.contains(StatusCode.GUARDED) && statusCodes.contains(StatusCode.PERIMETER_ONLY) -> 1
        statusCodes.contains(StatusCode.GUARDED) -> 0
        else -> 2
    }

    object AddressFirstAscendingComparator : FacilityComparator() {
        override fun compare(lhs: Facility?, rhs: Facility?): Int {
            if (lhs == null) {
                return -1
            }

            if (rhs == null) {
                return 1
            }

            return compareByAddress(lhs, rhs) ?:
                compareByStatus(lhs, rhs) ?:
                compareByName(lhs, rhs) ?:
                0
        }
    }

    object AddressFirstDescendingComparator : FacilityComparator() {
        override fun compare(lhs: Facility?, rhs: Facility?): Int {
            if (lhs == null) {
                return -1
            }

            if (rhs == null) {
                return 1
            }

            return reverse(compareByAddress(lhs, rhs)) ?:
                compareByStatus(lhs, rhs) ?:
                compareByName(lhs, rhs) ?:
                0
        }
    }

    object NameFirstAscendingComparator : FacilityComparator() {
        override fun compare(lhs: Facility?, rhs: Facility?): Int {
            if (lhs == null) {
                return -1
            }

            if (rhs == null) {
                return 1
            }

            return compareByName(lhs, rhs) ?:
                compareByStatus(lhs, rhs) ?:
                compareByAddress(lhs, rhs) ?:
                0
        }
    }

    object NameFirstDescendingComparator : FacilityComparator() {
        override fun compare(lhs: Facility?, rhs: Facility?): Int {
            if (lhs == null) {
                return -1
            }

            if (rhs == null) {
                return 1
            }

            return reverse(compareByName(lhs, rhs)) ?:
                compareByStatus(lhs, rhs) ?:
                compareByAddress(lhs, rhs) ?:
                0
        }
    }

    object StatusFirstComparator : FacilityComparator() {
        override fun compare(lhs: Facility?, rhs: Facility?): Int {
            if (lhs == null) {
                return -1
            }

            if (rhs == null) {
                return 1
            }

            return compareByStatus(lhs, rhs) ?:
                compareByName(lhs, rhs) ?:
                compareByAddress(lhs, rhs) ?:
                0
        }
    }

}
