import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';
import type { BeaconTag } from '../beacon/BeaconTag';

export type SubscriberId = string;

interface SpecBeacon {
  selectors: { [key: string]: string | number | boolean };
  tags: BeaconTag[];
  tagsToDelete: BeaconTag[];
  customId: string;
}

export interface Spec extends TurboModule {
  getSubscriberId: () => Promise<SubscriberId | null>;
  subscribeToNotifications: () => Promise<SubscriberId>;
  unsubscribeFromNotifications: () => Promise<void>;
  sendBeacon: (beacon: SpecBeacon) => Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('PushPushGo');
