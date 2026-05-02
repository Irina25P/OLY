import { useMemo, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../../services/axios";
import { useAuth } from "../../context/AuthContext";

interface Product {
    id: string;
    name: string;
    price: number;
    stock: number;
    sellerId: string;
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

const STATUS_COLORS: Record<string, string> = {
    PLACED:      "bg-[#FEF3C7] text-[#B45309]",
    SHIPPED:     "bg-[#DBEAFE] text-[#1D4ED8]",
    IN_DELIVERY: "bg-[#E0E7FF] text-[#4338CA]",
    CANCELLED:   "bg-[#FEE2E2] text-[#DC2626]",
    RECEIVED:    "bg-[#D1FAE5] text-[#065F46]",
};

type Period = "week" | "month" | "months";

function getChartLabels(period: Period) {
    const today = new Date();

    if (period === "week") {
        const dayNames = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];
        return Array.from({ length: 7 }, (_, i) => {
            const d = new Date(today);
            d.setDate(today.getDate() - (6 - i));
            return dayNames[d.getDay()];
        });
    }

    if (period === "month") {
        const weeks: string[] = [];
        const d = new Date(today.getFullYear(), today.getMonth(), 1);
        let n = 1;
        while (d.getMonth() === today.getMonth()) {
            weeks.push(`WEEK ${n}`);
            d.setDate(d.getDate() + 7);
            n++;
        }
        return weeks;
    }

    const monthNames = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];
    return Array.from({ length: 3 }, (_, i) => {
        const d = new Date(today);
        d.setMonth(today.getMonth() - (2 - i));
        return monthNames[d.getMonth()];
    });
}

