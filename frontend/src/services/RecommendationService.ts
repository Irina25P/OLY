import axiosInstance from "./axios";

export interface ProductRecommendationDto {
    id: string;
    name: string;
    price: number;
    category: string;
    sellerId: string;
    firstImageName: string | null;
}

export const RecommendationService = {
    getRecommendations: () =>
        axiosInstance.get<ProductRecommendationDto[]>("/v1/recommendations"),

    trackView: (productId: string) =>
        axiosInstance.post<void>("/v1/recommendations/view", { productId }),
};
