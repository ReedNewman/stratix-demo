import {Authorization, AuthService} from "../auth/auth-service.service";
import {OnInit} from "@angular/core";
import {RestApiService} from "./rest-api.service";
import {AppModule} from "../app.module";
import {NotificationService} from "./notification.service";

export abstract class BaseComponent implements OnInit {
  public user: Authorization;
  protected authService: AuthService;
  protected restApi: RestApiService;
  protected notificationService: NotificationService;

  public constructor() {
    this.authService = AppModule.Injector.get(AuthService);
    this.restApi = AppModule.Injector.get(RestApiService);
    this.notificationService = AppModule.Injector.get(NotificationService);

    this.user = this.authService.authorization;
    this.authService.onAuthChange().subscribe(user => {
      this.user = user;
    });
  }

  ngOnInit(): void {
  }
}
