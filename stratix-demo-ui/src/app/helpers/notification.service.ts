import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {
  }

  action(message: string, action: string, duration: number) {
    this.snackBar.open(message, action, {
      duration: duration
    });
  }

  show(message: string, duration: number = 3000): void {
    this.snackBar.open(message, '', {
      duration: duration
    });
  }

  warn(message: string): void {
    this.snackBar.open(message, 'X', {panelClass: ['error']});
  }
}
