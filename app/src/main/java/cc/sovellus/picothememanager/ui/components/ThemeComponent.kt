package cc.sovellus.picothememanager.ui.components

import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cc.sovellus.picothememanager.Constants.PICO_SCENE_MANAGER
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
                    environmentManager.applyEnvironment(packageName, sceneTag)
                }
            })
            .hoverable(interactionSource),
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
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
            Row(
                modifier = Modifier.width(200.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = sceneName,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .zIndex(1f),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                if (packageName != PICO_SCENE_MANAGER) {
                    IconButton(
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp),
                        onClick = {
                            context.requestPicoDeletion(packageName, sceneName)
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                        )
                    }
                }
            }
        }
    }
}