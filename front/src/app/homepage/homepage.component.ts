import { Component, OnDestroy, OnInit } from '@angular/core';
import { AlarmClock } from '../alarm-clock/alarm-clock';
import { AlarmClockService } from './../alarm-clock/alarm-clock.service';
import { DatePipe } from '@angular/common';
import { Player } from './../player/player';
import { PlayerService } from '../player/player.service';
import { WebRadio } from './../web-radio/web-radio';
import { WebRadioService } from '../web-radio/web-radio.service';
import { Observable, Subscription } from 'rxjs';
import { interval } from 'rxjs';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit, OnDestroy {

  clock: number;
  active_webradios: any[];
  active_alarms: AlarmClock[];
  all_webradios: any[];
  player: Player;
  playerLoaded: boolean = false;

  constructor(private webRadioService: WebRadioService,
    private playerService: PlayerService,
    private alarmClockService: AlarmClockService) {
        setInterval(() => {
          this.clock = Date.now();
        }, 1);
  }

  ngOnInit() {
    // get the active web radio
    this.webRadioService.getAllWebRadios()
      .subscribe(this.filterDefaultWebRadio.bind(this));
    // get the player status
    this.playerService.getPlayerStatus().subscribe(this.setPlayerStatus.bind(this));
    // get the list of activated Alarm
    this.alarmClockService.getAllAlarmClocks().subscribe(this.setActiveAlarmClocks.bind(this));
  }

  ngOnDestroy() {
  }


  /**
   * Filter the received list of webradios to keep only the active one (is_default)
   */
  filterDefaultWebRadio(webradios: WebRadio[]) {
    this.all_webradios = webradios;
    console.log(webradios);
    this.active_webradios = this.all_webradios.filter(
      webradio => webradio.default === true
    )
  }

  setPlayerStatus(player: Player){
    console.log("Player: " + player);
    this.player = player;
    this.playerLoaded = true;
  }

  switchPlayerStatus(status: string){
    this.player.status = status;
    this.playerService.updatePlayer(this.player).subscribe(this.setPlayerStatus.bind(this));
  }

  setActiveAlarmClocks(alarmclocks: AlarmClock[]){
    this.active_alarms = alarmclocks.filter(
      alarms => alarms.active === true
    )
  }
}

