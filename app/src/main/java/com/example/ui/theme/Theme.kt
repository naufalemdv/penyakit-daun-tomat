package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TomatScanColorScheme = lightColorScheme(
    primary = Nila,
    onPrimary = OnNila,
    primaryContainer = NilaContainer,
    onPrimaryContainer = Color.White,
    secondary = Daun,
    onSecondary = Color.White,
    secondaryContainer = DaunPucat,
    onSecondaryContainer = Daun,
    tertiary = Kunyit,
    onTertiary = Tinta,
    tertiaryContainer = KunyitPucat,
    onTertiaryContainer = KunyitTeks,
    background = Kertas,
    onBackground = Tinta,
    surface = Kertas,
    onSurface = Tinta,
    surfaceVariant = Putih,
    onSurfaceVariant = Tinta2,
    outline = Garis,
    outlineVariant = Garis,
    error = Bata,
    onError = Color.White,
    errorContainer = BataPucat,
    onErrorContainer = Bata
)

@Composable
fun TomatScanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TomatScanColorScheme,
        typography = Typography,
        content = content
    )
}
