import {
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    Heart,
    Search,
    ShoppingCart,
} from "lucide-react";
import { useState } from "react"
const products = [
    {
        id: 1,
        title: "Aura Studio ANC Headphones",
        seller: "SoundScape Tech",
        price: "$299.00",
        rating: "2.9",
        reviews: "128",
        category: "Electronics",
        image:
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 2,
        title: "Lumina Series 4 Watch",
        seller: "WearTech",
        price: "$199.00",
        rating: "4.7",
        reviews: "84",
        category: "Electronics",
        image:
            "https://images.unsplash.com/photo-1546868871-7041f2a55e12?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 3,
        title: "Aero Sprint Pro",
        seller: "Velocity Athletics",
        price: "$145.00",
        rating: "4.8",
        reviews: "312",
        category: "Fashion",
        image:
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 4,
        title: "RetroSnap Instant Camera",
        seller: "Nostalgia Co.",
        price: "$89.00",
        rating: "4.5",
        reviews: "67",
        category: "Photography",
        image:
            "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 5,
        title: "Urban Leather Backpack",
        seller: "CarryLab",
        price: "$120.00",
        rating: "4.6",
        reviews: "91",
        category: "Fashion",
        image:
            "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 6,
        title: "Minimal Desk Lamp",
        seller: "HomeGlow",
        price: "$59.00",
        rating: "4.4",
        reviews: "48",
        category: "Home",
        image:
            "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 7,
        title: "Ceramic Coffee Set",
        seller: "ClayHouse",
        price: "$42.00",
        rating: "4.8",
        reviews: "73",
        category: "Home",
        image:
            "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 8,
        title: "Cotton Oversized Hoodie",
        seller: "SoftWear Studio",
        price: "$75.00",
        rating: "4.6",
        reviews: "156",
        category: "Fashion",
        image:
            "https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 9,
        title: "Bluetooth Speaker Mini",
        seller: "SoundScape Tech",
        price: "$64.00",
        rating: "4.5",
        reviews: "102",
        category: "Electronics",
        image:
            "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 10,
        title: "Scented Candle Set",
        seller: "Aura Home",
        price: "$38.00",
        rating: "4.7",
        reviews: "65",
        category: "Home",
        image:
            "https://images.unsplash.com/photo-1603006905003-be475563bc59?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 11,
        title: "Classic White Sneakers",
        seller: "Velocity Athletics",
        price: "$110.00",
        rating: "4.9",
        reviews: "203",
        category: "Fashion",
        image:
            "https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 12,
        title: "Digital Drawing Tablet",
        seller: "Creative Tech",
        price: "$219.00",
        rating: "4.6",
        reviews: "57",
        category: "Electronics",
        image:
            "https://images.unsplash.com/photo-1561154464-82e9adf32764?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 13,
        title: "Handmade Wall Art",
        seller: "Canvas Corner",
        price: "$95.00",
        rating: "4.8",
        reviews: "39",
        category: "Art",
        image:
            "https://images.unsplash.com/photo-1541961017774-22349e4a1262?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 14,
        title: "Wireless Charging Pad",
        seller: "ChargePoint",
        price: "$34.00",
        rating: "4.3",
        reviews: "77",
        category: "Electronics",
        image:
            "https://images.unsplash.com/photo-1615526675159-e248c3021d3f?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 15,
        title: "Decorative Plant Pot",
        seller: "GreenSpace",
        price: "$28.00",
        rating: "4.4",
        reviews: "51",
        category: "Home",
        image:
            "https://images.unsplash.com/photo-1485955900006-10f4d324d411?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 16,
        title: "Travel Duffel Bag",
        seller: "CarryLab",
        price: "$132.00",
        rating: "4.7",
        reviews: "118",
        category: "Fashion",
        image:
            "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 17,
        title: "Smart Home Camera",
        seller: "SecureNest",
        price: "$159.00",
        rating: "4.5",
        reviews: "89",
        category: "Electronics",
        image:
            "https://images.unsplash.com/photo-1558002038-1055907df827?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 18,
        title: "Wooden Serving Tray",
        seller: "Nordic Home",
        price: "$49.00",
        rating: "4.6",
        reviews: "44",
        category: "Home",
        image:
            "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 19,
        title: "Silver Minimal Ring",
        seller: "Luna Jewelry",
        price: "$72.00",
        rating: "4.8",
        reviews: "95",
        category: "Fashion",
        image:
            "https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=900&q=80",
    },
    {
        id: 20,
        title: "Premium Yoga Mat",
        seller: "Balance Studio",
        price: "$68.00",
        rating: "4.7",
        reviews: "134",
        category: "Fitness",
        image:
            "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?auto=format&fit=crop&w=900&q=80",
    },
];

