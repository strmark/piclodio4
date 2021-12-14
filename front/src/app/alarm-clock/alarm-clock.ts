export class AlarmClock {
    id: number;
    name = '';
    monday  = false;
    tuesday  = false;
    wednesday  = false;
    thursday  = false;
    friday  = false;
    saturday  = false;
    sunday  = false;
    hour: number;
    minute: number;
    autoStopMinutes: number;
    active = false;
    webradio: number;

    constructor(values: any = {}) {
        Object.assign(this, values);
    }
}
