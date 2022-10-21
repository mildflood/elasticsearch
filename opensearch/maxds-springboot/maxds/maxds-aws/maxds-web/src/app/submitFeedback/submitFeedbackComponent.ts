import { Component } from '@angular/core';
import { OnInit, ViewChild } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ISubscription } from 'rxjs/Subscription';
import { NavigationEnd, Router } from '@angular/router';
import { SubmitFeedbackService } from './submitFeedbackService';
import { LoginComponent } from 'app/login/login.component';

@Component({
  selector: 'submitFeedback-page',
  templateUrl: './submitFeedback.html',
  styleUrls: [`../app.component.css`],
})
export class SubmitFeedbackComponent implements OnInit, OnDestroy {
  private subscription: ISubscription[] = [];
  navigationSubscription;
  showName: boolean = false;
  showEmail: boolean = false;
  isFeedbackSubmitted: boolean = false;
  feedbackList = [];
  feedbackCols: any[] ;
  feedBackObj = { "issue": "Issue", "category": "Derivation Trail", "name": "", "email": "", "phone": "", "message": "" };
  constructor(private submitFeedbackService: SubmitFeedbackService, private router: Router) {
    
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
      }
    });
  }

  async ngOnInit() {
    let userdata = {};
    console.log('submitFeedbackComponent : calling ngOnInit...');
    this.feedbackCol();
    const res =  localStorage.getItem("isAdmin");
    if(res) {
    this.feedbackList = await this.submitFeedbackService.getFeedback().toPromise();
  } else{
    this.feedbackList = await this.submitFeedbackService.getFeedbackByUser(localStorage.getItem("userName")).toPromise();
  }
    this.feedBackObj.name = localStorage.getItem("userName");
    this.feedBackObj.email = localStorage.getItem("userEmail");
  }

  async sendFeedback() {
    console.log(this.feedBackObj);
    this.submitFeedbackService.submitFeedback(this.feedBackObj).subscribe((response) =>
      console.log(response));
    this.isFeedbackSubmitted = true;
    this.feedBackObj = { "issue": "Issue", "category": "Derivation Trail", "name": localStorage.getItem("userName"), "email": localStorage.getItem("userEmail"), "phone": "", "message": "" };
    const res =  localStorage.getItem("isAdmin");
    if(res) {
      this.feedbackList = await this.submitFeedbackService.getFeedback().toPromise();
    } else{
      this.feedbackList = await this.submitFeedbackService.getFeedbackByUser(localStorage.getItem("userName")).toPromise();
    }
  }

  feedbackCol() {
    this.feedbackCols = [
        { field: 'ISSUETYPE', header: 'Type'},
        { field: 'CATEGORY', header: 'Category'},
        { field: 'NAME', header: 'User Name'},
        { field: 'EMAIL', header: 'Email'},
        { field: 'PHONE', header: 'Phone' },
        { field: 'MESSAGE', header: 'Message', width: '35%'},
        { field: 'dtCreated', header: 'Created Date'}
    ];
    return this.feedbackCols;
}

  ngOnDestroy() {
    this.subscription.forEach(s => s.unsubscribe());
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

}