import { useState, useEffect } from "react";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { getClientIdFromJwt } from "../../validators/checkoutValidations";
import { Trash2 } from "lucide-react";
import {
    ChevronLeft,
    ChevronRight,
    ShoppingCart,
    Star,
    Send,
    AlertCircle,
    Reply,
    Lock
} from "lucide-react";
import axiosInstance from "../../services/axios";
import { OrderService } from "../../services/order/OrderService";
import { useCart } from "../../contexts/CartContext";
import { useAuth } from "../../context/AuthContext";
import type { InterestType } from "../../models/InterestType";
import type { Product } from "../../models/Product";

interface ApiProduct {
    id: string;
    name: string;
    description: string;
    price: number;
    stock: number;
    imageUrls: string[];
    category: string;
    averageRating: number | null;
    sellerId: string;
}

interface SellerReply {
    id: string;
    reply: string;
    createdAt: string;
}

interface Review {
    id: string;
    rating: number | string;
    comment: string;
    clientId: string;
    createdAt: string;
    sellerReply: SellerReply | null;
}

const CATEGORY_LABEL: Record<string, string> = {
    ELECTRONICS_IT: "Electronics",
    FASHION_ACCESSORIES: "Fashion",
    HOME_GARDEN: "Home",
    AUTO_MOTO: "Auto & Moto",
    SPORTS_OUTDOOR: "Sports",
    GAMES_GAMING: "Games",
};

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";
const PLACEHOLDER =
    "https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=900&q=80";

function imageUrl(objectName: string | undefined): string {
    if (!objectName) return PLACEHOLDER;
    if (objectName.startsWith("http")) return objectName;
    return `${MINIO_BASE}/${objectName}`;
}

function mapToCartProduct(p: ApiProduct): Product {
    return {
        id: p.id,
        title: p.name,
        description: p.description,
        price: p.price,
        stock: p.stock,
        imageUrls: p.imageUrls,
        category: p.category as InterestType,
        available: p.stock > 0,
        averageRating: p.averageRating ?? 0,
        sellerId: p.sellerId,
    };
}

function StarRating({ rating, size = 16 }: { rating: number | string; size?: number }) {
    const numericRating = Math.round(Number(rating)) || 0;

    return (
        <div className="flex gap-1">
            {[1, 2, 3, 4, 5].map((star) => (
                <Star
                    key={star}
                    size={size}
                    className={
                        star <= numericRating
                            ? "text-[#F6B21A] fill-[#F6B21A]"
                            : "text-gray-200"
                    }
                />
            ))}
        </div>
    );
}

