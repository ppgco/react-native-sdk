package com.pushpushgo.bridge.reactnative.push

import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.pushpushgo.sdk.BeaconBuilder
import com.pushpushgo.sdk.PushPushGo

private data class TranslatedBeaconTag(
  val tag: String,
  val label: String,
  val strategy: String,
  val ttl: Int,
)

internal class PushNotificationsBeaconTranslator {
  companion object {
    private fun translateSelectors(
      map: ReadableMap,
      beacon: BeaconBuilder,
    ) {
      val selectors = map.getMap("selectors")
      val selectorsIterator = selectors?.keySetIterator()

      while (selectorsIterator?.hasNextKey() == true) {
        val key = selectorsIterator.nextKey()
        val value: Any? =
          when (selectors.getType(key)) {
            ReadableType.String -> selectors.getString(key)
            ReadableType.Number -> selectors.getDouble(key)
            ReadableType.Boolean -> selectors.getBoolean(key)
            else -> throw PushNotificationsError("Unexpected selector value type")
          }

        value?.let { beacon.set(key, it) }
      }
    }

    private fun translateTag(tag: ReadableMap): TranslatedBeaconTag? {
      val name = tag.getString("tag") ?: return null
      val label = tag.getString("label") ?: "default"
      val strategy = tag.getString("strategy") ?: "append"
      val ttl = tag.getInt("ttl")

      if (strategy != "append" && strategy != "rewrite") {
        throw PushNotificationsError("Unexpected beacon tag strategy")
      }

      return TranslatedBeaconTag(
        tag = name,
        label = label,
        strategy = strategy,
        ttl = ttl,
      )
    }

    private fun translateTags(
      map: ReadableMap,
      beacon: BeaconBuilder,
    ) {
      val tags = map.getArray("tags") ?: return

      for (i in 0 until tags.size()) {
        val rawTag = tags.getMap(i) ?: continue
        val tag = translateTag(rawTag) ?: continue

        beacon.appendTag(
          tag = tag.tag,
          label = tag.label,
          strategy = tag.strategy,
          ttl = tag.ttl,
        )
      }
    }

    private fun translateTagsToDelete(
      map: ReadableMap,
      beacon: BeaconBuilder,
    ) {
      val rawTagsToDelete = map.getArray("tagsToDelete") ?: return
      val tagsToDelete = mutableMapOf<String, String>()

      for (i in 0 until rawTagsToDelete.size()) {
        val rawTag = rawTagsToDelete.getMap(i) ?: continue
        val tag = translateTag(rawTag) ?: continue

        tagsToDelete[tag.tag] = tag.label
      }

      beacon.removeTags(tagsToDelete)
    }

    fun translate(map: ReadableMap): BeaconBuilder {
      val beacon = PushPushGo.getInstance().createBeacon()

      translateSelectors(map, beacon)
      translateTags(map, beacon)
      translateTagsToDelete(map, beacon)

      beacon.setCustomId(map.getString("customId"))

      map.getString("assignToGroup")?.let { beacon.assignToGroup(it) }
      map.getString("unassignFromGroup")?.let { beacon.unassignFromGroup(it) }

      return beacon
    }
  }
}
