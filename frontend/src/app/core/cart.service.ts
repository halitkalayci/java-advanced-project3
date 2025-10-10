import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Product } from './types';

@Injectable({ providedIn: 'root' })
export class CartService {
  private items = new Map<string, { product: Product; qty: number }>();
  private itemsSubject = new BehaviorSubject(Array.from(this.items.values()));
  readonly items$ = this.itemsSubject.asObservable();

  add(product: Product) {
    const current = this.items.get(product.id) ?? { product, qty: 0 };
    current.qty += 1;
    this.items.set(product.id, current);
    this.emit();
  }

  updateQty(productId: string, qty: number) {
    const current = this.items.get(productId);
    if (!current) return;
    current.qty = Math.max(1, qty);
    this.items.set(productId, current);
    this.emit();
  }

  remove(productId: string) {
    this.items.delete(productId);
    this.emit();
  }

  clear() {
    this.items.clear();
    this.emit();
  }

  list() {
    return Array.from(this.items.values());
  }

  totalCents() {
    return this.list().reduce((sum, item) => sum + item.product.unitCents * item.qty, 0);
  }

  private emit() {
    this.itemsSubject.next(this.list());
  }
}
