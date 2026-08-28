package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AuthSession
import com.example.data.model.Client
import com.example.data.model.Lead
import com.example.data.model.StaffMember
import com.example.ui.components.SlaCountdownBadge
import com.example.ui.components.StatMetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.WhatsAppActionButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    session: AuthSession,
    staffList: List<StaffMember>,
    allLeads: List<Lead>,
    allClients: List<Client>,
    currentTimeMs: Long,
    onUpdateLeadStatus: (String, String, String) -> Unit,
    onAddClient: (String, String, String, String, String, String, String, Double, Double) -> Unit,
    onUpdateClientStatus: (String, String) -> Unit
) {
    val currentStaffId = session.staffId ?: "S002"
    val staffProfile = staffList.find { it.staffId == currentStaffId }

    // STRICT DATA ISOLATION: Filter ONLY records owned by this staff ID
    val myLeads = allLeads.filter { it.staffReference == currentStaffId }
    val myClients = allClients.filter { it.staffReference == currentStaffId }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: My Leads & SLA, 1: My Clients, 2: Performance
    var showAddClientDialog by remember { mutableStateOf(false) }
    var updatingClientStatus by remember { mutableStateOf<Client?>(null) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Staff Profile Header Banner
        Surface(
            color = AviationNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentStaffId,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = session.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Staff ID: $currentStaffId • ${session.email}",
                            color = EmeraldContainer,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldContainer
                ) {
                    Text(
                        text = "Score: ${staffProfile?.score ?: 96.0}%",
                        color = OnGoldContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Tab Navigation
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = AviationNavyLight,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldSecondary
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "My Leads (${myLeads.size})",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "My Clients (${myClients.size})",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        text = "SLA Scorecard",
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        when (selectedTab) {
            0 -> MyLeadsTab(
                leads = myLeads,
                currentTimeMs = currentTimeMs,
                onUpdateStatus = onUpdateLeadStatus,
                onCallClient = { phone ->
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    context.startActivity(intent)
                }
            )
            1 -> MyClientsTab(
                clients = myClients,
                onAddClientClick = { showAddClientDialog = true },
                onUpdateStatusClick = { client -> updatingClientStatus = client }
            )
            2 -> StaffScorecardTab(
                staff = staffProfile,
                myLeads = myLeads,
                myClients = myClients
            )
        }
    }

    // Add Client Dialog
    if (showAddClientDialog) {
        AddClientDialog(
            staffId = currentStaffId,
            onDismiss = { showAddClientDialog = false },
            onConfirm = { name, phone, whatsapp, pass, visa, dest, total, paid ->
                onAddClient(name, phone, whatsapp, pass, visa, dest, currentStaffId, total, paid)
                showAddClientDialog = false
            }
        )
    }

    // Update Client Status Dialog
    if (updatingClientStatus != null) {
        UpdateClientStatusDialog(
            client = updatingClientStatus!!,
            onDismiss = { updatingClientStatus = null },
            onConfirm = { newStatus ->
                onUpdateClientStatus(updatingClientStatus!!.id, newStatus)
                updatingClientStatus = null
            }
        )
    }
}

@Composable
private fun MyLeadsTab(
    leads: List<Lead>,
    currentTimeMs: Long,
    onUpdateStatus: (String, String, String) -> Unit,
    onCallClient: (String) -> Unit
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
                        text = "My Assigned Inquiries (5-Minute SLA)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                    Text(
                        text = "Data Isolated: Showing only leads assigned to your Staff ID",
                        fontSize = 11.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (leads.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = SlateTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No assigned leads in your queue.",
                            color = SlateTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            items(leads) { lead ->
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
                                    fontSize = 15.sp,
                                    color = AviationNavy
                                )
                                Text(
                                    text = "${lead.phone} • Passport: ${lead.passportNo.ifBlank { "Pending" }}",
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
                                Text(
                                    text = "Destination: ${lead.destinationCountry} • ${lead.serviceType}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AviationNavy
                                )
                                StatusBadge(lead.assignmentStatus)
                            }
                        }

                        if (lead.notes.isNotBlank()) {
                            Text(
                                text = "Applicant Notes: ${lead.notes}",
                                fontSize = 11.sp,
                                color = SlateTextSecondary,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                WhatsAppActionButton(
                                    phoneOrWhatsApp = lead.phone,
                                    applicantName = lead.name,
                                    serviceName = lead.serviceType
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { onCallClient(lead.phone) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldContainer)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Direct Call",
                                        tint = EmeraldDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Row {
                                if (lead.assignmentStatus == "PENDING") {
                                    Button(
                                        onClick = { onUpdateStatus(lead.id, "IN_PROGRESS", "Consultant contacted client.") },
                                        colors = ButtonDefaults.buttonColors(containerColor = AviationNavyLight),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("In Progress", fontSize = 11.sp)
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))
                                }

                                Button(
                                    onClick = { onUpdateStatus(lead.id, "HANDLED", "Client requirements documented.") },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
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
}

@Composable
private fun MyClientsTab(
    clients: List<Client>,
    onAddClientClick: () -> Unit,
    onUpdateStatusClick: (Client) -> Unit
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
                        text = "My Active Clients",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                    Text(
                        text = "Track ongoing visa applications, MOFA & passport submissions",
                        fontSize = 11.sp,
                        color = SlateTextSecondary
                    )
                }

                Button(
                    onClick = onAddClientClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_client")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Client", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (clients.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No clients currently assigned to your staff profile.")
                    }
                }
            }
        } else {
            items(clients) { client ->
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

                            StatusBadge(client.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Destination: ${client.destination} • ${client.visaType}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AviationNavy
                        )
                        Text(
                            text = "Sub ID: ${client.submissionId}",
                            fontSize = 11.sp,
                            color = SlateTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurfaceVariant, RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total: ৳${"%,.0f".format(client.totalFee)}", fontSize = 11.sp)
                            Text(text = "Paid: ৳${"%,.0f".format(client.paidAmount)}", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "Due: ৳${"%,.0f".format(client.balanceDue)}", fontSize = 11.sp, color = if (client.balanceDue > 0) Color(0xFFDC2626) else EmeraldPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { onUpdateStatusClick(client) },
                                colors = ButtonDefaults.buttonColors(containerColor = AviationNavyLight),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Update Status", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffScorecardTab(
    staff: StaffMember?,
    myLeads: List<Lead>,
    myClients: List<Client>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Performance & SLA Compliance",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AviationNavy
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatMetricCard(
                    title = "Total Handled",
                    value = "${staff?.totalHandled ?: myLeads.size}",
                    subtitle = "Within 5m SLA",
                    icon = Icons.Default.CheckCircle,
                    accentColor = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Conversion Score",
                    value = "${staff?.score ?: 96.0}%",
                    subtitle = "Top Tier",
                    icon = Icons.Default.TrendingUp,
                    accentColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5-Minute Lead Handling Guidelines",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AviationNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. When a new lead is assigned, the 5-minute countdown starts automatically.\n" +
                               "2. Immediately contact the applicant via WhatsApp or Direct Call.\n" +
                               "3. Update status to 'In Progress' or 'Handled' to maintain a 100% compliance score.\n" +
                               "4. Document passport details and transition interested applicants to Client status.",
                        fontSize = 12.sp,
                        color = SlateTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AddClientDialog(
    staffId: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, Double, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var passportNo by remember { mutableStateOf("") }
    var visaType by remember { mutableStateOf("Work Visa (Driver)") }
    var destination by remember { mutableStateOf("Saudi Arabia") }
    var totalFeeStr by remember { mutableStateOf("350000") }
    var paidStr by remember { mutableStateOf("150000") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Register New Client",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationNavy
                )
                Text(
                    text = "Assigned under your Staff ID: $staffId",
                    fontSize = 11.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Client Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = passportNo,
                        onValueChange = { passportNo = it.uppercase() },
                        label = { Text("Passport #") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Destination") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = visaType,
                        onValueChange = { visaType = it },
                        label = { Text("Visa Type") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = totalFeeStr,
                        onValueChange = { totalFeeStr = it },
                        label = { Text("Total Fee (৳)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = paidStr,
                        onValueChange = { paidStr = it },
                        label = { Text("Paid (৳)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

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
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                val total = totalFeeStr.toDoubleOrNull() ?: 0.0
                                val paid = paidStr.toDoubleOrNull() ?: 0.0
                                onConfirm(name, phone, whatsapp, passportNo, visaType, destination, total, paid)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Save Client")
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateClientStatusDialog(
    client: Client,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val statuses = listOf(
        "Documents Received",
        "MOFA Submitted",
        "Medical Done",
        "Embassy Stamped",
        "Flight Booked",
        "Delivered"
    )
    var selectedStatus by remember { mutableStateOf(client.status) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Update Processing Status",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationNavy
                )
                Text(
                    text = "Client: ${client.name} (${client.submissionId})",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                statuses.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Text(
                            text = status,
                            fontSize = 13.sp,
                            fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

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
                        onClick = { onConfirm(selectedStatus) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}
