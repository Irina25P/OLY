import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ShoppingCart, Star } from "lucide-react";
import { RecommendationService, type ProductRecommendationDto } from "../../services/RecommendationService";
import { useCart } from "../../contexts/CartContext";

const CATEGORY_LABEL: Record<string, string> = {
    ELECTRONICS_IT: "Electronics",
    FASHION_ACCESSORIES: "Fashion",
    HOME_GARDEN: "Home",
    AUTO_MOTO: "Auto & Moto",
    SPORTS_OUTDOOR: "Sports",
    GAMES_GAMING: "Games",
};

const PLACEHOLDER =
    "https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=900&q=80";
const MINIO_BASE = "http://92.5.49.167:9000/disi-images";

function imageUrl(name: string | null | undefined): string {
    if (!name) return PLACEHOLDER;
    if (name.startsWith("http")) return name;
    return `${MINIO_BASE}/${name}`;
}

export default function RecommendationsPage() {
    const navigate = useNavigate();
    const { addToCart } = useCart();
    const [recommendations, setRecommendations] = useState<ProductRecommendationDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        RecommendationService.getRecommendations()
            .then((res) => setRecommendations(res.data))
            .catch(() => setError("Could not load recommendations. Please try again later."))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="min-h-screen bg-[#F8F8F8] px-6 py-10">
            <div className="mx-auto max-w-6xl">
                <div className="mb-8">
                    <h1 className="text-4xl font-extrabold text-[#000000]">
                        Recommended for You
                    </h1>
                    <p className="mt-2 text-gray-500">
                        Personalized product suggestions based on your purchase history.
                    </p>
                </div>

                {loading && (
                    <div className="flex items-center justify-center py-20">
                        <div className="h-10 w-10 animate-spin rounded-full border-4 border-[#54457F] border-t-transparent" />
                    </div>
                )}

                {!loading && error && (
                    <div className="rounded-2xl bg-red-50 p-6 text-center text-red-600">
                        {error}
                    </div>
                )}

                {!loading && !error && recommendations.length === 0 && (
                    <div className="rounded-2xl bg-white p-10 text-center shadow-sm">
                        <Star size={48} className="mx-auto mb-4 text-[#54457F] opacity-40" />
                        <p className="text-lg font-semibold text-gray-700">No recommendations yet</p>
                        <p className="mt-2 text-gray-500">
                            Start browsing and ordering products to get personalised suggestions.
                        </p>
                        <button
                            onClick={() => navigate("/products")}
                            className="mt-6 rounded-xl bg-[#54457F] px-6 py-2 text-sm font-semibold text-white hover:bg-[#3d3060] transition"
                        >
                            Browse Products
                        </button>
                    </div>
                )}

                {!loading && !error && recommendations.length > 0 && (
                    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                        {recommendations.map((product) => (
                            <div
                                key={product.id}
                                className="group flex flex-col overflow-hidden rounded-3xl bg-white shadow-sm transition hover:shadow-md"
                            >
                                <div
                                    className="relative h-48 cursor-pointer overflow-hidden"
                                    onClick={() => navigate(`/products/${product.id}`)}
                                >
                                    <img
                                        src={imageUrl(product.firstImageName)}
                                        alt={product.name}
                                        className="h-full w-full object-cover transition duration-300 group-hover:scale-105"
                                        onError={(e) => {
                                            (e.currentTarget as HTMLImageElement).src = PLACEHOLDER;
                                        }}
                                    />
                                    {product.category && (
                                        <span className="absolute right-3 top-3 rounded-full bg-white/90 px-3 py-1 text-xs font-semibold text-[#54457F] shadow-sm">
                                            {CATEGORY_LABEL[product.category] ?? product.category}
                                        </span>
                                    )}
                                </div>

                                <div className="flex flex-1 flex-col p-4">
                                    <h3
                                        className="cursor-pointer text-base font-bold text-[#000000] line-clamp-2 hover:text-[#54457F] transition"
                                        onClick={() => navigate(`/products/${product.id}`)}
                                    >
                                        {product.name ?? "Unknown Product"}
                                    </h3>

                                    <div className="mt-auto pt-4 flex items-center justify-between">
                                        <span className="text-lg font-extrabold text-[#54457F]">
                                            {product.price != null ? `$${product.price.toFixed(2)}` : "—"}
                                        </span>
                                        <button
                                            onClick={() =>
                                                addToCart({
                                                    id: product.id,
                                                    title: product.name,
                                                    description: "",
                                                    price: product.price,
                                                    imageUrls: product.firstImageName
                                                        ? [imageUrl(product.firstImageName)]
                                                        : [],
                                                    category: product.category as any,
                                                    stock: 99,
                                                    available: true,
                                                    averageRating: 0,
                                                    sellerId: product.sellerId,
                                                })
                                            }
                                            className="flex items-center gap-1.5 rounded-xl bg-[#54457F] px-3 py-2 text-xs font-semibold text-white hover:bg-[#3d3060] transition"
                                        >
                                            <ShoppingCart size={14} />
                                            Add
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}
