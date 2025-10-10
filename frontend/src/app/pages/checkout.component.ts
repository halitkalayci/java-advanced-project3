import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { CartService } from '../core/cart.service';

@Component({
  standalone: true,
  selector: 'app-checkout',
  imports: [CommonModule, RouterLink],
  template: `
  <div class="p-6 max-w-xl mx-auto space-y-4">
    <h2 class="text-xl font-semibold">Confirm Order</h2>
    <button class="py-3 w-full rounded-xl bg-black text-white hover:opacity-90" (click)="confirm()">
      Place order
    </button>
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

  constructor(private api: ApiService, private cart: CartService) {}

  confirm() {
    const items = this.cart.list().map((item) => ({ productId: item.product.id, quantity: item.qty }));

    this.api.createOrder({ items }).subscribe({
      next: (res) => {
        this.orderId = res.id;
        this.cart.clear();
      },
      error: (error) => {
        console.error(error);
      },
    });
  }
}
