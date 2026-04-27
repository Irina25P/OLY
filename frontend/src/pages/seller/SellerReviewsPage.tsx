import { useState } from "react"

function SellerReviewsPage() {
    const reviews = [
        ["Aura Cleansing Candle", "Maria P.", "Beautiful scent and fast delivery.", "5.0"],
        ["Luminary Silk Scarf", "Andrei M.", "Good quality, elegant packaging.", "4.8"],
        ["Raw Amethyst Band", "Elena R.", "Looks exactly like the pictures.", "4.7"],
    ]

    const [openReply, setOpenReply] = useState<string | null>(null)

    return (
        <div>
            <h1 className="text-4xl font-extrabold">Reviews</h1>
            <p className="mt-2 text-gray-600">
                View feedback left by users for your products.
            </p>

            <div className="mt-8 space-y-4">
                {reviews.map((review) => {
                    const reviewId = `${review[0]}-${review[1]}`

                    return (
                        <div key={reviewId} className="rounded-3xl bg-white p-6 shadow-sm">
                            <div className="flex items-start justify-between gap-6">
                                <div>
                                    <h3 className="font-bold">{review[0]}</h3>
                                    <p className="text-sm text-gray-500">By {review[1]}</p>
                                    <p className="mt-2 text-gray-700">{review[2]}</p>
                                </div>

                                <div className="flex shrink-0 flex-col items-end gap-4">
                  <span className="rounded-full bg-[#E9E7FF] px-4 py-2 text-sm font-bold text-[#54457F]">
                    ★ {review[3]}
                  </span>

                                    <button
                                        type="button"
                                        onClick={() =>
                                            setOpenReply(openReply === reviewId ? null : reviewId)
                                        }
                                        className="btn-primary !rounded-full !px-5 !py-2 !text-sm"
                                    >
                                        Reply
                                    </button>
                                </div>
                            </div>

                            {openReply === reviewId && (
                                <div className="mt-5 rounded-2xl bg-[#F8F8F8] p-4">
                  <textarea
                      rows={4}
                      placeholder="Write your reply..."
                      className="!w-full !rounded-xl !border !border-gray-200 !bg-white !p-3 !text-sm focus:!shadow-none"
                  />

                                    <div className="mt-3 flex justify-end gap-3">
                                        <button
                                            type="button"
                                            onClick={() => setOpenReply(null)}
                                            className="btn-secondary !rounded-full !px-5 !py-2 !text-sm"
                                        >
                                            Cancel
                                        </button>

                                        <button
                                            type="button"
                                            className="btn-accent !rounded-full !px-5 !py-2 !text-sm"
                                        >
                                            Send Reply
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

export default SellerReviewsPage