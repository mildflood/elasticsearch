import { Component, OnInit, ɵConsole } from '@angular/core';
import { ProfileService } from 'app/services/profile.service';
import { Router, ActivatedRoute, NavigationStart, NavigationEnd } from '@angular/router'
import * as _ from 'lodash';
import { FormGroup, FormControl, NgForm, Validators, FormBuilder } from '@angular/forms'
import { stringify } from 'querystring';
import { HomeComponent } from 'app/home/homeComponent';
import { UtilService } from 'app/utils/utilService';
import { MappedEntity } from 'app/domain/mappedEntity';
import { Message, TreeNode } from 'primeng/api';
import { element } from '@angular/core/src/render3';
import { CommonService } from 'app/services/common.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  preferencesList: any[]
  selectedProfile: any[];
  selectedEntity: any[] = [];
  existingProfiles = [];
  companyList: any[];
  termNamesList: any[];
  token = "";
  selectedProfiles: any[] = [];
  processTermsList: any[] = [];
  displayCreateModal: boolean = false;
  displayCreateProfileEditModal = false;
  filteredTerms: any[];
  filteredCompany: any[];
  preferenceForm: FormGroup;


  profile = {
    termName: "",
    companyName: "",
    preferenceyName: "",
    processId: "",
    division: "",
    filerCategory: "",
    industry: "",
    sector: "",
    filterRadio: "companyRadio",
    companies: []
  }
  listSharedPreferences = [];
  selectedSharedProfile = [];
  displayShareProfileModal = false;
  constructor(private profileService: ProfileService, private activatedRoute: ActivatedRoute, private commonService: CommonService, private router: Router, private builder: FormBuilder, private utilService: UtilService) {
    this.ngOnInit();
  }

  companiesList: any[];
  divSectorList: any[];
  filtereddivSector: any[];
  filerCategoryList: any[];
  filteredfilerCategory: any[];
  industryList: any[];
  completeCompanyList: any[];
  company: string;
  homeEntityCol: any[];
  entityList: any[];
  ngOnInit() {

    this.homeEntityCol = [
      { field: 'cik', header: 'Cik' },
      { field: 'companyName', header: 'Company Name' }
    ];

    this.reset();
    this.getAllSharedProfiles();
    this.getAllProfiles();
    this.getTermNames();
    this.getCompanyNames();
    this.formValidation();

    this.utilService.getCompanyList().subscribe((response) => { this.companiesList = response; });
    this.utilService.getDivSectorList().subscribe((response) => {
      this.divSectorList = response;
      this.filtereddivSector = [];
      response.forEach(obj => {
        var name = { label: obj, value: obj };
        this.filtereddivSector.push(name)
      });
    })
    this.utilService.getIndustryList().subscribe((response) => {
      this.industryList = response;
    })
    this.utilService.getFilerCategoryList().subscribe((response) => {
      this.filerCategoryList = response;
      this.filteredfilerCategory = [];
      response.forEach(obj => {
        var name = { label: obj, value: obj };
        this.filteredfilerCategory.push(name)
      });
    })
    this.utilService.getCompleteCompanyList().subscribe((response) => {
      this.completeCompanyList = response;

    });

  }

  // processIndustryChange(event) {
  //   event = event.value ? event.value : event;
  //   event = event.substring(0, 4);
  //   var completeEntities = this.completeEntities.filter(obj => obj.sic === event);
  //   this.sectorCount = completeEntities.length
  // }

  processIndustryChange(event) {
    // event = event.value ? event.value : null;
    if (event != null) {
      this.profile.filerCategory = undefined;
      event = event.substring(0, 4);
      var completeEntities = this.completeEntities.filter(obj => obj.sic === event);
      this.sectorCount = completeEntities.length;
      this.categoryCount = completeEntities.length;
    } else {
      this.sectorCount = this.divisionCount;
      this.categoryCount = this.divisionCount;
    }

  }

  // processfilCatChange(event) {
  //   event = event.value ? event.value : event;
  //   var completeEntities = this.completeEntities.filter(obj =>
  //     obj.filerCategory === event && obj.sic === this.profile.industry.substr(0, 4));
  //   this.sectorCount = completeEntities.length
  //   this.categoryCount = completeEntities.length;
  // }

  processfilCatChange(event) {
    // event = event.value ? event.value : null;
    if (event != null) {
      var completeEntities = this.completeEntities.filter(obj =>
        obj.filerCategory === event && obj.sic === this.profile.industry.substr(0, 4));
      // this.industryCount = completeEntities.length;
      this.categoryCount = completeEntities.length;
    } else {
      // this.industryCount = this.divisionCount;
      this.categoryCount = this.divisionCount;
    }

  }

  goToPeer(event) {

    if (this.profile.companyName != undefined && this.profile.companyName.length > 15) {
      this.company = this.profile.companyName.substr(0, this.profile.companyName.length - 12);
      var peer = this.completeCompanyList.filter(obj => obj.companyName === this.company);
      this.profile.division = peer[0].division;
      this.profile.sector = peer[0].sector;
      this.divSector = peer[0].division + ' -> ' + peer[0].sector;
      this.divSectorChange(this.divSector)
      this.profile.industry = peer[0].sic + '-' + peer[0].industry;
      this.processIndustryChange(this.profile.industry);
      this.profile.filerCategory = peer[0].filerCategory;
      this.processfilCatChange(this.profile.filerCategory);
      this.filterRadio = 'divisionRadio';
    }
  }

  showResolved() {
    let mp = new MappedEntity();
    mp.termRuleId = this.getTermIdOrEntityName(this.profile.termName);
    mp.entityId = "NULL";
    mp.division = this.profile.division;
    mp.sector = this.profile.sector;
    mp.filerCategory = this.profile.filerCategory;
    mp.rankId = 0;
    mp.minYear = (new Date()).getFullYear() - 4;
    mp.maxYear = (new Date()).getFullYear();
    mp.includeQuarterly = true;
    mp.sic = this.profile.industry.substring(0, 4);
    console.log(this.profile);
    console.log(mp)
    this.profileService.showReslovedData(mp).subscribe(results => {
      console.log(results);
      this.entityList = results;
    })

  }

  completeEntities: any[] = [];
  filteredindustry: any[];
  divisionCount: number;
  sectorCount: number;
  categoryCount: number;


  // divSectorChange(event) {
  //   this.completeEntities = this.completeCompanyList;
  //   var sicCodes: any[] = [];
  //   event = event.value ? event.value : event;
  //   var division = event.substr(0, event.indexOf('->') - 1);
  //   var sector = event.substr(event.indexOf('->') + 3, event.length);
  //   if (division) {
  //     this.completeEntities = this.completeCompanyList.filter(obj => obj.division === division);
  //   }

  //   if (sector) {
  //     this.completeEntities = this.completeEntities.filter(obj => obj.sector === sector);
  //   }
  //   this.completeEntities.forEach(obj => {
  //     sicCodes.push(obj.sic + '-' + obj.industry);
  //   })
  //   sicCodes = sicCodes.filter((el, i, a) => i === a.indexOf(el))
  //   this.filteredindustry = [];
  //   sicCodes.forEach(obj => {
  //     var name = { label: obj, value: obj };
  //     this.filteredindustry.push(name);
  //   })

  //   this.divisionCount = this.completeEntities.length;
  //   this.sectorCount = this.completeEntities.length;
  //   this.categoryCount = this.completeEntities.length;;
  //   // if(this.derivedZeroExpressionForEdit.industry) {
  //   //   var completeEntities = this.completeEntities.filter(obj => obj.sic === this.derivedZeroExpressionForEdit.industry);
  //   //   this.sectorCount = completeEntities.length
  //   // }
  // }

  divSectorChange(event) {
    this.completeEntities = this.completeCompanyList;
    var sicCodes: any[] = [];
    // event = event.value ? event.value : event;
    var division = event.substr(0, event.indexOf('->') - 1);
    var sector = event.substr(event.indexOf('->') + 3, event.length);
    this.profile.filerCategory = undefined;
    this.profile.industry = undefined;
    if (division) {
      this.completeEntities = this.completeCompanyList.filter(obj => obj.division === division);
    }

    if (sector) {
      this.completeEntities = this.completeEntities.filter(obj => obj.sector === sector);
    }
    this.completeEntities.forEach(obj => {
      sicCodes.push(obj.sic + '-' + obj.industry);
    })
    sicCodes = sicCodes.filter((el, i, a) => i === a.indexOf(el))
    this.filteredindustry = [];
    sicCodes.forEach(obj => {
      var name = { label: obj, value: obj };
      this.filteredindustry.push(name);
    })

    this.divisionCount = this.completeEntities.length;
    this.sectorCount = this.completeEntities.length;
    this.categoryCount = this.completeEntities.length;;
    // if(this.derivedZeroExpressionForEdit.industry) {
    //   var completeEntities = this.completeEntities.filter(obj => obj.sic === this.derivedZeroExpressionForEdit.industry);
    //   this.sectorCount = completeEntities.length
    // }
  }




  /*
   New login::
  */



  divSector: string = "";
  filterRadio: string = "companyRadio";



  formValidation() {
    this.preferenceForm = this.builder.group({
      termName: [this.profile.termName, []],
      companyName: [this.profile.companyName, []],
      preferenceyName: [this.profile.preferenceyName, [Validators.required, Validators.min(3)]],
      division: [this.profile.division, []],
      sector: [this.profile.sector, []],
      industry: [this.profile.industry, []],
      filerCategory: [this.profile.filerCategory, []],
      filterRadio: [this.profile.filterRadio, []]
    })
  }

  get pereferencesForm() {
    return this.preferenceForm.controls;
  }

  submitted: boolean = false;
  onSubmit() {
    this.submitted = true;
    if (this.preferenceForm.invalid) {
      return;
    } else {
      this.savePreference();
    }
  }

  reset() {
    HTMLDListElement
    this.selectedProfile = [];
    this.selectedProfiles = [];
    this.processTermsList = [];
    this.preferencesList = [];
  }

  termname: string;


  searchTerm(event) {
    this.filteredTerms = [];
    for (let i = 0; i < this.termNamesList.length; i++) {
      let name: string = this.termNamesList[i].name;
      if (name.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.filteredTerms.push(name);
      }
    }
  }

  showTermResultnavigate(data) {
    //let termName = data.code;
    this.router.navigate(['/termresults'],
      {
        state:
        {
          termName: this.getTermIdOrEntityName(data.termName),
          entityId: this.getTermIdOrEntityName(data.companyName),
          companyName: data.companyName.substring(0, data.companyName.indexOf("("))
        }
      })
  }

  searchCompany(event) {
    this.filteredCompany = [];
    for (let i = 0; i < this.companyList.length; i++) {
      let cname: string = this.companyList[i].name;
      if (cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.filteredCompany.push(cname);
      }
    }
  }


  cols = [

    { field: 'companyName', header: 'Company Name', hidden: false, exportable: false, width: '18%' },
    { field: 'termName', header: 'Term Name', hidden: false },
    { field: 'code', header: 'Code', hidden: false, exportable: false },
    { field: 'preferenceName', header: 'Preference Name', hidden: false },
    { field: 'resultLink', header: 'View Results Link', hidden: false },
    { field: 'validationStatus', header: 'Status', hidden: false },
    { field: 'researchLink', header: 'Research Link', hidden: false },
    { field: 'fsqvLink', header: 'FSQV Link', hidden: false },
    { field: 'quarterly', header: 'Is Quarterly', hidden: false },
    { field: 'userid', header: 'User', hidden: false },
  ];

  sharedPreferenceCols = [

    { field: 'companyName', header: 'Company Name', hidden: false, exportable: false, width: '15%' },
    { field: 'termName', header: 'Term Name', hidden: false },
    { field: 'code', header: 'Code', hidden: false, exportable: false },
    { field: 'preferenceName', header: 'Preference Name', hidden: false },
    { field: 'quarterly', header: 'Is Quarterly', hidden: false },
    { field: 'userid', header: 'User', hidden: false },
  ];

  gotoBtnEnable = false;
  isTermNameValid = false;
  selectedTermName = [];
  addOrRemoveTermName() {
    this.selectedTermName = [];

    this.isTermNameValid = false;
    for (let i = 0; i < this.termNamesList.length; i++) {
      let name: string = this.termNamesList[i].name;
      if (name.toLowerCase().indexOf(this.profile.termName.toLowerCase()) == 0) {
        this.selectedTermName.push(name);

        this.isTermNameValid = true;
      }
    }
  }

  addOrRemoveEntities() {
    let name = ""
    this.profile.companyName = "";
    this.profile.companies.forEach(entity => {
      if (name != null || name != "") {
        name = name + "," + entity

      } else {
        name = entity;
      }
    })
    this.profile.companyName = name.substr(1, name.length);

    if (this.profile.companies.length > 1) {
      this.gotoBtnEnable = true;
    } else {
      this.gotoBtnEnable = false;
    }
  }
  resetProfile() {
    this.profile = {
      termName: "",
      companyName: "",
      preferenceyName: "",
      processId: "",
      division: "",
      filerCategory: "",
      industry: "",
      sector: "",
      filterRadio: "companyRadio",
      companies: []
    }
    this.entityList = null;
    this.categoryCount = null;
    this.sectorCount = null;
    this.divisionCount = null;
  }

  openModal() {

    this.resetProfile();
    this.selectedEntity = [];
    this.isUpdate = false;
    this.displayCreateModal = true;
    this.profile.companyName = "";
    this.profile.termName = "";
    this.profile.preferenceyName = "";
    this.profile.processId = "";
    this.divSector = '';
  };

  sharePreference() {
    this.displayShareProfileModal = true;
  }

  isUpdate: boolean = false;
  listPreferences: TreeNode[];
  updateModal(rowdata) {
    this.profile.companies = [];
    this.isUpdate = true;
    this.displayCreateProfileEditModal = true;
    //this.profile.companies.push(rowdata.companyName);

    //reverse term name format from "term name(code)" to "code - term name"
    var tmp = "";
    var newTermName = "";
    var tmp1 = "";
    if (rowdata.termName) {
      tmp = rowdata.termName.split('(');
      var length = tmp.length;
      for (var i = 0 ; i <length-1; i++) {
        if (length > 2) {
          tmp1 = tmp1 + '(' + tmp[i];
        } else {
          tmp1 = tmp1 + tmp[i];
        }

      }
      newTermName = tmp[tmp.length - 1].split(')')[0] + ' - ' + tmp1;
    }
    this.profile.termName = newTermName;
    this.profile.preferenceyName = rowdata.preferenceName;
    this.profile.processId = rowdata.processId;

    this.fetchProcessObject(this.profile.processId);
    console.log(rowdata);

  };

  acceptPreferences(rowdata) {
    this.profileService.acceptSharedPrefernces(rowdata).subscribe((response) => {
      this.listSharedPreferences = [];
      const preferencesList = [];
      response.forEach(preferences => {
        const pname: any[] = preferences.companyName.split("$");
        preferencesList.push(
          this.getRootRecords(preferences, pname)
        );
      });
      this.refreshData();
      this.listSharedPreferences = preferencesList;
      CommonService.emitsharedPreferenceNotificationEvent(this.listSharedPreferences);
    },
      (error) => console.log(error)
    )
  }

  deleteSharedPreference(rowdata) {
    this.profileService.deleteSharedPrefernces([rowdata.processId]).subscribe((response) => {
      this.listSharedPreferences = [];
      const preferencesList = [];
      response.forEach(preferences => {
        const pname: any[] = preferences.companyName.split("$");
        preferencesList.push(
          this.getRootRecords(preferences, pname)
        );
      });
      this.listSharedPreferences = preferencesList;
      CommonService.emitsharedPreferenceNotificationEvent(this.listSharedPreferences);
    },
      (error) => console.log(error)
    )
  }

  fetchProcessObject(processId) {
    this.listPreferences.forEach((parentdata => {
      let selectedProfileId = parentdata.data.processId;
      if (processId === selectedProfileId) {
        let rowdata: any[] = new Array();
        rowdata.push(parentdata.data);
        parentdata.children.forEach(item => {
          rowdata.push(item.data);
        })

        this.entityList = [];
        this.selectedEntity = [];
        rowdata.forEach(element => {
          let entity = { "cik": this.getTermIdOrEntityName(element.companyName), "companyName": element.companyName.substr(0, element.companyName.indexOf('(')) };
          this.entityList.push(entity);
          this.selectedEntity.push(entity);
        })

      }
    }))
  }

  getTermNames() {
    this.profileService.getTermNames().subscribe((response) => {
      this.termNamesList = [];
      response.forEach(preferences => {
        this.termNamesList.push({ name: preferences.name });
      })
    },
      (error) => console.log(error)
    )
  };
  getCompanyNames() {
    this.profileService.getCoompanyNames().subscribe((response) => {
      this.companyList = [];
      response.forEach(preferences => {
        this.companyList.push({ name: preferences });
      })
    },
      (error) => console.log(error)
    )
  }
  openResearchLink(data) {
    let linkUrl = data.researchLink;
    let companyName = data.companyName;
    let cik = companyName.substring(companyName.indexOf("(") + 1, companyName.indexOf(")"));
    linkUrl = linkUrl.substring(0,linkUrl.indexOf("CIK=") + 4) + cik;
    window.open(linkUrl, "_blank");
  }

  openFSQVLink(data) {
    let linkUrl = data.fsqvLink;
    let companyName = data.companyName;
    let cik = companyName.substring(companyName.indexOf("(") + 1, companyName.indexOf(")"));
    linkUrl = linkUrl.substring(0,linkUrl.indexOf("cik=") + 4) + cik;
    window.open(linkUrl, "_blank");
  }

  getAllProfiles() {
    this.profileService.getAllProfiles().subscribe((response) => {
      this.preferencesList = [];
      this.selectedProfile = [];
      this.selectedProfiles = [];
      this.progressSpinner = false;
      this.populateResponse(response);

      setInterval(() => {
        this.msgs = [];
      }, 50000)

    },
      (error) => console.log(error)
    )
  }

  getAllSharedProfiles() {
    this.profileService.getAllSharedProfiles().subscribe((response) => {
      this.progressSpinner = false;
      this.listSharedPreferences = [];
      const preferencesList = [];
      response.forEach(preferences => {
        const pname: any[] = preferences.companyName.split("$");
        preferencesList.push(
          this.getRootRecords(preferences, pname)
        );
      });

      this.listSharedPreferences = preferencesList;
      CommonService.emitsharedPreferenceNotificationEvent(this.listSharedPreferences);

      setInterval(() => {
        this.msgs = [];
      }, 50000)

    },
      (error) => console.log(error)
    )
  }

  savePreference() {

    if (this.preferenceForm.invalid) {
      return;
    } else {


      if (!this.isUpdate) {

        if (this.profile.companyName.length > 0 && this.profile.preferenceyName != null) {
          this.displayCreateModal = false;
          this.displayCreateProfileEditModal = false;
          let finalStr;
          finalStr = this.profile.companyName;
          if (this.selectedEntity.length > 0) {
            this.selectedEntity.forEach(item => {

              finalStr = finalStr.concat("$").concat(item.companyName.concat("(" + item.cik + ")"));

            })
          } else {

          }

          console.log(finalStr);
          this.profileService.savePreference(this.profile.termName, finalStr, this.profile.preferenceyName).subscribe((response) => {
            this.populateResponse(response);
          },
            (error) => console.log(error)
          )
        } else {
          this.displayCreateModal = true;
          this.displayCreateProfileEditModal = false;

        }
        this.selectedProfile = [];
        this.selectedProfiles = [];
      } else {
        this.updateProfile()
      }
    }
  }
  updateProfile() {
    this.displayCreateModal = false;
    this.displayCreateProfileEditModal = false;
    let finalStr;
    finalStr = this.profile.companyName;
    if (this.selectedEntity.length > 0) {
      this.selectedEntity.forEach(item => {
        finalStr = finalStr.concat("$").concat(item.companyName.concat("(" + item.cik + ")"));
      })
    } else {

    }
    this.profileService.updatePreference(this.profile.termName, finalStr, this.profile.preferenceyName, Number(this.profile.processId)).subscribe((response) => {

      /* this.profile.companyName = "";
       this.profile.preferenceyName = "";
       this.profile.termName = "";*/
      this.isUpdate = false;

      this.populateResponse(response);
    },
      (error) => console.log(error)
    )
  }

  deletePreference() {
    this.getProcessIds(this.selectedProfile)


    this.profileService.deletePrefernces(this.selectedProfiles).subscribe((response) => {
      this.populateResponse(response);
    },
      (error) => console.log(error)
    )
  }

  clearPreference() {
    this.getProcessIds(this.selectedProfile)

    this.profileService.clearPrefernces(this.selectedProfiles).subscribe((response) => {
      this.populateResponse(response);
    },
      (error) => console.log(error)
    )
  }

  getProcessIds(selectedItems: any[]) {
    selectedItems.forEach(profiledata => {
      let profile = profiledata.data;
      this.selectedProfiles.push(profile.processId);
    })

  }
  entities: any[];
  progressSpinner: boolean = false;
  msgs: Message[] = [];

  processPreference() {
    this.progressSpinner = true;
    this.msgs = [{ severity: 'info', summary: '"Selected Profile(s) Processing in progress!"', detail: '' }];

    this.selectedProfile.forEach(profiledata => {
      let profile = profiledata.data;
      profile.validationStatus = "In Progress";
      if (profile.termName === "**All Terms**") {
        let termIds: any[] = [];
        this.termNamesList.forEach(termname => {
          termIds.push(this.getTermIdOrEntityName(termname.name));
        })
        this.selectedProfile.forEach(sprofiledata => {
          let sprofile = sprofiledata.data;
          termIds.push(this.getTermIdOrEntityName(sprofile.companyName));
          termIds.push(sprofile.processId);
        })

        this.profileService.processAllTerms(termIds).subscribe((response) => {
          this.msgs = [{ severity: 'info', summary: 'Selected Profile(s) Processed!', detail: '' }];

          this.getAllProfiles();
        },
          (error) => console.log(error)
        )
      } else {
        this.profileService.processMultiEntitiesTerms(profile.code, this.getTermIdOrEntityName(profile.companyName), profile.processId).subscribe((response) => {
          this.msgs = [{ severity: 'info', summary: 'Selected Profile(s) Processed!', detail: '' }];
          this.getAllProfiles();
        },
          (error) => console.log(error)
        )
      }
    })
  }

  splitEntityes(entityName: string) {
    return entityName.split("$");
  }

  refreshData() {
    this.getAllProfiles();
    this.getAllSharedProfiles();
  }
  getTermIdOrEntityName(inputValue: string): string {
    let outputValue: string = inputValue.substring((inputValue.indexOf("(") + 1), inputValue.indexOf(")"));
    return outputValue;
  }

  populateResponse(response) {
    this.listPreferences = [];
    this.preferencesList = [];
    response.forEach(preferences => {

      const pname: any[] = preferences.companyName.split("$");
      this.preferencesList.push(this.getRootRecords(preferences, pname));
      this.listPreferences = this.preferencesList;

    });
    this.selectedProfile = [];


  }

  getRootRecords(preferences, companyNames) {
    const entityName: string = companyNames[0];
    return {
      "data": {
        processId: preferences.processId, companyName: entityName, preferenceName: preferences.preferenceName,
        code: preferences.code, userid: preferences.userid, termName: preferences.termName.split('$')[0], resultLink: preferences.resultLink, validationStatus: preferences.validationStatus,
        researchLink: preferences.researchLink, fsqvLink: preferences.fsqvLink, cik: preferences.cik, cName: preferences.cName, quaterly: preferences.quaterly
      },
      "children": this.getChildRecord(preferences, companyNames)
    }
  }
  getChildRecord(preferences, companyNames) {
    const childrens: any[] = []
    if (preferences.termName.split('$').length > 1) {
      preferences.termName.split('$').forEach((item, index) => {
        if (index > 0) {
          childrens.push({
            "data": {
              processId: preferences.processId, companyName: companyNames[0], preferenceName: preferences.preferenceName,
              code: preferences.code, userid: preferences.userid, termName: item, resultLink: preferences.resultLink, validationStatus: preferences.validationStatus,
              researchLink: preferences.researchLink, fsqvLink: preferences.fsqvLink, cik: preferences.cik, cName: preferences.cName, quaterly: preferences.quaterly
            }
          })
        }
      })
    }
    for (let i = 1; i < companyNames.length; i++) {
      preferences.termName.split('$').forEach(item => {
        childrens.push({
          "data": {
            processId: preferences.processId, companyName: companyNames[i], preferenceName: preferences.preferenceName,
            code: preferences.code, userid: preferences.userid, termName: item, resultLink: preferences.resultLink, validationStatus: preferences.validationStatus,
            researchLink: preferences.researchLink, fsqvLink: preferences.fsqvLink, cik: preferences.cik, cName: preferences.cName, quaterly: preferences.quaterly
          }
        })
      })
    }
    return childrens;
  }

  closeDialog(value) {
    this.displayCreateModal = false;
    this.displayCreateProfileEditModal = false;
    this.getAllProfiles();
  }

  closeSharePreferencesDialog(isPreferenceShared) {
    this.displayShareProfileModal = false;
    if (isPreferenceShared) {
      this.refreshData();
    }
  }

}
