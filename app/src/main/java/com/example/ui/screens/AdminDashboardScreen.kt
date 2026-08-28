package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.data.supabase.LeadAssignmentEngine
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    staffList: List<StaffMember>,
    leads: List<Lead>,
    clients: List<Client>,
    auditLogs: List<AuditLog>,
    currentTimeMs: Long,
    onCreateStaff: (String, String, String, UserRole) -> Unit,
    onToggleStaffActive: (String) -> Unit,
    onToggleLeadReceiving: (String) -> Unit,
    onToggleLeadEligible: (String) -> Unit,
    onResetPassword: (String) -> String,
    onReassignLead: (String, String, String) -> Unit,
    onUpdateLeadStatus: (String, String, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Staff Mgmt, 2: All Leads, 3: Clients, 4: Audit Logs

    var showAddStaffDialog by remember { mutableStateOf(false) }
    var reassigningLead by remember { mutableStateOf<Lead?>(null) }
    var passwordResetResult by remember { mutableStateOf<Pair<String, String>?>(null) } // staffId, tempPassword

    val tabTitles = listOf("Overview", "Staff Management", "All Leads & SLA", "Clients", "Audit Logs")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = AviationNavy,
            contentColor = Color.White,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldSecondary
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> AdminOverviewTab(
                staffList = staffList,
                leads = leads,
                clients = clients,
                currentTimeMs = currentTimeMs,
                onGoToStaff = { selectedTab = 1 },
                onGoToLeads = { selectedTab = 2 }
            )
            1 -> StaffManagementTab(
                staffList = staffList,
                onAddStaffClick = { showAddStaffDialog = true },
                onToggleActive = onToggleStaffActive,
                onToggleReceiving = onToggleLeadReceiving,
                onToggleEligible = onToggleLeadEligible,
                onResetPass = { staffId ->
                    val temp = onResetPassword(staffId)
                    passwordResetResult = Pair(staffId, temp)
                }
            )
            2 -> AllLeadsTab(
                leads = leads,
                staffList = staffList,
                currentTimeMs = currentTimeMs,
                onReassignClick = { lead -> reassigningLead = lead },
                onUpdateStatus = onUpdateLeadStatus
            )
            3 -> MasterClientsTab(clients = clients)
            4 -> AuditLogsTab(auditLogs = auditLogs)
        }
    }

    // Add Staff Modal Dialog
    if (showAddStaffDialog) {
        AddStaffDialog(
            onDismiss = { showAddStaffDialog = false },
            onConfirm = { name, username, phone, role ->
                onCreateStaff(name, username, phone, role)
                showAddStaffDialog = false
            }
        )
    }

    // Reassign Lead Dialog
    if (reassigningLead != null) {
        ReassignLeadDialog(
            lead = reassigningLead!!,
            staffList = staffList,
            onDismiss = { reassigningLead = null },
            onConfirm = { newStaffId, reason ->
                onReassignLead(reassigningLead!!.id, newStaffId, reason)
                reassigningLead = null
            }
        )
    }

    // Password Reset Confirmation Dialog
    if (passwordResetResult != null) {
        AlertDialog(
            onDismissRequest = { passwordResetResult = null },
            title = { Text("Password Reset Successful", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Temporary password generated for ${passwordResetResult!!.first}:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = passwordResetResult!!.second,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = OnGoldContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The staff member can now log in using their username and this password.",
                        fontSize = 11.sp,
                        color = SlateTextSecondary
                    )
                }
            },
            confirmButton = {
                Button(onClick = { passwordResetResult = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun AdminOverviewTab(
    staffList: List<StaffMember>,
    leads: List<Lead>,
    clients: List<Client>,
    currentTimeMs: Long,
    onGoToStaff: () -> Unit,
    onGoToLeads: () -> Unit
) {
    val totalLeads = leads.size
    val activeStaffCount = staffList.count { it.isActive && !it.receivingPaused }
    val pendingSlaCount = leads.count { it.assignmentStatus == "PENDING" }
    val totalRevenue = clients.sumOf { it.paidAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Executive Operations Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AviationNavy
            )
            Text(
                text = "Organization ID: 11111111-1111-4111-8111-111111111111 • License RL-17385",
                fontSize = 11.sp,
                color = SlateTextSecondary
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatMetricCard(
                    title = "Total Leads",
                    value = "$totalLeads",
                    subtitle = "$pendingSlaCount Pending SLA",
                    icon = Icons.Default.AssignmentInd,
                    accentColor = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Active Staff Pool",
                    value = "$activeStaffCount / ${staffList.size}",
                    subtitle = "S001-S009 Active",
                    icon = Icons.Default.People,
                    accentColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatMetricCard(
                    title = "Total Clients",
                    value = "${clients.size}",
                    subtitle = "Active Visa Processing",
                    icon = Icons.Default.Work,
                    accentColor = AviationNavyLight,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Total Collected",
                    value = "৳ ${"%,.0f".format(totalRevenue)}",
                    subtitle = "Safe Supabase Ledger",
                    icon = Icons.Default.Payments,
                    accentColor = EmeraldLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Live SLA Attention List
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StatusPending)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live 5-Min SLA Monitor",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AviationNavy
                            )
                        }

                        TextButton(onClick = onGoToLeads) {
                            Text("View All Leads")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val pendingLeads = leads.take(3)
                    if (pendingLeads.isEmpty()) {
                        Text(
                            text = "No pending leads currently in SLA queue.",
                            color = SlateTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        pendingLeads.forEach { lead ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SlateSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = lead.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = AviationNavy
                                        )
                                        Text(
                                            text = "Assigned to [${lead.staffReference}] ${lead.staffName}",
                                            fontSize = 11.sp,
                                            color = SlateTextSecondary
                                        )
                                    }

                                    SlaCountdownBadge(
                                        deadlineMs = lead.assignmentDeadlineAt,
                                        currentTimeMs = currentTimeMs,
                                        status = lead.assignmentStatus
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

@Composable
private fun StaffManagementTab(
    staffList: List<StaffMember>,
    onAddStaffClick: () -> Unit,
    onToggleActive: (String) -> Unit,
    onToggleReceiving: (String) -> Unit,
    onToggleEligible: (String) -> Unit,
    onResetPass: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Staff Management & Single Control Point",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                    Text(
                        text = "Authoritative S001-S009 Registry • Supabase Edge Integration",
                        fontSize = 11.sp,
                        color = SlateTextSecondary
                    )
                }

                Button(
                    onClick = onAddStaffClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_staff")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Staff", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(staffList) { staff ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (staff.isActive) Color.White else Color(0xFFFFF1F2)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (staff.isActive) EmeraldDark else Color(0xFFEF4444)
                            ) {
                                Text(
                                    text = staff.staffId,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = staff.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = AviationNavy
                                )
                                Text(
                                    text = "${staff.username} • ${staff.email}",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (staff.isActive) EmeraldContainer else Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = if (staff.isActive) "ACTIVE" else "DISABLED",
                                color = if (staff.isActive) EmeraldDark else Color(0xFFB91C1C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stats row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = "Assigned: ${staff.totalAssigned}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AviationNavy
                        )
                        Text(
                            text = "Handled: ${staff.totalHandled}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "Missed: ${staff.totalMissed}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (staff.totalMissed > 0) Color(0xFFDC2626) else SlateTextSecondary
                        )
                        Text(
                            text = "Score: ${staff.score}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Management Action Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Receiving lead status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (staff.receivingPaused) "Paused" else "Receiving Leads",
                                fontSize = 11.sp,
                                color = if (staff.receivingPaused) Color(0xFFDC2626) else EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Switch(
                                checked = !staff.receivingPaused,
                                onCheckedChange = { onToggleReceiving(staff.staffId) },
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        // Quick buttons
                        Row {
                            OutlinedButton(
                                onClick = { onResetPass(staff.staffId) },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reset Pass", fontSize = 10.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = { onToggleActive(staff.staffId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (staff.isActive) Color(0xFFEF4444) else EmeraldPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(if (staff.isActive) "Disable" else "Enable", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AllLeadsTab(
    leads: List<Lead>,
    staffList: List<StaffMember>,
    currentTimeMs: Long,
    onReassignClick: (Lead) -> Unit,
    onUpdateStatus: (String, String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("ALL") }

    val filteredLeads = leads.filter {
        (filterStatus == "ALL" || it.assignmentStatus == filterStatus) &&
        (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) ||
         it.phone.contains(searchQuery) || it.staffReference.contains(searchQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Master Leads Stream & SLA Center",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AviationNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, phone, or staff ID (e.g. S002)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        items(filteredLeads) { lead ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = lead.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AviationNavy
                            )
                            Text(
                                text = "${lead.phone} • Passport: ${lead.passportNo.ifBlank { "N/A" }}",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }

                        SlaCountdownBadge(
                            deadlineMs = lead.assignmentDeadlineAt,
                            currentTimeMs = currentTimeMs,
                            status = lead.assignmentStatus
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SlateSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Target: ${lead.destinationCountry} • ${lead.serviceType}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AviationNavy
                                )
                                Text(
                                    text = "Assigned To: [${lead.staffReference}] ${lead.staffName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            }

                            StatusBadge(lead.assignmentStatus)
                        }
                    }

                    if (lead.notes.isNotBlank()) {
                        Text(
                            text = "Notes: ${lead.notes}",
                            fontSize = 11.sp,
                            color = SlateTextSecondary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WhatsAppActionButton(
                            phoneOrWhatsApp = lead.phone,
                            applicantName = lead.name,
                            serviceName = lead.serviceType
                        )

                        Row {
                            OutlinedButton(
                                onClick = { onReassignClick(lead) },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reassign", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = { onUpdateStatus(lead.id, "HANDLED", "Admin marked handled") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Mark Handled", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MasterClientsTab(clients: List<Client>) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = clients.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) ||
        it.passportNo.contains(searchQuery, ignoreCase = true) ||
        it.staffReference.contains(searchQuery, ignoreCase = true) ||
        it.submissionId.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Master Client Records",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AviationNavy
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, passport, submission ID, staff...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        items(filtered) { client ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = client.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AviationNavy
                            )
                            Text(
                                text = "Passport: ${client.passportNo} • ${client.phone}",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldContainer
                        ) {
                            Text(
                                text = "Staff: ${client.staffReference}",
                                color = OnGoldContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${client.destination} • ${client.visaType}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AviationNavy
                            )
                            Text(
                                text = "Sub ID: ${client.submissionId}",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }

                        StatusBadge(client.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Financial balance bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurfaceVariant, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total: ৳${"%,.0f".format(client.totalFee)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Paid: ৳${"%,.0f".format(client.paidAmount)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "Due: ৳${"%,.0f".format(client.balanceDue)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (client.balanceDue > 0) Color(0xFFDC2626) else EmeraldPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditLogsTab(auditLogs: List<AuditLog>) {
    val sdf = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "System Security & Audit Trail",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AviationNavy
            )
            Text(
                text = "Supabase audit_logs & staff_activity_logs",
                fontSize = 11.sp,
                color = SlateTextSecondary
            )
        }

        items(auditLogs) { log ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = log.action,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AviationNavy
                        )
                        Text(
                            text = sdf.format(Date(log.timestamp)),
                            fontSize = 10.sp,
                            color = SlateTextMuted
                        )
                    }
                    Text(
                        text = "By: ${log.performedBy} • Target: ${log.target}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = log.details,
                        fontSize = 11.sp,
                        color = SlateTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddStaffDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, UserRole) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.STAFF) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Rose Way Staff Member",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationNavy
                )
                Text(
                    text = "Will be assigned next Staff ID and @users.roseway.app Supabase Auth",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name (e.g. MD SAJID)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.uppercase() },
                    label = { Text("Username (e.g. SAJID)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / WhatsApp") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && username.isNotBlank()) {
                                onConfirm(name, username, phone, role)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Create Staff")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReassignLeadDialog(
    lead: Lead,
    staffList: List<StaffMember>,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var selectedStaffId by remember { mutableStateOf(staffList.firstOrNull()?.staffId ?: "S001") }
    var reason by remember { mutableStateOf("Workload rebalance") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Reassign Lead ${lead.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationNavy
                )
                Text(
                    text = "Currently assigned to [${lead.staffReference}] ${lead.staffName}",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select New Staff Consultant:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                staffList.filter { it.isActive }.forEach { staff ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStaffId = staff.staffId }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStaffId == staff.staffId,
                            onClick = { selectedStaffId = staff.staffId }
                        )
                        Text(
                            text = "[${staff.staffId}] ${staff.name} (${staff.username})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reassignment Reason") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(selectedStaffId, reason) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Confirm Reassign")
                    }
                }
            }
        }
    }
}
