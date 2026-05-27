import { useState, useEffect } from "react";
import toast from "react-hot-toast";
import {
    TrendingUp,
    TrendingDown,
    Minus,
    CheckCircle,
} from "lucide-react";
import {
    DynamicPricingService,
    type PriceRecommendationDto,
} from "../../services/DynamicPricingService";

const CATEGORY_LABEL: Record<string, string> = {
    ELECTRONICS_IT: "Electronics",
    FASHION_ACCESSORIES: "Fashion",
    HOME_GARDEN: "Home",
    AUTO_MOTO: "Auto & Moto",
    SPORTS_OUTDOOR: "Sports",
    GAMES_GAMING: "Games",
};

function DirectionBadge({ direction }: { direction: string }) {
    if (direction === "INCREASE") {
        return (
            <span className="inline-flex items-center gap-1 rounded-full bg-green-100 px-2.5 py-1 text-xs font-semibold text-green-700">
                <TrendingUp size={12} /> Increase
            </span>
        );
    }
    if (direction === "DECREASE") {
        return (
            <span className="inline-flex items-center gap-1 rounded-full bg-red-100 px-2.5 py-1 text-xs font-semibold text-red-700">
                <TrendingDown size={12} /> Decrease
            </span>
        );
    }
    return (
        <span className="inline-flex items-center gap-1 rounded-full bg-gray-100 px-2.5 py-1 text-xs font-semibold text-gray-600">
            <Minus size={12} /> Maintain
        </span>
    );
}

export default function SellerPricingPage() {
    const [recommendations, setRecommendations] = useState<PriceRecommendationDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [applying, setApplying] = useState<string | null>(null);

    useEffect(() => {
        DynamicPricingService.getSellerRecommendations()
            .then((res) => setRecommendations(res.data))
            .catch(() => toast.error("Could not load pricing recommendations."))
            .finally(() => setLoading(false));
    }, []);

    const handleApply = async (productId: string) => {
        setApplying(productId);
        try {
            const res = await DynamicPricingService.applyRecommendation(productId);
            setRecommendations((prev) =>
                prev.map((r) => (r.productId === productId ? res.data : r))
            );
            toast.success("Price updated successfully!");
        } catch {
            toast.error("Failed to apply price. Please try again.");
        } finally {
            setApplying(null);
        }
    };

    return (
        <div>
            <h1 className="text-4xl font-extrabold">AI Dynamic Pricing</h1>
            <p className="mt-2 text-gray-600">
                Demand-based price recommendations generated from your order history.
            </p>

            {loading ? (
                <div className="mt-12 flex items-center justify-center">
                    <div className="h-10 w-10 animate-spin rounded-full border-4 border-[#54457F] border-t-transparent" />
                </div>
            ) : recommendations.length === 0 ? (
                <div className="mt-10 rounded-3xl bg-white p-10 text-center shadow-sm">
                    <TrendingUp size={48} className="mx-auto mb-4 text-[#54457F] opacity-30" />
                    <p className="text-lg font-semibold text-gray-700">No recommendations available yet</p>
                    <p className="mt-2 text-sm text-gray-500">
                        Pricing recommendations are generated once your products start receiving orders.
                    </p>
                </div>
            ) : (
                <>
                    {/* Summary row */}
                    <div className="mt-8 grid grid-cols-3 gap-6">
                        <div className="rounded-3xl bg-white p-6 shadow-sm">
                            <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Products Tracked</p>
                            <p className="mt-3 text-3xl font-extrabold">{recommendations.length}</p>
                        </div>
                        <div className="rounded-3xl bg-white p-6 shadow-sm">
                            <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Price Increases</p>
                            <p className="mt-3 text-3xl font-extrabold text-green-600">
                                {recommendations.filter((r) => r.priceDirection === "INCREASE").length}
                            </p>
                        </div>
                        <div className="rounded-3xl bg-white p-6 shadow-sm">
                            <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Price Decreases</p>
                            <p className="mt-3 text-3xl font-extrabold text-red-500">
                                {recommendations.filter((r) => r.priceDirection === "DECREASE").length}
                            </p>
                        </div>
                    </div>

                    {/* Table */}
                    <div className="mt-8 overflow-hidden rounded-3xl bg-white shadow-sm">
                        <table className="w-full text-sm">
                            <thead>
                                <tr className="border-b border-gray-100">
                                    <th className="px-6 py-4 text-left text-xs font-bold uppercase tracking-wide text-gray-500">
                                        Product
                                    </th>
                                    <th className="px-4 py-4 text-left text-xs font-bold uppercase tracking-wide text-gray-500">
                                        Category
                                    </th>
                                    <th className="px-4 py-4 text-right text-xs font-bold uppercase tracking-wide text-gray-500">
                                        Current Price
                                    </th>
                                    <th className="px-4 py-4 text-right text-xs font-bold uppercase tracking-wide text-gray-500">
                                        Recommended
                                    </th>
                                    <th className="px-4 py-4 text-center text-xs font-bold uppercase tracking-wide text-gray-500">
                                        Direction
                                    </th>
                                    <th className="px-4 py-4 text-right text-xs font-bold uppercase tracking-wide text-gray-500">
                                        Orders (7d)
                                    </th>
                                    <th className="px-4 py-4 text-right text-xs font-bold uppercase tracking-wide text-gray-500">
                                        Stock
                                    </th>
                                    <th className="px-6 py-4 text-center text-xs font-bold uppercase tracking-wide text-gray-500">
                                        Action
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                {recommendations.map((rec, idx) => (
                                    <tr
                                        key={rec.productId}
                                        className={`border-b border-gray-50 transition hover:bg-gray-50 ${
                                            idx === recommendations.length - 1 ? "border-b-0" : ""
                                        }`}
                                    >
                                        <td className="px-6 py-4 font-semibold text-[#000000]">
                                            <span className="line-clamp-1">{rec.productName}</span>
                                        </td>
                                        <td className="px-4 py-4 text-gray-500">
                                            {CATEGORY_LABEL[rec.category] ?? rec.category ?? "—"}
                                        </td>
                                        <td className="px-4 py-4 text-right font-semibold">
                                            ${rec.currentPrice?.toFixed(2) ?? "—"}
                                        </td>
                                        <td className="px-4 py-4 text-right font-bold text-[#54457F]">
                                            ${rec.recommendedPrice?.toFixed(2) ?? "—"}
                                        </td>
                                        <td className="px-4 py-4 text-center">
                                            <DirectionBadge direction={rec.priceDirection} />
                                        </td>
                                        <td className="px-4 py-4 text-right text-gray-600">
                                            {rec.recentOrderCount7Days ?? 0}
                                        </td>
                                        <td className="px-4 py-4 text-right text-gray-600">
                                            {rec.currentStock ?? "—"}
                                        </td>
                                        <td className="px-6 py-4 text-center">
                                            {rec.applied ? (
                                                <span className="inline-flex items-center gap-1 text-xs font-semibold text-green-600">
                                                    <CheckCircle size={14} /> Applied
                                                </span>
                                            ) : rec.priceDirection === "MAINTAIN" ? (
                                                <span className="text-xs text-gray-400">No change needed</span>
                                            ) : (
                                                <button
                                                    disabled={applying === rec.productId}
                                                    onClick={() => handleApply(rec.productId)}
                                                    className="rounded-xl bg-[#54457F] px-4 py-2 text-xs font-semibold text-white hover:bg-[#3d3060] transition disabled:opacity-50"
                                                >
                                                    {applying === rec.productId ? "Applying…" : "Apply Price"}
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </>
            )}
        </div>
    );
}