function SellerOverviewPage() {
    const { user, token } = useAuth();
    const navigate = useNavigate();
    const authHeader = { Authorization: `Bearer ${token}` };

    const [products, setProducts] = useState<Product[]>([]);
    const [orders, setOrders]     = useState<Order[]>([]);
    const [loading, setLoading]   = useState(true);
    const [period, setPeriod]     = useState<Period>("week");

    const labels = useMemo(() => getChartLabels(period), [period]);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const [prodRes, orderRes] = await Promise.all([
                    axiosInstance.get<Product[]>("/v1/products/search"),
                    axiosInstance.get<Order[]>("/v1/orders/seller", { headers: authHeader }),
                ]);
                setProducts(prodRes.data.filter((p) => p.sellerId === user?.userId));
                setOrders(orderRes.data);
            } catch {
                // leave empty
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const totalRevenue   = orders.reduce((sum, o) => sum + (o.fullPrice ?? 0), 0);
    const pendingOrders  = orders.filter((o) => o.statusType === "PLACED").length;
    const recentOrders   = orders.slice(0, 5);

    const maxBarOrders   = labels.length;
    const barHeights     = labels.map((_, i) => 60 + (i / Math.max(maxBarOrders - 1, 1)) * 260);

    return (
        <div>
            {/* HEADER */}
            <div className="mb-10 flex items-start justify-between">
                <div>
                    <h1 className="text-4xl font-extrabold text-[#000000]">Store Overview</h1>
                    <p className="mt-2 text-base text-gray-600">
                        Here's your performance snapshot, curated for you.
                    </p>
                </div>

                <span className="rounded-full bg-white px-5 py-3 text-sm font-medium text-gray-600 shadow-sm">
                    <span className="mr-2 inline-block h-2 w-2 rounded-full bg-[#9BA2FF]" />
                    Live Data Sync
                </span>
            </div>

            {/* TOP SECTION */}
            <div className="grid grid-cols-[280px_1fr] gap-8">
                {/* STATS */}
                <div className="space-y-6">
                    <div className="rounded-3xl bg-white p-7 shadow-sm">
                        <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Total Revenue</p>
                        <h2 className="mt-8 text-4xl font-extrabold text-black">
                            {loading ? "—" : `$${totalRevenue.toFixed(2)}`}
                        </h2>
                        <p className="mt-4 text-sm text-gray-600">Across all orders</p>
                    </div>

                    <div className="rounded-3xl bg-white p-7 shadow-sm">
                        <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Total Orders</p>
                        <h2 className="mt-8 text-4xl font-extrabold text-black">
                            {loading ? "—" : orders.length}
                        </h2>
                        <p className="mt-4 text-sm text-gray-600">
                            {pendingOrders > 0 ? `${pendingOrders} pending now` : "No pending orders"}
                        </p>
                    </div>

                    <div className="rounded-3xl bg-white p-7 shadow-sm">
                        <p className="text-xs font-bold uppercase tracking-wide text-gray-500">Active Listings</p>
                        <h2 className="mt-8 text-4xl font-extrabold text-black">
                            {loading ? "—" : products.length}
                        </h2>
                        <p className="mt-4 text-sm text-gray-600">Products in your store</p>
                    </div>
                </div>

                {/* CHART */}
                <div className="rounded-3xl bg-[#F1F1F1] p-8 shadow-sm">
                    <div className="flex items-start justify-between">
                        <div>
                            <h2 className="text-2xl font-bold">Sales Rhythm</h2>
                            <p className="mt-1 text-sm text-gray-600">
                                Visualizing your sales performance by selected period.
                            </p>
                        </div>

                        <select
                            value={period}
                            onChange={(e) => setPeriod(e.target.value as Period)}
                            className="!h-10 !w-fit !rounded-full !border !border-gray-200 !bg-white !px-4 !py-0 !text-sm !font-semibold !shadow-sm focus:!shadow-none"
                        >
                            <option value="week">This Week</option>
                            <option value="month">This Month</option>
                            <option value="months">Last 3 Months</option>
                        </select>
                    </div>

                    <div className="mt-10 flex h-[360px] items-end justify-between border-t border-gray-200 pt-8">
                        {labels.map((label, i) => (
                            <div key={label} className="flex flex-col items-center gap-4">
                                <div
                                    className="w-12 rounded-t-2xl"
                                    style={{ height: `${barHeights[i]}px`, backgroundColor: "#9BA2FF" }}
                                />
                                <span className="text-xs font-semibold text-gray-500">{label}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* RECENT ACTIVITY */}
            <div className="mt-10 rounded-3xl bg-white p-8 shadow-sm">
                <div className="mb-8 flex items-center justify-between">
                    <div>
                        <h2 className="text-2xl font-bold">Recent Orders</h2>
                        <p className="mt-1 text-sm text-gray-600">
                            Your latest transactions.
                        </p>
                    </div>

                    <button
                        onClick={() => navigate("/seller/orders")}
                        className="text-sm font-semibold"
                        style={{ color: "#54457F" }}
                    >
                        View All Orders →
                    </button>
                </div>

                {loading ? (
                    <div className="py-8 text-center text-gray-400">Loading orders...</div>
                ) : recentOrders.length === 0 ? (
                    <div className="py-8 text-center text-gray-400">No orders yet.</div>
                ) : (
                    <div className="space-y-4">
                        {recentOrders.map((order) => (
                            <div
                                key={order.id}
                                className="grid grid-cols-[1fr_140px_160px_150px] items-center gap-4 rounded-2xl bg-[#F8F8F8] px-5 py-4 text-sm"
                            >
                                <div>
                                    <p className="font-bold">#{order.id.slice(0, 8).toUpperCase()}</p>
                                    <p className="mt-0.5 text-xs text-gray-500">
                                        {order.city}, {order.county}
                                    </p>
                                </div>

                                <span className="text-xs font-medium uppercase text-gray-500">
                                    {order.payment}
                                </span>

                                <span className="font-bold">
                                    {order.fullPrice != null ? `$${order.fullPrice.toFixed(2)}` : "—"}
                                </span>

                                <span
                                    className={`w-fit rounded-full px-4 py-1.5 text-xs font-bold ${STATUS_COLORS[order.statusType] ?? "bg-gray-100 text-gray-600"}`}
                                >
                                    {order.statusType}
                                </span>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default SellerOverviewPage;
