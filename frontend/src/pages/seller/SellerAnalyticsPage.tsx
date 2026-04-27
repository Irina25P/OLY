function SellerAnalyticsPage() {
    const stats = [
        ["Luminary Silk Scarf", "$1,450", "10 sold"],
        ["Raw Amethyst Band", "$840", "3 sold"],
        ["Aura Cleansing Candle", "$760", "20 sold"],
    ];

    return (
        <div>
            <h1 className="text-4xl font-extrabold">Analytics</h1>
            <p className="mt-2 text-gray-600">Track total sales per product and overall store performance.</p>

            <div className="mt-8 grid grid-cols-3 gap-6">
                {stats.map((item) => (
                    <div key={item[0]} className="rounded-3xl bg-white p-7 shadow-sm">
                        <p className="text-sm text-gray-500">{item[0]}</p>
                        <h2 className="mt-5 text-3xl font-extrabold">{item[1]}</h2>
                        <p className="mt-2 text-sm text-[#54457F]">{item[2]}</p>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default SellerAnalyticsPage;