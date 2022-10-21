import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { LoginService } from 'app/services/login.service';
import { Router } from '@angular/router';
import { AppComponent } from 'app/app.component';
import { HeaderComponent } from 'app/header/header.component';
import { CommonService } from 'app/services/common.service';
import { ProfileService } from 'app/services/profile.service';



@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  user: any = { username: "", password: "", }
  showErrors: boolean = false;
  loginErrorMsg: string;
  showLoginErrors: boolean = false;
  loginErrorMessg: string[] = [];
  loggeduser: any
  progressSpinner = false;


  constructor(private loginService: LoginService, private profileService: ProfileService, private router: Router, private app: AppComponent, private header: HeaderComponent) {
    this.app.showmenu = false;
    this.showLoginErrors = false;
    this.loginErrorMessg = [];
  }

  ngOnInit() {
    // if (!this.app.loggedUsername) {
    //   console.log("logout");
    //   this.loginService.logout();
    // }
    this.showLoginErrors = false;
    this.loginErrorMessg = [];
    if (localStorage.getItem('user') !== null && localStorage.getItem('error') === 'true') {
      this.showLoginErrors = true;
      this.loginErrorMessg[0] = 'User name not available in the list of valid MAXDS users. Please contact support.';
      this.loginErrorMessg[1] = 'User is not part of Active Directory. Please contact SEC-HELP';
    } else {
      localStorage.clear();
    }
  }

  onLoginSubmit() {
    this.showLoginErrors = false;
    this.loginErrorMessg = [];
    this.loginService.loginApp(this.user.username, this.user.password).subscribe((response) => {
      this.app.showmenu = true;

      if (response.status) {
        this.app.loggedUsername = this.user.username;
        this.app.showmenu = true;
        this.loginService.userId = response.resultObject.userid;
        this.getAllSharedProfiles();
        this.router.navigate(['/home'], {
          state:
          {
            loggedUsername: this.user.username,
          }
        })

      } else {
        console.log("error")
        this.showErrors = true;
        this.loginErrorMsg = "Invalid Login credentails";
        this.router.navigate(['/login'], {})

      }

    },
      (error) => {
        this.showErrors = true;
        this.loginErrorMsg = "Invalid Login credentails";
        console.log(error)
      }

    )

  }

  getAllSharedProfiles() {
    this.progressSpinner = true;
    this.profileService.getAllSharedProfiles().subscribe((response) => {
      let preferencesList = [];
      response.forEach(preferences => {
        const pname: any[] = preferences.companyName.split("$");
        preferencesList.push(
          this.getRootRecords(preferences, pname)
        );
      });
      CommonService.emitsharedPreferenceNotificationEvent(preferencesList);
      this.progressSpinner = false;

    },
      (error) => {
        console.log(error);
        this.progressSpinner = false;
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
  };

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

}
