import axiosInstance from "./axios";

export interface PriceRecommendationDto {
    productId: string;
    productName: string;
    category: string;
    currentPrice: number;
    recommendedPrice: number;
    currentStock: number;
    totalOrderCount: number;
    recentOrderCount7Days: number;
    priceDirection: "INCREASE" | "DECREASE" | "MAINTAIN";
    applied: boolean;
}

export const DynamicPricingService = {
    getSellerRecommendations: () =>
        axiosInstance.get<PriceRecommendationDto[]>("/v1/pricing"),

    getProductRecommendation: (productId: string) =>
        axiosInstance.get<PriceRecommendationDto>(`/v1/pricing/${productId}`),

    applyRecommendation: (productId: string) =>
        axiosInstance.post<PriceRecommendationDto>(`/v1/pricing/${productId}/apply`),
};
