package com.pushpushgo.reactnativesdk

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class PushPushGoPackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    if (name == PushPushGoModule.NAME) {
      return PushPushGoModule(reactContext)
    }

    return null
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      mapOf(PushPushGoModule.NAME to ReactModuleInfo(
        name = PushPushGoModule.NAME,
        className = PushPushGoModule.NAME,
        canOverrideExistingModule = false,
        needsEagerInit = false,
        hasConstants = false,
        isCxxModule = false,
        isTurboModule = true
      ))
    }
  }
}
