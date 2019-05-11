import {Component} from '@angular/core';
import {MatDialog} from '@angular/material';
import {LoginDialogComponent} from '../auth/login-dialog/login-dialog.component';
import {BaseComponent} from "../helpers/base-component";

@Component({
  selector: 'navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent extends BaseComponent {
  constructor(private dialog: MatDialog) {
    super()
  }

  ngOnInit() {
    this.authService.onAuthChange()
      .subscribe(user => this.user = user)
  }

  openLoginDialog(): void {
    let ref = this.dialog
      .open(LoginDialogComponent, {
        autoFocus: true
        //width: '250px'
      })
      .afterClosed()
      .subscribe(user => console.info('Dialog closed and returned', user));
  }

  logout(): void {
    this.authService.logout();
  }
}
