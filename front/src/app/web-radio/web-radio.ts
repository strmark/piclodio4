export class WebRadio {
    id: number;
    name = '';
    url = '';
    default = false;

    constructor(values: Object = {}) {
        Object.assign(this, values);
    }
}
