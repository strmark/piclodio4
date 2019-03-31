import { Component, OnInit } from '@angular/core';
import { AlarmClockService } from './alarm-clock.service';
import { AlarmClock } from './alarm-clock';
import { WebRadio } from '../web-radio/web-radio';
import { WebRadioService } from '../web-radio/web-radio.service';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-alarm-clock',
  templateUrl: './alarm-clock.component.html',
  styleUrls: ['./alarm-clock.component.css']
})
export class AlarmClockComponent implements OnInit {
  closeResult: string;

  alarmclock: AlarmClock;
  alarmclocks: AlarmClock[] = [];
  webradios: WebRadio[] = [];

  timePicker = {hour: 12, minute: 30};

  max_auto_stop_minute: number[];

  newAlarmClock: Boolean;
  message: String;
  alarmClockToDelete: AlarmClock;

  constructor( private alarmClockService: AlarmClockService 
             , private webRadioService: WebRadioService
             , private modalService: NgbModal
             ) {
    this.webRadioService.getAllWebRadios().subscribe(this.setWebRadios.bind(this));
    this.max_auto_stop_minute = this.create_range(90);
  }

  ngOnInit() {
    this.refreshAlarmClockList();
  }

  create_range(maxVal: number): number[] {
    var x = [];
    var i = 0;
    while (x.push(i++) <= maxVal) {};
    return x;
  }

  setWebRadios(webradios: WebRadio[]) {
    console.log(webradios);
    this.webradios = webradios;
  }

  deleteAlarmClock(alarmclock){
    // console.log(alarmclock)
    this.alarmClockService.deleteAlarmClockById(alarmclock.id).subscribe(success => this.refreshAlarmClockList(),
      error => console.log("error: " + error));
  }

  confirmDeleteAlarmClock(confDel, alarmclock: AlarmClock){
    console.log("confirmDeleteAlarmClock clicked");
    this.alarmClockToDelete = alarmclock;
    this.modalService.open(confDel, {ariaLabelledBy: 'modal-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
      if (result = 'yes click'){
        console.log("Closed  with " + this.closeResult);
        this.deleteAlarmClock(this.alarmClockToDelete);
      }
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
      console.log("Dismissed with ${result}");
    });
  }

  setAlarmClocks(alarmclocks: AlarmClock[]){
    this.alarmclocks = alarmclocks;
  }

  refreshAlarmClockList(){
      this.alarmClockService.getAllAlarmClocks().subscribe(this.setAlarmClocks.bind(this));
  }

  /**
   * Switch the status of the target AlarmClock. If the alarm is active, then switch to inactive and vice versa
   * */
  switchActiveAlarmClock(alarmclock: AlarmClock){
    if (alarmclock.is_active){
      alarmclock.is_active = false
    }else{
       alarmclock.is_active = true
    }
    // update the AlarmClock
    this.alarmClockService.updateAlarmClockById(alarmclock.id, alarmclock).subscribe(
        success => {
          this.refreshAlarmClockList();
        },
        error => console.log("Error "+ error)
      );
  }

  save() {
    console.log("Alarm Clock: save clicked")
    if (this.newAlarmClock){
      console.log("Create new alarm clock");
      //this.alarmclock.hour = this.timePicker.getHours();
      this.alarmclock.hour = this.timePicker.hour;
      //this.alarmclock.minute = this.timePicker.getMinutes();
      this.alarmclock.minute = this.timePicker.minute;
      this.alarmclock.is_active = true;
      console.log(this.alarmclock);
      this.alarmClockService.addAlarmClock(this.alarmclock).subscribe(
        success => {
          this.refreshAlarmClockList();
        },
        error => console.log("Error "+ error)
      );
    } else {
      console.log("alarm clock: alarm clock with id "+ this.alarmclock.id +" already exist. Call update service");
      this.alarmclock.hour = this.timePicker.hour;
      this.alarmclock.minute = this.timePicker.minute;

      this.alarmClockService.updateAlarmClockById(this.alarmclock.id, this.alarmclock).subscribe(
        success => {
          this.refreshAlarmClockList();
        },
        error => console.log("Error "+ error)
      );
    }

   this.newAlarmClock = false;
   this.alarmclock = null;
   }

  open(content,  alarmclock: AlarmClock) {
    if(alarmclock == null){
      this.newAlarmClock = true;
      this.alarmclock = new AlarmClock();
      this.timePicker= {hour: 12, minute:30};
      this.alarmclock.auto_stop_minutes = 90;
    } else {
      this.newAlarmClock = false;
      this.alarmclock = alarmclock;
      //this.timePicker= new Date();
      this.timePicker.hour=this.alarmclock.hour;
      this.timePicker.minute=this.alarmclock.minute;
    }
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
      if (result = 'Save click'){
        console.log("Closed  with " + this.closeResult);
        this.save();
      }
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
      console.log("Dismissed with ${result}");
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }

}
