import {Component, Input} from '@angular/core';
import {
    CORE_DIRECTIVES,
    FORM_DIRECTIVES,
    FormBuilder,
    ControlGroup,
    Validators, Control, NgControl
} from '@angular/common';

@Component({
  selector: 'sfc-input',
  directives: [CORE_DIRECTIVES, FORM_DIRECTIVES],
  template: `
      <div class="form-group">
        <label for="inputLabel" class="col-sm-2 control-label">{{label}}</label>
        <div class="col-sm-10" [ngClass]="{'has-error' : !name.valid}">
          <input type="text" class="form-control" id="inputLabel" placeholder="{{placeholder}}" [ngModel]="ctrlValue">
          <div *ngIf="!name.valid" class="text-danger">{{requireMsg}}</div>
        </div> 
      </div>
  `
})
export class SfcInput {
    @Input() label : String;
    @Input("require-msg") requireMsg : String;
    @Input() placeholder : String;
    @Input() public value : String;
    name : Control;
    ctrlValue : String;

    constructor(fb: FormBuilder) {
        this.name = fb.control('', Validators.required);
        this.ctrlValue = "Ok";
    }

    setValue(value) {
        //this.ctrlValue = value;
    }

}
