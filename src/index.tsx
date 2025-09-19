import NativePushPushGo from './specs/NativePushPushGo';

export type SubscriberId = string;

export interface IPushPushGo {
  getSubscriberId: () => Promise<SubscriberId | null>;
  subscribeToNotifications: () => Promise<SubscriberId>;
  unsubscribeFromNotifications: () => Promise<void>;
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
};
