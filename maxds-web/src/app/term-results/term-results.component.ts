import { Component, OnInit, ViewChild, Input, OnChanges } from '@angular/core';
import { ProfileService } from 'app/services/profile.service';
import { Router } from '@angular/router'
import { Table } from 'primeng/table';

@Component({

  selector: 'app-term-result',
  templateUrl: './term-results.component.html',
  styleUrls: ['./term-results.component.css']

})
export class TermResultsComponent implements OnInit, OnChanges {

  @ViewChild(Table) dt: Table;
  @ViewChild(Table) drt: Table;
  selectedTermResults: any[] = [];
  @Input() state;


  tableHeader: string = "test";
  derivationList: any[];
  minRange: number = (new Date()).getFullYear() - 4;
  maxRange: number = (new Date()).getFullYear();
  range: number[] = [(new Date()).getFullYear() - 4, (new Date()).getFullYear()];

  annualdata: any;
  quaterlydata: any;
  options: any;
  quaterlyoptions: any;
  allTermResults: any[];
  termResults: any[];
  chartXdata: any[] = [];
  chartYdata: any[] = [];
  quaterlychartXdata: any[] = [];
  quaterlychartYdata: any[] = [];
  termName: string;
  entityId: string;
  companyName: string;
  annualChecked: boolean = false;
  quaterlyChecked: boolean = false;
  showMissPrdChecked: boolean = false;


  constructor(private profileService: ProfileService, private router: Router) {
    if (this.router.getCurrentNavigation() && this.router.getCurrentNavigation().extras && this.router.getCurrentNavigation().extras.state) {
      this.termName = this.router.getCurrentNavigation().extras.state.termName;
      this.entityId = this.router.getCurrentNavigation().extras.state.entityId;
      this.companyName = this.router.getCurrentNavigation().extras.state.companyName;
    }
    if (sessionStorage.getItem('home.range') !== null) {
      this.range = JSON.parse(sessionStorage.getItem('home.range'));
    }
    this.termResults = [];
    this.populateAnnualChartData();
    this.populateQuaterlyChartData();
  }

  cols: any[];
  exportColumns: any[];
  display: boolean = false;

  yearChange() {
    const event = {
      target: {
        checked: this.showMissPrdChecked
      }
    }
    //this.showMissingProductData(event);
    this.getTermMngrResults();

  }

  showDerivationResults(derivationData) {
    //console.log(derivationData)
    let data = derivationData.derivationTrails;
    this.display = true;
    this.tableHeader = data.header;
    this.derivationList = [];
    this.derivationList = data.assets;
  }

  ngOnChanges() {
    if (this.state) {
      this.termName = this.state.termName;
      this.entityId = this.state.entityId;
      this.companyName = this.state.companyName;

      this.getTermMngrResults();
    }
  }

  ngOnInit() {
    this.getTermMngrResults();
    this.cols = [
      { field: 'FY', header: 'Period', width: '6%', exportable: true },
      { field: 'rank', header: 'Rank', width: '4%', exportable: true },
      { field: 'value', header: 'Value', width: '10%', exportable: true },
      { field: 'filingDate', header: 'Filing Date', width: '6%', exportable: true },
      { field: 'percentVarianceWithPrevPeriod', header: 'Period Over Period Variance', width: '8%', exportable: true },
      { field: 'percentVarianceWithPrevYear', header: 'Year Over Year Variance', width: '8%', exportable: true },
      { field: 'resolvedExpression', header: 'Expression', width: '38%', exportable: true },
      { field: 'validationMessages', header: 'Validation Messages', width: '22%', exportable: true },
      { field: 'devrivationTrails', header: 'Derivation Trails', width: '11%', exportable: true }
    ];
    this.exportColumns = this.cols.map(col => ({ title: col.header, dataKey: col.field }));
  }


  exportData(table) {

    table.value.forEach(item => {
      item.percentVarianceWithPrevPeriod = item.percentVarianceWithPrevPeriod.toString().includes('e') ? item.percentVarianceWithPrevPeriod.toFixed() : item.percentVarianceWithPrevPeriod;
      item.percentVarianceWithPrevYear = item.percentVarianceWithPrevYear.toString().includes('e') ? item.percentVarianceWithPrevYear.toFixed() : item.percentVarianceWithPrevYear;
    });
    table.exportCSV();
  }

