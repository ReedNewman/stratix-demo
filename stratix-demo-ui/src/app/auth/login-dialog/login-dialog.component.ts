import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {Authorization} from '../auth-service.service';
import {BaseComponent} from "../../helpers/base-component";

@Component({
  selector: 'login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.scss']
})
export class LoginDialogComponent extends BaseComponent {
  public email: string;
  public password: string;

  constructor(private dialogRef: MatDialogRef<LoginDialogComponent, Authorization>) {
    super();
  }

  login(): void {
    console.info("I got called", new Date(), this.email, this.password);
    this.authService.login(this.email, this.password)
      .subscribe((auth) => {
        console.info('Login Successful with', auth);
        this.dialogRef.close(auth);
      });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
