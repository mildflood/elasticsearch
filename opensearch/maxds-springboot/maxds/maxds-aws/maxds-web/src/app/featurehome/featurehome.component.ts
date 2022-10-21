import { Component, OnInit, HostListener, ElementRef } from '@angular/core';
import { HomeService } from 'app/home/homeService';
import { AppComponent } from 'app/app.component';
import { Router, NavigationEnd } from '@angular/router';
import { TreeNode, Message, ConfirmationService } from 'primeng/api';
import { Home } from 'app/domain/home';
import { UtilService } from 'app/utils/utilService';
import { ISubscription } from 'rxjs/Subscription';
import { TermRule } from 'app/domain/termRule';
import { MappedEntity } from 'app/domain/mappedEntity';
import { EditExpression } from 'app/domain/editExpression';
import { Idle, DEFAULT_INTERRUPTSOURCES } from '@ng-idle/core';
import { Keepalive } from '@ng-idle/keepalive';
import { LoginService } from 'app/services/login.service';
import { JoyrideService } from 'ngx-joyride';
import { ActivatedRoute } from '@angular/router';
declare var d3: any
@Component({
  selector: 'app-featurehome',
  templateUrl: './featurehome.component.html',
  styleUrls: ['./featurehome.component.css']
})
export class FeaturehomeComponent implements OnInit {
  private subscription: ISubscription[] = [];
  navigationSubscription;
  toHighlight;
  private termRuleByStatList: any = [];
  private trmRulFinStatmntList: any = [];
  private trmRulPriorGrpList: any = [];
  private periodTypeList: any = [{ name: "instant" }, { name: "duration" }, { name: "na" }];
  private typeList: any = [{ name: "decimal" }, { name: "integer" }, { name: "monetary" }, { name: "perShare" }, { name: "ratio" }, { name: "shares" }, { name: "na" }];
  private expressionTypes = ["ConceptMatch", "ConceptMatchWithDim", "ExtendedCheck", "ExtendedCheckWithDim", "Formula", "ConceptMatchMultiple"];
  private rulePeriodTypes = ["instant", "duration", "na"];
  private testFiscalYears = ["2015", "2016", "2017", "2018", "2019"];
  private balanceTypes = ["debit", "credit", "na", "none"];
  private ruleTypes = ["decimal", "integer", "monetary", "perShare", "ratio", "shares", "na"];
  private derivedZeroTypes = ["NoteBlockExistsCheck", "DependentTermCheck", "ExclusiveTermCheck", "IndustryExclusionCheck"];
  private formulaOperatorsList: any = [{ name: "" }, { name: "+" }, { name: "-" }, { name: "*" }, { name: "/" }];
  private termRulesList: any;
  private populateList: any[] = [];
  private uncategorizedPopulateList: any[] = [];
  private termRulesPriorityGroupList;
  private userRolesList;
  private userDisplayName;
  private display: true;
  msgs: Message[] = [];
  expressioncols: any;
  modalExpressioncols: any;
  entitycols: any;
  selectedItem: any;
  termTree: TreeNode[] = [];
  selectedTerm: TreeNode;
  termName: string;
  termCode: string;
  definition: string;
  incAccuTest: boolean;
  periodType: any;
  type: any;
  finStatement: any;
  priorgroup: any;
  processOrder: string;
  processingOrder: string;
  expTableList: any[] = [];
  expressionList: any[] = [];
  validationList: any[] = [];
  indOvrrideList: any[] = [];
  dervdZroExpList: any[] = [];
  expLabel: string;
  indOvrdExprDrop: boolean;
  indovrDrpList: any[];
  companyovrRideList: any[];
  selIndOvrdExpr: any;
  chart: any;
  companyList: any[];
  completeCompanyList: any[];
  completeEntities: any[] = [];
  filteredCompany: any[];
  companyName: string;
  termId: string;
  divSectorList: any[];
  filtereddivSector: any[];
  divSector: string;
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
  quaterly: boolean = true;
  filterRadio: string;
  newTerm: boolean;
  errorMsg: string = '';
  entityList: any[];
  chartData: any;
  resolvedRanks: any[];
  unResolvedRanks: any[];
  selectResolvedRank: string;
  chartUrl: string = '/api/TermRulesStatisticsForCriteria'
  termRule: TermRule;
  company: string;
  isAdminUser: boolean = true;
  index: number = 0;
  filteredconcMat: any[];
  conceptNameDefinition: string = "";
  concMatch: any;
  divisionCount: number;
  industryCount: number;
  categoryCount: any;
  fsqvLink: string = "";

  usePositiveFacts: boolean;
  useNegativeFacts: boolean;
  revNegativeFacts: boolean;
  axis: string;
  virtualFactMemberExclusions: any[];
  maxAxisCount: number;
  useMaxAxisCount: string;
  expressionSets: any[];
  conceptMatchMultipleList: any[];
  conceptMatchMultiple: any[];

  exprStr = "isExtended";
  containsWords: any[] = [];
  doesNotContainsWords: any[] = [];
  nameDoesNotContains: any[] = [];
  balType: string;
  perType: string;
  isShareItemType: boolean;
  axes: any;
  axesMembers: any;
  nameContains: any[] = [];
  extendedEntitiesLookup: any[] = [];
  namedAxisList: any[];
  namedMemberList: any[];
  formulaSets: any[];
  formulaTermName: string;
  formulaAllowNull: boolean;
  formulaOperator: string;

  formulaFilteredTerms: any[];
  termRuleLists: any[] = [];
  overrideForEdit: any;
  validationExpression: any;
  validationRank: any;
  addvalidation: boolean;

  entityOverrides: any[];
  overrideName: any;
  mergeBaseExpressions: boolean;
  overridecompanyName: any;
  overrideDivSec: any;

  derivedZeroExpressionForEdit: any = {};
  activeExpressionTabIndex = 0;
  excludeEntitiesFilter: any = {};
  filteredDeriveddivSectors: any = [];
  deriveddivSector: string = '';
  derivedindustry: string = '';
  filteredDerivedindustry: any[] = [];
  derivedRank: boolean = true;

  // this are for enabling and disabling purpos
  displayTerms: boolean = false;
  processnow: boolean = false;
  displayGetCoverage: boolean = false;
  showError: boolean;
  progressSpinner: boolean;
  enableTermButton: boolean;
  enableAddTermButton: boolean;
  enableTermCode: boolean;
  editExpression: boolean;
  addExpression: boolean;
  isOverrideExpressionForEdit: boolean;
  disableAxes: boolean;
  disableMember: boolean;
  disableMemInc: boolean;
  disableAxesInc: boolean;
  showExpression: boolean;
  addEditValidation: boolean;
  addEditIndustry: boolean;
  addEditDerived: boolean;
  filteredtext: any[] = [];
  expressionForEdit: any;

  expressionName: string;
  startYear: number = (new Date()).getFullYear() - 4;
  endYear: number = (new Date()).getFullYear();
  minRange: number = (new Date()).getFullYear() - 4;
  maxRange: number = (new Date()).getFullYear();
  range: number[] = [(new Date()).getFullYear() - 4, (new Date()).getFullYear()];
  minYear: number = (new Date()).getFullYear() - 4;
  maxYear: number = (new Date()).getFullYear();

  timedOut = false;
  lastPing?: Date = null;
  displayTermTreeModal = false;
  derivationTreeRes;

  modalExpTableList = [];
  modalExpTableData;
  minimizeArray = [];

  treeData = {};
  treeCol = [
    { field: 'name', header: 'Name' }
  ];
  chartDonutOptions;

  showTermResultsWindow = [];
  showTermResultsWindowMinimize = [];
  state = [];
  title = 'Maxds';
  tour: boolean;
  percent: number = 0;
  totalEntityCount: number = 0;
  showValidation: boolean;
  validationMessage: string;
  reloadTermFlag: boolean = false;

