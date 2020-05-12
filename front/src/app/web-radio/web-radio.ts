export class WebRadio {
    id: number;
    name: string = '';
    url: string = '';
    default: boolean = false;

    constructor(values: Object = {}) {
        Object.assign(this, values);
    }
}
