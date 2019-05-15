import {Routes} from "@angular/router";
import {SplashPageComponent} from "../splash-page/splash-page.component";
import {AdminComponent} from "../admin/admin.component";

export const NavigationRoutes: Routes = [
  {path: '', component: SplashPageComponent},
  {path: 'admin', component: AdminComponent},
  {path: '**', redirectTo: ''}
];
