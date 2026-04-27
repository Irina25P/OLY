import { Edit, Plus, Trash2 } from "lucide-react";

const sellerProducts = [
    {
        name: "Luminary Silk Scarf",
        sku: "OLY-SLK-01",
        price: "$145.00",
        stock: 42,
        category: "Accessories",
        status: "Active",
        image: "https://images.unsplash.com/photo-1606760227091-3dd870d97f1d?auto=format&fit=crop&w=300&q=80",
    },
    {
        name: "Raw Amethyst Band",
        sku: "OLY-RNG-88",
        price: "$280.00",
        stock: 3,
        category: "Jewelry",
        status: "Low Stock",
        image: "https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=300&q=80",
    },
    {
        name: "Aura Cleansing Candle",
        sku: "OLY-CND-12",
        price: "$38.00",
        stock: 115,
        category: "Home & Apothecary",
        status: "Draft",
        image: "https://images.unsplash.com/photo-1603006905003-be475563bc59?auto=format&fit=crop&w=300&q=80",
    },
];

function SellerProductsPage() {
    return (
        <div>
            <div className="mb-8 flex items-center justify-between">
                <div>
                    <h1 className="text-4xl font-extrabold">Product Management</h1>
                    <p className="mt-2 max-w-xl text-gray-600">
                        Curate your products. Manage stock, adjust pricing, and organize your digital storefront.
                    </p>
                </div>

                <button className="flex items-center gap-2 rounded-full bg-[#54457F] px-7 py-4 text-sm font-semibold text-white">
                    <Plus size={18} />
                    Add New Product
                </button>
            </div>

            <div className="rounded-3xl bg-[#F1F1F1] p-6 shadow-sm">
                <div className="grid grid-cols-[110px_1.5fr_100px_90px_150px_130px_100px] px-6 pb-4 text-xs font-bold uppercase tracking-wide text-gray-500">
                    <span>Image</span>
                    <span>Title & Details</span>
                    <span>Price</span>
                    <span>Stock</span>
                    <span>Category</span>
                    <span>Status</span>
                    <span>Actions</span>
                </div>

                <div className="space-y-4">
                    {sellerProducts.map((product) => (
                        <div
                            key={product.sku}
                            className="grid grid-cols-[110px_1.5fr_100px_90px_150px_130px_100px] items-center rounded-2xl bg-white px-6 py-4"
                        >
                            <img src={product.image} alt={product.name} className="h-16 w-16 rounded-lg object-cover" />

                            <div>
                                <h3 className="font-bold">{product.name}</h3>
                                <p className="text-sm text-gray-500">SKU: {product.sku}</p>
                            </div>

                            <span className="font-semibold">{product.price}</span>
                            <span className="w-fit rounded-full bg-gray-100 px-3 py-1 text-sm">{product.stock}</span>
                            <span className="text-sm text-gray-700">{product.category}</span>

                            <span className="w-fit rounded-full bg-[#FDE2EA] px-4 py-2 text-xs font-bold text-[#E85D75]">
                {product.status}
              </span>

                            <div className="flex gap-4 text-gray-500">
                                <button><Edit size={18} /></button>
                                <button><Trash2 size={18} /></button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default SellerProductsPage;