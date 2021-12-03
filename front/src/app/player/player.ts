export class Player {
    status = 'off';
    webradio: number;

    constructor(values: Object = {}) {
        Object.assign(this, values);
    }
}
