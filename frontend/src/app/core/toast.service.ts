import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: number;
  text: string;
  type: 'error' | 'success' | 'info';
}

/** Lightweight global notification store used by the interceptor and pages. */
@Injectable({ providedIn: 'root' })
export class ToastService {
  private seq = 0;
  readonly toasts = signal<Toast[]>([]);

  show(text: string, type: Toast['type'] = 'info'): void {
    const id = ++this.seq;
    this.toasts.update((list) => [...list, { id, text, type }]);
    setTimeout(() => this.dismiss(id), 5000);
  }

  error(text: string): void {
    this.show(text, 'error');
  }

  success(text: string): void {
    this.show(text, 'success');
  }

  dismiss(id: number): void {
    this.toasts.update((list) => list.filter((t) => t.id !== id));
  }
}
