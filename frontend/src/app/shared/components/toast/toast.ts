import { Component, computed } from '@angular/core';
import { ToastService } from '../../../core/services/toast.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.html',
  styleUrl: './toast.css',
})
export class Toast {
  constructor(public toastService: ToastService) { }

  toasts = computed(() => this.toastService.toasts());

  close(id: string) {
    this.toastService.remove(id);
  }

}
