import {Injectable,Injector} from '@angular/core';
import {Http,Response,RequestOptionsArgs, Headers, Request, RequestOptions, ConnectionBackend} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {SecurityToken} from '../security/securityToken';
import * as AppUtils from '../utils/app.utils';
import {AccountEventsService} from '../account/account.events.service';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/share';
import {Observer} from 'rxjs/Observer';
import * as CryptoJS from 'crypto-js';

@Injectable()
export class HmacHttpClient extends Http {
    http:Http;
    accountEventsService:AccountEventsService;
    constructor(_backend: ConnectionBackend, _defaultOptions: RequestOptions,accountEventsService:AccountEventsService) {
        super(_backend,_defaultOptions);
        this.accountEventsService = accountEventsService;
    }
    addSecurityHeader(url:string,method:string,options: RequestOptionsArgs):void {

        if(AppUtils.UrlMatcher.matches(url)) {

            let securityToken:SecurityToken = new SecurityToken(JSON.parse(localStorage.getItem(AppUtils.STORAGE_SECURITY_TOKEN)));
            let date:string = new Date().toISOString();
            let secret:string = securityToken.secretKey;

            let message = method + url + date;
            options.headers.set(AppUtils.HEADER_AUTHENTICATION, securityToken.token);

            if (securityToken.isEncoding('HmacSHA256')) {
                options.headers.set(AppUtils.HEADER_X_DIGEST, CryptoJS.HmacSHA256(message, secret).toString());
            } else if (securityToken.isEncoding('HmacSHA1')) {
                options.headers.set(AppUtils.HEADER_X_DIGEST, CryptoJS.HmacSHA1(message, secret).toString());
            } else if (securityToken.isEncoding('HmacMD5')) {
                options.headers.set(AppUtils.HEADER_X_DIGEST, CryptoJS.HmacMD5(message, secret).toString());
            }
            options.headers.set(AppUtils.HEADER_X_ONCE, date);

            console.log('url',url);
            console.log('message',message);
            console.log('secret',secret);
            console.log('hmac message',options.headers.get(AppUtils.HEADER_X_DIGEST));
        }

    }
    setOptions(options?: RequestOptionsArgs):RequestOptionsArgs {
        if(!options) {
            options = {};
        }
        if(!options.headers) {
            options.headers = new Headers();
        }
        return options;
    }
    mapResponse(res:Response,observer:Observer<Response>):void {
        if(res.ok && res.headers) {
            let securityToken:SecurityToken = new SecurityToken(JSON.parse(localStorage.getItem(AppUtils.STORAGE_SECURITY_TOKEN)));
            if(securityToken) {
                securityToken.token = res.headers.get(AppUtils.HEADER_X_TOKEN_ACCESS);
                localStorage.setItem(AppUtils.STORAGE_SECURITY_TOKEN,JSON.stringify(securityToken));
            }
        }
        observer.next(res);
        observer.complete();
    }
    catchResponse(res:Response,observer:Observer<Response>):void {
        if(res.status === 403) {
            console.log('Unauthorized request:',res.text());
            this.accountEventsService.logout({error:res.text()});
        }
        observer.complete();
    }
    get(url: string, options?: RequestOptionsArgs): Observable<Response> {
        options = this.setOptions(options);
        this.addSecurityHeader(url,'GET',options);

        return Observable.create((observer:Observer<Response>) => {
            super.get(url, options)
                .subscribe((res:Response) => {
                    this.mapResponse(res,observer);
                },(res:Response) => {
                    this.catchResponse(res,observer);
                });
        });
    }
    delete(url: string, options?: RequestOptionsArgs): Observable<Response> {
        options = this.setOptions(options);
        this.addSecurityHeader(url,'DELETE',options);

        return Observable.create((observer:Observer<Response>) => {
            super.delete(url, options)
                .subscribe((res:Response) => {
                    this.mapResponse(res,observer);
                },(res:Response) => {
                    this.catchResponse(res,observer);
                });
        });
    }
    post(url: string, body: string, options?: RequestOptionsArgs): Observable<Response> {
        options = this.setOptions(options);
        this.addSecurityHeader(url,'POST',options);

        return Observable.create((observer:Observer<Response>) => {
            super.post(url,body,options)
                .subscribe((res:Response) => {
                    this.mapResponse(res,observer);
                },(res:Response) => {
                    this.catchResponse(res,observer);
                });
        });
    }
    put(url: string, body: string, options?: RequestOptionsArgs): Observable<Response> {
        options = this.setOptions(options);
        this.addSecurityHeader(url,'PUT',options);

        return Observable.create((observer:Observer<Response>) => {
            super.put(url,body,options)
                .subscribe((res:Response) => {
                    this.mapResponse(res,observer);
                },(res:Response) => {
                    this.catchResponse(res,observer);
                });
        });
    }
}
