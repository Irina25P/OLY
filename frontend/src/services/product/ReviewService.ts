import axiosInstance from "../axios";

export interface SellerReplyDetailDTO {
    id: string;
    sellerId: string;
    reply: string;
    createdAt: string;
}

export interface ReviewDetailDTO {
    id: string;
    rating: number;
    comment: string;
    clientId: string;
    createdAt: string;
    sellerReply: SellerReplyDetailDTO | null;
}

export interface CreateReviewRequest {
    productId: string;
    rating: number;
    comment: string;
}

export const ReviewService = {
    getReviewsByProduct: (productId: string) =>
        axiosInstance.get<ReviewDetailDTO[]>(`/v1/reviews/product/${productId}`),

    createReview: (review: CreateReviewRequest) =>
        axiosInstance.post<ReviewDetailDTO>("/v1/reviews", review),

    deleteReview: (reviewId: string) =>
        axiosInstance.delete(`/v1/reviews/${reviewId}`)
};