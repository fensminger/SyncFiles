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
var platform_browser_dynamic_1 = require('@angular/platform-browser-dynamic');
var router_deprecated_1 = require('@angular/router-deprecated');
var common_1 = require('@angular/common');
var http_1 = require('@angular/http');
var synchro_list_1 = require('./forms/synchro_list');
var synchro_detail_1 = require('./forms/synchro_detail');
var SynchroFilesService_1 = require('./forms/SynchroFilesService');
require('../../node_modules/bootstrap/dist/css/bootstrap.css');
require('../images/ng-book-2-minibook.png');
require('../images/favicon-32x32.png');
require('../images/favicon.ico');
var SynchroApp = (function () {
    function SynchroApp(router) {
        this.router = router;
        this.menuClosed = true;
    }
    SynchroApp.prototype.openCloseMobileMenu = function (elt) {
        this.menuClosed = !this.menuClosed;
        if (elt) {
            console.log("open or close menu");
        }
        else {
            console.log("Not mobile menu");
        }
    };
    SynchroApp = __decorate([
        core_1.Component({
            selector: 'synchro-app',
            directives: [router_deprecated_1.ROUTER_DIRECTIVES],
            template: "\n<style>\n</style>\n        <nav class=\"navbar navbar-default\">\n          <div class=\"container-fluid\">\n            <!-- Brand and toggle get grouped for better mobile display -->\n            <div class=\"navbar-header\">\n              <button type=\"button\" class=\"navbar-toggle\" [ngClass]=\"{collapsed: menuClosed}\" (click)=\"openCloseMobileMenu(menu)\">\n                <span class=\"sr-only\">Nav</span>\n                <span class=\"icon-bar\"></span>\n                <span class=\"icon-bar\"></span>\n                <span class=\"icon-bar\"></span>\n              </button>\n              <span class=\"navbar-brand\" href=\"#\">Home</span>\n            </div>\n        \n            <!-- Collect the nav links, forms, and other content for toggling -->\n            <div class=\"navbar-collapse\" [ngClass]=\"{collapse: menuClosed}\" #menu>\n              <ul class=\"nav navbar-nav\">\n                <li><a [routerLink]=\"['/Home']\" (click)=\"openCloseMobileMenu()\">Home</a></li>\n                <li><a [routerLink]=\"['/Detail']\" (click)=\"openCloseMobileMenu()\">Cr\u00E9ation d'une synchronisation</a></li>\n              </ul>\n            </div><!-- /.navbar-collapse -->\n          </div><!-- /.container-fluid -->\n        </nav>\n        <router-outlet></router-outlet>\n  "
        }),
        router_deprecated_1.RouteConfig([
            { path: '/', name: 'root', redirectTo: ['/Home'] },
            { path: '/home', name: 'Home', component: synchro_list_1.SynchroList },
            { path: '/detail', name: 'Detail', component: synchro_detail_1.SynchroDetail },
            { path: '/detail/:id', name: 'DetailUpdate', component: synchro_detail_1.SynchroDetail }
        ]), 
        __metadata('design:paramtypes', [router_deprecated_1.Router])
    ], SynchroApp);
    return SynchroApp;
}());
platform_browser_dynamic_1.bootstrap(SynchroApp, [
    router_deprecated_1.ROUTER_PROVIDERS,
    http_1.HTTP_PROVIDERS,
    SynchroFilesService_1.SynchroFilesService,
    core_1.provide(common_1.LocationStrategy, { useClass: common_1.HashLocationStrategy }),
]);
