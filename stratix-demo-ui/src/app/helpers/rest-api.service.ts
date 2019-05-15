import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {retry} from 'rxjs/operators';
import {environment} from '../../environments/environment';
import {Observable} from "rxjs";

@Injectable({providedIn: 'root'})
export class RestApiService {
  private httpClient: HttpClient;

  constructor(private http: HttpClient) {
    this.httpClient = http;
    console.info('API Url: ', environment.apiUrl);
  }

  deleteSomething(coinId: string): Observable<any> {
    return this.http.delete<any>(`${environment.apiUrl}/${coinId}`);
  }

  getSomething(id: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/${id}`)
      .pipe(retry(1));
  }
}

export class EventData<T> {
  constructor(public event: string, public data: T) {
  }
}


