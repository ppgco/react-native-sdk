#import "PushPushGoModule.h"

@implementation PushPushGoModule

+ (NSString *)moduleName {
  return @"PushPushGo";
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<facebook::react::NativePushPushGoSpecJSI>(params);
}

- (void)getSubscriberId:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  resolve(@"Hello world");
}

- (void)sendBeacon:(JS::NativePushPushGo::SpecBeacon &)beacon resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  resolve(nil);
}

- (void)subscribeToNotifications:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  resolve(nil);
}

- (void)unsubscribeFromNotifications:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  resolve(nil);
}

@end
