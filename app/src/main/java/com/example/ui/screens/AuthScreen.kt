package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.supabase.SupabaseDataService
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onLoginSubmit: (String, String) -> Unit,
    onBackToPublic: () -> Unit
) {
    var usernameOrEmail by remember { mutableStateOf("NAHID") }
    var password by remember { mutableStateOf("RoseWay@2026") }
    var passwordVisible by remember { mutableStateOf(false) }

    val computedEmail = if (usernameOrEmail.contains("@")) {
        usernameOrEmail.lowercase().trim()
    } else {
        "${usernameOrEmail.lowercase().trim()}${SupabaseDataService.STAFF_EMAIL_DOMAIN}"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                TextButton(
                    onClick = onBackToPublic,
                    modifier = Modifier.testTag("btn_back_to_public")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back to Public Website", color = AviationNavy)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Brand Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AviationNavy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = "Staff Key",
                    tint = GoldSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Staff & Executive Login",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AviationNavy
            )

            Text(
                text = "Supabase Auth • Rose Way Air International (RL-17385)",
                fontSize = 11.sp,
                color = SlateTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Sign In to Organization",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AviationNavy
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Username or Email
                    OutlinedTextField(
                        value = usernameOrEmail,
                        onValueChange = { usernameOrEmail = it },
                        label = { Text("Staff Username or Email") },
                        placeholder = { Text("e.g. NAHID, SHAKIL, or admin@roseway.app") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_username"),
                        singleLine = true
                    )

                    // Email Conversion Hint
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Text(
                            text = "Supabase Auth Identity: $computedEmail",
                            color = EmeraldDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_password"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (usernameOrEmail.isNotBlank()) {
                                onLoginSubmit(usernameOrEmail, password)
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
                            .testTag("btn_login_submit")
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Authenticate & Enter Dashboard",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Role Switcher / Demo Presets for Testing
            Text(
                text = "Quick Demo / One-Tap Role Switcher",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = AviationNavy,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val testAccounts = listOf(
                Triple("S002 (MD NAHID)", "NAHID", "Staff Dashboard • Assigned Leads"),
                Triple("S003 (MD SHAKIL)", "SHAKIL", "Staff Dashboard • Assigned Leads"),
                Triple("Admin / CEO", "admin@roseway.app", "Admin Dashboard • All Access"),
                Triple("S009 (HIRU)", "HIRU", "Marketing Manager Dashboard"),
                Triple("Computer Operator", "operator@roseway.app", "Passport Data Entry & BMET"),
                Triple("Accounts Manager", "accounts@roseway.app", "Billing & Payment Receipts")
            )

            testAccounts.forEach { (label, user, desc) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            usernameOrEmail = user
                            onLoginSubmit(user, "RoseWay@2026")
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                text = label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AviationNavy
                            )
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = EmeraldPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Supabase Architecture Info Card
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AviationNavyLight.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Supabase Production Configuration",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = AviationNavy
                    )
                    Text(
                        text = "• Project: nzxustifqdaqxfwplwrg\n• Org ID: 11111111-1111-4111-8111-111111111111\n• Staff ID preservation: S001-S009 authoritative",
                        fontSize = 10.sp,
                        color = SlateTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
