package id.serviskus.app.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

val ServisKuShapes = Shapes(
    small = RoundedCornerShape(Spacing.sm),
    medium = RoundedCornerShape(Spacing.md),
    large = RoundedCornerShape(Spacing.md),
)

@Composable
fun ServisKuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: VehicleAccent = VehicleAccent.DEFAULT,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> servisKuDarkColorScheme(accent)
        else -> servisKuLightColorScheme(accent)
    }

    val statusColors = if (darkTheme) DarkStatusColors else LightStatusColors

    CompositionLocalProvider(LocalStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ServisKuTypography,
            shapes = ServisKuShapes,
            content = content,
        )
    }
}
