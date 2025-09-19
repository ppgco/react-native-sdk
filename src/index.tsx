import { PermissionsAndroid, Platform } from 'react-native';
import type { Beacon } from './beacon/Beacon';
import NativePushPushGo from './specs/NativePushPushGo';

export type SubscriberId = string;

export interface IPushPushGo {
  getSubscriberId: () => Promise<SubscriberId | null>;
  subscribeToNotifications: () => Promise<SubscriberId>;
  unsubscribeFromNotifications: () => Promise<void>;
  sendBeacon: (beacon: Beacon) => Promise<void>;
  hasNotificationsPermission: () => Promise<boolean>;
  requestNotificationsPermission: () => Promise<void>;
}

export const PushPushGo: IPushPushGo = {
  getSubscriberId: async () => {
    return await NativePushPushGo.getSubscriberId();
  },

  subscribeToNotifications: async () => {
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

  hasNotificationsPermission: async () => {
    if (Platform.OS === 'android') {
      if (Platform.Version >= 33) {
        return await PermissionsAndroid.check(
          PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
        );
      }
    }

    return true;
  },

  requestNotificationsPermission: async () => {
    if (Platform.OS === 'android' && Platform.Version >= 33) {
      await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS
      );
    }
  },
};

export {
  Beacon,
  type BeaconSelectorKey,
  type BeaconSelectorValue,
} from './beacon/Beacon';

export { BeaconTag, BeaconTagStrategy } from './beacon/BeaconTag';
