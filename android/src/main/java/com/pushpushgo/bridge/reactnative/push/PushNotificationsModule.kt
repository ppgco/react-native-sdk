package com.pushpushgo.bridge.reactnative.push

import androidx.core.content.ContextCompat
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.pushpushgo.sdk.PushPushGo

class PushNotificationsModule(
  reactContext: ReactApplicationContext,
) : NativePushNotificationsSpec(reactContext) {
  private val ppg by lazy { PushPushGo.getInstance() }

  companion object {
    const val NAME: String = "PushPushGoPushNotifications"
  }

  override fun getSubscriberId(promise: Promise) {
    promise.resolve(ppg.getSubscriberId().ifEmpty { null })
  }

  override fun subscribeToNotifications(promise: Promise) {
    Futures.addCallback(
      ppg.createSubscriber(),
      object : FutureCallback<String> {
        override fun onSuccess(result: String) {
          promise.resolve(result.ifEmpty { null })
        }

        override fun onFailure(t: Throwable) {
          promise.reject(PushNotificationsError("Cannot subscribe to notifications: " + t.message))
        }
      },
      ContextCompat.getMainExecutor(reactApplicationContext),
    )
  }

  override fun unsubscribeFromNotifications(promise: Promise) {
    ppg.unregisterSubscriber()

    promise.resolve(null)
  }

  override fun sendBeacon(
    beacon: ReadableMap,
    promise: Promise,
  ) {
    PushNotificationsBeaconTranslator.translate(beacon).send()

    promise.resolve(null)
  }
}
