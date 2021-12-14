export class Player {
    status = 'off';
    webradio: number;

    constructor(values: any = {}) {
        Object.assign(this, values);
    }
}
