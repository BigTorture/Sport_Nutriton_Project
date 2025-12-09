package com.example.myapplication.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.NutritionAppViewModel

@Composable
fun AuthScreen(viewModel: NutritionAppViewModel) {
    val authState by viewModel.authState.collectAsState()
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
            .verticalScroll(scroll)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Войдите или зарегистрируйтесь",
            style = MaterialTheme.typography.headlineMedium,
            color = AppPalette.TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Используйте email и номер телефона, чтобы получать SMS-коды. После подтверждения можно заполнить профиль, вести дневник питания, отслеживать воду и настраивать напоминания.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppPalette.TextSecondary
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, AppPalette.CardOutline),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = authState.fullName,
                    onValueChange = viewModel::updateAuthName,
                    label = { Text("Имя и фамилия") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = authState.email,
                    onValueChange = viewModel::updateAuthEmail,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = authState.phone,
                    onValueChange = viewModel::updateAuthPhone,
                    label = { Text("Мобильный телефон") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("+7...", color = AppPalette.TextMuted) },
                    colors = textFieldColors()
                )
                Button(
                    onClick = {
                        viewModel.requestSmsCode(authState.fullName, authState.email, authState.phone)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentMint),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(text = if (authState.isCodeSent) "Отправить код повторно" else "Получить SMS-код")
                }

                if (authState.isCodeSent) {
                    OutlinedTextField(
                        value = authState.enteredCode,
                        onValueChange = viewModel::updateEnteredCode,
                        label = { Text("SMS-код") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    Button(
                        onClick = { viewModel.verifyCode(authState.enteredCode) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentMint),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Подтвердить")
                    }
                    TextButton(
                        onClick = { viewModel.requestSmsCode(authState.fullName, authState.email, authState.phone) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Получить новый код", color = AppPalette.AccentLilac)
                    }
                }

                authState.error?.let {
                    Text(
                        text = it,
                        color = AppPalette.AccentPeach,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Text(
            text = "После подтверждения заполните анкету: рост, вес, возраст, уровень активности и цель. Это поможет точно рассчитать норму калорий, белков, жиров и углеводов.",
            color = AppPalette.TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedTextColor = AppPalette.TextPrimary,
    unfocusedTextColor = AppPalette.TextPrimary,
    cursorColor = AppPalette.AccentMint,
    focusedIndicatorColor = AppPalette.AccentMint,
    unfocusedIndicatorColor = AppPalette.CardOutline
)

