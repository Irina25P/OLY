import { useState, useEffect } from "react";
import {
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    Search,
    ShoppingCart,
} from "lucide-react";
import { Link } from "react-router-dom";
import axiosInstance from "../../services/axios";
import { useCart } from "../../contexts/CartContext";
import type { InterestType } from "../../models/InterestType";
import type { Product } from "../../models/Product";

interface ApiProduct {
    id: string;
    name: string;
    description: string;
    price: number;
    stock: number;
    imageUrls: string[];
    category: InterestType;
    averageRating: number | null;
}

const ALL_CATEGORIES: InterestType[] = [
    "ELECTRONICS",
    "FASHION",
    "HOME",
    "BEAUTY",
    "SPORTS",
    "TOYS",
    "BOOKS",
    "GROCERIES",
];

const CATEGORY_LABEL: Record<InterestType, string> = {
    ELECTRONICS: "Electronics",
    FASHION: "Fashion",
    HOME: "Home",
    BEAUTY: "Beauty",
    SPORTS: "Sports",
    TOYS: "Toys",
    BOOKS: "Books",
    GROCERIES: "Groceries",
};

const PLACEHOLDER =
    "https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=900&q=80";

function mapToCartProduct(p: ApiProduct): Product {
    return {
        id: p.id,
        title: p.name,
        description: p.description,
        price: p.price,
        stock: p.stock,
        imageUrls: p.imageUrls,
        category: p.category,
        available: p.stock > 0,
        averageRating: p.averageRating ?? 0,
        sellerId: "",
    };
}

