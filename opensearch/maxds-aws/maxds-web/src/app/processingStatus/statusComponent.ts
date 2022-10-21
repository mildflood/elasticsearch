import { Component } from '@angular/core';
import { OnInit, ViewChild } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ISubscription } from 'rxjs/Subscription';
import { NavigationEnd, Router } from '@angular/router';
import { StatusService } from './statusService';

@Component({
    selector: 'status-page',
    templateUrl: './status.html',
    styleUrls: [`../app.component.css`],
  })
  export class StatusComponent implements OnInit, OnDestroy  {
    private subscription: ISubscription[] = [];
    navigationSubscription;

    constructor(private statusService: StatusService, private router: Router) {
        this.navigationSubscription = this.router.events.subscribe((e: any) => {
          if (e instanceof NavigationEnd) {
          }
        });
      }
  
      ngOnInit() {
       
        console.log('statusComponent : calling ngOnInit...');
       
      }
  
      ngOnDestroy() {
        this.subscription.forEach(s => s.unsubscribe());
        if (this.navigationSubscription) {
            this.navigationSubscription.unsubscribe();
          }
      }

}