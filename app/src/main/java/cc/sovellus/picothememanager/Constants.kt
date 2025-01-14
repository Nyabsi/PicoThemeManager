package cc.sovellus.picothememanager

object Constants {
    const val ANDROID_PERMISSION_SECURE_SETTINGS = "android.permission.WRITE_SECURE_SETTINGS"
    const val PICO_SCENE_MANAGER = "com.pvr.scenemanager"
    const val PICO_VRSHELL = "com.pvr.vrshell"
    const val PICO_PERMISSION_CONTROLLER = "com.android.permissioncontroller"
    const val APP_PERMISSIONS_ACTIVITY_SPARROW = "com.android.permissioncontroller.permission.ui.pico.AppPermissionsActivity"
    const val APP_PERMISSIONS_ACTIVITY_LEGACY = "com.android.packageinstaller.permission.ui.pico.AppPermissionsActivity"
    const val VRSHELL_MINIMUM_VERSION_FOR_RESOLUTION = 200704000
    val IGNORE_ENVIRONMENTS = listOf("default_scene", "AIGC", "Emulator", "Default")
}