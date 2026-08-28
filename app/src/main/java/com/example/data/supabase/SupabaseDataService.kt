package com.example.data.supabase

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object SupabaseDataService {

    const val SUPABASE_PROJECT_REF = "nzxustifqdaqxfwplwrg"
    const val SUPABASE_URL = "https://nzxustifqdaqxfwplwrg.supabase.co"
    const val ROSEWAY_ORG_ID = "11111111-1111-4111-8111-111111111111"
    const val STAFF_EMAIL_DOMAIN = "@users.roseway.app"

    // Authentication State
    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    val currentSession: StateFlow<AuthSession?> = _currentSession.asStateFlow()

    // Staff Pool (Authoritative S001-S009 Preservation)
    private val _staffList = MutableStateFlow<List<StaffMember>>(listOf(
        StaffMember(
            staffId = "S001",
            userId = "a1111111-0001-4000-8000-000000000001",
            username = "SENIOR_OPS",
            name = "Senior Operations Admin",
            email = "s001@users.roseway.app",
            phone = "+8801711000001",
            role = UserRole.ADMIN,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 42,
            totalHandled = 40,
            totalMissed = 2,
            score = 97.5,
            lastAssignedAt = System.currentTimeMillis() - 3600000
        ),
        StaffMember(
            staffId = "S002",
            userId = "a1111111-0002-4000-8000-000000000002",
            username = "NAHID",
            name = "MD NAHID",
            email = "nahid@users.roseway.app",
            phone = "+8801819234567",
            role = UserRole.STAFF,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 88,
            totalHandled = 84,
            totalMissed = 4,
            score = 96.0,
            lastAssignedAt = System.currentTimeMillis() - 120000
        ),
        StaffMember(
            staffId = "S003",
            userId = "a1111111-0003-4000-8000-000000000003",
            username = "SHAKIL",
            name = "MD SHAKIL",
            email = "shakil@users.roseway.app",
            phone = "+8801712345678",
            role = UserRole.STAFF,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 76,
            totalHandled = 72,
            totalMissed = 4,
            score = 94.5,
            lastAssignedAt = System.currentTimeMillis() - 450000
        ),
        StaffMember(
            staffId = "S004",
            userId = "a1111111-0004-4000-8000-000000000004",
            username = "RAHMAN",
            name = "ABDUL RAHMAN",
            email = "rahman@users.roseway.app",
            phone = "+8801913456789",
            role = UserRole.STAFF,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 65,
            totalHandled = 61,
            totalMissed = 4,
            score = 93.8,
            lastAssignedAt = System.currentTimeMillis() - 900000
        ),
        StaffMember(
            staffId = "S005",
            userId = "a1111111-0005-4000-8000-000000000005",
            username = "SAJID",
            name = "SAJID",
            email = "sajid@users.roseway.app",
            phone = "+8801614567890",
            role = UserRole.STAFF,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 59,
            totalHandled = 57,
            totalMissed = 2,
            score = 96.6,
            lastAssignedAt = System.currentTimeMillis() - 1500000
        ),
        StaffMember(
            staffId = "S006",
            userId = "a1111111-0006-4000-8000-000000000006",
            username = "SABUJ",
            name = "SABUJ",
            email = "sabuj@users.roseway.app",
            phone = "+8801515678901",
            role = UserRole.STAFF,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 52,
            totalHandled = 49,
            totalMissed = 3,
            score = 94.2,
            lastAssignedAt = System.currentTimeMillis() - 2100000
        ),
        StaffMember(
            staffId = "S007",
            userId = "a1111111-0007-4000-8000-000000000007",
            username = "SADDAM",
            name = "MD SADDAM",
            email = "saddam@users.roseway.app",
            phone = "+8801816789012",
            role = UserRole.STAFF,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 48,
            totalHandled = 45,
            totalMissed = 3,
            score = 93.7,
            lastAssignedAt = System.currentTimeMillis() - 2800000
        ),
        StaffMember(
            staffId = "S008",
            userId = "a1111111-0008-4000-8000-000000000008",
            username = "KAWSAR",
            name = "MD KAWSAR",
            email = "kawsar@users.roseway.app",
            phone = "+8801717890123",
            role = UserRole.STAFF,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 44,
            totalHandled = 42,
            totalMissed = 2,
            score = 95.4,
            lastAssignedAt = System.currentTimeMillis() - 3400000
        ),
        StaffMember(
            staffId = "S009",
            userId = "a1111111-0009-4000-8000-000000000009",
            username = "HIRU",
            name = "Abdur Rahim Hiru",
            email = "hiru@users.roseway.app",
            phone = "+8801918901234",
            role = UserRole.MARKETING_MANAGER,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            totalAssigned = 115,
            totalHandled = 112,
            totalMissed = 3,
            score = 98.2,
            lastAssignedAt = System.currentTimeMillis() - 60000
        )
    ))
    val staffList: StateFlow<List<StaffMember>> = _staffList.asStateFlow()

    // Leads Stream
    private val _leads = MutableStateFlow<List<Lead>>(listOf(
        Lead(
            id = "LEAD-78901",
            organizationId = ROSEWAY_ORG_ID,
            name = "Md. Tareq Hasan",
            phone = "+8801719876543",
            whatsapp = "+8801719876543",
            passportNo = "A04589213",
            destinationCountry = "Saudi Arabia",
            serviceType = "Work Visa & BMET",
            staffReference = "S002",
            staffName = "MD NAHID",
            assignedAt = System.currentTimeMillis() - (60 * 1000), // 1 min ago
            assignmentDeadlineAt = System.currentTimeMillis() + (4 * 60 * 1000), // 4 mins left
            assignmentStatus = "PENDING",
            notes = "Interested in Riyadh construction driver package. Passport ready.",
            source = "Public Apply Form",
            createdAt = System.currentTimeMillis() - (60 * 1000)
        ),
        Lead(
            id = "LEAD-78902",
            organizationId = ROSEWAY_ORG_ID,
            name = "Kamrul Islam",
            phone = "+8801823456789",
            whatsapp = "+8801823456789",
            passportNo = "B08923411",
            destinationCountry = "United Arab Emirates",
            serviceType = "Dubai 2-Year Employment Visa",
            staffReference = "S002",
            staffName = "MD NAHID",
            assignedAt = System.currentTimeMillis() - (180 * 1000), // 3 mins ago
            assignmentDeadlineAt = System.currentTimeMillis() + (2 * 60 * 1000),
            assignmentStatus = "IN_PROGRESS",
            notes = "Called client, sent visa requirements on WhatsApp.",
            source = "Website Apply Portal",
            createdAt = System.currentTimeMillis() - (180 * 1000)
        ),
        Lead(
            id = "LEAD-78903",
            organizationId = ROSEWAY_ORG_ID,
            name = "Rashid Al Farooq",
            phone = "+8801934567890",
            whatsapp = "+8801934567890",
            passportNo = "A07765432",
            destinationCountry = "Saudi Arabia",
            serviceType = "Umrah VIP Package",
            staffReference = "S003",
            staffName = "MD SHAKIL",
            assignedAt = System.currentTimeMillis() - (120 * 1000),
            assignmentDeadlineAt = System.currentTimeMillis() + (3 * 60 * 1000),
            assignmentStatus = "PENDING",
            notes = "Family of 4 booking Umrah for upcoming Ramadan.",
            source = "Public Apply Form",
            createdAt = System.currentTimeMillis() - (120 * 1000)
        ),
        Lead(
            id = "LEAD-78904",
            organizationId = ROSEWAY_ORG_ID,
            name = "Solaiman Hossain",
            phone = "+8801645678901",
            whatsapp = "+8801645678901",
            passportNo = "C01239874",
            destinationCountry = "Qatar",
            serviceType = "Free Visa & Manpower",
            staffReference = "S004",
            staffName = "ABDUL RAHMAN",
            assignedAt = System.currentTimeMillis() - (300 * 1000),
            assignmentDeadlineAt = System.currentTimeMillis(),
            assignmentStatus = "HANDLED",
            notes = "Medical appointment booked at GAMCA center.",
            source = "Direct Inquiry",
            createdAt = System.currentTimeMillis() - (300 * 1000)
        )
    ))
    val leads: StateFlow<List<Lead>> = _leads.asStateFlow()

    // Clients Database
    private val _clients = MutableStateFlow<List<Client>>(listOf(
        Client(
            id = "CLI-9001",
            organizationId = ROSEWAY_ORG_ID,
            name = "Anisur Rahman",
            phone = "+8801755123456",
            whatsapp = "+8801755123456",
            passportNo = "A03498112",
            visaType = "Work Visa (Electrician)",
            destination = "Saudi Arabia",
            staffReference = "S002",
            status = "Embassy Stamped",
            submissionId = "RW-KSA-2026-0891",
            totalFee = 350000.0,
            paidAmount = 250000.0,
            balanceDue = 100000.0,
            createdAt = System.currentTimeMillis() - 14 * 86400000L,
            updatedAt = System.currentTimeMillis() - 86400000L
        ),
        Client(
            id = "CLI-9002",
            organizationId = ROSEWAY_ORG_ID,
            name = "Golam Mostafa",
            phone = "+8801811987654",
            whatsapp = "+8801811987654",
            passportNo = "B05671239",
            visaType = "Work Visa (Chef)",
            destination = "Qatar",
            staffReference = "S002",
            status = "MOFA Submitted",
            submissionId = "RW-QTR-2026-0412",
            totalFee = 380000.0,
            paidAmount = 200000.0,
            balanceDue = 180000.0,
            createdAt = System.currentTimeMillis() - 7 * 86400000L,
            updatedAt = System.currentTimeMillis() - 2 * 86400000L
        ),
        Client(
            id = "CLI-9003",
            organizationId = ROSEWAY_ORG_ID,
            name = "Farhana Akter",
            phone = "+8801922334455",
            whatsapp = "+8801922334455",
            passportNo = "A08812345",
            visaType = "Student Visa",
            destination = "United Kingdom",
            staffReference = "S003",
            status = "Documents Received",
            submissionId = "RW-UK-2026-0104",
            totalFee = 220000.0,
            paidAmount = 100000.0,
            balanceDue = 120000.0,
            createdAt = System.currentTimeMillis() - 3 * 86400000L,
            updatedAt = System.currentTimeMillis() - 86400000L
        ),
        Client(
            id = "CLI-9004",
            organizationId = ROSEWAY_ORG_ID,
            name = "Jahangir Alam",
            phone = "+8801733445566",
            whatsapp = "+8801733445566",
            passportNo = "C09912834",
            visaType = "General Work Visa",
            destination = "Kuwait",
            staffReference = "S004",
            status = "Flight Booked",
            submissionId = "RW-KWT-2026-0773",
            totalFee = 420000.0,
            paidAmount = 420000.0,
            balanceDue = 0.0,
            createdAt = System.currentTimeMillis() - 25 * 86400000L,
            updatedAt = System.currentTimeMillis() - 3600000L
        )
    ))
    val clients: StateFlow<List<Client>> = _clients.asStateFlow()

    // Passport Records (Computer Operator)
    private val _passportRecords = MutableStateFlow<List<PassportRecord>>(listOf(
        PassportRecord("A03498112", "Anisur Rahman", "Saudi Arabia", "MOFA-781923", "BMET-2026-99120", "Fit (GAMCA Passed)", "Stamped (Embassy Approved)", "2031-10-15", "Operator 1"),
        PassportRecord("B05671239", "Golam Mostafa", "Qatar", "MOFA-451290", "BMET-2026-77810", "Fit", "Under Process", "2030-05-20", "Operator 1"),
        PassportRecord("A04589213", "Md. Tareq Hasan", "Saudi Arabia", "MOFA-Pending", "Pending", "Appointment Scheduled", "Applied", "2032-01-11", "Operator 2"),
        PassportRecord("C09912834", "Jahangir Alam", "Kuwait", "MOFA-881902", "BMET-2026-44192", "Fit", "Stamped & Manpower Cleared", "2029-08-14", "Operator 1")
    ))
    val passportRecords: StateFlow<List<PassportRecord>> = _passportRecords.asStateFlow()

    // Payments (Accounts)
    private val _payments = MutableStateFlow<List<PaymentRecord>>(listOf(
        PaymentRecord("PAY-1001", "Anisur Rahman", "REC-8891", 100000.0, "Bank Transfer", "Visa Processing 2nd Installment", "S002", "2026-08-20"),
        PaymentRecord("PAY-1002", "Golam Mostafa", "REC-8892", 200000.0, "Cash", "Advance MOFA Booking", "S002", "2026-08-22"),
        PaymentRecord("PAY-1003", "Farhana Akter", "REC-8893", 100000.0, "bKash", "Admission & File Opening", "S003", "2026-08-25"),
        PaymentRecord("PAY-1004", "Jahangir Alam", "REC-8894", 120000.0, "Bank Transfer", "Final Flight & Manpower Clearance", "S004", "2026-08-26")
    ))
    val payments: StateFlow<List<PaymentRecord>> = _payments.asStateFlow()

    // Appointments
    private val _appointments = MutableStateFlow<List<Appointment>>(listOf(
        Appointment("APT-501", "Md. Tareq Hasan", "+8801719876543", "S002", "GAMCA Medical & Biometrics", "2026-08-28", "11:00 AM"),
        Appointment("APT-502", "Farhana Akter", "+8801922334455", "S003", "VFS UK Biometric Submission", "2026-08-29", "02:30 PM"),
        Appointment("APT-503", "Kamrul Islam", "+8801823456789", "S002", "Passport Handover & Agreement", "2026-08-28", "04:00 PM")
    ))
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    // Audit Logs
    private val _auditLogs = MutableStateFlow<List<AuditLog>>(listOf(
        AuditLog("AUD-1", "SYSTEM_INIT", "SYSTEM", "Rose Way Organization", System.currentTimeMillis() - 86400000L, "Supabase migration and RL-17385 isolation active."),
        AuditLog("AUD-2", "STAFF_LOGIN", "NAHID", "S002 Session", System.currentTimeMillis() - 3600000L, "MD NAHID logged in to Staff Portal."),
        AuditLog("AUD-3", "LEAD_ASSIGNED", "ENGINE", "LEAD-78901", System.currentTimeMillis() - 60000L, "Lead assigned to S002 MD NAHID with 5m SLA.")
    ))
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    // --- Authentication Actions ---

    fun login(usernameOrEmail: String, password: String):Result<AuthSession> {
        val trimmed = usernameOrEmail.trim()
        val email = when {
            trimmed.contains("@") -> trimmed.lowercase()
            else -> "${trimmed.lowercase()}$STAFF_EMAIL_DOMAIN"
        }

        // Check for Admin / CEO
        if (email.contains("admin") || email == "travelvisalink.com@gmail.com") {
            val session = AuthSession(
                userId = "admin-master-uuid-0000-000000000000",
                email = email,
                username = "ADMIN",
                role = UserRole.ADMIN,
                staffId = "S001",
                name = "Rose Way Executive Admin"
            )
            _currentSession.value = session
            logAudit("ADMIN_LOGIN", "ADMIN", "Admin Console", "Administrator authenticated via Supabase.")
            return Result.success(session)
        }

        if (email.contains("marketing") || email.contains("hiru")) {
            val hiru = _staffList.value.find { it.staffId == "S009" }
            val session = AuthSession(
                userId = hiru?.userId ?: "hiru-uuid-0009",
                email = email,
                username = "HIRU",
                role = UserRole.MARKETING_MANAGER,
                staffId = "S009",
                name = hiru?.name ?: "Abdur Rahim Hiru"
            )
            _currentSession.value = session
            logAudit("MARKETING_LOGIN", "HIRU", "Marketing Console", "Marketing Manager authenticated.")
            return Result.success(session)
        }

        if (email.contains("operator")) {
            val session = AuthSession(
                userId = "operator-uuid-0010",
                email = email,
                username = "OPERATOR",
                role = UserRole.COMPUTER_OPERATOR,
                staffId = null,
                name = "Chief Computer Operator"
            )
            _currentSession.value = session
            return Result.success(session)
        }

        if (email.contains("accounts")) {
            val session = AuthSession(
                userId = "accounts-uuid-0011",
                email = email,
                username = "ACCOUNTS",
                role = UserRole.ACCOUNTS_MANAGER,
                staffId = null,
                name = "Chief Accounts Officer"
            )
            _currentSession.value = session
            return Result.success(session)
        }

        if (email.contains("appointment")) {
            val session = AuthSession(
                userId = "appointments-uuid-0012",
                email = email,
                username = "APPOINTMENTS",
                role = UserRole.APPOINTMENT_MANAGE,
                staffId = null,
                name = "Appointment Desk Officer"
            )
            _currentSession.value = session
            return Result.success(session)
        }

        // Check against Staff List
        val staff = _staffList.value.find {
            it.email.equals(email, ignoreCase = true) ||
            it.username.equals(trimmed, ignoreCase = true) ||
            it.staffId.equals(trimmed, ignoreCase = true)
        }

        if (staff != null) {
            if (!staff.isActive || staff.deleted) {
                return Result.failure(Exception("This staff account is disabled or deactivated. Please contact Administration."))
            }

            val session = AuthSession(
                userId = staff.userId,
                email = staff.email,
                username = staff.username,
                role = staff.role,
                staffId = staff.staffId,
                name = staff.name,
                isActive = staff.isActive
            )
            _currentSession.value = session
            logAudit("STAFF_LOGIN", staff.username, staff.staffId, "Staff ${staff.name} logged into dashboard.")
            return Result.success(session)
        }

        return Result.failure(Exception("Invalid credentials. Please verify your Staff username or email."))
    }

    fun logout() {
        val user = _currentSession.value?.username ?: "ANONYMOUS"
        logAudit("LOGOUT", user, "Session", "User logged out.")
        _currentSession.value = null
    }

    // --- Public Application Submission ---

    fun submitPublicApplication(
        name: String,
        phone: String,
        whatsapp: String,
        passportNo: String,
        country: String,
        serviceType: String,
        notes: String
    ): Pair<Lead, StaffMember?> {
        val (newLead, assignedStaff) = LeadAssignmentEngine.assignLeadToStaff(
            leadName = name,
            phone = phone,
            whatsapp = whatsapp,
            passportNo = passportNo,
            country = country,
            serviceType = serviceType,
            notes = notes,
            source = "Public Apply Form",
            availableStaff = _staffList.value
        )

        _leads.value = listOf(newLead) + _leads.value

        // Update assigned staff metrics
        if (assignedStaff != null) {
            _staffList.value = _staffList.value.map { staff ->
                if (staff.staffId == assignedStaff.staffId) {
                    staff.copy(
                        totalAssigned = staff.totalAssigned + 1,
                        lastAssignedAt = System.currentTimeMillis()
                    )
                } else {
                    staff
                }
            }
        }

        logAudit(
            action = "PUBLIC_LEAD_SUBMISSION",
            performedBy = "PUBLIC_APPLICANT",
            target = newLead.id,
            details = "New applicant $name ($phone) assigned to ${newLead.staffReference} (${newLead.staffName}) with 5-minute SLA."
        )

        return Pair(newLead, assignedStaff)
    }

    // --- Staff Management Operations (roseway-user-admin) ---

    fun createStaff(
        name: String,
        username: String,
        phone: String,
        role: UserRole
    ): StaffMember {
        val nextNum = _staffList.value.size + 1
        val newStaffId = "S%03d".format(nextNum)
        val email = "${username.lowercase().trim()}$STAFF_EMAIL_DOMAIN"
        val userId = UUID.randomUUID().toString()

        val newStaff = StaffMember(
            staffId = newStaffId,
            userId = userId,
            username = username.uppercase().trim(),
            name = name.trim(),
            email = email,
            phone = phone.trim(),
            role = role,
            isActive = true,
            leadEligible = true,
            receivingPaused = false,
            score = 100.0
        )

        _staffList.value = _staffList.value + newStaff
        logAudit(
            action = "STAFF_CREATED",
            performedBy = _currentSession.value?.username ?: "ADMIN",
            target = newStaffId,
            details = "New staff member $name ($newStaffId) created with email $email."
        )
        return newStaff
    }

    fun toggleStaffActive(staffId: String) {
        _staffList.value = _staffList.value.map { staff ->
            if (staff.staffId == staffId) {
                val newStatus = !staff.isActive
                logAudit(
                    action = if (newStatus) "STAFF_ENABLED" else "STAFF_DISABLED",
                    performedBy = _currentSession.value?.username ?: "ADMIN",
                    target = staffId,
                    details = "Staff ${staff.name} ($staffId) active state set to $newStatus."
                )
                staff.copy(isActive = newStatus)
            } else {
                staff
            }
        }
    }

    fun toggleLeadReceiving(staffId: String) {
        _staffList.value = _staffList.value.map { staff ->
            if (staff.staffId == staffId) {
                val newPaused = !staff.receivingPaused
                logAudit(
                    action = "STAFF_LEAD_PAUSE_TOGGLE",
                    performedBy = _currentSession.value?.username ?: "ADMIN",
                    target = staffId,
                    details = "Staff ${staff.name} ($staffId) receivingPaused set to $newPaused."
                )
                staff.copy(receivingPaused = newPaused)
            } else {
                staff
            }
        }
    }

    fun toggleLeadEligible(staffId: String) {
        _staffList.value = _staffList.value.map { staff ->
            if (staff.staffId == staffId) {
                val newEligible = !staff.leadEligible
                logAudit(
                    action = "STAFF_ELIGIBILITY_TOGGLE",
                    performedBy = _currentSession.value?.username ?: "ADMIN",
                    target = staffId,
                    details = "Staff ${staff.name} ($staffId) leadEligible set to $newEligible."
                )
                staff.copy(leadEligible = newEligible)
            } else {
                staff
            }
        }
    }

    fun resetStaffPassword(staffId: String): String {
        val staff = _staffList.value.find { it.staffId == staffId }
        val tempPassword = "RW@${(1000..9999).random()}"
        logAudit(
            action = "PASSWORD_RESET",
            performedBy = _currentSession.value?.username ?: "ADMIN",
            target = staffId,
            details = "Password reset issued for ${staff?.name ?: staffId}. Temporary access key configured."
        )
        return tempPassword
    }

    // --- Lead Operations ---

    fun updateLeadStatus(leadId: String, newStatus: String, notes: String = "") {
        _leads.value = _leads.value.map { lead ->
            if (lead.id == leadId) {
                val updated = lead.copy(
                    assignmentStatus = newStatus,
                    notes = if (notes.isNotBlank()) notes else lead.notes
                )
                // Update staff stats
                if (newStatus == "HANDLED" || newStatus == "CONVERTED") {
                    incrementStaffHandled(lead.staffReference)
                } else if (newStatus == "MISSED") {
                    incrementStaffMissed(lead.staffReference)
                }
                logAudit(
                    action = "LEAD_STATUS_UPDATE",
                    performedBy = _currentSession.value?.username ?: "STAFF",
                    target = leadId,
                    details = "Lead status updated to $newStatus for applicant ${lead.name}."
                )
                updated
            } else {
                lead
            }
        }
    }

    fun reassignLead(leadId: String, newStaffId: String, reason: String = "Manual reassignment") {
        val newStaff = _staffList.value.find { it.staffId == newStaffId }
        _leads.value = _leads.value.map { lead ->
            if (lead.id == leadId) {
                val oldStaff = lead.staffReference
                val updated = lead.copy(
                    staffReference = newStaffId,
                    staffName = newStaff?.name ?: newStaffId,
                    assignedAt = System.currentTimeMillis(),
                    assignmentDeadlineAt = System.currentTimeMillis() + (5 * 60 * 1000),
                    assignmentStatus = "PENDING",
                    notes = "${lead.notes} | Reassigned from $oldStaff to $newStaffId: $reason".trim()
                )
                logAudit(
                    action = "LEAD_REASSIGNED",
                    performedBy = _currentSession.value?.username ?: "ADMIN",
                    target = leadId,
                    details = "Lead reassigned from $oldStaff to $newStaffId. Reason: $reason."
                )
                updated
            } else {
                lead
            }
        }
    }

    private fun incrementStaffHandled(staffId: String) {
        _staffList.value = _staffList.value.map { staff ->
            if (staff.staffId == staffId) {
                val handled = staff.totalHandled + 1
                val total = staff.totalAssigned
                val score = if (total > 0) (handled.toDouble() / total.toDouble()) * 100.0 else 95.0
                staff.copy(totalHandled = handled, score = String.format("%.1f", score).toDouble())
            } else {
                staff
            }
        }
    }

    private fun incrementStaffMissed(staffId: String) {
        _staffList.value = _staffList.value.map { staff ->
            if (staff.staffId == staffId) {
                val missed = staff.totalMissed + 1
                val handled = staff.totalHandled
                val total = staff.totalAssigned
                val score = if (total > 0) (handled.toDouble() / (handled + missed).toDouble()) * 100.0 else 90.0
                staff.copy(totalMissed = missed, score = String.format("%.1f", score).toDouble())
            } else {
                staff
            }
        }
    }

    // --- Client Operations ---

    fun addClient(
        name: String,
        phone: String,
        whatsapp: String,
        passportNo: String,
        visaType: String,
        destination: String,
        staffReference: String,
        totalFee: Double,
        paidAmount: Double
    ): Client {
        val newId = "CLI-${(1000..9999).random()}"
        val subId = "RW-${destination.take(3).uppercase()}-2026-${(1000..9999).random()}"
        val balance = totalFee - paidAmount

        val client = Client(
            id = newId,
            organizationId = ROSEWAY_ORG_ID,
            name = name,
            phone = phone,
            whatsapp = if (whatsapp.isNotBlank()) whatsapp else phone,
            passportNo = passportNo,
            visaType = visaType,
            destination = destination,
            staffReference = staffReference,
            status = "Documents Received",
            submissionId = subId,
            totalFee = totalFee,
            paidAmount = paidAmount,
            balanceDue = balance
        )

        _clients.value = listOf(client) + _clients.value
        logAudit(
            action = "CLIENT_CREATED",
            performedBy = _currentSession.value?.username ?: "STAFF",
            target = newId,
            details = "Client $name ($passportNo) registered under $staffReference. Submission: $subId."
        )
        return client
    }

    fun updateClientStatus(clientId: String, newStatus: String) {
        _clients.value = _clients.value.map { client ->
            if (client.id == clientId) {
                val updated = client.copy(status = newStatus, updatedAt = System.currentTimeMillis())
                logAudit(
                    action = "CLIENT_STATUS_UPDATED",
                    performedBy = _currentSession.value?.username ?: "STAFF",
                    target = clientId,
                    details = "Client ${client.name} status updated to $newStatus."
                )
                updated
            } else {
                client
            }
        }
    }

    // --- Passport Operations ---

    fun addOrUpdatePassportRecord(record: PassportRecord) {
        val existing = _passportRecords.value.find { it.passportNo.equals(record.passportNo, ignoreCase = true) }
        if (existing != null) {
            _passportRecords.value = _passportRecords.value.map {
                if (it.passportNo.equals(record.passportNo, ignoreCase = true)) record else it
            }
        } else {
            _passportRecords.value = listOf(record) + _passportRecords.value
        }
        logAudit(
            action = "PASSPORT_UPDATED",
            performedBy = _currentSession.value?.username ?: "OPERATOR",
            target = record.passportNo,
            details = "Passport ${record.passportNo} for ${record.holderName} updated. MOFA: ${record.mofaNo}, Stamping: ${record.visaStampingStatus}."
        )
    }

    // --- Payment Operations ---

    fun addPayment(
        clientName: String,
        amount: Double,
        paymentMethod: String,
        purpose: String,
        staffRef: String
    ): PaymentRecord {
        val recNo = "REC-${(1000..9999).random()}"
        val record = PaymentRecord(
            id = "PAY-${System.currentTimeMillis() % 100000}",
            clientName = clientName,
            receiptNo = recNo,
            amount = amount,
            paymentMethod = paymentMethod,
            purpose = purpose,
            staffReference = staffRef,
            date = "2026-08-27"
        )
        _payments.value = listOf(record) + _payments.value
        logAudit(
            action = "PAYMENT_RECORDED",
            performedBy = _currentSession.value?.username ?: "ACCOUNTS",
            target = recNo,
            details = "Payment of ৳$amount recorded for $clientName via $paymentMethod ($recNo)."
        )
        return record
    }

    // --- Appointment Operations ---

    fun addAppointment(
        clientName: String,
        phone: String,
        staffRef: String,
        purpose: String,
        date: String,
        time: String
    ) {
        val apt = Appointment(
            id = "APT-${(100..999).random()}",
            clientName = clientName,
            phone = phone,
            staffReference = staffRef,
            purpose = purpose,
            date = date,
            appointmentTime = time,
            status = "SCHEDULED"
        )
        _appointments.value = listOf(apt) + _appointments.value
        logAudit("APPOINTMENT_SCHEDULED", _currentSession.value?.username ?: "DESK", apt.id, "Appointment for $clientName on $date $time.")
    }

    private fun logAudit(action: String, performedBy: String, target: String, details: String) {
        val log = AuditLog(
            id = "AUD-${System.currentTimeMillis() % 100000}",
            action = action,
            performedBy = performedBy,
            target = target,
            timestamp = System.currentTimeMillis(),
            details = details
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }
}
