import Foundation
import PPG_framework

class PushPushGoBeaconTranslator {
  private static func translateSelectors(selectors: NSDictionary, beacon: Beacon) -> Void {
    for (key, value) in selectors {
      guard let keyString = key as? String else { continue }
        
      if let stringValue = value as? String {
        beacon.addSelector(keyString, stringValue)
        continue
      }
      
      if let numberValue = value as? NSNumber {
        let type = CFNumberGetType(numberValue)
        
        if (type == .charType) {
          beacon.addSelector(keyString, numberValue.boolValue)
        } else {
          beacon.addSelector(keyString, numberValue.floatValue)
        }
      }
    }
  }
  
  private static func translateTag(rawTag: [String: Any]) -> BeaconTag? {
    guard let tag = rawTag["tag"] as? String,
          let label = rawTag["label"] as? String,
          let _strategy = rawTag["strategy"] as? String,
          let ttl = rawTag["ttl"] as? Int64 else { return nil }
    
    let strategy: BeaconTagStrategy = _strategy == "rewrite" ? .rewrite : .append;
    
    return BeaconTag(
      tag: tag,
      label: label,
      strategy: strategy,
      ttl: ttl
    )
  }
  
  private static func translateTags(rawTags: NSArray, beacon: Beacon) -> Void {
    for _rawTag in rawTags {
      guard let rawTag = _rawTag as? [String: Any] else { continue }
      guard let tag = translateTag(rawTag: rawTag) else { continue }
      
      beacon.addTag(tag)
    }
  }
  
  private static func translateTagsToDelete(rawTags: NSArray, beacon: Beacon) -> Void {
    for _rawTag in rawTags {
      guard let rawTag = _rawTag as? [String: Any] else { continue }
      guard let tag = translateTag(rawTag: rawTag) else { continue }
      
      beacon.addTagToDelete(tag)
    }
  }
  
  static func translate(
    selectors: NSDictionary,
    tags: NSArray,
    tagsToDelete: NSArray,
    customId: NSString?,
  ) -> Beacon {
    var beacon = Beacon()
    
    translateSelectors(selectors: selectors, beacon: beacon)
    translateTags(rawTags: tags, beacon: beacon)
    translateTagsToDelete(rawTags: tags, beacon: beacon)
  
    beacon.customId = customId as? String ?? "";
    
    return beacon
  }
}
