import {Component, OnInit} from '@angular/core';
import {AlarmClockService} from './alarm-clock.service';
import {AlarmClock} from './alarm-clock';
import {WebRadio} from '../web-radio/web-radio';
import {WebRadioService} from '../web-radio/web-radio.service';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-alarm-clock',
  templateUrl: './alarm-clock.component.html',
  styleUrls: ['./alarm-clock.component.css']
})
export class AlarmClockComponent implements OnInit {

  constructor(
    private alarmClockService: AlarmClockService,
    private webRadioService: WebRadioService,
    private modalService: NgbModal
  ) {
    this.webRadioService.getAllWebRadios().subscribe(this.setWebRadios.bind(this));
    this.maxAutoStopMinute = this.create_range(180);
  }

  closeResult: string;

  alarmclock: AlarmClock;
  alarmclocks: AlarmClock[] = [];
  webradios: WebRadio[] = [];

  timePicker = {hour: 12, minute: 30};

  maxAutoStopMinute: number[];

  newAlarmClock: boolean;
  message: string;
  alarmClockToDelete: AlarmClock;

  private static getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  ngOnInit() {
    this.refreshAlarmClockList();
  }

  create_range(maxVal: number): number[] {
    const x = [];
    let i: number;
    for (i = 0; i <= maxVal; i++) {
      x.push(i);
    }
    return x;
  }

  setWebRadios(webradios: WebRadio[]) {
    console.log(webradios);
    this.webradios = webradios;
  }

  deleteAlarmClock(alarmclock) {
    this.alarmClockService.deleteAlarmClockById(alarmclock.id).subscribe(() => this.refreshAlarmClockList(),
      error => console.log('error: ' + error));
  }

  confirmDeleteAlarmClock(confDel, alarmclock: AlarmClock) {
    console.log('confirmDeleteAlarmClock clicked');
    this.alarmClockToDelete = alarmclock;
    this.modalService.open(confDel, {ariaLabelledBy: 'modal-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
      if (result === 'yes click') {
        console.log('Closed  with ' + this.closeResult);
        this.deleteAlarmClock(this.alarmClockToDelete);
      }
    }, (reason) => {
      this.closeResult = `Dismissed ${AlarmClockComponent.getDismissReason(reason)}`;
      console.log('Dismissed with ${result}');
    });
  }

  setAlarmClocks(alarmclocks: AlarmClock[]) {
    this.alarmclocks = alarmclocks;
  }

  refreshAlarmClockList() {
    this.alarmClockService.getAllAlarmClocks().subscribe(this.setAlarmClocks.bind(this));
  }

  switchActiveAlarmClock(alarmclock: AlarmClock) {
    alarmclock.active = !alarmclock.active;
    this.alarmClockService.updateAlarmClockById(alarmclock.id, alarmclock).subscribe(
      () => {
        this.refreshAlarmClockList();
      },
      error => console.log('Error ' + error)
    );
  }

  save() {
    console.log('Alarm Clock: save clicked');
    if (this.newAlarmClock) {
      console.log('Create new alarm clock');
      this.alarmclock.hour = this.timePicker.hour;
      this.alarmclock.minute = this.timePicker.minute;
      this.alarmclock.active = true;
      console.log(this.alarmclock);
      this.alarmClockService.addAlarmClock(this.alarmclock).subscribe(
        () => {
          this.refreshAlarmClockList();
        },
        error => console.log('Error ' + error)
      );
    } else {
      console.log('alarm clock: alarm clock with id ' + this.alarmclock.id + ' already exist. Call update service');
      this.alarmclock.hour = this.timePicker.hour;
      this.alarmclock.minute = this.timePicker.minute;

      this.alarmClockService.updateAlarmClockById(this.alarmclock.id, this.alarmclock).subscribe(
        () => {
          this.refreshAlarmClockList();
        },
        error => console.log('Error ' + error)
      );
    }

    this.newAlarmClock = false;
    this.alarmclock = null;
  }

  open(content, alarmclock: AlarmClock) {
    if (alarmclock == null) {
      this.newAlarmClock = true;
      this.alarmclock = new AlarmClock();
      this.timePicker = {hour: 12, minute: 30};
      this.alarmclock.autoStopMinutes = 180;
    } else {
      this.newAlarmClock = false;
      this.alarmclock = alarmclock;
      this.timePicker.hour = this.alarmclock.hour;
      this.timePicker.minute = this.alarmclock.minute;
    }
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
      if (result === 'Save click') {
        console.log('Closed  with ' + this.closeResult);
        this.save();
      }
    }, (reason) => {
      this.closeResult = `Dismissed ${AlarmClockComponent.getDismissReason(reason)}`;
      console.log('Dismissed with ${result}');
    });
  }

}
