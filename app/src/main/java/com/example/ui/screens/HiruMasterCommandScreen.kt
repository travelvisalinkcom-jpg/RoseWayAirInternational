package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.components.SlaCountdownBadge
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

enum class HiruCompany(val id: String, val displayName: String, val badge: String, val code: String) {
    ALL("all", "All Companies", "2 Active", "ALL"),
    AL_ISLAH("al_islah", "Al Islah International", "RL-1284", "AIS"),
    ROSE_WAY("rose_way", "Rose Way Air International", "RL-17385", "RWA")
}

enum class HiruNavTab(val titleEn: String, val titleBn: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("Dashboard", "ড্যাশবোর্ড", Icons.Default.Dashboard),
    LEADS("Leads & CRM", "লিডস ও সিআরএম", Icons.Default.People),
    CLIENTS("Clients", "ক্লায়েন্ট", Icons.Default.FolderShared),
    AI_AUTOMATION("AI & Auto", "এআই অটোমেশন", Icons.Default.AutoAwesome),
    MENU("Menu", "মেনু", Icons.Default.Menu)
}

data class StaffPerformanceItem(
    val rank: Int,
    val name: String,
    val company: String,
    val points: Int,
    val responseTimeMin: Double,
    val conversionRate: Int,
    val avatarColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiruMasterCommandScreen(
    session: AuthSession?,
    staffList: List<StaffMember>,
    leads: List<Lead>,
    clients: List<Client>,
    currentTimeMs: Long,
    onNavigateTo: (String) -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSimulateLeadSubmit: (name: String, phone: String, company: String, service: String) -> Unit,
    onUpdateLeadStatus: (leadId: String, status: String, notes: String) -> Unit,
    onReassignLead: (leadId: String, newStaffId: String, reason: String) -> Unit,
    onToggleStaffBlock: (staffId: String) -> Unit
) {
    var activeCompany by remember { mutableStateOf(HiruCompany.ALL) }
    var activeTab by remember { mutableStateOf(HiruNavTab.DASHBOARD) }
    var currentLang by remember { mutableStateOf("EN") } // EN, BN, AR
    var isDarkTheme by remember { mutableStateOf(true) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showStaffLeaderboardModal by remember { mutableStateOf(false) }
    var showQuickLeadDialog by remember { mutableStateOf(false) }
    var showRoleSwitcherDialog by remember { mutableStateOf(false) }
    var showApkExportDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Curated Top Staff Leaderboard data matching specification
    val leaderboard = remember {
        listOf(
            StaffPerformanceItem(1, "Arif Hossain", "Al Islah International", 3250, 1.8, 94, Color(0xFF6366F1)),
            StaffPerformanceItem(2, "Hasan Mahmud", "Rose Way Air Intl.", 2890, 2.1, 91, Color(0xFF10B981)),
            StaffPerformanceItem(3, "Rahim Uddin", "Al Islah International", 2650, 2.5, 88, Color(0xFF8B5CF6)),
            StaffPerformanceItem(4, "Sakib Ahmed", "Rose Way Air Intl.", 2410, 3.2, 85, Color(0xFFF59E0B)),
            StaffPerformanceItem(5, "Karim Sheikh", "Al Islah International", 2350, 3.4, 83, Color(0xFF3B82F6)),
            StaffPerformanceItem(6, "MD NAHID (S002)", "Rose Way Air Intl.", 2280, 2.9, 87, Color(0xFF10B981)),
            StaffPerformanceItem(7, "MD SHAKIL (S003)", "Rose Way Air Intl.", 2190, 3.1, 84, Color(0xFF059669)),
            StaffPerformanceItem(8, "ABDUL RAHMAN (S004)", "Rose Way Air Intl.", 2050, 3.8, 80, Color(0xFFD97706))
        )
    }

    // Dynamic metrics based on selected company
    val totalLeadsCount = when (activeCompany) {
        HiruCompany.ALL -> 1250
        HiruCompany.AL_ISLAH -> 700
        HiruCompany.ROSE_WAY -> 550
    }
    val totalClientsCount = when (activeCompany) {
        HiruCompany.ALL -> 420
        HiruCompany.AL_ISLAH -> 250
        HiruCompany.ROSE_WAY -> 170
    }
    val totalStaffCount = when (activeCompany) {
        HiruCompany.ALL -> 35
        HiruCompany.AL_ISLAH -> 20
        HiruCompany.ROSE_WAY -> 15
    }
    val totalIncomeTaka = when (activeCompany) {
        HiruCompany.ALL -> "৳ 8,540,000"
        HiruCompany.AL_ISLAH -> "৳ 4,850,000"
        HiruCompany.ROSE_WAY -> "৳ 3,690,000"
    }
    val totalExpenseTaka = when (activeCompany) {
        HiruCompany.ALL -> "৳ 2,350,000"
        HiruCompany.AL_ISLAH -> "৳ 1,350,000"
        HiruCompany.ROSE_WAY -> "৳ 1,000,000"
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(HiruDarkBg),
        bottomBar = {
            HiruBottomNavigationBar(
                activeTab = activeTab,
                currentLang = currentLang,
                onTabSelected = { activeTab = it }
            )
        },
        floatingActionButton = {
            if (activeTab == HiruNavTab.DASHBOARD || activeTab == HiruNavTab.LEADS) {
                ExtendedFloatingActionButton(
                    onClick = { showQuickLeadDialog = true },
                    containerColor = HiruPrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Bolt, "Quick AI Lead") },
                    text = {
                        Text(
                            text = if (currentLang == "BN") "নতুন এআই লিড" else "New AI Lead",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.testTag("fab_quick_lead")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(HiruDarkBg)
        ) {
            // Master OS Top Bar
            HiruMasterTopBar(
                currentLang = currentLang,
                onLangChange = { currentLang = it },
                onNotificationsClick = { showNotificationsDialog = true },
                onExportApkClick = { showApkExportDialog = true },
                onProfileClick = { showRoleSwitcherDialog = true },
                session = session
            )

            // Multi-Company Segmented Selector
            CompanySelectorTabs(
                selectedCompany = activeCompany,
                onCompanySelected = { activeCompany = it }
            )

            // Screen Content by Tab
            when (activeTab) {
                HiruNavTab.DASHBOARD -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
                    ) {
                        // 1. Five KPI Cards
                        item {
                            KpiCardsSection(
                                totalLeads = totalLeadsCount,
                                totalClients = totalClientsCount,
                                totalStaff = totalStaffCount,
                                totalIncome = totalIncomeTaka,
                                totalExpense = totalExpenseTaka,
                                currentLang = currentLang
                            )
                        }

                        // 2. Company Performance Comparison Card
                        item {
                            CompanyPerformanceCard(
                                selectedCompany = activeCompany,
                                onSelectCompany = { activeCompany = it }
                            )
                        }

                        // 3. AI Lead Algorithm Status & Efficiency Gauge
                        item {
                            AiLeadAlgorithmCard(
                                onUnblockStaff = {
                                    staffList.firstOrNull { !it.leadEligible }?.let { onToggleStaffBlock(it.staffId) }
                                },
                                onTestDistribution = { showQuickLeadDialog = true }
                            )
                        }

                        // 4. Top Staff Performance Leaderboard
                        item {
                            TopStaffPerformanceCard(
                                staffItems = leaderboard.filter {
                                    activeCompany == HiruCompany.ALL ||
                                            (activeCompany == HiruCompany.AL_ISLAH && it.company.contains("Al Islah")) ||
                                            (activeCompany == HiruCompany.ROSE_WAY && it.company.contains("Rose Way"))
                                },
                                onViewAllClick = { showStaffLeaderboardModal = true }
                            )
                        }

                        // 5. Leads Trend (This Month) - Interactive Multi-Line Canvas Chart
                        item {
                            LeadsTrendChartCard(selectedCompany = activeCompany)
                        }

                        // 6. Income vs Expense (This Month) - Dual Bar Canvas Chart
                        item {
                            IncomeExpenseChartCard(selectedCompany = activeCompany)
                        }
                    }
                }

                HiruNavTab.LEADS -> {
                    HiruLeadsCrmTab(
                        leads = leads,
                        staffList = staffList,
                        currentTimeMs = currentTimeMs,
                        selectedCompany = activeCompany,
                        onUpdateStatus = onUpdateLeadStatus,
                        onReassign = onReassignLead,
                        onAddNewLead = { showQuickLeadDialog = true }
                    )
                }

                HiruNavTab.CLIENTS -> {
                    HiruClientsVisaTab(
                        clients = clients,
                        selectedCompany = activeCompany,
                        onViewClient = {}
                    )
                }

                HiruNavTab.AI_AUTOMATION -> {
                    HiruAiAutomationTab(
                        staffList = staffList,
                        onTriggerEngine = { showQuickLeadDialog = true },
                        onToggleEligibility = onToggleStaffBlock
                    )
                }

                HiruNavTab.MENU -> {
                    HiruMenuModulesTab(
                        session = session,
                        onNavigate = onNavigateTo,
                        onLoginClick = onLoginClick,
                        onLogoutClick = onLogoutClick,
                        onExportApkClick = { showApkExportDialog = true },
                        onSwitchRole = { showRoleSwitcherDialog = true }
                    )
                }
            }
        }
    }

    // Dialog: Real-time Notifications
    if (showNotificationsDialog) {
        HiruNotificationsDialog(
            onDismiss = { showNotificationsDialog = false }
        )
    }

    // Dialog: Full Staff Leaderboard
    if (showStaffLeaderboardModal) {
        FullStaffLeaderboardDialog(
            staffItems = leaderboard,
            onDismiss = { showStaffLeaderboardModal = false }
        )
    }

    // Dialog: Quick AI Lead Submission
    if (showQuickLeadDialog) {
        QuickLeadSubmissionDialog(
            defaultCompany = if (activeCompany == HiruCompany.ALL) HiruCompany.ROSE_WAY else activeCompany,
            onDismiss = { showQuickLeadDialog = false },
            onSubmit = { name, phone, company, service ->
                onSimulateLeadSubmit(name, phone, company, service)
                showQuickLeadDialog = false
            }
        )
    }

    // Dialog: Role Switcher / Demo Explorer
    if (showRoleSwitcherDialog) {
        RoleSwitcherDialog(
            currentSession = session,
            onDismiss = { showRoleSwitcherDialog = false },
            onSelectRole = { role ->
                when (role) {
                    "ADMIN" -> onNavigateTo("admin")
                    "STAFF_NAHID" -> onNavigateTo("staff")
                    "MARKETING" -> onNavigateTo("marketing")
                    "OPERATOR" -> onNavigateTo("operator")
                    "ACCOUNTS" -> onNavigateTo("accounts")
                    "PUBLIC" -> onNavigateTo("public")
                }
                showRoleSwitcherDialog = false
            }
        )
    }

    // Dialog: Android APK & Multiplatform Export Hub
    if (showApkExportDialog) {
        ApkExportHubDialog(
            onDismiss = { showApkExportDialog = false }
        )
    }
}

// ---------------------------------------------------------------------------
// MASTER TOP BAR & COMPANY SELECTOR
// ---------------------------------------------------------------------------

@Composable
fun HiruMasterTopBar(
    currentLang: String,
    onLangChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onExportApkClick: () -> Unit,
    onProfileClick: () -> Unit,
    session: AuthSession?
) {
    Surface(
        color = HiruDarkBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // OS Brand Badge & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(HiruPrimary, HiruAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Hiru OS Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HIRU Master Command Center",
                                color = HiruTextLight,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = HiruCardBgElevated,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "V3.2",
                                    color = HiruPrimaryLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "All Companies Overview & AI-Powered Control Center",
                            color = HiruTextMuted,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls: APK Export, Language, Notifications & Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // APK Export Quick Button
                    Surface(
                        color = HiruCardBg,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, HiruPrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clickable { onExportApkClick() }
                            .testTag("btn_export_apk_topbar")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export APK",
                                tint = HiruPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "APK",
                                color = HiruPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Language Chip
                    Surface(
                        color = HiruCardBg,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, HiruBorder),
                        modifier = Modifier.clickable {
                            val next = when (currentLang) {
                                "EN" -> "BN"
                                "BN" -> "AR"
                                else -> "EN"
                            }
                            onLangChange(next)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Lang",
                                tint = HiruTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = currentLang,
                                color = HiruTextLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Notification Bell with Red Badge "12"
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(HiruCardBg)
                            .border(1.dp, HiruBorder, RoundedCornerShape(8.dp))
                            .clickable { onNotificationsClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = HiruTextLight,
                            modifier = Modifier.size(18.dp)
                        )
                        // Badge (12)
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(HiruDanger),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "12",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(HiruPrimary)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (session != null) session.name.take(1).uppercase() else "H",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompanySelectorTabs(
    selectedCompany: HiruCompany,
    onCompanySelected: (HiruCompany) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(HiruDarkBg)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(HiruCompany.entries) { company ->
            val isSelected = company == selectedCompany
            Surface(
                color = if (isSelected) HiruPrimary else HiruCardBg,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isSelected) HiruPrimary else HiruBorder),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onCompanySelected(company) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (company) {
                        HiruCompany.ALL -> Icons.Default.Hub
                        HiruCompany.AL_ISLAH -> Icons.Default.Mosque
                        HiruCompany.ROSE_WAY -> Icons.Default.Flight
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = company.displayName,
                        tint = if (isSelected) Color.White else HiruTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = company.displayName,
                        color = if (isSelected) Color.White else HiruTextLight,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (isSelected) Color.White.copy(alpha = 0.25f) else HiruCardBgElevated,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = company.badge,
                            color = if (isSelected) Color.White else HiruTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 5 KPI SUMMARY CARDS
// ---------------------------------------------------------------------------

@Composable
fun KpiCardsSection(
    totalLeads: Int,
    totalClients: Int,
    totalStaff: Int,
    totalIncome: String,
    totalExpense: String,
    currentLang: String
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            HiruKpiCard(
                title = if (currentLang == "BN") "মোট লিডস" else "TOTAL LEADS",
                value = NumberFormat.getNumberInstance(Locale.US).format(totalLeads),
                trend = "+12% this week",
                trendPositive = true,
                cardColor = Color(0xFF6366F1), // Indigo
                icon = Icons.Default.PersonSearch
            )
        }
        item {
            HiruKpiCard(
                title = if (currentLang == "BN") "মোট ক্লায়েন্ট" else "TOTAL CLIENTS",
                value = NumberFormat.getNumberInstance(Locale.US).format(totalClients),
                trend = "+8% this week",
                trendPositive = true,
                cardColor = Color(0xFF10B981), // Emerald
                icon = Icons.Default.Diversity3
            )
        }
        item {
            HiruKpiCard(
                title = if (currentLang == "BN") "মোট কর্মী" else "TOTAL STAFF",
                value = "$totalStaff",
                trend = "Active: 28",
                trendPositive = true,
                cardColor = Color(0xFFF59E0B), // Amber
                icon = Icons.Default.Badge
            )
        }
        item {
            HiruKpiCard(
                title = if (currentLang == "BN") "মোট আয়" else "TOTAL INCOME",
                value = totalIncome,
                trend = "+15% this month",
                trendPositive = true,
                cardColor = Color(0xFF0284C7), // Sky blue / Teal
                icon = Icons.Default.AccountBalanceWallet
            )
        }
        item {
            HiruKpiCard(
                title = if (currentLang == "BN") "মোট ব্যয়" else "TOTAL EXPENSE",
                value = totalExpense,
                trend = "+10% this month",
                trendPositive = false,
                cardColor = Color(0xFFEF4444), // Red
                icon = Icons.Default.TrendingDown
            )
        }
    }
}

@Composable
fun HiruKpiCard(
    title: String,
    value: String,
    trend: String,
    trendPositive: Boolean,
    cardColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = cardColor,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .width(160.dp)
            .height(105.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Icon decoration
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = trend,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// COMPANY PERFORMANCE MATRIX
// ---------------------------------------------------------------------------

@Composable
fun CompanyPerformanceCard(
    selectedCompany: HiruCompany,
    onSelectCompany: (HiruCompany) -> Unit
) {
    Surface(
        color = HiruCardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, HiruBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Company Performance",
                    color = HiruTextLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = HiruCardBgElevated,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "This Month ▼",
                        color = HiruTextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 1: Al Islah International
            CompanyRowItem(
                name = "Al Islah International",
                license = "RL-1284",
                leads = "700",
                clients = "250",
                income = "৳ 4,850,000",
                staff = "20",
                color = Color(0xFF6366F1),
                isSelected = selectedCompany == HiruCompany.AL_ISLAH,
                onClick = { onSelectCompany(HiruCompany.AL_ISLAH) }
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = HiruBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Rose Way Air International
            CompanyRowItem(
                name = "Rose Way Air International",
                license = "RL-17385",
                leads = "550",
                clients = "170",
                income = "৳ 3,690,000",
                staff = "15",
                color = Color(0xFF10B981),
                isSelected = selectedCompany == HiruCompany.ROSE_WAY,
                onClick = { onSelectCompany(HiruCompany.ROSE_WAY) }
            )
        }
    }
}

@Composable
fun CompanyRowItem(
    name: String,
    license: String,
    leads: String,
    clients: String,
    income: String,
    staff: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    color = HiruTextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($license)",
                    color = HiruTextMuted,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CompanyStatMini("Leads", leads)
            CompanyStatMini("Clients", clients)
            CompanyStatMini("Income", income)
            CompanyStatMini("Staff", staff)
        }
    }
}

@Composable
fun CompanyStatMini(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = HiruTextMuted,
            fontSize = 10.sp
        )
        Text(
            text = value,
            color = HiruTextLight,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

// ---------------------------------------------------------------------------
// AI LEAD ALGORITHM STATUS CARD (Radial Gauge & Metrics)
// ---------------------------------------------------------------------------

@Composable
fun AiLeadAlgorithmCard(
    onUnblockStaff: () -> Unit,
    onTestDistribution: () -> Unit
) {
    Surface(
        color = HiruCardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, HiruBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = HiruPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Lead Algorithm Status",
                        color = HiruTextLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "View Details",
                    color = HiruPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onTestDistribution() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Radial Circular Progress Gauge (92% System Efficiency)
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 10.dp.toPx()
                        // Background track
                        drawCircle(
                            color = Color(0xFF334155),
                            radius = size.minDimension / 2 - strokeWidth / 2,
                            style = Stroke(strokeWidth)
                        )
                        // Green Efficiency Arc 92%
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(HiruSuccess, HiruPrimary, HiruSuccess)
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * 0.92f,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "92%",
                            color = HiruTextLight,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "System\nEfficiency",
                            color = HiruTextMuted,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right: Metric breakdown list
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AiMetricProgressRow("Fast Response (5min)", 0.92f, "92%", HiruSuccess)
                    AiMetricProgressRow("Successful Calls", 0.85f, "85%", HiruPrimary)
                    AiMetricProgressRow("Status Updates", 0.90f, "90%", HiruAccent)
                    AiMetricProgressRow("Missed Leads", 0.08f, "8%", HiruWarning)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Blocked Staff",
                            color = HiruTextMuted,
                            fontSize = 11.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "2",
                                color = HiruDanger,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = HiruDanger.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.clickable { onUnblockStaff() }
                            ) {
                                Text(
                                    text = "Unblock",
                                    color = HiruDanger,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = HiruBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer status ticker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last Updated: 2 min ago",
                    color = HiruTextMuted,
                    fontSize = 10.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(HiruSuccess)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "System Running Smoothly",
                        color = HiruSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun AiMetricProgressRow(label: String, fraction: Float, percentage: String, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = HiruTextMuted,
                fontSize = 10.sp
            )
            Text(
                text = percentage,
                color = HiruTextLight,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = Color(0xFF334155),
        )
    }
}

// ---------------------------------------------------------------------------
// TOP STAFF PERFORMANCE (LEADERBOARD)
// ---------------------------------------------------------------------------

@Composable
fun TopStaffPerformanceCard(
    staffItems: List<StaffPerformanceItem>,
    onViewAllClick: () -> Unit
) {
    Surface(
        color = HiruCardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, HiruBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Leaderboard",
                        tint = HiruWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Top Staff Performance",
                        color = HiruTextLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "View All",
                    color = HiruPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            staffItems.take(5).forEachIndexed { _, item ->
                StaffPerformanceRow(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StaffPerformanceRow(item: StaffPerformanceItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(HiruCardBgElevated.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when (item.rank) {
                            1 -> HiruWarning
                            2 -> Color(0xFF94A3B8)
                            3 -> Color(0xFFB45309)
                            else -> HiruCardBgElevated
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${item.rank}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = item.name,
                    color = HiruTextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = item.company,
                    color = HiruTextMuted,
                    fontSize = 10.sp
                )
            }
        }

        // Points
        Surface(
            color = HiruPrimary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "${NumberFormat.getNumberInstance(Locale.US).format(item.points)} pts",
                color = HiruPrimaryLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// VISUAL CHARTS (Leads Trend & Income vs Expense)
// ---------------------------------------------------------------------------

@Composable
fun LeadsTrendChartCard(selectedCompany: HiruCompany) {
    Surface(
        color = HiruCardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, HiruBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Leads Trend (This Month)",
                    color = HiruTextLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(HiruPrimary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Al Islah", color = HiruTextMuted, fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(HiruSuccess)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rose Way", color = HiruTextMuted, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Line Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val paddingBottom = 20f

                    // Grid lines
                    for (i in 0..4) {
                        val y = (h - paddingBottom) * (i / 4f)
                        drawLine(
                            color = Color(0xFF334155).copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // Data points Al Islah (Indigo)
                    val ptsIslah = listOf(20f, 35f, 45f, 40f, 65f, 75f, 85f, 95f, 88f, 105f)
                    val islahPath = Path()
                    ptsIslah.forEachIndexed { idx, valY ->
                        val x = (w / (ptsIslah.size - 1)) * idx
                        val y = (h - paddingBottom) - (valY / 120f) * (h - paddingBottom)
                        if (idx == 0) islahPath.moveTo(x, y) else islahPath.lineTo(x, y)
                    }

                    drawPath(
                        path = islahPath,
                        color = HiruPrimary,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Data points Rose Way (Emerald)
                    val ptsRose = listOf(15f, 25f, 30f, 50f, 55f, 60f, 70f, 80f, 92f, 98f)
                    val rosePath = Path()
                    ptsRose.forEachIndexed { idx, valY ->
                        val x = (w / (ptsRose.size - 1)) * idx
                        val y = (h - paddingBottom) - (valY / 120f) * (h - paddingBottom)
                        if (idx == 0) rosePath.moveTo(x, y) else rosePath.lineTo(x, y)
                    }

                    drawPath(
                        path = rosePath,
                        color = HiruSuccess,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }

            // X-axis Day labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("1", "5", "10", "15", "20", "25", "30").forEach { day ->
                    Text(
                        text = day,
                        color = HiruTextMuted,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeExpenseChartCard(selectedCompany: HiruCompany) {
    Surface(
        color = HiruCardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, HiruBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Income vs Expense (This Month)",
                    color = HiruTextLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(HiruSuccess)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Income", color = HiruTextMuted, fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(HiruDanger)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Expense", color = HiruTextMuted, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Dual Bar Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val paddingBottom = 20f

                    // Grid lines
                    for (i in 0..4) {
                        val y = (h - paddingBottom) * (i / 4f)
                        drawLine(
                            color = Color(0xFF334155).copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // Bar data pairs (Income, Expense)
                    val barPairs = listOf(
                        Pair(0.4f, 0.15f),
                        Pair(0.6f, 0.25f),
                        Pair(0.5f, 0.20f),
                        Pair(0.75f, 0.30f),
                        Pair(0.85f, 0.35f),
                        Pair(0.7f, 0.28f),
                        Pair(0.95f, 0.40f)
                    )

                    val groupWidth = w / barPairs.size
                    val barWidth = groupWidth * 0.3f

                    barPairs.forEachIndexed { idx, pair ->
                        val groupX = idx * groupWidth + (groupWidth - barWidth * 2 - 4) / 2

                        // Income Bar (Green)
                        val incomeHeight = (h - paddingBottom) * pair.first
                        drawRoundRect(
                            color = HiruSuccess,
                            topLeft = Offset(groupX, (h - paddingBottom) - incomeHeight),
                            size = androidx.compose.ui.geometry.Size(barWidth, incomeHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )

                        // Expense Bar (Red)
                        val expenseHeight = (h - paddingBottom) * pair.second
                        drawRoundRect(
                            color = HiruDanger,
                            topLeft = Offset(groupX + barWidth + 4, (h - paddingBottom) - expenseHeight),
                            size = androidx.compose.ui.geometry.Size(barWidth, expenseHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                }
            }

            // X-axis Day labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("1", "5", "10", "15", "20", "25", "30").forEach { day ->
                    Text(
                        text = day,
                        color = HiruTextMuted,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// BOTTOM NAVIGATION BAR (Mobile Spec)
// ---------------------------------------------------------------------------

@Composable
fun HiruBottomNavigationBar(
    activeTab: HiruNavTab,
    currentLang: String,
    onTabSelected: (HiruNavTab) -> Unit
) {
    Surface(
        color = HiruDarkBg,
        border = BorderStroke(1.dp, HiruBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HiruNavTab.entries.forEach { tab ->
                val isSelected = tab == activeTab
                val label = if (currentLang == "BN") tab.titleBn else tab.titleEn

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = label,
                        tint = if (isSelected) HiruPrimary else HiruTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        color = if (isSelected) HiruPrimary else HiruTextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// LEADS & AI CRM TAB (with 5-minute live SLA countdown)
// ---------------------------------------------------------------------------

@Composable
fun HiruLeadsCrmTab(
    leads: List<Lead>,
    staffList: List<StaffMember>,
    currentTimeMs: Long,
    selectedCompany: HiruCompany,
    onUpdateStatus: (String, String, String) -> Unit,
    onReassign: (String, String, String) -> Unit,
    onAddNewLead: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Leads & 5-Min SLA Monitor",
                    color = HiruTextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${leads.size} Total Leads",
                    color = HiruPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        items(leads) { lead ->
            Surface(
                color = HiruCardBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, HiruBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = lead.name,
                                color = HiruTextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${lead.destinationCountry} • ${lead.serviceType}",
                                color = HiruTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // SLA Countdown Badge
                        SlaCountdownBadge(
                            deadlineMs = lead.assignmentDeadlineAt,
                            currentTimeMs = currentTimeMs,
                            status = lead.assignmentStatus
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assigned: ${lead.staffName} (${lead.staffReference})",
                            color = HiruSuccessLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Action Buttons: Call & WhatsApp
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // WhatsApp
                            IconButton(
                                onClick = {
                                    val cleanNum = lead.whatsapp.replace(Regex("[^0-9]"), "")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNum"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "WhatsApp",
                                    tint = HiruSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Call
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${lead.phone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = HiruPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status pill selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("PENDING", "IN_PROGRESS", "HANDLED", "CONVERTED").forEach { st ->
                            val isCurrent = lead.assignmentStatus == st
                            Surface(
                                color = if (isCurrent) HiruPrimary else HiruCardBgElevated,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdateStatus(lead.id, st, "Updated from OS Mobile CRM") }
                            ) {
                                Text(
                                    text = st.take(4),
                                    color = if (isCurrent) Color.White else HiruTextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// CLIENTS & VISA PROCESSING TAB
// ---------------------------------------------------------------------------

@Composable
fun HiruClientsVisaTab(
    clients: List<Client>,
    selectedCompany: HiruCompany,
    onViewClient: (Client) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Client Visa Processing Desk",
                    color = HiruTextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${clients.size} Active Clients",
                    color = HiruSuccess,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        items(clients) { client ->
            Surface(
                color = HiruCardBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, HiruBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = client.name,
                                color = HiruTextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Passport: ${client.passportNo} • ${client.destination}",
                                color = HiruTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Surface(
                            color = HiruPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = client.status,
                                color = HiruPrimaryLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Staff: ${client.staffReference}",
                            color = HiruTextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Paid: ৳${client.paidAmount.toInt()} / Due: ৳${client.balanceDue.toInt()}",
                            color = if (client.balanceDue <= 0) HiruSuccess else HiruWarning,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// AI & AUTOMATION TAB
// ---------------------------------------------------------------------------

@Composable
fun HiruAiAutomationTab(
    staffList: List<StaffMember>,
    onTriggerEngine: () -> Unit,
    onToggleEligibility: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "AI Automation & Intelligence Center",
                color = HiruTextLight,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        item {
            Surface(
                color = HiruCardBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, HiruBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, "Engine", tint = HiruPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Automated Round-Robin & SLA Enforcement",
                            color = HiruTextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The AI Lead Algorithm continuously monitors incoming leads, checks active staff capacity, and initiates 5-minute countdown timers. If a staff member fails to respond, the lead is automatically reassigned and penalties are logged.",
                        color = HiruTextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onTriggerEngine,
                        colors = ButtonDefaults.buttonColors(containerColor = HiruPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, "Run")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate New Applicant Distribution")
                    }
                }
            }
        }

        item {
            Text(
                text = "Staff Eligibility & Auto-Block Status",
                color = HiruTextLight,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        items(staffList) { staff ->
            Surface(
                color = HiruCardBg,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, HiruBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${staff.name} (${staff.staffId})",
                            color = HiruTextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Score: ${staff.score}% • Handled: ${staff.totalHandled} • Missed: ${staff.totalMissed}",
                            color = HiruTextMuted,
                            fontSize = 10.sp
                        )
                    }

                    Button(
                        onClick = { onToggleEligibility(staff.staffId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (staff.leadEligible) HiruSuccessContainer else HiruDangerContainer
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (staff.leadEligible) "Eligible" else "Blocked",
                            color = if (staff.leadEligible) HiruSuccessLight else HiruDanger,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// MENU & SPECIALIZED MODULES TAB
// ---------------------------------------------------------------------------

@Composable
fun HiruMenuModulesTab(
    session: AuthSession?,
    onNavigate: (String) -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onExportApkClick: () -> Unit,
    onSwitchRole: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        item {
            // User status card
            Surface(
                color = HiruCardBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, HiruBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(HiruPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = session?.name ?: "Master Administrator",
                                color = HiruTextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = session?.role?.displayName ?: "System Master",
                                color = HiruTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onSwitchRole,
                        colors = ButtonDefaults.buttonColors(containerColor = HiruCardBgElevated),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Switch Role", color = HiruPrimaryLight, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "Operational Desks & Modules",
                color = HiruTextLight,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        item {
            ModuleNavCard(
                title = "Staff Management & Single Control Point",
                subtitle = "Manage S001-S009 authoritative staff IDs, eligibility & temporary passwords",
                icon = Icons.Default.SupervisorAccount,
                color = HiruPrimary,
                onClick = { onNavigate("admin") }
            )
        }

        item {
            ModuleNavCard(
                title = "Finance & Accounts Desk",
                subtitle = "Income vs expense ledgers, client payment receipts & voucher generation",
                icon = Icons.Default.AccountBalance,
                color = HiruSuccess,
                onClick = { onNavigate("accounts") }
            )
        }

        item {
            ModuleNavCard(
                title = "Computer Operator Desk",
                subtitle = "Passport data entry, MOFA submission numbers, BMET & GAMCA medical tracking",
                icon = Icons.Default.Computer,
                color = HiruAccent,
                onClick = { onNavigate("operator") }
            )
        }

        item {
            ModuleNavCard(
                title = "Marketing & Campaigns Desk",
                subtitle = "Campaign analytics, staff conversion ratios & lead channel metrics",
                icon = Icons.Default.Campaign,
                color = HiruWarning,
                onClick = { onNavigate("marketing") }
            )
        }

        item {
            ModuleNavCard(
                title = "Public Visa Application Portal",
                subtitle = "Client-facing portal for online visa submission & instant staff assignment tracking",
                icon = Icons.Default.Public,
                color = Color(0xFF0284C7),
                onClick = { onNavigate("public") }
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Package Export & Deployment",
                color = HiruTextLight,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        item {
            ModuleNavCard(
                title = "Android APK & Build Export Hub",
                subtitle = "Direct APK download, share installer package, view file paths & iOS multiplatform options",
                icon = Icons.Default.Download,
                color = Color(0xFF10B981),
                onClick = onExportApkClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            if (session != null) {
                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HiruDanger),
                    border = BorderStroke(1.dp, HiruDanger)
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign Out")
                }
            } else {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = HiruPrimary)
                ) {
                    Icon(Icons.Default.Login, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign In to Staff Account")
                }
            }
        }
    }
}

@Composable
fun ModuleNavCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        color = HiruCardBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, HiruBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = HiruTextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    color = HiruTextMuted,
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = HiruTextMuted
            )
        }
    }
}

// ---------------------------------------------------------------------------
// DIALOGS: NOTIFICATIONS, LEADERBOARD, QUICK LEAD & ROLE SWITCHER
// ---------------------------------------------------------------------------

@Composable
fun HiruNotificationsDialog(onDismiss: () -> Unit) {
    val alerts = listOf(
        "⚡ S002 MD NAHID converted lead #1084 (Saudi Work Visa)",
        "⚠️ S004 Abdul Rahman missed SLA response - Lead reassigned to S002",
        "💰 Payment of ৳85,000 received for Rose Way Air client Rahim",
        "✈️ 3 Visa Stamping records approved by Computer Operator Desk",
        "🤖 AI Lead Distribution engine running at 92% efficiency"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = HiruCardBg,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, HiruBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Real-Time System Alerts", color = HiruTextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = HiruTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                alerts.forEach { alert ->
                    Surface(
                        color = HiruCardBgElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = alert,
                            color = HiruTextLight,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FullStaffLeaderboardDialog(
    staffItems: List<StaffPerformanceItem>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = HiruCardBg,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, HiruBorder),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Staff Leaderboard & Points", color = HiruTextLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = HiruTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(staffItems) { item ->
                        StaffPerformanceRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickLeadSubmissionDialog(
    defaultCompany: HiruCompany,
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, company: String, service: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var company by remember { mutableStateOf(defaultCompany.displayName) }
    var service by remember { mutableStateOf("Saudi Arabia - Work Visa") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = HiruCardBg,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, HiruBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Simulate New AI Lead",
                    color = HiruTextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Applicant Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HiruTextLight,
                        unfocusedTextColor = HiruTextLight
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / WhatsApp (+880...)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HiruTextLight,
                        unfocusedTextColor = HiruTextLight
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = service,
                    onValueChange = { service = it },
                    label = { Text("Destination & Service") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HiruTextLight,
                        unfocusedTextColor = HiruTextLight
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val finalName = name.ifBlank { "Applicant ${System.currentTimeMillis() % 10000}" }
                        val finalPhone = phone.ifBlank { "+8801700${(100000..999999).random()}" }
                        onSubmit(finalName, finalPhone, company, service)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HiruPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Bolt, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Distribute via AI Lead Engine")
                }
            }
        }
    }
}

@Composable
fun RoleSwitcherDialog(
    currentSession: AuthSession?,
    onDismiss: () -> Unit,
    onSelectRole: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = HiruCardBg,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, HiruBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick Demo / Role Switcher",
                    color = HiruTextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Select any desk to test specific operational flows:",
                    color = HiruTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                val roles = listOf(
                    Triple("ADMIN", "Master Admin (Full Access)", Icons.Default.Shield),
                    Triple("STAFF_NAHID", "MD NAHID (S002 Staff Consultant)", Icons.Default.Person),
                    Triple("MARKETING", "Marketing & Campaigns Desk", Icons.Default.Campaign),
                    Triple("OPERATOR", "Computer Operator (Passport/MOFA)", Icons.Default.Computer),
                    Triple("ACCOUNTS", "Accounts & Finance Desk", Icons.Default.AccountBalance),
                    Triple("PUBLIC", "Public Visa Portal", Icons.Default.Public)
                )

                roles.forEach { (roleKey, title, icon) ->
                    Surface(
                        color = HiruCardBgElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelectRole(roleKey) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, null, tint = HiruPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(title, color = HiruTextLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApkExportHubDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = HiruCardBg,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, HiruBorder),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(HiruPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = HiruPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "APK & App Export Center",
                                color = HiruTextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Android Package & Build Artifacts",
                                color = HiruTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = HiruTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // 1. Android APK Build Status Card
                    item {
                        Surface(
                            color = HiruCardBgElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HiruPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, null, tint = HiruSuccess, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Android Debug APK Ready", color = HiruTextLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Surface(
                                        color = HiruSuccess.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "BUILD SUCCESSFUL",
                                            color = HiruSuccess,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Binary: app-debug.apk (~18.4 MB)\nVersion: 3.2.0-PROD (Build 103)\nTarget SDK: Android 14 (API 34) | Universal Architecture",
                                    color = HiruTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, "HIRU Command OS Android App")
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "HIRU Master Command OS V3.2 Android App\nBuilt for Rose Way Air (RL-17385) & Al Islah International (RL-18231)\n\nDownload APK directly from AI Studio Build Settings (⚙️ Export APK) or install app-debug.apk."
                                                )
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share App & APK Details"))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = HiruPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share App", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString("app/build/outputs/apk/debug/app-debug.apk"))
                                            android.widget.Toast.makeText(context, "APK path copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HiruPrimaryLight),
                                        border = BorderStroke(1.dp, HiruPrimaryLight.copy(alpha = 0.5f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy APK Path", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // 2. Direct AI Studio Export Instructions Card
                    item {
                        Surface(
                            color = HiruCardBgElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HiruBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "📥 How to Download APK in AI Studio",
                                    color = HiruTextLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val steps = listOf(
                                    "1. Click the top-right Settings Menu (⚙️) in your AI Studio window.",
                                    "2. Select 'Export APK' to directly download the compiled .apk to your phone or PC.",
                                    "3. Select 'Download Project' to export the entire Kotlin / Compose Gradle source code as a ZIP.",
                                    "4. Transfer or sideload the .apk onto any Android 8.0+ device."
                                )

                                steps.forEach { step ->
                                    Text(
                                        text = step,
                                        color = HiruTextMuted,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Command Line & Build Commands
                    item {
                        Surface(
                            color = HiruCardBgElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HiruBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ Terminal Gradle Commands",
                                    color = HiruTextLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val commands = listOf(
                                    "Debug APK" to "gradle assembleDebug",
                                    "Release APK" to "gradle assembleRelease",
                                    "Play Store AAB" to "gradle bundleRelease"
                                )

                                commands.forEach { (label, cmd) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .background(HiruDarkBg, RoundedCornerShape(6.dp))
                                            .clickable {
                                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(cmd))
                                                android.widget.Toast.makeText(context, "Copied: $cmd", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(label, color = HiruPrimaryLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(cmd, color = Color.White, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                                        }
                                        Icon(Icons.Default.ContentCopy, null, tint = HiruTextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    // 4. iOS Multiplatform Deployment Info
                    item {
                        Surface(
                            color = HiruCardBgElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HiruBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🍏 Apple iOS Deployment", color = HiruTextLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "This application is built using Kotlin & Jetpack Compose. To deploy on iPhones (iOS .ipa):\n• Use 'Push to GitHub' in AI Studio Settings menu.\n• Open in Xcode or Compose Multiplatform to compile native iOS binaries.\n• All Supabase backends, SLA models & business logic are 100% cross-platform compatible.",
                                    color = HiruTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = HiruCardBgElevated),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close", color = HiruTextLight, fontSize = 12.sp)
                }
            }
        }
    }
}
