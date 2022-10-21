import { Component, OnInit, EventEmitter, Input, Output, OnDestroy } from '@angular/core';
import { ProfileService } from 'app/services/profile.service';
import { Router } from '@angular/router'
import * as _ from 'lodash';
import { FormGroup, FormBuilder, Validators } from '@angular/forms'
import { UtilService } from 'app/utils/utilService';
import { MappedEntity } from 'app/domain/mappedEntity';

@Component({
  selector: 'app-create-profile',
  templateUrl: './create-profile.component.html',
  styleUrls: ['./create-profile.component.css']
})
export class CreateProfileComponent implements OnInit, OnDestroy {

  @Input() profile;
  @Input() filteredTerms;
  @Input() filteredCompany;
  @Input() filtereddivSector;
  @Input() entityList;
  @Input() isUpdate;
  @Input() actionType;
  @Input() companyList: any[];
  @Input() termNamesList: any[];
  preferencesList: any[]
  selectedProfile: any[];
  selectedEntity: any[] = [];
  existingProfiles = [];
  token = "";
  selectedProfiles: any[] = [];
  processTermsList: any[] = [];
  preferenceForm: FormGroup;

  companiesList: any[];
  divSectorList: any[];
  filerCategoryList: any[];
  @Input() filteredfilerCategory: any[];
  industryList: any[];
  @Input() completeCompanyList: any[];
  company: string;
  msgs = [];
  homeEntityCol = [
    { field: 'cik', header: 'Cik' },
    { field: 'companyName', header: 'Company Name' }
  ];
  isTermNameValid = false;
  selectedTermName = [];
  gotoBtnEnable = false;

  completeEntities: any[] = [];
  @Input() filteredindustry: any[];
  divisionCount: number;
  sectorCount: number;
  categoryCount: number;
  divSector;
  filterRadio;
  terms: any = [];
  selectedTerms: any = [];
  actualSelectedTerms = [];
  @Output() closeDialogBox = new EventEmitter<boolean>();

  constructor(private profileService: ProfileService, private router: Router, private builder: FormBuilder, private utilService: UtilService) {
  }

  ngOnInit() {
    this.preferenceForm = this.builder.group({
      termName: [this.profile.termName, []],
      companyName: [this.profile.companyName, []],
      preferenceyName: [this.profile.preferenceyName, [Validators.required, Validators.min(3)]],
      division: [this.profile.division, []],
      sector: [this.profile.sector, []],
      industry: [this.profile.industry, []],
      filerCategory: [this.profile.filerCategory, []],
      filterRadio: [this.profile.filterRadio, []]
    });

    if (this.actionType === 'edit') {
      if (this.entityList.length > 0) {
        this.entityList.forEach(item => {
          this.profile.companies.push(item.companyName + '(' + item.cik + ')')
        })
      }
      this.profile.companyName = '';
      this.entityList = [];
      if (this.profile.termName) {
        this.addOrRemoveTermName();
      }
      this.filterRadio = 'companyRadio';
      this.profile.companies.forEach(item => {
        this.profile.companyName = this.profile.companyName + item + ',';
      })
      this.profile.companyName = this.profile.companyName.substring(0, this.profile.companyName.length - 1);

      if (this.profile.companies.length === 1) {
        this.goToPeer();
      }
    }

    this.preferenceForm.controls.termName.valueChanges.subscribe(value => {
      let id = value.substring(0, value.indexOf('-')-1);
      if (this.terms.find(item => item.termId === id)) {
        this.isTermNameValid = true;
      } else {
        this.isTermNameValid = false;
      }
    });

    this.utilService.getTermRuleList().subscribe((response) => {
      this.terms = response;
      this.terms = this.terms.sort((a, b) => {
        if (a.termId < b.termId) { return -1; }
        if (a.termId > b.termId) { return 1; }
        return 0;
      });
    });
  }


