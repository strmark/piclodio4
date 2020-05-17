export class Backup {
    id: number;
    backupFile: string;

    constructor(values: Object = {}) {
        Object.assign(this, values);
    }

}
