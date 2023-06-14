export class Backup {
  id: number;
  backupFile: string;

  constructor(values: any = {}) {
    Object.assign(this, values);
  }
}
