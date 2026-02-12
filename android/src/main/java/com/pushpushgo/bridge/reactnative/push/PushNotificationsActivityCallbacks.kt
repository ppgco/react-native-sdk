package com.pushpushgo.bridge.reactnative.push

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.pushpushgo.sdk.PushPushGo

internal class PushNotificationsActivityCallbacks : ActivityLifecycleCallbacks {
  override fun onActivityCreated(
    activity: Activity,
    savedInstanceState: Bundle?,
  ) {
    if (PushPushGo.isInitialized() && savedInstanceState == null) {
      PushPushGo.getInstance().handleBackgroundNotificationClick(activity.intent)
    }
  }

  override fun onActivityResumed(activity: Activity) {
    if (PushPushGo.isInitialized()) {
      PushPushGo.getInstance().handleBackgroundNotificationClick(activity.intent)
    }
  }

  override fun onActivityDestroyed(activity: Activity) = Unit

  override fun onActivityPaused(activity: Activity) = Unit

  override fun onActivitySaveInstanceState(
    activity: Activity,
    outState: Bundle,
  ) = Unit

  override fun onActivityStarted(activity: Activity) = Unit

  override fun onActivityStopped(activity: Activity) = Unit
}
