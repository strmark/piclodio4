import {GlobalVariable} from '../globals';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';


@Injectable()
export class SystemDateService {

  baseUrl: string = GlobalVariable.BASE_API_URL;

  constructor(private httpService: HttpClient) {
  }

  getSystemDate(): Observable<Date> {
    const datejsonObservable: Observable<string> = this.httpService.get<string>(this.baseUrl + '/systemdate/');
    return datejsonObservable.pipe(map((date: string) => new Date(date)));
  }
}
