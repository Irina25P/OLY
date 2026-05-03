import { useState, useEffect } from "react";
import axiosInstance from "../../services/axios";
import { useAuth } from "../../context/AuthContext";

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

const STATUS_OPTIONS = ["PLACED", "SHIPPED", "IN_DELIVERY", "CANCELLED", "RECEIVED"];

const STATUS_COLORS: Record<string, string> = {
    PLACED: "bg-[#FEF3C7] text-[#B45309]",
    SHIPPED: "bg-[#DBEAFE] text-[#1D4ED8]",
    IN_DELIVERY: "bg-[#E0E7FF] text-[#4338CA]",
    CANCELLED: "bg-[#FEE2E2] text-[#DC2626]",
    RECEIVED: "bg-[#D1FAE5] text-[#065F46]",
};

function SellerOrdersPage() {
    const { token } = useAuth();
    const authHeader = { Authorization: `Bearer ${token}` };

    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);
    const [updatingId, setUpdatingId] = useState<string | null>(null);

    useEffect(() => {
        axiosInstance
            .get<Order[]>("/v1/orders/seller", { headers: authHeader })
            .then((res) => setOrders(res.data))
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
            // silent — status reverts visually on next render
        } finally {
            setUpdatingId(null);
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
                    <div className="space-y-4">
                        {orders.map((order) => (
                            <div
                                key={order.id}
                                className="grid grid-cols-[1fr_160px_160px_180px] items-center gap-4 rounded-2xl bg-[#F8F8F8] px-5 py-4"
                            >
                                <div>
                                    <p className="font-bold text-sm">#{order.id.slice(0, 8).toUpperCase()}</p>
                                    <p className="mt-0.5 text-xs text-gray-500">
                                        {order.city}, {order.county} — {order.street}
                                    </p>
                                    <p className="mt-0.5 text-xs text-gray-400 uppercase">{order.payment}</p>
                                </div>

                                <p className="font-bold">
                                    {order.fullPrice != null ? `$${order.fullPrice.toFixed(2)}` : "—"}
                                </p>

                                <span
                                    className={`w-fit rounded-full px-3 py-1 text-xs font-bold ${STATUS_COLORS[order.statusType] ?? "bg-gray-100 text-gray-600"}`}
                                >
                                    {order.statusType}
                                </span>

                                <select
                                    value={order.statusType}
                                    disabled={updatingId === order.id}
                                    onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                    className="h-10 rounded-xl border border-gray-200 px-3 text-sm disabled:opacity-50"
                                >
                                    {STATUS_OPTIONS.map((s) => (
                                        <option key={s} value={s}>{s}</option>
                                    ))}
                                </select>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default SellerOrdersPage;
