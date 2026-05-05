import type { PaymentMethod } from "../CheckoutModels.tsx";

export type OrderStatus = "PLACED" | "SHIPPED" | "IN_DELIVERY" | "CANCELLED" | "RECEIVED";

export interface OrderDto {
    id: string;
    clientId: string;
    sellerId: string;
    statusType: OrderStatus;
    payment: PaymentMethod;
    county: string;
    city: string;
    street: string;
    fullPrice: number | null;
}

export interface OrderProductDto {
    id: string;
    orderId: string;
    productId: string;
    quantity: number;
    pricePerProduct: number;
}

export interface OrderFullDto {
    order: OrderDto;
    orderProducts: OrderProductDto[];
}