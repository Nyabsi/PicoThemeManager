package cc.sovellus.picothememanager.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import cc.sovellus.picothememanager.manager.EnvironmentManager

@Composable
private fun ResolutionButton(menuExpanded: MutableState<Boolean>, environmentManager: EnvironmentManager, resolution: Int) {
    DropdownMenuItem(text = {
        Text("${resolution}x${resolution}")
    }, onClick = {
        environmentManager.setShellResolution(resolution)
        menuExpanded.value = false
    })
}

@Composable
fun ResolutionDropdown(menuExpanded: MutableState<Boolean>, environmentManager: EnvironmentManager) {
    DropdownMenu(
        expanded = menuExpanded.value,
        onDismissRequest = { menuExpanded.value = false }
    ) {
        ResolutionButton(menuExpanded, environmentManager, 1080)
        ResolutionButton(menuExpanded, environmentManager, 1504)
        ResolutionButton(menuExpanded, environmentManager, 1920)
        ResolutionButton(menuExpanded, environmentManager, 2160)
        ResolutionButton(menuExpanded, environmentManager, 2800)
    }
}