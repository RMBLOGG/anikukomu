package com.dayynime.anikukomu.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayynime.anikukomu.ui.theme.AnikuAccent
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurface
import com.dayynime.anikukomu.ui.theme.AnikuTextPrimary
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestLoginDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    if (show) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = AnikuSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Yuk Login Dulu! 🌸",
                    style = AnikuTypography.displayLarge.copy(fontSize = 24.sp),
                    color = AnikuPink,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Bergabung dengan komunitas anime AnikuKomu untuk like, komen, dan follow sesama weeb!",
                    style = AnikuTypography.bodyLarge,
                    color = AnikuTextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        onDismiss()
                        onLoginClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnikuAccent),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Login", fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = {
                        onDismiss()
                        onRegisterClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnikuPink),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Daftar", fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Nanti Aja", color = AnikuTextSecondary)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
