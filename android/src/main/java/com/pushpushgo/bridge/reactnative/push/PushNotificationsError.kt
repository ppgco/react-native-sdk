package com.pushpushgo.bridge.reactnative.push

class PushNotificationsError(
  message: String,
  cause: Throwable? = null,
) : Exception(message, cause)
