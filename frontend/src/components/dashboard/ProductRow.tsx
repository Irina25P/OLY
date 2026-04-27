import StatusBadge from "./StatusBadge";
import { Pencil, Trash } from "lucide-react";

function ProductRow({ product }: any) {
    return (
        <div className="grid grid-cols-7 items-center bg-[#F8F8F8] rounded-xl p-4">

            {/* IMAGE */}
            <img
                src={product.image}
                className="w-[60px] h-[60px] rounded-lg object-cover"
            />

            {/* TITLE */}
            <div>
                <p className="font-semibold">{product.name}</p>
                <p className="text-xs text-gray-500">SKU: {product.sku}</p>
            </div>

            {/* PRICE */}
            <p className="font-medium">${product.price}</p>

            {/* STOCK */}
            <span className="bg-gray-200 px-3 py-1 rounded-full w-fit">
        {product.stock}
      </span>

            {/* CATEGORY */}
            <p>{product.category}</p>

            {/* STATUS */}
            <StatusBadge status={product.status} />

            {/* ACTIONS */}
            <div className="flex gap-3">
                <button>
                    <Pencil size={16} />
                </button>
                <button>
                    <Trash size={16} />
                </button>
            </div>
        </div>
    );
}

export default ProductRow;