import UserNotifications
import PPG_framework

class NotificationService: UNNotificationServiceExtension {

    var contentHandler: ((UNNotificationContent) -> Void)?
    var bestAttemptContent: UNMutableNotificationContent?

  override func didReceive(_ request: UNNotificationRequest, withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void) {
      self.contentHandler = contentHandler
      self.bestAttemptContent = (request.content.mutableCopy() as? UNMutableNotificationContent)

      guard let content = bestAttemptContent else { return }
    
      SharedData.shared.appGroupId = "group.ppg.reactnativesdkexample"

      // Wait for delivery event result & image fetch before returning from extension
      let group = DispatchGroup()
      
      group.enter()
      PPG.notificationDelivered(notificationRequest: request) { _ in
          group.leave()
      }

      group.enter()
      DispatchQueue.global().async { [weak self] in
          self?.bestAttemptContent = PPG.modifyNotification(content)
          group.leave()
      }

      group.notify(queue: .main) {
          contentHandler(self.bestAttemptContent ?? content)
      }
  }
    
    override func serviceExtensionTimeWillExpire() {
        // Called just before the extension will be terminated by the system.
        // Use this as an opportunity to deliver your "best attempt" at modified content, otherwise the original push payload will be used.
        if let contentHandler = contentHandler, let bestAttemptContent =  bestAttemptContent {
            contentHandler(bestAttemptContent)
        }
    }

}
