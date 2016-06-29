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
var platform_browser_1 = require('@angular/platform-browser');
var ng2_bootstrap_1 = require('ng2-bootstrap/ng2-bootstrap');
var SynchroFilesService_1 = require('./SynchroFilesService');
var confirm_dialog_1 = require('./confirm-dialog');
var router_deprecated_1 = require('@angular/router-deprecated');
var SynchroList = (function () {
    function SynchroList(fb, title, _synchroFilesService) {
        var _this = this;
        this._synchroFilesService = _synchroFilesService;
        this.isHttpRequest = false;
        this.confirmDialog = false;
        title.setTitle("SyncFiles - Liste des synchronisations");
        this._synchroFilesService.loadAll().subscribe(function (r) {
            _this.synchroFileList = r;
        }, function (e) {
            console.log("Error : " + e);
        });
    }
    SynchroList.prototype.openDialog = function () {
        this.confirmDialog = true;
    };
    SynchroList.prototype.confirmDelete = function (isConfirmed) {
        this.confirmDialog = false;
        if (isConfirmed) {
            this.remove(this._index, this._id);
        }
    };
    SynchroList.prototype.onSubmit = function (value) {
        console.log('you submitted value: ', value);
    };
    SynchroList.prototype.dialogConfirmRemove = function (index, id) {
        this._index = index;
        this._id = id;
        this.openDialog();
    };
    SynchroList.prototype.remove = function (index, id) {
        var _this = this;
        this.isHttpRequest = true;
        this._synchroFilesService.removeDetail(id).subscribe(function (r) {
            _this.isHttpRequest = false;
            _this.synchroFileList.splice(index, 1);
        }, function (e) {
            _this.isHttpRequest = false;
            console.log("Error remove : " + JSON.stringify(e));
        });
    };
    SynchroList = __decorate([
        core_1.Component({
            selector: 'synchro-list',
            viewProviders: [platform_browser_1.Title],
            providers: [SynchroFilesService_1.SynchroFilesService],
            directives: [ng2_bootstrap_1.AlertComponent, confirm_dialog_1.ConfirmDialog, common_1.CORE_DIRECTIVES, common_1.FORM_DIRECTIVES, router_deprecated_1.ROUTER_DIRECTIVES],
            template: "\n  <div class=\"panel panel-primary\">\n    <div class=\"panel-heading\">Liste des synchronisations</div>\n\n    <div class=\"panel-body\">\n        <table class=\"table table-striped\">\n            <thead>\n                <tr>\n                    <th>Nom</th>\n                    <th>R\u00E9p. ma\u00EEtre</th>\n                    <th>R\u00E9p. esclave</th>\n                    <th> </th>\n                </tr>\n            </thead>\n            <tbody>\n                <tr *ngFor=\"let synchro of synchroFileList;let i = index\">\n                    <td><a [routerLink]=\"['/Detail', {id : synchro.id}]\">{{synchro.name}}</a></td>\n                    <td>{{synchro.masterDir}}</td>\n                    <td>{{synchro.slaveDir}} </td>\n                    <td><a (click)=\"dialogConfirmRemove(i, synchro.id)\"><span class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\"></span></a></td>\n                </tr>\n            </tbody>\n        </table>\n    </div>\n  </div>\n  \n    <uif-dialog (confirmSelected)=\"confirmDelete($event)\" [display]=\"confirmDialog\" [dialogId]=\"'confirm'\">\n        Voulez-vous <b>supprimer</b> cette synchronisation ?\n    </uif-dialog>\n  "
        }), 
        __metadata('design:paramtypes', [common_1.FormBuilder, platform_browser_1.Title, SynchroFilesService_1.SynchroFilesService])
    ], SynchroList);
    return SynchroList;
}());
exports.SynchroList = SynchroList;