function ProductCatalogPage() {
    const [minPrice, setMinPrice] = useState("")
    const [maxPrice, setMaxPrice] = useState("")
    const [ratingFilter, setRatingFilter] = useState<number | null>(null)
    const [searchTerm, setSearchTerm] = useState("")
    const getNumericPrice = (price: string) => {
        return Number(price.replace("$", ""))
    }

    const filteredProducts = products.filter((product) => {
        const price = getNumericPrice(product.price)
        const rating = Number(product.rating)

        const min = minPrice ? Number(minPrice) : 0
        const max = maxPrice ? Number(maxPrice) : Infinity

        const matchesPrice = price >= min && price <= max
        const matchesRating = ratingFilter ? rating >= ratingFilter : true
        const matchesSearch = product.title
            .toLowerCase()
            .includes(searchTerm.toLowerCase().trim())

        return matchesPrice && matchesRating && matchesSearch
    })
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
                            {[
                                "All Products",
                                "Electronics",
                                "Fashion & Apparel",
                                "Home Goods",
                                "Art & Collectibles",
                                "Fitness",
                            ].map((category, index) => (
                                <label
                                    key={category}
                                    className="flex cursor-pointer items-center gap-3 text-left leading-5"
                                >
                                    <input
                                        type="checkbox"
                                        defaultChecked={index === 0}
                                        className="!h-4 !w-4 !shrink-0 !rounded !p-0 accent-[#54457F]"
                                    />
                                    <span className="block flex-1">{category}</span>
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
                            <label className="flex cursor-pointer items-center gap-3 leading-5">
                                <input
                                    type="radio"
                                    name="rating"
                                    onChange={() => setRatingFilter(5)}
                                    checked={ratingFilter === 5}
                                    className="!h-4 !w-4 accent-[#54457F]"
                                />
                                <span className="text-[#F6B21A]">★★★★★</span>
                                <span className="text-gray-500">& Up</span>
                            </label>

                            <label className="flex cursor-pointer items-center gap-3 leading-5">
                                <input
                                    type="radio"
                                    name="rating"
                                    onChange={() => setRatingFilter(4)}
                                    checked={ratingFilter === 4}
                                    className="!h-4 !w-4 accent-[#54457F]"
                                />
                                <span className="text-[#F6B21A]">★★★★☆</span>
                                <span className="text-gray-500">& Up</span>
                            </label>

                            <label className="flex cursor-pointer items-center gap-3 leading-5">
                                <input
                                    type="radio"
                                    name="rating"
                                    onChange={() => setRatingFilter(3)}
                                    checked={ratingFilter === 3}
                                    className="!h-4 !w-4 accent-[#54457F]"
                                />
                                <span className="text-[#F6B21A]">★★★☆☆</span>
                                <span className="text-gray-500">& Up</span>
                            </label>
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
                                Showing {filteredProducts.length} of {products.length} products
                            </p>
                        </div>

                        <div className="flex items-center gap-6">
                            <div className="flex h-12 w-[460px] items-center gap-3 rounded-full border border-gray-200 bg-white px-5 shadow-sm">
                                <Search size={20} className="shrink-0 text-gray-400" />
                                <input
                                    placeholder="Search products..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="!h-full !min-w-0 !flex-1 !border-0 !bg-transparent !p-0 !text-sm !text-gray-700 !shadow-none !outline-none focus:!border-0 focus:!shadow-none focus:!outline-none"
                                />
                            </div>

                            <div className="flex items-center gap-3">
                                <span className="text-sm text-gray-600">Sort by:</span>

                                <button className="flex h-12 items-center gap-10 rounded-full border border-gray-200 bg-white px-5 text-sm text-gray-700 shadow-sm">
                                    Recommended
                                    <ChevronDown size={16} />
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className="grid grid-cols-4 gap-6 2xl:grid-cols-5">
                        {filteredProducts.map((product) => (
                            <article
                                key={product.id}
                                className="flex h-[390px] flex-col overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-md"
                            >
                                <div className="relative h-[180px] bg-[#F1F1F1]">
                                    <img
                                        src={product.image}
                                        alt={product.title}
                                        className="h-full w-full object-cover"
                                    />

                                    <button className="absolute right-3 top-3 rounded-full bg-white/90 p-2 text-gray-600 shadow-sm transition hover:text-[#E85D75]">
                                        <Heart size={20} />
                                    </button>
                                </div>

                                <div className="flex flex-1 flex-col p-4">
                                    <p className="mb-2 text-[11px] font-bold uppercase tracking-wide text-[#54457F]">
                                        {product.category}
                                    </p>

                                    <div className="flex items-start justify-between gap-3">
                                        <h3 className="line-clamp-2 text-[18px] font-extrabold leading-[1.15] text-black">
                                            {product.title}
                                        </h3>

                                        <p className="whitespace-nowrap text-[15px] font-bold text-black">
                                            {product.price}
                                        </p>
                                    </div>

                                    <p className="mt-3 text-[14px] text-gray-500">
                                        By {product.seller}
                                    </p>

                                    <div className="mt-4 flex items-center gap-1 text-[14px]">
                                        <span className="text-[#F6B21A]">★</span>
                                        <span className="font-medium text-black">{product.rating}</span>
                                        <span className="text-gray-500">({product.reviews})</span>
                                    </div>

                                    <button className="mt-auto flex w-full items-center justify-center gap-2 border-t border-gray-100 bg-white py-3 text-[15px] font-medium text-black transition hover:bg-[#54457F] hover:text-white">
                                        <ShoppingCart size={18} />
                                        Add to Cart
                                    </button>
                                </div>
                            </article>
                        ))}
                    </div>

                    <div className="mt-10 flex items-center justify-center gap-5 text-sm">
                        <button className="text-gray-400">
                            <ChevronLeft size={18} />
                        </button>

                        <button className="rounded-lg bg-[#E9E7FF] px-4 py-3 font-semibold text-[#54457F]">
                            1
                        </button>

                        <button>2</button>
                        <button>3</button>
                        <span>...</span>
                        <button>20</button>

                        <button>
                            <ChevronRight size={18} />
                        </button>
                    </div>
                </main>
            </div>
        </div>
    );
}

export default ProductCatalogPage;