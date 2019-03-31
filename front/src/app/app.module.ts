import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgbModalModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule,  Routes }   from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient} from '@angular/common/http';
import { FileUploadModule } from 'ng2-file-upload';

import { OptionService } from './option/option.service';
import { PlayerService } from './player/player.service';
import { SystemDateService } from './homepage/systemdate.service';
import { AlarmClockService} from "./alarm-clock/alarm-clock.service";
import { WebRadioService } from './web-radio/web-radio.service';

import { AppComponent } from './app.component';
import { HomepageComponent } from './homepage/homepage.component';
import { WebRadioComponent } from './web-radio/web-radio.component';
import { OptionComponent } from './option/option.component';
import { AlarmClockComponent } from './alarm-clock/alarm-clock.component';

@NgModule({
  declarations: [
    AppComponent,
    HomepageComponent,
    WebRadioComponent,
    OptionComponent,
    AlarmClockComponent,
  ],
  imports: [
    NgbModule,
    NgbModalModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    FileUploadModule,
    RouterModule.forRoot([
      {
        path: '',
        component: HomepageComponent
      },
      {
        path: 'homepage',
        component: HomepageComponent
      },
      {
        path: 'webradio',
        component: WebRadioComponent
      },
      {
        path: 'alarm',
        component: AlarmClockComponent
      },
      {
        path: 'option',
        component: OptionComponent
      }
   ])
  ],
  providers: [WebRadioService, AlarmClockService, SystemDateService, PlayerService, OptionService],
  bootstrap: [AppComponent]
})
export class AppModule { }
