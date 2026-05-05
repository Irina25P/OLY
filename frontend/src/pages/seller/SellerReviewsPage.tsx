import { useState, useEffect } from "react";
import axiosInstance from "../../services/axios";
import { useAuth } from "../../context/AuthContext";

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";
const PLACEHOLDER = "https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=900&q=80";

function imageUrl(objectName: string | undefined): string {
    if (!objectName) return PLACEHOLDER;
    if (objectName.startsWith("http")) return objectName;
    return `${MINIO_BASE}/${objectName}`;
}

interface SellerReply {
    id: string;
    sellerId: string;
    reply: string;
    createdAt: string;
}

interface Review {
    id: string;
    rating: number;
    comment: string;
    clientId: string;
    createdAt: string;
    sellerReply: SellerReply | null;
}

interface ProductReviews {
    productId: string;
    productName: string;
    productImageUrl: string;
    reviews: Review[];
}

function StarRating({ rating }: { rating: number }) {
    return (
        <span>
            {Array.from({ length: 5 }, (_, i) => (
                <span key={i} style={{ color: i < Math.round(rating) ? "#F6B21A" : "#D1D5DB" }}>★</span>
            ))}
        </span>
    );
}

function SellerReviewsPage() {
    const { user, token } = useAuth();
    const authHeader = { Authorization: `Bearer ${token}` };

    const [productReviews, setProductReviews] = useState<ProductReviews[]>([]);
    const [loading, setLoading] = useState(true);
    const [replyTexts, setReplyTexts] = useState<Record<string, string>>({});
    const [submitting, setSubmitting] = useState<string | null>(null);
    const [errors, setErrors] = useState<Record<string, string>>({});

    useEffect(() => {
        const load = async () => {
            try {
                const productsRes = await axiosInstance.get<Array<{
                    id: string;
                    name: string;
                    sellerId: string;
                    imageUrls: string[];
                }>>("/v1/products/search");

                const myProducts = productsRes.data.filter((p) => p.sellerId === user?.userId);

                const results: ProductReviews[] = [];
                for (const product of myProducts) {
                    try {
                        const reviewRes = await axiosInstance.get<Review[]>(
                            `/v1/reviews/product/${product.id}`,
                            { headers: authHeader }
                        );
                        if (reviewRes.data.length > 0) {
                            results.push({
                                productId: product.id,
                                productName: product.name,
                                productImageUrl: product.imageUrls?.[0] ?? "",
                                reviews: reviewRes.data,
                            });
                        }
                    } catch {
                        // skip products with no review access
                    }
                }
                setProductReviews(results);
            } catch {
                setProductReviews([]);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, []);

    const handleReply = async (reviewId: string) => {
        const text = replyTexts[reviewId]?.trim();
        if (!text) {
            setErrors((prev) => ({ ...prev, [reviewId]: "Reply cannot be empty." }));
            return;
        }

        setSubmitting(reviewId);
        setErrors((prev) => ({ ...prev, [reviewId]: "" }));
        try {
            const res = await axiosInstance.post<SellerReply>(
                "/v1/seller-replies",
                { reviewId, reply: text },
                { headers: authHeader }
            );

            setProductReviews((prev) =>
                prev.map((pr) => ({
                    ...pr,
                    reviews: pr.reviews.map((r) =>
                        r.id === reviewId ? { ...r, sellerReply: res.data } : r
                    ),
                }))
            );
            setReplyTexts((prev) => ({ ...prev, [reviewId]: "" }));
        } catch {
            setErrors((prev) => ({ ...prev, [reviewId]: "Failed to submit reply. Please try again." }));
        } finally {
            setSubmitting(null);
        }
    };

    const totalReviews = productReviews.reduce((sum, pr) => sum + pr.reviews.length, 0);

    return (
        <div>
            <h1 className="text-4xl font-extrabold">Reviews</h1>
            <p className="mt-2 text-gray-600">View and respond to feedback left by customers.</p>

            {loading ? (
                <div className="mt-8 text-center text-gray-400">Loading reviews...</div>
            ) : totalReviews === 0 ? (
                <div className="mt-8 text-center text-gray-400">No reviews yet.</div>
            ) : (
                <div className="mt-8 space-y-10">
                    {productReviews.map((pr) => (
                        <div key={pr.productId}>
                            {/* Product header */}
                            <div className="mb-4 flex items-center gap-4">
                                <div className="h-14 w-14 shrink-0 overflow-hidden rounded-xl bg-[#F1F1F1]">
                                    <img
                                        src={imageUrl(pr.productImageUrl)}
                                        alt={pr.productName}
                                        className="h-full w-full object-cover"
                                        onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER; }}
                                    />
                                </div>
                                <div>
                                    <h2 className="text-lg font-bold text-gray-800">{pr.productName}</h2>
                                    <p className="text-sm text-gray-400">
                                        {pr.reviews.length} review{pr.reviews.length !== 1 ? "s" : ""}
                                    </p>
                                </div>
                            </div>

                            <div className="space-y-4">
                                {pr.reviews.map((review) => (
                                    <div key={review.id} className="rounded-3xl bg-white p-6 shadow-sm">
                                        {/* Reviewer identity */}
                                        <div className="mb-3 flex items-center gap-3">
                                            <div
                                                className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-sm font-bold text-white"
                                                style={{ backgroundColor: "#54457F" }}
                                            >
                                                {review.clientId.slice(0, 1).toUpperCase()}
                                            </div>
                                            <div>
                                                <p className="text-sm font-semibold text-gray-800">
                                                    Customer #{review.clientId.slice(0, 8).toUpperCase()}
                                                </p>
                                                <p className="text-xs text-gray-400">
                                                    {new Date(review.createdAt).toLocaleDateString()}
                                                </p>
                                            </div>
                                        </div>

                                        {/* Star rating */}
                                        <div className="flex items-center gap-3">
                                            <StarRating rating={review.rating} />
                                            <span className="rounded-full bg-[#E9E7FF] px-3 py-1 text-xs font-bold text-[#54457F]">
                                                {review.rating.toFixed(1)}
                                            </span>
                                        </div>

                                        {/* Review comment */}
                                        <p className="mt-3 text-sm text-gray-700">
                                            {review.comment || <em className="text-gray-400">No comment</em>}
                                        </p>

                                        {/* Existing seller reply */}
                                        {review.sellerReply && (
                                            <div className="mt-4 rounded-2xl border-l-4 bg-[#F8F8F8] px-4 py-3" style={{ borderColor: "#54457F" }}>
                                                <p className="mb-1 text-xs font-semibold" style={{ color: "#54457F" }}>
                                                    Your reply
                                                </p>
                                                <p className="text-sm text-gray-700">{review.sellerReply.reply}</p>
                                                <p className="mt-1 text-xs text-gray-400">
                                                    {new Date(review.sellerReply.createdAt).toLocaleDateString()}
                                                </p>
                                            </div>
                                        )}

                                        {/* Reply input — only shown when no reply exists yet */}
                                        {!review.sellerReply && (
                                            <div className="mt-4 rounded-2xl bg-[#F8F8F8] p-4">
                                                <p className="mb-2 text-xs font-semibold text-gray-500 uppercase tracking-wide">
                                                    Add a reply
                                                </p>
                                                <textarea
                                                    rows={3}
                                                    placeholder="Write your reply to this review..."
                                                    value={replyTexts[review.id] ?? ""}
                                                    onChange={(e) =>
                                                        setReplyTexts((prev) => ({ ...prev, [review.id]: e.target.value }))
                                                    }
                                                    className="w-full resize-none rounded-xl border border-gray-200 bg-white p-3 text-sm text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#54457F]/30"
                                                />
                                                {errors[review.id] && (
                                                    <p className="mt-1 text-xs text-red-500">{errors[review.id]}</p>
                                                )}
                                                <div className="mt-3 flex justify-end">
                                                    <button
                                                        type="button"
                                                        disabled={submitting === review.id}
                                                        onClick={() => handleReply(review.id)}
                                                        className="rounded-full px-6 py-2 text-sm font-semibold text-white transition disabled:opacity-60"
                                                        style={{ backgroundColor: "#54457F" }}
                                                    >
                                                        {submitting === review.id ? "Sending..." : "Add Comment"}
                                                    </button>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default SellerReviewsPage;
