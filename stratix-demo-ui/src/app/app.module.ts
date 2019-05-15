import {BrowserModule} from '@angular/platform-browser';
import {ErrorHandler, Injector, NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule, HttpErrorResponse} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import {library} from '@fortawesome/fontawesome-svg-core';
import {fab} from '@fortawesome/free-brands-svg-icons';
import {fas} from '@fortawesome/free-solid-svg-icons';

import {
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatDialogModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatSnackBarModule,
  MatStepperModule,
  MatToolbarModule,
  MatTooltipModule
} from '@angular/material';
import {FlexLayoutModule} from '@angular/flex-layout';
import {RouterModule} from '@angular/router';
import {OverlayContainer} from '@angular/cdk/overlay';
import {FormsModule} from '@angular/forms';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {NavigationComponent} from './navigation/navigation.component';
import {LoginDialogComponent} from './auth/login-dialog/login-dialog.component';
import {environment} from '../environments/environment';
import {RestApiInterceptor} from './auth/auth-service.service';
import {NavigationRoutes} from "./navigation/navigation.routes";
import {SplashPageComponent} from './splash-page/splash-page.component';
import {AdminComponent} from './admin/admin.component'

export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: Error): void {
    if (error instanceof HttpErrorResponse) {
      // Handled via specialized error handler
      return;
    }

    if (environment.annoyingErrors) {
      console.error('Uncaught Error', error.message, error.name);
      window.alert('Check console logs, an uncaught error took place\n' + error);
    } else {
      console.error(error);
    }
  }
}

@NgModule({
  declarations: [
    AppComponent,
    LoginDialogComponent,
    NavigationComponent,
    SplashPageComponent,
    AdminComponent
  ],
  entryComponents: [LoginDialogComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FlexLayoutModule,
    FontAwesomeModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(NavigationRoutes, {useHash: true}),
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatListModule,
    MatRadioModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatStepperModule,
    MatToolbarModule,
    MatTooltipModule,
    MatMenuModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: RestApiInterceptor, multi: true},
    {provide: ErrorHandler, useClass: GlobalErrorHandler}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  public static Injector: Injector;
  constructor(overlayContainer: OverlayContainer, injector: Injector) {
    //overlayContainer.getContainerElement().classList.add("dark-theme");
    library.add(fab, fas);
    AppModule.Injector = injector;
  }
}
