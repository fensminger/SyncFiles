"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var core_1 = require('@angular/core');
var common_1 = require('@angular/common');
var SfcInput = (function () {
    function SfcInput(fb) {
        this.name = fb.control('', common_1.Validators.required);
        this.ctrlValue = "Ok";
    }
    SfcInput.prototype.setValue = function (value) {
    };
    __decorate([
        core_1.Input(), 
        __metadata('design:type', String)
    ], SfcInput.prototype, "label", void 0);
    __decorate([
        core_1.Input("require-msg"), 
        __metadata('design:type', String)
    ], SfcInput.prototype, "requireMsg", void 0);
    __decorate([
        core_1.Input(), 
        __metadata('design:type', String)
    ], SfcInput.prototype, "placeholder", void 0);
    __decorate([
        core_1.Input(), 
        __metadata('design:type', String)
    ], SfcInput.prototype, "value", void 0);
    SfcInput = __decorate([
        core_1.Component({
            selector: 'sfc-input',
            directives: [common_1.CORE_DIRECTIVES, common_1.FORM_DIRECTIVES],
            template: "\n      <div class=\"form-group\">\n        <label for=\"inputLabel\" class=\"col-sm-2 control-label\">{{label}}</label>\n        <div class=\"col-sm-10\" [ngClass]=\"{'has-error' : !name.valid}\">\n          <input type=\"text\" class=\"form-control\" id=\"inputLabel\" placeholder=\"{{placeholder}}\" [ngModel]=\"ctrlValue\">\n          <div *ngIf=\"!name.valid\" class=\"text-danger\">{{requireMsg}}</div>\n        </div> \n      </div>\n  "
        }), 
        __metadata('design:paramtypes', [common_1.FormBuilder])
    ], SfcInput);
    return SfcInput;
}());
exports.SfcInput = SfcInput;
