import { Response } from '@angular/http';
import { Observable }     from 'rxjs/Rx';
import { environment } from '../environment';
import { ConfigService } from '../config.service';


//Headers HTTP
export const HEADER_X_SECRET:string = 'x-secret';
export const HEADER_X_TOKEN_ACCESS:string = 'x-tokenaccess';
export const HEADER_X_DIGEST:string = 'x-digest';
export const HEADER_X_ONCE:string = 'x-once';
export const HEADER_WWW_AUTHENTICATE:string = 'www-authenticate';
export const HEADER_AUTHENTICATION:string = 'authentication';

//Local storage keys
export const STORAGE_ACCOUNT_TOKEN:string = 'hmacApp-account';
export const STORAGE_SECURITY_TOKEN:string = 'hmacApp-security';

//Common http root api
export const BACKEND_API_PATH:string = '/api';
export const BACKEND_API_AUTHENTICATE_PATH:string = '/authenticate';

export class UrlMatcher {
    public static matches(url:string):boolean {
        return url.indexOf(BACKEND_API_PATH) !== -1
            && url.indexOf(BACKEND_API_PATH+BACKEND_API_AUTHENTICATE_PATH) === -1
            && url.indexOf("/api/config/loadAll") === -1;
    }
}

export class HttpUtils {

    constructor() {
    }

  protected extractData(res: Response) {
    let body = res.json();
    return body || { };
  }

  protected handleError (error: any) {
    // In a real world app, we might use a remote logging infrastructure
    // We'd also dig deeper into the error to get a better message
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg); // log to console instead
    return Observable.throw(errMsg);
  } 


    
}