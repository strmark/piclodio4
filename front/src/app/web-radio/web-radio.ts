export class WebRadio {
    id: number;
    name = '';
    url = '';
    default = false;

    constructor(values: any = {}) {
        Object.assign(this, values);
    }
}
