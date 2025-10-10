import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from '../core/cart.service';

@Component({
  standalone: true,
  selector: 'app-cart',
  imports: [CommonModule, RouterLink],
  template: `
  <div class="p-6 max-w-3xl mx-auto space-y-4">
    <h2 class="text-xl font-semibold">Cart</h2>
    <ng-container *ngIf="(cart.items$ | async) as items; else empty">
      <div
        *ngFor="let item of items"
        class="flex flex-col sm:flex-row sm:items-center justify-between bg-white rounded-xl shadow p-4 gap-3"
      >
        <div>
          <div class="font-medium">{{ item.product.name }}</div>
          <div class="text-sm text-gray-500">
            {{ item.product.currency }} {{ (item.product.unitCents * item.qty) / 100 | number: '1.2-2' }}
          </div>
        </div>
        <div class="flex items-center gap-3">
          <button class="px-3 py-1 border rounded-xl" (click)="dec(item.product.id)">-</button>
          <div>{{ item.qty }}</div>
          <button class="px-3 py-1 border rounded-xl" (click)="inc(item.product.id)">+</button>
          <button class="text-red-600" (click)="remove(item.product.id)">Remove</button>
        </div>
      </div>
      <div class="text-right font-medium">Total: {{ cart.totalCents() / 100 | number: '1.2-2' }}</div>
      <a routerLink="/checkout" class="inline-block px-4 py-2 bg-black text-white rounded-xl hover:opacity-90">Go to Checkout</a>
    </ng-container>
    <ng-template #empty>
      <div class="text-gray-500">Cart is empty.</div>
    </ng-template>
  </div>
  `,
})
export class CartComponent {
  constructor(public cart: CartService) {}

  inc(id: string) {
    const current = this.cart.list().find((item) => item.product.id === id);
    if (!current) return;
    this.cart.updateQty(id, current.qty + 1);
  }

  dec(id: string) {
    const current = this.cart.list().find((item) => item.product.id === id);
    if (!current) return;
    this.cart.updateQty(id, Math.max(1, current.qty - 1));
  }

  remove(id: string) {
    this.cart.remove(id);
  }
}
