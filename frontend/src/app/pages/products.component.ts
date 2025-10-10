import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ApiService } from '../core/api.service';
import { CartService } from '../core/cart.service';
import { Product } from '../core/types';

@Component({
  standalone: true,
  selector: 'app-products',
  imports: [CommonModule, CurrencyPipe],
  template: `
  <div class="p-6 max-w-6xl mx-auto">
    <h2 class="text-xl font-semibold mb-4">Products</h2>
    <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <div
        *ngFor="let product of products"
        class="rounded-2xl bg-white shadow p-4 flex flex-col"
      >
        <div class="font-medium">{{ product.name }}</div>
        <div class="text-sm text-gray-500 mt-1">
          {{ product.unitCents / 100 | currency: product.currency:'symbol-narrow':'1.2-2' }}
        </div>
        <button
          class="mt-4 py-2 rounded-xl bg-black text-white hover:opacity-90"
          (click)="add(product)"
        >
          Add to Cart
        </button>
      </div>
    </div>
  </div>
  `,
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];

  constructor(private api: ApiService, private cart: CartService) {}

  ngOnInit(): void {
    this.api.getProducts().subscribe((products) => (this.products = products));
  }

  add(product: Product) {
    this.cart.add(product);
  }
}
