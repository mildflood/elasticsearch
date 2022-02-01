import { Component, ElementRef } from '@angular/core';
import { OnInit, ViewChild } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ISubscription } from 'rxjs/Subscription';
import { NavigationEnd, Router } from '@angular/router';
import { ProcessingTermsService } from './processingTermsService';
import { UtilService } from '../utils/utilService';
import { Messages, Message, Dropdown } from 'primeng/primeng';
import { ConfirmationService } from 'primeng/api';
import { StatusService } from '../status/statusService';
import { Status } from '../domain/status';
import { MappedEntity } from 'app/domain/mappedEntity';
import { ProfileService } from 'app/services/profile.service';
import { DialogModalService } from 'app/_dialogmodal';

@Component({
  selector: 'ProcessingTerms-page',
  templateUrl: './processingTerms.html',
  styleUrls: [`../app.component.css`],
})
export class ProcessingTermsComponent implements OnInit, OnDestroy {
  private subscription: ISubscription[] = [];
  navigationSubscription;
  companyList: any[];
  filteredCompany: any[];
  companyName: string;
  industryList: any[];
  filteredindustry: any[];
  industry: string;
  divSectorList: any[];
  filtereddivSector: any[];
  divSector: string;
  division: string;
  sector: string;
  filerCategoryList: any[];
  filteredfilerCategory: any[];
  filerCategory: string = null;
  errorMsg: string = '';
  showError: boolean;
  company: string;
  completeEntities: any[] = [];
  completeCompanyList: any[];
  divisionCount: number;
  industryCount: number;
  categoryCount: number;
  filterRadio: string;
  processRadio: string;
  checkAccuracyTest: boolean;
  allCompanies: string;
  msgs: Message[] = [];
  progressSpinner: boolean;
  terms: any = [];
  filteredTerms: any = [];
  selectedTerms: any = [];
  resetProcessingPopup: boolean;

  statusNumber: number = 0;
  statusCols: any;
  cols: any[];

  statusNames: any[];
  interval: any;

  first: any = 0;
  total: any = 0;
  row: any = 10;

  selectedTermsCompanyNameRadio: any;
  selectedTermsCompanyName;
  selectedTermsCompanies = [];
  selectedTermsfilteredCompany = [];
  selectedTermsDivisionRadio;
  selectedTermDivSector;
  selectedTermIndustry;;
  selectedTermFilerCategory;
  selectedTermDivisionCount;
  selectedTermSectorCount;
  selectedTermCategoryCount;
  selectedTermDivision;
  selectedTermSector;
  selectedEntity;

  isAdmin: boolean;

  homeEntityCol = [
    { field: 'cik', header: 'Cik' },
    { field: 'companyName', header: 'Company Name' }
  ];
  entityList = [];

  ciksFromDialog: string;
  gotoPeerEntities = [];
  termAutoCompleteDisable: boolean = false;

  @ViewChild('inputFile') myInputVariable: ElementRef;
  @ViewChild('accuracyTestCheckbox') accuracyTestCheckbox: ElementRef;

