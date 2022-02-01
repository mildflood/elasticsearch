import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ProfileService } from 'app/services/profile.service';
import { Router } from '@angular/router';
import { UtilService } from 'app/utils/utilService';
import { profile } from 'app/profile/profile';

@Component({
  selector: 'app-share-preferences',
  templateUrl: './share-preferences.component.html',
  styleUrls: ['./share-preferences.component.css']
})
export class SharePreferencesComponent implements OnInit {

  @Input() selectedProfile;
  preferenceForm: FormGroup;
  listOfUsersToShareEveryone = [
    'chadav',
    'sharmaan'
  ]
  @Output() closeDialogBox = new EventEmitter<boolean>();

  constructor(private profileService: ProfileService, private router: Router, private builder: FormBuilder, private utilService: UtilService) {
  }

  ngOnInit() {
    this.preferenceForm = this.builder.group({
      userName: ['', [Validators.required]],
      shareToEveryone: [''],
    });

    this.preferenceForm.controls.shareToEveryone.valueChanges.subscribe(value => {
      if (value) {
        this.preferenceForm.controls.userName.clearValidators();
        this.preferenceForm.controls.userName.reset();
        this.preferenceForm.controls.userName.setErrors(null);
        this.preferenceForm.updateValueAndValidity();
      } else {
        this.preferenceForm.controls.userName.setValidators([Validators.required]);
        this.preferenceForm.controls.userName.setErrors({ 'required': true });
        this.preferenceForm.updateValueAndValidity();
      }
    });
  }

  shareSharedPreference() {
    let sharedItemsCount = 0;
    let updatedSelectedProfile = [];
    this.selectedProfile.forEach(item => {
      if (updatedSelectedProfile.find(profileData => profileData.data.preferenceName === item.data.preferenceName)) {
        updatedSelectedProfile.find(profileData => profileData.data.preferenceName === item.data.preferenceName).data.companyName = updatedSelectedProfile.find(profileData => profileData.data.preferenceName === item.data.preferenceName).data.companyName + '$' + item.data.companyName;
      } else {
        updatedSelectedProfile.push(item);
      }
    })
    updatedSelectedProfile.forEach(item => {
      const reqData = [];
 /*     if (this.preferenceForm.controls.userName) {
        this.listOfUsersToShareEveryone.forEach(user => {
          reqData.push({
            termName: item.data.termName,
            companyName: item.data.companyName,
            preName: item.data.preferenceName + ' (' + item.data.userid + ')',
            userid: user
          });
        });
      } else {  */
        this.preferenceForm.value.userName.split(';').forEach(user => {
          reqData.push({
            termName: item.data.termName,
            companyName: item.data.companyName,
            preName: item.data.preferenceName + ' (' + item.data.userid + ')',
            userid: user
          });
        });
  //    }
      this.profileService.saveSharedPreference(item.data.code, item.data.userid, reqData).subscribe((response) => {
        sharedItemsCount = sharedItemsCount + 1;
        if (sharedItemsCount === updatedSelectedProfile.length) {
          this.closeDialogBox.emit(true);
        }
      },
        (error) => console.log(error)
      )
    })
  }

  closeDialog() {
    this.closeDialogBox.emit(false);
  }

  ngOnDestroy() { }

}
