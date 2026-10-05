package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.Bata
import com.example.ui.theme.BataPucat
import com.example.ui.theme.Daun
import com.example.ui.theme.DaunPucat
import com.example.ui.theme.Garis
import com.example.ui.theme.Kertas
import com.example.ui.theme.Kunyit
import com.example.ui.theme.KunyitPucat
import com.example.ui.theme.KunyitTeks
import com.example.ui.theme.Malam
import com.example.ui.theme.Nila
import com.example.ui.theme.NilaPucat
import com.example.ui.theme.NilaTekan
import com.example.ui.theme.Putih
import com.example.ui.theme.Tinta
import com.example.ui.theme.Tinta2
import com.example.viewmodel.NavTab

@Composable
fun TomatScanHeader(
    title: String = "TomatScan",
    subtitle: String? = "Beranda",
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    showAboutAction: Boolean = false,
    onAboutClick: () -> Unit = {},
    showShareAction: Boolean = false,
    onShareClick: () -> Unit = {},
    showDeleteAction: Boolean = false,
    onDeleteClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Kertas.copy(alpha = 0.95f))
            .border(width = 1.dp, color = Garis)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (showBack) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .testTag("btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Tinta
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tomatscan_logo),
                        contentDescription = "Logo TomatScan",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Nila,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = "| $subtitle",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Tinta2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Right utility buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showShareAction) {
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_share")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan Hasil",
                            tint = Tinta2
                        )
                    }
                }

                if (showDeleteAction) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_delete")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Riwayat",
                            tint = Bata
                        )
                    }
                }

                if (showAboutAction) {
                    IconButton(
                        onClick = onAboutClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_about")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tentang TomatScan",
                            tint = Tinta2
                        )
                    }
                }

                // Profile avatar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Nila),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil",
                        tint = Putih,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TomatScanBottomBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onScanClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Kertas)
            .border(width = 1.dp, color = Garis)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Tab 1: Beranda
            BottomNavItem(
                label = "Beranda",
                icon = Icons.Default.Home,
                selected = currentTab == NavTab.BERANDA,
                onClick = { onTabSelected(NavTab.BERANDA) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_beranda")
            )

            // Tab 2: Riwayat
            BottomNavItem(
                label = "Riwayat",
                icon = Icons.Default.History,
                selected = currentTab == NavTab.RIWAYAT,
                onClick = { onTabSelected(NavTab.RIWAYAT) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_riwayat")
            )

            // Center Elevated Scan Action
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .offset(y = (-14).dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onScanClick() }
                        .testTag("btn_pindai_center")
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Nila)
                            .border(width = 2.dp, color = Putih, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Pindai Daun Tomat",
                            tint = Putih,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Pindai",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Nila,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Tab 3: Info Penyakit
            BottomNavItem(
                label = "Info",
                icon = Icons.Default.MenuBook,
                selected = currentTab == NavTab.INFO,
                onClick = { onTabSelected(NavTab.INFO) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_info")
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Nila else Tinta2,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Nila else Tinta2,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun StatusBadge(
    isHealthy: Boolean,
    isUncertain: Boolean = false,
    text: String? = null
) {
    val (bgColor, borderColor, textColor, icon) = when {
        isUncertain -> Quadruple(KunyitPucat, Kunyit, KunyitTeks, Icons.Default.Help)
        isHealthy -> Quadruple(DaunPucat, Daun, Daun, Icons.Default.CheckCircle)
        else -> Quadruple(BataPucat, Bata, Bata, Icons.Default.Warning)
    }

    val displayText = text ?: when {
        isUncertain -> "Tidak Yakin"
        isHealthy -> "Sehat"
        else -> "Terinfeksi"
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = displayText,
            tint = textColor,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun NoticeBanner(
    text: String = "Pemberitahuan Batasan: Hasil ini adalah skrining awal model SVM lokal dan bukan pengganti diagnosis ahli pertanian atau uji laboratorium fitopatologi."
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Putih)
            .border(width = 1.dp, color = Garis, shape = RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Pemberitahuan",
            tint = Tinta2,
            modifier = Modifier
                .size(20.dp)
                .offset(y = 2.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Tinta2,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun FirstTimeGuideDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Nila
                )
                Text(
                    text = "Panduan Memotret di Kebun",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GuideTipRow(number = "1", title = "Satu Daun Utama", desc = "Pastikan hanya satu helai daun bergejala masuk ke bingkai retikel pemindaian.")
                GuideTipRow(number = "2", title = "Tepat di Tengah Bidik", desc = "Pusatkan bercak daun pada titik fokus agar ekstraksi 23 fitur GLCM presisi.")
                GuideTipRow(number = "3", title = "Pencahayaan Matahari Rata", desc = "Gunakan cahaya alami cukup terang. Hindari bayangan badan atau topi.")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Nila),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.testTag("btn_guide_understood")
            ) {
                Text("Mengerti", color = Putih, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Putih,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun GuideTipRow(number: String, title: String, desc: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(NilaPucat),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Nila
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Tinta
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = Tinta2
            )
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    title: String = "Hapus Hasil Diagnosis?",
    message: String = "Hasil deteksi ini akan dihapus permanen dari riwayat lokal.",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Tinta
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Tinta2
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Bata),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.testTag("btn_confirm_delete")
            ) {
                Text("Hapus", color = Putih, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(999.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Text("Batal", color = Tinta, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = Putih,
        shape = RoundedCornerShape(16.dp)
    )
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
