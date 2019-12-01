import { Observable } from "rxjs/Observable";
import HttpResponse from "./HttpResponse";
import { Headers } from "./Headers";

interface IHttpClient {
    get(url:string, headers?: Headers):Observable<HttpResponse>
    post(url:string, body:{}, headers?: Headers):Observable<HttpResponse>
    put(url:string, body:{}, headers?: Headers):Observable<HttpResponse>
    delete(url:string, body: {}, headers?: Headers):Observable<HttpResponse>
}

export default IHttpClient