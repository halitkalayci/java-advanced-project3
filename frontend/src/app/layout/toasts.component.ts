import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../core/toast.service';

@Component({
  standalone: true,
  selector: 'app-toasts',
  imports: [CommonModule],
  template: `
    <div class="fixed top-16 right-4 z-50 flex flex-col gap-2 w-80">
      <div
        *ngFor="let toast of toastService.toasts()"
        class="rounded-xl shadow px-4 py-3 text-sm text-white flex justify-between items-start gap-3"
        [ngClass]="{
          'bg-red-600': toast.type === 'error',
          'bg-green-600': toast.type === 'success',
          'bg-gray-800': toast.type === 'info'
        }"
      >
        <span>{{ toast.text }}</span>
        <button class="opacity-80 hover:opacity-100" (click)="toastService.dismiss(toast.id)">✕</button>
      </div>
    </div>
  `,
})
export class ToastsComponent {
  readonly toastService = inject(ToastService);
}
