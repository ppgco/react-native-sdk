import { PermissionsAndroid, Platform } from 'react-native';
import type { Beacon } from './beacon/Beacon';
import NativePushPushGo from './specs/NativePushPushGo';

export type SubscriberId = string;

export interface IPushPushGo {
  getSubscriberId: () => Promise<SubscriberId | null>;
  subscribeToNotifications: () => Promise<SubscriberId>;
  unsubscribeFromNotifications: () => Promise<void>;
  sendBeacon: (beacon: Beacon) => Promise<void>;
}

export const PushPushGo: IPushPushGo = {
  getSubscriberId: async () => {
    return await NativePushPushGo.getSubscriberId();
  },

  subscribeToNotifications: async () => {
    if (Platform.OS === 'android' && Platform.Version >= 33) {
      const hasNotificationsPermission = await PermissionsAndroid.check(
        PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
      );

      if (!hasNotificationsPermission) {
        const hasGrantedNotificationsPermission =
          await PermissionsAndroid.request(
            PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
          );

        if (!hasGrantedNotificationsPermission) {
          throw new Error('Cannot subscribe to notifications: no permission');
        }
      }
    }

    return await NativePushPushGo.subscribeToNotifications();
  },

  unsubscribeFromNotifications: async () => {
    await NativePushPushGo.unsubscribeFromNotifications();
  },

  sendBeacon: async (beacon) => {
    await NativePushPushGo.sendBeacon({
      selectors: Object.fromEntries(beacon.selectors.entries()),
      tags: beacon.tags,
      tagsToDelete: beacon.tagsToDelete,
      customId: beacon.customId,
    });
  },
};

export {
  Beacon,
  type BeaconSelectorKey,
  type BeaconSelectorValue,
} from './beacon/Beacon';

export { BeaconTag, BeaconTagStrategy } from './beacon/BeaconTag';
