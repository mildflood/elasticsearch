import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { LoginService } from 'app/services/login.service';
import { Subscription } from 'rxjs';
import { first } from 'rxjs/operators';
import { UtilService } from '../utils/utilService';
import { CommonService } from 'app/services/common.service';
import { JoyrideService } from 'ngx-joyride';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  showmenu: boolean;
  userFullName: String;
  firstName: string[];
  isAdmin: boolean;
  notifications: number;
  currentUser: String;
  currentUserSubscription: Subscription;
  startTourCheck = false;
  subscription: any;
  steps = ['startTour'];
  appSteps = [
    'refreshData@profile',
    'selectUnselect@profile',
    'processPreference@profile',
    'createPreference@profile',
    'deletePreference@profile',
    'clearPreference@profile',
    'sharePreference@profile',
    'listOfSharedProfile@profile',
    'accept@profile',
    'delete@profile',
    'firstStep@home'
  ]
  constructor(private login: LoginService, private activatedRoute: ActivatedRoute, private router: Router, private joyrideService: JoyrideService, private utilService: UtilService, private commonService: CommonService) {

    this.currentUserSubscription = this.login.currentUser.subscribe(user => {
      this.currentUser = user;
    });

  }
  ngOnInit() {
    this.activatedRoute;
    this.router.events.subscribe((val) => {
      console.log(val)
      this.steps = ['startTour'];
      this.appSteps.forEach(item => {
        if (val['url'] && item.includes(val['url'].replace('/', '@'))) {
          this.steps.push(item);
        }
      })
    });
    this.login.currentUser
    // this.login.userFullName.subscribe(c => {
    //   this.userFullName = c;
    //   this.firstName = this.userFullName.split(" ");
    //   this.userFullName = this.firstName[1];
    //   console.log(this.userFullName);
    // })
    if (this.login.currentUserValue) {
      this.userFullName = this.login.currentUserValue;
      this.firstName = this.userFullName.split(" ");
      this.userFullName = this.firstName[1];
    }
    this.utilService.getUserRolesList().subscribe((response) => {
      this.isAdmin = response.includes('admin');
      var admin = this.isAdmin ? 'true' : null;
      localStorage.setItem("isAdmin", admin);
    })

    this.subscription = CommonService.sharedPreferenceNotification.subscribe(item => {
      this.notifications = item;
    });
  }

  startTour() {
    this.startTourCheck = true;
    const options = {
      steps: this.steps,
      // startWith: 'step3@app',
      // waitingTime: 3000,
      stepDefaultPosition: 'bottom',
      themeColor: '#ffffff',
      showPrevButton: true,
      logsEnabled: false,
      showCounter: false
      // customTexts: { prev: of('<<').pipe(delay(2000)), next: '>>'}
    };
    this.joyrideService.startTour(options).subscribe(
      step => {
        console.log('Next:', step);
      },
      e => {
        console.log('Error', e);
      },
      () => {
        // this.stepDone();
        this.startTourCheck = false;
        console.log('Tour finished');
      }
    );
  }

  logout() {
    this.login.logout();
  }

  ngOnDestroy() {
    // unsubscribe to ensure no memory leaks
    this.currentUserSubscription.unsubscribe();
    this.subscription.unsubscribe();
  }

}