function ProductCatalogPage() {
    const [products, setProducts] = useState<ApiProduct[]>([]);
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");
    const [debouncedSearch, setDebouncedSearch] = useState("");
    const [selectedCategories, setSelectedCategories] = useState<InterestType[]>([]);
    const [minPrice, setMinPrice] = useState("");
    const [maxPrice, setMaxPrice] = useState("");
    const [ratingFilter, setRatingFilter] = useState<number | null>(null);

    const { addToCart, totalItems } = useCart();

    useEffect(() => {
        const timer = setTimeout(() => setDebouncedSearch(searchTerm.trim()), 400);
        return () => clearTimeout(timer);
    }, [searchTerm]);

    useEffect(() => {
        const fetchProducts = async () => {
            setLoading(true);
            try {
                const params = new URLSearchParams();
                if (debouncedSearch) params.append("name", debouncedSearch);
                selectedCategories.forEach((cat) => params.append("categories", cat));
                if (minPrice) params.append("minPrice", minPrice);
                if (maxPrice) params.append("maxPrice", maxPrice);
                if (ratingFilter !== null) params.append("minRating", String(ratingFilter));

                const qs = params.toString();
                const { data } = await axiosInstance.get<ApiProduct[]>(
                    `/v1/products${qs ? `?${qs}` : ""}`
                );
                setProducts(data);
            } catch {
                setProducts([]);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, [debouncedSearch, selectedCategories, minPrice, maxPrice, ratingFilter]);

    const toggleCategory = (value: InterestType) => {
        setSelectedCategories((prev) =>
            prev.includes(value) ? prev.filter((c) => c !== value) : [...prev, value]
        );
    };

    return (
        <div className="min-h-screen w-full bg-[#F7F7F8] text-[#000000]">
            <div className="grid w-full grid-cols-[260px_minmax(0,1fr)]">
                <aside className="min-h-screen border-r border-gray-200 bg-[#F4F5F7] px-7 py-8 text-left">
                    <h2 className="text-xl font-bold leading-none text-black">Filters</h2>
                    <p className="mt-2 text-xs text-gray-500">Refine your search</p>

                    <div className="my-7 h-px w-full bg-gray-200" />

                    <div>
                        <h3 className="mb-4 text-[12px] font-bold uppercase tracking-[0.08em] text-black">
                            Category
                        </h3>

                        <div className="space-y-3 text-[13px] text-gray-700">
                            <label className="flex cursor-pointer items-center gap-3 text-left leading-5">
                                <input
                                    type="checkbox"
                                    checked={selectedCategories.length === 0}
                                    onChange={() => setSelectedCategories([])}
                                    className="!h-4 !w-4 !shrink-0 !rounded !p-0 accent-[#54457F]"
                                />
                                <span className="block flex-1">All Products</span>
                            </label>

                            {ALL_CATEGORIES.map((category) => (
                                <label
                                    key={category}
                                    className="flex cursor-pointer items-center gap-3 text-left leading-5"
                                >
                                    <input
                                        type="checkbox"
                                        checked={selectedCategories.includes(category)}
                                        onChange={() => toggleCategory(category)}
                                        className="!h-4 !w-4 !shrink-0 !rounded !p-0 accent-[#54457F]"
                                    />
                                    <span className="block flex-1">{CATEGORY_LABEL[category]}</span>
                                </label>
                            ))}
                        </div>
                    </div>

                    <div className="mt-9">
                        <h3 className="mb-4 text-[12px] font-bold uppercase tracking-[0.08em] text-black">
                            Price Range
                        </h3>

                        <div className="grid grid-cols-[1fr_auto_1fr] items-center gap-2">
                            <input
                                type="number"
                                placeholder="$ Min"
                                value={minPrice}
                                onChange={(e) => setMinPrice(e.target.value)}
                                className="!h-9 !min-w-0 !rounded-lg !border !border-gray-200 !bg-white !px-3 !py-0 !text-xs !shadow-none !outline-none focus:!border-[#54457F] focus:!shadow-none"
                            />
                            <span className="text-xs text-gray-400">-</span>
                            <input
                                type="number"
                                placeholder="$ Max"
                                value={maxPrice}
                                onChange={(e) => setMaxPrice(e.target.value)}
                                className="!h-9 !min-w-0 !rounded-lg !border !border-gray-200 !bg-white !px-3 !py-0 !text-xs !shadow-none !outline-none focus:!border-[#54457F] focus:!shadow-none"
                            />
                        </div>
                    </div>

                    <div className="mt-9">
                        <h3 className="mb-4 text-[12px] font-bold uppercase tracking-[0.08em] text-black">
                            Rating
                        </h3>

                        <div className="space-y-3 text-[13px] text-gray-700">
                            {([5, 4, 3] as const).map((stars) => (
                                <label
                                    key={stars}
                                    className="flex cursor-pointer items-center gap-3 leading-5"
                                >
                                    <input
                                        type="radio"
                                        name="rating"
                                        onChange={() => setRatingFilter(stars)}
                                        checked={ratingFilter === stars}
                                        className="!h-4 !w-4 accent-[#54457F]"
                                    />
                                    <span className="text-[#F6B21A]">
                                        {"★".repeat(stars)}{"☆".repeat(5 - stars)}
                                    </span>
                                    <span className="text-gray-500">& Up</span>
                                </label>
                            ))}

                            <label className="flex cursor-pointer items-center gap-3 leading-5">
                                <input
                                    type="radio"
                                    name="rating"
                                    onChange={() => setRatingFilter(null)}
                                    checked={ratingFilter === null}
                                    className="!h-4 !w-4 accent-[#54457F]"
                                />
                                <span className="text-gray-500">All Ratings</span>
                            </label>
                        </div>
                    </div>

                    <div className="mt-9">
                        <h3 className="mb-4 text-[12px] font-bold uppercase tracking-[0.08em] text-black">
                            Availability
                        </h3>

                        <div className="flex items-center justify-between text-[13px] text-gray-700">
                            <span>In Stock Only</span>

                            <button className="flex h-6 w-11 items-center rounded-full bg-gray-300 p-1">
                                <span className="h-4 w-4 rounded-full bg-white shadow-sm" />
                            </button>
                        </div>
                    </div>
                </aside>

                <main className="w-full px-8 py-8">
                    <div className="mb-8 flex items-start justify-between gap-6">
                        <div>
                            <h1 className="text-4xl font-extrabold leading-none text-black">
                                Shop All
                            </h1>
                            <p className="mt-4 text-base text-gray-600">
                                {loading
                                    ? "Loading..."
                                    : `Showing ${products.length} product${products.length !== 1 ? "s" : ""}`}
                            </p>
                        </div>

                        <div className="flex items-center gap-4">
                            <div className="flex h-12 w-[400px] items-center gap-3 rounded-full border border-gray-200 bg-white px-5 shadow-sm">
                                <Search size={20} className="shrink-0 text-gray-400" />
                                <input
                                    placeholder="Search products..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="!h-full !min-w-0 !flex-1 !border-0 !bg-transparent !p-0 !text-sm !text-gray-700 !shadow-none !outline-none focus:!border-0 focus:!shadow-none focus:!outline-none"
                                />
                            </div>

                            <Link
                                to="/checkout"
                                className="relative flex h-12 w-12 shrink-0 items-center justify-center rounded-full border border-gray-200 bg-white shadow-sm text-gray-700 transition hover:text-[#54457F]"
                            >
                                <ShoppingCart size={22} />
                                {totalItems > 0 && (
                                    <span className="absolute -top-1 -right-1 flex min-w-[18px] h-[18px] items-center justify-center rounded-full bg-[#54457F] px-1 text-[10px] font-bold text-white">
                                        {totalItems}
                                    </span>
                                )}
                            </Link>

                            <div className="flex items-center gap-3">
                                <span className="text-sm text-gray-600">Sort by:</span>

                                <button className="flex h-12 items-center gap-10 rounded-full border border-gray-200 bg-white px-5 text-sm text-gray-700 shadow-sm">
                                    Recommended
                                    <ChevronDown size={16} />
                                </button>
                            </div>
                        </div>
                    </div>

                    {loading ? (
                        <div className="flex items-center justify-center py-24 text-gray-400">
                            Loading products...
                        </div>
                    ) : products.length === 0 ? (
                        <div className="flex items-center justify-center py-24 text-gray-400">
                            No products found.
                        </div>
                    ) : (
                        <div className="grid grid-cols-4 gap-6 2xl:grid-cols-5">
                            {products.map((product) => (
                                <article
                                    key={product.id}
                                    className="flex flex-col overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-md"
                                >
                                    <div className="h-[180px] bg-[#F1F1F1]">
                                        <img
                                            src={product.imageUrls?.[0] || PLACEHOLDER}
                                            alt={product.name}
                                            className="h-full w-full object-cover"
                                            onError={(e) => {
                                                (e.target as HTMLImageElement).src = PLACEHOLDER;
                                            }}
                                        />
                                    </div>

                                    <div className="flex flex-1 flex-col p-4">
                                        <p className="mb-2 text-[11px] font-bold uppercase tracking-wide text-[#54457F]">
                                            {CATEGORY_LABEL[product.category] ?? product.category}
                                        </p>

                                        <div className="flex items-start justify-between gap-3">
                                            <h3 className="line-clamp-2 text-[16px] font-extrabold leading-[1.15] text-black">
                                                {product.name}
                                            </h3>

                                            <p className="whitespace-nowrap text-[15px] font-bold text-black">
                                                ${product.price.toFixed(2)}
                                            </p>
                                        </div>

                                        <div className="mt-3 flex items-center gap-1 text-[13px]">
                                            {product.averageRating !== null ? (
                                                <>
                                                    <span className="text-[#F6B21A]">★</span>
                                                    <span className="font-medium text-black">
                                                        {product.averageRating.toFixed(1)}
                                                    </span>
                                                </>
                                            ) : (
                                                <span className="text-[12px] text-gray-400">No reviews yet</span>
                                            )}
                                        </div>

                                        <button
                                            onClick={() => addToCart(mapToCartProduct(product))}
                                            className="mt-4 flex w-full items-center justify-center gap-2 rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-[14px] font-medium text-black transition hover:bg-[#54457F] hover:text-white hover:border-[#54457F]"
                                        >
                                            <ShoppingCart size={16} />
                                            Add to Cart
                                        </button>
                                    </div>
                                </article>
                            ))}
                        </div>
                    )}

                    {!loading && products.length > 0 && (
                        <div className="mt-10 flex items-center justify-center gap-5 text-sm">
                            <button className="text-gray-400">
                                <ChevronLeft size={18} />
                            </button>

                            <button className="rounded-lg bg-[#E9E7FF] px-4 py-3 font-semibold text-[#54457F]">
                                1
                            </button>

                            <button>
                                <ChevronRight size={18} />
                            </button>
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
}

export default ProductCatalogPage;
