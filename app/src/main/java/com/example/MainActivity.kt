package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.components.RoseWayTopBar
import com.example.ui.screens.*
import com.example.ui.theme.RoseWayTheme
import com.example.ui.viewmodel.RoseWayViewModel
import com.example.ui.viewmodel.ScreenRoute
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: RoseWayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoseWayTheme {
                val currentRoute by viewModel.currentRoute.collectAsStateWithLifecycle()
                val session by viewModel.currentSession.collectAsStateWithLifecycle()
                val staffList by viewModel.staffList.collectAsStateWithLifecycle()
                val leads by viewModel.allLeads.collectAsStateWithLifecycle()
                val clients by viewModel.allClients.collectAsStateWithLifecycle()
                val passportRecords by viewModel.passportRecords.collectAsStateWithLifecycle()
                val payments by viewModel.payments.collectAsStateWithLifecycle()
                val appointments by viewModel.appointments.collectAsStateWithLifecycle()
                val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
                val currentTimeMs by viewModel.currentTimeMillis.collectAsStateWithLifecycle()
                val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
                val lastSubmittedLead by viewModel.lastSubmittedLead.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                LaunchedEffect(toastMessage) {
                    toastMessage?.let {
                        scope.launch {
                            snackbarHostState.showSnackbar(it)
                            viewModel.clearToast()
                        }
                    }
                }

                if (currentRoute == ScreenRoute.MasterCommand) {
                    HiruMasterCommandScreen(
                        session = session,
                        staffList = staffList,
                        leads = leads,
                        clients = clients,
                        currentTimeMs = currentTimeMs,
                        onNavigateTo = { target ->
                            when (target) {
                                "admin" -> viewModel.navigateTo(ScreenRoute.AdminDashboard)
                                "staff" -> {
                                    if (session == null) {
                                        viewModel.login("NAHID", "roseway123")
                                    }
                                    viewModel.navigateTo(ScreenRoute.StaffDashboard)
                                }
                                "marketing" -> viewModel.navigateTo(ScreenRoute.MarketingDashboard)
                                "operator" -> viewModel.navigateTo(ScreenRoute.ComputerOperatorDashboard)
                                "accounts" -> viewModel.navigateTo(ScreenRoute.AccountsDashboard)
                                "public" -> viewModel.navigateTo(ScreenRoute.PublicHome)
                                "login" -> viewModel.navigateTo(ScreenRoute.Login)
                            }
                        },
                        onLoginClick = { viewModel.navigateTo(ScreenRoute.Login) },
                        onLogoutClick = { viewModel.logout() },
                        onSimulateLeadSubmit = { name, phone, company, service ->
                            viewModel.submitPublicApplication(
                                name = name,
                                phone = phone,
                                whatsapp = phone,
                                passportNo = "A${(10000000..99999999).random()}",
                                country = if (service.contains("Saudi")) "Saudi Arabia" else "United Arab Emirates",
                                serviceType = service,
                                notes = "Initiated from Hiru Master AI Engine for $company"
                            )
                        },
                        onUpdateLeadStatus = { id, status, notes ->
                            viewModel.updateLeadStatus(id, status, notes)
                        },
                        onReassignLead = { id, newStaff, reason ->
                            viewModel.reassignLead(id, newStaff, reason)
                        },
                        onToggleStaffBlock = { id ->
                            viewModel.toggleLeadEligible(id)
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            RoseWayTopBar(
                                title = "HIRU Master Command OS",
                                session = session,
                                onLoginClick = { viewModel.navigateTo(ScreenRoute.Login) },
                                onLogoutClick = { viewModel.logout() },
                                onHomeClick = { viewModel.navigateTo(ScreenRoute.MasterCommand) },
                                onDashboardClick = {
                                    session?.let {
                                        when (it.role) {
                                            UserRole.ADMIN -> viewModel.navigateTo(ScreenRoute.AdminDashboard)
                                            UserRole.MARKETING_MANAGER -> viewModel.navigateTo(ScreenRoute.MarketingDashboard)
                                            UserRole.STAFF -> viewModel.navigateTo(ScreenRoute.StaffDashboard)
                                            UserRole.COMPUTER_OPERATOR -> viewModel.navigateTo(ScreenRoute.ComputerOperatorDashboard)
                                            UserRole.ACCOUNTS_MANAGER -> viewModel.navigateTo(ScreenRoute.AccountsDashboard)
                                            UserRole.APPOINTMENT_MANAGE -> viewModel.navigateTo(ScreenRoute.AppointmentsDashboard)
                                        }
                                    } ?: viewModel.navigateTo(ScreenRoute.MasterCommand)
                                }
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentRoute) {
                                ScreenRoute.MasterCommand -> {} // Handled above
                                ScreenRoute.PublicHome, ScreenRoute.PublicApply -> {
                                    PublicPortalScreen(
                                        onApplyNowClick = { viewModel.navigateTo(ScreenRoute.PublicApply) },
                                        onSubmitApplication = { name, phone, wa, pass, country, srv, notes ->
                                            viewModel.submitPublicApplication(name, phone, wa, pass, country, srv, notes)
                                        },
                                        lastSubmittedLead = lastSubmittedLead,
                                        onDismissResultDialog = { viewModel.clearSubmittedLead() },
                                        currentTimeMs = currentTimeMs
                                    )
                                }
                                ScreenRoute.Login -> {
                                    AuthScreen(
                                        onLoginSubmit = { user, pass ->
                                            viewModel.login(user, pass)
                                        },
                                        onBackToPublic = { viewModel.navigateTo(ScreenRoute.MasterCommand) }
                                    )
                                }
                                ScreenRoute.AdminDashboard -> {
                                    AdminDashboardScreen(
                                        staffList = staffList,
                                        leads = leads,
                                        clients = clients,
                                        auditLogs = auditLogs,
                                        currentTimeMs = currentTimeMs,
                                        onCreateStaff = { name, user, phone, role ->
                                            viewModel.createStaff(name, user, phone, role)
                                        },
                                        onToggleStaffActive = { id -> viewModel.toggleStaffActive(id) },
                                        onToggleLeadReceiving = { id -> viewModel.toggleLeadReceiving(id) },
                                        onToggleLeadEligible = { id -> viewModel.toggleLeadEligible(id) },
                                        onResetPassword = { id -> viewModel.resetStaffPassword(id) },
                                        onReassignLead = { leadId, newStaffId, reason ->
                                            viewModel.reassignLead(leadId, newStaffId, reason)
                                        },
                                        onUpdateLeadStatus = { leadId, status, notes ->
                                            viewModel.updateLeadStatus(leadId, status, notes)
                                        }
                                    )
                                }
                                ScreenRoute.StaffDashboard -> {
                                    val currentSession = session
                                    if (currentSession != null) {
                                        StaffDashboardScreen(
                                            session = currentSession,
                                            staffList = staffList,
                                            allLeads = leads,
                                            allClients = clients,
                                            currentTimeMs = currentTimeMs,
                                            onUpdateLeadStatus = { id, status, notes ->
                                                viewModel.updateLeadStatus(id, status, notes)
                                            },
                                            onAddClient = { name, phone, wa, pass, visa, dest, staffRef, total, paid ->
                                                viewModel.addClient(name, phone, wa, pass, visa, dest, staffRef, total, paid)
                                            },
                                            onUpdateClientStatus = { id, status ->
                                                viewModel.updateClientStatus(id, status)
                                            }
                                        )
                                    } else {
                                        viewModel.navigateTo(ScreenRoute.Login)
                                    }
                                }
                                ScreenRoute.MarketingDashboard -> {
                                    MarketingDashboardScreen(
                                        staffList = staffList,
                                        leads = leads
                                    )
                                }
                                ScreenRoute.ComputerOperatorDashboard -> {
                                    ComputerOperatorDashboardScreen(
                                        records = passportRecords,
                                        onSaveRecord = { rec -> viewModel.savePassportRecord(rec) }
                                    )
                                }
                                ScreenRoute.AccountsDashboard -> {
                                    AccountsDashboardScreen(
                                        payments = payments,
                                        onAddPayment = { client, amount, method, purp, staff ->
                                            viewModel.recordPayment(client, amount, method, purp, staff)
                                        }
                                    )
                                }
                                ScreenRoute.AppointmentsDashboard -> {
                                    AppointmentsDashboardScreen(
                                        appointments = appointments,
                                        onAddAppointment = { client, phone, staff, purp, date, time ->
                                            viewModel.scheduleAppointment(client, phone, staff, purp, date, time)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
