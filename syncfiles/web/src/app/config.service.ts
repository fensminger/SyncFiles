import { Injectable } from '@angular/core';
import { Observable }     from 'rxjs/Rx';
import { Http, Response } from '@angular/http';
import {HttpUtils} from './utils/app.utils';
import { environment } from './environment';

@Injectable()
export class ConfigService {

  public urls : any = {BACKEND_API_ROOT_URL: '/api',
    BACKEND_WEBSOCKET_API_ROOT_URL: null};

  constructor(private http : Http) {
    let host = this.getHost();
    let portStr = "";
    if (host.port!=null) {
      let port : number = +host.port;
      if (port==4100) {
        port = 9900;
      }
      portStr = ":" + port;
    }

    this.urls.BACKEND_WEBSOCKET_API_ROOT_URL = "ws://" + host.hostname + portStr;
    console.log("URL WebSocket : " + this.urls.BACKEND_WEBSOCKET_API_ROOT_URL);
    this.loadConfig();
  }

  getHost() {
    var l = document.createElement("a");
    l.href = "/";
    return l;
  };

  public loadConfig() {
    let url = '/api/config/loadAll';
//    if (!environment.production) {
//      url = 'http://localhost:9900' + url;
//      this.urls.BACKEND_API_ROOT_URL = 'http://localhost:9900/api';
//    }
    this.http.get(url)
        .map(this.extractData)
        .catch(this.handleError).subscribe(config => {});
  }

  protected extractData(res: Response) {
    return res.json();
  }

  public log() {
    console.log("Test : " + this.urls.BACKEND_API_ROOT_URL);
  }

 public handleError (error: any) {
    // In a real world app, we might use a remote logging infrastructure
    // We'd also dig deeper into the error to get a better message
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg); // log to console instead
    return Observable.throw(errMsg);
  }
}
