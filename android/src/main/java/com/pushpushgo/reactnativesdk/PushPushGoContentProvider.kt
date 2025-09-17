package com.pushpushgo.reactnativesdk

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.pushpushgo.sdk.PushPushGo

class PushPushGoContentProvider: ContentProvider() {
  override fun onCreate(): Boolean {
    context?.applicationContext?.let { ctx ->
      val app = ctx as Application

      if (!PushPushGo.isInitialized()) {
        PushPushGo.getInstance(app)
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
