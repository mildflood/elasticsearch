import { BrowserModule, DomSanitizer } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http'
import { AppComponent } from './app.component';
import { FormsModule, FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { routing } from './app.routing';
import { ConfirmationService } from 'primeng/api';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { ChartModule } from 'primeng/chart';
import { NgIdleKeepaliveModule } from '@ng-idle/keepalive';
import { MomentModule } from 'angular2-moment';
import { TreeTableModule } from 'primeng/treetable';
import { MorrisJsModule } from 'angular-morris-js';
import { AngularDraggableModule } from 'angular2-draggable';
import { JoyrideModule } from 'ngx-joyride';


import {
  DialogModule, MultiSelectModule, RadioButtonModule,
  AccordionModule, CheckboxModule, DropdownModule, ButtonModule, PanelModule, TabViewModule, SidebarModule,
  PaginatorModule, ProgressSpinnerModule, InputTextareaModule, TriStateCheckboxModule, ConfirmDialogModule, TreeModule,
  MessageModule, MessagesModule, CalendarModule, ListboxModule, AutoCompleteModule, SelectButtonModule, SliderModule, FileUploadModule,
  InputSwitchModule, CardModule
} from 'primeng/primeng';

import { HomeComponent } from './home/homeComponent';
import { HomeService } from './home/homeService';
import { ProfileComponent } from './profile/profile.component';
import { ProfileService } from './services/profile.service';
import { ExportComponent } from './export/exportComponent';
import { ExportService } from './export/exportService';
import { AccuracyTestComponent } from './accutacytest/accuracyTestComponent';
import { AccuracyTestService } from './accutacytest/accuracyTestService';
import { ManageTermComponent } from './manageTerm/manageTermComponent';
import { ManageTermService } from './manageTerm/manageTermService';
import { ProcessingTermsComponent } from './processingTerms/processingTermsComponent';
import { ProcessingTermsService } from './processingTerms/processingTermsService';
import { AdminComponent } from './admin/adminComponent';
import { SubmitFeedbackComponent } from './submitFeedback/submitFeedbackComponent';
import { SubmitFeedbackService } from './submitFeedback/submitFeedbackService';
import { StatusComponent } from './status/statusComponent';
import { StatusService } from './status/statusService';

import { Status } from './domain/status';
import { TermMap } from './domain/termMap';
import { Home } from './domain/home';
import { UtilService } from './utils/utilService';
import { MappedEntity } from './domain/mappedEntity';
import { TermRule } from './domain/termRule';
import { EditExpression } from './domain/editExpression';
import { TermMapInformation } from './domain/termMapInformation';
import { GroupTermMapInformation } from './domain/groupTermMapInformation';

import { TermResultsComponent } from './term-results/term-results.component';
import { LoginComponent } from './login/login.component';
import { HeaderComponent } from './header/header.component';
import { FeaturehomeComponent } from './featurehome/featurehome.component';
import { ResizableDirective } from './featurehome/resizable.directive';
import { AngularSplitModule } from 'angular-split';
import { HighlightPipe } from './featurehome/highlight.pipe';

import { AngularD3TreeLibModule } from 'angular-d3-tree';
import { CreateProfileComponent } from './profile/create-profile/create-profile.component';
import { SharePreferencesComponent } from './profile/share-preferences/share-preferences.component';

import { DialogModalModule } from './_dialogmodal';


@NgModule({
  declarations: [
    HomeComponent,
    ProfileComponent,
    CreateProfileComponent,
    ExportComponent,
    AccuracyTestComponent,
    ManageTermComponent,
    ProcessingTermsComponent,
    AdminComponent,
    SubmitFeedbackComponent,
    StatusComponent,
    AppComponent,
    LoginComponent,
    HeaderComponent,
    FeaturehomeComponent,
    TermResultsComponent,
    ResizableDirective,
    HighlightPipe,
    SharePreferencesComponent
  ],
  imports: [
    BrowserModule, FormsModule, HttpModule, AccordionModule, BrowserAnimationsModule, RadioButtonModule, CardModule,
    routing, DialogModule, MultiSelectModule, CheckboxModule, DropdownModule, ButtonModule, PanelModule, TabViewModule, ReactiveFormsModule,
    SidebarModule, PaginatorModule, ProgressSpinnerModule, InputTextareaModule, TriStateCheckboxModule, TableModule, TooltipModule, TreeModule, ChartModule,
    ConfirmDialogModule, MessageModule, MessagesModule, CalendarModule, ListboxModule, AutoCompleteModule, SelectButtonModule, SliderModule, InputSwitchModule,
    FileUploadModule, HttpClientModule, NgIdleKeepaliveModule.forRoot(), MomentModule, TreeTableModule, JoyrideModule.forRoot(),
    AngularSplitModule.forRoot(), AngularD3TreeLibModule, MorrisJsModule, AngularDraggableModule, DialogModalModule

  ],
  providers: [HomeService, ProfileService, ExportService, AccuracyTestService, ManageTermService, ProcessingTermsService, SubmitFeedbackService, StatusService, UtilService,
    ConfirmationService, MessageModule, Status, TermMap, Home, MappedEntity, StatusService, TermRule, EditExpression, TermMapInformation, GroupTermMapInformation,
    { provide: LocationStrategy, useClass: HashLocationStrategy }, HeaderComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
