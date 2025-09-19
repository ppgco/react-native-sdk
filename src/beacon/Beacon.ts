import type { BeaconTag } from './BeaconTag';

export type BeaconSelectorKey = string;
export type BeaconSelectorValue = string | number | boolean;

export interface IBeacon {
  selectors: Map<BeaconSelectorKey, BeaconSelectorValue>;
  tags: BeaconTag[];
  tagsToDelete: BeaconTag[];
  customId: string | null;
}

class BeaconBuilder {
  private selectors = new Map<BeaconSelectorKey, BeaconSelectorValue>();
  private tags: BeaconTag[] = [];
  private tagsToDelete: BeaconTag[] = [];
  private customId: string | null = null;

  set(key: BeaconSelectorKey, value: BeaconSelectorValue): BeaconBuilder {
    this.selectors.set(key, value);

    return this;
  }

  appendTag(tag: BeaconTag): BeaconBuilder {
    this.tags.push(tag);

    return this;
  }

  removeTag(tag: BeaconTag): BeaconBuilder {
    this.tagsToDelete.push(tag);

    return this;
  }

  setCustomId(id: string | null): BeaconBuilder {
    this.customId = id;

    return this;
  }

  build(): Beacon {
    return new Beacon(
      this.selectors,
      this.tags,
      this.tagsToDelete,
      this.customId
    );
  }
}

export class Beacon implements IBeacon {
  constructor(
    readonly selectors: Map<BeaconSelectorKey, BeaconSelectorValue>,
    readonly tags: BeaconTag[],
    readonly tagsToDelete: BeaconTag[],
    readonly customId: string | null
  ) {}

  static builder(): BeaconBuilder {
    return new BeaconBuilder();
  }
}
