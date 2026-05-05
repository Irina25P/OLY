import { useState, useEffect } from "react";
import { ChevronDown, ChevronUp } from "lucide-react";
import axiosInstance from "../../services/axios";
import { useAuth } from "../../context/AuthContext";

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";
const PLACEHOLDER = "https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=900&q=80";

function imageUrl(objectName: string | undefined): string {
    if (!objectName) return PLACEHOLDER;
    if (objectName.startsWith("http")) return objectName;
    return `${MINIO_BASE}/${objectName}`;
}

interface Order {
    id: string;
    clientId: string;
    sellerId: string;
    statusType: string;
    payment: string;
    county: string;
    city: string;
    street: string;
    fullPrice: number | null;
}

interface OrderProductRaw {
    id: string;
    orderId: string;
    productId: string;
    quantity: number;
    pricePerProduct: number | null;
}

interface OrderFullDto {
    order: Order;
    orderProducts: OrderProductRaw[];
}

interface ApiProduct {
    id: string;
    name: string;
    imageUrls: string[];
}

interface DetailProduct extends OrderProductRaw {
    productName: string;
    productImageUrl: string;
}

interface OrderDetail {
    products: DetailProduct[];
    loading: boolean;
}

const STATUS_OPTIONS = ["PLACED", "SHIPPED", "IN_DELIVERY", "CANCELLED", "RECEIVED"];

const STATUS_COLORS: Record<string, string> = {
    PLACED:      "bg-[#FEF3C7] text-[#B45309]",
    SHIPPED:     "bg-[#DBEAFE] text-[#1D4ED8]",
    IN_DELIVERY: "bg-[#E0E7FF] text-[#4338CA]",
    CANCELLED:   "bg-[#FEE2E2] text-[#DC2626]",
    RECEIVED:    "bg-[#D1FAE5] text-[#065F46]",
};

