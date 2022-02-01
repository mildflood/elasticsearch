import { Component } from '@angular/core';
import { OnInit, ViewChild } from '@angular/core';
import { OnDestroy } from '@angular/core';
import { ISubscription } from 'rxjs/Subscription';
import { NavigationEnd, Router } from '@angular/router';
import { ManageTermService } from './manageTermService';
import { TermMap } from '../domain/termMap';
import {Messages, Message} from 'primeng/primeng';
import { ConfirmationService } from 'primeng/api';

@Component({
    selector: 'export-page',
    templateUrl: './manageTerm.html',
    styleUrls: [`../app.component.css`],
  })
  export class ManageTermComponent implements OnInit, OnDestroy  {
    private subscription: ISubscription[] = [];
    @ViewChild(('termMapTable')) termMapTable: any;
    navigationSubscription;
    termMapcols: any;
    //termMapList: any[] = [];
    editTerms: { [s: string]: any; } = {};
    actualTermList: any[] = [];
    msgs: Message[] = [];
    showError: boolean;
    errorMsg : string = '';

    termMappingGroups: any[] =[];
    topCol: any[] = [];
    toogleCols: any[]= [];
    compCheck: boolean;
    ifrsCheck: boolean;
    bloomCheck: boolean;
    disableNewGroup: boolean = false;
    newRow: boolean = false;

    newGroupName: string;
    shownGroupCount: number = 0
    first = 0;
    rows = 10;

    presetTermId: string = 'Search';

    constructor(private manageTermService: ManageTermService, private router: Router, private termMap: TermMap, private confirmationService: ConfirmationService) {
        this.navigationSubscription = this.router.events.subscribe((e: any) => {
          if (e instanceof NavigationEnd) {
          }
        });
      }

      ngOnInit() {
        console.log('manageTermComponent : calling ngOnInit...');
       //this.termMapcols = this.termMap.termMapCols();
       this.loadTermMappingInformationList();
        this.getTermMapGroups();

        //pre set termId filter with termid passed in from AccuracyTest page, when user clicks on termid link
        if (sessionStorage.getItem('accuracyTest.presetTermId') !== null) {
          this.presetTermId = sessionStorage.getItem('accuracyTest.presetTermId');
          this.onFilter(this.presetTermId, 'termId', 'contains');
          sessionStorage.removeItem('accuracyTest.presetTermId');
        }
      }

      resetPlaceholder() {
        this.presetTermId = 'Search';
      }

      getTermMapGroups() {
        this.manageTermService.getTermMapGroupNameList().subscribe((response) => {
          this.termMappingGroups = response;
          if(this.termMappingGroups.length >= 6) {
            this.disableNewGroup = true;
          } else
          this.disableNewGroup = false;

          this.termMappingGroups.forEach(obj => {
            obj.isDisplayed = true;
            var desc = { field: obj.mapName, header: obj.mapName };
            this.topCol.push(desc);
          })
        });
      }
      ngOnDestroy() {
        this.subscription.forEach(s => s.unsubscribe());
        if (this.navigationSubscription) {
            this.navigationSubscription.unsubscribe();
          }
      }
      onFilter(filter: string, col: string, cont: string) {
        // if (col === 'cik') {
        //     if (filter.length === 10) {
        //         filter = filter.replace(/^0+/, '');
        //     }
        //     this.termMapTable.filter(filter, col, 'contains');
        // }
        this.termMapTable.filter(filter, col , 'contains');
      }

      formatHeaders(list: any) {
        this.termMapcols = [
          { field: 'termId', header: 'Term ID' , isDisplayed: true, group:'Maxds'},
           { field: 'name', header: 'Term Name' , isDisplayed: true , group:'Maxds'},
          { field: 'description', header: 'Description' , isDisplayed: true , group:'Maxds'},
         ];
        if(list.mappedInfoSets[0]) {
          this.termMapcols.push( { field: 'atermId', header: 'Term ID' ,  group:0, isDisplayed: true},
          { field: 'atermName', header: 'Term Name' ,  group:0, isDisplayed: true},
         { field: 'adescription', header: 'Description' ,  group:0, isDisplayed: true},
         { field: 'amapping', header: 'Mapping' ,  group:0, isDisplayed: true})
        }
        if( list.mappedInfoSets[1]) {
          this.termMapcols.push( { field: 'btermId', header: 'Term ID' ,  group:1, isDisplayed: true},
          { field: 'btermName', header: 'Term Name' ,  group:1, isDisplayed: true},
         { field: 'bdescription', header: 'Description' ,  group:1, isDisplayed: true},
         { field: 'bmapping', header: 'Mapping' ,  group:1, isDisplayed: true},)
        }
        if( list.mappedInfoSets[2]) {
          this.termMapcols.push({ field: 'ctermId', header: 'Term ID' ,  group:2, isDisplayed: true},
          { field: 'ctermName', header: 'Term Name' ,  group:2, isDisplayed: true},
         { field: 'cdescription', header: 'Description' ,  group:2, isDisplayed: true},
         { field: 'cmapping', header: 'Mapping' ,  group:2, isDisplayed: true},)
        }
        if( list.mappedInfoSets[3]) {
          this.termMapcols.push({ field: 'dtermId', header: 'Term ID' ,  group:3, isDisplayed: true},
          { field: 'dtermName', header: 'Term Name' ,  group:3, isDisplayed: true},
         { field: 'ddescription', header: 'Description' ,  group:3, isDisplayed: true},
         { field: 'dmapping', header: 'Mapping' ,  group:3, isDisplayed: true},)
        }
        if( list.mappedInfoSets[4]) {
          this.termMapcols.push( { field: 'etermId', header: 'Term ID' ,  group:4, isDisplayed: true},
          { field: 'etermName', header: 'Term Name' ,  group:4, isDisplayed: true},
         { field: 'edescription', header: 'Description' ,  group:4, isDisplayed: true},
         { field: 'eapping', header: 'Mapping' ,  group:4, isDisplayed: true},)
        }
        if( list.mappedInfoSets[5]) {
          this.termMapcols.push({ field: 'ftermId', header: 'Term ID' ,  group:5, isDisplayed: true},
          { field: 'ftermName', header: 'Term Name' ,  group:5, isDisplayed: true},
         { field: 'fdescription', header: 'Description' ,  group:5, isDisplayed: true},
         { field: 'fmapping', header: 'Mapping' ,  group:5, isDisplayed: true},)
        }
        this.termMapcols.push({ field: '', header: 'Options', isDisplayed: true , group:'Options'},)
      }



      onRowEdit(rowdata, index) {
        // this.actualTermList[index].isEditing = true;
        rowdata.isEditing = true;
        this.editTerms[rowdata.termId] = {... rowdata}
    }

    onRowCancel(rowdata, index) {
      // this.actualTermList[index].isEditing = false;
      rowdata.isEditing = false;
      delete this.editTerms[rowdata.termId];
      if(this.newRow) {
        this.actualTermList.shift();
      }
    }

    onRowDelete(rowdata, index) {
      this.msgs = [];
      var mapInfo = rowdata;
      this.confirmationService.confirm({
        message: 'Are you sure you want to delete this ' + mapInfo.termId + ' ?',
        header: 'Remove Term Id ' +  mapInfo.termId,
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
      this.manageTermService.removeTermMap(mapInfo).subscribe((response) => {
        if(response) {
          //this.termMapList[index].termId = re;
          // this.actualTermList[index].isEditing = false;
          rowdata.isEditing = false;
          window.scroll(0, 0);
          this.msgs = [{severity:'info', summary:'Term Map', detail:' Successfully removed Mapping information for the term '+ mapInfo.termId}];
          this.loadTermMappingInformationList();
          //this.getTermMapGroups();
        }
      });
      rowdata.isEditing = false;
    },
    reject: () => {
    }
});

    }
    addNewTermMapping() {
      var termMap:any = {};
      termMap.mappingInfo = {};
      termMap.termId = 'TBD'
      termMap.isEditing = true;
      termMap.isTermRule=false;
      termMap.mappedInfoSets = [];
      for (var i = 0; i < this.termMappingGroups.length; i++) {
          var mappingInfo:any = {};
          mappingInfo.mapName = this.termMappingGroups[i].mapName;
          termMap.mappedInfoSets.push(mappingInfo);
      }
      this.newRow = true;
      this.actualTermList.forEach(obj => {
        if(obj.termId === termMap.termId) {
          const counter: number = this.actualTermList.length + 1;

          termMap.termId = termMap.termId + ' '  + counter;
        }
      })
      this.actualTermList.unshift(termMap);
  };

    onRowSave(rowdata, index) {
      //this.editTerms[index] = rowdata
      //this.termMapList[index] = this.editTerms[rowdata.termId];
      // var mapInfo = this.actualTermList[index];
      var mapInfo = rowdata;
      this.msgs = [];
      this.manageTermService.saveTermMap(mapInfo).subscribe((response) => {
        if(response) {
          //this.termMapList[index].termId = re;
          // this.actualTermList[index].isEditing = false;
          rowdata.isEditing = false;
          this.loadTermMappingInformationList();
          //this.getTermMapGroups();
          window.scroll(0, 0);
          this.msgs = [{severity:'info', summary:'Term Map', detail:' Successfully saved Mapping information for the term '+ mapInfo.termId}];
        }
      });
      // this.actualTermList[index].isEditing = false;
      rowdata.isEditing = false;
      delete this.editTerms[rowdata.termId];
    //this.editTerms = [];
  }

