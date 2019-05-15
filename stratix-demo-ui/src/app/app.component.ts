import { Component } from '@angular/core';
import {BaseComponent} from "./helpers/base-component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent extends BaseComponent {
    /*
    let ws = webSocket('ws://localhost:4567/session');
    ws.next('Hello Moto x4');
    //ws.subscribe((msg) => console.log('message received: ' + msg));
     ws.subscribe(data => {
      console.log('Subscription data: ', data);
    });
    */

    /*
    let ws = new WebsocketService();
    let subject = ws.connect('ws://localhost:4567/session');
    let subscription = subject.subscribe((value: MessageEvent) => {
      console.log('Type', typeof value.data);
      console.log('Value', value.data);
    });
    subject.next("Hello Moto x4");
    */
}
