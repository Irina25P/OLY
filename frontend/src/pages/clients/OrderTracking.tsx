import React, { useEffect, useState } from "react";
import { OrderService } from "../../services/order/OrderService.tsx";
import type { OrderDto, OrderFullDto, OrderStatus } from "../../models/order//OrderModels.tsx";
import type { Product } from "../../models/Product";
import {
    MapPin,
    CreditCard,
    Search,
    Check,
    Inbox,
    Truck,
    Package,
    Home,
    ArrowRight
} from "lucide-react";

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";

const STATUS_COLORS: Record<OrderStatus, string> = {
    PLACED: "bg-[#FEF3C7] text-[#B45309]",
    RECEIVED: "bg-[#D1FAE5] text-[#065F46]",
    SHIPPED: "bg-indigo-50 text-[#54457F]",
    IN_DELIVERY: "bg-[#E0E7FF] text-[#4338CA]",
    DELIVERED: "bg-orange-50 text-orange-700",
    CANCELLED: "bg-red-50 text-[#E85D75]",
};

const ORDER_STEPS: OrderStatus[] = [
    "PLACED",
    "RECEIVED",
    "SHIPPED",
    "IN_DELIVERY",
    "DELIVERED",
];

const STEP_LABELS: Record<string, string> = {
    PLACED: "Placed",
    RECEIVED: "Received",
    SHIPPED: "Shipped",
    IN_DELIVERY: "In Delivery",
    DELIVERED: "Delivered",
};

const STEP_ICONS: Record<string, React.ReactNode> = {
    PLACED: <Check size={18} />,
    RECEIVED: <Inbox size={18} />,
    SHIPPED: <Truck size={18} />,
    IN_DELIVERY: <Package size={18} />,
    DELIVERED: <Home size={18} />,
};

const getShortId = (id: string) => `#ORD-${id.slice(0, 6).toUpperCase()}`;

const getStepIndex = (status: OrderStatus) => {
    if (status === "CANCELLED") return 0;
    return ORDER_STEPS.indexOf(status);
};

const getProgressWidth = (status: OrderStatus) => {
    if (status === "CANCELLED") return "0%";
    const index = getStepIndex(status);
    if (index <= 0) return "0%";
    return `${(index / (ORDER_STEPS.length - 1)) * 100}%`;
};

const isStepCompleted = (currentStatus: OrderStatus, step: OrderStatus) => {
    if (currentStatus === "CANCELLED") return false;
    const currentIndex = getStepIndex(currentStatus);
    const stepIndex = getStepIndex(step);
    return stepIndex <= currentIndex;
};

