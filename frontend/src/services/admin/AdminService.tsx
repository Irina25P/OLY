import axiosInstance from "../../services/axios";

export interface AdminSellerDto {
    id: string;
    name: string;
    username: string;
    email: string;
    imageName: string;
    description: string;
    county: string;
}

export interface AdminClientDto {
    id: string;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    county: string;
    age: number;
    phoneNumber: string;
    interests: string[];
}

export interface AdminOrderDto {
    id: string;
    clientId: string;
    sellerId: string;
    statusType: "PLACED" | "SHIPPED" | "IN_DELIVERY" | "RECEIVED" | "CANCELLED";
    payment: string;
    county: string;
    city: string;
    street: string;
    fullPrice: number;
    createdAt: string;
}

export interface ProductDto {
    id: string;
    name: string;
    description: string;
    price: number;
    stock: number;
    imageUrls: string[];
    category: string;
    averageRating: number | null;
    sellerId: string;
}

export interface Seller {
    id: string;
    name: string;
    username: string;
    email: string;
    imageUrl: string;
    totalRevenue: number;
    totalSales: number;
    rating: number;
    status: "Active" | "Inactive";
}

export interface RevenueDataPoint {
    date: string;
    amount: number;
}

export interface SellerRevenueDataPoint {
    sellerName: string;
    revenue: number;
}

export interface AdminSummary {
    totalSellers: number;
    totalClients: number;
    totalOrders: number;
    finishedOrders: number;
    totalRevenue: number;
}

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";

const isInLastDays = (date: string, days: number) => {
    const createdAt = new Date(date);
    const now = new Date();

    const limit = new Date();
    limit.setDate(now.getDate() - days);

    return createdAt >= limit && createdAt <= now;
};

const getFinishedOrders = (orders: AdminOrderDto[], days?: number) => {
    return orders.filter((order) => {
        const isFinished = order.statusType === "RECEIVED";
        const isInPeriod = days ? isInLastDays(order.createdAt, days) : true;

        return isFinished && isInPeriod;
    });
};

export const AdminService = {
    async getAllSellers(): Promise<Seller[]> {
        try {
            const [sellersResponse, ordersResponse, productsResponse] =
                await Promise.all([
                    axiosInstance.get<AdminSellerDto[]>("/v1/users/admin/sellers"),
                    axiosInstance.get<AdminOrderDto[]>("/v1/orders/all"),
                    axiosInstance.get<ProductDto[]>("/v1/products/search"),
                ]);

            const sellers = sellersResponse.data;
            const orders = getFinishedOrders(ordersResponse.data);
            const products = productsResponse.data;

            return sellers.map((seller) => {
                const sellerOrders = orders.filter(
                    (order) => order.sellerId === seller.id
                );

                const sellerProducts = products.filter(
                    (product) => product.sellerId === seller.id
                );

                const totalRevenue = sellerOrders.reduce(
                    (sum, order) => sum + (order.fullPrice ?? 0),
                    0
                );

                const validRatings = sellerProducts
                    .map((product) => product.averageRating)
                    .filter((rating): rating is number => rating !== null);

                const rating =
                    validRatings.length > 0
                        ? validRatings.reduce((sum, value) => sum + value, 0) /
                        validRatings.length
                        : 0;

                return {
                    id: seller.id,
                    name: seller.name,
                    username: seller.username,
                    email: seller.email,
                    imageUrl: seller.imageName
                        ? `${MINIO_BASE}/${seller.imageName}`
                        : "",
                    totalRevenue,
                    totalSales: sellerOrders.length,
                    rating,
                    status: sellerOrders.length > 0 ? "Active" : "Inactive",
                };
            });
        } catch (error) {
            console.error("Error fetching admin sellers:", error);
            return [];
        }
    },

    async getRevenueAnalytics(days: number): Promise<RevenueDataPoint[]> {
        try {
            const response = await axiosInstance.get<AdminOrderDto[]>("/v1/orders/all");

            const orders = getFinishedOrders(response.data, days);

            const groupedByDate: Record<string, number> = {};

            for (let i = days - 1; i >= 0; i--) {
                const date = new Date();
                date.setDate(date.getDate() - i);

                const key = date.toISOString().split("T")[0];
                groupedByDate[key] = 0;
            }

            orders.forEach((order) => {
                const date = new Date(order.createdAt).toISOString().split("T")[0];

                if (groupedByDate[date] !== undefined) {
                    groupedByDate[date] += order.fullPrice ?? 0;
                }
            });

            return Object.entries(groupedByDate).map(([date, amount]) => ({
                date,
                amount,
            }));
        } catch (error) {
            console.error("Error fetching revenue analytics:", error);
            return [];
        }
    },

    async getRevenuePerSeller(days: number): Promise<SellerRevenueDataPoint[]> {
        try {
            const [sellersResponse, ordersResponse] = await Promise.all([
                axiosInstance.get<AdminSellerDto[]>("/v1/users/admin/sellers"),
                axiosInstance.get<AdminOrderDto[]>("/v1/orders/all"),
            ]);

            const sellers = sellersResponse.data;
            const orders = getFinishedOrders(ordersResponse.data, days);

            return sellers
                .map((seller) => {
                    const sellerOrders = orders.filter(
                        (order) => order.sellerId === seller.id
                    );

                    const revenue = sellerOrders.reduce(
                        (sum, order) => sum + (order.fullPrice ?? 0),
                        0
                    );

                    return {
                        sellerName: seller.name,
                        revenue,
                    };
                })
                .filter((item) => item.revenue > 0)
                .sort((a, b) => b.revenue - a.revenue);
        } catch (error) {
            console.error("Error fetching revenue per seller:", error);
            return [];
        }
    },

    async getSummary(): Promise<AdminSummary> {
        try {
            const [sellersResponse, clientsResponse, ordersResponse] =
                await Promise.all([
                    axiosInstance.get<AdminSellerDto[]>("/v1/users/admin/sellers"),
                    axiosInstance.get<AdminClientDto[]>("/v1/users/admin/clients"),
                    axiosInstance.get<AdminOrderDto[]>("/v1/orders/all"),
                ]);

            const finishedOrders = getFinishedOrders(ordersResponse.data);

            return {
                totalSellers: sellersResponse.data.length,
                totalClients: clientsResponse.data.length,
                totalOrders: ordersResponse.data.length,
                finishedOrders: finishedOrders.length,
                totalRevenue: finishedOrders.reduce(
                    (sum, order) => sum + (order.fullPrice ?? 0),
                    0
                ),
            };
        } catch (error) {
            console.error("Error fetching admin summary:", error);

            return {
                totalSellers: 0,
                totalClients: 0,
                totalOrders: 0,
                finishedOrders: 0,
                totalRevenue: 0,
            };
        }
    },
};

export default AdminService;