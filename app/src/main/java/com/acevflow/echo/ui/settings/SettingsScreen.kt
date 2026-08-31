package com.acevflow.echo.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.acevflow.echo.ui.theme.Dims

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToEqualizer: () -> Unit,
    onNavigateToExcludedFolders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState(initial = 0)
    val dynamicColorEnabled by viewModel.dynamicColorEnabled.collectAsState(initial = true)
    val crossfadeDuration by viewModel.crossfadeDuration.collectAsState(initial = 0)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dims.ScreenPadding)
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = Dims.ElementPadding)
            )

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(Modifier.selectableGroup().padding(Dims.SmallPadding)) {
                    ThemeOption("System default", themeMode == 0) { viewModel.setThemeMode(0) }
                    ThemeOption("Light", themeMode == 1) { viewModel.setThemeMode(1) }
                    ThemeOption("Dark", themeMode == 2) { viewModel.setThemeMode(2) }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = Dims.SmallPadding))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dims.ElementPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dynamic Color",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Use colors from your wallpaper (Android 12+)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = dynamicColorEnabled,
                    onCheckedChange = { viewModel.setDynamicColorEnabled(it) }
                )
            }

            Spacer(modifier = Modifier.padding(vertical = Dims.SmallPadding))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToExcludedFolders),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(Dims.ElementPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Library Control",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Hide specific folders from your music library",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = Dims.SmallPadding))

            Text(
                text = "Audio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = Dims.ElementPadding)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToEqualizer),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(Dims.ElementPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Equalizer",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Configure audio bands and presets",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = Dims.SmallPadding))

            Text(
                text = "Cross-fade Duration (${crossfadeDuration}s)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = Dims.SmallPadding)
            )

            var sliderValue by remember(crossfadeDuration) { mutableFloatStateOf(crossfadeDuration.toFloat()) }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    viewModel.setCrossfadeDuration(sliderValue.toInt())
                },
                valueRange = 0f..10f,
                steps = 9
            )
            Text(
                text = if (crossfadeDuration == 0) "Gapless playback only" else "Overlap tracks by ${crossfadeDuration} seconds",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = Dims.ElementPadding)
        )
    }
}
