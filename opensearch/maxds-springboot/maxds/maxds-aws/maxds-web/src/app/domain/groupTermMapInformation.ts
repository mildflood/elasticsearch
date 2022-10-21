import { Injectable } from '@angular/core';
import {TermMapInformation} from './termMapInformation';
@Injectable()
export class GroupTermMapInformation {
    constructor(private termMap: TermMapInformation) {

    }
    public  termId: String;
    public  name: String;
    public  description: String;
      
    public  isEditing: Boolean = false;
    public  isTermRule: Boolean = true;
    
    public mappedInfoSets : TermMapInformation[];
}