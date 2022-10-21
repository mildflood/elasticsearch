import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home/homeComponent';
import { ProfileComponent } from './profile/profile.component';
import { ExportComponent } from './export/exportComponent';
import { AccuracyTestComponent } from './accutacytest/accuracyTestComponent';
import { ManageTermComponent } from './manageTerm/manageTermComponent';
import { ProcessingTermsComponent } from './processingTerms/processingTermsComponent';
import { AdminComponent } from './admin/adminComponent';
import { SubmitFeedbackComponent } from './submitFeedback/submitFeedbackComponent';
import { StatusComponent } from './status/statusComponent';
import { TermResultsComponent } from './term-results/term-results.component'
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HeaderComponent } from './header/header.component';
import { AuthGuard } from './guards/auth.guards';
import { FeaturehomeComponent } from './featurehome/featurehome.component';
import { CompleteCompanylistResolverService } from './resolvers/completecompanylist-resolver.service';
import { TermlistResolverService } from './resolvers/termlist-resolver.service';
import { TermRuleCategoryListResolverService } from './resolvers/termrulecategorylist-resolver.service';

const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full', runGuardsAndResolvers: 'always' },
  { path: 'home', component: FeaturehomeComponent, runGuardsAndResolvers: 'always' ,
                    resolve: { completeCompanyList: CompleteCompanylistResolverService,
							   termRuleList: TermlistResolverService,
							   termRuleCategoryList: TermRuleCategoryListResolverService }},
  // { path: 'home', component: HomeComponent, runGuardsAndResolvers: 'always'},
  { path: 'header', component: HeaderComponent, runGuardsAndResolvers: 'always' },
  { path: 'maxds', component: HomeComponent, runGuardsAndResolvers: 'always' },
  { path: 'termresults', component: TermResultsComponent, runGuardsAndResolvers: 'always' },
  { path: 'profile', component: ProfileComponent, runGuardsAndResolvers: 'always' },
  { path: 'export', component: ExportComponent, runGuardsAndResolvers: 'always',
                    resolve: { completeCompanyList: CompleteCompanylistResolverService,
                               termRuleList: TermlistResolverService}},
  { path: 'accuracyTest', component: AccuracyTestComponent, runGuardsAndResolvers: 'always' },
  { path: 'manageTerm', component: ManageTermComponent, runGuardsAndResolvers: 'always' },
  { path: 'processTerm', component: ProcessingTermsComponent, runGuardsAndResolvers: 'always' },
  { path: 'admin', component: AdminComponent, runGuardsAndResolvers: 'always' },
  { path: 'status', component: StatusComponent, runGuardsAndResolvers: 'always' },
  { path: 'submit', component: SubmitFeedbackComponent, runGuardsAndResolvers: 'always' },
  { path: 'logout', component: LoginComponent, runGuardsAndResolvers: 'always' },
  { path: 'login', component: LoginComponent, runGuardsAndResolvers: 'always' }];


export const routing: ModuleWithProviders = RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' });
