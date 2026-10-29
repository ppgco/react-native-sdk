# PushPushGo Push Notifications SDK for React Native

React Native SDK for integrating push notifications into Android and iOS
applications. Supports FCM (Firebase Cloud Messaging) and HMS (Huawei Push
Kit) on Android, and APNs on iOS.

## Table of contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Expo projects](#expo-projects)
- [Android integration](#android-integration)
  - [Preparation](#android-preparation)
  - [FCM](#fcm-firebase-cloud-messaging)
  - [HMS](#hms-huawei-push-kit)
  - [Configuration](#android-configuration)
  - [Handling notification clicks](#handling-notification-clicks)
- [iOS integration](#ios-integration)
  - [Project setup](#ios-project-setup)
  - [App integration](#ios-app-integration)
  - [Notification Service Extension](#notification-service-extension)
- [Basic usage](#basic-usage)
  - [Push subscription](#push-subscription)
  - [Beacons and tags](#beacons-and-tags)
  - [Dynamic groups](#dynamic-groups)
- [Support](#support)

## Requirements

- Node.js 22+
- React Native 0.72+
- React 18+
- Android API 24+
- iOS 13.0+

## Installation

Install the package:

```sh
npm install @pushpushgo/react-native-push
```

or:

```sh
yarn add @pushpushgo/react-native-push
```

The native package is linked automatically by React Native.

## Expo projects

If you are using the Expo Managed workflow, generate the native Android and iOS projects before integrating this SDK:

```sh
npx expo prebuild
```

Then complete the Android and iOS integration steps below.

## Android integration

### Android preparation

1. Remove other push SDKs or custom FCM/HMS implementations.
2. Connect the app to FCM or HMS.
3. Prepare the provider configuration file:
   - FCM: `google-services.json`
   - HMS: `agconnect-services.json`
4. In PushPushGo, open **Project → Settings → Integration** and configure the
   selected provider.
5. Collect your PushPushGo Project ID and API Key.

Choose the appropriate provider setup below, then complete
[Android configuration](#android-configuration).

### FCM (Firebase Cloud Messaging)

#### Provider credentials

1. Open **Firebase Console**.
2. Navigate to **Project settings → Cloud Messaging**.
3. Click **Manage service accounts**.
4. Select your service account email.
5. Open **Keys → Add key → Create new key**.
6. Choose JSON format and download the file.
7. Upload the file in the PushPushGo **FCM v1** integration section.

#### FCM configuration

Place `google-services.json` in the app module:

```text
android/app/google-services.json
```

Add the Google Services plugin:

```groovy
// android/build.gradle
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.4")
    }
}
```

```groovy
// android/app/build.gradle
apply plugin: "com.google.gms.google-services"

dependencies {
    implementation platform("com.google.firebase:firebase-bom:34.9.0")
    implementation "com.google.firebase:firebase-messaging"
}
```

### HMS (Huawei Push Kit)

#### Provider credentials

1. Open **Huawei Developers Console**.
2. Select your project and open **Project settings**.
3. Collect `appId`, `authUrl`, `pushUrl`, and `appSecret`.
4. Enter the credentials in the PushPushGo **HMS** integration section.

#### HMS configuration

Place `agconnect-services.json` in the app module:

```text
android/app/agconnect-services.json
```

Add the Huawei repository and AG Connect plugin:

```groovy
// android/build.gradle
buildscript {
    repositories {
        maven { url "https://developer.huawei.com/repo/" }
    }

    dependencies {
        classpath("com.huawei.agconnect:agcp:1.9.1.304")
    }
}

allprojects {
    repositories {
        maven { url "https://developer.huawei.com/repo/" }
    }
}
```

```groovy
// android/app/build.gradle
apply plugin: "com.huawei.agconnect"

dependencies {
    implementation "com.huawei.agconnect:agconnect-core:1.9.1.304"
    implementation "com.huawei.hms:push:6.13.0.300"
}
```

### Android configuration

Add the Project ID and API Key inside the `<application>` element in
`android/app/src/main/AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.pushpushgo.projectId"
    android:value="YOUR_PROJECT_ID" />
<meta-data
    android:name="com.pushpushgo.apiKey"
    android:value="YOUR_API_KEY" />
```

The React Native package initializes the Android SDK automatically.

### Handling notification clicks

Set the main activity launch mode to `singleTop` and add the PushPushGo click
intent filter:

```xml
<activity
    android:name=".MainActivity"
    android:launchMode="singleTop">

    <intent-filter>
        <action android:name="APP_PUSH_CLICK" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

The package automatically forwards matching click intents to the native SDK.

## iOS integration

### iOS project setup

#### Create and upload an APNs certificate

Upload the APNs certificate to your PushPushGo project by following the
[APNs setup tutorial](https://docs.pushpushgo.company/application/providers/mobile-push/apns).

#### Add required capabilities

In Xcode, select the application target and open **Signing & Capabilities**:

1. Add **Push Notifications**.
2. Add **Background Modes** and enable **Remote notifications**.
3. Add **App Groups** and select an existing group or create one.
4. Ensure the provisioning profile includes the same capabilities.

The application and Notification Service Extension must use the same App Group.

#### Configure CocoaPods

The React Native package is autolinked. Add the PushPushGo iOS framework to the
application target in `ios/Podfile`:

```ruby
target 'YourApp' do
  # Existing React Native configuration...

  pod 'PPG_framework',
      :git => 'https://github.com/ppgco/ios-sdk.git',
      :tag => '4.2.0'
end
```

In the existing `post_install` block, allow the framework to use application
APIs:

```ruby
post_install do |installer|
  # Keep react_native_post_install(...) and other existing configuration.

  installer.pods_project.targets.each do |target|
    next unless target.name == 'PPG_framework'

    target.build_configurations.each do |config|
      config.build_settings['APPLICATION_EXTENSION_API_ONLY'] = 'No'
    end
  end
end
```

Install the pods:

```sh
cd ios
pod install
```

Open the generated `.xcworkspace` file in Xcode.

### iOS app integration

Import the bridge and initialize it at the beginning of your existing
`application(_:didFinishLaunchingWithOptions:)` implementation:

```swift
import PushPushGoRNPush

public override func application(
  _ application: UIApplication,
  didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
) -> Bool {
  PushNotificationsRN.initialize(
    projectId: "YOUR_PROJECT_ID",
    apiKey: "YOUR_API_KEY",
    appGroupId: "YOUR_APP_GROUP_ID"
  )

  return super.application(
    application,
    didFinishLaunchingWithOptions: launchOptions
  )
}
```

Forward the following application lifecycle callbacks to the bridge.

```swift
public override func applicationDidBecomeActive(_ application: UIApplication) {
    PushNotificationsRN.applicationDidBecomeActive()
}

public override func application(
    _ application: UIApplication,
    didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
) {
    PushNotificationsRN
        .applicationDidRegisterForRemoteNotificationsWithDeviceToken(
            deviceToken: deviceToken
        )
}

public override func application(
    _ application: UIApplication,
    didReceiveRemoteNotification userInfo: [AnyHashable: Any],
    fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
) {
    PushNotificationsRN.applicationDidReceiveRemoteNotification(
        userInfo: userInfo,
        fetchCompletionHandler: completionHandler
    )
}
```

### Notification Service Extension

1. In Xcode, go to **File → New → Target**.
2. Select **Notification Service Extension** and create a Swift target.
3. Open **Signing & Capabilities** for the extension.
4. Add **App Groups** and select the same group as the application target.
5. Ensure the application and extension use the same minimum iOS version.
6. Add the extension target to `ios/Podfile`:

```ruby
target 'PPGNotificationServiceExtension' do
  use_frameworks!
  use_modular_headers!

  pod 'PPG_framework',
      :git => 'https://github.com/ppgco/ios-sdk.git',
      :tag => '4.2.0'
end
```

Run `pod install` again, then add the following to the NSE and set your App
Group identifier:

```swift
import UserNotifications
import PPG_framework

class NotificationService: UNNotificationServiceExtension {
  var contentHandler: ((UNNotificationContent) -> Void)?
  var bestAttemptContent: UNMutableNotificationContent?

  override func didReceive(
    _ request: UNNotificationRequest,
    withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void
  ) {
    self.contentHandler = contentHandler
    self.bestAttemptContent = (request.content.mutableCopy() as? UNMutableNotificationContent)

    guard let content = bestAttemptContent else { return }

    // replace with your appGroupId
    SharedData.shared.appGroupId = "<YOUR-APP-GROUP-ID>"

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
}
```

## Basic usage

### Push subscription

```ts
import { PushNotifications } from '@pushpushgo/react-native-push';

PushNotifications.subscribeToNotifications()
  .then((subscriberId) => console.log(`subscriberId: ${subscriberId}`))
  .catch((e) =>
    console.error(`Cannot subscribe to notifications: ${e?.message}`)
  );

PushNotifications.unsubscribeFromNotifications()
  .then(() => console.log('Unsubscribed from notifications'))
  .catch((e) =>
    console.error(`Cannot unsubscribe from notifications: ${e?.message}`)
  );

PushNotifications.getSubscriberId()
  .then((subscriberId) =>
    console.log(`subscriberId: ${subscriberId ?? 'unsubscribed'}`)
  )
  .catch((e) => console.error(`Cannot get subscriberId: ${e?.message}`));
```

`subscribeToNotifications()` requests notification permission when required.

### Beacons and tags

```ts
import {
  Beacon,
  BeaconTag,
  BeaconTagStrategy,
  PushNotifications,
} from '@pushpushgo/react-native-push';

const beacon = Beacon.builder()
  .set('a-flag', true)
  .set('b-flag', 123)
  .set('c-flag', 'hello-world')
  .appendTag(BeaconTag.fromTagAndLabel('my-tag', 'my-label'))
  .appendTag(
    new BeaconTag({
      tag: 'my-tag-2',
      label: 'my-label-2',
      strategy: BeaconTagStrategy.REWRITE,
      ttl: 1000,
    })
  )
  .setCustomId('my-custom-id')
  .build();

PushNotifications.sendBeacon(beacon)
  .then(() => console.log('Beacon sent'))
  .catch((e) => console.error(`Cannot send beacon: ${e?.message}`));
```

The tag `ttl` is expressed in seconds. A value of `0` means that the tag
does not expire.

### Dynamic groups

Assign the subscriber to a dynamic group:

```ts
const beacon = Beacon.builder()
  .assignToGroup('my-group-name')
  .build();

PushNotifications.sendBeacon(beacon)
  .then(() => console.log('Beacon sent'))
  .catch((e) => console.error(`Cannot send beacon: ${e?.message}`));
```

Unassign the subscriber from a dynamic group:

```ts
const beacon = Beacon.builder()
  .unassignFromGroup('my-group-name')
  .build();

PushNotifications.sendBeacon(beacon)
  .then(() => console.log('Beacon sent'))
  .catch((e) => console.error(`Cannot send beacon: ${e?.message}`));
```

## Support

- [GitHub Issues](https://github.com/ppgco/react-native-sdk/issues)
- [PushPushGo documentation](https://docs.pushpushgo.com)

## License

[MIT](LICENSE)
