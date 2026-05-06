export interface Seller {
    id: string;
    name: string;
    email: string;
    imageUrl: string;
    joinDate: string;
    rating: number;
}

export interface SalesDataPoint {
    date: string; // Format ISO sau "YYYY-MM-DD"
    revenue: number;
}

export interface CategoryStats {
    category: string;
    count: number;
    revenue: number;
}

export interface SellerPerformanceDto {
    sellerId: string;
    sellerName: string;
    imageUrl: string;
    totalRevenue: number;
    ordersCount: number; // Doar cele RECEIVED
    popularCategory: string;
}