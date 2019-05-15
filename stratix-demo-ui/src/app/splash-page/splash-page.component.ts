import {Component} from '@angular/core';
import {BaseComponent} from "../helpers/base-component";

@Component({
  selector: 'app-splash-page',
  templateUrl: './splash-page.component.html',
  styleUrls: ['./splash-page.component.scss']
})
export class SplashPageComponent extends BaseComponent {

  ngAfterViewInit() {
    console.info("Splash Page loaded");
  }

}
