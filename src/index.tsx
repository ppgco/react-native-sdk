import { PermissionsAndroid, Platform } from 'react-native';
import type { Beacon } from './beacon/Beacon';
import NativePushNotifications from './specs/NativePushNotifications';

export type SubscriberId = string;

export interface IPushNotifications {
  getSubscriberId: () => Promise<SubscriberId | null>;
  subscribeToNotifications: () => Promise<SubscriberId>;
  unsubscribeFromNotifications: () => Promise<void>;
  sendBeacon: (beacon: Beacon) => Promise<void>;
}

export const PushNotifications: IPushNotifications = {
  getSubscriberId: async () => {
    return await NativePushNotifications.getSubscriberId();
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

    return await NativePushNotifications.subscribeToNotifications();
  },

  unsubscribeFromNotifications: async () => {
    await NativePushNotifications.unsubscribeFromNotifications();
  },

  sendBeacon: async (beacon) => {
    await NativePushNotifications.sendBeacon({
      selectors: Object.fromEntries(beacon.selectors.entries()),
      tags: beacon.tags,
      tagsToDelete: beacon.tagsToDelete,
      customId: beacon.customId,
      assignToGroup: beacon.assignToGroup,
      unassignFromGroup: beacon.unassignFromGroup,
    });
  },
};

export {
  Beacon,
  type BeaconSelectorKey,
  type BeaconSelectorValue,
} from './beacon/Beacon';

export { BeaconTag, BeaconTagStrategy } from './beacon/BeaconTag';
