import { Component } from '@angular/core';
import { OnInit, ViewChild } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ISubscription } from 'rxjs/Subscription';
import { NavigationEnd, Router } from '@angular/router';
import { ExportService } from './exportService';
import { UtilService } from '../utils/utilService';
import { AppComponent } from 'app/app.component';
import { CommonService } from 'app/services/common.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'export-page',
  templateUrl: './export.html',
  styleUrls: [`../app.component.css`],
})
export class ExportComponent implements OnInit, OnDestroy {
  private subscription: ISubscription[] = [];
  navigationSubscription;
  activeIndex: string = '0';

  companyList: any[];

  completeCompanyList: any[];
  completeEntities: any[] = [];
  filteredCompany: any[];
  divisionList: any[];
  filtereddivision: any[];
  division: string;
  sector: string;
  sectorList: any[];
  filteredsector: any[];
  industryList: any[];
  filteredindustry: any[];
  industry: string;
  filerCategoryList: any[];
  filteredfilerCategory: any[];
  filerCategory: string = null;
  sics: any[] = [];
  entitiesList: any = [];
  selectedEntities: any = [];
  company: string;
  companyName: string;
  industryCheck: boolean;

  termInput: any;
  actualTerms: any[] = [];
  terms: any[] = [];
  selectedTerms: any[] = [];
  filteredTerms: any = [];
  quarterly: boolean = true;
  quatCheck: boolean = false;
  annual: boolean = true;
  selectedOver: boolean = false;
  selectedVal: boolean = false;
  termRes: boolean = true
  covStat: boolean = false;
  termResultFilterOptions = { isForAllEntities: false, exportType: "termResults", includeFiscalYears: true, includeFiscalQuarters: true, includeValidationInfos: false, startYear: (new Date()).getFullYear() - 4, endYear: (new Date()).getFullYear(), entityList: ["000050"], termIdList: ["AVAILSEC", "ACT", "HTMSEC", "XINTRP"] };
  exportDataCols: any[] = [];
  exportData: any;
  actualExportData: any;
  first: any = 10;
  num: number = 1;
  covNum: number = 1;
  last: any = 10;
  total: any = 0;
  row: any = 10;
  covFirst: any = 10;
  covTotal: any = 0;
  covRow: any = 10;

  rangeValues: number[] = [(new Date()).getFullYear() - 4, (new Date()).getFullYear()];
  minYear: number = (new Date()).getFullYear() - 4;
  maxYear: number = (new Date()).getFullYear();
  years: any[] = [{ label: (new Date()).getFullYear() - 4, value: (new Date()).getFullYear() - 4 }, { label: (new Date()).getFullYear() - 3, value: (new Date()).getFullYear() - 3 }, { label: (new Date()).getFullYear() - 2, value: (new Date()).getFullYear() - 2 }, { label: (new Date()).getFullYear() - 1, value: (new Date()).getFullYear() - 1 }, { label: (new Date()).getFullYear(), value: (new Date()).getFullYear() }];
  year: any;

  coverageStats: any[];
  coverageDataCols: any[] = [];
  progressSpinner: boolean;
  covFileName: any = 'CoverageStats';
  expFileName: any = 'TermResults';

  errorMsg: string = '';
  showError: boolean;

