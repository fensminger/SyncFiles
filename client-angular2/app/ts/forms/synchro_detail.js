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
var sfc_input_1 = require('./sfc_input');
var SynchroFilesService_1 = require('./SynchroFilesService');
var router_deprecated_1 = require("@angular/router-deprecated");
var SynchroDetail = (function () {
    function SynchroDetail(_synchroFilesService, _routeParams, _fb, title) {
        this._synchroFilesService = _synchroFilesService;
        this._routeParams = _routeParams;
        this._fb = _fb;
        this.model = {
            includeExcludePatterns: []
        };
        this.modelModified = {
            name: null,
            cronExp: null,
            masterDir: null,
            slaveDir: null,
            includeExcludePatterns: []
        };
        this.includeExcludePatterns = [];
        this.alerts = [];
        title.setTitle("SyncFiles - Detail d'une synchronisation");
        this.id = _routeParams.get('id');
        console.log("Paramètre" + this.id);
        this.synchroForm = _fb.group({
            'name': ['', common_1.Validators.required],
            'cronExp': [''],
            'masterDir': ['', common_1.Validators.required],
            'slaveDir': ['', common_1.Validators.required],
        });
    }
    SynchroDetail.prototype.ngOnInit = function () {
        var _this = this;
        if (this.id != null) {
            this.isHttpRequest = true;
            this._synchroFilesService.loadOne(this.id).subscribe(function (r) {
                _this.isHttpRequest = false;
                _this.model = r;
                if (_this.model.includeExcludePatterns) {
                    _this.includeExcludePatterns.length = _this.model.includeExcludePatterns.length;
                }
                _this.modelModified = JSON.parse(JSON.stringify(_this.model));
                console.log("Chargement de : " + JSON.stringify(_this.model));
            }, function (e) {
                _this.isHttpRequest = false;
                console.log("Error : " + e);
            });
        }
    };
    SynchroDetail.prototype.createSynchro = function (value) {
        var _this = this;
        console.log('Synchro à sauvegarder : ' + JSON.stringify(this.model));
        this.isHttpRequest = true;
        value.version = this.version;
        this.modelModified.name = this.synchroForm.find('name').value;
        this.modelModified.cronExp = this.synchroForm.find('cronExp').value;
        this.modelModified.masterDir = this.synchroForm.find('masterDir').value;
        this.modelModified.slaveDir = this.synchroForm.find('slaveDir').value;
        this._synchroFilesService.saveDetail(this.modelModified).subscribe(function (r) {
            _this.isHttpRequest = false;
            _this.model = r;
            _this.modelModified = JSON.parse(JSON.stringify(_this.model));
            console.log("OK" + JSON.stringify(r));
            _this.alerts.push({ msg: 'Sauvegarde effectu\u00E9e avec succ\u00E8s', type: 'info', closable: true });
        }, function (e) {
            _this.isHttpRequest = false;
            console.log("Error : " + JSON.stringify(e));
            _this.alerts.push({ msg: 'Impossible de sauvegarder...', type: 'error', closable: true });
        });
    };
    SynchroDetail.prototype.closeAlert = function (i) {
        this.alerts.splice(i, 1);
    };
    SynchroDetail.prototype.addPattern = function () {
        if (!this.modelModified.includeExcludePatterns) {
            this.model.includeExcludePatterns = [];
            this.modelModified.includeExcludePatterns = [];
            this.includeExcludePatterns = [];
        }
        this.model.includeExcludePatterns.push("");
        this.modelModified.includeExcludePatterns.push("");
        this.includeExcludePatterns.push("");
        console.log("Model : " + JSON.stringify(this.modelModified));
    };
    SynchroDetail.prototype.chgPattern = function (i, event) {
        this.modelModified.includeExcludePatterns[i] = event.target.value;
        console.log("Model : " + JSON.stringify(this.modelModified));
    };
    SynchroDetail = __decorate([
        core_1.Component({
            selector: 'synchro-detail',
            viewProviders: [platform_browser_1.Title],
            providers: [SynchroFilesService_1.SynchroFilesService],
            directives: [ng2_bootstrap_1.AlertComponent, sfc_input_1.SfcInput, common_1.CORE_DIRECTIVES, common_1.FORM_DIRECTIVES],
            template: require('./synchro_detail.html')
        }), 
        __metadata('design:paramtypes', [SynchroFilesService_1.SynchroFilesService, router_deprecated_1.RouteParams, common_1.FormBuilder, platform_browser_1.Title])
    ], SynchroDetail);
    return SynchroDetail;
}());
exports.SynchroDetail = SynchroDetail;