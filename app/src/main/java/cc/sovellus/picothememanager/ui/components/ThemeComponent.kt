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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cc.sovellus.picothememanager.Constants.PICO_SCENE_MANAGER
import cc.sovellus.picothememanager.R
import cc.sovellus.picothememanager.manager.EnvironmentManager
import cc.sovellus.picothememanager.utils.checkSecurePermission
import cc.sovellus.picothememanager.utils.requestPicoDeletion

@Composable
fun ThemeComponent(
    environmentManager: EnvironmentManager,
    packageName: String,
    sceneTag: String,
    sceneName: String
) {
    val context = LocalContext.current

    val bitmapRoutine = rememberCoroutineScope()
    val bitmap = remember(sceneTag) {
        bitmapRoutine.run {
            environmentManager.getThumbnail(packageName, sceneTag)
        }
    }

    val resources = context.packageManager.getResourcesForApplication(packageName)
    val backgroundMusicEnabled = remember(packageName) { resources.getIdentifier("backgroundMusic", "integer", packageName) > 0 }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(120.dp)
            .width(200.dp)
            .clip(RoundedCornerShape(10))
            .clickable(onClick = {
                context.checkSecurePermission {
                    environmentManager.applyEnvironment(packageName, sceneTag, backgroundMusicEnabled)
                }
            })
            .hoverable(interactionSource),
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier
                .height(120.dp)
                .width(200.dp)
                .zIndex(0f),
            contentScale = ContentScale.Crop,
            alpha = if (isHovered) {
                0.4f
            } else {
                1.0f
            },
        )
        if (isHovered) {
            Column(
                modifier = Modifier
                    .height(120.dp)
                    .width(200.dp)
                    .padding(4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sceneName,
                        modifier = Modifier
                            .weight(1f)
                            .zIndex(1f),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    if (packageName != PICO_SCENE_MANAGER) {
                        IconButton(
                            modifier = Modifier
                                .width(28.dp)
                                .height(28.dp),
                            onClick = {
                                context.requestPicoDeletion(packageName, sceneName)
                            }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp)
                            )
                        }
                    }
                }
                if (backgroundMusicEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .width(12.dp)
                                .height(12.dp)
                        )
                        Text(
                            text = stringResource(R.string.theme_contains_audio),
                            modifier = Modifier
                                .weight(1f)
                                .zIndex(1f)
                                .padding(4.dp),
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}