package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.supabase.SupabaseDataService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenRoute {
    data object MasterCommand : ScreenRoute()
    data object PublicHome : ScreenRoute()
    data object PublicApply : ScreenRoute()
    data object Login : ScreenRoute()
    data object AdminDashboard : ScreenRoute()
    data object StaffDashboard : ScreenRoute()
    data object MarketingDashboard : ScreenRoute()
    data object ComputerOperatorDashboard : ScreenRoute()
    data object AccountsDashboard : ScreenRoute()
    data object AppointmentsDashboard : ScreenRoute()
}

class RoseWayViewModel : ViewModel() {

    private val _currentRoute = MutableStateFlow<ScreenRoute>(ScreenRoute.MasterCommand)
    val currentRoute: StateFlow<ScreenRoute> = _currentRoute.asStateFlow()

    val currentSession: StateFlow<AuthSession?> = SupabaseDataService.currentSession
    val staffList: StateFlow<List<StaffMember>> = SupabaseDataService.staffList
    val allLeads: StateFlow<List<Lead>> = SupabaseDataService.leads
    val allClients: StateFlow<List<Client>> = SupabaseDataService.clients
    val passportRecords: StateFlow<List<PassportRecord>> = SupabaseDataService.passportRecords
    val payments: StateFlow<List<PaymentRecord>> = SupabaseDataService.payments
    val appointments: StateFlow<List<Appointment>> = SupabaseDataService.appointments
    val auditLogs: StateFlow<List<AuditLog>> = SupabaseDataService.auditLogs

    // Current Time in millis updated every second for real-time 5-minute SLA countdowns
    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    // Status Message / Toast
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Public Application Result Dialog State
    private val _lastSubmittedLead = MutableStateFlow<Pair<Lead, StaffMember?>?>(null)
    val lastSubmittedLead: StateFlow<Pair<Lead, StaffMember?>?> = _lastSubmittedLead.asStateFlow()

    init {
        // SLA Timer countdown loop
        viewModelScope.launch {
            while (true) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    fun navigateTo(route: ScreenRoute) {
        _currentRoute.value = route
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearSubmittedLead() {
        _lastSubmittedLead.value = null
    }

    // --- Authentication ---

    fun login(usernameOrEmail: String, pass: String): Boolean {
        val result = SupabaseDataService.login(usernameOrEmail, pass)
        return if (result.isSuccess) {
            val session = result.getOrNull()!!
            showToast("Welcome, ${session.name} (${session.role.displayName})")
            // Route to corresponding role dashboard
            when (session.role) {
                UserRole.ADMIN -> _currentRoute.value = ScreenRoute.AdminDashboard
                UserRole.MARKETING_MANAGER -> _currentRoute.value = ScreenRoute.MarketingDashboard
                UserRole.STAFF -> _currentRoute.value = ScreenRoute.StaffDashboard
                UserRole.COMPUTER_OPERATOR -> _currentRoute.value = ScreenRoute.ComputerOperatorDashboard
                UserRole.ACCOUNTS_MANAGER -> _currentRoute.value = ScreenRoute.AccountsDashboard
                UserRole.APPOINTMENT_MANAGE -> _currentRoute.value = ScreenRoute.AppointmentsDashboard
            }
            true
        } else {
            showToast(result.exceptionOrNull()?.message ?: "Login failed")
            false
        }
    }

    fun logout() {
        SupabaseDataService.logout()
        showToast("Logged out successfully.")
        _currentRoute.value = ScreenRoute.PublicHome
    }

    // --- Public Lead Submission ---

    fun submitPublicApplication(
        name: String,
        phone: String,
        whatsapp: String,
        passportNo: String,
        country: String,
        serviceType: String,
        notes: String
    ) {
        val result = SupabaseDataService.submitPublicApplication(
            name = name,
            phone = phone,
            whatsapp = whatsapp,
            passportNo = passportNo,
            country = country,
            serviceType = serviceType,
            notes = notes
        )
        _lastSubmittedLead.value = result
        showToast("Application submitted! Assigned to ${result.second?.name ?: "Support Desk"}")
    }

    // --- Admin / Staff Management ---

    fun createStaff(name: String, username: String, phone: String, role: UserRole) {
        val newStaff = SupabaseDataService.createStaff(name, username, phone, role)
        showToast("Staff ${newStaff.name} created with ID ${newStaff.staffId}")
    }

    fun toggleStaffActive(staffId: String) {
        SupabaseDataService.toggleStaffActive(staffId)
        showToast("Staff $staffId status updated")
    }

    fun toggleLeadReceiving(staffId: String) {
        SupabaseDataService.toggleLeadReceiving(staffId)
        showToast("Staff $staffId lead receiving updated")
    }

    fun toggleLeadEligible(staffId: String) {
        SupabaseDataService.toggleLeadEligible(staffId)
        showToast("Staff $staffId eligibility updated")
    }

    fun resetStaffPassword(staffId: String): String {
        val temp = SupabaseDataService.resetStaffPassword(staffId)
        showToast("Temporary password generated: $temp")
        return temp
    }

    // --- Lead Operations ---

    fun updateLeadStatus(leadId: String, newStatus: String, notes: String = "") {
        SupabaseDataService.updateLeadStatus(leadId, newStatus, notes)
        showToast("Lead status updated to $newStatus")
    }

    fun reassignLead(leadId: String, newStaffId: String, reason: String) {
        SupabaseDataService.reassignLead(leadId, newStaffId, reason)
        showToast("Lead reassigned to $newStaffId")
    }

    // --- Client Operations ---

    fun addClient(
        name: String,
        phone: String,
        whatsapp: String,
        passportNo: String,
        visaType: String,
        destination: String,
        staffRef: String,
        totalFee: Double,
        paid: Double
    ) {
        val client = SupabaseDataService.addClient(
            name, phone, whatsapp, passportNo, visaType, destination, staffRef, totalFee, paid
        )
        showToast("Client ${client.name} registered (${client.submissionId})")
    }

    fun updateClientStatus(clientId: String, newStatus: String) {
        SupabaseDataService.updateClientStatus(clientId, newStatus)
        showToast("Client status updated to $newStatus")
    }

    // --- Operator / Accounts / Appointments ---

    fun savePassportRecord(record: PassportRecord) {
        SupabaseDataService.addOrUpdatePassportRecord(record)
        showToast("Passport ${record.passportNo} record saved")
    }

    fun recordPayment(
        clientName: String,
        amount: Double,
        method: String,
        purpose: String,
        staffRef: String
    ) {
        val pay = SupabaseDataService.addPayment(clientName, amount, method, purpose, staffRef)
        showToast("Payment receipt ${pay.receiptNo} generated")
    }

    fun scheduleAppointment(
        clientName: String,
        phone: String,
        staffRef: String,
        purpose: String,
        date: String,
        time: String
    ) {
        SupabaseDataService.addAppointment(clientName, phone, staffRef, purpose, date, time)
        showToast("Appointment scheduled for $clientName")
    }
}
