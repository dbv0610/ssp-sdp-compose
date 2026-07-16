package com.sdp.ssp.kmp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.net.Uri

internal object SdpRuntime {
    @Volatile
    internal var appResources: Resources? = null

    /**
     * The configuration used to resolve sdp/ssp values. Prefers the host app's
     * resources (identical to what `values-swXXXdp` resource resolution would
     * see — font scale, display size and window configuration included) and
     * falls back to system resources if the init provider has not run
     * (e.g. unit tests, IDE preview).
     */
    internal val configuration: Configuration
        get() = (appResources ?: Resources.getSystem()).configuration
}

/**
 * Auto-initializes [SdpRuntime] with the host app's resources so [Sdp], [Ssp]
 * and [RSsp] resolve against the same configuration the Android resource
 * system uses. Registered in the library manifest — no setup code required.
 */
class SdpInitProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        val ctx = context ?: return false
        SdpRuntime.appResources = (ctx.applicationContext ?: ctx).resources
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): Int = 0
}
