package com.example.data.model

enum class UserRole(val displayName: String, val roleKey: String) {
    ADMIN("Admin / CEO", "admin"),
    MARKETING_MANAGER("Marketing Manager", "marketing_manager"),
    STAFF("Staff Consultant", "staff"),
    COMPUTER_OPERATOR("Computer Operator", "computer_operator"),
    ACCOUNTS_MANAGER("Accounts Manager", "accounts_manager"),
    APPOINTMENT_MANAGE("Appointment Manager", "appointment_manage");

    companion object {
        fun fromKey(key: String): UserRole {
            return entries.find { it.roleKey.equals(key, ignoreCase = true) } ?: STAFF
        }
    }
}

data class AuthSession(
    val userId: String,
    val email: String,
    val username: String,
    val role: UserRole,
    val staffId: String?,
    val name: String,
    val organizationId: String = "11111111-1111-4111-8111-111111111111",
    val isActive: Boolean = true
)

data class StaffMember(
    val staffId: String, // Authoritative ID: S001, S002, S003, ...
    val userId: String,  // Supabase Auth UUID
    val username: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val deleted: Boolean = false,
    val leadEligible: Boolean = true,
    val receivingPaused: Boolean = false,
    val totalAssigned: Int = 0,
    val totalHandled: Int = 0,
    val totalMissed: Int = 0,
    val score: Double = 95.0,
    val lastAssignedAt: Long? = null
)

data class Lead(
    val id: String,
    val organizationId: String = "11111111-1111-4111-8111-111111111111",
    val name: String,
    val phone: String,
    val whatsapp: String,
    val passportNo: String,
    val destinationCountry: String,
    val serviceType: String,
    val staffReference: String, // e.g. "S002"
    val staffName: String,
    val assignedAt: Long = System.currentTimeMillis(),
    val assignmentDeadlineAt: Long = System.currentTimeMillis() + (5 * 60 * 1000), // 5-minute SLA
    val assignmentStatus: String = "PENDING", // PENDING, IN_PROGRESS, HANDLED, CONVERTED, MISSED
    val notes: String = "",
    val source: String = "Public Apply Form",
    val createdAt: Long = System.currentTimeMillis()
)

data class Client(
    val id: String,
    val organizationId: String = "11111111-1111-4111-8111-111111111111",
    val name: String,
    val phone: String,
    val whatsapp: String,
    val passportNo: String,
    val visaType: String,
    val destination: String,
    val staffReference: String, // Authoritative staff owner e.g. S002
    val status: String, // Documents Received, MOFA Submitted, Medical Done, Embassy Stamped, Flight Booked, Delivered
    val submissionId: String,
    val totalFee: Double,
    val paidAmount: Double,
    val balanceDue: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Appointment(
    val id: String,
    val clientName: String,
    val phone: String,
    val staffReference: String,
    val purpose: String,
    val date: String,
    val appointmentTime: String,
    val status: String = "SCHEDULED" // SCHEDULED, COMPLETED, CANCELLED
)

data class PassportRecord(
    val passportNo: String,
    val holderName: String,
    val country: String,
    val mofaNo: String,
    val bmetSubmissionId: String,
    val medicalStatus: String, // Passed, Pending, Fit, Unfit
    val visaStampingStatus: String, // Applied, Under Process, Stamped, Rejected
    val expiryDate: String,
    val updatedBy: String
)

data class PaymentRecord(
    val id: String,
    val clientName: String,
    val receiptNo: String,
    val amount: Double,
    val paymentMethod: String, // Cash, Bank Transfer, bKash/Nagad
    val purpose: String,
    val staffReference: String,
    val date: String,
    val status: String = "CONFIRMED"
)

data class AuditLog(
    val id: String,
    val action: String,
    val performedBy: String,
    val target: String,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String
)
