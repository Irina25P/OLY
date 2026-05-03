import { useState, useEffect } from "react";
import axiosInstance from "../../services/axios";
import { useAuth } from "../../context/AuthContext";

interface Product {
    id: string;
    name: string;
    price: number;
    stock: number;
    category: string;
    averageRating: number | null;
    sellerId: string;
}

const CATEGORY_LABEL: Record<string, string> = {
    ELECTRONICS_IT:      "Electronics",
    FASHION_ACCESSORIES: "Fashion",
    HOME_GARDEN:         "Home",
    AUTO_MOTO:           "Auto & Moto",
    SPORTS_OUTDOOR:      "Sports",
    GAMES_GAMING:        "Games",
};

function SellerAnalyticsPage() {
    const { user } = useAuth();
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axiosInstance
            .get<Product[]>("/v1/products/search")
            .then((res) => setProducts(res.data.filter((p) => p.sellerId === user?.userId)))
            .catch(() => setProducts([]))
            .finally(() => setLoading(false));
    }, []);

    const totalStock = products.reduce((sum, p) => sum + p.stock, 0);
    const totalValue = products.reduce((sum, p) => sum + p.price * p.stock, 0);
    const avgRating =
        products.length > 0
            ? products.reduce((sum, p) => sum + (p.averageRating ?? 0), 0) / products.length
            : null;

    const categoryCounts = products.reduce<Record<string, number>>((acc, p) => {
        acc[p.category] = (acc[p.category] ?? 0) + 1;
        return acc;
    }, {});

    return (
        <div>
            <h1 className="text-4xl font-extrabold">Analytics</h1>
            <p className="mt-2 text-gray-600">Track your inventory stats and product performance.</p>

            {loading ? (
                <div className="mt-8 text-center text-gray-400">Loading analytics...</div>
            ) : (
                <>
                    {/* Summary cards */}
                    <div className="mt-8 grid grid-cols-3 gap-6">
                        <div className="rounded-3xl bg-white p-7 shadow-sm">
                            <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Total Products</p>
                            <h2 className="mt-5 text-3xl font-extrabold">{products.length}</h2>
                            <p className="mt-2 text-sm text-[#54457F]">Active listings</p>
                        </div>

                        <div className="rounded-3xl bg-white p-7 shadow-sm">
                            <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Inventory Value</p>
                            <h2 className="mt-5 text-3xl font-extrabold">${totalValue.toFixed(2)}</h2>
                            <p className="mt-2 text-sm text-[#54457F]">{totalStock} units in stock</p>
                        </div>

                        <div className="rounded-3xl bg-white p-7 shadow-sm">
                            <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Avg. Rating</p>
                            <h2 className="mt-5 text-3xl font-extrabold">
                                {avgRating != null ? avgRating.toFixed(1) : "—"}
                            </h2>
                            <p className="mt-2 text-sm text-[#54457F]">Across all products</p>
                        </div>
                    </div>

                    {/* Per-product table */}
                    {products.length > 0 && (
                        <div className="mt-8 rounded-3xl bg-[#F1F1F1] p-6 shadow-sm">
                            <h2 className="mb-5 text-xl font-bold">Product Breakdown</h2>

                            <div className="grid grid-cols-[2fr_100px_100px_130px_130px] px-4 pb-3 text-xs font-bold uppercase tracking-wide text-gray-500">
                                <span>Product</span>
                                <span>Price</span>
                                <span>Stock</span>
                                <span>Category</span>
                                <span>Rating</span>
                            </div>

                            <div className="space-y-3">
                                {products.map((product) => (
                                    <div
                                        key={product.id}
                                        className="grid grid-cols-[2fr_100px_100px_130px_130px] items-center rounded-2xl bg-white px-4 py-4"
                                    >
                                        <p className="font-semibold">{product.name}</p>
                                        <p className="font-bold">${product.price.toFixed(2)}</p>
                                        <span className="w-fit rounded-full bg-gray-100 px-3 py-1 text-sm">
                                            {product.stock}
                                        </span>
                                        <span className="text-sm text-gray-600">
                                            {CATEGORY_LABEL[product.category] ?? product.category}
                                        </span>
                                        <span className="w-fit rounded-full bg-[#E9E7FF] px-4 py-1 text-xs font-bold text-[#54457F]">
                                            ★ {product.averageRating != null ? product.averageRating.toFixed(1) : "—"}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Category distribution */}
                    {Object.keys(categoryCounts).length > 0 && (
                        <div className="mt-8 rounded-3xl bg-white p-8 shadow-sm">
                            <h2 className="mb-5 text-xl font-bold">Category Distribution</h2>
                            <div className="grid grid-cols-3 gap-4">
                                {Object.entries(categoryCounts).map(([cat, count]) => (
                                    <div key={cat} className="rounded-2xl bg-[#F8F8F8] px-5 py-4">
                                        <p className="text-sm font-semibold text-gray-700">
                                            {CATEGORY_LABEL[cat] ?? cat}
                                        </p>
                                        <p className="mt-2 text-2xl font-extrabold text-[#54457F]">{count}</p>
                                        <p className="mt-1 text-xs text-gray-400">products</p>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}

export default SellerAnalyticsPage;
