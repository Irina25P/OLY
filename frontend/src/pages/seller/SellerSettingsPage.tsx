import { useState, useEffect, useRef } from "react";
import { Upload, X } from "lucide-react";
import axiosInstance from "../../services/axios";
import { useAuth } from "../../context/AuthContext";
import { COUNTY_NAMES, formatCountyForBackend } from "../../models/romaniaData";

interface SellerProfile {
    id: string;
    name: string;
    username: string;
    email: string;
    imageName: string;
    description: string;
    county: string;
}

const inputCls =
    "h-11 w-full rounded-xl border border-gray-200 px-4 py-0 text-sm focus:outline-none focus:ring-2 focus:ring-[#54457F]/30";

function countyEnumToDisplay(countyEnum: string): string {
    return COUNTY_NAMES.find(
        (name) => formatCountyForBackend(name) === countyEnum
    ) ?? countyEnum;
}

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";

function imageUrl(objectName: string | undefined | null): string | null {
    if (!objectName) return null;
    if (objectName.startsWith("http")) return objectName;
    return `${MINIO_BASE}/${objectName}`;
}

function SellerSettingsPage() {
    const { token } = useAuth();
    const authHeader = { Authorization: `Bearer ${token}` };

    const [profile, setProfile] = useState<SellerProfile | null>(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);

    // Editable fields
    const [name, setName] = useState("");
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [description, setDescription] = useState("");
    const [county, setCounty] = useState("");

    // Image
    const [newImageFile, setNewImageFile] = useState<File | null>(null);
    const [newImagePreview, setNewImagePreview] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        axiosInstance
            .get<SellerProfile>("/v1/users/seller/profile", { headers: authHeader })
            .then((res) => {
                const p = res.data;
                setProfile(p);
                setName(p.name ?? "");
                setUsername(p.username ?? "");
                setEmail(p.email ?? "");
                setDescription(p.description ?? "");
                setCounty(countyEnumToDisplay(p.county ?? ""));
            })
            .catch(() => setError("Failed to load profile."))
            .finally(() => setLoading(false));
    }, []);

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;
        setNewImageFile(file);
        if (newImagePreview) URL.revokeObjectURL(newImagePreview);
        setNewImagePreview(URL.createObjectURL(file));
    };

    const removeNewImage = () => {
        setNewImageFile(null);
        if (newImagePreview) URL.revokeObjectURL(newImagePreview);
        setNewImagePreview(null);
        if (fileInputRef.current) fileInputRef.current.value = "";
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        setSuccess(false);
        setSaving(true);

        try {
            let imageName = profile?.imageName ?? "";

            if (newImageFile) {
                const formData = new FormData();
                formData.append("file", newImageFile);
                const uploadRes = await axiosInstance.post<{ objectName: string }>(
                    "/v1/images",
                    formData,
                    { headers: { "Content-Type": undefined } }
                );
                imageName = uploadRes.data.objectName;
            }

            const updated = await axiosInstance.put<SellerProfile>(
                "/v1/users/seller/profile",
                {
                    id: profile?.id,
                    name,
                    username,
                    email,
                    imageName,
                    description,
                    county: formatCountyForBackend(county),
                },
                { headers: authHeader }
            );

            setProfile(updated.data);
            setSuccess(true);
            removeNewImage();
        } catch {
            setError("Failed to save changes. Please try again.");
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div>
                <h1 className="text-4xl font-extrabold">Settings</h1>
                <div className="mt-8 text-center text-gray-400">Loading profile...</div>
            </div>
        );
    }

    return (
        <div>
            <h1 className="text-4xl font-extrabold">Settings</h1>
            <p className="mt-2 text-gray-600">Manage your seller profile information.</p>

            <div className="mt-8 rounded-3xl bg-white p-8 shadow-sm">
                <form onSubmit={handleSave}>
                    {error && (
                        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-600">
                            {error}
                        </div>
                    )}
                    {success && (
                        <div className="mb-4 rounded-lg border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-700">
                            Profile saved successfully.
                        </div>
                    )}

                    <div className="grid max-w-3xl grid-cols-2 gap-5">
                        <div>
                            <label className="mb-2 block text-sm font-semibold text-gray-700">Full Name</label>
                            <input
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                placeholder="Store or full name"
                                className={inputCls}
                            />
                        </div>

                        <div>
                            <label className="mb-2 block text-sm font-semibold text-gray-700">Username</label>
                            <input
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="username"
                                className={inputCls}
                            />
                        </div>

                        <div>
                            <label className="mb-2 block text-sm font-semibold text-gray-700">Email</label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="seller@example.com"
                                className={inputCls}
                            />
                        </div>

                        <div>
                            <label className="mb-2 block text-sm font-semibold text-gray-700">County</label>
                            <select
                                value={county}
                                onChange={(e) => setCounty(e.target.value)}
                                className={inputCls}
                            >
                                <option value="">Select county</option>
                                {COUNTY_NAMES.map((c) => (
                                    <option key={c} value={c}>{c}</option>
                                ))}
                            </select>
                        </div>

                        {/* Store Image */}
                        <div className="col-span-2">
                            <label className="mb-3 block text-sm font-semibold text-gray-700">Store Image</label>

                            <div className="flex items-start gap-6">
                                {/* Current image */}
                                <div className="flex flex-col items-center gap-1">
                                    <p className="text-xs text-gray-400">Current</p>
                                    <div className="flex h-24 w-24 items-center justify-center overflow-hidden rounded-2xl border border-gray-200 bg-[#F4F4F6] text-xs text-gray-400">
                                        {imageUrl(profile?.imageName) ? (
                                            <img
                                                src={imageUrl(profile?.imageName)!}
                                                alt="Store logo"
                                                className="h-full w-full object-cover"
                                                onError={(e) => {
                                                    (e.target as HTMLImageElement).style.display = "none";
                                                }}
                                            />
                                        ) : (
                                            <span>No image</span>
                                        )}
                                    </div>
                                </div>

                                {/* New image preview */}
                                {newImagePreview && (
                                    <div className="flex flex-col items-center gap-1">
                                        <p className="text-xs text-gray-400">New preview</p>
                                        <div className="relative h-24 w-24 overflow-hidden rounded-2xl border border-[#54457F] bg-[#F4F4F6]">
                                            <img src={newImagePreview} alt="New logo preview" className="h-full w-full object-cover" />
                                            <button
                                                type="button"
                                                onClick={removeNewImage}
                                                className="absolute -right-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full bg-white shadow text-gray-500 hover:text-red-500"
                                            >
                                                <X size={11} />
                                            </button>
                                        </div>
                                        <p className="max-w-[96px] truncate text-[11px] text-gray-400">{newImageFile?.name}</p>
                                    </div>
                                )}

                                {/* Upload button */}
                                <div className="mt-6">
                                    <button
                                        type="button"
                                        onClick={() => fileInputRef.current?.click()}
                                        className="flex items-center gap-2 rounded-xl border border-gray-200 bg-[#F4F4F6] px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100"
                                    >
                                        <Upload size={16} />
                                        {newImageFile ? "Change image" : "Upload new image"}
                                    </button>
                                </div>
                            </div>

                            <input
                                ref={fileInputRef}
                                type="file"
                                accept="image/*"
                                onChange={handleImageChange}
                                className="hidden"
                            />
                        </div>

                        {/* Description */}
                        <div className="col-span-2">
                            <label className="mb-2 block text-sm font-semibold text-gray-700">Description</label>
                            <textarea
                                rows={5}
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                placeholder="Write a short description about your store..."
                                className="w-full rounded-xl border border-gray-200 p-4 text-sm focus:outline-none focus:ring-2 focus:ring-[#54457F]/30 resize-none"
                            />
                        </div>

                        <div className="col-span-2 mt-3 flex justify-end">
                            <button
                                type="submit"
                                disabled={saving}
                                className="rounded-full px-7 py-3 text-sm font-semibold text-white disabled:opacity-60"
                                style={{ backgroundColor: "#54457F" }}
                            >
                                {saving ? "Saving..." : "Save Changes"}
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default SellerSettingsPage;
