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
var http_1 = require('@angular/http');
var Observable_1 = require('rxjs/Observable');
var SynchroFilesService = (function () {
    function SynchroFilesService(http) {
        this.headers = new http_1.Headers();
        this.http = http;
        this.headers.append('Content-Type', 'application/json');
    }
    SynchroFilesService.prototype.loadAll = function () {
        return this.http.get('/api/sync-files/param/load-all')
            .map(this.extractData)
            .catch(this.handleError);
    };
    SynchroFilesService.prototype.loadOne = function (id) {
        return this.http.get('/api/sync-files/param/load-one/' + id)
            .map(this.extractData)
            .catch(this.handleError);
    };
    SynchroFilesService.prototype.extractData = function (res) {
        if (res.status < 200 || res.status >= 300) {
            throw new Error('Response status: ' + res.status + ' : ' + res.text());
        }
        var body = res.json();
        return body || {};
    };
    SynchroFilesService.prototype.handleError = function (error) {
        var errMsg = error.message || 'Server error';
        console.error(errMsg);
        return Observable_1.Observable.throw(errMsg);
    };
    SynchroFilesService.prototype.saveDetail = function (detail) {
        return this.http.post("/api/sync-files/param/save", JSON.stringify(detail), { headers: this.headers });
    };
    SynchroFilesService.prototype.removeDetail = function (id) {
        return this.http.delete("/api/sync-files/param/remove/" + id, { headers: this.headers });
    };
    SynchroFilesService = __decorate([
        core_1.Injectable(), 
        __metadata('design:paramtypes', [http_1.Http])
    ], SynchroFilesService);
    return SynchroFilesService;
}());
exports.SynchroFilesService = SynchroFilesService;