addNewGroup() {
  this.errorMsg =  "";
  this.showError = false;
    if( !this.newGroupName || this.newGroupName.length ===0) {
      window.scroll(0, 0);
        this.errorMsg = 'Error","Please enter a valid term mapping group name.';
        this.showError = true;
        return;
    }
   this.manageTermService.addTermMappingGroup(this.newGroupName).subscribe((response) => {
        if (response.status) {
            var newData = {mapName: this.newGroupName};
            this.termMappingGroups.push(newData);
            this.newGroupName = "";
            this.actualTermList = [];
            this.topCol = [];
            this.getTermMapGroups();
            this.loadTermMappingInformationList();
            window.scroll(0, 0);
            this.msgs = [{severity:'info', summary:'Add new Group', detail:' Successfully added new Term Mapping group'}];
        }
        else{
          window.scroll(0, 0);
          this.errorMsg =  response.errorMessage;
          this.showError = true;
        }
    },
    function (err) {
      window.scroll(0, 0);
      this.errorMsg =  'Failed to add Term Mapping group.';
      this.showError = true;
    }
);
}

removeGroup(groupName: string) {
  this.msgs = [];
  this.errorMsg = "";
  this.showError = false;
  this.confirmationService.confirm({
    message: 'Are you sure you want to delete all the mapping info associated with the group ' + groupName + ' ?',
    header: 'Remove Group ' +  groupName,
    icon: 'pi pi-exclamation-triangle',
    accept: () => {
      this.manageTermService.removeTermMappingGroup(groupName).subscribe((response) => {
        if (response.status === true) {
            this.termMappingGroups = [];
            this.actualTermList = [];
            this.topCol = [];
            this.getTermMapGroups();
            this.loadTermMappingInformationList();
            window.scroll(0, 0);
            this.msgs = [{severity:'info', summary:'Successfully removed group and all the mapping associated with the group.', detail:''}];
        } else {
          window.scroll(0, 0);
          this.errorMsg = response.errorMessage;
          this.showError = true;
        }
      });

    },
    reject: () => {
    }
});

}

