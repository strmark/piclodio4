import { GlobalVariable } from '../globals';
import { Observable } from 'rxjs';
import { HttpClientModule, HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { WebRadio } from './web-radio';

@Injectable()
export class WebRadioService {
 
  baseUrl: string = GlobalVariable.BASE_API_URL;

  constructor(private httpService: HttpClient) {}

  // GET /webradios
  getAllWebRadios(): Observable < WebRadio[] > {    
    return this.httpService.get<WebRadio[]>(this.baseUrl + "/webradio/");
  }

  // GET /webradios/:id
  getWebRadioById(id: number): Observable < WebRadio > {
    return  this.httpService.get< WebRadio >(this.baseUrl + "/webradio/" + id);    
  }

  // POST /webradios
  addWebRadio(webradio: WebRadio): Observable < WebRadio > {
    console.log("addWebRadio");
    let body = JSON.stringify(webradio); // Stringify payload
    return this.httpService.post< WebRadio>(this.baseUrl + "/webradio/", body, {
      headers: new HttpHeaders().set('Content-Type', 'application/json')
    });
  }

  // DELETE /webradios/:id
  deleteWebRadioById(id: number): Observable < any > {
    console.log("call delete service, delete webradio id " + id);
    return this.httpService.delete(this.baseUrl + "/webradio/" + id);
  }

  //  PUT /todos/:id
  updateWebRadioById(id: number, values: Object = {}): Observable < WebRadio > {
    let body = JSON.stringify(values); // Stringify payload
    return this.httpService.put< WebRadio >(this.baseUrl + "/webradio/" + id, body, {
      headers: new HttpHeaders().set('Content-Type', 'application/json'),
    });
  }
}
