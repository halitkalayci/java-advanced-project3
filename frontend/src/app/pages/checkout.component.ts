import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { CartService } from '../core/cart.service';
import { ToastService } from '../core/toast.service';

@Component({
  standalone: true,
  selector: 'app-checkout',
  imports: [CommonModule, RouterLink],
  template: `
  <div class="p-6 max-w-xl mx-auto space-y-4">
    <h2 class="text-xl font-semibold">Confirm Order</h2>
    <button
      class="py-3 w-full rounded-xl bg-black text-white hover:opacity-90 disabled:opacity-50"
      [disabled]="submitting || empty"
      (click)="confirm()"
    >
      {{ submitting ? 'Placing order…' : 'Place order' }}
    </button>
    <p *ngIf="empty" class="text-sm text-gray-500">Your cart is empty.</p>
    <div class="mt-4" *ngIf="orderId">
      Order created: <b>{{ orderId }}</b>
      <div class="mt-2">
        <a routerLink="/products" class="text-blue-600 hover:underline">Back to Products</a>
      </div>
    </div>
  </div>
  `,
})
export class CheckoutComponent {
  orderId?: string;
  submitting = false;

  private readonly api = inject(ApiService);
  private readonly cart = inject(CartService);
  private readonly toast = inject(ToastService);

  get empty(): boolean {
    return this.cart.list().length === 0;
  }

  confirm() {
    if (this.submitting || this.empty) return;
    this.submitting = true;
    const items = this.cart.list().map((item) => ({ productId: item.product.id, quantity: item.qty }));

    this.api.createOrder({ items }).subscribe({
      next: (res) => {
        this.orderId = res.id;
        this.cart.clear();
        this.submitting = false;
        this.toast.success('Order placed successfully');
      },
      error: () => {
        // The interceptor already surfaces a toast; just re-enable the button.
        this.submitting = false;
      },
    });
  }
}
