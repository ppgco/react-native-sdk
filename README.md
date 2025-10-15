# React Native SDK

PushPushGo Push Notifications SDK for React Native. Supports Android (Firebase / Huawei) and iOS (APNS).

## Requirements

- Node 22+
- Android Studio
- Xcode

## Expo Projects

If you are using the Expo Managed workflow, you will need to eject your project to switch to the Expo Bare workflow.

Run the following command in your project root:

```bash
npx expo prebuild
```

## Installation

```sh
npm install @pushpushgo/react-native-sdk
# or
yarn install @pushpushgo/react-native-sdk
```

## Setup

1. Save your Project ID and generate API key

## Setup - Android

1. Add your Project ID and API key as `<meta-data>` tags
2. Set `android:launchMode` to `singleTop` on your activity
3. Add following `<intent-filter>` inside your activity

```xml
android/app/src/main/AndroidManifest.xml

<application>

<activity android:launchMode="singleTop">
    <intent-filter>
        <action android:name="APP_PUSH_CLICK" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>

<meta-data android:name="com.pushpushgo.projectId" android:value="<YOUR-PROJECT-ID>" />
<meta-data android:name="com.pushpushgo.apiKey" android:value="<YOUR-API-KEY>" />

</application>
```

## Setup - Android (Google)

1. Connect your application to Firebase project
   - Go to Firebase Console and click Add app
   - Select Android
   - Provide Android package name
     - Go to android/app/build.gradle
     - Look for android.defaultConfig.applicationId
   - Download given `google-services.json` file and put it under android/app directory

2. Authorize PushPushGo to access your Firebase project
   - Go to Firebase Console and navigate to Project Settings
   - Go to Cloud Messaging tab
   - Click Manage Service Accounts
   - Click on your service account email
   - Go to Keys tab
   - Click Add Key > Create New Key
   - Select JSON key type and click Create
   - Download given file and upload it in PushPushGo application
     - Project Settings > Integration > FCM
     - Additional info: https://docs.pushpushgo.company/mobile-push/google-android
3. Install dependencies

   ```gradle
   // android/build.gradle
   buildscript {
    dependencies {
        ...
        classpath("com.google.gms:google-services:4.4.4")
    }
   }

   // android/app/build.gradle
   apply plugin: 'com.google.gms.google-services'

   dependencies {
       ...
       implementation platform('com.google.firebase:firebase-bom:34.4.0')
       implementation 'com.google.firebase:firebase-messaging'
   }
   ```

## Setup - Android (Huawei)

1. Connect your application to Huawei project
2. Authorize PushPushGo to access your Huawei project
   - Go to Huawei Developers Console
   - Go to your project
   - Open Project Settings
   - Collect required credentials (appId, authUrl, pushUrl, appSecret)
   - Provide given credentials in PushPushGo application
     - Project Settings > Integration > HMS
     - Additional info: https://docs.pushpushgo.company/mobile-push/huawei-android
3. Install dependencies

   ```gradle
   // android/app/build.gradle
   dependencies {
    implementation 'com.huawei.agconnect:agconnect-core:1.9.1.304'
    implementation 'com.huawei.hms:push:6.13.0.300'
   }
   ```

## Setup - iOS

1.  Modify Podfile

    ```rb
    # ios/Podfile

    # Add PPG_framework pod to existing main application target
    target '<application_name>' do

    pod 'PPG_framework', :git => 'https://github.com/ppgco/ios-sdk.git', :tag => '3.0.6'

    end

    # Add the following code to the post_install section
    post_install do |installer|
     installer.pods_project.targets.each do |target|
       target.build_configurations.each do |config|
         config.build_settings['APPLICATION_EXTENSION_API_ONLY'] = 'No'
       end
     end
    end
    ```

2.  Under ios directory run `pod install`

3.  Open iOS project in Xcode (ios/\*.xcworkspace)