  constructor(private processingTermsService: ProcessingTermsService, private router: Router, private utilService: UtilService, private confirmationService: ConfirmationService,
    private statusService: StatusService, private status: Status, private profileService: ProfileService, private dialogModalService: DialogModalService) {
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
      }
    });
  }

  ngOnInit() {
    console.log('ProcessingTermsComponent : calling ngOnInit...');
    this.utilService.getUserRolesList().subscribe((response) => {
        this.isAdmin = response.includes('admin');
      });
    this.utilService.getCompanyList().subscribe((response) => { this.companyList = response; });
    this.utilService.getDivSectorList().subscribe((response) => {
      this.divSectorList = response;
      this.filtereddivSector = [];
      response.forEach(obj => {
        var name = { label: obj, value: obj };
        this.filtereddivSector.push(name)
      });
    })
    this.utilService.getIndustryList().subscribe((response) => { this.industryList = response; })
    this.utilService.getFilerCategoryList().subscribe((response) => {
      this.filerCategoryList = response;
      this.filteredfilerCategory = [];
      response.forEach(obj => {
        var name = { label: obj, value: obj };
        this.filteredfilerCategory.push(name)
      });
    })
    this.utilService.getCompleteCompanyList().subscribe((response) => { this.completeCompanyList = response });
    this.filterRadio = 'company';
    this.processRadio = 'termRadio';
    this.statusCols = this.status.statusCols();

    this.interval = setInterval(() => {
      this.loadProcessStatus();
    }, 10000);
    this.loadProcessStatus();

    this.utilService.getTermRuleList().subscribe((response) => {
      this.terms = response;
      // this.actualTerms = response
      this.terms = this.terms.sort((a, b) => {
        if (a.termId < b.termId) { return -1; }
        if (a.termId > b.termId) { return 1; }
        return 0;
      });
    });
  }

  ngAfterContentInit() {
    this.loadFromStorage();
  }

  loadFromStorage() {

      if (sessionStorage.getItem('processTerm.companyName') !== null && sessionStorage.getItem('processTerm.companyName') !== "undefined") {
        this.companyName = JSON.parse(sessionStorage.getItem('processTerm.companyName'));
      }
      if (sessionStorage.getItem('processTerm.selectedTermsCompanies') !== null && sessionStorage.getItem('processTerm.selectedTermsCompanies') !== "undefined") {
        this.selectedTermsCompanies = JSON.parse(sessionStorage.getItem('processTerm.selectedTermsCompanies'));
      }
      if (sessionStorage.getItem('processTerm.selectedTerms') !== null && sessionStorage.getItem('processTerm.selectedTerms') !== "undefined") {
        this.selectedTerms = JSON.parse(sessionStorage.getItem('processTerm.selectedTerms'));
      }
  }

  getUserRole() {
    this.utilService.getUserRolesList().subscribe(
      (response) => {
        this.isAdmin = response.includes('admin');
      },
      (error) => console.log(error)
    )
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


  pagenation(value) {
    console.log(value);
    let initialCount = value.rows;
    value.first = (value.first + initialCount);
    this.first = value.first;
    this.row = (this.statusNames.length - value.first);
    this.total = this.statusNames.length;
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

  selectedTermsSearchCompany(event) {
    this.selectedTermsfilteredCompany = [];
    for (let i = 0; i < this.companyList.length; i++) {
      let cname: string = this.companyList[i];
      if (cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.selectedTermsfilteredCompany.push(cname);
      }
    }
  }

  addOrRemoveEntities() {
    let name = ""
    this.selectedTermsCompanyName = "";
    this.selectedTermsCompanies.forEach(entity => {
      if (name != null || name != "") {
        name = name + "," + entity

      } else {
        name = entity;
      }
    })
    this.selectedTermsCompanyName = name.substr(1, name.length);
    this.processRadio = 'companyRadio';

    sessionStorage.setItem('processTerm.selectedTermsCompanies', JSON.stringify(this.selectedTermsCompanies));

  }

  selectedTermDivSectorChange(event) {
    this.completeEntities = this.completeCompanyList;
    var sicCodes: any[] = [];
    var division = event.substr(0, event.indexOf('->') - 1);
    var sector = event.substr(event.indexOf('->') + 3, event.length);
    this.selectedTermFilerCategory = undefined;
    this.selectedTermIndustry = undefined;
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

    this.selectedTermDivisionCount = this.completeEntities.length;
    this.selectedTermSectorCount = this.completeEntities.length;
    this.selectedTermCategoryCount = this.completeEntities.length;
  }

  selectedTermProcessIndustryChange(event) {
    if (event != null) {
      this.selectedTermFilerCategory = undefined;
      event = event.substring(0, 4);
      var completeEntities = this.completeEntities.filter(obj => obj.sic === event);
      this.selectedTermSectorCount = completeEntities.length;
      this.selectedTermCategoryCount = completeEntities.length;
    } else {
      this.selectedTermSectorCount = this.divisionCount;
      this.selectedTermCategoryCount = this.divisionCount;
    }

  }

  selectedTermProcessfilCatChange(event) {
    if (event != null) {
      var completeEntities = this.completeEntities.filter(obj =>
        obj.filerCategory === event && obj.sic === this.selectedTermIndustry.substr(0, 4));
      this.selectedTermCategoryCount = completeEntities.length;
      this.gotoPeerEntities = completeEntities;
    } else {
      this.selectedTermCategoryCount = this.divisionCount;
    }
  }

  selectedTermGoToPeer(event?) {

    if (this.selectedTermsCompanies != undefined) {
      this.company = this.selectedTermsCompanies[0].split('(')[0];
      var peer = this.completeCompanyList.filter(obj => obj.companyName === this.company);
      this.selectedTermDivision = peer[0].division;
      this.selectedTermSector = peer[0].sector;
      this.selectedTermDivSector = peer[0].division + ' -> ' + peer[0].sector;
      this.selectedTermDivSectorChange(this.selectedTermDivSector)
      this.selectedTermIndustry = peer[0].sic + '-' + peer[0].industry;
      this.selectedTermProcessIndustryChange(this.selectedTermIndustry);
      this.selectedTermFilerCategory = peer[0].filerCategory;
      this.selectedTermProcessfilCatChange(this.selectedTermFilerCategory);
      this.processRadio = 'divisionRadio';
    }
  }

  selectedTermShowResolved() {
    let mp = new MappedEntity();
    this.entityList = [];
    this.selectedEntity = undefined;
    this.selectedTerms.forEach(item => {
      mp.termRuleId = item;
      mp.entityId = "NULL";
      if (this.processRadio === "companyRadio") {
        this.selectedTermsCompanies.forEach (cmy => {
        var selectionData = cmy; //this.selectedTermsCompanyName;
        if (selectionData != undefined && selectionData.length > 15) {
          selectionData = selectionData.substring((selectionData.indexOf("(") + 1), selectionData.indexOf(")"));
          var entities = this.completeCompanyList.filter(obj => obj.entityId === selectionData);
          var company = entities.length == 1 ? entities[0] : null;
          if (!company) {
            this.errorMsg = 'Please select a company, division->Sector or Filer Category before running process.';
            this.showError = true;
            window.scroll(0, 0);
          } else {
            mp.entityId = company.entityId;
          }
        }
        mp.division = this.selectedTermDivision;
        mp.sector = this.selectedTermSector;
        mp.filerCategory = this.selectedTermFilerCategory;
        mp.rankId = 0;
        mp.minYear = (new Date()).getFullYear() - 4;
        mp.maxYear = (new Date()).getFullYear();
        mp.includeQuarterly = true;
        if (this.selectedTermIndustry) {
            mp.sic = this.selectedTermIndustry.substring(0, 4);
        }
        this.profileService.showReslovedData(mp).subscribe(results => {
          this.entityList = this.entityList.concat(results.filter( ({id}) => !this.entityList.find(f => f.id == id))); //merge two array without duplicates
          //this.entityList = this.entityList.concat(results);
        })
      })
      } else { //non companyRadio
        mp.division = this.selectedTermDivision;
        mp.sector = this.selectedTermSector;
        mp.filerCategory = this.selectedTermFilerCategory;
        mp.rankId = 0;
        mp.minYear = (new Date()).getFullYear() - 4;
        mp.maxYear = (new Date()).getFullYear();
        mp.includeQuarterly = true;
        if (this.selectedTermIndustry) {
            mp.sic = this.selectedTermIndustry.substring(0, 4);
        }
        this.profileService.showReslovedData(mp).subscribe(results => {
          this.entityList = this.entityList.concat(results.filter( ({id}) => !this.entityList.find(f => f.id == id))); //merge two array without duplicates
          //this.entityList = this.entityList.concat(results);
        })
      }
    });
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
    this.filtereddivSector = [];
    for (let i = 0; i < this.divSectorList.length; i++) {
      let cname: string = this.divSectorList[i];
      if (cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.filtereddivSector.push(cname);
      }
    }
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

  enableAccuracyTest(event){
    if (event.target.checked) {
      this.checkAccuracyTest = true;
      if (this.processRadio === 'termRadio') {
        this.processRadio = 'companyRadio';
      }
      this.termAutoCompleteDisable = true; //diable term selection
      this.entityList = [];//remove the cik list display
    } else {
      this.checkAccuracyTest = false;
      this.termAutoCompleteDisable = false;
    }
  }

  termRadioChecked(event){
    this.checkAccuracyTest = false;
    this.accuracyTestCheckbox.nativeElement.checked = false;
    this.termAutoCompleteDisable = false;
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
      this.divSector = peer[0].division + ' -> ' + peer[0].sector;
      this.divSectorChange(this.divSector)
      this.industry = peer[0].sic + '-' + peer[0].industry;;
      this.processIndustryChange(this.industry);
      this.filerCategory = peer[0].filerCategory;
      this.processfilCatChange(this.filerCategory);
      this.filterRadio = 'group';
    } else {
      window.scroll(0, 0);
      this.errorMsg = 'Please Select Company';
      this.showError = true;

    }

  }
  divSectorChange(event) {
    this.completeEntities = this.completeCompanyList;
    var sicCodes: any[] = [];
    event = event.value ? event.value : event;
    var division = event.substr(0, event.indexOf('->') - 1);
    var sector = event.substr(event.indexOf('->') + 3, event.length);
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
    this.industryCount = this.completeEntities.length;
    this.categoryCount = this.completeEntities.length;;
  }

  processIndustryChange(event) {
    event = event.value ? event.value : event;
    event = event.substring(0, 4);
    var completeEntities = this.completeEntities.filter(obj => obj.sic === event);
    this.industryCount = completeEntities.length;
    this.industryCount = completeEntities.length;
  }

  processfilCatChange(event) {
    event = event.value ? event.value : event;
    var completeEntities = this.completeEntities.filter(obj =>
      obj.filerCategory === event && obj.sic === this.industry.substr(0, 4));
    this.industryCount = completeEntities.length
    this.categoryCount = completeEntities.length;
  }

  ProcessAllRulesWithCriteria() {
    this.showError = false;
    this.confirmationService.confirm({
      message: 'Are you sure you want to process all terms based on selection?',
      header: 'Process Term',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        var inputData = this.getInputdata();
        if (!inputData.isValid) {
          this.showError = true;
          window.scroll(0, 0);
          this.errorMsg = 'Please select a company,  division->Sector or Filer Category before processing all terms.';
          return;
        }
        inputData.isnewAction = true;
        this.progressSpinner = true;
        this.msgs = [{ severity: 'info', summary: 'Processing Term Rule', detail: '' }];
        this.utilService.processAllTermWithCriteria(inputData.division, inputData.sector, inputData.sic, inputData.filerCategory,
          inputData.entityId, inputData.isnewAction).subscribe((response) => {
            if (response) {
              var res = response._body;
              if (res.includes('"status":true')) {
                this.msgs = [{ severity: 'info', summary: 'Processed Term Rule' }];
                this.progressSpinner = false
              } if (res.includes('"status":false')) {
                // this.msgs = [{severity:'info', summary:'Processed Term Rule'}];
                // this.progressSpinner = false
              } else {
                this.msgs = [{ severity: 'info', summary: res, detail: '' }];
                this.progressSpinner = false
              }

            }

          })
      },
      reject: () => {
      }
    });
    this.progressSpinner = false;
    this.loadProcessStatus();
  }

  continueProcessingAllTerms() {
    this.showError = false;
    this.confirmationService.confirm({
      message: 'Are you sure you want to reprocess all terms based on selection?',
      header: 'Process Term',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        var inputData = this.getInputdata();
        inputData.isnewAction = false;
        this.progressSpinner = true;
        this.msgs = [{ severity: 'info', summary: 'Processing Term Rule', detail: '' }];
        this.utilService.processAllTermWithCriteria(inputData.division, inputData.sector, inputData.sic, inputData.filerCategory,
          inputData.entityId, inputData.isnewAction).subscribe((response) => {
            const data = response._body;
            if (data) {
              this.msgs = [{ severity: 'info', summary: data, detail: '' }];
              this.progressSpinner = false
            }
          })
      },
      reject: () => {
      }
    });
  }

  resetCoverage() {
    this.companyName = undefined;
    this.divSector = undefined;
    this.filterRadio = undefined;
    this.division = undefined;
    this.sector = undefined;
    this.industry = undefined;
    this.divisionCount = this.completeCompanyList.length;
    this.industryCount = null;
    this.categoryCount = null;
    this.filerCategory = undefined;
    this.filterRadio = undefined;
    this.errorMsg = '';
    this.showError = false;
    this.msgs = [];
    sessionStorage.setItem("processTerm.companyName", this.companyName);
  }

  resetCoverageSelected() {
    this.processRadio = 'termRadio';
    this.selectedTermsCompanies = [];
    this.selectedTermDivSector = undefined;
    this.selectedTermIndustry = undefined;
    this.selectedTermFilerCategory = undefined;

    this.selectedTermCategoryCount = null;
    this.selectedTermDivisionCount = this.completeCompanyList.length;;
    this.selectedTermSectorCount = null;

    this.selectedEntity = undefined;
    this.entityList = [];
    this.msgs = [];

    sessionStorage.setItem("processTerm.selectedTermsCompanies", JSON.stringify(this.selectedTermsCompanies));
  }
  cancelProcessing() {
    this.showError = false;
    this.confirmationService.confirm({
      message: 'Are you sure you want to cancel all pending processing items?',
      header: 'Process Term',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.msgs = [];
        var inputData = this.getInputdata();
        inputData.isnewAction = false;
        this.progressSpinner = true;
        this.msgs = [{ severity: 'info', summary: 'Cancelling Process All Terms', detail: '' }]
        this.utilService.cancelProcessing().subscribe((response) => {
          if (response) {
            this.msgs = [{ severity: 'info', summary: 'All pending processing has been canceled successfully', detail: '' }];
            this.progressSpinner = false
          }
          this.progressSpinner = false;
        })
      },
      reject: () => {
      }
    });
  }

  resetProcessing() {
    this.msgs = [];
    this.progressSpinner = true
    this.msgs = [{ severity: 'info', summary: ' All Term Processing has been reset. Pending and Inprogress TermRules have been restarted again. Please wait for it to be completed', detail: '' }];
    this.utilService.resetProcessing().subscribe((response) => {
      if (response) {
        this.msgs = [{ severity: 'info', summary: 'All Term Processing has been reset and completed.', detail: '' }];
      }
    })
    this.resetProcessingPopup = false;
    this.progressSpinner = false;

  }

  getInputdata() {
    var result: any = {};

    result.sic = "NULL";
    result.filerCategory = "NULL";
    result.division = "NULL";
    result.sector = "NULL";
    result.entityId = "NULL";
    result.isValid = false;
    result.useGroup = true;

    if (this.filterRadio === "all") {
      result.isValid = true;
      return result;
    }
    if ((this.filterRadio === "group")) {
      if ((this.companyName && this.divSector) || (this.filerCategory)) {
        result.isValid = true;
      }


      if (this.companyName) {
        var selectionData = this.companyName;
        if (selectionData.length > 15) {
          selectionData = selectionData.substr(0, selectionData.length - 12);
        }
        var entities = this.completeCompanyList.filter(obj => obj.companyName === selectionData);
        var company = entities.length == 1 ? entities[0] : null;
        result.sic = company.sic;
      }

      if (this.filerCategory) {
        result.filerCategory = this.filerCategory;
      }



      if (this.divSector) {
        var division = this.divSector.substr(0, this.divSector.indexOf('->') - 1);
        var sector = this.divSector.substr(this.divSector.indexOf('->') + 3, this.divSector.length);
        result.division = division;
        result.sector = sector;
      }
    } else {

      result.useGroup = false;

      if (this.companyName) {

        result.division = "settoinvalid";
        result.sector = "settoinvalid";
        var selectionData = this.companyName;
        if (selectionData.length > 15) {
          selectionData = selectionData.substr(0, selectionData.length - 12);
        }
        var entities = this.completeCompanyList.filter(obj => obj.companyName === selectionData);

        var company = entities.length == 1 ? entities[0] : null;

        if (company) {
          result.selectedCompany = company;
          result.isValid = true;
          result.entityId = company.entityId;

        }
      }

    }

    return result;
  }

  filterTerm(event) {
    if (event.query != null && event.query != '') {
      // this.terms = this.terms.sort((a, b) => {
      //   if (a.financialStatement < b.financialStatement) { return -1; }
      //   if (a.financialStatement > b.financialStatement) { return 1; }
      //   return 0;
      // });
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

  onSelectCompany(event){
      if (this.companyName) {
        sessionStorage.setItem('processTerm.companyName', JSON.stringify(this.companyName));
      }
  }

  onSelectTerm(event) {
    if (this.selectedTerms && this.selectedTerms.length > 0) {
      const modifiedSelectedTerms = [];
      this.selectedTerms.forEach(item => {
        modifiedSelectedTerms.push(item.split(' - ')[0]);
      })
      this.selectedTerms = modifiedSelectedTerms;
      sessionStorage.setItem('processTerm.selectedTerms', JSON.stringify(this.selectedTerms));
    }
  }

  processSelectedTerm() {
    this.confirmationService.confirm({
      message: 'Are you sure you want to process all terms based on selection?',
      header: 'Process Term',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.msgs = [];
        this.progressSpinner = true;
        if ((this.selectedTerms.length > 0) && (this.selectedTermsCompanies.length === 0)) {
          this.utilService.processSelctedTerms(this.selectedTerms).subscribe((response) => {
            var res = response._body;
            if (res) {
              this.msgs = [{ severity: 'info', summary: res, detail: '' }];
              this.progressSpinner = false
            }
            this.progressSpinner = false;

          })
        }

        if ((this.selectedTerms.length > 0) && this.selectedTermsCompanies && (this.selectedTermsCompanies.length > 0)) {
          const terms = this.selectedTerms;
          const entities = [];
          this.selectedTermsCompanies.forEach(item => {
            entities.push(item.substring((item.indexOf("(") + 1), item.indexOf(")")));
          });
          if (this.selectedEntity && (this.selectedEntity.length > 0)) {
            this.selectedEntity.forEach(item => {
              if(entities.indexOf(item.cik) < 0) { //do not add if already exist
                entities.push(item.cik);
              }
            })
          }
          this.utilService.processSelctedTermsWithCompany(terms, entities).subscribe((response) => {
            var res = response._body;
            if (res) {
              this.msgs = [{ severity: 'info', summary: res, detail: '' }];
              this.progressSpinner = false
            }
            this.progressSpinner = false;

          })
        }
      },
      reject: () => {
      }
    });
  }

  processAccuTerm() {
    this.confirmationService.confirm({
      message: 'Are you sure you want to process Accuracy Test Term Processing?',
      header: 'Process Term',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.msgs = [];
        this.progressSpinner = true;
        //process user input entities on available terms
        if (this.processRadio === 'companyRadio' && this.selectedTermsCompanies && (this.selectedTermsCompanies.length > 0)) {
          const entities = [];
          //add user input entities from company name input field
          this.selectedTermsCompanies.forEach(item => {
            entities.push(item.substring((item.indexOf("(") + 1), item.indexOf(")")));
          });
          //add selected entities from show resolved list
          if (this.selectedEntity && (this.selectedEntity.length > 0)) {
            this.selectedEntity.forEach(item => {
              if(entities.indexOf(item.cik) < 0) { //do not add if already exist
                entities.push(item.cik);
              }
            })
          }
          this.utilService.processAccuTermsForTermsAndEntities(this.selectedTerms, entities).subscribe((response) => {
            var res = response._body;
            if (res) {
              this.msgs = [{ severity: 'info', summary: res, detail: '' }];
              this.progressSpinner = false
            }
            this.progressSpinner = false;
          })
        }
        //process gotopeer entities on available terms
        else if (this.processRadio === 'divisionRadio' && this.gotoPeerEntities && this.gotoPeerEntities.length > 0){
          const entities = [];
          this.gotoPeerEntities.forEach(item => {
            entities.push(item.cik);
          });
          this.utilService.processAccuTermsForTermsAndEntities(this.selectedTerms, entities).subscribe((response) => {
            var res = response._body;
            if (res) {
              this.msgs = [{ severity: 'info', summary: res, detail: '' }];
              this.progressSpinner = false
            }
            this.progressSpinner = false;
          })
        }
        //process all entities on available terms
        else {
          this.utilService.processAccuTerms(this.selectedTerms).subscribe((response) => {
            var res = response._body;
            if (res) {
              this.msgs = [{ severity: 'info', summary: res, detail: '' }];
              this.progressSpinner = false
            }
            this.progressSpinner = false;
          }
        )}
      },
      reject: () => {
      }
    });
  }

  handleCIKListSearch(id: string) {
    this.ciksFromDialog = "";
    this.selectedTermsCompanies = [];
    this.dialogModalService.open(id);
    this.myInputVariable.nativeElement.value = ''; //clear the upload file name on the side of the upload button
  }

  close(id: string){
    this.dialogModalService.close(id);
    this.myInputVariable.nativeElement.value = ''; //clear the upload file name on the side of the upload button
  }

  confirm(id: string) {
    let selected = [];
    selected = this.ciksFromDialog.split(',');
    this.selectedTermsCompanies = [];

    for (let j=0; j < selected.length; j++){
      let cik: string =selected[j];
      if (cik != null && cik.trim().length>1 ) {
        cik = '0000000000' + cik.trim();
        cik = cik.substring(cik.length-10);
        for (let i = 0; i < this.companyList.length; i++) {
          let cname: string = this.companyList[i];
          if (cname.toLowerCase().indexOf(cik) >= 0) {
            this.selectedTermsCompanies.push(cname);
          }
        }
      }
    }
    let uniqueSelected = new Set(this.selectedTermsCompanies);
    this.selectedTermsCompanies = Array.from(uniqueSelected); //make an unique array
    this.selectedTermsCompanies.slice(0,99);//limit input cik list to size of 100

    this.processRadio = 'companyRadio';
    this.ciksFromDialog = '';
    this.dialogModalService.close(id);
    this.myInputVariable.nativeElement.value = ''; //clear the upload file name on the side of the upload button

  }

  uploadFile(event) {
    const file:File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
          let text = reader.result.toString().trim();
          text = text.replace(/\r?\n|\r/g, "");
          this.ciksFromDialog = text;
      }
      reader.readAsText(file);
    //   this.fs.readFile('file.txt', function (err, data) {
    //     if (err) {
    //         return console.error(err);
    //     }
    //     console.log("Asynchronous read: " + data.toString());
    // });
    }
  }

}
