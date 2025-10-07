import Foundation
import PPG_framework
import React

@objc(PushPushGoModuleDelegate)
public class PushPushGoModuleDelegate: NSObject {
  @objc public func getSubscriberId() -> String? {
    return PPG.subscriberId.isEmpty ? nil : PPG.subscriberId;
  }
  
  @objc public func subscribeToNotifications(
    application: UIApplication,
    resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) -> Void {
    PPG.registerForNotifications(application: application) { result in
      switch result {
      case .success:
        resolve(nil)
      case .error(let error):
        reject(nil, "Cannot subscribe to notifications: " + error, nil)
      }
    }
  }
  
  @objc public func unsubscribeFromNotifications(
    resolve: @escaping RCTPromiseResolveBlock,
    reject: @escaping RCTPromiseRejectBlock
  ) -> Void {
    PPG.unsubscribeUser() { result in
      switch result {
      case .success:
        resolve(nil)
      case .error(let error):
        reject(nil, "Cannot unregister from notifications: " + error, nil)
      }
    }
  }
  
  @objc public func sendBeacon() {}
}