4.  Modify AppDelegate

    ```swift
    // ios/<name>/AppDelegate.swift

    import PushPushGoRN

    public override func application(
      _ application: UIApplication,
      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
      // At the beginning of this method, initialize the PushPushGoRN library

      PushPushGoRN.initialize(
        projectId: "<YOUR-PROJECT-ID>",
        apiKey: "<YOUR-API-KEY>",
        appGroupId: "<YOUR-APP-GROUP-ID>"
      )

      // ...
    }

    // Add the following code

    public override func applicationDidBecomeActive(_ application: UIApplication) {
       PushPushGoRN.applicationDidBecomeActive()
    }

    public override func application(
      _ application: UIApplication,
      didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
         PushPushGoRN.applicationDidRegisterForRemoteNotificationsWithDeviceToken(deviceToken: deviceToken)
    }

    public override func application(
      _ application: UIApplication,
      didReceiveRemoteNotification userInfo: [AnyHashable : Any],
      fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
       PushPushGoRN.applicationDidReceiveRemoteNotification(userInfo: userInfo, fetchCompletionHandler: completionHandler)
    }
    ```

5.  Add required capabilities
    - Click on top item in your project hierarchy
    - Select your project on target list
    - Select `Signing & Capabilities`
    - You can add capability by clicking on `+ Capability` button that is placed under `Signing & Capabilities` button
    - Add `Background Modes` capability unless it is already on your capability list. Then select `Remote notifications`
    - Add `Push notifications` capability unless it is already on your capability list
    - Add `App Groups`. You can use your default app group ID or add new one
    - Make sure that your `Provisioning Profile` has required capabilities. If you didn't add them while creating `Provisioning Profile` for your app you should go to your Apple Developer Center to add them. Then refresh your profile in Xcode project.

    #### How to add new group to your provisioning profile?
    - Go to Apple Developers and navigate to Certificates, Identifiers & Profiles. Then go to Identifiers and in the right corner change App IDs to AppGroups. You can add new group here.
    - Now you can go back to Identifiers, choose your app identifier and add AppGroup capability. Remember to check your new group.

6.  Create Notification Service Extension (NSE)
    - Go to `File -> New -> Target`
    - Select `Notification Service Extension`
    - Choose a suitable name for it (for example PushPushGoRNNSE)
    - Right click on the created NSE and select `Convert To Group`
    - Select your NSE on target list
    - Select `Signing & Capabilities`
    - Click on `+ Capability` button, under `Signing & Capabilities`, and add AppGroup capability with the exact same appGroupId as in the main application target and in the code
    - Modify `Podfile` and add NSE target

      ```rb
      # ios/Podfile

      # Replace PushPushGoRNNSE with the name of your NSE
      target 'PushPushGoRNNSE' do
        use_modular_headers!

        pod 'PPG_framework', :git => 'https://github.com/ppgco/ios-sdk.git', :tag => '3.0.6'
      end
      ```

    - In ios directory run `pod install`
    - Select your NSE on target list, in `General` > `Frameworks and Libraries` add `libPPG_framework.a`
    - Modify `NotificationService.swift` file in the created NSE and fill in the appGroupId value

      ```swift
      // ios/<NSE>/NotificationService.swift

      import PPG_framework

      // replace didReceive function with the following implementation
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
      ```

7.  Ensure that application and NSE targets have the same minimum iOS version requirement - `General` > `Minimum Deployments`

8.  Additional info: https://docs.pushpushgo.company/mobile-push/apple-ios

## Usage

```ts
import { PushPushGo } from '@pushpushgo/react-native-sdk';

PushPushGo.subscribeToNotifications()
  .then((subscriberId) => console.log(`subscriberId: ${subscriberId}`))
  .catch((e) =>
    console.error(`Cannot subscribe to notifications: ${e?.message}`)
  );

PushPushGo.unsubscribeFromNotifications()
  .then(() => console.log('Unsubscribed from notifications'))
  .catch((e) =>
    console.error(`Cannot unsubscribe from notifications: ${e?.message}`)
  );

PushPushGo.getSubscriberId()
  .then((subscriberId) =>
    console.log(`subscriberId: ${subscriberId ?? 'unsubscribed'}`)
  )
  .catch((e) => console.error(`Cannot get subscriberId: ${e?.message}`));

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

PushPushGo.sendBeacon(beacon)
  .then(() => console.log('Beacon sent'))
  .catch((e) => `Cannot send beacon: ${e?.message}`);
```

## License

MIT
