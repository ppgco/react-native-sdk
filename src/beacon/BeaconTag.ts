export enum BeaconTagStrategy {
  APPEND = 'append',
  REWRITE = 'rewrite',
}

export interface IBeaconTag {
  tag: string;
  label: string;
  strategy: BeaconTagStrategy;
  ttl: number;
}

interface IBeaconTagProps {
  tag: string;
  label?: string;
  strategy?: BeaconTagStrategy;
  ttl?: number;
}

export class BeaconTag implements IBeaconTag {
  readonly tag: string;
  readonly label: string;
  readonly strategy: BeaconTagStrategy;
  readonly ttl: number;

  constructor({
    tag,
    label = 'default',
    strategy = BeaconTagStrategy.APPEND,
    ttl = 0,
  }: IBeaconTagProps) {
    this.tag = tag;
    this.label = label;
    this.strategy = strategy;
    this.ttl = ttl;
  }

  static fromTag(tag: string): BeaconTag {
    return new BeaconTag({ tag });
  }

  static fromTagAndLabel(tag: string, label: string) {
    return new BeaconTag({ tag, label });
  }
}
