#import "PushPushGoModule.h"
#import "PushPushGoRN-Swift.h"

static NSString* const MODULE_NAME = @"PushPushGo";

@implementation PushPushGoModule {
  PushPushGoModuleDelegate *delegate;
}

- (instancetype)init {
  if (self = [super init]) {
    delegate = [PushPushGoModuleDelegate new];
  }
  
  return self;
}

+ (NSString *)moduleName {
  return MODULE_NAME;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<facebook::react::NativePushPushGoSpecJSI>(params);
}

- (void)getSubscriberId:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  NSString* subscriberId = [delegate getSubscriberId];
  
  resolve(subscriberId);
}

- (void)sendBeacon:(JS::NativePushPushGo::SpecBeacon &)beacon resolve:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
}

- (void)subscribeToNotifications:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  UIApplication *application = [UIApplication sharedApplication];
  
  [delegate subscribeToNotificationsWithApplication:application resolve:resolve reject:reject];
}

- (void)unsubscribeFromNotifications:(nonnull RCTPromiseResolveBlock)resolve reject:(nonnull RCTPromiseRejectBlock)reject {
  [delegate unsubscribeFromNotificationsWithResolve:resolve reject:reject];
}

@end
