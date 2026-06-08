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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
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
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirInputBorder
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirPlaceholder
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily
import kotlinx.coroutines.delay

private val BackgroundColor = ParkirBackground
private val AccentColor = ParkirAccent
private val PrimaryTextColor = ParkirTextPrimary
private val SecondaryTextColor = ParkirTextSecondary
private val ErrorColor = ParkirDanger
private val CardColor = ParkirSurface
private val FieldBorderColor = ParkirInputBorder
private val PlaceholderColor = ParkirPlaceholder
private val SpaceGrotesk = SpaceGroteskFamily

private fun isValidCampusEmail(value: String): Boolean =
    value.isNotBlank() && value.trim().endsWith(".ac.id")

private fun isValidPassword(value: String): Boolean =
    value.isNotBlank() && value.length >= 8

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterClick: (name: String, email: String, password: String) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean = false,
) {
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = BackgroundColor.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    fun validateAndSubmit() {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        nameError = when {
            trimmedName.isBlank() -> "Nama lengkap wajib diisi"
            trimmedName.length < 3 -> "Nama terlalu pendek"
            else -> null
        }
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
        confirmPasswordError = when {
            confirmPassword.isBlank() -> "Konfirmasi password wajib diisi"
            confirmPassword != password -> "Password belum sama"
            else -> null
        }

        if (nameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
            onRegisterClick(trimmedName, trimmedEmail, password)
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
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 380.dp)
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = PrimaryTextColor,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Buat Akun Baru",
                    color = PrimaryTextColor,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 380.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(CardColor, CircleShape)
                    .border(BorderStroke(1.dp, FieldBorderColor), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = AccentColor,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bergabunglah dengan Sentinel untuk\npengalaman parkir kampus tanpa hambatan.",
                color = SecondaryTextColor,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = "Nama Lengkap",
                    color = SecondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError != null && it.trim().length >= 3) {
                            nameError = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    singleLine = true,
                    placeholder = { Text(text = "John Doe") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SecondaryTextColor,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    isError = nameError != null,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors,
                )

                if (nameError != null) {
                    Text(
                        text = nameError.orEmpty(),
                        color = ErrorColor,
                        fontSize = 11.sp,
                    )
                }

                Text(
                    text = "Email Kampus",
                    color = SecondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                )

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
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = SecondaryTextColor,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    isError = emailError != null,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors,
                )

                if (emailError != null) {
                    Text(
                        text = emailError.orEmpty(),
                        color = ErrorColor,
                        fontSize = 11.sp,
                    )
                }

                Text(
                    text = "Password",
                    color = SecondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                )

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
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SecondaryTextColor,
                            modifier = Modifier.size(20.dp),
                        )
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = passwordError != null,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors,
                )

                if (passwordError != null) {
                    Text(
                        text = passwordError.orEmpty(),
                        color = ErrorColor,
                        fontSize = 11.sp,
                    )
                }

                Text(
                    text = "Konfirmasi Password",
                    color = SecondaryTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (confirmPasswordError != null && it == password) {
                            confirmPasswordError = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    singleLine = true,
                    placeholder = { Text(text = "••••••••") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SecondaryTextColor,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Sembunyikan password" else "Tampilkan password",
                                tint = SecondaryTextColor,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                    visualTransformation = if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = confirmPasswordError != null,
                    shape = RoundedCornerShape(10.dp),
                    colors = textFieldColors,
                )

                if (confirmPasswordError != null) {
                    Text(
                        text = confirmPasswordError.orEmpty(),
                        color = ErrorColor,
                        fontSize = 11.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = { validateAndSubmit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
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
                        text = "Daftar Sekarang",
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundColor,
                        fontSize = 16.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    color = SecondaryTextColor,
                    fontSize = 13.sp,
                )
                Text(
                    text = "Masuk di sini",
                    color = AccentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(role = Role.Button) { onLoginClick() },
                )
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0A0E1A)
private fun RegisterScreenPreview() {
    MaterialTheme {
        Box(modifier = Modifier.background(BackgroundColor)) {
            RegisterScreen(
                onBackClick = {},
                onRegisterClick = { _, _, _ -> },
                onLoginClick = {},
            )
        }
    }
}
