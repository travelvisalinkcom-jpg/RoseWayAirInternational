package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.components.StatMetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

// -------------------------------------------------------------
// 1. Marketing Manager Dashboard (HIRU / Master Mirror)
// -------------------------------------------------------------
@Composable
fun MarketingDashboardScreen(
    staffList: List<StaffMember>,
    leads: List<Lead>
) {
    val totalLeads = leads.size
    val activeStaffCount = staffList.count { it.isActive && !it.receivingPaused }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Marketing & Lead Acquisition Dashboard",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AviationNavy
            )
            Text(
                text = "Master Mirror & Advertising Analytics • Supabase Realtime Pool",
                fontSize = 11.sp,
                color = SlateTextSecondary
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatMetricCard(
                    title = "Total Inbound Leads",
                    value = "$totalLeads",
                    subtitle = "Public Portal + Campaigns",
                    icon = Icons.Default.Campaign,
                    accentColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Active Staff Pool",
                    value = "$activeStaffCount / ${staffList.size}",
                    subtitle = "Ready for Instant 5m SLA",
                    icon = Icons.Default.Groups,
                    accentColor = EmeraldPrimary,
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
                        text = "Staff Lead Distribution & Conversion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AviationNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    staffList.filter { it.isActive }.forEach { staff ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "[${staff.staffId}] ${staff.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AviationNavy
                                )
                                Text(
                                    text = "Handled: ${staff.totalHandled} • Missed: ${staff.totalMissed}",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldContainer
                            ) {
                                Text(
                                    text = "${staff.score}% Success",
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Computer Operator Dashboard (Passport Records & BMET)
// -------------------------------------------------------------
@Composable
fun ComputerOperatorDashboardScreen(
    records: List<PassportRecord>,
    onSaveRecord: (PassportRecord) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = records.filter {
        searchQuery.isBlank() || it.passportNo.contains(searchQuery, ignoreCase = true) ||
        it.holderName.contains(searchQuery, ignoreCase = true) ||
        it.mofaNo.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
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
                        text = "Computer Operator Desk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                    Text(
                        text = "Passport Data Entry • MOFA • BMET Submission ID",
                        fontSize = 11.sp,
                        color = SlateTextSecondary
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Record", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by Passport #, Name, MOFA #...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        items(filtered) { record ->
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
                                text = record.holderName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AviationNavy
                            )
                            Text(
                                text = "Passport: ${record.passportNo} (Exp: ${record.expiryDate})",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AviationNavyLight
                        ) {
                            Text(
                                text = record.country,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateSurfaceVariant, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "MOFA: ${record.mofaNo}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "BMET: ${record.bmetSubmissionId}", fontSize = 11.sp, color = SlateTextSecondary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Medical: ${record.medicalStatus}", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "Visa: ${record.visaStampingStatus}", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddPassportRecordDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { rec ->
                onSaveRecord(rec)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddPassportRecordDialog(
    onDismiss: () -> Unit,
    onConfirm: (PassportRecord) -> Unit
) {
    var passNo by remember { mutableStateOf("") }
    var holder by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Saudi Arabia") }
    var mofa by remember { mutableStateOf("") }
    var bmet by remember { mutableStateOf("") }
    var medical by remember { mutableStateOf("Fit (GAMCA Passed)") }
    var stamping by remember { mutableStateOf("Applied") }
    var expiry by remember { mutableStateOf("2031-12-31") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "New Passport & Embassy Entry",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(value = passNo, onValueChange = { passNo = it.uppercase() }, label = { Text("Passport Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = holder, onValueChange = { holder = it }, label = { Text("Holder Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = mofa, onValueChange = { mofa = it }, label = { Text("MOFA Number") }, modifier = Modifier.weight(1f), singleLine = true)
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(value = bmet, onValueChange = { bmet = it }, label = { Text("BMET Submission ID") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (passNo.isNotBlank() && holder.isNotBlank()) {
                                onConfirm(PassportRecord(passNo, holder, country, mofa.ifBlank { "MOFA-Pending" }, bmet.ifBlank { "BMET-Pending" }, medical, stamping, expiry, "Operator"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Save Record")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. Accounts Dashboard (Payments & Receipts)
// -------------------------------------------------------------
@Composable
fun AccountsDashboardScreen(
    payments: List<PaymentRecord>,
    onAddPayment: (String, Double, String, String, String) -> Unit
) {
    var showPaymentDialog by remember { mutableStateOf(false) }
    val totalCollected = payments.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Accounts & Payment Receipts",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                    Text(
                        text = "Official Billing & Ledger (RL-17385)",
                        fontSize = 11.sp,
                        color = SlateTextSecondary
                    )
                }

                Button(
                    onClick = { showPaymentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Payment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            StatMetricCard(
                title = "Total Revenue Collected",
                value = "৳ ${"%,.0f".format(totalCollected)}",
                subtitle = "${payments.size} Verified Invoices",
                icon = Icons.Default.AccountBalanceWallet,
                accentColor = EmeraldPrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(payments) { payment ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = payment.clientName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AviationNavy
                        )
                        Text(
                            text = "Receipt: ${payment.receiptNo} • ${payment.paymentMethod} • ${payment.date}",
                            fontSize = 11.sp,
                            color = SlateTextSecondary
                        )
                        Text(
                            text = "Purpose: ${payment.purpose} (Staff: ${payment.staffReference})",
                            fontSize = 11.sp,
                            color = EmeraldDark
                        )
                    }

                    Text(
                        text = "৳${"%,.0f".format(payment.amount)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EmeraldPrimary
                    )
                }
            }
        }
    }

    if (showPaymentDialog) {
        AddPaymentDialog(
            onDismiss = { showPaymentDialog = false },
            onConfirm = { client, amount, method, purpose, staff ->
                onAddPayment(client, amount, method, purpose, staff)
                showPaymentDialog = false
            }
        )
    }
}

@Composable
private fun AddPaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String, String) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("50000") }
    var method by remember { mutableStateOf("Bank Transfer") }
    var purpose by remember { mutableStateOf("Visa Processing 1st Installment") }
    var staffRef by remember { mutableStateOf("S002") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Record Client Payment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AviationNavy)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Client Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = amountStr, onValueChange = { amountStr = it }, label = { Text("Amount (৳)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = purpose, onValueChange = { purpose = it }, label = { Text("Purpose / Service") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            if (clientName.isNotBlank() && amt > 0) {
                                onConfirm(clientName, amt, method, purpose, staffRef)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Issue Receipt")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. Appointment Management Dashboard
// -------------------------------------------------------------
@Composable
fun AppointmentsDashboardScreen(
    appointments: List<Appointment>,
    onAddAppointment: (String, String, String, String, String, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
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
                        text = "Appointment Desk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                    Text(
                        text = "Embassy Biometrics & Consultation Calendar",
                        fontSize = 11.sp,
                        color = SlateTextSecondary
                    )
                }

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(appointments) { apt ->
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
                                text = apt.clientName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AviationNavy
                            )
                            Text(
                                text = "${apt.phone} • Staff: ${apt.staffReference}",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldContainer
                        ) {
                            Text(
                                text = "${apt.date} • ${apt.appointmentTime}",
                                color = OnGoldContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Purpose: ${apt.purpose}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AviationNavy
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddAppointmentDialog(
            onDismiss = { showDialog = false },
            onConfirm = { client, phone, staff, purp, date, time ->
                onAddAppointment(client, phone, staff, purp, date, time)
                showDialog = false
            }
        )
    }
}

@Composable
private fun AddAppointmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("GAMCA Medical & Biometrics") }
    var date by remember { mutableStateOf("2026-08-30") }
    var time by remember { mutableStateOf("11:30 AM") }
    var staffRef by remember { mutableStateOf("S002") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Schedule Appointment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AviationNavy)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Client Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = purpose, onValueChange = { purpose = it }, label = { Text("Purpose (e.g. GAMCA Biometrics)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.weight(1f), singleLine = true)
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (clientName.isNotBlank() && phone.isNotBlank()) {
                                onConfirm(clientName, phone, staffRef, purpose, date, time)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Schedule")
                    }
                }
            }
        }
    }
}
