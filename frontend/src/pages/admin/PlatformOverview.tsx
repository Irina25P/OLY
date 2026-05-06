import React, { useEffect, useState } from 'react';
import {
    Bell,
    Users,
    Store,
    DollarSign,
    ArrowRight,
    type LucideIcon,
    Loader2
} from 'lucide-react';
import { AdminService, type Seller, type RevenueDataPoint, type CategoryDistribution } from "../../services/admin/AdminService";

// === CONFIGURARE DESIGN ===
const THEME = {
    primary: "#54457F",
    secondary: "#9BA2FF",
    accent: "#E85D75",
    background: "#F9FAFB",
};

const STATUS_STYLES: Record<string, string> = {
    'Pending': 'bg-[#FEF3C7] text-[#B45309]',
    'Completed': 'bg-[#DCFCE7] text-[#15803d]',
    'Action Req.': 'bg-[#FEE2E2] text-[#E85D75]',
};

// === COMPONENTE MICI ===
const StatCard: React.FC<{ label: string; value: string; change: string; Icon: LucideIcon; color: string }> =
    ({ label, value, change, Icon, color }) => {
        const isPositive = change.startsWith('^');
        return (
            <div className="bg-white p-6 rounded-2xl shadow-sm border-l-4" style={{ borderColor: color }}>
                <div className="flex justify-between items-start mb-2">
                    <span className="text-sm font-bold uppercase tracking-wide text-slate-500">{label}</span>
                    <div className="p-3 rounded-full" style={{ backgroundColor: `${color}20` }}>
                        <Icon className="w-6 h-6" style={{ color: color }} />
                    </div>
                </div>
                <p className="text-4xl font-extrabold text-slate-950 mb-3">{value}</p>
                <div className="flex items-center gap-1.5">
                <span className={`text-sm font-semibold p-1 px-2 rounded-full flex items-center gap-1 ${isPositive ? 'text-emerald-600 bg-emerald-50' : 'text-slate-500 bg-slate-50'}`}>
                    {change}
                </span>
                    <span className="text-sm font-medium text-slate-500">vs last period</span>
                </div>
            </div>
        );
    };

