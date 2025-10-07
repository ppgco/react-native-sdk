import Foundation
import PPG_framework

public class PushPushGoRN {
  public static func initialize(
    application: UIApplication,
    projectId: String,
    apiKey: String,
    appGroupId: String
  ) {
    PPG.initializeNotifications(
      projectId: projectId,
      apiToken: apiKey,
      appGroupId: appGroupId
    )
    
    UNUserNotificationCenter.current().delegate = PPG.shared
    
    PPG.registerForNotifications(application: application) { result in }
  }
  
  public static func applicationDidBecomeActive() {
    PPG.sendEventsDataToApi()
  }
  
  public static func applicationDidRegisterForRemoteNotificationsWithDeviceToken(deviceToken: Data) {
    PPG.sendDeviceToken(deviceToken) { _ in }
  }
  
  public static func applicationDidReceiveRemoteNotification(
    userInfo: [AnyHashable : Any],
    fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
  ) {
    PPG.registerNotificationDeliveredFromUserInfo(userInfo: userInfo) { _ in
        completionHandler(.newData)
    }
  }
}
