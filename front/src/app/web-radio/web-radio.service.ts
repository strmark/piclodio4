import { GlobalVariable } from '../globals';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { WebRadio } from './web-radio';

@Injectable()
export class WebRadioService {

  baseUrl: string = GlobalVariable.BASE_API_URL;

  constructor(private httpService: HttpClient) {}

  getAllWebRadios(): Observable < WebRadio[] > {
    return this.httpService.get<WebRadio[]>(this.baseUrl + '/webradio/');
  }

  getWebRadioById(id: number): Observable < WebRadio > {
    return  this.httpService.get< WebRadio >(this.baseUrl + '/webradio/' + id);
  }

  addWebRadio(webradio: WebRadio): Observable < WebRadio > {
    console.log('addWebRadio');
    const body = JSON.stringify(webradio); // Stringify payload
    return this.httpService.post< WebRadio>(this.baseUrl + '/webradio/', body, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    });
  }

  deleteWebRadioById(id: number): Observable < any > {
    console.log('call delete service, delete webradio id ' + id);
    return this.httpService.delete(this.baseUrl + '/webradio/' + id);
  }

  updateWebRadioById(id: number, values: Object = {}): Observable < WebRadio > {
    const body = JSON.stringify(values); // Stringify payload
    return this.httpService.put< WebRadio >(this.baseUrl + '/webradio/' + id, body, {
      headers: new HttpHeaders().set('Content-Type', 'application/json'),
    });
  }
}
