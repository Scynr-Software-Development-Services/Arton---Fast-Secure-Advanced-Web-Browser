package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WhmcsUser
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite

private val ScynrBlue = Color(0xFF1B2DE8)
private val SoftFieldBg = Color(0xFFF8F9FD)
private val SoftBorder = Color(0xFFE2E5F0)

@Composable
fun ScynrAuthScreen(
    user: WhmcsUser,
    isSyncing: Boolean,
    syncMessage: String,
    onLogin: (String, String) -> Unit,
    onRegister: (firstName: String, lastName: String, email: String, password: String, phoneNumber: String) -> Unit,
    onBack: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var currentStep by remember { mutableIntStateOf(1) } // 1, 2, 3

    // Form fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var dob by remember { mutableStateOf("15-08-1998") }
    var countryCode by remember { mutableStateOf("+63") }
    var otpDigits by remember { mutableStateOf(listOf("5", "8", "4", "")) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button in rounded squircle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (isRegisterMode && currentStep > 1) {
                                currentStep -= 1
                            } else {
                                onBack()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isRegisterMode && currentStep <= 3) {
                    // Step pill badge matching screenshot: "Step 1 of 3"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(ScynrBlue.copy(alpha = 0.10f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Step $currentStep of 3",
                            color = ScynrBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(42.dp))
                }

                Text(
                    text = "Need help?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* help */ }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (!isRegisterMode) {
                // ============================================
                // LOGIN SCREEN
                // ============================================
                Text(
                    text = "Login to Scynr",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Access your cross-platform cloud sync, bookmarks, and security vault.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // E-mail input
                ScynrInputField(
                    label = "E-mail Address",
                    value = email,
                    placeholder = "you@scynr.xyz",
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input
                Text(
                    text = "Password",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        focusedBorderColor = ScynrBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onLogin(email, password) },
                    enabled = email.isNotBlank() && password.isNotBlank() && !isSyncing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScynrBlue)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(color = UberPureWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(text = "Login to Scynr", color = UberPureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Social divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Text(
                        text = "  or continue with  ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                SocialButton(text = "Continue with Google", iconText = "G") {
                    onLogin("demo.user@scynr.xyz", "ScynrDemoPass123!")
                }

                Spacer(modifier = Modifier.height(10.dp))

                SocialButton(text = "Continue with Apple", iconText = "") {
                    onLogin("apple.user@scynr.xyz", "ScynrApplePass123!")
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Don't have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    TextButton(onClick = {
                        isRegisterMode = true
                        currentStep = 1
                    }) {
                        Text(text = "Register", color = ScynrBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            } else {
                // ============================================
                // MULTI-STEP REGISTRATION SCREEN
                // ============================================
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                        }
                    },
                    label = "register_step"
                ) { step ->
                    when (step) {
                        1 -> {
                            // Step 1: E-mail Address & Social
                            Column {
                                Text(
                                    text = "Create an Account",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    text = "Enter your primary email address to get started with Scynr Cloud.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                                )

                                ScynrInputField(
                                    label = "E-mail",
                                    value = email,
                                    placeholder = "muhammad@email.com",
                                    onValueChange = { email = it },
                                    keyboardType = KeyboardType.Email
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = { currentStep = 2 },
                                    enabled = email.isNotBlank() && email.contains("@"),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ScynrBlue)
                                ) {
                                    Text(text = "Continue", color = UberPureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                    Text(text = "  or continue with  ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                SocialButton(text = "Continue with Google", iconText = "G") {
                                    email = "muhammad.ali@gmail.com"
                                    currentStep = 2
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                SocialButton(text = "Continue with Apple", iconText = "") {
                                    email = "muhammad.apple@icloud.com"
                                    currentStep = 2
                                }

                                Spacer(modifier = Modifier.height(30.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Already have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                                    TextButton(onClick = { isRegisterMode = false }) {
                                        Text(text = "Login to Scynr", color = ScynrBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Step 2: Personal Information (matching screenshot 2 & 3)
                            Column {
                                Text(
                                    text = "Personal Information",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Please enter your profile details.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                                )

                                // Circular Avatar with User Icon
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(ScynrBlue.copy(alpha = 0.12f))
                                        .align(Alignment.CenterHorizontally),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Avatar",
                                        tint = ScynrBlue,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                ScynrInputField(
                                    label = "Full Name",
                                    value = firstName,
                                    placeholder = "Muhammad",
                                    onValueChange = { firstName = it }
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                ScynrInputField(
                                    label = "Surname",
                                    value = lastName,
                                    placeholder = "Ali",
                                    onValueChange = { lastName = it }
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Gender Picker
                                Text(
                                    text = "Gender",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .clickable { gender = if (gender == "male") "female" else "male" }
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = gender.replaceFirstChar { it.uppercase() }, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Phone with country code (+63)
                                Text(
                                    text = "Phone Number",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(52.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                            .padding(horizontal = 14.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🇵🇭 $countryCode", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = { phoneNumber = it },
                                        placeholder = { Text("923 123 124") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        singleLine = true
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Date of Birth
                                Text(
                                    text = "Date of Birth",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = dob,
                                    onValueChange = { dob = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    trailingIcon = {
                                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = ScynrBlue)
                                    },
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(22.dp))

                                Button(
                                    onClick = { currentStep = 3 },
                                    enabled = firstName.isNotBlank() && lastName.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ScynrBlue)
                                ) {
                                    Text(text = "Continue", color = UberPureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        3 -> {
                            // Step 3: Enter OTP Code / Email verification matching screenshot
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(modifier = Modifier.height(10.dp))

                                // Mail illustration
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Color(0xFFE8F0FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = "Mail confirmation",
                                        tint = ScynrBlue,
                                        modifier = Modifier.size(42.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Text(
                                    text = "Enter OTP Code",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black
                                )

                                Text(
                                    text = "Check your SMS! We've sent a one-time verification code to $countryCode ${phoneNumber.ifBlank { "923 123 124" }}. Enter the code below to verify your account.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // 4 OTP digit boxes matching screenshot: 5, 8, 4, _
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OtpBox(digit = "5", isFilled = true)
                                    OtpBox(digit = "8", isFilled = true)
                                    OtpBox(digit = "4", isFilled = true)
                                    OtpBox(digit = "", isFilled = false, isFocused = true)
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "You can resend the code in 56 seconds",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )

                                TextButton(onClick = { /* resend */ }) {
                                    Text(text = "Resend code", color = ScynrBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        onRegister(
                                            firstName,
                                            lastName,
                                            email,
                                            "ScynrUserPass#2026",
                                            "$countryCode$phoneNumber"
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ScynrBlue)
                                ) {
                                    if (isSyncing) {
                                        CircularProgressIndicator(color = UberPureWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text(text = "Complete Registration", color = UberPureWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (syncMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = syncMessage,
                    color = if (syncMessage.contains("Success", true)) UberGreen else Color(0xFFFF2A6D),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ScynrInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                focusedBorderColor = ScynrBlue,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

@Composable
private fun OtpBox(digit: String, isFilled: Boolean, isFocused: Boolean = false) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) ScynrBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                if (isFocused) ScynrBlue.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun SocialButton(text: String, iconText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (iconText.isNotBlank()) {
            Text(
                text = iconText,
                color = Color(0xFF4285F4),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
