package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.Lead
import com.example.data.model.StaffMember
import com.example.data.supabase.LeadAssignmentEngine
import com.example.ui.components.SlaCountdownBadge
import com.example.ui.components.WhatsAppActionButton
import com.example.ui.theme.*

data class VisaCountry(
    val name: String,
    val flagEmoji: String,
    val popularService: String,
    val processingTime: String,
    val startingPrice: String
)

val PopularCountries = listOf(
    VisaCountry("Saudi Arabia", "🇸🇦", "Work Visa & Umrah", "7-10 Days", "৳ 250,000"),
    VisaCountry("United Arab Emirates", "🇦🇪", "2-Yr Employment Visa", "5-7 Days", "৳ 280,000"),
    VisaCountry("Qatar", "🇶🇦", "Work & Free Visa", "10-14 Days", "৳ 320,000"),
    VisaCountry("Kuwait", "🇰🇼", "General Work Visa", "15-20 Days", "৳ 380,000"),
    VisaCountry("Oman", "🇴🇲", "Employment / Business", "7-12 Days", "৳ 220,000"),
    VisaCountry("United Kingdom", "🇬🇧", "Student & Visitor Visa", "15-21 Days", "৳ 180,000"),
    VisaCountry("Malaysia", "🇲🇾", "Calling Visa & Tourist", "10-15 Days", "৳ 190,000")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicPortalScreen(
    onApplyNowClick: () -> Unit,
    onSubmitApplication: (String, String, String, String, String, String, String) -> Unit,
    lastSubmittedLead: Pair<Lead, StaffMember?>?,
    onDismissResultDialog: () -> Unit,
    currentTimeMs: Long
) {
    var applicantName by remember { mutableStateOf("") }
    var applicantPhone by remember { mutableStateOf("") }
    var applicantWhatsApp by remember { mutableStateOf("") }
    var passportNo by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("Saudi Arabia") }
    var selectedService by remember { mutableStateOf("Work Visa & BMET") }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    val serviceOptions = listOf(
        "Work Visa & BMET",
        "Umrah VIP Package",
        "Tourist & Visitor Visa",
        "Student Visa Consultation",
        "Air Ticket Booking",
        "GAMCA Medical Assistance",
        "Manpower Clearance"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Hero Section with Agency Credibility Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(AviationNavy, AviationNavyLight)
                        )
                    )
            ) {
                // Hero Background Asset
                Image(
                    painter = painterResource(id = R.drawable.roseway_hero_1787871529688),
                    contentDescription = "Rose Way Air Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.35f
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Approved",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GOVT APPROVED RECRUITING LICENSE RL-17385",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Your Trusted Gateway to the World",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp
                    )

                    Text(
                        text = "Fast Visa Processing • Manpower Clearance • Umrah Packages • Air Tickets",
                        color = EmeraldContainer,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row {
                        Button(
                            onClick = onApplyNowClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldLight,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_hero_apply_now")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Apply",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Apply Online",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Trust Badges Grid
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrustBadgeItem("Govt. License", "RL-17385 Approved", Icons.Default.VerifiedUser)
                TrustBadgeItem("5-Min SLA", "Instant Staff Assign", Icons.Default.Speed)
                TrustBadgeItem("100% Genuine", "BMET Verified", Icons.Default.Security)
            }
        }

        // Popular Visa Destinations
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Featured Destinations",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )
                    Text(
                        text = "Explore Visas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(PopularCountries) { country ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .width(160.dp)
                                .clickable {
                                    selectedCountry = country.name
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = country.flagEmoji, fontSize = 24.sp)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldContainer
                                    ) {
                                        Text(
                                            text = country.processingTime,
                                            color = EmeraldDark,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = country.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AviationNavy
                                )

                                Text(
                                    text = country.popularService,
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Starts ${country.startingPrice}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Public Application Form Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("public_apply_form_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Online Visa & Ticket Application",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationNavy
                            )
                            Text(
                                text = "Submit details • Assigned to active Rose Way consultant in < 5 mins",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (formError != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = formError!!,
                                color = Color(0xFFB91C1C),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Full Name Input
                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text("Applicant Full Name (as on Passport)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_applicant_name"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phone & WhatsApp
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = applicantPhone,
                            onValueChange = { applicantPhone = it },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_applicant_phone"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = applicantWhatsApp,
                            onValueChange = { applicantWhatsApp = it },
                            label = { Text("WhatsApp (Optional)") },
                            leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_applicant_whatsapp"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Passport Number
                    OutlinedTextField(
                        value = passportNo,
                        onValueChange = { passportNo = it.uppercase() },
                        label = { Text("Passport Number (e.g. A04589213)") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_passport_no"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Country Selection
                    Text(
                        text = "Destination Country",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AviationNavy,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    var countryExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = countryExpanded,
                        onExpandedChange = { countryExpanded = !countryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCountry,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = countryExpanded,
                            onDismissRequest = { countryExpanded = false }
                        ) {
                            PopularCountries.map { it.name }.forEach { country ->
                                DropdownMenuItem(
                                    text = { Text(country) },
                                    onClick = {
                                        selectedCountry = country
                                        countryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Service Type Selection
                    Text(
                        text = "Service Required",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AviationNavy,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    var serviceExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = serviceExpanded,
                        onExpandedChange = { serviceExpanded = !serviceExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedService,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = serviceExpanded,
                            onDismissRequest = { serviceExpanded = false }
                        ) {
                            serviceOptions.forEach { srv ->
                                DropdownMenuItem(
                                    text = { Text(srv) },
                                    onClick = {
                                        selectedService = srv
                                        serviceExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Special requirements or questions") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (applicantName.isBlank() || applicantPhone.isBlank()) {
                                formError = "Please enter applicant full name and phone number."
                            } else {
                                formError = null
                                onSubmitApplication(
                                    applicantName,
                                    applicantPhone,
                                    applicantWhatsApp,
                                    passportNo,
                                    selectedCountry,
                                    selectedService,
                                    notes
                                )
                                // Clear form
                                applicantName = ""
                                applicantPhone = ""
                                applicantWhatsApp = ""
                                passportNo = ""
                                notes = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_submit_public_application")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Submit Application (Free Consultation)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Office Contact & License Footer
        item {
            Surface(
                color = AviationNavyDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ROSE WAY AIR INTERNATIONAL",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Recruiting License No: RL-17385",
                        color = GoldSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Dhaka Central Office & Saudi Arabia Liaison Desk",
                        color = SlateTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Hotline & WhatsApp: +966 56 122 6349",
                        color = EmeraldContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "© 2026 Rose Way Air International. Supabase-Only Architecture.",
                        color = SlateTextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    // Success Confirmation Dialog
    if (lastSubmittedLead != null) {
        val lead = lastSubmittedLead.first
        val staff = lastSubmittedLead.second
        val contactNumber = LeadAssignmentEngine.getWhatsAppContactNumber(staff)

        Dialog(onDismissRequest = onDismissResultDialog) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Application Received!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationNavy
                    )

                    Text(
                        text = "Ref ID: ${lead.id}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SlateSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Assigned Consultant:",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                                SlaCountdownBadge(
                                    deadlineMs = lead.assignmentDeadlineAt,
                                    currentTimeMs = currentTimeMs,
                                    status = lead.assignmentStatus
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "[${lead.staffReference}] ${lead.staffName}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationNavy
                            )

                            Text(
                                text = "Service: ${lead.serviceType} (${lead.destinationCountry})",
                                fontSize = 12.sp,
                                color = SlateTextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your dedicated Rose Way consultant has been assigned and is ready to assist you.",
                        fontSize = 11.sp,
                        color = SlateTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    WhatsAppActionButton(
                        phoneOrWhatsApp = contactNumber,
                        applicantName = lead.name,
                        serviceName = lead.serviceType,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onDismissResultDialog,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close & Track Status")
                    }
                }
            }
        }
    }
}

@Composable
private fun TrustBadgeItem(title: String, subtitle: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(EmeraldContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = EmeraldDark,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = AviationNavy
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = SlateTextSecondary
            )
        }
    }
}
