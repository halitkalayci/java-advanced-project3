import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './layout/navbar.component';

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [NavbarComponent, RouterOutlet],
  template: `
    <app-navbar></app-navbar>
    <main class="pt-14">
      <router-outlet></router-outlet>
    </main>
  `,
})
export class AppComponent {}
