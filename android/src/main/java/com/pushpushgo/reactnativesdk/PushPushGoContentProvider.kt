package com.pushpushgo.reactnativesdk

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import com.pushpushgo.reactnativesdk.bridge.PushPushGoError
import com.pushpushgo.sdk.PushPushGo

class PushPushGoContentProvider: ContentProvider() {
  override fun onCreate(): Boolean {
    context?.applicationContext?.let { ctx ->
      val app = ctx as Application
      val metadata = ctx
          .packageManager
          .getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA)
          .metaData

      if (!PushPushGo.isInitialized()) {
        PushPushGo.getInstance(
          application = app,
          projectId = metadata.getString("com.pushpushgo.projectId") ?: throw PushPushGoError("com.pushpushgo.projectId is required"),
          apiKey = metadata.getString("com.pushpushgo.apiKey") ?: throw PushPushGoError("com.pushpushgo.apiKey is required"),
          isDebug = metadata.getBoolean("com.pushpushgo.isDebug"),
          isProduction = metadata.getBoolean("com.pushpushgo.isProduction")
        )

        PushPushGo
          .getInstance()
          .setCustomClickIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
      }

      app.registerActivityLifecycleCallbacks(PushPushGoActivityCallbacks())
    }

    return true
  }

  override fun getType(uri: Uri): String? = null

  override fun query(
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?
  ): Cursor? = null

  override fun insert(uri: Uri, values: ContentValues?): Uri? = null

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = -1

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<out String>?
  ): Int = -1
}
