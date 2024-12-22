package cc.sovellus.picothememanager.manager

import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.pm.PackageInfo
import android.provider.Settings

class EnvironmentManager(
    context: Context
) : ContextWrapper(context) {
    private val officialEnvironments: List<String> = listOf(
        "com.pvr.ZeroIsland.scene",
        "com.pvr.EnchantedLibrary.scene",
        "com.pvr.MountainVilla.scene",
        "com.pvr.MoonshadowDunes.scene",
        "com.pvr.ZeroIslandNight.scene",
        "com.pvr.WoodenHouse.scene",
        "com.pvr.SeaviewVilla.scene",
        "com.pvr.CloudDragonDance.scene",
        "com.pvr.SuperMechaChampions.scene"
    )

    fun forceVrShellRestart() {
        val intent = Intent()
        intent.component = ComponentName("com.pvr.vrshell", "com.pvr.vrshell.MainActivity")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    fun getPackageList(): List<PackageInfo> {
        return packageManager.getInstalledPackages(0).filter {
            officialEnvironments.contains(it.packageName).not() && it.packageName.contains(Regex("com.pvr.[^.]+.scene"))
        }
    }

    fun getPackageListOfficial(): List<PackageInfo> {
        return packageManager.getInstalledPackages(0).filter {
            officialEnvironments.contains(it.packageName)
        }
    }

    fun resetEnvironment() {
        Settings.Global.putString(contentResolver, "SceneManager.CurPackage", null)
        Settings.Global.putString(contentResolver, "SceneManager.CurrentScene", "")
        Settings.Global.putString(contentResolver, "current_scene", "default_scene")
    }

    fun setEnvironment(scenePackage: String, tag: String, scene: String) {
        Settings.Global.putString(contentResolver, "SceneManager.CurPackage", scenePackage)
        Settings.Global.putString(contentResolver, "SceneManager.CurrentScene", tag)
        Settings.Global.putString(contentResolver, "current_scene", scene)
    }
}