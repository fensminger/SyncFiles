import {Injectable} from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import {URLSearchParams} from '@angular/http';
import {TreeNode} from 'primeng/primeng';

@Injectable()
export class SynchroFilesService {
    private headers = new Headers();

    constructor(private http: Http) {
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
        //console.log("extractData:"+res.text())
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
        return this.http.delete("/api/sync-files/param/remove/"+id);
    }

    public synchronize(idParamSyncFiles : string) : Observable<Response> {
        return this.http.post("/api/sync-files/synchronize/"+idParamSyncFiles, "", {headers:this.headers})
            .map((res: Response) => {
                return res.text;
            })
            .catch(this.handleError);
    }


    public viewList(id :string, originFile : string, pageNumber : number, pageSize : number, params : URLSearchParams) : Observable<any> {
        return this.http.get("/api/sync-files/view/list/"+id+"/"+originFile+"/"+pageNumber+"/"+pageSize
                , {
                    headers:this.headers,
                    search: params
                }
                )
            .map(this.extractData)
            .catch(this.handleError);
    }

    public viewTree(id :string, originFile : string, params : URLSearchParams) : Observable<any> {
        return this.http.get("/api/sync-files/view/tree/"+id+"/"+originFile
                , {
                    headers:this.headers,
                    search: params
                }
                )
            .map(this.extractData)
            .map(fileInfoList => {
                let res : TreeNode[] = [];
                for(let fileInfo of fileInfoList) {
                    let treeNode : TreeNode = {};
                    treeNode.data = fileInfo;
                    treeNode.leaf = !fileInfo.directory;
                    res.push(treeNode);
                }
                return res;
            })
            .catch(this.handleError);

    }

    public calcSchedule(schedule : any) : Observable<any> {
      return this.http.post("/api/sync-files/param/schedule/calc", schedule, {headers:this.headers})
        .map(this.extractData)
        .catch(this.handleError);
    }
}
