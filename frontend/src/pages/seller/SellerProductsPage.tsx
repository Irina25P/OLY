import { useState, useEffect, useRef } from "react";
import { Edit, Plus, Trash2, X, Upload } from "lucide-react";
import axiosInstance from "../../services/axios";
import { useAuth } from "../../context/AuthContext";
interface Product {
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

const CATEGORY_OPTIONS: { value: string; label: string }[] = [
    { value: "ELECTRONICS_IT",      label: "Electronics" },
    { value: "FASHION_ACCESSORIES", label: "Fashion" },
    { value: "HOME_GARDEN",         label: "Home" },
    { value: "AUTO_MOTO",           label: "Auto & Moto" },
    { value: "SPORTS_OUTDOOR",      label: "Sports" },
    { value: "GAMES_GAMING",        label: "Games" },
];

const CATEGORY_LABEL: Record<string, string> = Object.fromEntries(
    CATEGORY_OPTIONS.map(({ value, label }) => [value, label])
);

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";

function imageUrl(objectName: string | undefined): string {
    if (!objectName) return "";
    if (objectName.startsWith("http")) return objectName;
    return `${MINIO_BASE}/${objectName}`;
}

const inputCls =
    "w-full rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-[#54457F]/30";

function AddProductModal({
    onClose,
    onSaved,
    token,
}: {
    onClose: () => void;
    onSaved: () => void;
    token: string | null;
}) {
    const authHeader = { Authorization: `Bearer ${token}` };
    const fileInputRef = useRef<HTMLInputElement>(null);

    // "form" → fill details   "confirm" → review before saving
    const [step, setStep]               = useState<"form" | "confirm">("form");

    const [name, setName]               = useState("");
    const [description, setDescription] = useState("");
    const [price, setPrice]             = useState("");
    const [stock, setStock]             = useState("");
    const [category, setCategory]       = useState("");
    const [imageFiles, setImageFiles]   = useState<File[]>([]);
    const [previews, setPreviews]       = useState<string[]>([]);
    const [loading, setLoading]         = useState(false);
    const [error, setError]             = useState("");

    const addImages = (e: React.ChangeEvent<HTMLInputElement>) => {
        const files = Array.from(e.target.files ?? []);
        if (!files.length) return;
        setImageFiles(prev => [...prev, ...files]);
        setPreviews(prev => [...prev, ...files.map(f => URL.createObjectURL(f))]);
        if (fileInputRef.current) fileInputRef.current.value = "";
    };

    const removeImage = (i: number) => {
        URL.revokeObjectURL(previews[i]);
        setImageFiles(prev => prev.filter((_, idx) => idx !== i));
        setPreviews(prev => prev.filter((_, idx) => idx !== i));
    };

    // Step 1: validate form then show confirmation screen
    const handleReview = (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        if (imageFiles.length === 0) { setError("Add at least one product image."); return; }
        setStep("confirm");
    };

    // Step 2: actually hit the API
    const handleConfirm = async () => {
        setError("");
        setLoading(true);
        try {
            const imageNames: string[] = [];
            for (const file of imageFiles) {
                const fd = new FormData();
                fd.append("file", file);
                const r = await axiosInstance.post<{ objectName: string }>("/v1/images", fd, {
                    headers: { "Content-Type": undefined },
                });
                imageNames.push(r.data.objectName);
            }
            await axiosInstance.post(
                "/v1/products",
                { name, description, price: parseFloat(price), stock: parseInt(stock), imageNames, category },
                { headers: authHeader }
            );
            previews.forEach(u => URL.revokeObjectURL(u));
            onSaved();
        } catch (err: unknown) {
            const axiosErr = err as { response?: { data?: { message?: string } }; message?: string };
            const msg = axiosErr?.response?.data?.message ?? axiosErr?.message ?? "Failed to save product. Please try again.";
            setError(msg);
            setStep("form");
        } finally {
            setLoading(false);
        }
    };

    const isConfirm = step === "confirm";

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
            <div className="w-full max-w-2xl rounded-3xl bg-white shadow-2xl">

                {/* Header */}
                <div className="flex items-center justify-between border-b border-gray-100 px-8 py-5">
                    <div>
                        <h2 className="text-xl font-bold">
                            {isConfirm ? "Confirm Product" : "Add New Product"}
                        </h2>
                        {isConfirm && (
                            <p className="mt-0.5 text-sm text-gray-500">
                                Review the details below before saving to the database.
                            </p>
                        )}
                    </div>
                    <button
                        type="button"
                        onClick={onClose}
                        className="rounded-full p-1.5 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
                    >
                        <X size={20} />
                    </button>
                </div>

                {/* ── STEP 1: form ── */}
                {!isConfirm && (
                    <form onSubmit={handleReview} className="px-8 py-6 space-y-5 max-h-[75vh] overflow-y-auto">
                        {error && (
                            <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-600">
                                {error}
                            </div>
                        )}

                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="mb-1 block text-sm font-semibold text-gray-700">Product Name *</label>
                                <input value={name} onChange={e => setName(e.target.value)} required placeholder="e.g. Running Shoes" className={inputCls} />
                            </div>
                            <div>
                                <label className="mb-1 block text-sm font-semibold text-gray-700">Category *</label>
                                <select value={category} onChange={e => setCategory(e.target.value)} required className={inputCls}>
                                    <option value="">Select a category</option>
                                    {CATEGORY_OPTIONS.map(opt => (
                                        <option key={opt.value} value={opt.value}>{opt.label}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <label className="mb-1 block text-sm font-semibold text-gray-700">Price ($) *</label>
                                <input type="number" min="0.01" step="0.01" value={price} onChange={e => setPrice(e.target.value)} required placeholder="0.00" className={inputCls} />
                            </div>
                            <div>
                                <label className="mb-1 block text-sm font-semibold text-gray-700">Stock *</label>
                                <input type="number" min="0" value={stock} onChange={e => setStock(e.target.value)} required placeholder="0" className={inputCls} />
                            </div>
                        </div>

                        <div>
                            <label className="mb-1 block text-sm font-semibold text-gray-700">Description</label>
                            <textarea value={description} onChange={e => setDescription(e.target.value)} rows={3} placeholder="Describe your product..." className={`${inputCls} resize-none`} />
                        </div>

                        <div>
                            <label className="mb-2 block text-sm font-semibold text-gray-700">
                                Product Images * <span className="font-normal text-gray-400">(at least one)</span>
                            </label>
                            <div className="flex flex-wrap gap-3">
                                {previews.map((src, i) => (
                                    <div key={i} className="relative">
                                        <img src={src} alt="" className="h-20 w-20 rounded-xl object-cover" />
                                        <button
                                            type="button"
                                            onClick={() => removeImage(i)}
                                            className="absolute -right-1.5 -top-1.5 flex h-5 w-5 items-center justify-center rounded-full bg-white shadow text-gray-500 hover:text-red-500"
                                        >
                                            <X size={11} />
                                        </button>
                                    </div>
                                ))}
                                <button
                                    type="button"
                                    onClick={() => fileInputRef.current?.click()}
                                    className="flex h-20 w-20 flex-col items-center justify-center gap-1 rounded-xl border-2 border-dashed border-gray-300 text-gray-400 hover:border-[#54457F] hover:text-[#54457F] transition-colors"
                                >
                                    <Upload size={18} />
                                    <span className="text-xs">Add</span>
                                </button>
                            </div>
                            <input ref={fileInputRef} type="file" accept="image/*" multiple onChange={addImages} className="hidden" />
                        </div>

                        <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                            <button type="button" onClick={onClose} className="rounded-full border border-gray-200 px-6 py-2.5 text-sm font-semibold text-gray-600 hover:bg-gray-50">
                                Cancel
                            </button>
                            <button type="submit" className="rounded-full px-6 py-2.5 text-sm font-semibold text-white" style={{ backgroundColor: "#54457F" }}>
                                Review →
                            </button>
                        </div>
                    </form>
                )}

                {/* ── STEP 2: confirmation ── */}
                {isConfirm && (
                    <div className="px-8 py-6 space-y-5 max-h-[75vh] overflow-y-auto">
                        {error && (
                            <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-600">
                                {error}
                            </div>
                        )}

                        {/* Image strip */}
                        {previews.length > 0 && (
                            <div className="flex gap-3 flex-wrap">
                                {previews.map((src, i) => (
                                    <img key={i} src={src} alt="" className="h-20 w-20 rounded-xl object-cover" />
                                ))}
                            </div>
                        )}

                        {/* Details grid */}
                        <div className="rounded-2xl bg-[#F8F8F8] divide-y divide-gray-200">
                            {[
                                { label: "Product Name", value: name },
                                { label: "Category",     value: CATEGORY_LABEL[category] ?? category },
                                { label: "Price",        value: `$${parseFloat(price).toFixed(2)}` },
                                { label: "Stock",        value: stock },
                                { label: "Description",  value: description || "—" },
                                { label: "Images",       value: `${imageFiles.length} file${imageFiles.length !== 1 ? "s" : ""} selected` },
                            ].map(({ label, value }) => (
                                <div key={label} className="flex items-start gap-4 px-5 py-3">
                                    <span className="w-36 shrink-0 text-sm font-semibold text-gray-500">{label}</span>
                                    <span className="text-sm text-gray-800 break-words">{value}</span>
                                </div>
                            ))}
                        </div>

                        <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                            <button
                                type="button"
                                onClick={() => { setStep("form"); setError(""); }}
                                className="rounded-full border border-gray-200 px-6 py-2.5 text-sm font-semibold text-gray-600 hover:bg-gray-50"
                            >
                                ← Edit
                            </button>
                            <button
                                type="button"
                                onClick={handleConfirm}
                                disabled={loading}
                                className="rounded-full px-6 py-2.5 text-sm font-semibold text-white disabled:opacity-60"
                                style={{ backgroundColor: "#54457F" }}
                            >
                                {loading ? "Saving..." : "Confirm & Save"}
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

function EditProductModal({
    product,
    onClose,
    onSaved,
    token,
}: {
    product: Product;
    onClose: () => void;
    onSaved: () => void;
    token: string | null;
}) {
    const authHeader = { Authorization: `Bearer ${token}` };
    const fileInputRef = useRef<HTMLInputElement>(null);

    const [name, setName]               = useState(product.name);
    const [description, setDescription] = useState(product.description);
    const [price, setPrice]             = useState(String(product.price));
    const [stock, setStock]             = useState(String(product.stock));
    const [category, setCategory]       = useState(product.category);
    const [keptNames, setKeptNames]     = useState<string[]>(product.imageUrls ?? []);
    const [newFiles, setNewFiles]       = useState<File[]>([]);
    const [newPreviews, setNewPreviews] = useState<string[]>([]);
    const [loading, setLoading]         = useState(false);
    const [error, setError]             = useState("");

    const addImages = (e: React.ChangeEvent<HTMLInputElement>) => {
        const files = Array.from(e.target.files ?? []);
        if (!files.length) return;
        setNewFiles(prev => [...prev, ...files]);
        setNewPreviews(prev => [...prev, ...files.map(f => URL.createObjectURL(f))]);
        if (fileInputRef.current) fileInputRef.current.value = "";
    };

    const removeKept = (i: number) => setKeptNames(prev => prev.filter((_, idx) => idx !== i));

    const removeNew = (i: number) => {
        URL.revokeObjectURL(newPreviews[i]);
        setNewFiles(prev => prev.filter((_, idx) => idx !== i));
        setNewPreviews(prev => prev.filter((_, idx) => idx !== i));
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        if (keptNames.length === 0 && newFiles.length === 0) {
            setError("At least one image is required.");
            return;
        }
        setError("");
        setLoading(true);
        try {
            const uploadedNames: string[] = [];
            for (const file of newFiles) {
                const fd = new FormData();
                fd.append("file", file);
                const r = await axiosInstance.post<{ objectName: string }>("/v1/images", fd, {
                    headers: { "Content-Type": undefined },
                });
                uploadedNames.push(r.data.objectName);
            }
            const imageNames = [...keptNames, ...uploadedNames];
            await axiosInstance.put(
                `/v1/products/${product.id}`,
                { name, description, price: parseFloat(price), stock: parseInt(stock), imageNames, category },
                { headers: authHeader }
            );
            newPreviews.forEach(u => URL.revokeObjectURL(u));
            onSaved();
        } catch (err: unknown) {
            const axiosErr = err as { response?: { data?: { message?: string } }; message?: string };
            setError(axiosErr?.response?.data?.message ?? axiosErr?.message ?? "Failed to update product.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
            <div className="w-full max-w-2xl rounded-3xl bg-white shadow-2xl">
                <div className="flex items-center justify-between border-b border-gray-100 px-8 py-5">
                    <h2 className="text-xl font-bold">Edit Product</h2>
                    <button type="button" onClick={onClose} className="rounded-full p-1.5 text-gray-400 hover:bg-gray-100 hover:text-gray-600">
                        <X size={20} />
                    </button>
                </div>

                <form onSubmit={handleSave} className="px-8 py-6 space-y-5 max-h-[75vh] overflow-y-auto">
                    {error && (
                        <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-600">{error}</div>
                    )}

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="mb-1 block text-sm font-semibold text-gray-700">Product Name *</label>
                            <input value={name} onChange={e => setName(e.target.value)} required className={inputCls} />
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-semibold text-gray-700">Category *</label>
                            <select value={category} onChange={e => setCategory(e.target.value)} required className={inputCls}>
                                <option value="">Select a category</option>
                                {CATEGORY_OPTIONS.map(opt => (
                                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-semibold text-gray-700">Price ($) *</label>
                            <input type="number" min="0.01" step="0.01" value={price} onChange={e => setPrice(e.target.value)} required className={inputCls} />
                        </div>
                        <div>
                            <label className="mb-1 block text-sm font-semibold text-gray-700">Stock *</label>
                            <input type="number" min="0" value={stock} onChange={e => setStock(e.target.value)} required className={inputCls} />
                        </div>
                    </div>

                    <div>
                        <label className="mb-1 block text-sm font-semibold text-gray-700">Description</label>
                        <textarea value={description} onChange={e => setDescription(e.target.value)} rows={3} className={`${inputCls} resize-none`} />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold text-gray-700">Images</label>
                        <div className="flex flex-wrap gap-3">
                            {keptNames.map((obj, i) => (
                                <div key={obj} className="relative">
                                    <img src={imageUrl(obj)} alt="" className="h-20 w-20 rounded-xl object-cover" />
                                    <button type="button" onClick={() => removeKept(i)} className="absolute -right-1.5 -top-1.5 flex h-5 w-5 items-center justify-center rounded-full bg-white shadow text-gray-500 hover:text-red-500">
                                        <X size={11} />
                                    </button>
                                </div>
                            ))}
                            {newPreviews.map((src, i) => (
                                <div key={i} className="relative">
                                    <img src={src} alt="" className="h-20 w-20 rounded-xl object-cover" />
                                    <button type="button" onClick={() => removeNew(i)} className="absolute -right-1.5 -top-1.5 flex h-5 w-5 items-center justify-center rounded-full bg-white shadow text-gray-500 hover:text-red-500">
                                        <X size={11} />
                                    </button>
                                </div>
                            ))}
                            <button type="button" onClick={() => fileInputRef.current?.click()} className="flex h-20 w-20 flex-col items-center justify-center gap-1 rounded-xl border-2 border-dashed border-gray-300 text-gray-400 hover:border-[#54457F] hover:text-[#54457F] transition-colors">
                                <Upload size={18} />
                                <span className="text-xs">Add</span>
                            </button>
                        </div>
                        <input ref={fileInputRef} type="file" accept="image/*" multiple onChange={addImages} className="hidden" />
                    </div>

                    <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
                        <button type="button" onClick={onClose} className="rounded-full border border-gray-200 px-6 py-2.5 text-sm font-semibold text-gray-600 hover:bg-gray-50">
                            Cancel
                        </button>
                        <button type="submit" disabled={loading} className="rounded-full px-6 py-2.5 text-sm font-semibold text-white disabled:opacity-60" style={{ backgroundColor: "#54457F" }}>
                            {loading ? "Saving..." : "Save Changes"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function SellerProductsPage() {
    const { user, token } = useAuth();
    const authHeader = { Authorization: `Bearer ${token}` };

    const [products, setProducts]         = useState<Product[]>([]);
    const [loading, setLoading]           = useState(true);
    const [showModal, setShowModal]       = useState(false);
    const [editingProduct, setEditingProduct] = useState<Product | null>(null);

    const fetchProducts = async () => {
        setLoading(true);
        try {
            const res = await axiosInstance.get<Product[]>("/v1/products/search");
            setProducts(res.data.filter(p => p.sellerId === user?.userId));
        } catch {
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { fetchProducts(); }, []);

    const handleDelete = async (id: string) => {
        if (!window.confirm("Are you sure you want to delete this product?")) return;
        try {
            await axiosInstance.delete(`/v1/products/${id}`, { headers: authHeader });
            setProducts(prev => prev.filter(p => p.id !== id));
        } catch { /* silent */ }
    };

    return (
        <div>
            {showModal && (
                <AddProductModal
                    token={token}
                    onClose={() => setShowModal(false)}
                    onSaved={() => { setShowModal(false); fetchProducts(); }}
                />
            )}

            {editingProduct && (
                <EditProductModal
                    product={editingProduct}
                    token={token}
                    onClose={() => setEditingProduct(null)}
                    onSaved={() => { setEditingProduct(null); fetchProducts(); }}
                />
            )}

            {/* Header */}
            <div className="mb-8 flex items-center justify-between">
                <div>
                    <h1 className="text-4xl font-extrabold">Product Management</h1>
                    <p className="mt-2 max-w-xl text-gray-600">
                        Curate your products, manage stock, and adjust pricing.
                    </p>
                </div>

                <button
                    onClick={() => setShowModal(true)}
                    className="flex items-center gap-2 rounded-full px-7 py-4 text-sm font-semibold text-white"
                    style={{ backgroundColor: "#54457F" }}
                >
                    <Plus size={18} />
                    Add New Product
                </button>
            </div>

            {/* Product table */}
            <div className="rounded-3xl bg-[#F1F1F1] p-6 shadow-sm">
                <div className="grid grid-cols-[110px_1.5fr_100px_90px_150px_130px_100px] px-6 pb-4 text-xs font-bold uppercase tracking-wide text-gray-500">
                    <span>Image</span>
                    <span>Title & Details</span>
                    <span>Price</span>
                    <span>Stock</span>
                    <span>Category</span>
                    <span>Rating</span>
                    <span>Actions</span>
                </div>

                {loading ? (
                    <div className="py-12 text-center text-gray-400">Loading products...</div>
                ) : products.length === 0 ? (
                    <div className="py-12 text-center text-gray-400">
                        No products yet.{" "}
                        <button onClick={() => setShowModal(true)} className="font-semibold underline" style={{ color: "#54457F" }}>
                            Add your first product
                        </button>
                    </div>
                ) : (
                    <div className="space-y-4">
                        {products.map(product => (
                            <div
                                key={product.id}
                                className="grid grid-cols-[110px_1.5fr_100px_90px_150px_130px_100px] items-center rounded-2xl bg-white px-6 py-4"
                            >
                                <img
                                    src={imageUrl(product.imageUrls?.[0])}
                                    alt={product.name}
                                    className="h-16 w-16 rounded-lg object-cover bg-gray-100"
                                    onError={e => { (e.target as HTMLImageElement).style.display = "none"; }}
                                />

                                <div>
                                    <h3 className="font-bold">{product.name}</h3>
                                    <p className="line-clamp-1 text-sm text-gray-500">{product.description}</p>
                                </div>

                                <span className="font-semibold">${product.price.toFixed(2)}</span>

                                <span className="w-fit rounded-full bg-gray-100 px-3 py-1 text-sm">{product.stock}</span>

                                <span className="text-sm text-gray-700">
                                    {CATEGORY_LABEL[product.category] ?? product.category}
                                </span>

                                <span className="w-fit rounded-full bg-[#E9E7FF] px-4 py-2 text-xs font-bold text-[#54457F]">
                                    ★ {product.averageRating != null ? product.averageRating.toFixed(1) : "—"}
                                </span>

                                <div className="flex gap-4 text-gray-400">
                                    <button title="Edit" onClick={() => setEditingProduct(product)} className="hover:text-gray-700"><Edit size={18} /></button>
                                    <button title="Delete" onClick={() => handleDelete(product.id)} className="hover:text-red-500"><Trash2 size={18} /></button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default SellerProductsPage;