  goToPeer(event?) {

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

  filterTerm(event) {
    if (event.query != null && event.query != '') {
      this.filteredTerms = [];
      this.terms.forEach(cname => {
        if ((cname != null && cname.termId.toLowerCase().includes(event.query.toLowerCase())) || (cname != null && cname.name.toLowerCase().includes(event.query.toLowerCase()))) {
          this.filteredTerms.push(cname.termId + ' - ' + cname.name);
        }
      });
      this.filteredTerms = this.filteredTerms.sort((a, b) => {
        if (a.toLowerCase().indexOf(event.query.toLowerCase()) < b.toLowerCase().indexOf(event.query.toLowerCase())) { return -1; }
        if (a.toLowerCase().indexOf(event.query.toLowerCase()) > b.toLowerCase().indexOf(event.query.toLowerCase())) { return 1; }
        return 0;
      });
    } else {
      this.filteredTerms = [];
      this.terms.forEach(obj => {
        this.filteredTerms.push(obj.termId + ' - ' + obj.name)
      })
    }
  }


  onSelectTerm(event) {
  /*  if (this.selectedTerms && this.selectedTerms.length > 0) {
      const modifiedSelectedTerms = [];
      this.actualSelectedTerms = [];
      this.selectedTerms.forEach(item => {
        this.actualSelectedTerms.push(item);
        modifiedSelectedTerms.push(item.split(' - ')[0]);
      })
      this.selectedTerms = modifiedSelectedTerms;
    } */

    if (this.profile.termName) {
      const modifiedSelectedTerms = [];
      this.actualSelectedTerms = [];
      this.actualSelectedTerms.push(this.profile.termName);
      modifiedSelectedTerms.push(this.profile.termName.split(' - ')[0]);
      this.selectedTerms = modifiedSelectedTerms;
    }
  }

  addOrRemoveTermName() {
    this.selectedTermName = [];

    this.isTermNameValid = false;
    for (let i = 0; i < this.termNamesList.length; i++) {
      let name: string = this.termNamesList[i].name;
      if (name.toLowerCase().indexOf(this.profile.termName.toLowerCase()) == 0) {
        this.selectedTermName.push(name);
        this.selectedTerms.push(name);

        this.isTermNameValid = true;
      }
    }
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
    this.categoryCount = this.completeEntities.length;
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

  getTermIdOrEntityName(inputValue: string): string {
    //let outputValue: string = inputValue.substring((inputValue.indexOf("(") + 1), inputValue.indexOf(")"));
    let outputValue: string = inputValue.substring(0, inputValue.indexOf('-')-1);
    return outputValue;
  }

  processIndustryChange(event) {
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

  processfilCatChange(event) {
    if (event != null) {
      var completeEntities = this.completeEntities.filter(obj =>
        obj.filerCategory === event && obj.sic === this.profile.industry.substr(0, 4));
      this.categoryCount = completeEntities.length;
    } else {
      this.categoryCount = this.divisionCount;
    }
  }

  savePreference() {

    if (this.preferenceForm.invalid) {
      return;
    } else {

      if (!this.isUpdate) {

        if (this.profile.companyName.length > 0 && this.profile.preferenceyName != null) {
          let finalStr;
          finalStr = this.profile.companyName;
          if (this.selectedEntity.length > 0) {
            this.selectedEntity.forEach(item => {
              let cname = item.companyName.concat("(" + item.cik + ")");
              if (cname !== this.profile.companyName) {
                finalStr = finalStr.concat("$").concat(cname);
              }
            })
          } else {

          }
          let term = '';
          term = this.actualSelectedTerms[0].split(' - ')[1] + '(' + this.actualSelectedTerms[0].split(' - ')[0] +')'
          /*
          this.actualSelectedTerms.forEach((item, index) => {
            if (index === (this.actualSelectedTerms.length - 1)) {
              const termObj = this.terms.find(val => val.termId === item);
              term = term + termObj.name + '(' + termObj.termId + ')';
            } else {

              const termObj = this.terms.find(val => val.termId === item);
              term = term + termObj.name + '(' + termObj.termId + ')' + '$';
            }
          }); */

          this.profileService.savePreference(term, finalStr, this.profile.preferenceyName).subscribe((response) => {
            // this.populateResponse(response);
            this.closeDialog();
          },
            (error) => {
              console.log(error);
              if (JSON.parse(error._body).message.includes('Cannot insert duplicate key')) {
                this.msgs = [{ severity: 'info', summary: 'This user profile already exists', detail: '' }];
                setInterval(() => {
                  this.msgs = [];
                }, 50000)
              }
            }
          )
        }
        this.selectedProfile = [];
        this.selectedProfiles = [];
      } else {
        this.updateProfile();

      }
    }
  }

  updateProfile() {
    let finalStr;
    finalStr = this.profile.companyName;
    if (this.selectedEntity.length > 0) {
      this.selectedEntity.forEach(item => {
        finalStr = finalStr.concat("$").concat(item.companyName.concat("(" + item.cik + ")"));
      })
    } else {

    }
    let term = '';
    term = this.actualSelectedTerms[0].split(' - ')[1] + '(' + this.actualSelectedTerms[0].split(' - ')[0] +')'
    /*
    this.actualSelectedTerms.forEach((item, index) => {
      if (index === (this.actualSelectedTerms.length - 1)) {
        const termObj = this.terms.find(val => val.termId === item);
        term = term + termObj.name + '(' + termObj.termId + ')';
      } else {

        const termObj = this.terms.find(val => val.termId === item);
        term = term + termObj.name + '(' + termObj.termId + ')' + '$';
      }
    }); */
    this.profileService.updatePreference(term, finalStr, this.profile.preferenceyName, Number(this.profile.processId)).subscribe((response) => {
      this.isUpdate = false;
      this.closeDialog();
    },
      (error) => console.log(error)
    )
  }

  closeDialog() {
    this.closeDialogBox.emit(true);
  }

  ngOnDestroy() {
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
  }

}
