import React, { useEffect, useState } from 'react';
import { Users, Store, DollarSign, Package, Loader2, TrendingUp } from 'lucide-react';
import AdminService, {
    type Seller, type RevenueDataPoint, type SellerRevenueDataPoint, type AdminSummary
} from "../../services/admin/AdminService";

const THEME = {
    primary: "#54457F",
    secondary: "#9BA2FF",
    accent: "#E85D75",
    background: "#F9FAFB",
};

const PlatformOverview: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const [timeRange, setTimeRange] = useState(30);
    const [summary, setSummary] = useState<AdminSummary | null>(null);
    const [sellers, setSellers] = useState<Seller[]>([]);
    const [revenueHistory, setRevenueHistory] = useState<RevenueDataPoint[]>([]);
    const [sellerRevenue, setSellerRevenue] = useState<SellerRevenueDataPoint[]>([]);

    useEffect(() => {
        const loadData = async () => {
            setLoading(true);
            try {
                const [sSum, sList, sRev, sSellerRev] = await Promise.all([
                    AdminService.getSummary(),
                    AdminService.getAllSellers(),
                    AdminService.getRevenueAnalytics(timeRange),
                    AdminService.getRevenuePerSeller(timeRange)
                ]);
                setSummary(sSum);
                setSellers(sList);
                setRevenueHistory(sRev);
                setSellerRevenue(sSellerRev);
                console.log(sellers);
            } catch (error) {
                console.error("Dashboard error:", error);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, [timeRange]);

    if (loading || !summary) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#F9FAFB]">
                <Loader2 className="w-10 h-10 animate-spin text-[#54457F]" />
            </div>
        );
    }

    const maxRevenue = Math.max(...revenueHistory.map(d => d.amount), 1);

    return (
        <div className="min-h-screen font-sans p-10" style={{ backgroundColor: THEME.background }}>
            <header className="flex justify-between items-center mb-12">
                <div>
                    <h1 className="text-5xl font-extrabold text-slate-950 mb-3 tracking-tight">
                        Platform Admin
                    </h1>
                    <p className="text-xl text-slate-600 font-medium">
                        Monitoring {summary.totalSellers} sellers and {summary.totalClients} clients.
                    </p>
                </div>

                <div className="flex gap-4">
                    <select
                        value={timeRange}
                        onChange={(e) => setTimeRange(Number(e.target.value))}
                        className="bg-white border-none shadow-sm rounded-xl px-4 py-2 font-bold text-[#54457F] outline-none cursor-pointer"
                    >
                        <option value={7}>Last 7 Days</option>
                        <option value={30}>Last 30 Days</option>
                    </select>
                </div>
            </header>

            <section className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-12">
                <div className="bg-white p-6 rounded-3xl shadow-sm border-b-4 border-[#54457F]">
                    <DollarSign className="text-[#54457F] mb-4" size={24} />
                    <p className="text-sm font-bold text-slate-400 uppercase">Total Revenue</p>
                    <h3 className="text-3xl font-black">
                        {summary.totalRevenue.toLocaleString()} €
                    </h3>
                </div>

                <div className="bg-white p-6 rounded-3xl shadow-sm border-b-4 border-[#9BA2FF]">
                    <Package className="text-[#9BA2FF] mb-4" size={24} />
                    <p className="text-sm font-bold text-slate-400 uppercase">Finished Orders</p>
                    <h3 className="text-3xl font-black">{summary.finishedOrders}</h3>
                </div>

                <div className="bg-white p-6 rounded-3xl shadow-sm border-b-4 border-[#E85D75]">
                    <Users className="text-[#E85D75] mb-4" size={24} />
                    <p className="text-sm font-bold text-slate-400 uppercase">Total Clients</p>
                    <h3 className="text-3xl font-black">{summary.totalClients}</h3>
                </div>

                <div className="bg-white p-6 rounded-3xl shadow-sm border-b-4 border-slate-900">
                    <Store className="text-slate-900 mb-4" size={24} />
                    <p className="text-sm font-bold text-slate-400 uppercase">Total Sellers</p>
                    <h3 className="text-3xl font-black">{summary.totalSellers}</h3>
                </div>
            </section>

            <div className={`grid grid-cols-1 ${timeRange <= 7 ? "lg:grid-cols-3" : "lg:grid-cols-1"} gap-8`}>
                <div className={`${timeRange <= 7 ? "lg:col-span-2" : "w-full"} bg-white p-8 rounded-[40px] shadow-sm`}>
                    <div className="flex justify-between items-center mb-10">
                        <h2 className="text-2xl font-black flex items-center gap-2">
                            <TrendingUp className="text-emerald-500" /> Revenue Flow
                        </h2>

                        <span className="text-xs font-bold text-slate-400">
                        {timeRange} Days View
                    </span>
                    </div>

                    <div className="flex items-end justify-between h-64 gap-2 px-2">
                        {revenueHistory.map((day, i) => {
                            const hasRevenue = day.amount > 0;
                            const displayHeight = hasRevenue
                                ? Math.max((day.amount / maxRevenue) * 100, 10)
                                : 0;

                            return (
                                <div key={i} className="flex-1 flex flex-col items-center gap-3 group relative">
                                    {hasRevenue && (
                                        <div className="absolute -top-10 scale-0 group-hover:scale-100 transition-transform bg-slate-900 text-white text-[10px] py-1 px-2 rounded-lg z-10 whitespace-nowrap">
                                            {day.amount.toLocaleString()} €
                                        </div>
                                    )}

                                    <div
                                        className={`w-full transition-all duration-500 rounded-t-xl 
                                            ${hasRevenue
                                            ? 'bg-gradient-to-t from-[#54457F] to-[#9BA2FF] shadow-[0_-4px_12px_rgba(155,162,255,0.3)]'
                                            : 'bg-slate-100'
                                        }`}
                                        style={{
                                            height: hasRevenue ? `${displayHeight}%` : '4px',
                                            minHeight: hasRevenue ? '20px' : '4px'
                                        }}
                                    />

                                    {(timeRange <= 7 || i % 5 === 0) && (
                                        <span className="text-[9px] font-bold text-slate-400 uppercase">
                                        {day.date.split("-").slice(2)}/{day.date.split("-").slice(1, 2)}
                                    </span>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                </div>

                <div className="bg-white p-8 rounded-[40px] shadow-sm">
                    <h2 className="text-2xl font-black mb-8">Top Sellers</h2>

                    <div className="space-y-6">
                        {sellerRevenue.slice(0, 6).map((item, i) => (
                            <div key={i} className="flex items-center justify-between">
                                <div className="flex items-center gap-3">
                                    <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-[10px] font-bold">
                                        #{i + 1}
                                    </div>

                                    <span className="font-bold text-slate-700 truncate max-w-[120px]">
                                    {item.sellerName}
                                </span>
                                </div>

                                <span className="font-black text-[#54457F]">
                                {item.revenue.toLocaleString()} €
                            </span>
                            </div>
                        ))}

                        {sellerRevenue.length === 0 && (
                            <p className="text-slate-400 italic text-center">
                                No sales in this period
                            </p>
                        )}
                    </div>
                </div>
            </div>

            <div className="mt-10 bg-white p-8 rounded-[40px] shadow-sm overflow-hidden">
                <h2 className="text-2xl font-black mb-8">Sellers Directory</h2>

                <table className="w-full">
                    <thead>
                    <tr className="text-left border-b border-slate-50">
                        <th className="pb-6 text-xs font-black text-slate-400 uppercase">
                            Seller
                        </th>
                        <th className="pb-6 text-xs font-black text-slate-400 uppercase text-center">
                            Sales
                        </th>
                        <th className="pb-6 text-xs font-black text-slate-400 uppercase text-center">
                            Rating
                        </th>
                        <th className="pb-6 text-xs font-black text-slate-400 uppercase text-right">
                            Lifetime Revenue
                        </th>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-slate-50">
                    {sellers.map((s) => (
                        <tr key={s.id} className="group hover:bg-slate-50/50 transition-colors">
                            <td className="py-6">
                                <div className="flex items-center gap-4">
                                    <img
                                        src={s.imageUrl || "/placeholder-user.png"}
                                        alt=""
                                        className="w-12 h-12 rounded-2xl object-cover shadow-sm"
                                    />

                                    <div>
                                        <p className="font-black text-slate-900">{s.name}</p>
                                        <p className="text-xs text-slate-400">{s.email}</p>
                                    </div>
                                </div>
                            </td>

                            <td className="py-6 text-center font-bold text-slate-600">
                                {s.totalSales}
                            </td>

                            <td className="py-6 text-center">
                                <span className="bg-amber-50 text-amber-600 px-3 py-1 rounded-full text-xs font-black">
                                    ★ {s.rating.toFixed(1)}
                                </span>
                            </td>

                            <td className="py-6 text-right font-black text-[#54457F]">
                                {s.totalRevenue.toLocaleString()} €
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default PlatformOverview;