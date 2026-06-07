import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './layout/navbar.component';
import { ToastsComponent } from './layout/toasts.component';

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [NavbarComponent, ToastsComponent, RouterOutlet],
  template: `
    <app-navbar></app-navbar>
    <app-toasts></app-toasts>
    <main class="pt-14">
      <router-outlet></router-outlet>
    </main>
  `,
})
export class AppComponent {}
