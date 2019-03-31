import { WebRadio } from '../web-radio/web-radio';

export class AlarmClock {
    id: number;
    name: string = '';
    monday: boolean  = false;
    tuesday: boolean  = false;
    wednesday: boolean  = false;
    thursday: boolean  = false;
    friday: boolean  = false;
    saturday: boolean  = false;
    sunday: boolean  = false;
    hour: number;
    minute: number;
    auto_stop_minutes: number;
    is_active: boolean = false;
    webradio: WebRadio;

    constructor(values: Object = {}) {
        Object.assign(this, values);
    }
}
