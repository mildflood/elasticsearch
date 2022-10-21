import { Component } from '@angular/core';
import { OnInit, ViewChild } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ISubscription } from 'rxjs/Subscription';
import { NavigationEnd, Router } from '@angular/router';
import { AccuracyTestService } from './accuracyTestService';
import { ProfileService } from 'app/services/profile.service';
import { DomSanitizer } from '@angular/platform-browser';
import { AccuracyTestItem } from './accuracytestitem';
import { TableModule, Table } from 'primeng/table';
import { Message } from 'primeng/api';

@Component({
  selector: 'app-accuracy-test',
  templateUrl: './accuracyTest.html',
  styleUrls: ['./accuracyTest.css']
})
export class AccuracyTestComponent implements OnInit, OnDestroy {

  progressSpinner: boolean = false;
  msgs: Message[] = [];

  private subscription: ISubscription[] = [];
  navigationSubscription;
  accuracytestdata: any = { items: [] };
  years: any[] = [];
  companyList: any[];
  filteredYears: any[];
  filteredCompany: any[];
  researchurl: string = "https://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&CIK=";
  accuracyTestList: any[];
  fillingURL: string;
  htmlcontent: any;
  accuracyTest = { year: (new Date()).getFullYear(), companyName: "" };
  showTitle: boolean = false;
  cols: any[];
  exportColumns: any[];
  selectedAccuracyTest: any[] = [];
  accuracydata: AccuracyTestItem;
  accuracyDataItem: any = { notes: "", isCheckedCS: false, isCheckedMaxDS: false };
  errorMsg: string;
  showError: boolean;

  @ViewChild(Table) dt: Table;

