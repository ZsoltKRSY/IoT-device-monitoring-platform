import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthUser } from '../../../core/models/auth-user';
import { User } from '../../../core/models/user';
import { UserRole } from '../../../core/models/user-role.enum';

@Component({
  selector: 'app-profile-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-modal.html',
  styleUrl: './profile-modal.css',
})
export class ProfileModal {
  @Input() authUser: AuthUser | null = null;
  @Input() user: User | null = null;
  @Output() close = new EventEmitter<void>();

  UserRole = UserRole;

  onClose() {
    this.close.emit();
  }

  onOverlayClick() {
    this.onClose();
  }

  onModalClick(event: Event) {
    event.stopPropagation();
  }

}
