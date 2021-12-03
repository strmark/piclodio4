import {Component, OnDestroy, OnInit} from '@angular/core';
import {AlarmClock} from '../alarm-clock/alarm-clock';
import {AlarmClockService} from '../alarm-clock/alarm-clock.service';
import {Player} from '../player/player';
import {PlayerService} from '../player/player.service';
import {WebRadio} from '../web-radio/web-radio';
import {WebRadioService} from '../web-radio/web-radio.service';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit, OnDestroy {

  clock: number;
  activeWebradios: any[];
  activeAlarms: AlarmClock[];
  allWebradios: any[];
  player: Player;
  playerLoaded = false;

  constructor(private webRadioService: WebRadioService,
              private playerService: PlayerService,
              private alarmClockService: AlarmClockService) {
    setInterval(() => {
      this.clock = Date.now();
    }, 1);
  }

  ngOnDestroy(): void {
        throw new Error('Method not implemented.');
    }

  ngOnInit() {
    this.webRadioService.getAllWebRadios()
      .subscribe(this.filterDefaultWebRadio.bind(this));
    this.playerService.getPlayerStatus().subscribe(this.setPlayerStatus.bind(this));
    this.alarmClockService.getAllAlarmClocks().subscribe(this.setActiveAlarmClocks.bind(this));
  }

  filterDefaultWebRadio(webradios: WebRadio[]) {
    this.allWebradios = webradios;
    console.log(webradios);
    this.activeWebradios = this.allWebradios.filter(
      webradio => webradio.default === true
    );
  }

  setPlayerStatus(player: Player) {
    console.log('Player: ' + player);
    this.player = player;
    this.playerLoaded = true;
  }

  switchPlayerStatus(status: string) {
    this.player.status = status;
    this.playerService.updatePlayer(this.player).subscribe(this.setPlayerStatus.bind(this));
  }

  setActiveAlarmClocks(alarmclocks: AlarmClock[]) {
    this.activeAlarms = alarmclocks.filter(
      alarms => alarms.active === true
    );
  }
}