// === PAGINA PRINCIPALA ===
const PlatformOverview: React.FC = () => {
    const [sellers, setSellers] = useState<Seller[]>([]);
    const [categories, setCategories] = useState<CategoryDistribution[]>([]);
    const [revenueData, setRevenueData] = useState<RevenueDataPoint[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const [sellersRes, catRes, revRes] = await Promise.all([
                    AdminService.getAllSellers(),
                    AdminService.getCategoryStats(),
                    AdminService.getRevenueAnalytics(30)
                ]);
                setSellers(sellersRes);
                setCategories(catRes);
                setRevenueData(revRes);
            } catch (error) {
                console.error("Error loading admin data:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    // Calcule pentru Carduri
    const totalRevenue = revenueData.reduce((sum, d) => sum + d.amount, 0);
    const activeSellersCount = sellers.length;
    // Exemplu: Top performer din datele de categorii
    const topCategory = categories.sort((a, b) => b.revenue - a.revenue)[0];

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#F9FAFB]">
                <Loader2 className="w-10 h-10 animate-spin text-[#54457F]" />
            </div>
        );
    }

    return (
        <div className="min-h-screen font-sans p-10" style={{ backgroundColor: THEME.background }}>
            <header className="flex justify-between items-start mb-12">
                <div>
                    <h1 className="text-5xl font-extrabold text-slate-950 mb-3">Platform Overview</h1>
                    <p className="text-xl text-slate-600 font-medium">Insights based on the last 30 days of activity.</p>
                </div>
                <button className="bg-white p-3.5 rounded-full shadow-sm">
                    <Bell className="w-7 h-7 text-slate-700" />
                </button>
            </header>

            {/* STATS SECTION */}
            <section className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-12">
                <StatCard
                    label="TOTAL SELLERS"
                    value={activeSellersCount.toString()}
                    change="^100%" // Ar trebui calculat vs perioada precedenta din backend
                    Icon={Users}
                    color={THEME.primary}
                />
                <StatCard
                    label="PLATFORM REVENUE"
                    value={`$${totalRevenue.toLocaleString()}`}
                    change="^5.2%"
                    Icon={DollarSign}
                    color={THEME.secondary}
                />
                <StatCard
                    label="TOP CATEGORY"
                    value={topCategory?.category || "N/A"}
                    change="Trending"
                    Icon={Store}
                    color={THEME.accent}
                />
            </section>

            <section className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* SELLERS TABLE */}
                <div className="lg:col-span-2 bg-white p-8 rounded-3xl shadow-sm">
                    <div className="flex justify-between items-center mb-8">
                        <h2 className="text-2xl font-bold text-slate-950">Active Sellers</h2>
                        <button className="flex items-center gap-2 text-[#54457F] font-bold text-sm hover:underline">
                            Manage All <ArrowRight size={16} />
                        </button>
                    </div>

                    <div className="w-full overflow-x-auto">
                        <table className="w-full">
                            <thead>
                            <tr className="border-b border-slate-100">
                                <th className="text-left py-4 text-[11px] font-black uppercase tracking-widest text-slate-400">Seller</th>
                                <th className="text-left py-4 text-[11px] font-black uppercase tracking-widest text-slate-400">Total Sales</th>
                                <th className="text-left py-4 text-[11px] font-black uppercase tracking-widest text-slate-400">Rating</th>
                                <th className="text-right py-4 text-[11px] font-black uppercase tracking-widest text-slate-400">Status</th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-50">
                            {sellers.map((seller) => (
                                <tr key={seller.id} className="group hover:bg-slate-50/50 transition-colors">
                                    <td className="py-5">
                                        <div className="flex items-center gap-4">
                                            <div className="w-10 h-10 rounded-full bg-slate-100 overflow-hidden">
                                                <img
                                                    src={seller.imageUrl ? `http://92.5.49.167:9000/disi-images/${seller.imageUrl}` : "/placeholder-user.png"}
                                                    alt={seller.name}
                                                    className="w-full h-full object-cover"
                                                />
                                            </div>
                                            <div>
                                                <p className="font-bold text-[15px] text-black">{seller.name}</p>
                                                <p className="text-[11px] text-slate-400">{seller.email}</p>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="py-5 font-semibold text-slate-600">
                                        ${seller.totalRevenue.toLocaleString()}
                                    </td>
                                    <td className="py-5">
                                            <span className="px-3 py-1 rounded-full bg-amber-50 text-amber-600 text-xs font-bold">
                                                ★ {seller.rating.toFixed(1)}
                                            </span>
                                    </td>
                                    <td className="py-5 text-right">
                                        {/* Presupunând că seller.status există, altfel folosim 'Completed' ca default */}
                                        <span className={`inline-block px-4 py-1.5 rounded-full text-[12px] font-bold ${STATUS_STYLES['Completed']}`}>
                                            Active
                                        </span>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* CATEGORY CHART AREA */}
                <div className="bg-white p-8 rounded-3xl shadow-sm flex flex-col">
                    <h2 className="text-2xl font-bold text-slate-950 mb-8">Sales by Category</h2>
                    <div className="flex-1 space-y-6">
                        {categories.map((cat) => (
                            <div key={cat.category}>
                                <div className="flex justify-between text-sm mb-2">
                                    <span className="font-bold text-slate-700">{cat.category}</span>
                                    <span className="text-slate-400">${cat.revenue.toLocaleString()}</span>
                                </div>
                                <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-[#54457F]"
                                        style={{ width: `${(cat.revenue / totalRevenue) * 100}%` }}
                                    ></div>
                                </div>
                            </div>
                        ))}
                    </div>
                    <div className="mt-10 p-4 rounded-xl border border-slate-100 bg-slate-50/50">
                        <p className="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">Recommendation</p>
                        <p className="text-sm font-bold text-slate-900">
                            {topCategory ? `Focus promotions on ${topCategory.category} this week.` : "Insufficient data."}
                        </p>
                    </div>
                </div>
            </section>
        </div>
    );
};

export default PlatformOverview;