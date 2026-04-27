function StatusBadge({ status }: { status: string }) {
    if (status === "active") {
        return (
            <span className="bg-red-100 text-red-500 px-3 py-1 rounded-full text-xs">
        Active
      </span>
        );
    }

    if (status === "low") {
        return (
            <span className="bg-purple-100 text-purple-600 px-3 py-1 rounded-full text-xs">
        Low Stock
      </span>
        );
    }

    if (status === "draft") {
        return (
            <span className="bg-gray-200 text-gray-600 px-3 py-1 rounded-full text-xs">
        Draft
      </span>
        );
    }

    return null;
}

export default StatusBadge;