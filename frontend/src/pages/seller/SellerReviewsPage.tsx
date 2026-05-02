import { useState, useEffect } from "react";
import axiosInstance from "../../services/axios";
import { useAuth } from "../../context/AuthContext";

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
    reviews: Review[];
}

function SellerReviewsPage() {
    const { user, token } = useAuth();
    const authHeader = { Authorization: `Bearer ${token}` };

    const [productReviews, setProductReviews] = useState<ProductReviews[]>([]);
    const [loading, setLoading] = useState(true);
    const [openReply, setOpenReply] = useState<string | null>(null);
    const [replyTexts, setReplyTexts] = useState<Record<string, string>>({});
    const [submitting, setSubmitting] = useState<string | null>(null);

    useEffect(() => {
        const load = async () => {
            try {
                const productsRes = await axiosInstance.get<Array<{ id: string; name: string; sellerId: string }>>(
                    "/v1/products/search"
                );
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
        if (!text) return;

        setSubmitting(reviewId);
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
            setOpenReply(null);
            setReplyTexts((prev) => ({ ...prev, [reviewId]: "" }));
        } catch {
            // silent
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
                <div className="mt-8 space-y-8">
                    {productReviews.map((pr) => (
                        <div key={pr.productId}>
                            <h2 className="mb-3 text-lg font-bold text-gray-800">{pr.productName}</h2>

                            <div className="space-y-4">
                                {pr.reviews.map((review) => (
                                    <div key={review.id} className="rounded-3xl bg-white p-6 shadow-sm">
                                        <div className="flex items-start justify-between gap-6">
                                            <div>
                                                <p className="text-xs text-gray-400 mb-1">
                                                    {new Date(review.createdAt).toLocaleDateString()}
                                                </p>
                                                <p className="text-gray-700">{review.comment || <em className="text-gray-400">No comment</em>}</p>

                                                {review.sellerReply && (
                                                    <div className="mt-3 rounded-xl bg-[#F1F1F1] px-4 py-3">
                                                        <p className="text-xs font-semibold text-[#54457F] mb-1">Your reply</p>
                                                        <p className="text-sm text-gray-700">{review.sellerReply.reply}</p>
                                                    </div>
                                                )}
                                            </div>

                                            <div className="flex shrink-0 flex-col items-end gap-3">
                                                <span className="rounded-full bg-[#E9E7FF] px-4 py-2 text-sm font-bold text-[#54457F]">
                                                    ★ {review.rating.toFixed(1)}
                                                </span>

                                                {!review.sellerReply && (
                                                    <button
                                                        type="button"
                                                        onClick={() =>
                                                            setOpenReply(openReply === review.id ? null : review.id)
                                                        }
                                                        className="rounded-full bg-[#54457F] px-5 py-2 text-sm font-semibold text-white"
                                                    >
                                                        Reply
                                                    </button>
                                                )}
                                            </div>
                                        </div>

                                        {openReply === review.id && (
                                            <div className="mt-5 rounded-2xl bg-[#F8F8F8] p-4">
                                                <textarea
                                                    rows={4}
                                                    placeholder="Write your reply..."
                                                    value={replyTexts[review.id] ?? ""}
                                                    onChange={(e) =>
                                                        setReplyTexts((prev) => ({ ...prev, [review.id]: e.target.value }))
                                                    }
                                                    className="w-full rounded-xl border border-gray-200 bg-white p-3 text-sm focus:outline-none focus:ring-2 focus:ring-[#54457F]/30 resize-none"
                                                />

                                                <div className="mt-3 flex justify-end gap-3">
                                                    <button
                                                        type="button"
                                                        onClick={() => setOpenReply(null)}
                                                        className="rounded-full border border-gray-200 px-5 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50"
                                                    >
                                                        Cancel
                                                    </button>
                                                    <button
                                                        type="button"
                                                        disabled={submitting === review.id}
                                                        onClick={() => handleReply(review.id)}
                                                        className="rounded-full bg-[#54457F] px-5 py-2 text-sm font-semibold text-white disabled:opacity-60"
                                                    >
                                                        {submitting === review.id ? "Sending..." : "Send Reply"}
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
