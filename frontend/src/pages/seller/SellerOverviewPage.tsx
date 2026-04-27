import { useMemo, useState } from "react"

type Period = "week" | "month" | "months"

function getChartLabels(period: Period) {
    const today = new Date()

    if (period === "week") {
        const dayNames = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"]

        return Array.from({ length: 7 }, (_, index) => {
            const date = new Date(today)
            date.setDate(today.getDate() - (6 - index))
            return dayNames[date.getDay()]
        })
    }

    if (period === "month") {
        const currentMonth = today.getMonth()
        const currentYear = today.getFullYear()

        const weeks: string[] = []
        const date = new Date(currentYear, currentMonth, 1)
        let weekNumber = 1

        while (date.getMonth() === currentMonth) {
            weeks.push(`WEEK ${weekNumber}`)
            date.setDate(date.getDate() + 7)
            weekNumber++
        }

        return weeks
    }

    const monthNames = [
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC",
    ]

    return Array.from({ length: 3 }, (_, index) => {
        const date = new Date(today)
        date.setMonth(today.getMonth() - (2 - index))
        return monthNames[date.getMonth()]
    })
}

function SellerOverviewPage() {
    const [period, setPeriod] = useState<Period>("week")

    const labels = useMemo(() => getChartLabels(period), [period])

    return (
        <div>
            {/* HEADER */}
            <div className="mb-10 flex items-start justify-between">
                <div>
                    <h1 className="text-4xl font-extrabold text-[#000000]">
                        Store Overview
                    </h1>
                    <p className="mt-2 text-base text-gray-600">
                        Here's your performance snapshot for today, curated for you.
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
                    {[
                        { title: "Today's Sales", value: "$1,240.50", meta: "↗ 12.5% vs yesterday" },
                        { title: "Total Orders", value: "48", meta: "+4 new processing now" },
                        { title: "New Feedback", value: "12", meta: "⚠ 2 Action Req" },
                    ].map((card) => (
                        <div key={card.title} className="rounded-3xl bg-white p-7 shadow-sm">
                            <p className="text-xs font-bold uppercase tracking-wide text-gray-500">
                                {card.title}
                            </p>
                            <h2 className="mt-8 text-4xl font-extrabold text-black">
                                {card.value}
                            </h2>
                            <p className="mt-4 text-sm text-gray-600">{card.meta}</p>
                        </div>
                    ))}
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

                    {/* BARS */}
                    <div className="mt-10 flex h-[360px] items-end justify-between border-t border-gray-200 pt-8">
                        {labels.map((label, index) => (
                            <div key={label} className="flex flex-col items-center gap-4">
                                <div
                                    className="w-12 rounded-t-2xl bg-[#9BA2FF]"
                                    style={{ height: `${90 + index * 28}px` }}
                                />
                                <span className="text-xs font-semibold text-gray-500">
                  {label}
                </span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* RECENT ACTIVITY */}
            <div className="mt-10 rounded-3xl bg-white p-8 shadow-sm">
                <div className="mb-8 flex items-center justify-between">
                    <div>
                        <h2 className="text-2xl font-bold">Recent Activity</h2>
                        <p className="mt-1 text-sm text-gray-600">
                            Your latest transactions requiring attention.
                        </p>
                    </div>

                    <button className="text-sm font-semibold text-[#54457F]">
                        View All Orders
                    </button>
                </div>

                <div className="space-y-4">
                    {[
                        ["#ORD-9932", "Sarah Jenkins", "Vintage Denim Jacket", "$125.00", "Processing"],
                        ["#ORD-9931", "Michael K.", "Leather Crossbody Bag", "$89.50", "Shipped"],
                        ["#ORD-9930", "David Ross", "Silk Scarf - Floral Print", "$45.00", "Pending Review"],
                    ].map((row) => (
                        <div
                            key={row[0]}
                            className="grid grid-cols-5 items-center rounded-2xl bg-[#F8F8F8] px-5 py-5 text-sm"
                        >
                            <span className="font-bold">{row[0]}</span>
                            <span>{row[1]}</span>
                            <span>{row[2]}</span>
                            <span className="font-bold">{row[3]}</span>
                            <span className="w-fit rounded-full bg-[#E9E7FF] px-4 py-2 text-xs font-bold text-[#54457F]">
                {row[4]}
              </span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}

export default SellerOverviewPage