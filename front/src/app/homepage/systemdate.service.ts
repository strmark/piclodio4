import { GlobalVariable } from './../globals';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as url from 'url';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';


@Injectable()
export class SystemDateService {

    baseUrl: string = GlobalVariable.BASE_API_URL;

    constructor(private httpService: HttpClient) {}

    // GET /alarmclocks

    getSystemDate(): Observable < Date > {
        var datejsonObservable: Observable <String> = this.httpService.get<string>(this.baseUrl + "/systemdate/");
        var dateObservable: Observable <Date> = datejsonObservable.pipe(map((date: string) => new Date(date)));
        //return this.dateJson(datejsonObservable);
        return dateObservable;
    }
}
