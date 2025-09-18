package com.pushpushgo.reactnativesdk

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap

class PushPushGoModule(reactContext: ReactApplicationContext): NativePushPushGoSpec(reactContext) {
  companion object {
    const val NAME: String = "PushPushGo"
  }

  override fun getSubscriberId(promise: Promise) {
    promise.resolve("Hello World")
  }

  override fun subscribeToNotifications(promise: Promise) {
    promise.resolve(Unit)
  }

  override fun unsubscribeFromNotifications(promise: Promise) {
    promise.resolve(Unit)
  }

  override fun sendBeacon(beacon: ReadableMap, promise: Promise) {
    promise.resolve(Unit)
  }
}