function ProductDetailPage() {
    const { id } = useParams<{ id: string }>();
    const location = useLocation();
    const navigate = useNavigate();
    const { token } = useAuth();
    const { addToCart } = useCart();

    const [product, setProduct] = useState<ApiProduct | null>(
        (location.state as { product?: ApiProduct })?.product ?? null
    );

    const [selectedImg, setSelectedImg] = useState(0);
    const [productLoading, setProductLoading] = useState(!product);
    const [addedToCart, setAddedToCart] = useState(false);
    const [sellerName, setSellerName] = useState<string | null>(null);

    const [reviews, setReviews] = useState<Review[]>([]);
    const [reviewsLoading, setReviewsLoading] = useState(false);
    const [newRating, setNewRating] = useState(0);
    const [newComment, setNewComment] = useState("");
    const [reviewError, setReviewError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [isEligible, setIsEligible] = useState(false);
    const [eligibilityLoading, setEligibilityLoading] = useState(true);

    const [showRestrictedMessage, setShowRestrictedMessage] = useState(false);

    const [hasAlreadyReviewed, setHasAlreadyReviewed] = useState(false);
    const currentUserId = getClientIdFromJwt();

    const [reviewToDelete, setReviewToDelete] = useState<string | null>(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        if (product || !id) return;

        setProductLoading(true);

        axiosInstance
            .get<ApiProduct[]>("/v1/products/search")
            .then((res) => {
                const found = res.data.find((p) => p.id === id) ?? null;
                setProduct(found);
            })
            .catch(() => setProduct(null))
            .finally(() => setProductLoading(false));
    }, [id, product]);

    useEffect(() => {
        if (!token || !product) return;

        axiosInstance
            .get<{ id: string; name: string }>("/v1/users/seller/profile", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((res) => {
                if (res.data.id === product.sellerId) {
                    setSellerName(res.data.name);
                }
            })
            .catch(() => {});
    }, [product, token]);

    useEffect(() => {
        const checkEligibility = async () => {
            if (!id || !token) {
                setEligibilityLoading(false);
                setIsEligible(false);
                return;
            }

            setEligibilityLoading(true);

            try {
                const ordersRes = await OrderService.getClientOrders();
                const receivedOrders = ordersRes.data.filter(
                    (order: any) => order.statusType === "RECEIVED"
                );

                let foundMatch = false;

                for (const order of receivedOrders) {
                    const fullOrder = await OrderService.getOrderDetails(order.id);

                    const containsProduct = fullOrder.data.orderProducts.some(
                        (p: any) => p.productId === id
                    );

                    if (containsProduct) {
                        foundMatch = true;
                        break;
                    }
                }

                setIsEligible(foundMatch);
            } catch (err) {
                console.error("Eligibility check failed", err);
                setIsEligible(false);
            } finally {
                setEligibilityLoading(false);
            }
        };

        checkEligibility();
    }, [id, token]);

    const fetchReviews = () => {
        if (!id || !token) return;

        setReviewsLoading(true);

        axiosInstance
            .get<Review[]>(`/v1/reviews/product/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((res) => {
                console.table(res.data);
                const mappedReviews = res.data.map((rev) => ({
                    ...rev,
                    rating: typeof rev.rating === 'string' ? parseInt(rev.rating, 10) : rev.rating,
                }));

                console.log("--- DATE DUPĂ MAPARE (FRONTEND) ---");
                console.table(mappedReviews);

                const sortedReviews = mappedReviews.sort(
                    (a, b) =>
                        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
                );

                setReviews(sortedReviews);

                if (currentUserId) {
                    const alreadyDidIt = res.data.some(rev => rev.clientId === currentUserId);
                    setHasAlreadyReviewed(alreadyDidIt);
                }
            })
            .catch((err) => {
                console.error("Error fetching reviews:", err);
                setReviews([]);
            })
            .finally(() => setReviewsLoading(false));
    };

    useEffect(() => {
        fetchReviews();
    }, [id, token]);

    const handleAddToCart = () => {
        if (!product) return;

        addToCart(mapToCartProduct(product));
        setAddedToCart(true);

        setTimeout(() => setAddedToCart(false), 1500);
    };

    const handleSubmitReview = async () => {
        if (!token) {
            setReviewError("Please log in before writing a review.");
            return;
        }

        if (!isEligible) {
            setReviewError("You can only review products that have been delivered to you.");
            return;
        }

        if (newRating === 0) {
            setReviewError("Please select a star rating.");
            return;
        }

        if (hasAlreadyReviewed) {
            setReviewError("You have already submitted a review for this product.");
            return;
        }

        setIsSubmitting(true);
        setReviewError(null);

        try {
            await axiosInstance.post(
                "/v1/reviews",
                {
                    productId: id,
                    rating: newRating,
                    comment: newComment,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            setNewRating(0);
            setNewComment("");
            fetchReviews();
        } catch (err: any) {
            setReviewError(
                err.response?.data?.message || "You cannot review this product."
            );
        } finally {
            setIsSubmitting(false);
        }
    };

    if (productLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center text-gray-400">
                Loading product...
            </div>
        );
    }

    if (!product) {
        return (
            <div className="flex min-h-screen flex-col items-center justify-center gap-4 text-gray-400">
                <p>Product not found.</p>
                <button
                    onClick={() => navigate("/products")}
                    className="text-sm font-semibold underline"
                >
                    Back to catalog
                </button>
            </div>
        );
    }

    const images = product.imageUrls?.length ? product.imageUrls : [""];

    const handleRestrictedFocus = () => {
        if (isEligible || eligibilityLoading || !token) return;

        setShowRestrictedMessage(true);

        setTimeout(() => {
            setShowRestrictedMessage(false);
        }, 5000);
    };

    const handleDeleteReview = async () => {
        if (!reviewToDelete || !token) return;

        try {
            await axiosInstance.delete(`/v1/reviews/${reviewToDelete}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            fetchReviews();
            setShowDeleteModal(false);
            setReviewToDelete(null);
        } catch (err: any) {
            setReviewError("Could not delete review. Please try again.");
            setShowDeleteModal(false);
        }
    };

    return (
        <div className="min-h-screen bg-[#F8FAFC] px-6 py-8 md:px-16 font-sans text-black">
            <button
                onClick={() => navigate("/products")}
                className="mb-8 flex items-center gap-2 text-sm font-bold text-gray-500 hover:text-black"
            >
                <ChevronLeft size={18} />
                Back to Products
            </button>

            <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 max-w-7xl mx-auto">
                <div className="lg:col-span-7 space-y-8">
                    <div className="bg-white rounded-[32px] p-6 shadow-sm border border-gray-100">
                        <div className="relative h-[450px] overflow-hidden rounded-2xl bg-[#F3F4F6]">
                            <img
                                src={imageUrl(images[selectedImg])}
                                alt={product.name}
                                className="h-full w-full object-contain p-4"
                                onError={(e) => {
                                    (e.target as HTMLImageElement).src = PLACEHOLDER;
                                }}
                            />

                            {images.length > 1 && (
                                <>
                                    <button
                                        onClick={() =>
                                            setSelectedImg(
                                                (i) => (i - 1 + images.length) % images.length
                                            )
                                        }
                                        className="absolute left-3 top-1/2 -translate-y-1/2 flex h-10 w-10 items-center justify-center rounded-full bg-white/80 shadow-md transition hover:bg-white"
                                    >
                                        <ChevronLeft size={20} className="text-gray-700" />
                                    </button>

                                    <button
                                        onClick={() =>
                                            setSelectedImg((i) => (i + 1) % images.length)
                                        }
                                        className="absolute right-3 top-1/2 -translate-y-1/2 flex h-10 w-10 items-center justify-center rounded-full bg-white/80 shadow-md transition hover:bg-white"
                                    >
                                        <ChevronRight size={20} className="text-gray-700" />
                                    </button>
                                </>
                            )}
                        </div>

                        {images.length > 1 && (
                            <div className="mt-4 flex gap-3 overflow-x-auto pb-1">
                                {images.map((img, i) => (
                                    <button
                                        key={i}
                                        onClick={() => setSelectedImg(i)}
                                        className={`h-20 w-20 shrink-0 overflow-hidden rounded-xl border-2 transition ${
                                            selectedImg === i
                                                ? "border-[#54457F]"
                                                : "border-transparent opacity-60"
                                        }`}
                                    >
                                        <img
                                            src={imageUrl(img)}
                                            alt=""
                                            className="h-full w-full object-cover rounded-lg"
                                            onError={(e) => {
                                                (e.target as HTMLImageElement).src = PLACEHOLDER;
                                            }}
                                        />
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className="bg-white rounded-[32px] p-8 shadow-sm border border-gray-100">
                        <p className="text-xs font-black uppercase tracking-[0.2em] mb-4 text-[#54457F]">
                            {CATEGORY_LABEL[product.category] ?? product.category}
                        </p>

                        <div className="flex justify-between items-start gap-4 mb-4">
                            <h1 className="text-4xl font-black">{product.name}</h1>
                            <p className="text-3xl font-black text-black">
                                ${product.price.toFixed(2)}
                            </p>
                        </div>

                        <div className="mb-4 flex items-center gap-2">
                            {product.averageRating != null && product.averageRating > 0 ? (
                                <>
                                    <StarRating rating={product.averageRating} size={18} />
                                    <span className="text-sm font-medium text-gray-700">
                                        {Number(product.averageRating).toFixed(1)}
                                    </span>
                                    <span className="text-sm text-gray-400">
                                        ({reviews.length} review{reviews.length !== 1 ? "s" : ""})
                                    </span>
                                </>
                            ) : (
                                <span className="text-sm text-gray-400">No ratings yet</span>
                            )}
                        </div>

                        <div className="mb-6">
                            {product.stock > 0 ? (
                                <span className="rounded-full bg-green-100 px-3 py-1 text-xs font-semibold text-green-700">
                                    In Stock — {product.stock} available
                                </span>
                            ) : (
                                <span className="rounded-full bg-red-100 px-3 py-1 text-xs font-semibold text-red-600">
                                    Out of Stock
                                </span>
                            )}
                        </div>

                        <p className="text-gray-600 leading-relaxed mb-8">
                            {product.description}
                        </p>

                        <button
                            onClick={handleAddToCart}
                            disabled={product.stock === 0}
                            style={{
                                backgroundColor: addedToCart ? "#10B981" : "#54457F",
                                color: "#FFFFFF",
                            }}
                            className="w-full flex items-center justify-center gap-3 py-4 rounded-2xl font-black transition-all hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50 shadow-lg"
                        >
                            <ShoppingCart size={22} />
                            {addedToCart ? "Added to Cart!" : "Add to Cart"}
                        </button>
                    </div>

                    <div className="rounded-[32px] bg-white p-8 shadow-sm border border-gray-100">
                        <h3 className="mb-4 text-sm font-bold uppercase tracking-wide text-gray-500">
                            Seller
                        </h3>

                        <div className="flex items-center gap-4">
                            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-[#E9E7FF] text-lg font-bold text-[#54457F]">
                                {(sellerName ?? "S")[0].toUpperCase()}
                            </div>

                            <div>
                                <p className="font-semibold text-gray-800 capitalize">
                                    {sellerName ?? "Verified Seller"}
                                </p>
                                <p className="text-sm text-gray-400">Marketplace seller</p>
                            </div>
                        </div>
                    </div>

                    <section className="bg-white rounded-[32px] p-8 shadow-sm border border-gray-100 relative overflow-hidden">
                        {showRestrictedMessage && (
                            <div className="absolute inset-0 bg-white/75 backdrop-blur-[2px] z-10 flex flex-col items-center justify-center text-center p-6">
                                <div className="bg-gray-100 p-4 rounded-full mb-4">
                                    <Lock size={30} className="text-gray-400" />
                                </div>

                                <h3 className="text-lg font-black text-black mb-2">
                                    Review Restricted
                                </h3>

                                <p className="text-sm text-gray-500 max-w-xs">
                                    You can only write a review for products that have been{" "}
                                    <strong>delivered</strong> to your address.
                                </p>
                            </div>
                        )}

                        <div className="flex items-center gap-2 mb-6">
                            <h2 className="text-xl font-black uppercase tracking-tight">
                                Write a Review
                            </h2>
                            <div className="w-1.5 h-1.5 bg-[#E85D75] rounded-full" />
                        </div>

                        {reviewError && (
                            <div className="mb-6 p-4 bg-red-50 text-[#E85D75] rounded-2xl flex items-center gap-3 border border-red-100 text-sm font-bold">
                                <AlertCircle size={18} />
                                {reviewError}
                            </div>
                        )}

                        <div className="space-y-6">
                            <div>
                                <label className="text-[10px] font-black uppercase tracking-widest text-gray-400 block mb-3">
                                    Rating
                                </label>

                                <div className="flex gap-2">
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <button
                                            key={star}
                                            type="button"
                                            onClick={() => {
                                                if (!isEligible) {
                                                    handleRestrictedFocus();
                                                    return;
                                                }
                                                setNewRating(star);
                                            }}
                                            disabled={eligibilityLoading}
                                            className={`transition-transform active:scale-90 ${
                                                !isEligible && !eligibilityLoading ? "cursor-not-allowed" : ""
                                            }`}
                                        >
                                            <Star
                                                size={30}
                                                className={
                                                    star <= newRating
                                                        ? "text-[#F6B21A] fill-[#F6B21A]"
                                                        : "text-gray-200"
                                                }
                                            />
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div>
                                <label className="text-[10px] font-black uppercase tracking-widest text-gray-400 block mb-3">
                                    Your Comment
                                </label>

                                <textarea
                                    value={newComment}
                                    onChange={(e) => {
                                        if (!isEligible) return;
                                        setNewComment(e.target.value);
                                    }}
                                    onFocus={handleRestrictedFocus}
                                    placeholder="Share your experience..."
                                    readOnly={!isEligible || eligibilityLoading}
                                    className={`w-full bg-[#F9FAFB] rounded-2xl p-4 text-sm border-none focus:ring-2 focus:ring-[#54457F] outline-none min-h-[120px] ${
                                        !isEligible && !eligibilityLoading ? "cursor-not-allowed" : ""
                                    }`}
                                />
                            </div>

                            <div className="flex justify-end">
                                <button
                                    type="button"
                                    onClick={handleSubmitReview}
                                    disabled={
                                        isSubmitting ||
                                        !token ||
                                        !isEligible ||
                                        eligibilityLoading
                                    }
                                    style={{
                                        backgroundColor: "#54457F",
                                        color: "#FFFFFF",
                                    }}
                                    className="font-black px-8 py-3 rounded-xl flex items-center gap-2 shadow-md transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    {isSubmitting ? "Submitting..." : "Submit Review"}
                                    <Send size={16} />
                                </button>
                            </div>
                        </div>
                    </section>
                </div>

                <div className="lg:col-span-5 space-y-6">
                    <div className="bg-white rounded-[32px] p-8 shadow-sm border border-gray-100 sticky top-8">
                        <div className="flex justify-between items-center mb-8">
                            <h2 className="text-2xl font-black">Client Feedback</h2>

                            <span className="bg-[#E9E7FF] text-[#54457F] px-4 py-1 rounded-full text-xs font-black">
                                {reviews.length} total
                            </span>
                        </div>

                        {!token ? (
                            <div className="text-center py-10 bg-gray-50 rounded-3xl border-2 border-dashed border-gray-200">
                                <p className="text-gray-500 font-bold mb-4">
                                    Please log in to see reviews
                                </p>

                                <button
                                    onClick={() => navigate("/login")}
                                    className="text-[#54457F] font-black underline"
                                >
                                    Login Now
                                </button>
                            </div>
                        ) : reviewsLoading ? (
                            <div className="space-y-4 animate-pulse">
                                {[1, 2, 3].map((i) => (
                                    <div key={i} className="h-24 bg-gray-100 rounded-2xl" />
                                ))}
                            </div>
                        ) : (
                            <div className="space-y-6 max-h-[800px] overflow-y-auto pr-2 custom-scrollbar">
                                {reviews.length === 0 ? (
                                    <p className="text-center text-gray-400 italic py-10">
                                        Be the first to review this product!
                                    </p>
                                ) : (
                                    reviews.map((rev) => (
                                        <div
                                            key={rev.id}
                                            className="border-b border-gray-100 pb-6 last:border-0 group"
                                        >
                                            <div className="flex justify-between items-center mb-3">
                                                <StarRating rating={rev.rating} size={14} />

                                                <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">
                                                    {new Date(rev.createdAt).toLocaleDateString()}
                                                </span>
                                            </div>

                                            <div className="flex items-center gap-3">
                                                {rev.clientId === currentUserId && (
                                                    <button
                                                        onClick={() => {
                                                            setReviewToDelete(rev.id);
                                                            setShowDeleteModal(true);
                                                        }}
                                                        className="p-2 rounded-full text-gray-400 hover:text-red-500 hover:bg-red-50 transition-all"
                                                        title="Delete review"
                                                    >
                                                        <Trash2 size={16} />
                                                    </button>
                                                )}

                                                <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">
                                                     {new Date(rev.createdAt).toLocaleDateString()}
                                                 </span>
                                            </div>

                                            {rev.comment && (
                                                <p className="text-sm font-medium text-gray-700 mb-4">
                                                    {rev.comment}
                                                </p>
                                            )}

                                            {rev.sellerReply && (
                                                <div className="bg-gray-50 rounded-2xl p-4 border border-gray-100 relative mt-2">
                                                    <div className="flex items-center gap-2 mb-2 text-[#54457F] font-black text-[10px] uppercase tracking-wider">
                                                        <Reply size={12} className="rotate-180" />
                                                        Seller Response
                                                    </div>

                                                    <p className="text-xs text-gray-600 italic">
                                                        "{rev.sellerReply.reply}"
                                                    </p>
                                                </div>
                                            )}
                                        </div>
                                    ))
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
            {showDeleteModal && (
                <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 backdrop-blur-sm">
                    <div className="bg-white p-8 rounded-[32px] shadow-2xl max-w-sm w-full mx-4 border border-gray-100">
                        <h3 className="text-xl font-black mb-2">Are you sure?</h3>
                        <p className="text-gray-500 text-sm mb-8 font-medium">
                            This action cannot be undone. Your review will be permanently removed.
                        </p>
                        <div className="flex gap-3">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                className="flex-1 py-3 rounded-xl font-bold text-gray-500 bg-gray-100 hover:bg-gray-200 transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDeleteReview}
                                className="flex-1 py-3 rounded-xl font-bold !text-white !bg-red-500 hover:!bg-red-600 transition-colors shadow-lg shadow-red-200"
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ProductDetailPage;