import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { catchError, map, shareReplay } from 'rxjs/operators';
import { NotificationService } from '../helpers/notification.service';

@Injectable({providedIn: 'root'})
export class AuthService {
  public auth: Observable<Authorization>;
  private readonly subject: BehaviorSubject<Authorization>;
  private readonly guestAuthentication = new Authorization();

  constructor(private http: HttpClient) {
    let parse = JSON.parse(localStorage.getItem('auth'));
    if (parse !== null) {
      parse.expiration = new Date(parse.expiration);
    }
    let stored = parse ? Object.assign(new Authorization(), parse) : this.guestAuthentication;
    this.subject = new BehaviorSubject<Authorization>(stored);
    this.auth = this.subject.asObservable();
  }

  get authorization(): Authorization {
    let authorization = this.subject.value;
    if (authorization && +authorization.expiration < +new Date()) {
      console.debug('Expired authorization', authorization);
      this.logout();
    }

    return this.subject.value;
  }

  onAuthChange(): Subject<Authorization> {
    return this.subject;
  }

  login(email: string, password: string): Observable<Authorization> {
    let response = this.http.post<Authorization>(`${environment.apiUrl}/user/authenticate`, {email, password})
      .pipe(shareReplay(),
        catchError(error => {
          // failed logins for any reason should force logout
          console.warn('Login error', error, error.message);
          if (this.subject.value != this.guestAuthentication) {
            this.logout();
          }
          return throwError(error);
        }),
        map(auth => {
          localStorage.setItem('auth', JSON.stringify(auth));
          this.subject.next(auth);
          return auth;
        }))
    ;
    response.subscribe(user => {
      localStorage.setItem('auth', JSON.stringify(user));
    });
    return response;
  }

  logout() {
    localStorage.removeItem('auth');
    this.subject.next(this.guestAuthentication);
  }
}

@Injectable()
export class RestApiInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private notification: NotificationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let auth = this.authService.authorization;
    if (auth.token) {
      req = req.clone({setHeaders: {Authorization: `Token ${auth.token}`}});
    }

    return next.handle(req)
      .pipe(catchError(error => {
        if (error.status === 401) {
          this.authService.logout();
        }

        let errorMessage = '';
        if (error.error instanceof ErrorEvent) {
          // client-side error
          errorMessage = `Client Error: ${error.error.message}`;
        } else {
          // server-side error
          errorMessage = `Server Error: ${error.status}\nMessage: ${error.message}`;
        }
        console.error(errorMessage, error);
        this.notification.warn(errorMessage);
        return throwError(error);
      }));
  }
}

export class Authorization {
  token: string;
  firstName: string;
  lastName: string;
  admin: boolean;
  expiration: Date;
}

