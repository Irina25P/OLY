import axiosInstance from "../axios";
import type {OrderDto, OrderFullDto, OrderStatus} from "../../models/order/OrderModels";
import type {Product} from "../../models/Product";

export const OrderService = {
    getClientOrders: () =>
        axiosInstance.get<OrderDto[]>("/v1/orders/client"),

    getOrderDetails: (orderId: string) =>
        axiosInstance.get<OrderFullDto>(`/v1/orders/${orderId}`),

    getSellerOrders: () =>
        axiosInstance.get<OrderDto[]>("/v1/orders/seller"),

    updateOrderStatus: (orderId: string, newStatus: OrderStatus) =>
        axiosInstance.put("/v1/orders", { id: orderId, status: newStatus }),

    getAllProducts: () =>
        axiosInstance.get<Product[]>("/v1/products/search")
};