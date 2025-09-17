package com.pushpushgo.reactnativesdk

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.pushpushgo.sdk.PushPushGo
import java.util.logging.Level
import java.util.logging.Logger

class PushPushGoActivityCallbacks:  ActivityLifecycleCallbacks {
  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    if (PushPushGo.isInitialized() && savedInstanceState == null) {
      PushPushGo.getInstance().handleBackgroundNotificationClick(activity.intent)
    }
  }

  override fun onActivityResumed(activity: Activity) {
    if (PushPushGo.isInitialized()) {
      PushPushGo.getInstance().handleBackgroundNotificationClick(activity.intent)
    }
  }

  override fun onActivityDestroyed(activity: Activity) {
  }

  override fun onActivityPaused(activity: Activity) {
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
  }

  override fun onActivityStarted(activity: Activity) {
  }

  override fun onActivityStopped(activity: Activity) {
  }
}
