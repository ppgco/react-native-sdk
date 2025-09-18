import NativePushPushGo from './specs/NativePushPushGo';

export async function getSubscriberId(): Promise<string | null> {
  return await NativePushPushGo.getSubscriberId();
}
