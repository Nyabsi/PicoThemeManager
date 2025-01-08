package cc.sovellus.picothememanager.ui.components

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import cc.sovellus.picothememanager.Constants
import cc.sovellus.picothememanager.Constants.PICO_SCENE_MANAGER
import cc.sovellus.picothememanager.R
import cc.sovellus.picothememanager.manager.EnvironmentManager

@Composable
fun ThemeComponent(
    environmentManager: EnvironmentManager,
    packageName: String,
    sceneTag: String,
    sceneName: String
) {
    val context = LocalContext.current
    val assets = context.packageManager.getResourcesForApplication(packageName).assets
    val bitmap = remember(sceneTag) { BitmapFactory.decodeStream(assets.open("thumbs/${sceneTag}/Scene_${sceneTag}_1_1.png")) }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    assets.close()

    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(120.dp)
            .width(200.dp)
            .clip(RoundedCornerShape(10))
            .clickable(onClick = {
                if (ActivityCompat.checkSelfPermission(
                        context, Constants.ANDROID_PERMISSION_SECURE_SETTINGS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    environmentManager.applyEnvironment(packageName, sceneTag)
                } else {
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.toast_no_permission),
                            Toast.LENGTH_LONG
                        )
                        .show()
                }
            })
            .hoverable(interactionSource),
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
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
                            Intent().apply {
                                setComponent(
                                    ComponentName(
                                        "com.android.permissioncontroller",
                                        "com.android.packageinstaller.permission.ui.pico.AppPermissionsActivity"
                                    )
                                )
                                putExtra(
                                    "android.intent.extra.PACKAGE_NAME",
                                    packageName
                                )
                                putExtra(
                                    "pico_permission_app_name", sceneName
                                )
                            }.run {
                                context.startActivity(this)
                            }
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(
                                R.string.delete_environment,
                                sceneName
                            ),
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