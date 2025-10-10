export type UUID = string;
export interface Product {
  id: UUID;
  name: string;
  unitCents: number;
  currency: string;
  active: boolean;
}
export interface CreateOrderItem {
  productId: UUID;
  quantity: number;
}
export interface CreateOrderRequest {
  items: CreateOrderItem[];
}
export interface OrderCreatedResponse {
  id: UUID;
  status: string;
  totalCents: number;
  currency: string;
}
