import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { environment } from '../../environments/environment';

@Component({
  standalone: true,
  selector: 'app-navbar',
  imports: [RouterLink],
  template: `
  <nav class="w-full bg-white border-b sticky top-0 z-10">
    <div class="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
      <a routerLink="/products" class="font-semibold">SmartOrder</a>
      <div class="flex gap-4 items-center text-sm">
        <a routerLink="/products" class="hover:underline">Products</a>
        <a routerLink="/cart" class="hover:underline">Cart</a>
        <a routerLink="/checkout" class="hover:underline">Checkout</a>
        <button class="ml-4 px-3 py-1 rounded-xl border" (click)="login()">Login</button>
        <button class="px-3 py-1 rounded-xl border" (click)="logout()">Logout</button>
      </div>
    </div>
  </nav>
  `,
})
export class NavbarComponent {
  login() {
    window.location.href = `${environment.apiBase}/oauth2/authorization/keycloak`;
  }

  logout() {
    window.location.href = `${environment.apiBase}/auth/logout`;
  }
}
