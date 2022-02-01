import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { HttpClientXsrfModule } from '@angular/common/http';
import { HeaderComponent } from './header/header.component';
import { LoginService } from 'app/services/login.service';
import { Router } from '@angular/router';
import { Idle, DEFAULT_INTERRUPTSOURCES } from '@ng-idle/core';
import { Keepalive } from '@ng-idle/keepalive';
import { JoyrideService } from 'ngx-joyride';
import { ProfileService } from 'app/services/profile.service';
import { CommonService } from 'app/services/common.service';
import { UtilService } from 'app/utils/utilService';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Maxds application';
  showmenu = true;
  loggedUsername: string;
  userFullName: string;
  public username: String = '';

  timedOut = false;
  lastPing?: Date = null;

  @Output('onUserNameChange') onUserNameChange = new EventEmitter<string>();
  constructor(private login: LoginService, private router: Router, private idle: Idle, private keepalive: Keepalive,
    private readonly joyrideService: JoyrideService, private utilService: UtilService, private profileService: ProfileService) {
    this.login.userId = localStorage.getItem("userid");
    // localStorage.clear();
  }
}


