// PicoThemeManager
// Copyright (C) 2025 Nyabsi <nyabsi@sovellus.cc>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.


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