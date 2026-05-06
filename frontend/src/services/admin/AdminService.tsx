import axiosInstance from "../../services/axios";

// --- INTERFEȚE ---
export interface Seller {
    id: string;
    name: string;
    email: string;
    imageUrl: string;
    totalRevenue: number;
    rating: number;
}

export interface RevenueDataPoint {
    date: string;
    amount: number;
}

export interface CategoryDistribution {
    category: string;
    count: number;
    revenue: number;
}

// --- SERVICIUL ---
export const AdminService = {
    /**
     * Extrage toți sellerii unici din produsele existente.
     * Folosește endpoint-ul real: /v1/products/search
     */
    async getAllSellers(): Promise<Seller[]> {
        try {
            const response = await axiosInstance.get<any[]>("/v1/products/search");
            const products = response.data;

            // Extragem ID-uri unice de selleri
            const uniqueSellerIds = Array.from(new Set(products.map((p) => p.sellerId)));

            return uniqueSellerIds.map((id) => ({
                id: id,
                name: `Seller ${id.slice(0, 5).toUpperCase()}`,
                email: "contact@platform.com",
                imageUrl: "", // Aici se poate completa ulterior cu date din UserMicroservice
                totalRevenue: products
                    .filter((p) => p.sellerId === id)
                    .reduce((sum, p) => sum + (p.price * 5), 0), // Simulare venit per seller
                rating: 4.5
            }));
        } catch (error) {
            console.error("Error fetching sellers from products:", error);
            return [];
        }
    },

    /**
     * Calculează veniturile.
     * Am prefixat days cu '_' pentru a evita eroarea TS6133 (unused variable).
     */
    async getRevenueAnalytics(_days: number): Promise<RevenueDataPoint[]> {
        try {
            // Momentan returnăm un array gol pentru a evita 404 pe rutele inexistente de admin.
            // Când adaugi în Backend rutele de admin, vei decomenta linia de mai jos:
            // const response = await axiosInstance.get("/v1/orders/all", { params: { lastDays: _days } });

            return [
                { date: "2024-05-01", amount: 400 },
                { date: "2024-05-02", amount: 700 }
            ]; // Date simulate pentru a vedea graficul în UI
        } catch (error) {
            console.error("Error fetching revenue stats:", error);
            return [];
        }
    },

    /**
     * Grupează produsele pe categorii folosind datele din ProductMicroservice.
     */
    async getCategoryStats(): Promise<CategoryDistribution[]> {
        try {
            const response = await axiosInstance.get<any[]>("/v1/products/search");
            const products = response.data;

            const stats: Record<string, { count: number; rev: number }> = {};

            products.forEach((p) => {
                const cat = p.category || "General";
                if (!stats[cat]) stats[cat] = { count: 0, rev: 0 };
                stats[cat].count++;
                stats[cat].rev += (p.price * p.stock); // Calculăm valoarea potențială a stocului
            });

            return Object.entries(stats).map(([category, data]) => ({
                category,
                count: data.count,
                revenue: data.rev
            }));
        } catch (error) {
            console.error("Error fetching category stats:", error);
            return [];
        }
    }
};