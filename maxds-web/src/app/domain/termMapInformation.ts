import { Injectable } from '@angular/core';
@Injectable()
export class TermMapInformation {
    public id: string;  
    public termId: string;
    public mapName: string;
    public mapTermId: string;
    public mapTermName: string;
    public mapTermDescription: string;
    public mapTermMappingInfo: string;
}