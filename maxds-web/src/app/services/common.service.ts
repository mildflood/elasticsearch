import { Injectable, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, FormArray, FormControl, AbstractControl } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/components/common/messageservice';
import * as _ from 'lodash';
import { Subject } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class CommonService {

  public static logOutEventEmitter: EventEmitter<boolean> = new EventEmitter();
  public static systemErrorMsgEventEmitter: EventEmitter<string> = new EventEmitter();
  public static progressSpinnerEventEmitter: EventEmitter<boolean> = new EventEmitter();
  public static stringBetweenCurlyBracesExtractor: any = CommonService.extract(['{', '}']);
  public messages: any[];
  public static sharedPreferenceNotification: EventEmitter<number> = new EventEmitter();
  public static isTourGuideStart: Subject<any> = new Subject<any>();
  public static exportTabIndex: Subject<any> = new Subject<any>();
  constructor(private formBuilder: FormBuilder, private primeNgMessageService: MessageService) {
    this.messages = [];
  }

  public static setTourGuideStart(value) {
    CommonService.isTourGuideStart.next(value);
  }

  public static setExportTabIndex(value) {
    CommonService.exportTabIndex.next(value);
  }

  public static emitsharedPreferenceNotificationEvent(number) {
    CommonService.sharedPreferenceNotification.emit(number);
  }

  public static systemErrorMsgEvent(errorMsg: string) {
    CommonService.systemErrorMsgEventEmitter.emit(errorMsg);
  }

  public static progressSpinnerEvent(showOrHideFlag: boolean) {
    CommonService.progressSpinnerEventEmitter.emit(showOrHideFlag);
  }

  public static getValidLength(formValueArray: any[]) {
    let length = 0;
    formValueArray.forEach((form) => {
      if (form.uiAction !== 'delete') {
        length++;
      }
    });
    return length;
  }

  public static padNumber(element, length): string {
    let elementString = element.toString();
    if (elementString.length === length) {
      elementString = '0' + element;
    }
    return elementString;
  }

  private static convertToCSV(objArray) {
    const array = typeof objArray !== 'object' ? JSON.parse(objArray) : objArray;
    let str = '';
    for (let i = 0; i < array.length; i++) {
      let line = '';
      for (const index in array[i]) {
        if (array[i].hasOwnProperty(index)) {
          if (line !== '') {
            line += ',';
          }
          line += array[i][index];
        }
      }
      str += line + '\r\n';
    }
    return str;
  }

  public static exportCSVFile(headers, items, fileTitle) {
    if (headers) {
      items.unshift(headers);
    }
    const jsonObject = JSON.stringify(items);
    const csv = this.convertToCSV(jsonObject);
    const exportedFilename = fileTitle + '.csv' || 'export.csv';
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    if (navigator.msSaveBlob) { // IE 10+
      navigator.msSaveBlob(blob, exportedFilename);
    } else {
      const link = document.createElement('a');
      if (link.download !== undefined) { // feature detection
        // Browsers that support HTML5 download attribute
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', exportedFilename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    }
  }

  public static systemErrorHandler(processMsg: string, err: any) {
    CommonService.progressSpinnerEvent(false);
    const customErrorMessage = 'Sorry, something went wrong with the network connection. '
      + processMsg + '. Please try again later. ' +
      'If you continue to get this error, please contact Tech Support at 1-800-952-8889.';
    CommonService.systemErrorMsgEvent(customErrorMessage);
    console.error(err);
  }

  public static convertArrayToOptionList(arrayOfStrings: string[]): any[] {
    const optionsList = [];
    arrayOfStrings.forEach((entry: string) => {
      const optionEntry = { label: entry, value: entry };
      optionsList.push(optionEntry);
    });
    return optionsList;
  }

  public static mergeArrays(existingArray: any[], newEntriesArray: any[], placement: string, currentIndex: number) {
    const selectedIndex = (placement === 'After') ? currentIndex : currentIndex - 1;
    existingArray = _.cloneDeep(existingArray.splice(selectedIndex, 0, ...newEntriesArray));
  }

  public static compareDate(arg1: Date, arg2: Date): number {
    const date1 = new Date(arg1);
    const date2 = new Date(arg2);
    date1.setHours(12, 0, 0, 0);
    date2.setHours(12, 0, 0, 0);
    const sameDay = date1.getTime() === date2.getTime();
    if (sameDay) { return 0; }
    if (date1 > date2) { return 1; }
    if (date1 < date2) { return -1; }
  }

  public static extract([beg, end]) {
    const matcher = new RegExp(`${beg}(.*?)${end}`, 'gm');
    const normalise = (str) => str.slice(beg.length, end.length * -1);
    return function (str) {
      return str.match(matcher).map(normalise);
    };
  }

  public isViewOnly() {
    return false;
  }

  public cloneFormGroup(control: AbstractControl) {
    if (control instanceof FormControl) {
      return this.formBuilder.control(control.value);
    } else if (control instanceof FormGroup) {
      const copy = this.formBuilder.group({});
      Object.keys(control.getRawValue()).forEach(key => {
        copy.addControl(key, this.cloneFormGroup(control.controls[key]));
      });
      return copy;
    } else if (control instanceof FormArray) {
      const copy = this.formBuilder.array([]);
      control.controls.forEach(controlEntry => {
        copy.push(this.cloneFormGroup(controlEntry));
      });
      return copy;
    }
  }

  public getNextValidIndex(formValueArray: any[]) {
    let currentIndex = 0;
    if (formValueArray.length > 0) {
      currentIndex = formValueArray.length - 1;
      while (formValueArray[currentIndex].uiAction === 'delete' && currentIndex > 0) {
        currentIndex = currentIndex - 1;
      }
    }
    return currentIndex;
  }

  public getAllErrors(form: FormGroup | FormArray): { [key: string]: any; } | null {
    let hasError = false;
    const result = Object.keys(form.controls).reduce((errorObject, key) => {
      const control = form.get(key);
      let errors;
      if (control instanceof FormGroup || control instanceof FormArray) {
        if (control instanceof FormArray && control.controls.length === 0) {
          // Handling error for empty form array (when no offers are added to form)
          errors = control.errors;
        } else if (control instanceof FormArray) {
          const formArrayError: any = {};
          if (control.errors) {
            formArrayError.arrError = control.errors;
          }
          const arrEntriesErrors = this.getAllErrors(control);
          if (arrEntriesErrors) {
            formArrayError.arrEntriesErrors = arrEntriesErrors;
          }
          if (formArrayError.arrError || formArrayError.arrEntriesErrors) {
            errors = formArrayError;
          }
        } else {
          errors = this.getAllErrors(control);
        }
      } else {
        errors = control.errors;
      }
      if (errors) {
        errorObject[key] = errors;
        hasError = true;
      }
      return errorObject;
    }, {} as { [key: string]: any; });
    return hasError ? result : null;
  }


  public showGrowlConfirmMessage(message: any) {
    this.primeNgMessageService.clear();
    this.primeNgMessageService.add(message);
  }
}