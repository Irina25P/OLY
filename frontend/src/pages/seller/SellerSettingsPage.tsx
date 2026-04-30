import { useState } from "react"

const romanianCounties = [
    "Alba",
    "Arad",
    "Argeș",
    "Bacău",
    "Bihor",
    "Bistrița-Năsăud",
    "Botoșani",
    "Brașov",
    "Brăila",
    "București",
    "Buzău",
    "Caraș-Severin",
    "Călărași",
    "Cluj",
    "Constanța",
    "Covasna",
    "Dâmbovița",
    "Dolj",
    "Galați",
    "Giurgiu",
    "Gorj",
    "Harghita",
    "Hunedoara",
    "Ialomița",
    "Iași",
    "Ilfov",
    "Maramureș",
    "Mehedinți",
    "Mureș",
    "Neamț",
    "Olt",
    "Prahova",
    "Satu Mare",
    "Sălaj",
    "Sibiu",
    "Suceava",
    "Teleorman",
    "Timiș",
    "Tulcea",
    "Vaslui",
    "Vâlcea",
    "Vrancea",
]

function SellerSettingsPage() {
    const [imagePreview, setImagePreview] = useState<string | null>(null)

    return (
        <div>
            <h1 className="text-4xl font-extrabold">Settings</h1>
            <p className="mt-2 text-gray-600">
                Manage your seller profile information.
            </p>

            <div className="mt-8 rounded-3xl bg-white p-8 shadow-sm">
                <div className="grid max-w-3xl grid-cols-2 gap-5">
                    <div>
                        <label className="mb-2 block text-sm font-semibold text-gray-700">
                            Username
                        </label>
                        <input
                            placeholder="Enter username"
                            defaultValue="oly_seller"
                            className="!h-11 !rounded-xl !border-gray-200 !px-4 !py-0 !text-sm focus:!shadow-none"
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold text-gray-700">
                            Email
                        </label>
                        <input
                            type="email"
                            placeholder="seller@example.com"
                            defaultValue="seller@oly.com"
                            className="!h-11 !rounded-xl !border-gray-200 !px-4 !py-0 !text-sm focus:!shadow-none"
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold text-gray-700">
                            Region
                        </label>
                        <input
                            placeholder="Enter region"
                            defaultValue="North-West"
                            className="!h-11 !rounded-xl !border-gray-200 !px-4 !py-0 !text-sm focus:!shadow-none"
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold text-gray-700">
                            County
                        </label>
                        <select
                            defaultValue="Cluj"
                            className="!h-11 !rounded-xl !border-gray-200 !px-4 !py-0 !text-sm focus:!shadow-none"
                        >
                            <option value="">Select county</option>
                            {romanianCounties.map((county) => (
                                <option key={county} value={county}>
                                    {county}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="col-span-2">
                        <label className="mb-2 block text-sm font-semibold text-gray-700">
                            Store Image
                        </label>

                        <div className="flex items-center gap-4 h-11">
                            <input
                                type="file"
                                accept="image/png"
                                onChange={(event) => {
                                    const file = event.target.files?.[0]
                                    if (file) {
                                        setImagePreview(URL.createObjectURL(file))
                                    }
                                }}
                                className="h-full !rounded-xl !border !border-gray-200 !px-3 !py-2 !text-sm file:h-full file:rounded-full file:border-0 file:bg-[#54457F] file:px-4 file:text-sm file:font-semibold file:text-white hover:file:opacity-90"
                            />

                            <div className="flex h-16 w-16 items-center justify-center overflow-hidden rounded-xl bg-[#F4F4F6] text-xs text-gray-400">
                                {imagePreview ? (
                                    <img
                                        src={imagePreview}
                                        alt="Store preview"
                                        className="h-full w-full object-cover"
                                    />
                                ) : (
                                    <span>No image</span>
                                )}
                            </div>
                        </div>

                        <p className="mt-2 text-xs text-gray-500">
                            Upload a PNG image for the seller profile.
                        </p>
                    </div>

                    <div className="col-span-2">
                        <label className="mb-2 block text-sm font-semibold text-gray-700">
                            Description
                        </label>
                        <textarea
                            rows={5}
                            placeholder="Write a short description about your store..."
                            defaultValue="Curated products for modern online shopping."
                            className="!rounded-xl !border-gray-200 !p-4 !text-sm focus:!shadow-none"
                        />
                    </div>

                    <div className="col-span-2 mt-3 flex justify-end">
                        <button className="btn-primary !rounded-full">
                            Save Changes
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default SellerSettingsPage