  showAnnualData(event) {
    let filterDataSet: any[] = [];
    if (event.target.checked) {
      this.allTermResults.forEach(term => {
        if (this.quaterlyChecked && this.showMissPrdChecked) {
          if ((term.isAnnual || term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (this.quaterlyChecked && !this.showMissPrdChecked) {
          if ((term.isAnnual || term.isQtrly) && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (!this.quaterlyChecked && this.showMissPrdChecked) {
          if ((term.isAnnual && !term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else {
          if (term.isAnnual && !term.isQtrly && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        }
      })
      this.termResults = [];
      this.termResults = this.filterDataBasedOnRange(filterDataSet);
    } else {
      this.allTermResults.forEach(term => {
        if (this.quaterlyChecked && this.showMissPrdChecked) {
          if ((!term.isAnnual && term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (this.quaterlyChecked && !this.showMissPrdChecked) {
          if (!term.isAnnual && term.isQtrly && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (!this.quaterlyChecked && this.showMissPrdChecked) {
          if ((!term.isAnnual && !term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else {
          this.termResults = [];
        }
      })
      this.termResults = [];
      this.termResults = this.filterDataBasedOnRange(filterDataSet);
    }
  }

  showQuaterlyData(event) {
    let filterDataSet: any[] = [];
    if (event.target.checked) {
      this.allTermResults.forEach(term => {
        if (this.annualChecked && this.showMissPrdChecked) {
          if ((term.isAnnual || term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (this.annualChecked && !this.showMissPrdChecked) {
          if ((term.isAnnual || term.isQtrly) && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (!this.annualChecked && this.showMissPrdChecked) {
          if ((!term.isAnnual && term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else {
          if (!term.isAnnual && term.isQtrly && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        }
      })
      this.termResults = [];
      this.termResults = this.filterDataBasedOnRange(filterDataSet);
    } else {
      this.allTermResults.forEach(term => {
        if (this.annualChecked && this.showMissPrdChecked) {
          if ((term.isAnnual && !term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (this.annualChecked && !this.showMissPrdChecked) {
          if ((term.isAnnual && !term.isQtrly) && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (!this.annualChecked && this.showMissPrdChecked) {
          if ((!term.isAnnual && !term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else {
          this.termResults = [];
        }
      })
      this.termResults = [];
      this.termResults = this.filterDataBasedOnRange(filterDataSet);
    }
  }
  showMissingProductData(event) {
    let filterDataSet: any[] = [];
    if (event.target.checked) {
      this.allTermResults.forEach(term => {

        if (this.annualChecked && this.quaterlyChecked) {
          if ((term.isAnnual || term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (this.annualChecked && !this.quaterlyChecked) {
          if ((term.isAnnual && !term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (!this.annualChecked && this.quaterlyChecked) {
          if ((!term.isAnnual && term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else {
          if ((!term.isAnnual && !term.isQtrly) || term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        }
      })
      this.termResults = [];
      this.termResults = this.filterDataBasedOnRange(filterDataSet);
    } else {
      this.allTermResults.forEach(term => {

        if (this.annualChecked && this.quaterlyChecked) {
          if ((term.isAnnual || term.isQtrly) && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (this.annualChecked && !this.quaterlyChecked) {
          if (term.isAnnual && !term.isQtrly && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else if (!this.annualChecked && this.quaterlyChecked) {
          if (!term.isAnnual && term.isQtrly && !term.isMissingPrd) {
            filterDataSet.push(this.populateData(term))
          }
        } else {
          this.termResults = [];
        }
      })
      this.termResults = [];
      this.termResults = this.filterDataBasedOnRange(filterDataSet);
    }
  }

  getPercentage(value) {
    value = value * 100;
    value = Math.round(value * 100) / 100;
    return value + "%";
  }

  populateAnnualChartData() {
    this.annualdata = {
      labels: this.chartXdata,
      datasets: [
        {
          label: 'Annual',
          backgroundColor: "#007ad9",
          data: this.chartYdata
        }
      ]
    }

    this.options = {
      title: {
        display: true,
        text: '',
        fontSize: 16
      },
      legend: {
        position: 'top'
      },

      scales: {
        yAxes: [{
           ticks: {
              //stepSize: 100000,
              suggestedMin: 0,
              callback: function(value) {
                 function formatNumber(value) {
                  if (value) {
                    var parts = value.toString().split(".");
                    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                    return parts.join(".");
                  } else return "";
                 }
                 //return '$' + formatNumber(value);
                 return formatNumber(value);
              }
           }
        }]
     }
    };
  }


  populateQuaterlyChartData() {
    this.quaterlydata = {
      labels: this.quaterlychartXdata,
      datasets: [
        {
          label: 'Quarterly',
          backgroundColor: "#007ad9",
          data: this.quaterlychartYdata
        }
      ]
    }

    this.quaterlyoptions = {
      title: {
        display: true,
        text: '',
        fontSize: 16
      },
      legend: {
        position: 'top'
      },
      scales: {
        yAxes: [{
           ticks: {
              //stepSize: 100000,
              suggestedMin: 0,
              callback: function(value) {
                 function formatNumber(value) {
                  if (value) {
                    var parts = value.toString().split(".");
                    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                    return parts.join(".");
                  } else return "";
                 }
                 return formatNumber(value);
              }
           }
        }]
     }
    };
  }
  openTermLink(data) {
    let url: string = 'https://www.sec.gov/Archives/edgar/data/' + data.cik + '/' + data.accession + '-index.htm';
    console.log(url);
    window.open(url, "_blank");

  }

  getTermMngrResults() {
    this.profileService.getTermMngrResults(this.termName, this.entityId).subscribe((response) => {
      this.termResults = [];
      this.allTermResults = [];
      this.chartXdata = [];
      this.quaterlychartXdata = [];

      console.log(response);
      response.forEach(termMgrRslt => {
        this.termResults.push(this.populateData(termMgrRslt));
        this.allTermResults.push(this.populateData(termMgrRslt));

        if (termMgrRslt.FY >= this.range[0] && termMgrRslt.FY <= this.range[1]) { //filter chart data in year range
          if (termMgrRslt.FQ === "FY") {
            this.chartXdata.push(termMgrRslt.FY)
            this.chartYdata.push(termMgrRslt.value)
            this.populateAnnualChartData();
          } else {
            this.quaterlychartXdata.push(termMgrRslt.FY + "-" + termMgrRslt.FQ)
            this.quaterlychartYdata.push(termMgrRslt.value)
            this.populateQuaterlyChartData();
          }
        }

      })
      this.filterAnnualData();
    },
      (error) => console.log(error)
    )

  }

  numberWithCommas(x) {
    if (x) {
      var parts = x.toString().split(".");
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      return parts.join(".");
    } else return "";
}

  populateData(termMgrRslt) {

    return {
      rank: termMgrRslt.rank, FY: termMgrRslt.FY, FQ: termMgrRslt.FQ, value: termMgrRslt.value, expression: termMgrRslt.expression, filingDate: termMgrRslt.filingDate,
      percentVarianceWithPrevPeriod: termMgrRslt.percentVarianceWithPrevPeriod, percentVarianceWithPrevYear: termMgrRslt.percentVarianceWithPrevYear, resolvedExpression: termMgrRslt.resolvedExpression,
      validationMessages: termMgrRslt.validationMessages, isAnnual: (termMgrRslt.FQ === "FY") ? true : false, isQtrly: (termMgrRslt.FQ !== "FY") ? true : false, isMissingPrd: (termMgrRslt.rank != 99) ? false : true, accession: termMgrRslt.accession, derivationTrails: termMgrRslt.derivationTrails
    }

  }

  filterAnnualData() {
    let filterDataSet = [];
    this.annualChecked = true;
    this.allTermResults.forEach(term => {
      if (this.quaterlyChecked && this.showMissPrdChecked) {
        if ((term.isAnnual || term.isQtrly) && term.isMissingPrd) {
          filterDataSet.push(this.populateData(term))
        }
      } else if (this.quaterlyChecked && !this.showMissPrdChecked) {
        if ((term.isAnnual || term.isQtrly) && !term.isMissingPrd) {
          filterDataSet.push(this.populateData(term))
        }
      } else if (!this.quaterlyChecked && this.showMissPrdChecked) {
        if (term.isAnnual && !term.isQtrly && term.isMissingPrd) {
          filterDataSet.push(this.populateData(term))
        }
      } else {
        if (term.isAnnual && !term.isQtrly && !term.isMissingPrd) {
          filterDataSet.push(this.populateData(term))
        }
      }
    })
    this.termResults = [];
    this.termResults = this.filterDataBasedOnRange(filterDataSet);
  }

  filterDataBasedOnRange(filterDataSet) {
    const termResults = [];
    filterDataSet.forEach(item => {
      if (item.FY >= this.range[0] && item.FY <= this.range[1]) {
        termResults.push(item);
      }
    });
    return termResults;
  }
}
