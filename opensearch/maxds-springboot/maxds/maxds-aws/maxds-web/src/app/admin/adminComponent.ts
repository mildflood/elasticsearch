import { Component } from '@angular/core';
import { OnInit, ViewChild } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ISubscription } from 'rxjs/Subscription';
import { NavigationEnd, Router } from '@angular/router';
import { UtilService } from '../utils/utilService';
import { MessagesModule } from 'primeng/messages';
import { MessageModule } from 'primeng/message';
import { FileUploadModule } from 'primeng/fileupload';
import { Messages, Message } from 'primeng/primeng';
import { forkJoin } from 'rxjs';
import { ProfileService } from 'app/services/profile.service';
@Component({
  selector: 'admin-page',
  templateUrl: './admin.html',
  styleUrls: [`../app.component.css`],
})
export class AdminComponent implements OnInit, OnDestroy {
  private subscription: ISubscription[] = [];
  navigationSubscription;
  uploadedFiles: any[] = [];
  msgs: Message[] = [];
  errorMsg: string = '';
  showError: boolean;
  termList = [];
  entityList = [];
  reportData = [];
  reportDataCols = [
    { field: 'rowNumber', header: 'Row Number', width: '10%' },
    { field: 'profileID', header: 'Profile Id', width: '30%' },
    { field: 'collectionType', header: 'Collection Type', width: '30%' },
    { field: 'collectionName', header: 'Collection Name', width: '30%' }
  ];
  constructor(private utils: UtilService, private router: Router, private utilService: UtilService, private profileService: ProfileService) {
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
      }
    });
  }

  ngOnInit() {
    var isAdmin = localStorage.getItem("isAdmin")
    if (isAdmin == "null") {
      this.router.navigate(['/home']);
    }
    console.log('adminComponent : calling ngOnInit...');
    forkJoin(
      this.utilService.getTermRuleList(),
      this.utilService.getCompleteCompanyList(),
      this.profileService.getReportData()
    ).subscribe(([termList, entityList, reportData]) => {
      this.termList = termList ? termList : [];
      this.entityList = entityList ? entityList : [];
      if (reportData && reportData.length > 0) {
        reportData.forEach((value, index) => {
          value['rowNumber'] = index;
          this.reportData.push(value);
        })
      }
    });


  }

  onUpload(event) {
    this.errorMsg = "";
    this.showError = false;
    this.msgs = [];
    var file = event.files[0];
    // for(let file of event.files) {
    //   file = file;
    //     this.uploadedFiles.push(file);
    // }

    //var f = document.getElementById('myFileField').files[0];

    //Take the first selected file
    let fd = new FormData();
    fd.append('file', event.files[0]);
    this.utils.uploadLookupReferenceFile(fd).subscribe((response) => {
      if (response) {
        this.msgs = [{ severity: 'info', summary: 'Admin Operation', detail: 'Successfully uploaded file ' + file.name + ' to server.' }];
      }
      else {
        this.errorMsg = 'Error, Failed to upload file to server.';
        this.showError = true;
      }
    }, function errorCallback(response) {
      this.errorMsg = 'Error, Failed to upload file to server.';
      this.showError = true;
    });


    // this.messageService.add({severity: 'info', summary: 'File Uploaded', detail: ''});
  }



  ngOnDestroy() {
    this.subscription.forEach(s => s.unsubscribe());
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

}