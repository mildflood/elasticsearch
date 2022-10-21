import { Injectable } from '@angular/core';
@Injectable()
export class EditExpression {
    expression: any = '';
    axisExpression: string = '';
    memberExpression: string = '';
    type: string = '';
    conceptName: string = null;
    NamedAxisList: any[] = [];
    NamedMemberList: any[] = [];
    conceptMatchMultipleList: any[] = [];
    virtualFactMemberExclusionList: any[] = [];
    useVirtualParentNew: boolean;
    virtualFactAxis: string = null;
    virtualFactMemberExclusions: any[] = [];
    useMaxAxisCount: boolean;
    maxAxisCount: number;
    axisType: string;
    dimensionExpressionSets: any[] = [];
    balType: string  = null;
    perType: string  = null;
    isShareItemType: boolean;
    containsWords : any[]= [];
    doesNotContainsWords:  any[] = [];
    usePositiveValuesOnly: boolean = false;
    useNegativeValuesOnly: boolean = false;
    reverseNegativeValues: boolean = false;
    formulaList:  any[] = [];
    rank: number;
}