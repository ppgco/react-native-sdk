package com.pushpushgo.reactnativesdk.bridge

open class PushPushGoError(message: String): Exception(message) {
}

class PushPushGoNotInitializedError: PushPushGoError("PushPushGo is not initialized") {}
