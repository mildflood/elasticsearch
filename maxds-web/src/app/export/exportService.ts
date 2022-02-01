
import {map} from 'rxjs/operators';
import { Http, Response, Headers } from '@angular/http';
import { Injectable } from '@angular/core';
import 'rxjs/Rx';

@Injectable()
export class ExportService {

    private exportTermResultsUrl = '/api/ExportTermResults';
    private exportTermCoverageUrl = '/api/ExportTermCoverage'

    
    // private exportTermResultsUrl = 'https://localhost:18081/api/ExportTermResults';
    // private exportTermCoverageUrl = 'https://localhost:18081/api/ExportTermCoverage'

    constructor(private http: Http) { }

    getResultsForExportTermResults(trp) {
        console.log('ExportService : getResultsForExportTermResults()...');
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        const body = JSON.stringify({
            includeFiscalYears: trp.includeFiscalYears, includeFiscalQuarters: trp.includeFiscalQuarters, startYear: trp.startYear, endYear: trp.endYear, entityId: trp.entityId,
            termId: trp.termId, entityList: trp.entityList, termIdList: trp.termIdList, exportType: trp.exportType, isForAllEntities: trp.isForAllEntities, includeValidationInfos: trp.includeValidationInfos
        });
        return this.http.put(`${this.exportTermResultsUrl}`, body, {
            headers: headers
        }).pipe(map((data: Response) => data.json()))
    }

    getResultsForExportCoverage(trp) {
        console.log('ExportService : getResultsForExportCoverage()...');
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        const body = JSON.stringify({
            includeFiscalYears: trp.includeFiscalYears, includeFiscalQuarters: trp.includeFiscalQuarters, startYear: trp.startYear, endYear: trp.endYear, entityId: trp.entityId,
            termId: trp.termId, entityList: trp.entityList, termIdList: trp.termIdList, exportType: trp.exportType, isForAllEntities: trp.isForAllEntities, includeValidationInfos: trp.includeValidationInfos
        });
        return this.http.put(`${this.exportTermCoverageUrl}`, body, {
            headers: headers
        }).pipe(map((data: Response) => data.json()))
    }
}