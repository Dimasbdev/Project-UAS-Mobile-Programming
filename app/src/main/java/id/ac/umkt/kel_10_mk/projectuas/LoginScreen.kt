package id.ac.umkt.kel_10_mk.projectuas

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay

private val BackgroundColor = Color(0xFF0A0E1A)
private val AccentColor = Color(0xFF00D4AA)
private val PrimaryTextColor = Color(0xFFF9FAFB)
private val SecondaryTextColor = Color(0xFF9CA3AF)
private val DividerTextColor = Color(0xFF6B7280)
private val ErrorColor = Color(0xFFEF4444)
private val CardColor = Color(0xFF1A1F2E)
private val FieldBorderColor = Color(0xFF2D3748)
private val PlaceholderColor = Color(0xFF4B5563)
private val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.spacegrotesk_semibold, FontWeight.SemiBold),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

private fun isValidCampusEmail(value: String): Boolean =
    value.isNotBlank() && value.contains(".ac.id")

private fun isValidPassword(value: String): Boolean =
    value.isNotBlank() && value.length >= 8

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotClick: () -> Unit,
) {
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = BackgroundColor.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateAndSubmit() {
        val trimmedEmail = email.trim()
        emailError = when {
            trimmedEmail.isBlank() -> "Email kampus wajib diisi"
            !trimmedEmail.contains(".ac.id") -> "Gunakan email kampus (.ac.id)"
            else -> null
        }
        passwordError = when {
            password.isBlank() -> "Password wajib diisi"
            password.length < 8 -> "Password minimal 8 karakter"
            else -> null
        }

        if (emailError == null && passwordError == null) {
            isLoading = true
            onLoginClick(trimmedEmail, password)
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(800)
            isLoading = false
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedTextColor = PrimaryTextColor,
        unfocusedTextColor = PrimaryTextColor,
        focusedPlaceholderColor = PlaceholderColor,
        unfocusedPlaceholderColor = PlaceholderColor,
        cursorColor = AccentColor,
        focusedBorderColor = AccentColor,
        unfocusedBorderColor = FieldBorderColor,
        errorBorderColor = ErrorColor,
        errorPlaceholderColor = PlaceholderColor,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 350.dp)
                .padding(top = 124.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "PARKIRUMKT",
                color = AccentColor,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                letterSpacing = 2.6.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Parkir Cerdas, Kampus Lancar",
                color = SecondaryTextColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardColor, RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, FieldBorderColor.copy(alpha = 0.75f)), RoundedCornerShape(14.dp))
                    .padding(horizontal = 25.dp, vertical = 24.dp),
            ) {
                Text(
                    text = "EMAIL KAMPUS",
                    color = SecondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null && isValidCampusEmail(it.trim())) {
                            emailError = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    singleLine = true,
                    placeholder = { Text(text = "nim@umkt.ac.id") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    isError = emailError != null,
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors,
                )

                if (emailError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = emailError.orEmpty(),
                        color = ErrorColor,
                        fontSize = 11.sp,
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PASSWORD",
                    color = SecondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError != null && isValidPassword(it)) {
                            passwordError = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    singleLine = true,
                    placeholder = { Text(text = "••••••••") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password",
                                tint = SecondaryTextColor,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                    isError = passwordError != null,
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors,
                )

                if (passwordError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = passwordError.orEmpty(),
                        color = ErrorColor,
                        fontSize = 11.sp,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Lupa Password?",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable(role = Role.Button) { onForgotClick() },
                    color = AccentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { validateAndSubmit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, AccentColor.copy(alpha = 0.65f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor,
                    contentColor = BackgroundColor,
                    disabledContainerColor = AccentColor.copy(alpha = 0.8f),
                    disabledContentColor = BackgroundColor.copy(alpha = 0.8f),
                ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = BackgroundColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text(
                        text = "Masuk",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundColor,
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = DividerTextColor.copy(alpha = 0.6f),
                )
                Text(
                    text = "ATAU",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = SecondaryTextColor,
                    fontSize = 12.sp,
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = DividerTextColor.copy(alpha = 0.6f),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, FieldBorderColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CardColor,
                    contentColor = PrimaryTextColor,
                ),
            ) {
                Text(
                    text = "Daftar Akun Baru",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor,
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Gunakan email kampus (.ac.id) untuk mendaftar",
                color = DividerTextColor,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0A0E1A)
private fun LoginScreenPreview() {
    MaterialTheme {
        Box(modifier = Modifier.background(BackgroundColor)) {
            LoginScreen(
                onLoginClick = { _, _ -> },
                onRegisterClick = { },
                onForgotClick = { },
            )
        }
    }
}
