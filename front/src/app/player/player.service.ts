import {Player} from './player';
import {Observable} from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {GlobalVariable} from '../globals';
import {Injectable} from '@angular/core';

@Injectable()
export class PlayerService {

  baseUrl: string = GlobalVariable.BASE_API_URL;

  constructor(private httpService: HttpClient) {
  }

  getPlayerStatus(): Observable<Player> {
    return this.httpService.get<Player>(this.baseUrl + '/player/');
  }

  updatePlayer(player: Player): Observable<Player> {
    const body = JSON.stringify(player);
    return this.httpService.post<Player>(this.baseUrl + '/player/', body, {
      headers: new HttpHeaders().set('Content-Type', 'application/json'),
    });
  }

}
