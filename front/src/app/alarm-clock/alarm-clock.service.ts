import {GlobalVariable} from '../globals';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AlarmClock} from './alarm-clock';
import {Injectable} from '@angular/core';

@Injectable()
export class AlarmClockService {

  baseUrl: string = GlobalVariable.BASE_API_URL;

  constructor(private httpService: HttpClient) {
  }

  getAllAlarmClocks(): Observable<AlarmClock[]> {
    return this.httpService.get<AlarmClock[]>(this.baseUrl + '/alarms/');
  }

  deleteAlarmClockById(id: number): Observable<any> {
    console.log('call delete service, delete alarm id ' + id);
    return this.httpService.delete(this.baseUrl + '/alarms/' + id);
  }

  addAlarmClock(alarmClock: AlarmClock): Observable<AlarmClock> {
    const body = JSON.stringify(alarmClock);
    return this.httpService.post<AlarmClock>(this.baseUrl + '/alarms/', body, {
      headers: new HttpHeaders().set('Content-Type', 'application/json'),
    });
  }

  getAlarmClockById(id: number): Observable<AlarmClock> {
    return this.httpService.get<AlarmClock>(this.baseUrl + '/alarms/' + id);
  }

  updateAlarmClockById(id: number, values: Object = {}): Observable<AlarmClock> {
    const body = JSON.stringify(values);
    return this.httpService.put<AlarmClock>(this.baseUrl + '/alarms/' + id, body, {
      headers: new HttpHeaders().set('Content-Type', 'application/json'),
    });
  }
}
