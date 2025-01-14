package cc.sovellus.picothememanager.manager

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
import android.graphics.BitmapFactory
import android.provider.Settings
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import cc.sovellus.picothememanager.Constants.PICO_VRSHELL
import cc.sovellus.picothememanager.Constants.VRSHELL_MINIMUM_VERSION_FOR_RESOLUTION
import cc.sovellus.picothememanager.extension.lastUsedTheme
import cc.sovellus.picothememanager.service.AudioService

class EnvironmentManager(
    context: Context
) : ContextWrapper(context) {

    private var previousSelection = ""
    private val cachedThumbnails = mutableMapOf<String, ImageBitmap>()
    private val preferences = getSharedPreferences("pico_theme_manager_prefs", MODE_PRIVATE)

    fun getThumbnail(pkg: String, tag: String): ImageBitmap {
        val key = "$pkg/$tag"

        val cached = cachedThumbnails[key]
        if (cached != null) {
            return cached
        }

        val assets = packageManager.getResourcesForApplication(pkg).assets
        val bitmap = BitmapFactory
            .decodeStream(assets.open("thumbs/${tag}/Scene_${tag}_1_1.png"))
            .asImageBitmap()
        assets.close()

        cachedThumbnails[key] = bitmap
        return bitmap
    }

    fun clearThumbnailCache() {
        cachedThumbnails.clear()
    }

    fun isResolutionOptionAvailable(): Boolean {
        return packageManager.getPackageInfo(PICO_VRSHELL, 0).longVersionCode >= VRSHELL_MINIMUM_VERSION_FOR_RESOLUTION
    }

    fun setShellResolution(resolution: Int) {
        Settings.Global.putString(contentResolver, "sys_set_vrshell_eyebuffer", resolution.toString())
    }

    fun getPackageList(): SnapshotStateList<PackageInfo> {
        return packageManager.getInstalledPackages(GET_SIGNING_CERTIFICATES).filter { packageInfo ->
            packageInfo.packageName.contains(Regex("com.pvr.[^.]+.scene")) && !packageInfo.signingInfo!!.apkContentsSigners!![0]!!.toByteArray().contentEquals(PICO_CERTIFICATE)
        }.toMutableStateList()
    }

    fun getSystemPackageList(): SnapshotStateList<PackageInfo> {
        return packageManager.getInstalledPackages(GET_SIGNING_CERTIFICATES).filter { packageInfo ->
            packageInfo.packageName.contains(Regex("com.pvr.[^.]+.scene")) && packageInfo.signingInfo!!.apkContentsSigners!![0]!!.toByteArray().contentEquals(PICO_CERTIFICATE)
        }.toMutableStateList()
    }

    fun resetEnvironment() {
        Settings.Global.putString(contentResolver, "SceneManager.CurPackage", null)
        Settings.Global.putString(contentResolver, "SceneManager.CurrentScene", "")
        Settings.Global.putString(contentResolver, "current_scene", "default_scene")
        Settings.Global.putInt(contentResolver, "current_support_skybox", 0)
        Settings.Global.putString(contentResolver, "current_scene_custom", null)
        Settings.Global.putString(contentResolver, "scene_change_type", "${System.currentTimeMillis()}1")
        previousSelection = ""
    }

    fun applyEnvironment(pkg: String, tag: String, hasAudio: Boolean) {
        if (previousSelection != tag) {
            Settings.Global.putString(contentResolver, "SceneManager.CurPackage", pkg)
            Settings.Global.putString(contentResolver, "SceneManager.CurrentScene", tag)
            Settings.Global.putString(contentResolver, "current_scene", "/assets/scene/$tag/Scene_${tag}_1_1.unity3d")
            Settings.Global.putString(contentResolver, "scene_change_type", "${System.currentTimeMillis()}1")

            if (hasAudio) {
                val serviceIntent = Intent(this, AudioService::class.java)
                stopService(serviceIntent)

                serviceIntent.putExtra("packageName", pkg)
                startForegroundService(serviceIntent)
            } else {
                val serviceIntent = Intent(this, AudioService::class.java)
                stopService(serviceIntent)
            }

            preferences.lastUsedTheme = pkg
            previousSelection = tag
        }
    }

    companion object {
        private val PICO_CERTIFICATE = byteArrayOf(48,-126,1,-33,48,-126,1,72,2,1,1,48,13,6,9,42,-122,72,-122,-9,13,1,1,5,5,0,48,55,49,22,48,20,6,3,85,4,3,12,13,65,110,100,114,111,105,100,32,68,101,98,117,103,49,16,48,14,6,3,85,4,10,12,7,65,110,100,114,111,105,100,49,11,48,9,6,3,85,4,6,19,2,85,83,48,32,23,13,50,49,49,48,49,51,48,53,53,51,53,52,90,24,15,50,48,53,49,49,48,48,54,48,53,53,51,53,52,90,48,55,49,22,48,20,6,3,85,4,3,12,13,65,110,100,114,111,105,100,32,68,101,98,117,103,49,16,48,14,6,3,85,4,10,12,7,65,110,100,114,111,105,100,49,11,48,9,6,3,85,4,6,19,2,85,83,48,-127,-97,48,13,6,9,42,-122,72,-122,-9,13,1,1,1,5,0,3,-127,-115,0,48,-127,-119,2,-127,-127,0,-71,51,-66,28,-42,93,62,-105,13,58,-100,8,-22,-10,-112,-72,-66,-45,78,-65,9,42,81,-53,92,116,-10,7,104,-79,76,94,-125,86,105,-36,25,9,65,85,-54,-5,83,-57,99,10,-72,-57,-47,89,56,73,-95,42,112,-87,94,-43,-55,-49,122,7,-61,-3,-24,-74,-105,-113,75,18,65,-28,-71,28,5,83,35,6,-70,-70,-108,-53,-70,-6,-18,62,90,-58,-24,73,-37,-76,98,20,-87,-73,-127,124,-13,-87,-118,-56,-69,-33,-46,-49,112,31,69,-47,-64,123,-44,65,115,-28,42,-116,56,-21,54,51,105,-2,-48,-125,14,-17,2,3,1,0,1,48,13,6,9,42,-122,72,-122,-9,13,1,1,5,5,0,3,-127,-127,0,-116,-92,29,-25,120,-99,-51,-58,111,-81,111,109,-39,7,27,6,-32,-34,34,-71,-109,-5,-109,-94,40,-39,-6,21,3,-34,119,108,-52,61,-73,108,71,118,107,-82,48,53,40,53,-63,-32,38,-112,70,24,-102,-9,-124,-40,-38,96,-1,85,124,16,-39,121,-28,27,-18,-124,-119,120,38,-79,-91,42,113,106,-102,-53,-54,11,122,-47,37,97,-19,66,82,-19,88,-121,27,75,-38,42,-62,-82,16,83,78,-24,-109,-67,-43,-125,118,24,-69,125,-53,61,88,-66,66,-118,29,90,-56,-5,-56,-47,118,-80,104,20,-33,-30,24,91,49,76)
    }
}