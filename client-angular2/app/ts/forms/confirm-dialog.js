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
var ConfirmDialog = (function () {
    function ConfirmDialog() {
        this.confirmSelected = new core_1.EventEmitter();
    }
    ConfirmDialog.prototype.isDisplay = function () {
        return this.display;
    };
    ConfirmDialog.prototype.displayStatus = function () {
        if (this.display) {
            return "block";
        }
        else {
            return "none";
        }
    };
    ConfirmDialog.prototype.ok = function () {
        this.display = false;
        this.confirmSelected.emit(true);
    };
    ConfirmDialog.prototype.cancel = function () {
        this.display = false;
        this.confirmSelected.emit(false);
    };
    __decorate([
        core_1.Input('display'), 
        __metadata('design:type', Boolean)
    ], ConfirmDialog.prototype, "display", void 0);
    __decorate([
        core_1.Output(), 
        __metadata('design:type', core_1.EventEmitter)
    ], ConfirmDialog.prototype, "confirmSelected", void 0);
    ConfirmDialog = __decorate([
        core_1.Component({
            selector: 'uif-dialog',
            directives: [common_1.CORE_DIRECTIVES],
            template: "\n<div class=\"modal fade\" [ngClass]=\"{'in': isDisplay()}\" *ngIf=\"isDisplay()\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" style=\"display: block\">\n    <div class=\"modal-dialog\" role=\"document\">\n        <div class=\"modal-content\">\n            <div class=\"modal-header\">\n                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\" (click)=\"cancel()\"><span aria-hidden=\"true\">&times;</span></button>\n                <h4 class=\"modal-title\" id=\"myModalLabel\">Confirmation</h4>\n            </div>\n            <div class=\"modal-body\">\n                <ng-content></ng-content>\n            </div>\n            <div class=\"modal-footer\">\n                <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\" (click)=\"cancel()\">Non</button>\n                <button type=\"button\" class=\"btn btn-primary\" (click)=\"ok()\">Oui</button>\n            </div>\n        </div>\n    </div>\n</div>\n"
        }), 
        __metadata('design:paramtypes', [])
    ], ConfirmDialog);
    return ConfirmDialog;
}());
exports.ConfirmDialog = ConfirmDialog;