  constructor(private homeService: HomeService, private app: AppComponent, private router: Router, private home: Home, private utilService: UtilService, private mappedEntity: MappedEntity, private rule: TermRule, private el: ElementRef,
    private confirmationService: ConfirmationService, private editForExpression: EditExpression, private idle: Idle, private keepalive: Keepalive, private login: LoginService, private readonly joyrideService: JoyrideService, private route: ActivatedRoute) {

    this.app.showmenu = true;

    this.chart = {};
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
      }
    });
  }

  public areas = [
    { size: 20, order: 1, name: 'Term Tree' },
    { size: 80, order: 2, name: 'Content' }
  ];

  onGutterClick(e: {gutterNum: number; sizes: number[]}) {
    if (!e || e.gutterNum !== 1) return;

    if(e.sizes[0] > 0) {
      this.areas = [
        { size: 0, order: 1, name: 'Term Tree' },
        { size: 100, order: 2, name: 'Content' }

      ];
    }
    else {
      this.areas = [
        { size: 20, order: 1, name: 'Term Tree' },
        { size: 80, order: 2, name: 'Content' }
      ];
    }
  }

  onDragEnd(e: {gutterNum: number; sizes: number[]}) {
    this.areas[0].size = e.sizes[0];
    this.areas[1].size = e.sizes[1];
  }

  ngOnInit() {
    console.log('homeComponent : calling ngOnInit...');
    this.termRule = new TermRule;
    //this.expressionForEdit = null;
    this.expressionName = 'Expression';
    this.display = true;
    this.errorMsg = '';
    this.progressSpinner = false;
	//from resolver
	this.completeCompanyList = this.route.snapshot.data['completeCompanyList'];
	this.trmRulFinStatmntList = this.route.snapshot.data['termRuleCategoryList'];

	//init ...
    this.expressioncols = this.home.homeCols();
    this.modalExpressioncols = this.home.modalExpressioncols();
    this.getRuleList();
    this.getTermRuleOptions();
    this.getUserRole();
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

    if (! this.reloadTermFlag) {
      this.refresh();
      this.termRulesList = this.route.snapshot.data['termRuleList'];
      this.addTerm();
    } else {
      this.enableTermCode = true;
      this.showError = false;
      this.newTerm = true;
      this.definition = '';
      this.termCode = '';
      this.termName = '';
      this.periodType = { name: '' };
      this.type = { name: '' };
      this.incAccuTest = false;
      this.finStatement = { financialStatement: '' };
      this.priorgroup = { name: '' };
      this.processingOrder = '';
      this.termRule = this.rule;
      this.expressionList = [];
      this.validationList = [];
      this.indOvrrideList = [];
      this.dervdZroExpList = [];
      this.expTableList = [];
      this.displayTerms = false;
    }

	this.loadFromStorage();
  }

  ngAfterContentInit() {
   // this.loadFromStorage();
  }

  loadFromStorage() {
    let termTreeId = sessionStorage.getItem('home.termTreeId');
	console.log('selected: ' + termTreeId);
    if (sessionStorage.getItem('home.termRule') !== null) {
	  this.termRule = JSON.parse(sessionStorage.getItem('home.termRule'));
	  this.termId = this.termRule.termId;
      this.processnow = false;
      this.indovrDrpList = [];
      this.selIndOvrdExpr = null;
      this.indOvrdExprDrop = false;
      this.enableAddTermButton = false;

      this.displayTerms = true;
      this.displayGetCoverage = false;
      this.enableTermButton = false;
      this.entitycols = this.home.homeEntityCols();
      this.expressionName = 'Expression'

	  if (sessionStorage.getItem('home.termRule') !== null) {
        this.termRule = JSON.parse(sessionStorage.getItem('home.termRule'));
		this.termId = this.termRule.termId;
      }
      if (sessionStorage.getItem('home.companyName') !== null) {
        this.companyName = sessionStorage.getItem('home.companyName');
      }
      if (sessionStorage.getItem('home.division') !== null) {
        this.division = sessionStorage.getItem('home.division');
      }
      if (sessionStorage.getItem('home.sector') !== null) {
        this.sector = sessionStorage.getItem('home.sector');
      }
      if (sessionStorage.getItem('home.divSector') !== null) {
        this.divSector = sessionStorage.getItem('home.divSector');
      }
      if (sessionStorage.getItem('home.filteredindustry') !== null) {
        this.filteredindustry = JSON.parse(sessionStorage.getItem('home.filteredindustry'));
      }
      if (sessionStorage.getItem('home.industry') !== null) {
        this.industry = sessionStorage.getItem('home.industry');
      }
      if (sessionStorage.getItem('home.filerCategory') !== null) {
        this.filerCategory = sessionStorage.getItem('home.filerCategory');
      }
      if (sessionStorage.getItem('home.divisionCount') !== null) {
        this.divisionCount = JSON.parse(sessionStorage.getItem('home.divisionCount'));
      }
      if (sessionStorage.getItem('home.industryCount') !== null) {
        this.industryCount = JSON.parse(sessionStorage.getItem('home.industryCount'));
      }
      if (sessionStorage.getItem('home.categoryCount') !== null) {
        this.categoryCount = JSON.parse(sessionStorage.getItem('home.categoryCount'));
      }
      if (sessionStorage.getItem('home.quaterly') !== null) {
        this.quaterly = JSON.parse(sessionStorage.getItem('home.quaterly'));
      }
      if (sessionStorage.getItem('home.termName') !== null) {
        this.termName = sessionStorage.getItem('home.termName');
      }
      if (sessionStorage.getItem('home.definition') !== null) {
        this.definition = sessionStorage.getItem('home.definition');
      }
      if (sessionStorage.getItem('home.termCode') !== null) {
        this.termCode = sessionStorage.getItem('home.termCode');
      }
      if (sessionStorage.getItem('home.incAccuTest') !== null) {
        this.incAccuTest = JSON.parse(sessionStorage.getItem('home.incAccuTest'));
      }
      if (sessionStorage.getItem('home.periodType') !== null) {
        this.periodType = JSON.parse(sessionStorage.getItem('home.periodType'));
      }
      if (sessionStorage.getItem('home.type') !== null) {
        this.type = JSON.parse(sessionStorage.getItem('home.type'));
      }
      if (sessionStorage.getItem('home.finStatement') !== null) {
        this.finStatement = JSON.parse(sessionStorage.getItem('home.finStatement'));
      }
      if (sessionStorage.getItem('home.priorgroup') !== null) {
        this.priorgroup = JSON.parse(sessionStorage.getItem('home.priorgroup'));
      }
      if (sessionStorage.getItem('home.processingOrder') !== null) {
        this.processingOrder = sessionStorage.getItem('home.processingOrder');
      }
      if (sessionStorage.getItem('home.range') !== null) {
        this.range = JSON.parse(sessionStorage.getItem('home.range'));
      }
      if (sessionStorage.getItem('home.expTableList') !== null) {
        this.expTableList = JSON.parse(sessionStorage.getItem('home.expTableList'));
      }
      if (sessionStorage.getItem('home.expLabel') !== null) {
        this.expLabel = sessionStorage.getItem('home.expLabel');
      }
      if (sessionStorage.getItem('home.expressionList') !== null) {
        this.expressionList = JSON.parse(sessionStorage.getItem('home.expressionList'));
      }
      if (sessionStorage.getItem('home.validationList') !== null) {
        this.validationList = JSON.parse(sessionStorage.getItem('home.validationList'));
      }
      if (sessionStorage.getItem('home.indOvrrideList') !== null) {
        this.indOvrrideList = JSON.parse(sessionStorage.getItem('home.indOvrrideList'));
      }
      if (sessionStorage.getItem('home.dervdZroExpList') !== null) {
        this.dervdZroExpList = JSON.parse(sessionStorage.getItem('home.dervdZroExpList'));
      }
      //if (sessionStorage.getItem('home.selectedTerm') !== null) {
      //  this.selectedTerm = JSON.parse(sessionStorage.getItem('home.selectedTerm'));
      //}
    } else {
      //no term node stored
    }
  }



  // This method gets all pre options on home page
  getTermRuleOptions() {
    this.trmRulFinStatmntList;
    this.getPriorityGrpList();
  }

  getUserRole() {
	  this.utilService.getUserRolesList().subscribe(
	      (response) => {
	        this.isAdminUser = response.includes('admin');
	      },
	      (error) => console.log(error)
	    )
	  }

  // This method populate termTree in home page
  getRuleList() {
		if (this.trmRulFinStatmntList && this.trmRulFinStatmntList.length > 0) {
      var found = false;
      for (var k = this.trmRulFinStatmntList.length-1; k > 0; k--) {
        var obj = this.trmRulFinStatmntList[k];
        var v = obj["financialStatement"];

        if (v == "Uncategorized") {
          found = true;
          break;
        }
      }
		  if (!found ) {
       this.trmRulFinStatmntList[this.trmRulFinStatmntList.length] = ({ "financialStatement": "Uncategorized" });
      }
		} else {
		  this.homeService.getTermRuleCategoryList().subscribe(
		      (response) => {
		        this.trmRulFinStatmntList = response;
		        this.trmRulFinStatmntList[this.trmRulFinStatmntList.length] = ({ "financialStatement": "Uncategorized" });
		      },
		      (error) => console.log(error)
	    	)
		}

		if (this.termRulesList) {
		    this.termRulesList = this.termRulesList.sort((a, b) => {
		          if (a.termId < b.termId) { return -1; }
		          if (a.termId > b.termId) { return 1; }
		          return 0;
	        });
	        for (var i = 0; i < this.trmRulFinStatmntList.length; i++) {
	          this.populateList = [];
	          for (var j = 0; j < this.termRulesList.length; j++) {
	            if (this.trmRulFinStatmntList[i].financialStatement === this.termRulesList[j].financialStatement) {
	              this.populateList.push(this.termRulesList[j]);
	            }
	          }
	          let home = new Home();
	          home.label = this.trmRulFinStatmntList[i].financialStatement;
	          this.populateList.forEach(obj => {
	            home.children.push({ "label": obj.termId + ' - ' + obj.name, "leaf": true, "key": obj.termId });
	            this.termRuleLists.push(obj.name);
	          }
	          )
	          home.leaf = false;
	          this.termTree.push(home);

	        }
		} else {
			this.utilService.getTermRuleList().subscribe(
		      	(response) => {
			        this.termRulesList = response;
			        this.termRulesList = this.termRulesList.sort((a, b) => {
			          if (a.termId < b.termId) { return -1; }
			          if (a.termId > b.termId) { return 1; }
			          return 0;
		        });
		        for (var i = 0; i < this.trmRulFinStatmntList.length; i++) {
		          this.populateList = [];
		          for (var j = 0; j < this.termRulesList.length; j++) {
		            if (this.trmRulFinStatmntList[i].financialStatement === this.termRulesList[j].financialStatement) {
		              this.populateList.push(this.termRulesList[j]);
		            }
		          }
		          let home = new Home();
		          home.label = this.trmRulFinStatmntList[i].financialStatement;
		          this.populateList.forEach(obj => {
		            home.children.push({ "label": obj.termId + ' - ' + obj.name, "leaf": true, "key": obj.termId });
		            this.termRuleLists.push(obj.name);
		          }
		          )
		          home.leaf = false;
		          this.termTree.push(home);
		        };
		      }
	    	)
		}

/*
    this.utilService.getTermRuleList().subscribe(
      (response) => {
        this.termRulesList = response;
        this.termRulesList = this.termRulesList.sort((a, b) => {
          if (a.termId < b.termId) { return -1; }
          if (a.termId > b.termId) { return 1; }
          return 0;
        });
        for (var i = 0; i < this.trmRulFinStatmntList.length; i++) {
          this.populateList = [];
          for (var j = 0; j < this.termRulesList.length; j++) {
            if (this.trmRulFinStatmntList[i].financialStatement === this.termRulesList[j].financialStatement) {
              this.populateList.push(this.termRulesList[j]);
            }
          }
          let home = new Home();
          home.label = this.trmRulFinStatmntList[i].financialStatement;
          this.populateList.forEach(obj => {
            home.children.push({ "label": obj.termId + ' - ' + obj.name, "leaf": true, "key": obj.termId });
            this.termRuleLists.push(obj.name);
          }
          )
          home.leaf = false;
          this.termTree.push(home);

        };
      }
    ) */
  }

  onCompanyNameChange($event) {
    console.log(this.companyName);
    sessionStorage.setItem('home.companyName', $event);
  }
  onTermNameChange($event) {
    sessionStorage.setItem('home.termName', $event);
  }
  onTermDefinitionChange($event) {
    sessionStorage.setItem('home.definition', $event);
  }
  termCodeChange($event) {
    console.log(this.termCode);
    if (this.termCode != "" && this.termCode) {
      this.enableTermButton = false;
      this.enableAddTermButton = true;
    } else {
      this.enableTermButton = true;
      this.enableAddTermButton = false;
    }
    sessionStorage.setItem('home.termCode', $event);
  }
  termTreeSelection($event) {
    this.processnow = false;
    this.indovrDrpList = [];
    this.selIndOvrdExpr = null;
    this.indOvrdExprDrop = false;
    this.enableAddTermButton = false;
    if ($event.node.key !== undefined) {
      console.log('selected: ' + $event.node.key);
      this.termId = $event.node.key;
      this.displayTerms = true;
      this.displayGetCoverage = false;
      this.enableTermButton = false;
      this.entitycols = this.home.homeEntityCols();
      this.expressionName = 'Expression'
      this.getTermBasedOnId($event.node.key);
      if (this.entityList) {
        this.showResolved(true);
      } else
        this.divisionCount = this.completeCompanyList.length;
      console.log(this.selectedTerm);

      sessionStorage.setItem('home.termTreeId', this.termId);
      //sessionStorage.setItem('home.selectedTerm', JSON.stringify(this.selectedTerm));
    }
  }

  getPriorityGrpList() {
    this.homeService.getTermRulesPriorityGroupList().subscribe(
      (response) => {
        this.trmRulPriorGrpList = response;
      }
    )
  }
  onTabChange(event) {
    this.index = event.index;
    this.expressionForEdit.type = this.expressionTypes[this.index];
  }

  onExpressionEdit(rowdata: any) {
    //this.reset();
    //this.expressionList = rowdata.expression;
    this.showValidation = false;
    this.validationMessage = '';
    if (this.expressionName === 'Expression') {
      this.expressionsEdit(rowdata);
    }
    if (this.expressionName === 'Validation') {
      this.showExpression = false;
      this.validationEdit(rowdata);
    }
    if (this.expressionName === 'Industry') {
      this.isOverrideExpressionForEdit = true;
      this.entityOverrides = [];
      this.addAnotherIndustryOverride();
      this.industryEdit(rowdata);
      this.expressionSets = [];
      this.formulaSets = [];
      this.AddExpressionSet();
      this.expressionsEdit(rowdata);
      this.editExpression = false;
    }
    if (this.expressionName === 'Derived') {
      this.derivedZeroExpressionForEdit = this.termRule.derivedZeroExpressions[rowdata.rank - 1];
      if (rowdata.type == 'NoteBlockExistsCheck') {
        this.derivedZeroExpressionForEdit.type = 'NoteBlockExistsCheck'
        this.activeExpressionTabIndex = 0;
      }
      if (rowdata.type == 'DependentTermCheck') {
        this.derivedZeroExpressionForEdit.type = 'DependentTermCheck'
        this.activeExpressionTabIndex = 1;
      }
      if (rowdata.type == 'ExclusiveTermCheck') {
        this.derivedZeroExpressionForEdit.type = 'ExclusiveTermCheck'
        this.activeExpressionTabIndex = 2;
      }
      if (rowdata.type == 'IndustryExclusionCheck') {
        this.derivedZeroExpressionForEdit.type = 'IndustryExclusionCheck'
        this.activeExpressionTabIndex = 3;
      }

      this.addEditDerived = true;
      this.prepareDerivedZeroExpressionForEdit();
    }
  }
  industryEdit(rowdata: any) {
    this.showValidation = false;
    this.validationMessage = '';
    this.addEditIndustry = true;
    this.editOverrideExpression(rowdata);
  }

  validationEdit(rowdata: any) {
    this.showValidation = false;
    this.validationMessage = '';
    this.addEditValidation = true;
    this.addvalidation = false;
    this.formulaFilteredTerms = this.termRuleLists;
    this.validationRank = rowdata.rank;
    if (rowdata.expression) {
      this.convertExpressionToFormula(rowdata.expression);
    } else {
      this.formulaSets = [];
      this.formulaOperatorsList;
      this.addAnotherFormula();
    }
  }
  getconceptNameDefinition() {
    if (this.concMatch.id) {
      this.utilService.getTaxonomyElement(this.concMatch.id).subscribe((response) => {
        this.conceptNameDefinition = response.elementDefaultLabel + "\n" + response.elementDefinitionUS;
      })
    }

  }

  expressionsEdit(rowdata: any) {
    this.showValidation = false;
    this.validationMessage = '';
    this.index = 0;
    this.conceptNameDefinition = "";
    this.conceptMatchMultipleList = [];
    this.conceptMatchMultiple = [];
    // this.addAnotherConcept();
    this.concMatch = {};
    //this.expressionForEdit =new EditExpression;
    this.expressionForEdit = rowdata;
    if (rowdata.type == 'ConceptMatch') {
      this.index = 0;
    }
    if (rowdata.type == 'ConceptMatchWithDim') {
      this.index = 1;
    }
    if (rowdata.type == 'ExtendedCheck') {
      this.index = 2;
    }
    if (rowdata.type == 'ExtendedCheckWithDim') {
      this.index = 3;
    }
    if (rowdata.type == 'Formula') {
      this.index = 4;
    }
    if (rowdata.type == 'ConceptMatchMultiple') {
      this.index = 5;
    }

    this.conceptMatchExpression(rowdata);
    this.conceptMatchWithDim(rowdata);
    this.editExtendedCheck(rowdata);
    this.editExtendedCheckWithDim(rowdata);
    this.editFormula(rowdata);
    this.editConceptMultiple(rowdata);
    this.editExpression = true;
  }
  reset() {
    this.concMatch = {};
    this.usePositiveFacts = false;
    this.useNegativeFacts = false;
    this.revNegativeFacts = false;
    this.axis = null
    this.virtualFactMemberExclusions = [];
    this.axes = null
    this.useMaxAxisCount = null;
    this.maxAxisCount = 0;
    this.expressionSets = [];
    this.nameContains = [];
    this.nameDoesNotContains = [];
    this.balType = null;
    this.perType = null;
    this.isShareItemType = false;
    this.divSector = null;
    this.industry = null;
    this.filerCategory = null;
    this.editExpression = false;
    this.axes = null;
    this.axesMembers = null;
    this.formulaSets = [];
    this.conceptMatchMultipleList = [];
    this.overrideName = null;
    this.mergeBaseExpressions = true;
    this.entityOverrides = [];
    this.showValidation = false;
    this.validationMessage = '';

  }

  conceptMatchExpression(rowdata: any) {
    if (rowdata.type === 'ConceptMatch') {
      if (rowdata.type === 'ConceptMatch') {
        this.concMatch = rowdata.expression.replace("{", "");
        this.concMatch = { 'id': this.concMatch.replace('}', "") };
        this.utilService.getTaxonomyElement(this.concMatch.id).subscribe((response) => {
          this.conceptNameDefinition = response.elementDefaultLabel + "\n" + response.elementDefinitionUS;
        });
      }
      this.usePositiveFacts = rowdata.usePositiveValuesOnly;
      this.useNegativeFacts = rowdata.useNegativeValuesOnly;
      this.revNegativeFacts = rowdata.reverseNegativeValues;
      this.axis = rowdata.virtualFactAxis;
      this.virtualFactMemberExclusions = rowdata.virtualFactMemberExclusionList;
    }
  }
  conceptMatchWithDim(rowdata: any) {
    if (rowdata.type === 'ConceptMatchWithDim') {
      if (rowdata.type === 'ConceptMatchWithDim') {
        this.concMatch = rowdata.expression.replace("{", "");
        this.concMatch = { 'id': this.concMatch.replace('}', "") };
        this.utilService.getTaxonomyElement(this.concMatch.id).subscribe((response) => {
          this.conceptNameDefinition = response.elementDefaultLabel + "\n" + response.elementDefinitionUS;
        });
      }
      this.useMaxAxisCount = rowdata.useMaxAxisCount;
      this.maxAxisCount = rowdata.maxAxisCount;
      if (!rowdata.dimensionExpressionSets && rowdata.dimensionExpressionSets == null) {
        this.expressionSets = [];
        this.AddExpressionSet();
      } else {
        //this.expressionSets = rowdata.dimensionExpressionSets;
        this.expressionSets = [];
        rowdata.dimensionExpressionSets.forEach(expSet => {
          expSet.axisInclusionListAsString = this.arrayToExpression(expSet.axisInclusionList, "\n", false, false);
          expSet.axisExclusionListAsString = this.arrayToExpression(expSet.axisExclusionList, "\n", false, false);
          expSet.memberInclusionListAsString = this.arrayToExpression(expSet.memberInclusionList, "\n", false, false);
          expSet.memberExclusionListAsString = this.arrayToExpression(expSet.memberExclusionList, "\n", false, false);
          if (expSet.memberInclusionListAsString || expSet.memberExclusionListAsString) {
            expSet.disableMember = true;
            expSet.disableMemInc = false;
          }
          if (expSet.axisExclusionListAsString || expSet.axisInclusionListAsString) {
            expSet.disableAxes = true;
            expSet.disableAxesInc = false;
          }
          this.expressionSets.push(expSet);
        })
      }
    }

  }
  editExtendedCheck(rowdata: any) {
    if (rowdata.type === 'ExtendedCheck') {
      this.nameContains = this.splitTextToArray(rowdata.containsWords, "\n")
      this.nameDoesNotContains = this.splitTextToArray(rowdata.doesNotContainsWords, "\n")
      this.balType = rowdata.balType;
      this.perType = rowdata.perType;
      this.isShareItemType = rowdata.isShareItemType;

    }

  }
  editExtendedCheckWithDim(rowdata: any) {
    if (rowdata.type === 'ExtendedCheckWithDim') {
      this.nameContains = this.splitTextToArray(rowdata.containsWords, "\n")
      this.nameDoesNotContains = this.splitTextToArray(rowdata.doesNotContainsWords, "\n")
      this.balType = rowdata.balType;
      this.perType = rowdata.perType;
      this.isShareItemType = rowdata.isShareItemType;
      this.axes = this.arrayToExpression(rowdata.NamedAxisList, "\n", false, false);
      this.axesMembers = this.arrayToExpression(rowdata.NamedMemberList, "\n", false, false);
    }
  }
  editFormula(rowdata: any) {
    this.formulaSets = [];
    this.formulaFilteredTerms = this.termRuleLists;
    if (rowdata.type === 'Formula') {

      if (rowdata.formulaList) {
        rowdata.formulaList.forEach(obj => {
          var formula = { termName: obj.termName, nullable: obj.nullable, operation: { name: obj.operation } };
          this.formulaSets.push(formula);
        })
      }
    } else {

      this.formulaOperatorsList;
      this.addAnotherFormula();
    }


  }
  editConceptMultiple(rowdata: any) {
    if (rowdata.type === 'ConceptMatchMultiple') {
      this.conceptMatchMultiple = [];
      this.conceptMatchMultipleList = [];
      if (!rowdata.conceptMatchMultipleList) {
        this.addAnotherConcept();
      }
      else {
        rowdata.conceptMatchMultipleList.forEach(obj => {
          this.conceptMatchMultipleList.push(obj);
          this.conceptMatchMultiple.push({ id: obj });
        })
      }
    }
  }
  addAnotherFormula() {
    var formula = { termName: "", nullable: false, operation: "" };
    this.formulaSets.push(formula);
  }

  AddExpressionSet() {
    var termExpressionSet: any = {};
    termExpressionSet.axisType = "ExactMatch";
    termExpressionSet.memberType = "ExactMatch";
    termExpressionSet.disableAxes = false;
    termExpressionSet.disableMember = false;
    termExpressionSet.disableAxesInc = true;
    termExpressionSet.disableMemInc = true;
    termExpressionSet.axisInclusionListAsString = "";
    termExpressionSet.axisExclusionListAsString = "";
    termExpressionSet.axisInclusionList = [];
    termExpressionSet.axisExclusionList = [];
    termExpressionSet.memberName = "";
    termExpressionSet.axisName = "";
    termExpressionSet.memberInclusionListAsString = "";
    termExpressionSet.memberExclusionListAsString = "";
    termExpressionSet.memberInclusionList = [];
    termExpressionSet.memberExclusionList = [];
    this.expressionSets.push(termExpressionSet);
  }
  removeSet(index) {
    this.expressionSets.splice(index, 1);
  };

  addAnotherConcept() {

    if (!this.conceptMatchMultipleList) {
      this.conceptMatchMultipleList = [];
    }
    var conceptExpression = "";
    this.conceptMatchMultipleList.push(conceptExpression);
  };
  deleteConceptMatchMultipleItem(index) {
    this.conceptMatchMultipleList.splice(index, 1);
  };

  getTermBasedOnId(id: string) {
    this.showError = false;
    this.homeService.getTermRule(id).subscribe(
      (response) => {
        this.termRule = response;
        this.definition = response.description
        this.termCode = response.termId;
        if (this.termCode != "") {
          this.enableTermCode = true;
        } else {
          this.enableTermCode = false;
        }
        this.termName = response.name;
        this.periodType = { name: response.periodType };
        this.type = { name: response.type };
        this.incAccuTest = response.includeInAccuracyTests;
        this.finStatement = { financialStatement: response.financialStatement };
        this.priorgroup = { name: response.priorityGroup };
        this.processingOrder = response.order;

        this.expTableList = response.expressions;
        this.expLabel = 'Add Expression';
        this.expressionList = []
        this.expressionList = response.expressions;
        this.validationList = [];
        if (response.validationExpressions)
          response.validationExpressions.forEach(obj => {
            obj.type = 'V';
            this.validationList.push(obj);
          })
        // this.validationList = response.validationExpressions;
        this.indOvrrideList = [];
        this.indOvrrideList = response.overrides;
        this.dervdZroExpList = [];
        this.dervdZroExpList = response.derivedZeroExpressions;

        this.showTermResultsWindow = [];
        this.showTermResultsWindowMinimize = [];

        sessionStorage.setItem('home.termCode', this.termCode);
        sessionStorage.setItem('home.termName', this.termName);
        sessionStorage.setItem('home.definition', this.definition);
        sessionStorage.setItem('home.periodType', JSON.stringify(this.periodType));
        sessionStorage.setItem('home.type', JSON.stringify(this.type));
        sessionStorage.setItem('home.incAccuTest', JSON.stringify(this.incAccuTest));
        sessionStorage.setItem('home.finStatement', JSON.stringify(this.finStatement));
        sessionStorage.setItem('home.priorgroup', JSON.stringify(this.priorgroup));
        sessionStorage.setItem('home.processingOrder', this.processingOrder);

        sessionStorage.setItem('home.expTableList', JSON.stringify(this.expTableList));
        sessionStorage.setItem('home.expLabel', this.expLabel);
        sessionStorage.setItem('home.expressionList', JSON.stringify(this.expressionList));
        sessionStorage.setItem('home.validationList', JSON.stringify(this.validationList));
        sessionStorage.setItem('home.indOvrrideList', JSON.stringify(this.indOvrrideList));
        sessionStorage.setItem('home.dervdZroExpList', JSON.stringify(this.dervdZroExpList));
		sessionStorage.setItem('home.termRule', JSON.stringify(this.termRule));
      }
    )
    sessionStorage.setItem('home.processingOrder', this.processingOrder);
  }

  onIncAccuTest($event){
    sessionStorage.setItem('home.incAccuTest', JSON.stringify($event));
  }
  onPeriodTypeChange($event){
    sessionStorage.setItem('home.periodType', JSON.stringify($event.value));
  }
  onTypeChange($event){
    sessionStorage.setItem('home.type', JSON.stringify($event.value));
  }
  onFinStatemenChange($event){
    sessionStorage.setItem('home.finStatement', JSON.stringify($event.value));
  }
  onPriorgroupChange($event){
    sessionStorage.setItem('home.priorgroup', JSON.stringify($event.value));
  }

  onExpressionButtonClick(type: string) {
    this.indovrDrpList = [];
    this.indOvrdExprDrop = false;
    this.selIndOvrdExpr = null;
    this.showValidation = false;
    this.validationMessage = '';
    if (type == 'expressions') {
      this.expTableList = this.expressionList;
      this.expLabel = 'Add Expression';
      this.expressionName = 'Expression'
      return;
    }
    if (type == 'validations') {
      this.expTableList = this.validationList;
      this.expressionName = 'Validation'
      this.expLabel = 'Add Validations';
      return;
    }
    if (type == 'industry') {
      this.expTableList = [];
      if (this.indOvrrideList != null && this.indOvrrideList[0] != null) {
        this.indOvrrideList.forEach(obj => {
          this.expTableList = this.indOvrrideList[0].expressions;
          this.indovrDrpList.push({ "name": obj.name });
        })
        this.selIndOvrdExpr = this.indovrDrpList[0].name;
        this.indOvrdExprDrop = true;
      }
      this.expressionName = 'Industry'
      this.expLabel = 'Add Industry Override Expressions';
      return;
    }
    if (type == 'derived') {
      this.expTableList = this.dervdZroExpList;
      this.expressionName = 'Derived'
      this.expLabel = 'Add Derived Zero Expressions';
      return;
    }
  }
  industryChange() {
    this.selIndOvrdExpr;
    this.indOvrrideList.forEach(obj => {
      if (this.selIndOvrdExpr.name == obj.name) {
        this.expTableList = obj.expressions;
      }
    })
  }

  expressionClick() {
    this.reset();
    this.showValidation = false;
    this.validationMessage = '';
    this.validationMessage = '';
    this.showValidation = false;
    if (this.expLabel === 'Add Expression') {
      this.expressionName = 'Expression'
      this.addingExpression();
    }
    if (this.expLabel === 'Add Validations') {
      this.expressionName = 'Validation'
      this.addValidations();
    }
    if (this.expLabel === 'Add Industry Override Expressions') {
      this.expressionName = 'Industry'
      this.entityOverrides = [];
      this.addAnotherIndustryOverride();
      this.addOverrideExpression();
      this.addEditIndustry = true;
      this.editExpression = false;

    }
    if (this.expLabel === 'Add Derived Zero Expressions') {
      this.expressionName = 'Derived'
      this.addDerivedZeroExpression();
    }
  }
  deleteFormula(index) {
    this.formulaSets.splice(index, 1);
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
      this.industry = peer[0].sic + '-' + peer[0].industry;
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
  searchCompany(event) {
    this.filteredCompany = [];
    this.toHighlight = event.query;
    for (let i = 0; i < this.companyList.length; i++) {
      let cname: string = this.companyList[i];
      if ((cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) || (cname != null && cname.toLowerCase().includes(event.query.toLowerCase()))) {
        this.filteredCompany.push(cname);
      }
    }
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
    this.toHighlight = event.query;
    for (let i = 0; i < this.divSectorList.length; i++) {
      let cname: string = this.divSectorList[i];
      if (cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.filtereddivSector.push(cname);
      }
    }
  }

  derivedDivisionSeleted(event) {
    this.deriveddivSector;
    console.log(event.value);
    if (event.value) {
      var division = event.value.substr(0, event.value.indexOf('->') - 1);
      var sector = event.value.substr(event.value.indexOf('->') + 3, event.value.length);
      this.setExcDivSec(division, sector);
    }
  }

  industryChanged(event) {
    console.log(event.value);
    var sic = event.value.substr(0, 4)
    var completeEntities = this.completeEntities.filter(obj => obj.sic === sic);
    this.industryCount = completeEntities.length
    this.categoryCount = completeEntities.length;
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

  quaterlyChange($event){
    this.quaterly = $event;
    sessionStorage.setItem('home.quaterly', JSON.stringify(this.quaterly));
  }

  searchTermNames(event) {
    this.formulaFilteredTerms = [];
    this.toHighlight = event.query;
    for (let i = 0; i < this.termRuleLists.length; i++) {
      let cname: string = this.termRuleLists[i];
      if (cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.formulaFilteredTerms.push(cname);
      }
    }
  }

  resetCoverage() {
    this.companyName = undefined;
    this.divSector = undefined;
    this.filterRadio = undefined;
    this.division = undefined;
    this.sector = undefined;
    this.industry = undefined;
    this.resolvedRanks = undefined;
    this.unResolvedRanks = undefined;
    this.divisionCount = this.completeCompanyList.length;
    this.industryCount = null;
    this.categoryCount = null;
    this.startYear = (new Date()).getFullYear() - 4;
    this.endYear = (new Date()).getFullYear();
    this.filerCategory = undefined;
    this.entityList = [];
    this.showTermResultsWindow = [];
    this.showTermResultsWindowMinimize = [];
    this.state = [];
    this.chart = {};
    this.errorMsg = '';
    this.showError = false;

    sessionStorage.removeItem('home.companyName');
    sessionStorage.removeItem('home.divSector');
    sessionStorage.removeItem('home.division');
    sessionStorage.removeItem('home.industry');
    sessionStorage.removeItem('home.sector');
    sessionStorage.removeItem('home.filerCategory');
    sessionStorage.removeItem('home.divisionCount');
    sessionStorage.removeItem('home.industryCount');
    sessionStorage.removeItem('home.categoryCount');

  }
  showResolved(isShow) {
    this.showTermResultsWindow = [];
    this.showTermResultsWindowMinimize = [];
    this.entityList =[];
    this.entitycols  = this.home.homeEntityCols();
    let mp = new MappedEntity();
    let url = '/api/UnMappedEntitiesForCriteria';
    if (isShow) {
      url = '/api/MappedEntitiesForCriteria';
    } else {
      this.selectResolvedRank = undefined;
    }

    //url = '/api/UnMappedEntitiesForCriteria';
    mp.termRuleId = this.termId;
    mp.minYear = this.startYear;
    mp.maxYear = this.endYear;
    this.showError = false;
    var selectionData = this.companyName
    if (this.filterRadio !== 'group' && selectionData != undefined && selectionData.length > 15) {
      selectionData = selectionData.substring((selectionData.indexOf("(") + 1), selectionData.indexOf(")"))
      var entities = this.completeCompanyList.filter(obj => obj.entityId === selectionData);
      var company = entities.length == 1 ? entities[0] : null;
      if (!company) {
        this.errorMsg = 'Please select a company, division->Sector or Filer Category before getting coverage statistics.';
        this.showError = true;
        window.scroll(0, 0);
        return;
      } else {
        mp.entityId = company.entityId;
      }
    } if (this.filterRadio === 'group' && (this.divSector === undefined)) {
      this.errorMsg = 'Please select a company, division->Sector or Filer Category before getting coverage statistics.';
      this.showError = true;
      window.scroll(0, 0);
      return;
    }
    this.displayGetCoverage = true;
    if ((this.companyName == undefined || this.companyName == null) && this.divSector != undefined) {
      this.filterRadio = 'group';
    }
    if (this.filterRadio === 'group' && this.division != undefined && this.sector != undefined) {
      mp.division = this.division;
      mp.sector = this.sector;
    } if (this.filterRadio === 'group' && this.divSector != '' && this.divSector != undefined) {
      mp.division = this.divSector.substr(0, this.divSector.indexOf('->') - 1);
      mp.sector = this.divSector.substr(this.divSector.indexOf('->') + 3, this.divSector.length);
      mp.entityId = 'NULL';

      mp.filerCategory = this.filerCategory;
      mp.rankId = 0;

    } else {

      mp.division = "settoinvalid";
      mp.sector = "settoinvalid";
    } if (this.industry != undefined) {
      mp.sic = this.industry.substr(0, 4);
      mp.includeQuarterly = true;
    } else {
      mp.sic = 'NULL';
      mp.filerCategory = 'NULL';
      mp.includeQuarterly = this.quaterly;
    }
    if (this.selectResolvedRank) {
      mp.rankId = Number(this.selectResolvedRank);
    }
    mp.includeQuarterly = this.quaterly;

    this.homeService.getTermResults(this.termId, selectionData).subscribe(
      (data) => {
        data.forEach(obj => {
          if (obj.FQ == 'FY') {
            this.percent += 25;
          }
        });
        if (this.percent > 100) {
          this.percent = 100;
        }
        var percent = this.percent;
      }
    )
    this.homeService.getMappedEntityList(mp, this.chartUrl).subscribe(
      (response) => {
        this.chartData = [];
        this.resolvedRanks = [{ label: 'Show Resolved By Rank', value: null }];
        this.unResolvedRanks = [{ label: 'Show UnResolved', value: null }];
        // let ranks = {};
        let unresolved = { label: 'Unresolved', value: response.unMappedEntityCount };
        this.chartData.push(unresolved);
        response.resultsByRank.forEach(obj => {
          // var name = { label: obj, value: obj };
          // ranks = { label: 'Rank ' + obj.rankId + ' (' + obj.nonDistinctMappedEntityCount + ')', value: obj.mappedEntityCount };
          // const name = { label: obj, value: obj };

          const ranks = { label: 'Rank ' + obj.rankId, value: obj.mappedEntityCount };
          const chart = { label: 'Rank ' + obj.rankId + ' (' + obj.mappedEntityCount + ') ' + (obj.mappedEntityCount / response.totalEntityCount * 100).toFixed(2) + '%', value: obj.rankId };
          this.resolvedRanks.push(chart);
          if (ranks.value != 0) {
            this.chartData.push(ranks);
          }

        })
        var percent = this.percent;
        this.totalEntityCount = response.totalEntityCount;
        this.getChartData(this.chartData);
      })
    this.homeService.getMappedEntityList(mp, url).subscribe(
      (response) => {
        this.entityList = response;
        if (this.entityList.length > 0) {
          this.entityList.forEach(() => {
            this.showTermResultsWindow.push(false);
            this.showTermResultsWindowMinimize.push(false);
            this.state.push({});
            this.minimizeArray.push(null);
          });
        } else {
          this.showTermResultsWindow = [];
          this.showTermResultsWindowMinimize = [];
          this.state = [];
        }if(!isShow) {
          this.resolvedRanks = this.unResolvedRanks;
          this.entitycols.splice(2,1);
        }
          this.entityList.forEach(obj => {
            obj.show = true;
          })
        // }
      })
  }

  showNotResolved() {
    this.displayGetCoverage = true;
  }

  getChartData(data: any) {
    this.chart = data;
  }
  getFormulaSuffix(formula) {

    if (formula.nullable && formula.nullable === true) {
      return "=0 " + formula.operation;
    }
    return formula.operation;
  }

  showTermResultnavigate(event, data) {
    console.log(data)

    if (data !== undefined) {
      console.log('selected: ' + data);
      event.preventDefault();
      var term = this.getTermIdFromTermName(data)
      this.termId = term;
      this.displayTerms = true;
      this.displayGetCoverage = false;
      this.entitycols = this.home.homeEntityCols();
      this.getTermBasedOnId(term);
      console.log(term)
    }
  }

  showTermResults(data) {
    const index = this.entityList.findIndex(item => (item.entityId === data.entityId) && (item.companyName === data.companyName));
    this.state[index] = {
      termName: this.termCode,
      entityId: data.entityId,
      companyName: data.companyName
    };
    this.showTermResultsWindow[index] = true;
    this.showTermResultsWindowMinimize[index] = false;
  }

  openFSQVLink(data) {
    let linkUrl: string = "https://sp-us-deraodp01.ix.sec.gov:18443/fsqv-solr/filingSearchSolr?refresh=1&cik=";
    let cik: string = data.cik;
    this.utilService.getProfileId().subscribe((response) => {
      if (response) {
        let profileId: string = response.text();
        if ( profileId === 'ci') {
          linkUrl = "https://sp-us-deraodp01.ix.sec.gov:18443/fsqv-solr/filingSearchSolr?refresh=1&cik=" + cik;
        } else if ( profileId === 'prod' ) {
          linkUrl = "https://md-up-webidap.ix.sec.gov:18443/fsqv-solr/filingSearchSolr?refresh=1&cik=" + cik;
        }
        window.open(linkUrl, "_blank");
      }
    });
  }

  getTermIdOrEntityName(inputValue: string): string {
    let outputValue: string = inputValue.substring((inputValue.indexOf("(") + 1), inputValue.indexOf(")"));
    return outputValue;
  }

  getTermIdFromTermName(termName) {

    for (var i = 0; i < this.termRulesList.length; i++) {
      var termRule = this.termRulesList[i];
      if (termRule.name === termName) {
        return termRule.termId;
      }

    }
    return "LSE";
  }
  ngOnDestroy() {
    this.subscription.forEach(s => s.unsubscribe());
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  moveRankBy(index, amount) {
    this.msgs = [];
    let e1 = Object.assign(this.expressionList[index])
    let e2 = Object.assign(this.expressionList[index + amount])


    console.log("e1:" + e1);
    console.log("e2:" + e2);

    console.log(e1.rank + " will be moved to " + e2.rank);
    e2.rank = index + 1;
    e1.rank = index + amount + 1;
    this.expressionList[index] = e2;
    this.expressionList[index + amount] = e1;

    //utilService.saveTerm("Expression Rank " + e2.rank + " moved to " + e1.rank);
    // updateExpressionTableParams(rule);

  };

  addTerm() {
    this.showError = false;
    this.msgs = [];
    this.refresh();
    this.newTerm = true;
    this.definition = '';
    this.termCode = '';
    this.termName = '';
    this.periodType = { name: '' };
    this.type = { name: '' };
    this.incAccuTest = false;
    this.finStatement = { financialStatement: '' };
    this.priorgroup = { name: '' };
    this.processingOrder = '';
    this.termRule = this.rule;
    this.expressionList = [];
    this.validationList = [];
    this.indOvrrideList = [];
    this.dervdZroExpList = [];
    this.expTableList = [];
    this.displayTerms = false;

  }
  processTermnow() {
    this.refresh();
    this.enableTermButton = false;
    this.enableTermCode = true;
    this.processnow = true;
    if (this.divisionCount === 0) {
      this.divisionCount = this.completeCompanyList.length;
    }
    this.enableTermCode = true;
    this.displayTerms = true;
    this.displayGetCoverage = true;
    if (this.termCode != '') {
      this.enableTermButton = false;
    }

  }

  divSectorChange(event) {
    this.divSector = event;
    this.completeEntities = this.completeCompanyList;
    var sicCodes: any[] = [];
    // event = event.value ? event.value : event;
    var division = event.substr(0, event.indexOf('->') - 1);
    var sector = event.substr(event.indexOf('->') + 3, event.length);
    this.filerCategory = undefined;
    this.industry = undefined;
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
    this.categoryCount = this.completeEntities.length;

    //sessionStorage.setItem('home.completeEntities', JSON.stringify(this.completeEntities));
    sessionStorage.setItem('home.filteredindustry', JSON.stringify(this.filteredindustry));
    sessionStorage.setItem('home.division', this.division);
    sessionStorage.setItem('home.sector', this.sector);
    sessionStorage.setItem('home.divSector', this.divSector);
    sessionStorage.setItem('home.divisionCount', JSON.stringify(this.divisionCount));
    sessionStorage.setItem('home.industryCount', JSON.stringify(this.industryCount));
    sessionStorage.setItem('home.categoryCount', JSON.stringify(this.categoryCount));

  }
  processIndustryChange(event) {
    // event = event.value ? event.value : null;
    if (event != null) {
      this.industry = event;
      this.filerCategory = undefined;
      event = event.substring(0, 4);
      var completeEntities = this.completeEntities.filter(obj => obj.sic === event);
      this.industryCount = completeEntities.length;
      this.categoryCount = completeEntities.length;
    } else {
      this.industryCount = this.divisionCount;
      this.categoryCount = this.divisionCount;
    }
    sessionStorage.setItem('home.industry', this.industry);
    //sessionStorage.setItem('home.completeEntities', JSON.stringify(this.completeEntities));
    sessionStorage.setItem('home.industryCount', JSON.stringify(this.industryCount));
    sessionStorage.setItem('home.categoryCount', JSON.stringify(this.categoryCount));
  }

  processfilCatChange(event) {
    // event = event.value ? event.value : null;
    if (event != null) {
      this.filerCategory = event;
      var completeEntities = this.completeEntities.filter(obj =>
        obj.filerCategory === event && obj.sic === this.industry.substr(0, 4));
      this.categoryCount = completeEntities.length;
    } else {
      this.categoryCount = null;
    }
    sessionStorage.setItem('home.filerCategory', this.filerCategory);
    //sessionStorage.setItem('home.completeEntities', JSON.stringify(this.completeEntities));
    sessionStorage.setItem('home.categoryCount', JSON.stringify(this.categoryCount));
  }


  processNow() {
    this.refresh();
    this.enableTermButton = false;
    this.enableTermCode = true;
    if (this.companyName && this.divSector && this.division && this.sector) {
      this.processTermRule();
      window.scroll(0, 0);
      return;
    } else {

      this.confirmationService.confirm({
        message: 'Are you sure you want to process all entities for the selected term?',
        header: 'Processing Term Rule ' + this.termId,
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
          //this.msgs = [{severity:'info', summary:'Process Term', detail:'Process Term'}];
          this.processTermRule();

        },
        reject: () => {
          //this.msgs = [{severity:'info', summary:'Rejected', detail:'You have rejected'}];
        }
      });
    }
    window.scroll(0, 0);
  }

  removeTerm() {
    this.refresh();
    this.enableTermButton = false;
    this.enableTermCode = true;
    this.confirmationService.confirm({
      message: 'Term Rule will be removed, are you sure?',
      header: 'Remove Rule ' + this.termCode,
      icon: 'pi pi-exclamation-triangle',
      accept: () => {

        this.utilService.removeTerm(this.termCode).subscribe((response) => {
          if (response.errorMessage) {
            this.msgs = [{ severity: 'info', summary: 'Confirmed', detail: response.errorMessage }];
          } else {
            this.msgs = [{ severity: 'info', summary: '', detail: 'Term Rule Removed' }];
		  	console.log("Remove Term  completed ")
          	this.selectedTerm = null;
          	this.termTree = [];
		  	    sessionStorage.removeItem('home.termRule');
            this.termRulesList = null;
            this.reloadTermFlag = true;
            this.ngOnInit();
            //this.getRuleList();
          }
        },
          (error) => console.log(error)
        )
      },
      reject: () => {
        //this.msgs = [{severity:'info', summary:'Rejected', detail:'You have rejected'}];
      }
    });
  }

  saveTerm() {
    this.showError = false;
    if (!this.newTerm && this.termRule.termId != '') {
      this.termRule.expressions = this.expressionList;
    }
    this.termRule.termId = this.termCode;
    this.termRule.id = this.termCode;
    this.termRule.name = this.termName
    this.termRule.description = this.definition;
    this.termRule.includeInAccuracyTests = this.incAccuTest;
    if (this.periodType.name === '') {
      this.termRule.periodType = null;
    } else {
      this.termRule.periodType = this.periodType.name;

    }
    if (this.type.name === '') {
      this.termRule.type = null;
    } else {
      this.termRule.type = this.type.name;

    }
    if (this.finStatement.financialStatement === '') {
      this.termRule.financialStatement = null;
    } else {
      this.termRule.financialStatement = this.finStatement.financialStatement;

    }
    if (this.priorgroup.name === '') {
      this.termRule.priorityGroup = null;
    } else {
      this.termRule.priorityGroup = this.priorgroup.name;

    }

    this.utilService.saveTerm(this.termRule).subscribe((response) => {
      console.log("Save Term  completed ")
      this.msgs = [{ severity: 'info', summary: 'Term', detail: this.termRule.termId + ' saved successfully' }];
      this.termTree = [];
      if(this.expressionName === 'Expression') {
        this.getTermBasedOnId(this.termRule.termId);
      }
	    sessionStorage.setItem('home.termRule', JSON.stringify(this.termRule));
      sessionStorage.setItem('home.termTreeId', this.termRule.termId);

      this.termRulesList = null;
      this.reloadTermFlag = true;
     // this.getRuleList();
	    this.ngOnInit();
      //return;
    },
      (error) => {
        console.log(error)
        this.msgs = [{ severity: 'danger', summary: 'Term Rule Save Failed' }];
        return;
      }
    )


  }

  refresh() {
    this.processnow = false;
    this.msgs = [];
    // this.displayTerms = false;
    // this.displayGetCoverage = false;
    this.showError = false;
    this.enableTermButton = true;
    this.enableTermCode = false;

  }
  processTermRule() {
    console.log("process Rule : " + JSON.stringify(this.termId));
    this.progressSpinner = true;
    this.msgs = [{ severity: 'info', summary: 'Started processing for ' + this.termName, detail: '' }];
    if (this.divSector && this.filerCategory && this.industry) {
      var sic = "NULL";
      if (this.industry) {
        sic = this.industry.substr(0, this.industry.indexOf('-'));
      }
      var filerCategory = "NULL";
      if (this.filerCategory) {
        filerCategory = this.filerCategory;
      }
      var division = "NULL";
      var sector = "NULL";
      if (this.divSector) {
        division = this.divSector.substr(0, this.divSector.indexOf('->') - 1);
        sector = this.divSector.substr(this.divSector.indexOf('->') + 3, this.divSector.length);

      }
      this.utilService.processTermForCriteria(this.termCode, division, sector, sic, filerCategory).subscribe((response) => {
        if (!response.data) {
          window.scroll(0, 0);
          this.msgs = [{ severity: 'info', summary: 'Term processing for' + this.termName, detail: ' processed successfully' }];
          this.progressSpinner = false;

        } else {
          window.scroll(0, 0);
          this.msgs = [{ severity: 'info', summary: 'Process Term Rule failed' + this.termName, detail: '' }];
          this.progressSpinner = false;

        }
      }, function (err) {
      }
      );
    } else {
      if (this.isAdminUser) {
        //we can allow admin users to process all entities for a given term....
        this.processTermRuleAdmin();
        return;
      } else {
        window.scroll(0, 0);
        this.msgs = [{ severity: 'info', summary: 'Please select a division->Sector or Filer Category before processing Term Rule.', detail: '' }];
        this.progressSpinner = false;
        return;
      }
    }

    // adminProcessAllTermsClicked('#processTermForCriteria');


  };

  processTermRuleAdmin() {
    this.utilService.processTerm(this.termRule).subscribe((response) => {
      if (!response.data) {
        window.scroll(0, 0);
        this.msgs = [{ severity: 'info', summary: 'Term processing for ' + this.termName, detail: 'processed successfully' }];
        this.progressSpinner = false;
      } else {
        window.scroll(0, 0);
        this.msgs = [{ severity: 'info', summary: 'Process Term Rule failed for ' + this.termName, detail: ' ' }];
        this.progressSpinner = false;
      }
    }, function (err) {
    }
    );

  }

  filterConceptMatch(event) {
    this.toHighlight = event.query;
    this.conceptMatch(event.query)
  }

  conceptMatch(q: string) {
    this.utilService.getTaxonomyElements(q, false).subscribe((response) => {
      this.filteredconcMat = response;
    })
  }

  OnExpressionAxisTypeChanged(index) {
    var expSet = this.expressionSets[index];
    if (this.expressionSets[index].axisType === "ExactMatch") {
      this.expressionSets[index].axisInclusionListAsString = "";
      this.expressionSets[index].axisExclusionListAsString = "";
      this.expressionSets[index].axisInclusionList = [];
      this.expressionSets[index].axisExclusionList = [];
    } else {
      this.expressionSets[index].axisName = "";
    }
    //this.expressionSets[index] = expSet;
  };

  OnExpressionMemberTypeChanged(index) {

    var expSet = this.expressionSets[index];

    if (this.expressionSets[index].memberType === "Any") {
      this.expressionSets[index].memberName = "";
      this.expressionSets[index].memberInclusionListAsString = "";
      this.expressionSets[index].memberExclusionListAsString = "";
      this.expressionSets[index].memberInclusionList = [];
      this.expressionSets[index].memberExclusionList = [];
    } else if (this.expressionSets[index].memberType === "ExactMatch") {
      this.expressionSets[index].memberInclusionListAsString = "";
      this.expressionSets[index].memberExclusionListAsString = "";
      this.expressionSets[index].memberInclusionList = [];
      this.expressionSets[index].memberExclusionList = [];
    } else {
      this.expressionSets[index].memberName = "";
    }
  };

  buildListFromString(listAsString) {
    return this.splitTextToArray(listAsString, "\n");
  };

  splitTextToArray(textData, spliter) {
    //console.log("TextArea to array : " + textData + ", splitter : " + spliter);
    var strArray = [];
    if (!textData) {
      return strArray;
    }
    strArray = textData.toString().split(spliter);
    //console.log("TextArea to array : result array: " + JSON.stringify(strArray));
    return strArray;
  }

  updateNameContains(nameContains) {
    this.containsWords = this.splitTextToArray(nameContains, "\n");

  };

  updateNameNotContains(nameNotContains) {
    this.doesNotContainsWords = this.splitTextToArray(nameNotContains, "\n");
  };

  resolve() {
    // this.updateExpressionForEditFromTextArea();
    var expressionObject: any = {};
    expressionObject.type = this.expressionForEdit.type;
    switch (this.expressionForEdit.type) {
      case this.expressionTypes[2]:
        {
          expressionObject.expression = this.getExtendedCheckExpression(this.expressionForEdit);

          break;
        }
      case this.expressionTypes[3]:
        {
          //extended check with dimension
          expressionObject.expression = this.getExtendedCheckExpression(this.expressionForEdit);
          var dim = this.getDimensionExpression(this.expressionForEdit);
          expressionObject.axisExpression = dim.axisExpression;
          expressionObject.memberExpression = dim.memberExpression;
          break;
        }
    };

    //console.log("resolve - : " + JSON.stringify( expressionObject));

    this.getExtendedEntityLookup(expressionObject);
  };

  getExtendedEntityLookup(expression) {
    //console.log("extended entity lookup call with : " + JSON.stringify(companiesFilter) + ", expression:" + JSON.stringify(expression) + ",TOKEN: " + this.csrfToken);

    var query: any = {};
    query.sic = this.industry.split('-')[0];
    query.division = this.divSector.split(' -> ')[0];
    query.sector = this.divSector.split(' -> ')[1];
    query.filerCategory = this.filerCategory;
    query.expression = expression;

    this.utilService.extendedEntityLookup(query).subscribe((response) => {
      this.extendedEntitiesLookup = response.data
    })
  };

  updateAxes(axes) {
    //console.log("updateAxes: " + this.axes + " - " + axes);
    this.namedAxisList = this.splitTextToArray(axes, '\n');
  };

  updateAxesMembers(axesMembers) {
    this.namedMemberList = this.splitTextToArray(axesMembers, "\n");
  };


  arrayToExpression(arrayData, splitter, prefix, suffix) {
    if (!prefix) {
      prefix = "";
    }
    if (!suffix) {
      suffix = "";
    }
    //console.log("arrayToExpression with spliter: " + splitter + ",prefix: " + prefix + ", suffix: " + suffix);
    var textData: any[];
    if (!arrayData) {
      return textData;
    }
    for (var i = 0; i < arrayData.length; i++) {
      //
      var elem = arrayData[i].trim();
      //console.log("word from array : " + elem);
      if (elem.length > 0) {
        if (i === 0) {
          textData = this.surroundTextWith(elem, prefix, suffix, false);
        } else {
          textData = textData + splitter + this.surroundTextWith(elem, prefix, suffix, false);
        }
      }

    }

    //console.log("arrayToExpression converted expression : " + textData);
    return textData;
  };

  surroundTextWith(str, prefix, suffix, check) {
    //console.log("surround text " + str + " with " + prefix + " and "+ suffix);
    if (!check) {
      str = prefix + str + suffix;
    } else {
      var patt1 = new RegExp("^" + prefix);
      var patt2 = new RegExp(suffix + "$");
      if (!patt1.test(str)) {
        str = prefix + str;
      }
      if (!patt2.test(str)) {
        str = str + suffix;
      }
    }

    return str;
  };

  addingExpression() {
    this.expressionForEdit = {};
    this.conceptMatchMultipleList = [];
    this.conceptMatchMultiple = [];
    this.concMatch = {};
    this.index = 0;
    this.isOverrideExpressionForEdit = false;
    this.expressionForEdit.expression = "";

    // this.expressionForEdit.type = this.expressionTypes[0];
    this.expressionForEdit.type = this.expressionTypes[this.index];
    this.expressionForEdit.NamedAxisList = [];
    this.expressionForEdit.useVirtualParentNew = true;
    this.expressionForEdit.useMaxAxisCount = false;
    this.expressionForEdit.maxAxisCount = 2;
    this.expressionForEdit.NamedMemberList = [];
    this.expressionForEdit.dimensionExpressionSets = [];
    this.expressionForEdit.virtualFactMemberExclusionList = [];
    this.expressionForEdit.containsWords = [];
    this.expressionForEdit.doesNotContainsWords = [];
    this.conceptNameDefinition = "";
    this.formulaSets = [];
    this.expressionSets = [];
    this.AddExpressionSet();
    this.addAnotherConcept();
    this.addAnotherFormula();
    this.editExpression = true;
  };

  saveExpression() {
    this.saveValues(this.expressionForEdit);
    this.validationMessage = null;
    this.showValidation = false;
    if (!!this.formulaSets) {
      for (var i = 0; i < this.formulaSets.length; i++) {
        var formula = this.formulaSets[i];
        if (this.formulaSets.length == 1 && formula.termName == "" && formula.nullable == false && formula.operation == "") {
          break;
        }
        if (!formula.termName) {
          this.validationMessage = 'Incomplete Formula", "Please complete Term Rule selection';
          this.showValidation = true;
          window.scroll(0, 0);
          return;
        } else {
          if (i !== (this.formulaSets.length - 1) && !formula.operation) {
            this.validationMessage = 'Incomplete Formula", "Please complete missing operation';
            this.showValidation = true;
            window.scroll(0, 0);
            return;
          }
        }
      }
    }

    if (this.expressionForEdit.type === this.expressionTypes[1]) {
      if (this.validateDimensionalExpressions() === false)
        return;
    }

    if (this.isOverrideExpressionForEdit) {
      var overrideName = this.overrideName.name ? this.overrideName.name : this.overrideName;
      if (!overrideName) {
        this.validationMessage = 'Incomplete override information, Please provide an override name for the expression being added.';
        this.showValidation = true;
        this.addEditIndustry = true;
      }
      //verify that override name is populated...
    }
    switch (this.expressionForEdit.type) {

      case this.expressionTypes[0]:
        {
          this.expressionForEdit.expression = this.getExpression();
          this.expressionForEdit.conceptName = this.expressionForEdit.expression;
          this.expressionForEdit.dimensionExpressionSets = null;
          this.expressionSets = null;
          break;
        }
      case this.expressionTypes[1]:
        {
          this.expressionForEdit.expression = this.surroundTextWith(this.concMatch.id, "{", "}", true);
          this.expressionForEdit.conceptName = this.expressionForEdit.expression;


          //cleanup old code...
          this.expressionForEdit.axisExpression = null;
          this.expressionForEdit.memberExpression = null;
          this.expressionForEdit.NamedAxisList = null;
          this.expressionForEdit.NamedMemberList = null;
          break;
        }
      case this.expressionTypes[2]:
        {
          this.expressionForEdit.expression = this.getExtendedCheckExpression(this.expressionForEdit);
          this.expressionForEdit.dimensionExpressionSets = null;
          break;
        }
      case this.expressionTypes[3]:
        {

          //extended check with dimension
          this.expressionForEdit.expression = this.getExtendedCheckExpression(this.expressionForEdit);
          var dim = this.getDimensionExpression(this.expressionForEdit);
          this.expressionForEdit.axisExpression = dim.axisExpression;
          this.expressionForEdit.memberExpression = dim.memberExpression;
          this.expressionForEdit.dimensionExpressionSets = null;
          this.expressionSets = null;
          break;
        }
      case this.expressionTypes[4]:
        {
          //formula
          this.expressionForEdit.formulaList = this.formulaSets;
          this.expressionForEdit.expression = this.getFormulaExpression(this.expressionForEdit);
          this.expressionForEdit.dimensionExpressionSets = null;
          this.expressionSets = null;
          break;
        }
      case this.expressionTypes[5]:
        {
          //conceptmatchmultiple
          this.expressionForEdit.expression = this.expressionForEdit.conceptMatchMultipleList.join("||");
          this.expressionForEdit.dimensionExpressionSets = null;
          this.expressionSets = null;
          break;
        }

    }
    if (this.isOverrideExpressionForEdit) {
      this.updateOverrideFromUIToRule();
    } else {
      if (!this.expressionForEdit.rank) {
        this.expressionForEdit.rank = this.termRule.expressions.length + 1;
        this.termRule.expressions.push(this.expressionForEdit);
      } else {
        var index = this.expressionForEdit.rank - 1;
        this.termRule.expressions[index] = this.expressionForEdit;
      }
    }
    // this.activeExpressionTabIndex = this.isOverrideExpressionForEdit ? 2 : 0;
    if (!this.showValidation) {
      this.addEditIndustry = false;
      this.editExpression = false;
      this.addEditDerived = false;
      this.saveTerm();
    }

    window.scroll(0, 0);
    this.msgs = [{ severity: 'info', summary: 'Saved Expression successfully' }];

  }

  getExpression() {
    var exprStr = "";
    switch (this.expressionForEdit.type) {
      case this.expressionTypes[0]:
        {
          if (this.concMatch.id === undefined) {
            // this.concMatch.id = this.concMatch?;
            if (this.concMatch.length !== undefined) {
              const id = { id: this.concMatch };
              this.concMatch = id;
            }
          }
          if (!this.concMatch.id) {
            this.showValidation = true;
            this.validationMessage = 'Concept Name is required.';
            return;


          }
          exprStr = this.surroundTextWith(this.concMatch.id, "{", "}", true);
          break;
        }
      case this.expressionTypes[1]:
        {
          if (!this.concMatch.id) {
            return;

          }
          exprStr = this.surroundTextWith(this.concMatch.id, "{", "}", true);
          var dim = this.getDimensionExpression(this.expressionForEdit);
          exprStr = exprStr + ", axisExpression:" + dim.axisExpression + ", memberExpression:" + dim.memberExpression;


          break;
        }
      case this.expressionTypes[2]:
        {
          exprStr = this.getExtendedCheckExpression(this.expressionForEdit);
          break;
        }
      case this.expressionTypes[3]:
        {
          exprStr = this.getExtendedCheckExpression(this.expressionForEdit);
          var dim = this.getDimensionExpression(this.expressionForEdit);
          exprStr = exprStr + ", axisExpression:" + dim.axisExpression + ", memberExpression:" + dim.memberExpression;
          break;
        }
      case this.expressionTypes[4]:
        {

          exprStr = this.getFormulaExpression(this.expressionForEdit);
          break;
        }
      case this.expressionTypes[5]:
        {

          exprStr = this.expressionForEdit.conceptMatchMultipleList.join("||");
          break;
        }


    }
    return exprStr;
  }
  getFormulaExpression = function (expr) {
    var exprStr = "";
    for (var i = 0; i < expr.formulaList.length; i++) {
      var formula = expr.formulaList[i];
      if (!formula) {
        continue;
      }
      var term = "sec:" + formula.termName;
      if (!term) {
        continue;
      }
      if (formula.nullable) {
        term = term + "=0";
      }
      exprStr = exprStr + this.surroundTextWith(term, "{", "}", true);
      if (expr.formulaList[i+1] != undefined && formula.operation) {
        var operation = formula.operation.name ? formula.operation.name : formula.operation;
        exprStr = exprStr + " " + operation;
      }

    }
    return exprStr;
  }

  saveValues(expression) {
    // Concept Match
    if (this.concMatch) {
      this.expressionForEdit.conceptName = this.concMatch.id !== undefined ? this.concMatch.id : null;
    }
    this.expressionForEdit.usePositiveValuesOnly = this.usePositiveFacts ? true : false;
    this.expressionForEdit.useNegativeValuesOnly = this.useNegativeFacts ? true : false;
    this.expressionForEdit.reverseNegativeValues = this.revNegativeFacts ? true : false;
    this.expressionForEdit.virtualFactAxis = this.axis;
    if (this.virtualFactMemberExclusions && this.virtualFactMemberExclusions[0] != null) {
      // this.expressionForEdit.virtualFactMemberExclusionList = [];
      this.expressionForEdit.virtualFactMemberExclusionList.push(this.virtualFactMemberExclusions);
    } else {
      this.expressionForEdit.virtualFactMemberExclusionList = [];
    }

    // Concept Match With Dim
    if (this.expressionSets) {
      this.expressionForEdit.dimensionExpressionSets = []
      this.expressionSets.forEach(obj => {
        if (obj.memberInclusionListAsString) {
          obj.memberInclusionList = []
          obj.memberInclusionList.push(obj.memberInclusionListAsString);
        }
        if (obj.memberExclusionListAsString) {
          obj.memberExclusionList = []
          obj.memberExclusionList.push(obj.memberExclusionListAsString);
        }
        if (obj.axisExclusionListAsString) {
          obj.axisExclusionList = []
          obj.axisExclusionList.push(obj.axisExclusionListAsString);
        }
        if (obj.axisInclusionListAsString) {
          obj.axisInclusionList = []
          obj.axisInclusionList.push(obj.axisInclusionListAsString);
        }
      })
      this.expressionForEdit.dimensionExpressionSets = this.expressionSets;
    }


    // Extended check
    this.expressionForEdit.containsWords = this.containsWords;
    this.expressionForEdit.doesNotContainsWords = this.doesNotContainsWords;
    this.expressionForEdit.balType = this.balType !== undefined ? this.balType : null;
    this.expressionForEdit.perType = this.perType !== undefined ? this.perType : null;
    this.expressionForEdit.isShareItemType = this.isShareItemType !== undefined ? this.isShareItemType : null;


    // Extended check with dim
    this.expressionForEdit.NamedAxisList = this.splitTextToArray(this.axes, '\n');
    this.expressionForEdit.NamedMemberList = this.splitTextToArray(this.axesMembers, '\n');

    //Formula
    this.formulaSets.forEach(obj => {
      if(obj.operation !== null) {
        obj.operation = obj.operation.name ? obj.operation.name : "";
      }
    })
    this.expressionForEdit.formulaList = this.formulaSets;

    if (this.formulaSets.length > 1) {
      this.expressionForEdit.formulaList = [];
      this.formulaSets.forEach(obj => {
        var operation = "";
        if(obj.operation != null) {
          operation = obj.operation.name != undefined ? obj.operation.name : ""
        }
        var formula = { termName: obj.termName, nullable: obj.nullable, operation:  operation};

        this.expressionForEdit.formulaList.push(formula);
      })
    }

    //Concept Match Multiple
    if (this.conceptMatchMultiple[0] != "") {
      this.expressionForEdit.conceptMatchMultipleList = [];
      this.conceptMatchMultiple.forEach(obj => {

        this.expressionForEdit.conceptMatchMultipleList.push(obj.id);
      })
    }
  }

  getExtendedCheckExpression = function (expression) {
    //console.log("name contains expression:  " + JSON.stringify(expression));
    var exprStr = "isExtended";

    var nameContainsExpr = this.arrayToExpression(expression.containsWords, " && ", 'nameContains(', ')');
    if (nameContainsExpr && nameContainsExpr.indexOf('nameContains') >= 0) {
      exprStr = exprStr + " && " + nameContainsExpr;
    }
    //console.log("name contains expression:  " + exprStr);

    var notContainsExpr = this.arrayToExpression(expression.doesNotContainsWords, " && ", 'nameNotContains(', ')');

    if (notContainsExpr && notContainsExpr.indexOf('nameNotContains') >= 0) {
      exprStr = exprStr + " && " + notContainsExpr;
    }

    //console.log("name not contains expression:  " + exprStr);

    if (expression.balType === this.balanceTypes[0]) {
      exprStr = exprStr + " && isDebit";
    }
    if (expression.balType === this.balanceTypes[1]) {
      exprStr = exprStr + " && isCredit";
    }
    if (expression.balType === this.balanceTypes[2]) {
      exprStr = exprStr + " && isNA";
    }
    if (expression.perType === this.rulePeriodTypes[0]) {
      exprStr = exprStr + " && isInstant";
    }
    if (expression.perType === this.rulePeriodTypes[1]) {
      exprStr = exprStr + " && isDuration";
    }
    if (expression.isShareItemType) {
      exprStr = exprStr + " && isShareItemType";
    }

    return exprStr;
  };

  getDimensionExpression = function (expression) {
    var dim: any = {};
    dim.memberExpression = this.arrayToExpression(expression.NamedMemberList, " || ", "{", "}");
    dim.axisExpression = this.arrayToExpression(expression.NamedAxisList, " || ", "{", "}");
    return dim;
  };

  validateDimensionalExpressions() {
    if (this.expressionForEdit.dimensionExpressionSets == null ||
      this.expressionForEdit.dimensionExpressionSets.length == 0) {
      this.errorMsg = 'Incomplete Dimension", "Please specify dimensional information.';
      this.showError = true;
      window.scroll(0, 0);
      return false;
    }

    this.expressionForEdit.dimensionExpressionSets.forEach(function (expSet) {

      if (expSet.axisType === "ExactMatch") {
        if (!expSet.axisName || expSet.axisName.length === 0) {
          this.errorMsg = 'Incomplete Dimension", "Please specify Axis name for the dimensional information.';
          this.showError = true;
          window.scroll(0, 0);
          return false;
        }
      } else {
        var hasData = false;
        if (expSet.axisInclusionList && expSet.axisInclusionList.length > 0) {
          hasData = true;
        }
        if (expSet.axisExclusionList && expSet.axisExclusionList.length > 0) {
          hasData = true;
        }

        if (hasData === false) {
          this.errorMsg = 'Incomplete Dimension", "Please specify Axis inclusion /exclusion data for the dimensional information.';
          this.showError = true;
          window.scroll(0, 0);
          return false;
        }
      }

      if (expSet.memberType === "ExactMatch") {
        if (!expSet.memberName || expSet.memberName.length === 0) {
          this.errorMsg = 'Incomplete Dimension", "Please specify Member name for the dimensional information.';
          this.showError = true;
          window.scroll(0, 0);
          return false;
        }
      } else if (expSet.memberType === "InclusionExclusionList") {
        var hasData = false;
        if (expSet.memberInclusionList && expSet.memberInclusionList.length > 0) {
          hasData = true;
        }
        if (expSet.memberExclusionList && expSet.memberExclusionList.length > 0) {
          hasData = true;
        }

        if (hasData === false) {
          this.errorMsg = 'Incomplete Dimension", "Please specify member inclusion /exclusion data for the dimensional information.';
          this.showError = true;
          window.scroll(0, 0);
          return false;
        }
      }


    });
    return true;
  }

  deleteWithRank(index) {
    if (this.expressionName === 'Expression')
      this.deleteExpressions(index)
    if (this.expressionName === 'Validation')
      this.deleteValidation(index);
    if (this.expressionName === 'Industry')
      this.deleteIndustry(index);
  };

  deleteIndustry(index) {
    var ind = index + 1
    this.confirmationService.confirm({
      message: 'Override Expression will be removed, are you sure?',
      header: 'Delete Override Expression',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        var override = this.getOverride(this.selIndOvrdExpr.name);
        if (override) {
          override.expressions.splice(index, 1);
          for (var j = 0; j < override.expressions.length; j++) {
            override.expressions[j].rank = j + 1;
          }
        }
        // this.termRule.overrides.push( override.expressions);
        this.saveTerm();
        this.expTableList = override.expressions;
        window.scroll(0, 0);
        this.msgs = [];
        this.msgs = [{ severity: 'info', summary: 'Deleted Override Expression successfully' }];
      },
      reject: () => {
      }
    });

  }

  deleteExpressions(index) {
    var ind = index + 1
    this.confirmationService.confirm({
      message: 'Rank ' + ind + ' Expression will be removed, are you sure?',
      header: 'Delete Expression',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.expressionList.splice(index, 1);
        for (var i = 0; i < this.expressionList.length; i++) {
          this.expressionList[i].rank = i + 1;
        }
        this.termRule.expressions = this.expressionList;

        this.saveTerm();
        this.expTableList = this.termRule.expressions;
        window.scroll(0, 0);
        this.msgs = [];
        this.msgs = [{ severity: 'info', summary: 'Deleted Expression with rank ', detail: index + ' successfully' }];

      },
      reject: () => {
        //this.msgs = [{severity:'info', summary:'Rejected', detail:'You have rejected'}];
      }
    });

  }

  addValidations() {
    this.addEditValidation = true;
    this.formulaSets = [];
    this.addAnotherFormula();
    this.addvalidation = true
    //   if(!this.termRule.validationExpressions) {
    //     this.termRule.validationExpressions = [];
    // }
    // this.validation = {};
    // this.validation.rank = this.termRule.validationExpressions.length+1;
    // this.validation.formulaList = [];
    // this.validation.formulaList.push({});
    // this.validation.expression = "";


  }

  saveRuleWithValidation() {
    if (!this.checkFormulas()) {
      this.validationMessage = "Incomplete Validation entry found. Please complete before saving.";
      this.showValidation = true;
      window.scroll(0, 0);
      return;
    };
    let val: any = {};
    if (this.addvalidation) {
      if(this.termRule != null && this.termRule.validationExpressions === null) {
        this.termRule.validationExpressions = [];
      }
      val.rank = this.termRule.validationExpressions.length + 1;
      val.expression = this.convertFormulaToExpression(this.formulaSets);
      this.termRule.validationExpressions[val.rank - 1] = val;
    } else {
      val.rank = this.validationRank;
      val.expression = this.convertFormulaToExpression(this.formulaSets);
      this.formulaSets = [];
      this.termRule.validationExpressions[val.rank - 1] = val;
    }


    //this.activeExpressionTabIndex = 1;
    this.saveTerm();
    this.msgs = [];
    window.scroll(0, 0);
    this.msgs = [{ severity: 'info', summary: 'Saved Validation successfully' }];
    this.validationList = [];
    this.expTableList = []
    this.termRule.validationExpressions.forEach(obj => {
      obj.type = 'V';
      this.validationList.push(obj);
    })
    this.expTableList = this.validationList;
  }
  checkFormulas = function () {
    console.log("checking formulas add allowed");
    var allowAdd = true;
    for (var x = 0; x < this.formulaSets.length; x++) {
      var formula = this.formulaSets[x];
      if (!formula.termName) {
        console.log("failed validation: " + JSON.stringify(formula));
        allowAdd = false;
        break;
      } else {
        if (x !== (this.formulaSets.length - 1) && !formula.operation) {
          console.log("failed validation: " + JSON.stringify(formula));
          allowAdd = false;
          break;
        }
      }
      //|| !formula.operation
    }
    console.log("checking formulas add allowed: " + allowAdd);
    return allowAdd;
  }
  convertFormulaToExpression(formulaList) {
    console.log("convertFormulaToExpression: " + JSON.stringify(formulaList));
    var expression = "work in progress... available soon";
    expression = "";
    for (var x = 0; x < formulaList.length; x++) {
      var formula = formulaList[x];
      if (!!formula.termName) {
        expression = expression + "{sec:" + formula.termName;
        if (!!formula.nullable) {
          expression = expression + "=0";
        }
        expression = expression + "}";
        if (!!formula.operation) {
          expression = expression + formula.operation.name;
        }
      }
    }

    return expression;
  }

  getValidationExpression() {
    this.validationExpression = this.convertFormulaToExpression(this.formulaSets);
    this.validationRank = this.termRule.validationExpressions.length + 1

  }

  deleteValidation(index) {
    var ind = index + 1;
    this.confirmationService.confirm({
      message: 'Rank ' + ind + ' validation will be removed, are you sure?',
      header: 'Delete Validation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.termRule.validationExpressions.splice(index, 1);
        for (var i = 0; i < this.termRule.validationExpressions.length; i++) {
          var elem = this.termRule.validationExpressions[i];
          elem.rank = i + 1;
        }
        this.saveTerm();
        this.expTableList = this.termRule.validationExpressions;
        this.msgs = [];
        window.scroll(0, 0);
        this.msgs = [{ severity: 'info', summary: 'Deleted Validation with rank ', detail: ind + ' successfully' }];
      },
      reject: () => {
        //this.msgs = [{severity:'info', summary:'Rejected', detail:'You have rejected'}];
      }
    });

  };

  addOverrideExpression() {
    this.expressionForEdit = {};
    this.expressionSets = [];
    this.formulaSets = [];
    this.AddExpressionSet();
    this.addAnotherFormula();
    this.overrideName = null;
    this.mergeBaseExpressions = null;
    this.overridecompanyName = null;
    this.disableAxes = false;
    this.disableMember = false;
    this.disableAxesInc = true;
    this.disableMemInc = true;
    this.overrideForEdit = {};
    this.mergeBaseExpressions = true;
    // this.expressionForEdit.expression = "";
    // this.expressionForEdit.type = this.expressionTypes[0];
    // this.expressionForEdit.NamedAxisList = [];
    // this.expressionForEdit.NamedMemberList = [];
    // this.expressionForEdit.useVirtualParentNew = true;
    // this.expressionForEdit.useMaxAxisCount = false;
    // this.expressionForEdit.maxAxisCount = 2;
    // this.expressionForEdit.dimensionExpressionSets = [];
    // this.expressionForEdit.virtualFactMemberExclusionList = [];
    // this.expressionForEdit.containsWords = [];
    // this.expressionForEdit.doesNotContainsWords = [];
    this.conceptNameDefinition = "";
    this.showExpression = false;
    this.addingExpression();
    this.isOverrideExpressionForEdit = true;
  };

  editOverrideExpression(rowdata) {
    this.entityOverrides = [];
    var override = this.getOverride(this.selIndOvrdExpr.name);
    this.overrideForEdit = {}
    if (!override)
      return;
    this.expressionForEdit = override.expressions[rowdata.rank - 1];
    this.overrideForEdit.name = override.name;
    this.overrideForEdit.mergeBaseExpressions = override.mergeBaseExpressions;
    this.overrideForEdit.origName = override.name;
    this.overrideName = { name: override.name };
    this.mergeBaseExpressions = override.mergeBaseExpressions;
    if (override.entityOverrides[0] != null) {
      this.setCurrentOverrideIndustryInformation(override);
    } else {
      this.entityOverrides = null;
      this.addAnotherIndustryOverride();
    }


    this.isOverrideExpressionForEdit = true;
    if (!this.expressionForEdit.type || this.expressionForEdit.type === 'na') {
      this.expressionForEdit.type = this.expressionTypes[0];
    }
  };

  setCurrentOverrideIndustryInformation(override) {

    this.entityOverrides = [];
    for (var j = 0; j < override.entityOverrides.length; j++) {
      var entityOverride: any = {};

      if (override.entityOverrides[j].entityId) {
        var entity = this.completeCompanyList.filter(obj => obj.entityId === override.entityOverrides[j].entityId);
        if (entity && entity.length > 0) {
          entityOverride.entityname = entity[0].companyName + "(" + entity[0].entityId + ")";
        }
      }
      if (!entityOverride.entityname) {
        entityOverride.overrideDivSec = this.divSector;
      }
      this.entityOverrides.push(entityOverride);


    }
  }



  addAnotherIndustryOverride() {

    var overrideEntity = { divisionSector: "", entityname: "" };
    if (!this.entityOverrides) {
      this.entityOverrides = [];
    }
    this.entityOverrides.push(overrideEntity);
  }

  removeOverride = function (index) {
    this.entityOverrides.splice(index, 1);
  }

  updateOverrideFromUIToRule() {
    var overrideName = this.overrideName.name ? this.overrideName.name : this.overrideName;
    var index = -1;
    var existingExpressions = [];
    if (!this.termRule.overrides) {
      this.termRule.overrides = [];
    }
    for (var i = 0; i < this.termRule.overrides.length; i++) {
      if (overrideName) {
        if (overrideName === this.termRule.overrides[i].name) {
          index = i;
          existingExpressions = this.termRule.overrides[i].expressions;
          break;
        }
      } else {
        if (this.overrideForEdit.name === this.termRule.overrides[i].name) {
          index = i;
          existingExpressions = this.termRule.overrides[i].expressions;
          break;
        }
      }
    }
    var override: any = {};
    override.name = overrideName;
    override.mergeBaseExpressions = this.mergeBaseExpressions;
    override.expressions = existingExpressions;
    override.entityOverrides = [];
    for (var i = 0; i < this.entityOverrides.length; i++) {
      var indOverride: any = {};
      if (this.entityOverrides[i].entityname) {
        // entityOverride.overridecompanyName = entity[0].companyName.substr(0, entity[0].companyName.indexOf( '(');
        // mp.division = this.divSector.substr(0,this.divSector.indexOf( '->')-1);
        // mp.sector =  this.divSector.substr(this.divSector.indexOf( '->')+3, this.divSector.length);
        indOverride.entityId = this.getCompanyIdFromName(this.entityOverrides[i].entityname.substr(0, this.entityOverrides[i].entityname.indexOf('(')));
      } else {
        if (this.entityOverrides[i].divisionSector) {
          indOverride.division = this.entityOverrides[i].divisionSector.division;
          indOverride.sector = this.entityOverrides[i].divisionSector.sector;
        }
      }
      if (!indOverride.division && !indOverride.entityId) {
        continue;
      }

      override.entityOverrides.push(indOverride);
    }
    if (this.expressionForEdit.rank) {

      //console.log("Updating Expression array index : " + index);
      override.expressions[this.expressionForEdit.rank - 1] = this.expressionForEdit;
    } else {
      this.expressionForEdit.rank = override.expressions.length + 1;

      override.expressions.push(this.expressionForEdit);
    }

    if (index < 0) {
      this.termRule.overrides.push(override);
    } else {
      this.termRule.overrides[index] = override;
    }
  }

  getCompanyIdFromName(input) {
    var entities = this.completeCompanyList.filter(obj => obj.companyName === input);

    if (entities.length > 0) {
      return entities[0].entityId;
    }

    return null;
  }
  getOverride(overrideName) {
    this.toHighlight = (overrideName && overrideName.query) ? overrideName.query : '';
    if (overrideName && this.termRule.overrides) {
      for (var i = 0; i < this.termRule.overrides.length; i++) {
        if (this.termRule.overrides[i].name === overrideName) {
          return this.termRule.overrides[i];
        }
      }
    }

    return null;
  };

  deleteOverrideExpressionWithRank(selectedOverride, index) {
    if (confirm("Override Expression will be removed, are you sure?")) {

      var override = this.getOverride(selectedOverride);

      if (override) {
        override.expressions.splice(index, 1);
        for (var j = 0; j < override.expressions.length; j++) {
          override.expressions[j].rank = j + 1;
        }
      }
      this.saveTerm();
    } else {
    }

  };

  convertExpressionToFormula(expression) {
    console.log("convertExpressionToFormula: " + expression);
    var parts = expression.split("{");
    this.formulaSets = [];
    for (var i = 0; i < parts.length; i++) {
      var part = parts[i];
      if (!!part) {
        var fields = part.split("}");
        if (fields.length > 0) {
          var term = fields[0];
          term = term.replace("sec:", "");
          var nullable = false;
          if ((term.length - 2) === term.lastIndexOf("=0")) {
            nullable = true;
          }
          if (fields.length === 2) {
            var operation = fields[1].trim();

            term = term.replace("=0", "");
          }
          this.formulaSets.push({ termName: term, nullable: nullable, operation: { name: operation } });
        }
      }
    }
  }
  getCurrentOverrideDetails() {
    var override = this.getOverride(this.overrideName.name);
    if (override) {
      this.setCurrentOverrideIndustryInformation(override);
    }
  };

  getOverRides(event) {
    this.companyovrRideList = [];
    for (let i = 0; i < this.termRule.overrides.length; i++) {
      let cname: string = this.termRule.overrides[i].name;
      if (cname != null && cname.toLowerCase().indexOf(event.query.toLowerCase()) == 0) {
        this.companyovrRideList.push({ name: cname });
      }
    }
  }

  removeTextBlock(index) {
    this.derivedZeroExpressionForEdit.textBlockList.splice(index, 1);
  };
  addAnotherTextBlock() {
    var item = { expressionItem: "" };
    this.derivedZeroExpressionForEdit.textBlockList.push(item);
  };

  addDerivedZeroExpression() {
    this.addEditDerived = true;
    this.derivedZeroExpressionForEdit = {};
    this.activeExpressionTabIndex = 0;
    this.derivedZeroExpressionForEdit.expression = "";
    this.derivedZeroExpressionForEdit.type = this.derivedZeroTypes[0];
    this.derivedZeroExpressionForEdit.textBlockList = [];
    var item = { expressionItem: "" };
    this.derivedZeroExpressionForEdit.textBlockList.push(item);
    this.derivedZeroExpressionForEdit.depTermNameList = [];
    this.derivedZeroExpressionForEdit.excTermNameList = [];
    this.divisionCount = this.completeCompanyList.length;
    this.addAnotherItem(false);
    this.addAnotherItem(true);

  };

  editDerivedZeroExpression = function (index) {
    this.activeExpressionTabIndex = 3;
    this.derivedZeroExpressionForEdit = this.termRule.derivedZeroExpressions[index];
  };

  saveNewDerivedExpression() {

    if (!this.termRule.derivedZeroExpressions) {
      this.termRule.derivedZeroExpressions = [];
    }

    var derivedZeroExpression: any = {};
    derivedZeroExpression.type = this.derivedZeroExpressionForEdit.type;
    derivedZeroExpression.rank = this.derivedZeroExpressionForEdit.rank;
    derivedZeroExpression.allTermsRequired = false;
    switch (this.derivedZeroExpressionForEdit.type) {
      case this.derivedZeroTypes[0]:
        {
          derivedZeroExpression.expression = this.getTextBlockExpression(this.derivedZeroExpressionForEdit.textBlockList);
          break;
        }
      case this.derivedZeroTypes[1]:
        {
          derivedZeroExpression.allTermsRequired = this.derivedZeroExpressionForEdit.allTermsRequired;
          var splitStr = derivedZeroExpression.allTermsRequired && derivedZeroExpression.allTermsRequired == true ? "&&" : "||";
          derivedZeroExpression.expression = this.getTermExpression(this.derivedZeroExpressionForEdit.depTermNameList, splitStr);
          break;
        }
      case this.derivedZeroTypes[2]:
        {
          derivedZeroExpression.expression = this.getTermExpression(this.derivedZeroExpressionForEdit.excTermNameList, "||");
          break;
        }
      case this.derivedZeroTypes[3]:
        {
          var division = this.deriveddivSector.substr(0, this.deriveddivSector.indexOf('->') - 1);
          var sector = this.deriveddivSector.substr(this.deriveddivSector.indexOf('->') + 3, this.deriveddivSector.length);
          var sic = this.derivedindustry.substr(0, 4);
          derivedZeroExpression.division = division;
          derivedZeroExpression.sector = sector;
          derivedZeroExpression.industry = sic;
          derivedZeroExpression.expression = this.getExcludeIndustryExpression(derivedZeroExpression);
          break;
        }
    }
    ;

    if (!derivedZeroExpression.rank) {

      derivedZeroExpression.rank = this.termRule.derivedZeroExpressions.length + 1;


      this.termRule.derivedZeroExpressions.push(derivedZeroExpression);
    } else {
      var index = this.derivedZeroExpressionForEdit.rank - 1;
      //console.log("Updating Expression array index : " + index);
      this.termRule.derivedZeroExpressions[index] = derivedZeroExpression;
    }
    this.activeExpressionTabIndex = 3;
    this.saveTerm();

  }

  onDerivedTabChange(event) {
    this.activeExpressionTabIndex = event.index;
    this.derivedZeroExpressionForEdit = [];
    this.deriveddivSector = '';
    this.derivedindustry = '';
    this.filteredDerivedindustry = [];
    // this.divisionCount =0;
    // this.sectorCount = 0;
    this.derivedZeroExpressionForEdit.type = this.derivedZeroTypes[this.activeExpressionTabIndex];
    this.prepareDerivedZeroExpressionForEdit();
  }


  getTermExpression(tnList, splitStr) {
    var exprStr = "";
    for (var i = 0; i < tnList.length; i++) {
      var exp = tnList[i].expressionItem;
      if (i === 0) {
        exprStr = this.surroundTextWith(exp, "{sec:", "}", true);
      } else {
        exprStr = exprStr + splitStr + this.surroundTextWith(exp, "{sec:", "}", true);
      }
    }

    return exprStr;
  };
  getExcludeIndustryExpression(derivedZeroExpression) {
    var exp = "";
    if (derivedZeroExpression.division) {

      exp = derivedZeroExpression.division + "->" + derivedZeroExpression.sector;
    }
    if (derivedZeroExpression.industry) {
      exp = exp + " " + derivedZeroExpression.industry;
    }

    return exp;
  }

  getTextBlockExpression(tnList) {
    var exprStr = "";
    for (var i = 0; i < tnList.length; i++) {
      var exp = tnList[i].expressionItem;
      if (i === 0) {
        exprStr = this.surroundTextWith(exp.id, "{", "}", true);
      } else {
        exprStr = exprStr + '||' + this.surroundTextWith(exp.id, "{", "}", true);
      }
    }

    return exprStr;
  }

  addAnotherItem(isExclusion) {
    var item = { expressionItem: "" };

    if (isExclusion) {
      this.derivedZeroExpressionForEdit.excTermNameList.push(item);
    } else {
      this.derivedZeroExpressionForEdit.depTermNameList.push(item);
    }

    //console.log(this.derivedZeroExpressionForEdit.formulaList);
  };
  removeDerivedTerm(index, isExclusion) {

    if (isExclusion) {
      this.derivedZeroExpressionForEdit.excTermNameList.splice(index, 1);
    } else {
      this.derivedZeroExpressionForEdit.depTermNameList.splice(index, 1);
    }

  }
  prepareDerivedZeroExpressionForEdit() {

    switch (this.derivedZeroExpressionForEdit.type) {
      case this.derivedZeroTypes[0]:
        {
          this.setTextBlockExpression();
          break;
        }
      case this.derivedZeroTypes[1]:
        {
          this.setDepTermExpression();
          break;
        }
      case this.derivedZeroTypes[2]:
        {
          this.setExlTermExpression();
          break;
        }
      case this.derivedZeroTypes[3]:
        {
          this.derivedRank = false;
          this.divSectorList.forEach(obj => {
            var name = { label: obj, value: obj };
            this.filteredDeriveddivSectors.push(name)
          });
          this.setExcludeEntitiesExpression();
          break;
        }

    }
  }
  setTextBlockExpression() {

    // if (!this.derivedZeroExpressionForEdit.textBlockList) {
    this.derivedZeroExpressionForEdit.textBlockList = [];
    if (!this.derivedZeroExpressionForEdit.expression) {
      this.filteredtext = [];
      this.addAnotherTextBlock();

    } else {

      this.filteredtext = [];
      var strArray = this.derivedZeroExpressionForEdit.expression.split("||");
      for (var i = 0; i < strArray.length; i++) {
        var itemExpression = strArray[i]
        itemExpression = itemExpression.replace("{", "");
        itemExpression = itemExpression.replace("}", "");
        var item = { 'expressionItem': itemExpression };

        this.derivedZeroExpressionForEdit.textBlockList.push(item);
        this.filteredtext.push(itemExpression);
      }
      //this.derivedZeroExpressionForEdit.expression = null;
    }

    // }
  }
  setDepTermExpression() {

    // if (!this.derivedZeroExpressionForEdit.depTermNameList) {
    this.derivedZeroExpressionForEdit.depTermNameList = [];
    if (!this.derivedZeroExpressionForEdit.expression) {

      this.addAnotherItem(false);

    } else {

      var splitStr = this.derivedZeroExpressionForEdit.allTermsRequired == true ? "&&" : "||";
      var strArray = this.derivedZeroExpressionForEdit.expression.split(splitStr);
      for (var i = 0; i < strArray.length; i++) {
        var itemExpression = strArray[i]
        itemExpression = itemExpression.replace("{sec:", "");
        itemExpression = itemExpression.replace("}", "");
        var item = { expressionItem: itemExpression };
        this.derivedZeroExpressionForEdit.depTermNameList.push(item);
      }
      //this.derivedZeroExpressionForEdit.expression = null;

    }

    // }
  }

  setExlTermExpression() {

    if (!this.derivedZeroExpressionForEdit.excTermNameList) {
      this.derivedZeroExpressionForEdit.excTermNameList = [];
      if (!this.derivedZeroExpressionForEdit.expression) {

        this.addAnotherItem(true);

      } else {


        var strArray = this.derivedZeroExpressionForEdit.expression.split("||");
        for (var i = 0; i < strArray.length; i++) {
          var itemExpression = strArray[i]
          itemExpression = itemExpression.replace("{sec:", "");
          itemExpression = itemExpression.replace("}", "");
          var item = { expressionItem: itemExpression };
          this.derivedZeroExpressionForEdit.excTermNameList.push(item);
        }
        // this.derivedZeroExpressionForEdit.expression = null;

      }

    }
  }

  setExcludeEntitiesExpression() {

    if (this.derivedZeroExpressionForEdit.division && this.derivedZeroExpressionForEdit.sector) {
      var divSec = {
        "division": this.derivedZeroExpressionForEdit.division,
        "sector": this.derivedZeroExpressionForEdit.sector
      };
      this.filteredDerivedindustry = []
      this.setExcDivSec(this.derivedZeroExpressionForEdit.division, this.derivedZeroExpressionForEdit.sector);
      if (this.derivedZeroExpressionForEdit.sector && this.derivedZeroExpressionForEdit.industry)
        this.deriveddivSector = this.derivedZeroExpressionForEdit.division + ' -> ' + this.derivedZeroExpressionForEdit.sector;
      this.filteredDerivedindustry.forEach(obj => {
        var sic = obj.label.substr(0, 4);
        if (sic === this.derivedZeroExpressionForEdit.industry) {
          this.derivedindustry = obj.label;
        }
      })
      // this.derivedindustry = this.derivedZeroExpressionForEdit.industry + ' - ' + this.derivedZeroExpressionForEdit.sector;

      if (!this.excludeEntitiesFilter)
        this.excludeEntitiesFilter = {};
      this.excludeEntitiesFilter.sic = this.derivedZeroExpressionForEdit.industry;
    }
    else {
      this.excludeEntitiesFilter = {};
    }
  }

  setExcDivSec(division, sector) {
    this.completeEntities = this.completeCompanyList;
    var sicCodes: any[] = [];
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
    sicCodes.forEach(obj => {
      var name = { label: obj, value: obj };
      this.filteredDerivedindustry.push(name);
    })

    this.divisionCount = this.completeEntities.length;
    if (this.derivedZeroExpressionForEdit.industry) {
      var completeEntities = this.completeEntities.filter(obj => obj.sic === this.derivedZeroExpressionForEdit.industry);
      this.industryCount = completeEntities.length
    }

  }

  processTermForEntity(rowdata) {
    //console.log("Process rule " + $scope.rule.termId + " with token " + $scope.csrfToken + " for entity: " + entityId);
    this.progressSpinner = true;
    this.msgs = [{ severity: 'info', summary: 'Started processing for ' + this.termName, detail: '' }];
    this.utilService.processTerms(this.termRule.termId, rowdata.entityId, null).subscribe((response) => {
      if (!response.data) {
        window.scroll(0, 0);
        this.msgs = [{ severity: 'info', summary: 'Term processing for' + this.termName, detail: ' processed successfully' }];
        this.progressSpinner = false;

      } else {
        window.scroll(0, 0);
        this.msgs = [{ severity: 'info', summary: 'Process Term Rule failed' + this.termName, detail: '' }];
        this.progressSpinner = false;

      }


    }, function (err) {

    }
    );
  }

  yearChange(event) {
    this.startYear = this.range[0];
    this.endYear = this.range[1];
    sessionStorage.setItem('home.range', JSON.stringify(event.values));

  }
  selectedTreeNode = {};
  showDerivationTree() {
    this.homeService.getTermRule(this.termId).subscribe(
      (response) => {
        this.modalExpTableData = response;
        this.modalExpTableList = response.expressions;

        this.homeService.getRuleGraph(this.termId).subscribe(
          (response) => {
            this.derivationTreeRes = response;
            response.forEach(item => {
              item.name = item.name.replace(' ( - )', '').replace(' ( + )', '');
            });

            const newRes = [];

            if (response.length > 0) {
              newRes.push({
                data: response.find(item => item.parent === null),
                children: []
              })
            }

            if (newRes && newRes.length > 0) {
              newRes.forEach(eachItem => {
                let result = response.filter(obj => {
                  return obj.parent === eachItem.data.id
                });
                eachItem.data['hasChildren'] = result.length > 0 ? true : false;
                result.forEach(item => {

                  eachItem.children.push({
                    data: item,
                    children: []
                  })
                });

                if (eachItem.children.length > 0) {
                  eachItem.children.forEach(child1 => {
                    let result = response.filter(obj => {
                      return obj.parent === child1.data.id
                    });
                    child1.data['hasChildren'] = result.length > 0 ? true : false;
                    result.forEach(newItem => {
                      child1.children.push({
                        data: newItem,
                        children: []
                      })
                    });

                    if (child1.children.length > 0) {
                      child1.children.forEach(child2 => {
                        let result = response.filter(obj => {
                          return obj.parent === child2.data.id
                        });
                        child2.data['hasChildren'] = result.length > 0 ? true : false;
                        result.forEach(newItem => {
                          child2.children.push({
                            data: newItem,
                            children: []
                          })
                        });

                        if (child2.children.length > 0) {
                          child2.children.forEach(child3 => {
                            let result = response.filter(obj => {
                              return obj.parent === child3.data.id
                            });
                            child3.data['hasChildren'] = result.length > 0 ? true : false;
                            result.forEach(newItem => {
                              child3.children.push({
                                data: newItem,
                                children: []
                              })
                            });

                            if (child3.children.length > 0) {
                              child3.children.forEach(child4 => {
                                let result = response.filter(obj => {
                                  return obj.parent === child4.data.id
                                });
                                child4.data['hasChildren'] = result.length > 0 ? true : false;
                                result.forEach(newItem => {
                                  child4.children.push({
                                    data: newItem,
                                    children: []
                                  })
                                });
                              });
                            }
                          });
                        }
                      });
                    }

                  });
                }
              });
            }

            this.treeData = { data: newRes };
            this.displayTermTreeModal = true;
          });

      });
  }


  nodeUpdated(node: any) {
    console.info("app detected node change");
  }
  nodeSelected(node: any) {
    if (!node.children) {
      this.homeService.getTermRule(node.data.childId).subscribe(
        (response) => {
          this.modalExpTableList = response.expressions;
        });
    }
  }

  showTermRuleResultnavigateTree(event, data) {

    if (data !== undefined) {
      event.preventDefault();
      let term = 'LSE';
      if ((data && data.childId)) {
        term = data.childId;
      } else {
        term = data.name;
      }

      this.selectedTreeNode = data;
      this.homeService.getTermRule(term).subscribe(
        (response) => {
          this.modalExpTableData = response;
          this.modalExpTableList = response.expressions;
        }
      )
    }

  }

  showTermRuleResultnavigate(event, data) {

    if (data !== undefined) {
      event.preventDefault();
      var term = this.getTermIdFromTermName(data)
      this.homeService.getTermRule(term).subscribe(
        (response) => {
          this.modalExpTableData = response;
          this.modalExpTableList = response.expressions;
        }
      )
    }
  }
  minimizeWidth;
  minimizeModal(i) {
    let minimizeCount = undefined;
    let minimizeItems = 0;;
    this.showTermResultsWindowMinimize[i] = true;

    this.minimizeArray.forEach((item, index) => {
      if (!item && (minimizeCount === undefined)) {
        minimizeCount = index
      }
      if (item) {
        minimizeItems = minimizeItems + 1
      }
    })
    const defaultWidth = (window.innerWidth / (minimizeItems + 1));

    for (let j = 0; j < this.entityList.length; j++) {
      if (!this.minimizeArray[j]) {
        this.minimizeArray[j] = this.entityList[i].id;
        break;
      }
    }
    // this.el.nativeElement.getElementsByClassName('modal' + i)[0].style.left = (+(i.toString().slice(i.toString().length - 1, i.toString().length)) * 190) + 'px';
    this.el.nativeElement.getElementsByClassName('modal' + i)[0].style.left = (minimizeCount * 190) + 'px';
    this.el.nativeElement.getElementsByClassName('modal' + i)[0].style.transform = 'translate(0px, 0px)';
    this.minimizeWidth = defaultWidth + 'px';
  }

  maximizeModal(i) {
    this.showTermResultsWindowMinimize[i] = false;
    const removeIndex = this.minimizeArray.findIndex(item => item === this.entityList[i].id);
    this.minimizeArray[removeIndex] = null;
    this.el.nativeElement.getElementsByClassName('modal' + i)[0].style.left = '0px';
    this.el.nativeElement.getElementsByClassName('modal' + i)[0].style.transform = 'translate(0px, 0px)';
    this.el.nativeElement.getElementsByClassName('modal' + i)[0].style.width = 'auto';
  }

  close(i) {
    const removeIndex = this.minimizeArray.findIndex(item => item === this.entityList[i].id);
    if (removeIndex > -1) {
      this.minimizeArray[removeIndex] = null;
    }
    this.showTermResultsWindow[i] = false;
    this.showTermResultsWindowMinimize[i] = false;
  }
  // Home page tour
  stepDone() {
    setTimeout(() => {
      this.title = 'Tour Finished!';
      this.tour = false;
      this.termId = '';
      this.displayTerms = false;
      this.displayGetCoverage = false;
      console.log('Step done!');
    }, 3000);
  }

  startTour() {
    const options = {
      steps: [
        'step1@home',
        'step2@home',
        'step3@home',
        'step4@home',
        'step5@home',
        'step6@home',
        'step7@home',
        'step8@home',
        'step9@home',
        'step10@home',
        'step11@home',
        'step12@home',
        'step13@home',
        'step14@home',
        'step15@home',
        'step16@home',
        'step17@home',
        // 'step14@home',
        // 'step15@home',
        // 'step16@home',
        // 'step17@home',
        'step18@home',
        'step19@home',
        'step20@home',
        'step21@home',
        'step22@home',
        'step23@home',
        'step24@home',
        'step25@home',

      ],

      // startWith: 'step3@app',
      // waitingTime: 3000,
      stepDefaultPosition: 'right',
      themeColor: '#345632',
      showPrevButton: true,
      logsEnabled: false
      // customTexts: { prev: of('<<').pipe(delay(2000)), next: '>>'}
    };
    this.joyrideService.startTour(options).subscribe(
      step => {
        console.log('Next:', step);
        if (step.name === 'step13' || step.name === 'step14' || step.name === 'step15' || step.name === 'step16' || step.name === 'step17') {
          this.processnow = true;
        } else {
          this.processnow = false;
        }
        if (step.name === 'step1') {
          this.termId = 'SALE';
          this.displayTerms = true;
          this.displayGetCoverage = true;
        }
      },
      e => {
        console.log('Error', e);
      },
      () => {
        this.stepDone();
        // this.tour = false;
        // this.termId = '';
        //   this.displayTerms = false;
        //   this.displayGetCoverage = false;
        console.log('Tour finished');
      }
    );
  }

}
