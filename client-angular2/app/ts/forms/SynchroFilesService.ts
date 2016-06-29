import {Injectable} from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs';


@Injectable()
export class SynchroFilesService {
    http : Http;
    private headers = new Headers();

    constructor(http: Http) {
        this.http = http;
        this.headers.append('Content-Type', 'application/json');
    }

    public loadAll() : Observable<any> {
        return this.http.get('/api/sync-files/param/load-all')
            .map(this.extractData)
            .catch(this.handleError);
    }

    public loadOne(id :String) : Observable<any> {
        return this.http.get('/api/sync-files/param/load-one/'+id)
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

    public saveDetail(detail : any) : Observable<Response> {
        return this.http.post("/api/sync-files/param/save", JSON.stringify(detail), {headers:this.headers})
            .map(this.extractData)
            .catch(this.handleError);
    }

    public removeDetail(id : number) : Observable<Response> {
        return this.http.delete("/api/sync-files/param/remove/"+id, {headers:this.headers});
    }
}