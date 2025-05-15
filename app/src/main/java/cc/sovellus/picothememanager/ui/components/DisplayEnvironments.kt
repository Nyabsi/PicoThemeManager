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

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cc.sovellus.picothememanager.manager.EnvironmentManager

@SuppressLint("DiscouragedApi")
@Composable
fun DisplayEnvironments(list: State<SnapshotStateList<PackageInfo>>, environmentManager: EnvironmentManager) {
    val context = LocalContext.current

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 4.dp,
            end = 16.dp,
            bottom = 4.dp
        ),
        modifier = Modifier.heightIn(max = 120.dp),
        content = {
            items(list.value) {
                val packageName = it.packageName
                val resources = context.packageManager.getResourcesForApplication(packageName)

                val sceneTagId = resources.getIdentifier("sceneTag", "string", packageName)
                val sceneTag = resources.getString(sceneTagId)

                val sceneNameId = resources.getIdentifier("SceneName_$sceneTag", "string", it.packageName)
                var sceneName = sceneTag
                if (sceneNameId > 0) {
                    sceneName = resources.getString(sceneNameId)
                }

                if (sceneTagId > 0) {
                    ThemeComponent(environmentManager, packageName, sceneTag, sceneName)
                }
            }
        }
    )
}