const OrderTracking: React.FC = () => {
    const [orders, setOrders] = useState<OrderDto[]>([]);
    const [selectedOrder, setSelectedOrder] = useState<OrderFullDto | null>(null);
    const [productsCache, setProductsCache] = useState<Record<string, Product>>({});
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");

    const handleSelect = async (id: string) => {
        try {
            const { data } = await OrderService.getOrderDetails(id);
            setSelectedOrder(data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        const loadData = async () => {
            try {
                const [prodRes, orderRes] = await Promise.all([
                    OrderService.getAllProducts(),
                    OrderService.getClientOrders(),
                ]);

                const cache = prodRes.data.reduce(
                    (acc: Record<string, Product>, product: Product) => {
                        acc[product.id] = product;
                        return acc;
                    },
                    {}
                );

                setProductsCache(cache);
                setOrders(orderRes.data);

                if (orderRes.data.length > 0) {
                    await handleSelect(orderRes.data[0].id);
                }
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, []);

    const filteredOrders = orders.filter((order) =>
        order.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
        order.statusType.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) {
        return (
            <div className="flex min-h-screen items-center justify-center text-gray-400">
                Loading orders...
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#F8FAFC] font-sans p-6 md:p-16 text-black">
            <header className="flex flex-col md:flex-row justify-between items-start mb-16 gap-6">
                <div>
                    <h1 className="text-5xl font-black text-black mb-3 tracking-tight">
                        Order Tracking
                    </h1>
                    <p className="text-gray-500 text-lg font-medium">
                        Manage and track your curated shipments.
                    </p>
                </div>

                <div className="bg-white rounded-[18px] flex items-center px-5 py-3 shadow-sm w-72">
                    <Search className="text-gray-400" size={18} />
                    <input
                        type="text"
                        placeholder="Search orders..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="ml-3 bg-transparent outline-none text-sm w-full placeholder:text-gray-400 font-medium"
                    />
                </div>
            </header>

            <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
                <div className="lg:col-span-4">
                    <h2 className="text-[11px] space-y-6 font-black uppercase tracking-[0.2em] mb-12 text-black opacity-80">
                        My Orders
                    </h2>

                    <div className="space-y-6">
                        {filteredOrders.map((order) => (
                            <div
                                key={order.id}
                                onClick={() => handleSelect(order.id)}
                                className={`p-6 rounded-[24px] transition-all relative cursor-pointer ${
                                    selectedOrder?.order.id === order.id
                                        ? "bg-white shadow-xl shadow-gray-200/50 border-l-[6px] border-[#9BA2FF]"
                                        : "bg-gray-100 border-l-[6px] border-transparent hover:bg-gray-200"
                                }`}
                            >
                                <div className="flex justify-between items-center mb-2">
                                    <p className="text-[11px] font-bold text-gray-400 uppercase tracking-tight">
                                        ORDER
                                    </p>

                                    <span
                                        className={`px-3 py-1 text-[10px] font-bold rounded-full flex items-center gap-1 ${STATUS_COLORS[order.statusType]}`}
                                    >
                                        {order.statusType}
                                    </span>
                                </div>

                                <p className="text-lg font-black text-black mb-6">
                                    {getShortId(order.id)}
                                </p>

                                <div className="flex justify-between items-center border-t border-gray-100 pt-5">
                                    <p className="text-2xl font-black text-black">
                                        ${order.fullPrice?.toFixed(2)}
                                    </p>

                                    <button className="text-[#54457F] font-bold text-sm flex items-center gap-1 hover:underline">
                                        Details <ArrowRight size={14} />
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="lg:col-span-8 bg-white rounded-[48px] shadow-2xl shadow-gray-200/40 p-10 md:p-14 border border-gray-50">
                    {selectedOrder ? (
                        <>
                            <div className="mb-14">
                                <div className="flex items-start justify-between gap-6">
                                    <div>
                                        <h2 className="text-[42px] font-black text-black leading-tight mb-3">
                                            Order {getShortId(selectedOrder.order.id)}
                                        </h2>

                                        <p className="text-gray-400 text-sm font-bold">
                                            To: {selectedOrder.order.street},{" "}
                                            {selectedOrder.order.city}
                                        </p>
                                    </div>

                                    <span
                                        className={`px-4 py-2 text-xs font-black rounded-full ${STATUS_COLORS[selectedOrder.order.statusType]}`}
                                    >
                                        {selectedOrder.order.statusType}
                                    </span>
                                </div>
                            </div>

                            <div className="relative mb-24 max-w-4xl mx-auto pt-10">
                                <div className="absolute top-[58px] left-0 w-full h-[6px] bg-gray-100 rounded-full">
                                    <div
                                        className="h-full bg-[#9BA2FF] rounded-full transition-all duration-500"
                                        style={{
                                            width: getProgressWidth(selectedOrder.order.statusType),
                                        }}
                                    ></div>
                                </div>

                                <div className="relative flex justify-between">
                                    {ORDER_STEPS.map((step) => {
                                        const completed = isStepCompleted(
                                            selectedOrder.order.statusType,
                                            step
                                        );

                                        return (
                                            <div
                                                key={step}
                                                className="flex flex-col items-center relative group"
                                            >
                                                <div
                                                    className={`w-12 h-12 rounded-full flex items-center justify-center border-[6px] border-white shadow-sm relative z-10 ${
                                                        completed
                                                            ? "bg-[#9BA2FF]"
                                                            : "bg-gray-100"
                                                    }`}
                                                >
                                                    <span
                                                        className={`${
                                                            completed
                                                                ? "text-white"
                                                                : "text-gray-400"
                                                        }`}
                                                    >
                                                        {STEP_ICONS[step]}
                                                    </span>
                                                </div>

                                                <p
                                                    className={`absolute -bottom-9 whitespace-nowrap text-[10px] font-black uppercase tracking-widest ${
                                                        completed
                                                            ? "text-[#9BA2FF]"
                                                            : "text-gray-400"
                                                    }`}
                                                >
                                                    {STEP_LABELS[step]}
                                                </p>
                                            </div>
                                        );
                                    })}
                                </div>

                                {selectedOrder.order.statusType === "CANCELLED" && (
                                    <p className="text-center mt-16 text-[#E85D75] font-black text-sm uppercase tracking-widest">
                                        Order cancelled
                                    </p>
                                )}
                            </div>

                            <div className="mb-14">
                                <div className="flex items-center gap-2 mb-8">
                                    <h3 className="text-[11px] font-black uppercase tracking-[0.2em] text-black">
                                        Curated Items
                                    </h3>
                                    <div className="w-1.5 h-1.5 bg-[#E85D75] rounded-full mt-0.5"></div>
                                </div>

                                <div className="space-y-4">
                                    {selectedOrder.orderProducts.map((item) => {
                                        const product = productsCache[item.productId];

                                        return (
                                            <div
                                                key={item.id}
                                                className="flex items-center justify-between bg-gray-50/70 p-5 rounded-[24px] hover:bg-gray-100/50 transition-colors"
                                            >
                                                <div className="flex items-center gap-5">
                                                    <div className="w-20 h-20 bg-white rounded-2xl flex items-center justify-center shadow-sm p-2">
                                                        <img
                                                            src={
                                                                product?.imageUrls?.[0]
                                                                    ? `${MINIO_BASE}/${product.imageUrls[0]}`
                                                                    : "https://via.placeholder.com/100"
                                                            }
                                                            alt={product?.title || "Product"}
                                                            className="w-full h-full rounded-xl object-cover"
                                                        />
                                                    </div>

                                                    <div>
                                                        <p className="font-extrabold text-[17px] text-black mb-1">
                                                            {product?.title || "Product"}
                                                        </p>

                                                        <p className="text-xs text-gray-400 font-bold uppercase tracking-wide">
                                                            Qty: {item.quantity}
                                                        </p>
                                                    </div>
                                                </div>

                                                <p className="text-[20px] font-black text-black tracking-tight">
                                                    ${item.pricePerProduct.toFixed(2)}
                                                </p>
                                            </div>
                                        );
                                    })}
                                </div>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div className="bg-gray-50/70 p-8 rounded-[32px] border border-gray-100/50">
                                    <div className="flex items-center gap-3 mb-6 opacity-60">
                                        <MapPin size={16} />
                                        <h4 className="text-[10px] font-black uppercase tracking-[0.2em]">
                                            Shipping Address
                                        </h4>
                                    </div>

                                    <p className="text-[15px] font-bold text-black leading-relaxed">
                                        {selectedOrder.order.county}
                                        <br />
                                        {selectedOrder.order.city}
                                        <br />
                                        {selectedOrder.order.street}
                                    </p>
                                </div>

                                <div className="bg-gray-50/70 p-8 rounded-[32px] border border-gray-100/50 flex flex-col justify-between">
                                    <div className="flex items-center gap-3 mb-6 opacity-60">
                                        <CreditCard size={16} />
                                        <h4 className="text-[10px] font-black uppercase tracking-[0.2em]">
                                            Payment Method
                                        </h4>
                                    </div>

                                    <div className="flex items-center gap-4 bg-white p-3 rounded-xl border border-gray-100 w-fit">
                                        <div className="bg-[#1A1F71] text-white text-[8px] px-2 py-1 rounded font-black italic tracking-tighter">
                                            PAY
                                        </div>

                                        <p className="text-[13px] font-extrabold text-black">
                                            {selectedOrder.order.payment.replace(/_/g, " ")}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </>
                    ) : (
                        <div className="h-full flex items-center justify-center text-gray-300 font-bold italic">
                            Select an order from the list
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default OrderTracking;