import { Component } from '@angular/core';
import { OnInit, ViewChild } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ISubscription } from 'rxjs/Subscription';
import { NavigationEnd, Router } from '@angular/router';
import { StatusService } from './statusService';
import { Status } from '../domain/status';
import { UtilService } from 'app/utils/utilService';
import { ConfirmationService, Message } from 'primeng/api';


@Component({
  selector: 'app-root',
  templateUrl: './status.html',
  styleUrls: ['./status.css']
})
export class StatusComponent implements OnInit, OnDestroy {
  private subscription: ISubscription[] = [];
  navigationSubscription;
  statusNumber: number = 0;
  statusCols: any;
  cols: any[];

  statusNames: any[];

  first: any = 0;
  total: any = 0;
  row: any = 10;

  constructor(private statusService: StatusService, private router: Router, private status: Status, private utilService: UtilService, private confirmationService: ConfirmationService) {
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
      }
    });
  }

  msgs: Message[] = [];
  interval: any;
  cancelProcessing() {

    this.confirmationService.confirm({
      message: 'Are you sure you want to cancel all pending processing items?',
      header: 'Process Term',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.msgs = [];

        this.msgs = [{ severity: 'info', summary: 'Cancelling Process All Terms', detail: '' }]
        this.utilService.cancelProcessing().subscribe((response) => {
          if (response) {
            this.msgs = [{ severity: 'info', summary: 'All pending processing has been canceled successfully', detail: '' }];

          }

        })
      },
      reject: () => {
      }
    });
  }
  pagenation(value) {
    console.log(value);
    let initialCount = value.rows;
    value.first = (value.first + initialCount);
    this.first = value.first;
    this.row = (this.statusNames.length - value.first);
    this.total = this.statusNames.length;
  }

  updatePagenationCount(event) {
    //this.statusNames.length; = 
    if (event.filteredValue != this.statusNames.length) {
      let data = event.filteredValue;
      console.log(data.length + " :: " + this.statusNames.length);
      this.first = data.length > 10 ? 10 : data.length;
      this.row = data.length > 10 ? (data.length / 10) : data.length;
      this.total = data.length;
    }

  }

  ngOnInit() {
    console.log('statusServiceComponent : calling ngOnInit...');
    this.statusCols = this.status.statusCols();
    this.interval = setInterval(() => { 
          this.loadProcessStatus(); 
      }, 10000);
         this.loadProcessStatus();
  }
  refreshLoadStatus() {
    this.loadProcessStatus();
  }
  loadProcessStatus() {
    this.statusService.loadProcessStatus().subscribe((response) => {
      this.statusNames = [];
      response.content.forEach(status => {
        this.statusNames.push({
          termId: status.termId, userName: status.userName, logStatus: status.logStatus, noOfEntitiesBeingProcessed: status.noOfEntitiesBeingProcessed,
          lastModified: status.lastModified, processingTimeInSec: status.processingTimeInSec, description: status.description
        })
      })
      console.log(response);
      this.first = 10;
      this.row = (this.statusNames.length - this.first);
      this.total = this.statusNames.length;
    })
  }


  ngOnDestroy() {
    this.subscription.forEach(s => s.unsubscribe());
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }

    if (this.interval) {
      clearInterval(this.interval);
    }
  }

}