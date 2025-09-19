package com.pushpushgo.reactnativesdk

import androidx.core.content.ContextCompat
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.pushpushgo.reactnativesdk.bridge.PushPushGoBeaconTranslator
import com.pushpushgo.reactnativesdk.bridge.PushPushGoError
import com.pushpushgo.sdk.PushPushGo

class PushPushGoModule(reactContext: ReactApplicationContext): NativePushPushGoSpec(reactContext) {
  private val ppg by lazy { PushPushGo.getInstance() }

  companion object {
    const val NAME: String = "PushPushGo"
  }

  override fun getSubscriberId(promise: Promise) {
    promise.resolve(ppg.getSubscriberId().ifEmpty { null })
  }

  override fun subscribeToNotifications(promise: Promise) {
    Futures.addCallback(ppg.createSubscriber(), object: FutureCallback<String> {
      override fun onSuccess(result: String) {
        promise.resolve(result.ifEmpty { null })
      }

      override fun onFailure(t: Throwable) {
        promise.reject(PushPushGoError("Cannot subscribe to notifications"))
      }
    }, ContextCompat.getMainExecutor(reactApplicationContext))
  }

  override fun unsubscribeFromNotifications(promise: Promise) {
    ppg.unregisterSubscriber()

    promise.resolve(Unit)
  }

  override fun sendBeacon(beacon: ReadableMap, promise: Promise) {
    PushPushGoBeaconTranslator.translate(beacon).send()

    promise.resolve(Unit)
  }
}
