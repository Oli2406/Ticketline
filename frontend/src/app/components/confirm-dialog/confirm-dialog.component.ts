import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

export enum ConfirmationDialogMode {
  confirm,
  delete
}

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.scss'
})
export class ConfirmDialogComponent {
  ConfirmationDialogMode = ConfirmationDialogMode;

  @Input() message= '?';
  @Input() mode: ConfirmationDialogMode = ConfirmationDialogMode.confirm;
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  get confirmText(): string {
    switch (this.mode) {
      case ConfirmationDialogMode.confirm:
        return 'Confirm';
      case ConfirmationDialogMode.delete:
        return 'Delete';
    }
  }

}
