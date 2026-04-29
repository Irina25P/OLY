import type { InterestType } from "./InterestType";

export interface Product {
    id: string;
    title: string;
    description: string;
    price: number;
    stock: number;
    imageUrls: string[];
    category: InterestType;
    available: boolean;
    averageRating: number;
    sellerId: string;
}