  constructor(private profileService: ProfileService, private accuracyTestService: AccuracyTestService, private router: Router, private domsanitizer: DomSanitizer) {
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
      }
    });
  }

  loadBackgroundColors(column, data) {
    if (data.csTermId === null && data.termId !== null) {
      return { 'background-color': 'white' };
    }
    if (column.field === 'value') {
      if (data.csTermValue === data.value) {
        return { 'background-color': '#00B800' };
      } else {
        if ((this.calculatePercentage(data.csTermValue, data.value) < 1.05) && (this.calculatePercentage(data.csTermValue, data.value) > 0)) {
          return { 'background-color': 'orange' };
        } else {
          return { 'background-color': 'red' };
        }
      }
    } else {
      return { 'background-color': 'white' };
    }
  }

  calculatePercentage(csValue, mxdsValue) {

    let difPerAvg = (mxdsValue / csValue);
    if (mxdsValue > csValue) {
      difPerAvg = (mxdsValue / csValue);
    }
    else {
      difPerAvg = (csValue / mxdsValue);
    }
    //(maxds/csvalue)*100
    //console.log(difPerAvg);
    return difPerAvg;

  }

  ngOnInit() {
    for (let i = 0; i < 5; i++) {
      this.years.push((new Date().getFullYear() - i) + '');
    }

    console.log('accuracyTestComponent : calling ngOnInit...');
    this.cols = [
      { field: 'csTermId', header: 'CS', hidden: false, exportable: true, width: '8%' },
      { field: 'csTermValue', header: this.accuracyTest.year, exportable: true, width: '17%' },
      { field: 'isCheckedCS', header: '', hidden: false, exportable: true, width: '5%' },
      { field: 'termId', header: 'MAXDS', hidden: false, exportable: true, width: '8%' },
      { field: 'value', header: this.accuracyTest.year, hidden: false, exportable: true, width: '17%' },
      { field: 'isCheckedMaxDS', header: '', hidden: false, exportable: true, width: '5%' },
      { field: 'resolvedExpression', header: 'Resolved Expression', exportable: true, width: '29%' },
      { field: 'notes', header: 'Notes', hidden: false, exportable: true, width: '10%' }
    ];
    this.exportColumns = this.cols.map(col => ({ title: col.header, dataKey: col.field }));
    this.getCompanyNames();

    if (sessionStorage.getItem('accuracyTest.companyName') !== null) {
      this.accuracyTest.companyName = sessionStorage.getItem('accuracyTest.companyName');
    }
    if (sessionStorage.getItem('accuracyTest.year') !== null) {
      this.accuracyTest.year = JSON.parse(sessionStorage.getItem('accuracyTest.year'));
    }
  }

  onCompanyNameChange($event) {
    console.log(this.accuracyTest.companyName);
    sessionStorage.setItem('accuracyTest.companyName', $event);
  }
  onYearChange($event) {
    console.log(this.accuracyTest.year);
    sessionStorage.setItem('accuracyTest.year', JSON.stringify($event));
  }

  exportTableToCSV(table) {
    table.exportCSV();
  }

  csCheckboxselect(event, rowdata) {

    rowdata.isCheckedCS = event.target.checked;
    if (this.selectedAccuracyTest.length == 0) {
      this.selectedAccuracyTest.push(rowdata);
    } else {
      this.selectedAccuracyTest.forEach(item => {
        if (item.termId.indexOf(rowdata.termId) == -1 && !item.isCheckedMaxDS) {
          this.selectedAccuracyTest.push(rowdata);
        } else {
          if (!event.target.checked && !item.isCheckedMaxDS) {
            this.selectedAccuracyTest.splice(rowdata);
          }
        }
      })
    }
    console.log(this.selectedAccuracyTest);

  }

  maxDsCheckboxselect(event, rowdata) {

    rowdata.isCheckedMaxDS = event.target.checked;
    if (this.selectedAccuracyTest.length == 0) {
      this.selectedAccuracyTest.push(rowdata);
    } else {
      this.selectedAccuracyTest.forEach(item => {
        if (item.termId.indexOf(rowdata.termId) == -1 && !item.isCheckedCS) {
          this.selectedAccuracyTest.push(rowdata);
        } else {
          if (!event.target.checked && !item.isCheckedCS) {
            this.selectedAccuracyTest.splice(rowdata);
          }
        }
      })
    }

    console.log(this.selectedAccuracyTest);
  }

  saveAccuracyTestData() {
    this.showError = false;
    console.log(this.selectedAccuracyTest);
    let items: any[] = [];
    this.selectedAccuracyTest.forEach(selectedItem => {
      this.accuracydata = new AccuracyTestItem();
      this.accuracydata.csTermId = selectedItem.csTermId;
      this.accuracydata.csTermValue = selectedItem.csTermValue;
      this.accuracydata.isCheckedCS = selectedItem.isCheckedCS;
      this.accuracydata.isCheckedMaxDS = selectedItem.isCheckedMaxDS;
      this.accuracydata.resolvedExpression = selectedItem.resolvedExpression;
      this.accuracydata.termId = selectedItem.termId;
      this.accuracydata.notes = selectedItem.notes;
      items.push(this.accuracydata);
    })
    this.accuracytestdata.items = items;
    this.accuracyTestService.saveAccuracyTestData(this.accuracytestdata).subscribe((response) => {
      this.onSubmit(null);
    }, (error) => console.log(error)
    )
  }

  searchYears() {
    this.filteredYears = [];
    for (let i = 0; i < this.years.length; i++) {
      let year: string = this.years[i];
      this.filteredYears.push(year);
    }
  }

  onSubmit(dt) {
    this.showError = false;
    if (this.accuracyTest.companyName  === undefined || this.accuracyTest.companyName  === null || this.accuracyTest.companyName === "" ) {
      this.showError = true;
      this.errorMsg = 'Please Select Company to Run Test';
      return;
    }
    this.progressSpinner = true;
    this.msgs = [{ severity: 'info', summary: '"Processing in progress!"', detail: '' }];

    this.dt.columns[1].header = this.accuracyTest.year;
    this.dt.columns[4].header = this.accuracyTest.year;
    this.dt.reset();
    this.accuracyTestService.runAccuracyTest(this.getTermIdOrEntityName(this.accuracyTest.companyName), this.accuracyTest.year).subscribe((response) => {
      this.accuracyTestList = [];
      this.getFilingUrl();
      const HTMLfillingURL = response.filingUrl;
      this.progressSpinner = false;
      this.msgs = [{ severity: 'info', summary: '"Processing completed!"', detail: '' }];
      // this.accuracyTestList = response.items;

      setInterval(() => {
        this.msgs = [];
      }, 50000)
      response.items.forEach(item => {
        let resolvedValue: string;
        if (item.resolvedExpression != null) {
          resolvedValue = this.differenceBtnAccu(item.resolvedExpression);
          //item.resolvedValue = resolvedValue;
        } else {
          resolvedValue = "0";
          //item.resolvedValue = resolvedValue;
        }

        this.accuracyTestList.push({
          termId: item.termId, csTermValue: item.csTermValue, resolvedExpression: item.resolvedExpression,
          value: item.value, csTermId: item.csTermId, notes: item.notes, isCheckedCS: item.isCheckedCS, isCheckedMaxDS: item.isCheckedMaxDS
        })
      });
      console.log("#######################################################33");
      console.log(this.accuracyTestList);
      console.log("#######################################################33");
      this.accuracyTestService.fillingHTMLView(HTMLfillingURL).subscribe((response) => {
        this.htmlcontent = this.domsanitizer.bypassSecurityTrustHtml(response.resultObject);
        //this.htmlcontent = this.domsanitizer.bypassSecurityTrustResourceUrl(response.resultObject);
        //this.htmlcontent = this.domsanitizer.bypassSecurityTrustStyle(response.resultObject);
        //this.htmlcontent = this.domsanitizer.bypassSecurityTrustScript(response.resultObject);

        this.showTitle = true;
      }, (error) => console.log(error)
      )
    },
      (error) => console.log(error)
    )
  };

  exportAccuracyResults() {
    this.accuracyTestService.exportAccuracyResults(this.getTermIdOrEntityName(this.accuracyTest.companyName), this.accuracyTest.companyName, this.accuracyTest.year.toString()).subscribe((response) => {
      console.log(response)
      const blob = new Blob([response], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
      this.showTitle = true;
    }
    )
  }

  differenceBtnAccu(value: string): string {
    return value.substr(value.indexOf("=") + 1);
  }

  populateColor(record) {
    return "red"
  }

  getTermIdOrEntityName(inputValue: string): string {
    let outputValue: string = inputValue.substring((inputValue.indexOf("(") + 1), inputValue.indexOf(")"));
    return outputValue;
  }

  openTermLink(data) {
    let url: string = 'https://www.sec.gov/Archives/edgar/data/' + data.cik + '/' + data.accession + '-index.htm';
    console.log(url);
    window.open(url, "_blank");

  }

  searchCompany(event) {
    this.filteredCompany = [];
    for (let i = 0; i < this.companyList.length; i++) {
      let cname = this.companyList[i].name;
      if ((cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) || (cname != null && cname.toLowerCase().includes(event.query.toLowerCase()))) {
        this.filteredCompany.push(cname);
      }
    }
  }

  getCompanyNames() {
    this.profileService.getCoompanyNames().subscribe((response) => {
      this.companyList = [];
      console.log(response);
      response.forEach(preferences => {
        this.companyList.push({ name: preferences });
      })
    },
      (error) => console.log(error)
    )
  }

  ngOnDestroy() {
    this.subscription.forEach(s => s.unsubscribe());
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  openResearchLink() {
    let cik = this.getTermIdOrEntityName(this.accuracyTest.companyName);
    window.open(this.researchurl + cik, "_blank");

  }
  openlink() {
    window.open(this.fillingURL, "_blank");
  }

  getFilingUrl(termId?) {
    const reqData = {
      companyId: this.accuracyTest.companyName.slice(this.accuracyTest.companyName.indexOf('(') + 1, this.accuracyTest.companyName.indexOf(')')),
      year: this.accuracyTest.year,
      termId: termId ? termId : null
    }

    this.accuracyTestService.getFilingUrl(reqData).subscribe(res => {
      if (res && res._body) {
        if (termId) {
          window.open(res._body, "_blank");
        } else {
          this.fillingURL = res._body;
        }
      }
    }, (error) => console.log(error));
  }

  goManageTermPage(termId?) {
    sessionStorage.setItem('accuracyTest.presetTermId', termId);
    //window.open('#/manageTerm', '_self');
    window.open('#/manageTerm', '_blank').focus();
    //sessionStorage is not shared among browser tabs
    sessionStorage.removeItem('accuracyTest.presetTermId');
  }
}