function SellerOrdersPage() {
    const { token } = useAuth();
    const authHeader = { Authorization: `Bearer ${token}` };

    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);
    const [updatingId, setUpdatingId] = useState<string | null>(null);
    const [expandedId, setExpandedId] = useState<string | null>(null);
    const [orderDetails, setOrderDetails] = useState<Record<string, OrderDetail>>({});

    useEffect(() => {
        axiosInstance
            .get<Order[]>("/v1/orders/seller", { headers: authHeader })
            .then((res) => setOrders([...res.data].reverse()))
            .catch(() => setOrders([]))
            .finally(() => setLoading(false));
    }, []);

    const handleStatusChange = async (orderId: string, newStatus: string) => {
        setUpdatingId(orderId);
        try {
            await axiosInstance.put(
                "/v1/orders",
                { id: orderId, status: newStatus },
                { headers: authHeader }
            );
            setOrders((prev) =>
                prev.map((o) => (o.id === orderId ? { ...o, statusType: newStatus } : o))
            );
        } catch {
            // silent
        } finally {
            setUpdatingId(null);
        }
    };

    const handleRowClick = async (orderId: string) => {
        if (expandedId === orderId) {
            setExpandedId(null);
            return;
        }
        setExpandedId(orderId);

        if (orderDetails[orderId]) return;

        setOrderDetails((prev) => ({ ...prev, [orderId]: { products: [], loading: true } }));

        try {
            const fullRes = await axiosInstance.get<OrderFullDto>(
                `/v1/orders/${orderId}`,
                { headers: authHeader }
            );
            const rawProducts = fullRes.data.orderProducts;

            const detailed = await Promise.all(
                rawProducts.map(async (op) => {
                    try {
                        const pRes = await axiosInstance.get<ApiProduct>(`/v1/products/${op.productId}`);
                        return {
                            ...op,
                            productName: pRes.data.name,
                            productImageUrl: pRes.data.imageUrls?.[0] ?? "",
                        };
                    } catch {
                        return {
                            ...op,
                            productName: `Product #${op.productId.slice(0, 8).toUpperCase()}`,
                            productImageUrl: "",
                        };
                    }
                })
            );

            setOrderDetails((prev) => ({ ...prev, [orderId]: { products: detailed, loading: false } }));
        } catch {
            setOrderDetails((prev) => ({ ...prev, [orderId]: { products: [], loading: false } }));
        }
    };

    return (
        <div>
            <h1 className="text-4xl font-extrabold">Orders</h1>
            <p className="mt-2 text-gray-600">Manage customer orders and update their status.</p>

            <div className="mt-8 rounded-3xl bg-white p-6 shadow-sm">
                {loading ? (
                    <div className="py-12 text-center text-gray-400">Loading orders...</div>
                ) : orders.length === 0 ? (
                    <div className="py-12 text-center text-gray-400">No orders yet.</div>
                ) : (
                    <div className="space-y-3">
                        {orders.map((order) => {
                            const isExpanded = expandedId === order.id;
                            const detail = orderDetails[order.id];

                            return (
                                <div key={order.id} className="overflow-hidden rounded-2xl border border-gray-100">
                                    {/* Order row */}
                                    <div
                                        onClick={() => handleRowClick(order.id)}
                                        className="flex cursor-pointer items-center gap-4 bg-[#F8F8F8] px-5 py-4 transition hover:bg-[#F1F1F1]"
                                    >
                                        {/* ID + address */}
                                        <div className="min-w-0 flex-1">
                                            <p className="text-sm font-bold">
                                                #{order.id.slice(0, 8).toUpperCase()}
                                            </p>
                                            <p className="mt-0.5 truncate text-xs text-gray-500">
                                                {order.city}, {order.county} — {order.street}
                                            </p>
                                            <p className="mt-0.5 text-xs uppercase text-gray-400">
                                                {order.payment}
                                            </p>
                                        </div>

                                        {/* Price */}
                                        <p className="w-24 shrink-0 text-sm font-bold">
                                            {order.fullPrice != null ? `$${order.fullPrice.toFixed(2)}` : "—"}
                                        </p>

                                        {/* Status badge */}
                                        <span
                                            className={`w-28 shrink-0 rounded-full px-3 py-1 text-center text-xs font-bold ${STATUS_COLORS[order.statusType] ?? "bg-gray-100 text-gray-600"}`}
                                        >
                                            {order.statusType}
                                        </span>

                                        {/* Status select */}
                                        <div className="w-44 shrink-0" onClick={(e) => e.stopPropagation()}>
                                            <select
                                                value={order.statusType}
                                                disabled={updatingId === order.id}
                                                onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                                className="w-full rounded-xl border border-gray-200 bg-white px-3 py-2 text-sm disabled:opacity-50"
                                            >
                                                {STATUS_OPTIONS.map((s) => (
                                                    <option key={s} value={s}>{s}</option>
                                                ))}
                                            </select>
                                        </div>

                                        {/* Expand chevron */}
                                        <div className="shrink-0 text-gray-400">
                                            {isExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
                                        </div>
                                    </div>

                                    {/* Expanded product panel */}
                                    {isExpanded && (
                                        <div className="border-t border-gray-100 bg-white px-5 py-4">
                                            {detail?.loading ? (
                                                <p className="py-4 text-center text-sm text-gray-400">
                                                    Loading products...
                                                </p>
                                            ) : !detail || detail.products.length === 0 ? (
                                                <p className="py-4 text-center text-sm text-gray-400">
                                                    No products found.
                                                </p>
                                            ) : (
                                                <div className="space-y-3">
                                                    <p className="text-xs font-bold uppercase tracking-wide text-gray-400">
                                                        Order Contents
                                                    </p>
                                                    {detail.products.map((dp) => (
                                                        <div
                                                            key={dp.id}
                                                            className="flex items-center gap-4 rounded-xl bg-[#F8F8F8] px-4 py-3"
                                                        >
                                                            <div className="h-14 w-14 shrink-0 overflow-hidden rounded-lg bg-[#F1F1F1]">
                                                                <img
                                                                    src={imageUrl(dp.productImageUrl)}
                                                                    alt={dp.productName}
                                                                    className="h-full w-full object-cover"
                                                                    onError={(e) => {
                                                                        (e.target as HTMLImageElement).src = PLACEHOLDER;
                                                                    }}
                                                                />
                                                            </div>
                                                            <div className="min-w-0 flex-1">
                                                                <p className="truncate text-sm font-semibold text-gray-800">
                                                                    {dp.productName}
                                                                </p>
                                                                <p className="text-xs text-gray-500">
                                                                    Qty: {dp.quantity}
                                                                </p>
                                                            </div>
                                                            <div className="shrink-0 text-right">
                                                                {dp.pricePerProduct != null && (
                                                                    <>
                                                                        <p className="text-sm font-bold text-gray-800">
                                                                            ${(dp.pricePerProduct * dp.quantity).toFixed(2)}
                                                                        </p>
                                                                        <p className="text-xs text-gray-400">
                                                                            ${dp.pricePerProduct.toFixed(2)} each
                                                                        </p>
                                                                    </>
                                                                )}
                                                            </div>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
}

export default SellerOrdersPage;
