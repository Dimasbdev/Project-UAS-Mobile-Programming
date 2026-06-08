package id.ac.umkt.kel_10_mk.projectuas

import android.app.Activity
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirInputBorder
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirPlaceholder
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily

private fun isValidCampusEmail(value: String): Boolean =
    value.isNotBlank() && value.trim().endsWith(".ac.id")

private fun isValidPassword(value: String): Boolean =
    value.isNotBlank() && value.length >= 8

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotClick: () -> Unit,
    isLoading: Boolean = false,
) {
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = ParkirBackground.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateAndSubmit() {
        val trimmedEmail = email.trim()
        emailError = when {
            trimmedEmail.isBlank() -> "Email kampus wajib diisi"
            !trimmedEmail.endsWith(".ac.id") -> "Gunakan email kampus (.ac.id)"
            else -> null
        }
        passwordError = when {
            password.isBlank() -> "Password wajib diisi"
            password.length < 8 -> "Password minimal 8 karakter"
            else -> null
        }

        if (emailError == null && passwordError == null) {
            onLogin(trimmedEmail, password)
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedTextColor = ParkirTextPrimary,
        unfocusedTextColor = ParkirTextPrimary,
        focusedPlaceholderColor = ParkirPlaceholder,
        unfocusedPlaceholderColor = ParkirPlaceholder,
        cursorColor = ParkirAccent,
        focusedBorderColor = ParkirAccent,
        unfocusedBorderColor = ParkirInputBorder,
        errorBorderColor = ParkirDanger,
        errorPlaceholderColor = ParkirPlaceholder,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ParkirBackground)
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
                color = ParkirAccent,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                letterSpacing = 2.6.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Parkir Cerdas, Kampus Lancar",
                color = ParkirTextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ParkirSurface, RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, ParkirInputBorder.copy(alpha = 0.75f)), RoundedCornerShape(14.dp))
                    .padding(horizontal = 25.dp, vertical = 24.dp),
            ) {
                Text(
                    text = "EMAIL KAMPUS",
                    color = ParkirTextSecondary,
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
                        .heightIn(min = 56.dp),
                    singleLine = true,
                    placeholder = { Text(text = "nim@umkt.ac.id") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    isError = emailError != null,
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors,
                )

                if (emailError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = emailError.orEmpty(),
                        color = ParkirDanger,
                        fontSize = 11.sp,
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "PASSWORD",
                    color = ParkirTextSecondary,
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
                        .heightIn(min = 56.dp),
                    singleLine = true,
                    placeholder = { Text(text = "••••••••") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { validateAndSubmit() },
                    ),
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
                                tint = ParkirTextSecondary,
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
                        color = ParkirDanger,
                        fontSize = 11.sp,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Lupa Password?",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable(role = Role.Button) { onForgotClick() },
                    color = ParkirAccent,
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
                border = BorderStroke(1.dp, ParkirAccent.copy(alpha = 0.65f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkirAccent,
                    contentColor = ParkirBackground,
                    disabledContainerColor = ParkirAccent.copy(alpha = 0.8f),
                    disabledContentColor = ParkirBackground.copy(alpha = 0.8f),
                ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = ParkirBackground,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text(
                        text = "Masuk",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        color = ParkirBackground,
                        fontSize = 16.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Belum punya akun? ",
                    color = ParkirTextSecondary,
                    fontSize = 13.sp,
                )
                Text(
                    text = "Daftar di sini",
                    color = ParkirAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(role = Role.Button) { onRegisterClick() },
                )
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0A0E1A)
private fun LoginScreenPreview() {
    MaterialTheme {
        Box(modifier = Modifier.background(ParkirBackground)) {
            LoginScreen(
                onLogin = { _, _ -> },
                onRegisterClick = { },
                onForgotClick = { },
            )
        }
    }
}
