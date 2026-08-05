# React Native SDK

PushPushGo Push Notifications SDK for React Native. Supports Android (FCM / HMS) and iOS (APNS).

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

## Android Integration

### Preparation

1. Remove other push SDKs or custom FCM/HMS implementations.
2. Connect your app to a push provider.
3. Prepare provider configuration files:
   - FCM: `google-services.json`
   - HMS: `agconnect-services.json`
4. Integrate the provider in the PushPushGo app:
   - Project → Settings → Integration
   - See FCM or HMS sections below for details.
5. Collect your PushPushGo Project ID and API Key.

> [!IMPORTANT]
> ### Use a dedicated Mobile SDK token
>
> We have introduced a **dedicated mobile authorization token** for mobile SDK
> integrations. We recommend generating one in *Project settings → Integration → Mobile*
> (`https://app.pushpushgo.com/projects/{yourProjectID}/settings/integration/mobile`)
> and using it in the SDK instead of a general-purpose API key from the Access Manager.
> It is a drop-in replacement — no other changes to your integration are required.

### Why a separate token?

The recommendation is a security one — a credential shipped inside a mobile app should
be the one you can afford to lose:

- **Limited blast radius** — the mobile token is scoped to what the SDK actually needs.
  A general API key usually also allows sending campaigns, exporting subscribers or
  changing project settings; if it leaks from an app, the attacker gets all of that.
- **Independent revocation** — you can revoke or rotate the mobile token without
  breaking your server-side integrations that share the old key.

> [!NOTE]
> Tokens you are currently using will keep working — this is not a breaking change.
> We still recommend switching to a dedicated mobile token.

### Installation

```sh
npm install @pushpushgo/react-native-push
# or
yarn add @pushpushgo/react-native-push
```

### FCM (Firebase Cloud Messaging)

#### Provider credentials

1. Open **Firebase Console**.
2. Navigate to **Project settings** → **Cloud Messaging**.
3. Click **Manage service accounts**.
4. Select your service account email.
5. Open the **Keys** tab.
6. Click **Add key** → **Create new key**.
7. Choose **JSON** format and download the file.
8. Upload the JSON file in the PushPushGo **FCM** integration section.

#### FCM configuration

Place `google-services.json` in the app module root:

```
android/app/google-services.json
```

#### Gradle setup

```gradle
// android/build.gradle
buildscript {
  dependencies {
    classpath("com.google.gms:google-services:4.4.4")
  }
}
```

```gradle
// android/app/build.gradle

apply plugin: 'com.google.gms.google-services'

dependencies {
  implementation platform('com.google.firebase:firebase-bom:34.9.0')
  implementation 'com.google.firebase:firebase-messaging'
}
```

### HMS (Huawei Push Kit)

#### Provider credentials

1. Open **Huawei Developers Console**.
2. Navigate to your project.
3. Open **Project settings**.
4. Collect the required values:
   - `appId`
   - `authUrl`
   - `pushUrl`
   - `appSecret`
5. Provide these credentials in the PushPushGo **HMS** integration section.

#### HMS configuration

Place `agconnect-services.json` in the app module root:

```
android/app/agconnect-services.json
```

#### Gradle setup

```gradle
// android/build.gradle
buildscript {
  repositories {
    maven {
      url('https://developer.huawei.com/repo/')
    }
  }

  dependencies {
    classpath('com.huawei.agconnect:agcp:1.9.1.304')
  }
}

allprojects {
  repositories {
    maven {
      url('https://developer.huawei.com/repo/')
    }
  }
}
```

```gradle
// android/app/build.gradle

apply plugin: 'com.huawei.agconnect'

dependencies {
  implementation 'com.huawei.agconnect:agconnect-core:1.9.1.304'
  implementation 'com.huawei.hms:push:6.13.0.300'
}
```

### Android Configuration

#### AndroidManifest.xml

Add your Project ID and API Key inside `<application>`:

```xml
<meta-data
  android:name="com.pushpushgo.projectId"
  android:value="{projectId}" />

<meta-data
  android:name="com.pushpushgo.apiKey"
  android:value="{apiKey}" />
```

#### Handling notification clicks

To ensure correct handling of notification taps:

1. Set `android:launchMode` to `singleTop` on your activity.
2. Add the following `intent-filter`.

   ```xml
   <activity android:launchMode="singleTop">
       <intent-filter>
           <action android:name="APP_PUSH_CLICK" />
           <category android:name="android.intent.category.DEFAULT" />
       </intent-filter>
   </activity>
   ```

## iOS Integration

### 1. Modify Podfile

    ```rb
    # ios/Podfile

    # Add PPG_framework pod to existing main application target
    target '<application_name>' do

    pod 'PPG_framework', :git => 'https://github.com/ppgco/ios-sdk.git', :tag => '4.2.0'

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

### 2. Install pods

Under ios/ directory run `pod install`.

### 3. Open workspace

Open iOS project in Xcode (`ios/*.xcworkspace`).

### 4. Modify AppDelegate

    ```swift
    // ios/<name>/AppDelegate.swift

    import PushPushGoRNPush

    public override func application(
      _ application: UIApplication,
      didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
      // At the beginning of this method, initialize the PushNotificationsRN library

      PushNotificationsRN.initialize(
        projectId: "<YOUR-PROJECT-ID>",
        apiKey: "<YOUR-API-KEY>",
        appGroupId: "<YOUR-APP-GROUP-ID>"
      )

      // ...
    }

    // Add the following code

    public override func applicationDidBecomeActive(_ application: UIApplication) {
      PushNotificationsRN.applicationDidBecomeActive()
    }

    public override func application(
      _ application: UIApplication,
      didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        PushNotificationsRN.applicationDidRegisterForRemoteNotificationsWithDeviceToken(deviceToken: deviceToken)
    }

    public override func application(
      _ application: UIApplication,
      didReceiveRemoteNotification userInfo: [AnyHashable : Any],
      fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
      PushNotificationsRN.applicationDidReceiveRemoteNotification(userInfo: userInfo, fetchCompletionHandler: completionHandler)
    }
    ```

### 5. Add required capabilities

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

### 6. Create Notification Service Extension (NSE)

- Go to `File -> New -> Target`
- Select `Notification Service Extension`
- Choose a suitable name for it
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

    pod 'PPG_framework', :git => 'https://github.com/ppgco/ios-sdk.git', :tag => '4.2.0'
  end
  ```

- In ios/ directory run `pod install`
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

### 7. Ensure minimum deployment version

Ensure that application and NSE targets have the same minimum iOS version requirement - `General` > `Minimum Deployments`.

## Additional Integration Info

- https://pushpushgo.productfruits.help/en/article/web-mobile-push-integration

## Usage

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
  .assignToGroup('my-group-name') // Assign subscriber to dynamic group
  .unassignFromGroup('my-group-name') // Unassign subscriber from dynamic group
  .build();

PushNotifications.sendBeacon(beacon)
  .then(() => console.log('Beacon sent'))
  .catch((e) => `Cannot send beacon: ${e?.message}`);
```

## License

MIT
