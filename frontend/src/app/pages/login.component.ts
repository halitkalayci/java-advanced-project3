import { Component } from '@angular/core';
import { environment } from '../../environments/environment';

@Component({
  standalone: true,
  selector: 'app-login',
  template: `
  <div class="min-h-screen flex items-center justify-center bg-gray-50 px-4">
    <div class="w-full max-w-sm bg-white rounded-2xl shadow p-8">
      <h1 class="text-2xl font-semibold mb-6 text-center">SmartOrder</h1>
      <button class="w-full py-3 rounded-xl bg-black text-white hover:opacity-90" (click)="login()">
        Sign in with Keycloak
      </button>
      <p class="mt-4 text-center text-sm text-gray-500">Giriş yaptıktan sonra ürünleri görüntüleyebilirsiniz.</p>
    </div>
  </div>
  `,
})
export class LoginComponent {
  login() {
    window.location.href = `${environment.apiBase}/auth/me`;
  }
}
