import { Component, OnInit, Input, OnDestroy, HostListener } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { LoginService } from 'app/services/login.service';
import { Subscription } from 'rxjs';
import { first } from 'rxjs/operators';
import { UtilService } from '../utils/utilService';
import { CommonService } from 'app/services/common.service';
import { ProfileService } from 'app/services/profile.service';
import { JoyrideService } from 'ngx-joyride';
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  @HostListener('document:click', ['$event.target'])
  public onClick(targetElement) {
    if (targetElement.classList.contains('joyride-backdrop')) {
      this.joyrideService.closeTour();
    }
  }

  showmenu: boolean;
  userFullName: String;
  firstName: string[];
  isAdmin: boolean;
  notifications: any;
  currentUser: String;
  currentUserSubscription: Subscription;
  errorMessage: boolean = false;
  startTourCheck = false;
  subscription: any;
  steps = ['startTour']
  appStep = [
    'termRuleSelection@home',
    'termName@home',
    'termCode@home',
    'difinition@home',
    'IncludeInAccuracyTest@home',
    'periodType@home',
    'type@home',
    'financialStatement@home',
    'priorityGroup@home',
    'addTerm@home',
    'saveTerm@home',
    'processTerm@home',
    'removeTerm@home',
    // 'expLabel@home',

    'companyName@export',
    'goToPeer@export',
    'division@export',
    'sector@export',
    'industry@export',
    'filerCategory@export',
    'term@export',
    'selectAllTerms@export',
    'deselectAllTerms@export',
    'annual@export',
    'selectYear@export',
    'overrideSelected@export',
    'includeValidationInfo@export',
    'termResult@export',
    'coverageStats@export',
    'quarterly@export',


    'searchCompany@accuracyTest',
    'year@accuracyTest',
    'runTest@accuracyTest',

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

    'groupName@manageTerm',
    'newGroupStep@manageTerm',
    'newRowStep@manageTerm',
    'searchTermMap@manageTerm',
    'exportTermMap@manageTerm',

    'refreshStatus@status',
    'cancelProcessing@status',

    'type@submit',
    'category@submit',
    'name@submit',
    'email@submit',
    'phone@submit',
    'message@submit',
    'sendFeedback@submit'
  ]
  currentPage = '';
  constructor(private login: LoginService, private location: Location, private activatedRoute: ActivatedRoute, private router: Router, private joyrideService: JoyrideService, private utilService: UtilService, private commonService: CommonService, private profileService: ProfileService) {
    //this.autoLogin();
    this.currentUserSubscription = this.login.currentUser.subscribe(user => {
      this.currentUser = user;
    });

  }
  ngOnInit() {
    this.steps = ['startTour'];
    const locationPath = this.location.path();
    this.currentPage = locationPath.replace('/', '');
    const stepForCurrentRoute = this.appStep.filter((item) => {
      if (locationPath !== '/profile') {
        return item.includes(locationPath.replace('/', '@'));
      } else {
        if (item === 'listOfSharedProfile@profile' || item === 'accept@profile' || item === 'delete@profile') {
          if (this.notifications && this.notifications.length > 0) {
            return item.includes(locationPath.replace('/', '@'));
          }
        } else {
          return item.includes(locationPath.replace('/', '@'));
        }
      }
    });
    // const stepForOtherRoute = this.appStep.filter((item) => {
    //   return !item.includes(locationPath.replace('/', '@'));
    // });
    this.steps = this.steps.concat(stepForCurrentRoute);
    // this.steps = this.steps.concat(stepForOtherRoute);

    this.router.events.subscribe((event) => {
      this.steps = ['startTour']
      if (event instanceof NavigationEnd) {
        this.currentPage = event.url.replace('/', '');
        const stepForCurrentRoute = this.appStep.filter((item) => {
          if (event.url !== '/profile') {
            return item.includes(event.url.replace('/', '@'));
          } else {
            if (item === 'listOfSharedProfile@profile' || item === 'accept@profile' || item === 'delete@profile') {
              if (this.notifications && this.notifications.length > 0) {
                return item.includes(event.url.replace('/', '@'));
              }
            } else {
              return item.includes(event.url.replace('/', '@'));
            }
          }
        });
        // const stepForOtherRoute = this.appStep.filter((item) => {
        //   return !item.includes(event.url.replace('/', '@'));
        // });
        this.steps = this.steps.concat(stepForCurrentRoute);
        // this.steps = this.steps.concat(stepForOtherRoute);
      }
    });

    this.login.currentUser;
    if (localStorage.getItem("userFullName")) {
      if (this.currentUser == null) {
        this.userFullName = this.login.fullname;
      } else {
        this.userFullName = this.currentUser;
      }
      this.firstName = this.userFullName.split(" ");
      this.userFullName = this.firstName[1];
      this.getAllSharedProfiles();
    } else {
      this.autoLogin();
    }
    this.subscription = CommonService.sharedPreferenceNotification.subscribe(item => {
      this.notifications = item;
    });

  }
  startTour() {
    CommonService.setTourGuideStart(true);
    this.startTourCheck = true;
    const options = {
      steps: this.steps,
      // startWith: 'step3@app',
      // waitingTime: 3000,
      stepDefaultPosition: 'bottom',
      themeColor: 'transparent',
      showPrevButton: true,
      logsEnabled: false,
      showCounter: false
      // customTexts: { prev: of('<<').pipe(delay(2000)), next: '>>'}
    };
    this.joyrideService.startTour(options).subscribe(
      step => {
        if (step.route === 'export') {
          if (step.name === 'companyName') {
            CommonService.setExportTabIndex(0);
          }

          if (step.name === 'term') {
            CommonService.setExportTabIndex(1);
          }
          if (step.name === 'annual') {
            CommonService.setExportTabIndex(2);
          }

          if (step.name === 'filerCategory') {
            CommonService.setExportTabIndex(0);
          }
          if (step.name === 'deselectAllTerms') {
            CommonService.setExportTabIndex(1);
          }
          if (step.name === 'quarterly') {
            CommonService.setExportTabIndex(2);
          }
        }
      },
      e => {
        console.log('Error', e);
      },
      () => {
        // this.stepDone();
        this.startTourCheck = false;
        console.log('Tour finished');
        CommonService.setTourGuideStart(false);
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
    this.logout();
  }
  autoLogin() {
    this.utilService.autoLogin().subscribe((response) => {
      // this.app.showmenu = true;
      if (response.resultObject != null) {
        localStorage.removeItem('error')
        localStorage.removeItem('user');
        this.userFullName = response.resultObject.password;
        localStorage.setItem('userid', response.resultObject.userid);
        this.utilService.getUserRolesList().subscribe((res) => {
          if(res.length === 0) {
            localStorage.setItem('error', 'true');
            localStorage.setItem('user', 'null');
            this.errorMessage = true;
            return this.router.navigate(['/login']);
          }
          this.isAdmin = res.includes('admin');
          var admin = this.isAdmin ? 'true' : null;
          localStorage.setItem("isAdmin", admin);
        })
      } else {
        localStorage.setItem('error', 'true');
        localStorage.setItem('user', 'null');
        this.errorMessage = true;
        return this.router.navigate(['/login']);

      }

      if (response.status) {
        this.getAllSharedProfiles();
        this.router.navigate(['/home'], {
          state:
          {
            loggedUsername: response.username,

          }
        })
        this.router.getCurrentNavigation

      } else {
        console.log("error")

        this.router.navigate(['/login'], {})

      }

    },
      (error) => {
        console.log(error)
      }

    )
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }

  getAllSharedProfiles() {
    this.profileService.getAllSharedProfiles().subscribe((response) => {
      let preferencesList = [];
      response.forEach(preferences => {
        const pname: any[] = preferences.companyName.split("$");
        preferencesList.push(
          this.getRootRecords(preferences, pname)
        );
      });
      CommonService.emitsharedPreferenceNotificationEvent(preferencesList);

    },
      (error) => {
        console.log(error);
      }
    )
  }
  getRootRecords(preferences, companyNames) {
    const entityName: string = companyNames[0];
    return {
      "data": {
        processId: preferences.processId, companyName: entityName, preferenceName: preferences.preferenceName,
        code: preferences.code, userid: preferences.userid, termName: preferences.termName, resultLink: preferences.resultLink, validationStatus: preferences.validationStatus,
        researchLink: preferences.researchLink, cik: preferences.cik, cName: preferences.cName, quaterly: preferences.quaterly
      },
      "children": this.getChildRecord(preferences, companyNames)
    }
  }
  getChildRecord(preferences, companyNames) {
    const childrens: any[] = []
    for (let i = 1; i < companyNames.length; i++) {
      childrens.push({
        "data": {
          processId: preferences.processId, companyName: companyNames[i], preferenceName: preferences.preferenceName,
          code: preferences.code, userid: preferences.userid, termName: preferences.termName, resultLink: preferences.resultLink, validationStatus: preferences.validationStatus,
          researchLink: preferences.researchLink, cik: preferences.cik, cName: preferences.cName, quaterly: preferences.quaterly
        }
      })
    }
    return childrens;
  }

  goToHome() {
    this.router.navigate(['/home']);
  }

}