  constructor(private exportService: ExportService, private commonService: CommonService, private router: Router, private utilService: UtilService, private app: AppComponent, private route: ActivatedRoute) {
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
      }
    });
  }

  ngOnInit() {
    // CommonService.isTourGuideStart.subscribe((value) => {
    //   if (value) {
    //     this.activeIndex = '0';
    //   }
    // })
    CommonService.exportTabIndex.subscribe((value) => {
      this.activeIndex = value + '';
    })
    console.log('exportComponent : calling ngOnInit...');
    this.utilService.getCompanyList().subscribe((response) => { this.companyList = response; });
    this.utilService.getDivisionList().subscribe((response) => {
      this.divisionList = response;
      this.filtereddivision = [];
      response.forEach(obj => {
        if (obj != null) {
          var name = { label: obj, value: obj };
          this.filtereddivision.push(name)
        }

      });
    })
    this.utilService.getSectorList().subscribe((response) => {
      this.sectorList = response;

    })
    this.utilService.getFilerCategoryList().subscribe((response) => {
      this.filerCategoryList = response;
      this.filteredfilerCategory = [];
      response.forEach(obj => {
        var name = { label: obj, value: obj };
        this.filteredfilerCategory.push(name)
      });
    })
    this.utilService.getIndustryList().subscribe((response) => { this.industryList = response; })

    //this.utilService.getCompleteCompanyList().subscribe((response) => { this.completeCompanyList = response });
    this.completeCompanyList = this.route.snapshot.data['completeCompanyList'];
    this.terms = this.route.snapshot.data['termRuleList'];
    this.terms = this.terms.sort((a, b) => {
      if (a.termId < b.termId) { return -1; }
      if (a.termId > b.termId) { return 1; }
      return 0;
    });
    this.actualTerms = this.terms;
    // this.utilService.getTermRuleList().subscribe((response) => {
    //   this.terms = response;
    //   this.actualTerms = response
    //   this.terms = this.terms.sort((a, b) => {
    //     if (a.termId < b.termId) { return -1; }
    //     if (a.termId > b.termId) { return 1; }
    //     return 0;
    //   });
    // });

    this.terms.forEach(obj => {
      obj.check = false;
    })

    this.actualTerms.forEach(obj => {
      obj.check = false;
    })
  }

  ngAfterContentInit() {
    this.loadFromStorage();
  }

  loadFromStorage () {
    //starting company tab
    if(sessionStorage.getItem('export.companyName') !== null){
      this.companyName = sessionStorage.getItem('export.companyName');
    }

    if(sessionStorage.getItem('export.division') !== null){
      this.division = sessionStorage.getItem('export.division');
      this.divisionChange(this.division, true);
    }

    if(sessionStorage.getItem('export.sector') !== null){
      this.sector = sessionStorage.getItem('export.sector');
      this.sectorChange(this.sector, false);
    }

    if(sessionStorage.getItem('export.industry') !== null){
      this.industry = sessionStorage.getItem('export.industry');
      this.industryChange(this.industry, true);
    }

    if(sessionStorage.getItem('export.filerCategory') !== null){
      this.filerCategory = sessionStorage.getItem('export.filerCategory');
      this.filerChange(this.filerCategory, true);
    }

    if(sessionStorage.getItem('export.entitiesList') !== null){
      this.entitiesList = JSON.parse(sessionStorage.getItem('export.entitiesList'));
    }

    if(sessionStorage.getItem('export.selectedEntities') !== null){
      this.selectedEntities = JSON.parse(sessionStorage.getItem('export.selectedEntities'));
      //update entitiesList checkbox status
      if (this.selectedEntities) {
        this.selectedEntities.forEach(selected => {
          this.entitiesList.forEach(eObj => {
            eObj.entities.forEach(ent => {
              if (ent.cik == selected.cik)
                ent.check = true;
            })
          })
        })
      }
    }

    //starting term tab
    if(sessionStorage.getItem('export.termInput') !== null){
      this.termInput = JSON.parse(sessionStorage.getItem('export.termInput'));
    }

    if(sessionStorage.getItem('export.terms') !== null){
      this.terms = JSON.parse(sessionStorage.getItem('export.terms'));
    }
    if(sessionStorage.getItem('export.filteredTerms') !== null){
      this.filteredTerms = JSON.parse(sessionStorage.getItem('export.filteredTerms'));
    }

    if(sessionStorage.getItem('export.selectedTerms') !== null){
      this.selectedTerms = JSON.parse(sessionStorage.getItem('export.selectedTerms'));
    }

    //starting export tab
    if(sessionStorage.getItem('export.annual') !== null) {
      this.annual = JSON.parse(sessionStorage.getItem('export.annual'));
    }
    if(sessionStorage.getItem('export.selectedOver') !== null) {
      this.selectedOver = JSON.parse(sessionStorage.getItem('export.selectedOver'));
    }
    if(sessionStorage.getItem('export.selectedVal') !== null) {
      this.selectedVal = JSON.parse(sessionStorage.getItem('export.selectedVal'));
    }
    if(sessionStorage.getItem('export.termRes') !== null) {
      this.termRes = JSON.parse(sessionStorage.getItem('export.termRes'));
    }
    if(sessionStorage.getItem('export.covStat') !== null) {
      this.covStat = JSON.parse(sessionStorage.getItem('export.covStat'));
    }
    if(sessionStorage.getItem('export.exportData') !== null) {
      this.exportData = JSON.parse(sessionStorage.getItem('export.exportData'));
    }
    if(sessionStorage.getItem('export.first') !== null) {
      this.first = JSON.parse(sessionStorage.getItem('export.first'));
    }
    if(sessionStorage.getItem('export.row') !== null) {
      this.row = JSON.parse(sessionStorage.getItem('export.row'));
    }
    if(sessionStorage.getItem('export.total') !== null) {
      this.total = JSON.parse(sessionStorage.getItem('export.total'));
    }
    if(sessionStorage.getItem('export.quatCheck') !== null) {
      this.quatCheck = JSON.parse(sessionStorage.getItem('export.quatCheck'));
    }
    if(sessionStorage.getItem('export.termResultFilterOptions.startYear') !== null) {
      this.termResultFilterOptions.startYear = JSON.parse(sessionStorage.getItem('export.termResultFilterOptions.startYear'));
    }
    if(sessionStorage.getItem('export.termResultFilterOptions.endYear') !== null) {
      this.termResultFilterOptions.endYear = JSON.parse(sessionStorage.getItem('export.termResultFilterOptions.endYear'));
    }
    if(sessionStorage.getItem('export.rangeValues') !== null) {
      this.rangeValues = JSON.parse(sessionStorage.getItem('export.rangeValues'));
    }
    if(sessionStorage.getItem('export.year') !== null) {
      this.year = JSON.parse(sessionStorage.getItem('export.year'));
    }

  }
  clearStorage() {
    sessionStorage.removeItem('export.companyName');
    sessionStorage.removeItem('export.division');
    sessionStorage.removeItem('export.sector');
    sessionStorage.removeItem('export.industry');
    sessionStorage.removeItem('export.filerCategory');
    sessionStorage.removeItem('export.selectedEntities');
  }

  resetCompanies() {
    this.companyName = undefined;
    this.division = undefined;
    this.sector = undefined;
    this.industry = undefined;
    this.filerCategory = undefined;
    this.sics = [];
    this.entitiesList = [];
    this.selectedEntities = [];
    this.errorMsg = '';
    this.showError = false;

    this.clearStorage();
  }
  searchCompany(event) {
    this.filteredCompany = [];
    for (let i = 0; i < this.companyList.length; i++) {
      let cname: string = this.companyList[i];
      if ((cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) || (cname != null && cname.toLowerCase().includes(event.query.toLowerCase()))) {
        this.filteredCompany.push(cname);
      }
    }
  }

  onCompanyNameChange($event) {
    console.log(this.companyName);
    sessionStorage.setItem('export.companyName', $event);
  }

  searchIndustry(event) {
    this.filteredindustry = [];
    for (let i = 0; i < this.industryList.length; i++) {
      let cname: string = this.industryList[i];
      if (cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.filteredindustry.push(cname);
      }
    }
  }

  searchDivisionSector(event) {
    this.filtereddivision = [];
    for (let i = 0; i < this.divisionList.length; i++) {
      let cname: string = this.divisionList[i];
      if (cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.filtereddivision.push(cname);
      }
    }
  }

  searchFilerCategory(event) {
    this.filteredfilerCategory = [];
    for (let i = 0; i < this.filerCategoryList.length; i++) {
      let cname: string = this.filerCategoryList[i];
      if (cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.filteredfilerCategory.push(cname);
      }
    }
  }

  goToPeer(event) {
    this.errorMsg = '';
    this.showError = false;
    this.company = null;
    if (this.companyName != undefined && this.companyName.length > 15) {
      this.company = this.companyName.substr(0, this.companyName.length - 12);
      var peer = this.completeCompanyList.filter(obj => obj.companyName === this.company);
      this.division = peer[0].division;
      this.sector = peer[0].sector;
      this.industry = peer[0].sic + '-' + peer[0].industry;
      this.filerCategory = peer[0].filerCategory;
      this.sics = this.completeCompanyList.filter(obj => obj.companyName === this.company);
      this.divisionChange(this.division, true);
      this.filteredindustry = [];
      var name = { label: this.industry, value: this.industry };
      this.filteredindustry.push(name);
      this.sectorChange(this.sector, true);
      this.industryChange(this.industry, true);
      this.filerChange(this.filerCategory, true)

    } else {
      this.errorMsg = 'Please Select Company';
      this.showError = true;

    }

  }
  divisionChange(event, peer) {
    this.completeEntities = this.completeCompanyList;
    event = event.value ? event.value : event;
    var division = event;
    if (!peer) {
      this.sector = undefined;
      this.industry = undefined;
      this.filerCategory = undefined;
      this.sics = [];
      this.entitiesList = [];
      sessionStorage.removeItem('export.sector');
      sessionStorage.removeItem('export.industry');
      sessionStorage.removeItem('export.filerCategory');
    }
    // var sector = event.substr(event.indexOf( '->')+3, event.length);
    if (division) {
      this.completeEntities = this.completeCompanyList.filter(obj => obj.division === division);
    }
    this.filteredsector = [];
    this.completeEntities.forEach(obj => {
      var name = { label: obj.sector, value: obj.sector };
      if (obj != null) {
        this.filteredsector.push(name)
      }
    })
    this.filteredsector = this.filteredsector.filter((el, i, a) =>
      i === a.findIndex((obj) =>
        obj.label === el.label
      ))
      this.filteredsector.sort((a, b) => a.label > b.label ? 1 : -1)

    sessionStorage.setItem('export.division', event);
  }

  sectorChange(event, peer) {
    this.completeEntities = this.completeCompanyList;
    var sicCodes: any[] = [];
    this.entitiesList = [];
    if (!peer)
      this.sics = [];
    event = event.value ? event.value : event;
    var sector = event;
    if (sector) {
      this.completeEntities = this.completeEntities.filter(obj => obj.sector === sector);
    }
    this.completeEntities.forEach(obj => {
      sicCodes.push(obj.sic + '-' + obj.industry);
      var ind = { industry: obj.industry }
      if (!peer)
        this.sics.push(ind);
    })
    sicCodes = sicCodes.filter((el, i, a) => i === a.indexOf(el))
    if (!peer) {
      this.filteredindustry = [];
      this.industry = undefined;
    }
    sicCodes.forEach(obj => {
      var name = { label: obj, value: obj };
      this.filteredindustry.push(name);
    })

    this.sics = this.sics.filter((el, i, a) =>
      i === a.findIndex((obj) =>
        obj.industry === el.industry
      ))
    this.sics.forEach(sic => {
      var completeList = this.completeCompanyList;
      var list = completeList.filter(obj => obj.industry === sic.industry);
      if (this.filerCategory)
        list = list.filter(obj => obj.filerCategory === this.filerCategory);
        list.sort((a, b) => a.companyName > b.companyName ? 1 : -1)
      var entity = { industry: sic.industry, entities: list, check: false };
      this.entitiesList.push(entity);
    })
    this.filteredindustry.sort((a, b) => a.label > b.label ? 1 : -1)
    this.sics.sort((a, b) => a.industry > b.industry ? 1 : -1)
    // this.entitiesList.sort((a, b) => a.label > b.label ? 1 : -1)

    sessionStorage.setItem('export.sector', event);
  }

  industryChange(event, peer) {
    this.entitiesList = [];
    event = event.value ? event.value : event;
    var industry = event.substr(event.indexOf('-') + 1, event.length);
    var entities = this.completeEntities;
    if (industry) {
      entities.filter(obj => obj.industry === industry);
      this.sics = [];
      this.sics.push({ industry: industry });
      this.sics.forEach(sic => {
        var list = entities.filter(obj => obj.industry === sic.industry);
        if (this.filerCategory)
          list = list.filter(obj => obj.filerCategory === this.filerCategory);
          list.sort((a, b) => a.companyName > b.companyName ? 1 : -1)
        var entity = { industry: sic.industry, entities: list, check: false };
        this.entitiesList.push(entity);
      })
    }

    sessionStorage.setItem('export.industry', event);
  }

  filerChange(event, peer) {
    event = event.value ? event.value : event;
    var filer = event;
    var entities = this.completeEntities;
    this.entitiesList = [];
    entities.filter(obj => obj.filerCategory === filer);
    this.sics.forEach(sic => {
      var list = entities.filter(obj => obj.industry === sic.industry);
      if (this.filerCategory)
        list = list.filter(obj => obj.filerCategory === this.filerCategory);
        list.sort((a, b) => a.companyName > b.companyName ? 1 : -1)
      var entity = { industry: sic.industry, entities: list, check: false };
      this.entitiesList.push(entity);
    })

    sessionStorage.setItem('export.filerCategory', event);
  }
  selectEntitiesWithSicCode(type: any, list, checked) {
    console.log(type + list);
    if (checked) {
      list.forEach(obj => {
        if (!this.selectedEntities.some((item) => item == obj))
          this.selectedEntities.push(obj);
        this.entitiesList.forEach(eObj => {
          eObj.entities.forEach(ent => {
            if (ent == obj) {
              eObj.check = true;
              ent.check = true;
            }
          })
        })
      })
    } else {
      for (var i = 0; i < list.length; i++) {
        var ind;
        this.selectedEntities.forEach(element => {
          if(element.cik == list[i].cik) {
            ind = this.selectedEntities.indexOf(element);
          }
        });
        if (ind !== -1)
          this.selectedEntities.splice(ind, 1);
        this.entitiesList.forEach(eObj => {
          eObj.entities.forEach(ent => {
            if (ent == list[i])
              ent.check = false;
          })
        })
      }
    }

    sessionStorage.setItem('export.selectedEntities', JSON.stringify(this.selectedEntities));
    sessionStorage.setItem('export.entitiesList', JSON.stringify(this.entitiesList));
  }

  show(industry) {
    console.log(industry);
  }

  isSelected(selected, index) {
    if (!this.selectedEntities.some((item) => item.cik == selected.cik)) { //check
      this.selectedEntities.push(selected);
    } else { //uncheck
      //remove item from existing selected list
      var ind;
      this.selectedEntities.forEach(element => {
        if(element.cik == selected.cik) {
          ind = this.selectedEntities.indexOf(element);
        }
      });
      this.selectedEntities.splice(ind, 1);
      //update check status on original entity list
      this.entitiesList.forEach(eObj => {
        eObj.entities.forEach(ent => {
          if (ent.cik == selected.cik)
            ent.check = false;
        })
      })
    }
    sessionStorage.setItem('export.selectedEntities', JSON.stringify(this.selectedEntities));
  };
  deselectAllTerms() {
    this.selectedTerms = [];
    this.terms.forEach(obj => {
      obj.check = false;
    })
    sessionStorage.removeItem('export.selectedTerms');
    sessionStorage.setItem('export.terms', JSON.stringify(this.terms));
  }

  selectAllTerms() {
    this.terms.forEach(obj => {
      obj.check = true;
    })
    this.terms.forEach(obj => {
      if (!this.selectedTerms.some((item) => item.termId == obj.termId)) {
        this.selectedTerms.push(obj);
      }
    })
    sessionStorage.setItem('export.selectedTerms', JSON.stringify(this.selectedTerms));
    sessionStorage.setItem('export.terms', JSON.stringify(this.terms));
  }

  isTermSelected(selected, index) {
    if (!this.selectedTerms.some((item) => item.name == selected.name)) {
      this.selectedTerms.push(selected);
    } else {
      this.terms.forEach(obj => {
        if (obj.name === selected.name) {
          obj.check = false;
        }
      })
      var ind;
      this.selectedTerms.forEach(element => {
        if(element.name === selected.name) {
          ind = this.selectedTerms.indexOf(element);
        }
      });
      this.selectedTerms.splice(ind, 1);
    }
    sessionStorage.setItem('export.selectedTerms', JSON.stringify(this.selectedTerms));
    sessionStorage.setItem('export.terms', JSON.stringify(this.terms));
  };

  removeSelectedTerms() {
    this.selectedTerms = [];
    this.terms.forEach(obj => {
      obj.check = false;
    })
    sessionStorage.removeItem('export.selectedTerms');
  }
  removeSelectedEntities() {
    this.selectedEntities = [];
    this.entitiesList.forEach(eObj => {
      eObj.check = false;
      eObj.entities.forEach(ent => {
        ent.check = false;
      })
    })
    this.sics.forEach(eObj => {
      eObj.check = false;
    })
    sessionStorage.removeItem('export.selectedEntities');
  }
  removeEntity() {
    this.selectedEntities
  }

  filterTerm(event) {
    if (event.query != null && event.query != '') {
      this.filteredTerms = [];
      for (let i = 0; i < this.actualTerms.length; i++) {
        var cname = this.actualTerms[i];
        if ((cname != null && cname.termId.toLowerCase().indexOf(event.query.toLowerCase()) == 0) || (cname != null && cname.name.toLowerCase().includes(event.query.toLowerCase()))) {
          this.filteredTerms.push(cname);
        }
      }
      this.terms = this.filteredTerms;
    } else {
      this.terms = this.actualTerms;
    }
    sessionStorage.setItem('export.terms', JSON.stringify(this.terms));
    sessionStorage.setItem('export.filteredTerms', JSON.stringify(this.filteredTerms));
  }

  onTermChange($event){
    console.log(this.termInput);
    sessionStorage.setItem('export.termInput', JSON.stringify($event));
  }

  //from export tab
  onAnnualChange($event) {
    sessionStorage.setItem('export.annual', JSON.stringify(this.annual));
  }
  onAllCompanyChange($event) {
    sessionStorage.setItem('export.selectedOver', JSON.stringify(this.selectedOver));
  }
  onValidationChange($event) {
    sessionStorage.setItem('export.selectedVal', JSON.stringify(this.selectedVal));
  }

  termChange(event) {
    if (event.checked) {
      this.termRes = true;
      this.covStat = false;
    } else {
      this.termRes = false;
      this.covStat = true;
    }
    sessionStorage.setItem('export.termRes', JSON.stringify(this.termRes));
    sessionStorage.setItem('export.covStat', JSON.stringify(this.covStat));
  }

  covChange(event) {
    if (event.checked) {
      this.covStat = true;
      this.termRes = false
    } else {
      this.termRes = true;
      this.covStat = false;
    }
    sessionStorage.setItem('export.termRes', JSON.stringify(this.termRes));
    sessionStorage.setItem('export.covStat', JSON.stringify(this.covStat));
  }
  quaterlyChange(event) {
    this.exportData = [];
    if (!event.checked) {
      //this.exportData = this.actualExportData;
      if (this.actualExportData) {
        this.actualExportData.forEach(obj => {
          if (obj.reportingPeriod.includes('FY')) {
            this.exportData.push(obj);
          }
        })
      }
    }
    else {
      this.exportData = this.actualExportData;
    }
    if (this.exportData) {
      this.first = this.exportData.length > 10 ? 10 : this.exportData.length;
      this.row = (this.exportData.length - this.first);
      this.total = this.exportData.length;
      sessionStorage.setItem('export.exportData', JSON.stringify(this.exportData));
    } else {
      sessionStorage.removeItem('export.exportData');
    }
    sessionStorage.setItem('export.first', JSON.stringify(this.first));
    sessionStorage.setItem('export.row', JSON.stringify(this.row));
    sessionStorage.setItem('export.total', JSON.stringify(this.total));
    sessionStorage.setItem('export.quatCheck', JSON.stringify(this.quatCheck));
  }

  refreshData() {
    this.termResultFilterOptions.isForAllEntities = this.selectedOver;
    this.termResultFilterOptions.includeFiscalYears = this.annual;
    this.termResultFilterOptions.includeFiscalQuarters = this.quarterly;
    this.termResultFilterOptions.includeValidationInfos = this.selectedVal;
    this.num = 1;
    this.first = 10;
    this.total = 0;
    this.covNum = 1;
    this.covFirst = 10;
    this.covTotal = 0;
    this.progressSpinner = true;
    this.showError = false;
    this.errorMsg = '';
    //this.quatCheck = true;
    this.exportData = [];
    this.coverageStats = [];

    if (this.termRes) {
      this.refreshExportTermResultsData();

    } else {
      this.refreshExportCoverageData();
    }
  }

  refreshExportTermResultsData() {
    var entityIds: any[] = [];
    this.exportDataCols = [];
    this.exportData = [];
    this.selectedEntities.forEach(obj => {
      entityIds.push(obj.entityId);
    })
    this.termResultFilterOptions.entityList = entityIds;
    if (this.termResultFilterOptions.entityList.length === 0) {
      // if (this.termResultFilterOptions.isForAllEntities !== true) {
      //   this.progressSpinner = false;
      //     this.showError = true;
      //     this.errorMsg = "Error Atleast one Company needs to be specified for processing or all companies override needs to be checked.";
      //     return;
      // }
      this.termResultFilterOptions.isForAllEntities = true;
      this.selectedOver = true;
    }
    var termIds: any = [];
    this.selectedTerms.forEach(obj => {
      termIds.push(obj.termId);
    })
    if (termIds.length === 0) {
      this.showError = true;
      this.progressSpinner = false;
      this.errorMsg = "Atleast one Term needs to be checked.";
      return;
    }
    this.termResultFilterOptions.termIdList = termIds;
    var cols = [
      { field: 'company', header: 'Company' },
      { field: 'reportingPeriod', header: 'Reporting Period' },
      { field: 'periodEndDate', header: 'Period End Date' },
    ];
    cols.forEach(obj => {
      this.exportDataCols.push(obj)
    })

    this.exportService.getResultsForExportTermResults(this.termResultFilterOptions).subscribe(
      (response) => {
        this.exportData = response;
        if (this.termResultFilterOptions.includeValidationInfos === true) {

          this.exportData.termNameList.forEach(name => {
            var col = [{ field: name, header: name },
            { field: 'resolvedExpressions', header: name + ' Resolved Expression' },
            { field: 'validationStatuses', header: name + ' Validation Status' },
            { field: 'validationMessages', header: name + ' Validation Message' }];
            col.forEach(obj => {
              this.exportDataCols.push(obj)
            })
            this.progressSpinner = false;
            //this.exportDataCols.push(col);
          });

        } else {
          this.exportData.termNameList.forEach(name => {

            var col = [{ field: 'termResultValues', header: name },
            { field: 'resolvedExpressions', header: name + ' Resolved Expression' }]
            col.forEach(obj => {
              this.exportDataCols.push(obj)
            })
            this.progressSpinner = false;
            // this.exportDataCols.push (col);
          });
        }
        for (var i = 0; i < this.exportData.exportItemList.length; i++) {
          var res = this.exportData.exportItemList[i];
          var termValues = res.termResultValues;
          var updatedTermValues = [];
          if (this.termResultFilterOptions.includeValidationInfos === true) {

            for (var x = 0; x < termValues.length; x++) {
              var expo = Number.parseFloat(termValues[x]).toFixed(0)
              updatedTermValues.push(expo);
              updatedTermValues.push(res.resolvedExpressions[x]);
              updatedTermValues.push(res.validationStatuses[x]);
              updatedTermValues.push(res.validationMessages[x]);

            }

          } else {
            for (var x = 0; x < termValues.length; x++) {
              var expo = Number.parseFloat(termValues[x]).toFixed(0)
              updatedTermValues.push(expo);
              updatedTermValues.push(res.resolvedExpressions[x]);

            }
          }

          res.termResultValues = updatedTermValues;
        }
        this.actualExportData = this.exportData.exportItemList;
        this.exportData = this.exportData.exportItemList;
        // this.actualExportData = this.exportData;
        if (!this.quatCheck) {
          this.exportData = [];
          this.actualExportData.forEach(obj => {
            if (obj.reportingPeriod.includes('FY')) {
              this.exportData.push(obj);
            }
          })
        }

        this.first = this.exportData.length > 10 ? 10 : this.exportData.length;
        this.row = (this.exportData.length - this.first);
        this.total = this.exportData.length;
      })


  };
  pagenation(value) {
    console.log(value);
    let initialCount = value.rows;
    value.first = (value.first + initialCount);
    this.num = value.first - value.rows;
    this.num = this.num = 0 ? 1 : this.num;
    this.first = value.first;
    this.first = this.first > this.exportData.length ? this.exportData.length : this.first;
    this.row = (this.exportData.length - value.first);
    this.total = this.exportData.length;
  }

  covPagenation(value) {
    console.log(value);
    let initialCount = value.rows;
    value.first = (value.first + initialCount);
    this.covNum = value.first - value.rows;
    this.covNum = this.covNum = 0 ? 1 : this.covNum;
    this.covFirst = value.first;
    this.covFirst = this.covFirst > this.coverageStats.length ? this.coverageStats.length : this.covFirst;
    this.covRow = (this.coverageStats.length - value.first);
    this.covTotal = this.exportData.length;
  }


  yearChange(event) {
    if (event.value === null) {
      this.termResultFilterOptions.startYear = (new Date()).getFullYear() - 4;
      this.termResultFilterOptions.endYear = (new Date()).getFullYear();
    } else {
      this.rangeValues = [event.value, event.value];
      // this.rangeValues = [(new Date()).getFullYear()-4, (new Date()).getFullYear()];
      this.termResultFilterOptions.startYear = event.value;
      this.termResultFilterOptions.endYear = event.value;
      //this.selectedOver = true;
    }
    sessionStorage.setItem('export.termResultFilterOptions.startYear', JSON.stringify(this.termResultFilterOptions.startYear));
    sessionStorage.setItem('export.termResultFilterOptions.endYear', JSON.stringify(this.termResultFilterOptions.endYear));
    sessionStorage.setItem('export.selectedOver', JSON.stringify(this.selectedOver));
    sessionStorage.setItem('export.rangeValues', JSON.stringify(this.rangeValues));
    sessionStorage.setItem('export.year', JSON.stringify(this.year));
  }

  yearSlideChange(event) {
    this.year = '';
    this.selectedOver = false;
    this.termResultFilterOptions.startYear = this.rangeValues[0];
    this.termResultFilterOptions.endYear = this.rangeValues[1];

    sessionStorage.setItem('export.termResultFilterOptions.startYear', JSON.stringify(this.termResultFilterOptions.startYear));
    sessionStorage.setItem('export.termResultFilterOptions.endYear', JSON.stringify(this.termResultFilterOptions.endYear));
    sessionStorage.setItem('export.selectedOver', JSON.stringify(this.selectedOver));
    sessionStorage.setItem('export.rangeValues', JSON.stringify(this.rangeValues));
    sessionStorage.setItem('export.year', JSON.stringify(this.year));
  }

  refreshExportCoverageData() {
    this.coverageStats = [];
    this.coverageDataCols = [];
    var entityIds: any[] = [];
    this.selectedEntities.forEach(obj => {
      entityIds.push(obj.entityId);
    })
    this.termResultFilterOptions.entityList = entityIds;

    if (this.termResultFilterOptions.entityList.length === 0) {
      // if (this.termResultFilterOptions.isForAllEntities !== true)  {
      //   this.showError = true;
      //   this.progressSpinner = false;
      //     this.errorMsg = "Atleast one Company needs to be specified for processing or all companies override needs to be checked.";
      //     return;
      // }
      this.termResultFilterOptions.isForAllEntities = true;
      this.selectedOver = true
    }

    var termIds: any = [];
    this.selectedTerms.forEach(obj => {
      termIds.push(obj.termId);
    })
    if (termIds.length === 0) {
      this.showError = true;
      this.progressSpinner = false;
      this.errorMsg = "Atleast one Term needs to be checked.";
      return;
    }
    this.termResultFilterOptions.termIdList = termIds;

    this.exportService.getResultsForExportCoverage(this.termResultFilterOptions).subscribe(
      (response) => {
        this.coverageStats = response.exportItemList;
        this.covFirst = this.coverageStats.length > 10 ? 10 : this.coverageStats.length;
        this.covRow = (this.coverageStats.length - this.covFirst);
        this.covTotal = this.coverageStats.length;
        this.progressSpinner = false;
        response.columnList.forEach(obj => {
          this.coverageDataCols.push({ header: obj })
        })
      }, function errorfunction(err) {
        this.progressSpinner = false;
        console.log("Export error: " + JSON.stringify(err));
      });

  };
  downloadTermResData(fileName) {
  // downloadTermResData(table) {
    fileName = fileName.includes('.csv') ? fileName : fileName + '.csv'
    var headers: any[] = [];
    headers.push('CIK')
    this.exportDataCols.forEach(obj => {
      headers.push(obj.header);
    })
    var data: any[] = [];
    this.exportData.forEach(obj => {
      var dat = [];
      dat.push(obj.cik);
      dat.push("\"" + obj.company + "\"")
      dat.push(obj.reportingPeriod)
      dat.push(obj.periodEndDate)
      obj.termResultValues.forEach(val => {
        val = "\"" + val + "\""
        dat.push(val);
      })
      data.push(dat);
    })

    this.downloadFile(data, fileName, headers);

    // table.value.forEach(item => {
    //   item.termResultValues = item.termResultValues[0];
    // });
    // table.exportCSV();

  }

  downloadCovData(fileName) {
    fileName = fileName.includes('.csv') ? fileName : fileName + '.csv'
    var headers: any[] = [];
    this.coverageDataCols.forEach(obj => {
      headers.push(obj.header);
    })
    var data: any[] = [];
    this.coverageStats.forEach(obj => {
      var dat = [];
      dat.push("\"" + obj.termName + "\"")
      dat.push(obj.termId)
      obj.coverageValues.forEach(val => {
        val = "\"" + val + "\""
        dat.push(val);
      })
      data.push(dat);
    })

    this.downloadFile(data, fileName, headers);

  }


  downloadFile(data, filename, header: any[]) {
    let csvData = this.ConvertToCSV(data, header);
    console.log(csvData)
    let blob = new Blob(['\ufeff' + csvData], { type: 'text/csv;charset=utf-8;' });
    let dwldLink = document.createElement("a");
    let url = URL.createObjectURL(blob);
    let isSafariBrowser = navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1;
    if (isSafariBrowser) {  //if Safari open in new window to save file with random filename.
      dwldLink.setAttribute("target", "_blank");
    }
    dwldLink.setAttribute("href", url);
    dwldLink.setAttribute("download", filename);
    dwldLink.style.visibility = "hidden";
    document.body.appendChild(dwldLink);
    dwldLink.click();
    document.body.removeChild(dwldLink);
  }

  ConvertToCSV(objArray, headerList) {
    let array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
    let str = '';
    let row = '';

    for (let index in headerList) {
      row += headerList[index] + ',';
    }
    row = row.slice(0, -1);
    str += row + '\r\n';
    for (let i = 0; i < array.length; i++) {
      let line = '';
      line = array[i];
      str += line + '\r\n';
    }
    return str;
  }



  ngOnDestroy() {
    this.subscription.forEach(s => s.unsubscribe());
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

}
