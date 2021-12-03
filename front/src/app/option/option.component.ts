import {Component, OnInit} from '@angular/core';
import {GlobalVariable} from '../globals';
import {OptionService} from './option.service';
import {Volume} from './volume';
import {Backup} from './backup';
import {FileUploader} from 'ng2-file-upload';

interface Alert {
  type: string;
  message: string;
}

@Component({
  selector: 'app-option',
  templateUrl: './option.component.html',
  styleUrls: ['./option.component.css']
})

export class OptionComponent implements OnInit {
  alerts: Alert[];
  baseUrl: string = GlobalVariable.BASE_API_URL;
  currentVolume: Volume = new Volume(70);
  currentBackup: Backup;
  volumeLoaded = false;

  public uploader: FileUploader = new FileUploader({
    url: this.baseUrl + '/backup/',
    method: 'POST',
    itemAlias: 'backupFile',
    queueLimit: 1,
    removeAfterUpload: true
  });

  constructor(private optionService: OptionService) {
    this.uploader.onSuccessItem = () => {
      console.log('upload complete');
      this.refreshBackup();
      this.alerts = [{
        type: 'success',
        message: 'File uploaded',
      }];
    };

    this.uploader.onErrorItem = () => {
      console.log('upload failed');
      this.alerts = [{
        type: 'danger',
        message: 'Fail to upload',
      }];
    };
  }

  close(alert: Alert) {
    this.alerts.splice(this.alerts.indexOf(alert), 1);
  }

  ngOnInit() {
    // get the current volume
    this.refreshVolume();
    // get the current backup file
    this.refreshBackup();
    // set CORS to *
    this.uploader.onBeforeUploadItem = (item) => {
      item.withCredentials = false;
    };
  }

  /**
   * Load the view with the received backup file
   */
  refreshBackup() {
    this.optionService.getBackup().subscribe(this.setBackup.bind(this));
  }

  refreshVolume() {
    this.optionService.getVolume().subscribe(this.setVolume.bind(this));
  }

  setVolume(volume: Volume) {
    this.currentVolume = volume;
    this.volumeLoaded = true;
  }

  setBackup(backup: Backup[]) {
    console.log('Received backup: ');
    console.log(backup);
    if (typeof backup !== 'undefined' && backup.length > 0) {
      // the array is defined and has at least one element
      console.log(backup[0]);
      // we receive a complete path that contain the root path and the file name. let's keep only the file name
      const tmpBackup = backup[0];
      tmpBackup.backupFile = tmpBackup.backupFile.split('/')[1];
      this.currentBackup = tmpBackup;
    }
  }

  reduceVolume() {
    let newVolumeLevel = this.currentVolume.volume;
    newVolumeLevel = newVolumeLevel - 2;
    if (newVolumeLevel < 0) {
      newVolumeLevel = 0;
    }
    this.currentVolume.volume = newVolumeLevel;
    this.optionService.setVolume(this.currentVolume).subscribe(
      () => {
        this.refreshVolume();
      },
      error => console.log('Error ' + error)
    );
  }

  increaseVolume() {
    let newVolumeLevel = this.currentVolume.volume;
    newVolumeLevel = newVolumeLevel + 2;
    if (newVolumeLevel > 100) {
      newVolumeLevel = 100;
    }
    this.currentVolume.volume = newVolumeLevel;
    this.optionService.setVolume(this.currentVolume).subscribe(
      () => {
        this.refreshVolume();
      },
      error => console.log('Error ' + error)
    );
  }
}

