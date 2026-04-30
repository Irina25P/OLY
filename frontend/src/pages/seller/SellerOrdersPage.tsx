const orders = [
    { id: "#ORD-9932", product: "Vintage Denim Jacket", image: "https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=300&q=80", price: "$125.00", status: "Pending" },
    { id: "#ORD-9931", product: "Leather Crossbody Bag", image: "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=300&q=80", price: "$89.50", status: "Delivered" },
    { id: "#ORD-9930", product: "Silk Scarf", image: "https://images.unsplash.com/photo-1606760227091-3dd870d97f1d?auto=format&fit=crop&w=300&q=80", price: "$45.00", status: "Processing" },
];

function SellerOrdersPage() {
    return (
        <div>
            <h1 className="text-4xl font-extrabold">Orders</h1>
            <p className="mt-2 text-gray-600">Manage customer orders and update their status manually.</p>

            <div className="mt-8 rounded-3xl bg-white p-6 shadow-sm">
                <div className="space-y-4">
                    {orders.map((order) => (
                        <div key={order.id} className="grid grid-cols-[90px_1fr_130px_180px] items-center rounded-2xl bg-[#F8F8F8] px-5 py-4">
                            <img src={order.image} alt={order.product} className="h-16 w-16 rounded-xl object-cover" />

                            <div>
                                <p className="font-bold">{order.id}</p>
                                <p className="text-sm text-gray-600">{order.product}</p>
                            </div>

                            <p className="font-bold">{order.price}</p>

                            <select className="!h-10 !rounded-xl !border-gray-200 !px-3 !py-0 !text-sm">
                                <option>{order.status}</option>
                                <option>Pending</option>
                                <option>Processing</option>
                                <option>Delivered</option>
                                <option>Cancelled</option>
                            </select>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default SellerOrdersPage;