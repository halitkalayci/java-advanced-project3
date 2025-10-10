import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CreateOrderRequest, OrderCreatedResponse, Product } from './types';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  getMe() {
    return this.http.get('/me');
  }

  getProducts() {
    return this.http.get<Product[]>('/catalog/products');
  }

  createOrder(payload: CreateOrderRequest) {
    return this.http.post<OrderCreatedResponse>('/orders', payload);
  }
}
