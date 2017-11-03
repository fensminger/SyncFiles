import {Injectable} from '@angular/core';
import {OnDestroy} from '@angular/core';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {Subject}    from 'rxjs/Subject';
import {Http, Response, Headers} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import { ConfigService } from '../config.service';


@Injectable()
export class SynchroRunningService implements OnDestroy {
  private headers = new Headers();
  private ws: $WebSocket = null;

  private syncFilesinfoSubject = new Subject<any>();

  public syncFilesinfo: any;
  public syncFilesinfoObservable = this.syncFilesinfoSubject.asObservable();

  constructor(private http: Http, private config : ConfigService) {
    this.headers.append('Content-Type', 'application/json');

    this.ws = new $WebSocket(config.urls.BACKEND_WEBSOCKET_API_ROOT_URL + "/websocket/syncfilesInfo");
    this.ws.send("Hello");
    this.ws.getDataStream().subscribe(
      res => {
        let msg: any = JSON.parse(res.data);

//        console.log("##### Socket " + res.data);
        for(let syncFilesInfo of msg) {
          if (syncFilesInfo.running) {
            if (syncFilesInfo.synchroReal.numberOfFiles > 0) {
              if (syncFilesInfo.percentWork < 20) {
                syncFilesInfo.percentWork = 20;
              } else {
                let percent = syncFilesInfo.synchroReal.numberOfFiles / syncFilesInfo.synchroResume.numberOfFiles * 100;
                syncFilesInfo.percentWork = 20 + percent*80/100;
                // console.log("Pourcentage : " + syncFilesInfo.percentWork);
              }
            } else if (syncFilesInfo.synchroResume.numberOfFiles > 0) {
              syncFilesInfo.percentWork = 15;
            } else if (syncFilesInfo.remoteResume.numberOfFiles > 0) {
              syncFilesInfo.percentWork = 10;
            } else if (syncFilesInfo.localResume.numberOfFiles > 0) {
              syncFilesInfo.percentWork = 5;
            }
          } else {
            syncFilesInfo.percentWork = 100;
          }
        }

        this.syncFilesinfo = msg;
        this.syncFilesinfoSubject.next(msg);
      },
      function (e) {
        console.log('Error: ' + e.message);
      },
      function () {
        console.log('Completed');
      }
    );
  }

  public ngOnDestroy() {
    this.ws.close(true);
  }

  public loadOne(id: String): Observable<any> {
    return this.http.get('/api/sync-files/param/load-one-msg/' + id)
      .map(this.extractData)
      .catch(this.handleError);
  }


  private extractData(res: Response) {
    if (res.status < 200 || res.status >= 300) {
      throw new Error('Response status: ' + res.status + ' : ' + res.text());
    }
    let body = res.json();
    return body || {};
  }

  private handleError(error: any) {
    // In a real world app, we might use a remote logging infrastructure
    let errMsg = error.message || 'Server error';
    console.error(errMsg); // log to console instead
    return Observable.throw(errMsg);
  }


}
