import { useState, useEffect } from "react";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import {ChevronLeft, ChevronRight, ShoppingCart} from "lucide-react";
import axiosInstance from "../../services/axios";
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
    rating: number;
    comment: string;
    clientId: string;
    createdAt: string;
    sellerReply: SellerReply | null;
}

const CATEGORY_LABEL: Record<string, string> = {
    ELECTRONICS_IT:      "Electronics",
    FASHION_ACCESSORIES: "Fashion",
    HOME_GARDEN:         "Home",
    AUTO_MOTO:           "Auto & Moto",
    SPORTS_OUTDOOR:      "Sports",
    GAMES_GAMING:        "Games",
};

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";
const PLACEHOLDER = "https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=900&q=80";

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

function StarRating({ rating }: { rating: number }) {
    return (
        <span>
            {Array.from({ length: 5 }, (_, i) => (
                <span key={i} style={{ color: i < Math.round(rating) ? "#F6B21A" : "#D1D5DB" }}>★</span>
            ))}
        </span>
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
    const [reviews, setReviews] = useState<Review[]>([]);
    const [reviewsLoading, setReviewsLoading] = useState(false);
    const [productLoading, setProductLoading] = useState(!product);
    const [addedToCart, setAddedToCart] = useState(false);
    const [sellerName, setSellerName] = useState<string | null>(null);

    // Fallback: load from search if no state was passed
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
    }, [id]);

    // Fetch seller name (only works when the viewer is the seller themselves)
    useEffect(() => {
        if (!token || !product) return;
        axiosInstance
            .get<{ id: string; name: string }>("/v1/users/seller/profile", {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((res) => {
                if (res.data.id === product.sellerId) setSellerName(res.data.name);
            })
            .catch(() => {});
    }, [product, token]);

    // Fetch reviews (requires auth)
    useEffect(() => {
        if (!id || !token) return;
        setReviewsLoading(true);
        axiosInstance
            .get<Review[]>(`/v1/reviews/product/${id}`, {
                headers: { Authorization: `Bearer ${token}` },
            })
            .then((res) => setReviews(res.data))
            .catch(() => setReviews([]))
            .finally(() => setReviewsLoading(false));
    }, [id, token]);

    const handleAddToCart = () => {
        if (!product) return;
        addToCart(mapToCartProduct(product));
        setAddedToCart(true);
        setTimeout(() => setAddedToCart(false), 1500);
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
                <button onClick={() => navigate("/products")} className="text-sm font-semibold underline">
                    Back to catalog
                </button>
            </div>
        );
    }

    const images = product.imageUrls?.length ? product.imageUrls : [""];

    return (
        <div className="min-h-screen bg-[#F7F7F8] px-8 py-8">
            {/* Breadcrumb */}
            <button
                onClick={() => navigate("/products")}
                className="mb-6 flex items-center gap-2 text-sm font-medium text-gray-500 hover:text-gray-800"
            >
                <ChevronLeft size={16} />
                Back to Products
            </button>

            <div className="grid grid-cols-[1fr_360px] gap-8 items-start">
                {/* ── LEFT: images + details ── */}
                <div className="space-y-6">
                    {/* Image gallery */}
                    <div className="rounded-3xl bg-white p-6 shadow-sm">
                        <div className="relative h-[420px] overflow-hidden rounded-2xl bg-[#F1F1F1]">
                            <img
                                src={imageUrl(images[selectedImg])}
                                alt={product.name}
                                className="h-full w-full object-contain"
                                onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER; }}
                            />
                            {images.length > 1 && (
                                <>
                                    <button
                                        onClick={() => setSelectedImg((i) => (i - 1 + images.length) % images.length)}
                                        className="absolute left-3 top-1/2 -translate-y-1/2 flex h-10 w-10 items-center justify-center rounded-full bg-white/80 shadow-md transition hover:bg-white"
                                    >
                                        <ChevronLeft size={20} className="text-gray-700" />
                                    </button>
                                    <button
                                        onClick={() => setSelectedImg((i) => (i + 1) % images.length)}
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
                                            selectedImg === i ? "border-[#54457F]" : "border-transparent"
                                        }`}
                                    >
                                        <img
                                            src={imageUrl(img)}
                                            alt=""
                                            className="h-full w-full object-cover"
                                            onError={(e) => { (e.target as HTMLImageElement).src = PLACEHOLDER; }}
                                        />
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>

                    {/* Product details */}
                    <div className="rounded-3xl bg-white p-8 shadow-sm">
                        <p className="mb-2 text-xs font-bold uppercase tracking-widest" style={{ color: "#54457F" }}>
                            {CATEGORY_LABEL[product.category] ?? product.category}
                        </p>

                        <div className="flex items-start justify-between gap-4">
                            <h1 className="text-3xl font-extrabold text-black">{product.name}</h1>
                            <p className="shrink-0 text-3xl font-extrabold text-black">
                                ${product.price.toFixed(2)}
                            </p>
                        </div>

                        {/* Rating */}
                        <div className="mt-3 flex items-center gap-2">
                            {product.averageRating != null && product.averageRating > 0 ? (
                                <>
                                    <StarRating rating={product.averageRating} />
                                    <span className="text-sm font-medium text-gray-700">
                                        {product.averageRating.toFixed(1)}
                                    </span>
                                    <span className="text-sm text-gray-400">
                                        ({reviews.length} review{reviews.length !== 1 ? "s" : ""})
                                    </span>
                                </>
                            ) : (
                                <span className="text-sm text-gray-400">No ratings yet</span>
                            )}
                        </div>

                        {/* Stock */}
                        <div className="mt-4">
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

                        {/* Description */}
                        {product.description && (
                            <div className="mt-6">
                                <h3 className="mb-2 text-sm font-bold uppercase tracking-wide text-gray-500">
                                    Description
                                </h3>
                                <p className="text-sm leading-relaxed text-gray-700">{product.description}</p>
                            </div>
                        )}

                        {/* Add to cart */}
                        <button
                            onClick={handleAddToCart}
                            disabled={product.stock === 0}
                            className="mt-8 flex w-full items-center justify-center gap-3 rounded-2xl py-4 text-base font-semibold text-white transition disabled:opacity-50"
                            style={{ backgroundColor: addedToCart ? "#22c55e" : "#54457F" }}
                        >
                            <ShoppingCart size={20} />
                            {addedToCart ? "Added!" : "Add to Cart"}
                        </button>
                    </div>

                    {/* Seller info */}
                    <div className="rounded-3xl bg-white p-8 shadow-sm">
                        <h3 className="mb-4 text-sm font-bold uppercase tracking-wide text-gray-500">Seller</h3>
                        <div className="flex items-center gap-4">
                            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-[#E9E7FF] text-lg font-bold" style={{ color: "#54457F" }}>
                                {(sellerName ?? "S")[0].toUpperCase()}
                            </div>
                            <div>
                                <p className="font-semibold text-gray-800" style={{ textTransform: "capitalize" }}>
                                    {sellerName ?? "Verified Seller"}
                                </p>
                                <p className="text-sm text-gray-400">Marketplace seller</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* ── RIGHT: reviews ── */}
                <aside className="sticky top-8 rounded-3xl bg-white p-6 shadow-sm">
                    <h2 className="mb-1 text-xl font-bold">Reviews</h2>
                    <p className="mb-5 text-sm text-gray-500">
                        {reviews.length} review{reviews.length !== 1 ? "s" : ""}
                    </p>

                    {!token ? (
                        <p className="rounded-xl bg-[#F8F8F8] p-4 text-center text-sm text-gray-500">
                            <button onClick={() => navigate("/login")} className="font-semibold underline" style={{ color: "#54457F" }}>
                                Sign in
                            </button>{" "}
                            to see reviews.
                        </p>
                    ) : reviewsLoading ? (
                        <p className="text-center text-sm text-gray-400">Loading reviews...</p>
                    ) : reviews.length === 0 ? (
                        <p className="rounded-xl bg-[#F8F8F8] p-4 text-center text-sm text-gray-400">
                            No reviews yet for this product.
                        </p>
                    ) : (
                        <div className="max-h-[70vh] space-y-4 overflow-y-auto pr-1">
                            {reviews.map((review) => (
                                <div key={review.id} className="rounded-2xl bg-[#F8F8F8] p-4">
                                    <div className="flex items-center justify-between">
                                        <StarRating rating={review.rating} />
                                        <span className="text-xs text-gray-400">
                                            {new Date(review.createdAt).toLocaleDateString()}
                                        </span>
                                    </div>

                                    {review.comment && (
                                        <p className="mt-2 text-sm text-gray-700">{review.comment}</p>
                                    )}

                                    {review.sellerReply && (
                                        <div className="mt-3 rounded-xl border-l-2 bg-white pl-3 pr-3 py-2" style={{ borderColor: "#54457F" }}>
                                            <p className="text-xs font-semibold mb-1" style={{ color: "#54457F" }}>
                                                Seller reply
                                            </p>
                                            <p className="text-xs text-gray-600">{review.sellerReply.reply}</p>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </aside>
            </div>
        </div>
    );
}

export default ProductDetailPage;
