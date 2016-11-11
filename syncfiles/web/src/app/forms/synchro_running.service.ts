import { Injectable } from '@angular/core';
import { OnDestroy } from '@angular/core';
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import { Subject }    from 'rxjs/Subject';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class SynchroRunningService implements OnDestroy {
  private headers = new Headers();
  private ws : $WebSocket = null;

  private syncFilesinfoSubject = new Subject<any>();

  public syncFilesinfo : any;
  public syncFilesinfoObservable = this.syncFilesinfoSubject.asObservable();

  constructor(private http: Http) {
    this.headers.append('Content-Type', 'application/json');

    this.ws = new $WebSocket("ws://localhost:9900/websocket/syncfilesInfo");
    this.ws.send("Hello");
    this.ws.getDataStream().subscribe(
      res => {
        let msg : any = JSON.parse(res.data);
        this.syncFilesinfo = msg;
        this.syncFilesinfoSubject.next(msg);
      },
      function(e) {
         console.log('Error: ' + e.message);
        },
      function() {
         console.log('Completed');
      }
    );
  }

  public ngOnDestroy() {
    this.ws.close(true);
  }

    public loadOne(id :String) : Observable<any> {
        return this.http.get('/api/sync-files/param/load-one-msg/'+id)
            .map(this.extractData)
            .catch(this.handleError);
    }


    private extractData(res: Response) {
        if (res.status < 200 || res.status >= 300) {
            throw new Error('Response status: ' + res.status + ' : ' + res.text());
        }
        let body = res.json();
        return body || { };
    }
    private handleError (error: any) {
        // In a real world app, we might use a remote logging infrastructure
        let errMsg = error.message || 'Server error';
        console.error(errMsg); // log to console instead
        return Observable.throw(errMsg);
    }

  

}