loadTermMappingInformationList () {
  this.manageTermService.getManageTermMap().subscribe((response) => {
    this.actualTermList = response;
    this.termMapcols = [];
    this.formatHeaders(response[0])
    });

};
    groupClick(group, index) {
     console.log(group, index);
      if(this.toogleCols.includes(group)) {
        this.termMappingGroups[index].isDisplayed = true
        this.topCol.splice(this.termMappingGroups.indexOf(group),0, { field: group.mapName, header: group.mapName });
        var removeIndex = this.toogleCols.indexOf(group);
        this.toogleCols.splice(removeIndex, 1);
        this.termMapcols.forEach(obj => {
          if(obj.group === index) {
            obj.isDisplayed = true;
          }
        })
      } else {
        this.termMapcols.forEach(obj => {
          if(obj.group === index) {
            obj.isDisplayed = false;
          }
        })
      this.termMappingGroups[index].isDisplayed = false
      this.topCol = this.topCol.filter(item => item.field !== group.mapName);
      //this.topCol.splice(index,1);
      this.toogleCols.push(group);
    }
    }

    isGroupVisible (mapName){

      for (var i = 0; i < this.termMappingGroups.length; i++) {
          if(mapName === this.termMappingGroups[i].mapName )
          {
              return this.termMappingGroups[i].isDisplayed;
          }
      }
      return true;
  }


}
