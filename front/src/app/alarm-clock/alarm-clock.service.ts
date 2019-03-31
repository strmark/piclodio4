import { GlobalVariable } from './../globals';
import { HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AlarmClock } from "./alarm-clock";
import { Injectable } from '@angular/core';

@Injectable()
export class AlarmClockService {
  
  baseUrl: string = GlobalVariable.BASE_API_URL;

  constructor(private httpService: HttpClient) { }

  // GET /alarmclocks
  getAllAlarmClocks(): Observable <AlarmClock[]> {
    return this.httpService.get<AlarmClock[]>(this.baseUrl + "/alarms/");
  }

  // DELETE /alarms/:id
  deleteAlarmClockById(id: number): Observable < any > {
    console.log("call delete service, delete alarm id " + id);
    return this.httpService.delete(this.baseUrl + "/alarms/" + id);
  }

  // POST /alarms/new
  addAlarmClock(alarmClock: AlarmClock): Observable <AlarmClock> {
    let body = JSON.stringify(alarmClock); // Stringify payload
    return this.httpService.post<AlarmClock>(this.baseUrl + "/alarms/", body,  {
      headers: new HttpHeaders().set('Content-Type', 'application/json'),
    } );
  }

  // GET /alarms/:id
  getAlarmClockById(id: number): Observable <AlarmClock> {
    return this.httpService.get<AlarmClock>(this.baseUrl + "/alarms/" + id);
  }

  updateAlarmClockById(id: number, values: Object = {}): Observable <AlarmClock> {        
    let body = JSON.stringify(values); // Stringify payload
    let headers = new Headers({
      'Content-Type': 'application/json'
    });
    return this.httpService.put<AlarmClock>(this.baseUrl + "/alarms/" + id, body, {
      headers: new HttpHeaders().set('Content-Type', 'application/json'),
    } );
  }

}
