import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import api from "../../services/api";
import { COUNTY_NAMES } from "../../models/romaniaData";
import { AlertCircle, Save, X, Trash2 } from "lucide-react";
import { interestOptions } from "../../models/interests";

interface ClientProfile {
	id: string;
	username: string;
	firstName: string;
	lastName: string;
	email: string;
	region: string;
	age: number;
	phoneNumber: string;
	interests: string[];
}

const inputCls =
	"w-full bg-gray-100 rounded-lg px-4 py-3 text-sm text-gray-700 outline-none placeholder-gray-400 focus:ring-2 focus:ring-indigo-300 disabled:opacity-50 disabled:cursor-not-allowed";
const labelCls = "block text-xs font-semibold text-gray-600 uppercase tracking-wider mb-1";

function ClientProfile() {
	const navigate = useNavigate();
	const { logout } = useAuth();
	const [profile, setProfile] = useState<ClientProfile | null>(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [isEditing, setIsEditing] = useState(false);
	const [isSaving, setIsSaving] = useState(false);
	const [isDeleting, setIsDeleting] = useState(false);
	const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

	// Form state
	const [formData, setFormData] = useState<ClientProfile | null>(null);

	useEffect(() => {
		fetchProfile();
	}, []);

	const fetchProfile = async () => {
		setLoading(true);
		setError("");
		try {
			let data: ClientProfile;

			const response = await api.get<ClientProfile>("/v1/users/client/profile");
			data = response.data;

			setProfile(data);
			setFormData(data);
		} catch {
			setError("Failed to load profile. Please try again.");
		} finally {
			setLoading(false);
		}
	};

	const handleInputChange = (field: keyof ClientProfile, value: any) => {
		if (formData) {
			setFormData({ ...formData, [field]: value });
		}
	};

	const toggleInterest = (interest: string) => {
		if (formData) {
			setFormData({
				...formData,
				interests: formData.interests.includes(interest)
					? formData.interests.filter((i) => i !== interest)
					: [...formData.interests, interest],
			});
		}
	};

	const handleSave = async () => {
		if (!formData) return;
		setIsSaving(true);
		setError("");
		try {
			let response: ClientProfile;

			const res = await api.put<ClientProfile>("/v1/users/client/profile", formData);
			response = res.data;

			setProfile(response);
			setFormData(response);
			setIsEditing(false);
		} catch {
			setError("Failed to update profile. Please try again.");
		} finally {
			setIsSaving(false);
		}
	};

	const handleCancel = () => {
		setFormData(profile);
		setIsEditing(false);
	};

	const handleDelete = async () => {
		setIsDeleting(true);
		setError("");
		try {			
			await api.delete("/v1/users/client/profile");
			logout();
			navigate("/login");
		} catch {
			setError("Failed to delete account. Please try again.");
			setIsDeleting(false);
		}
	};

	if (loading) {
		return (
			<div className="flex items-center justify-center min-h-screen">
				<div className="text-center">
					<div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mb-4" />
					<p className="text-gray-600">Loading profile...</p>
				</div>
			</div>
		);
	}

	if (!profile || !formData) {
		return (
			<div className="flex items-center justify-center min-h-screen">
				<div className="text-center">
					<p className="text-gray-600">Profile not found</p>
				</div>
			</div>
		);
	}

	return (
		<div className="max-w-2xl mx-auto py-8 px-4">
			<div className="bg-white rounded-lg shadow-lg p-8">

				{/* Header */}
				<div className="flex items-center justify-between mb-6">
					<h1 className="text-3xl font-bold text-gray-900">My Profile</h1>
					{!isEditing && (
						<button
							onClick={() => setIsEditing(true)}
							className="cursor-pointer !bg-[#54457f] text-white px-4 py-2 rounded-lg hover:!bg-[#54457f]/[0.8] transition-colors"
						>
							Edit Profile
						</button>
					)}
				</div>

				{/* Error Message */}
				{error && (
					<div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-gap-3">
						<AlertCircle className="w-5 h-5 text-red-600 shrink-0 mt-0.5" />
						<p className="text-red-600 text-sm">{error}</p>
					</div>
				)}

				{/* Profile Form */}
				<form className="space-y-6">
					{/* Username & Email Row */}
					<div className="grid grid-cols-2 gap-4">
						<div>
							<label className={labelCls}>Username</label>
							<input
								type="text"
								value={formData.username}
								onChange={(e) => handleInputChange("username", e.target.value)}
								disabled
								className={inputCls}
							/>
						</div>
						<div>
							<label className={labelCls}>Email</label>
							<input
								type="email"
								value={formData.email}
								onChange={(e) => handleInputChange("email", e.target.value)}
								disabled={!isEditing}
								className={inputCls}
							/>
						</div>
					</div>

					{/* First Name & Last Name Row */}
					<div className="grid grid-cols-2 gap-4">
						<div>
							<label className={labelCls}>First Name</label>
							<input
								type="text"
								value={formData.firstName}
								onChange={(e) => handleInputChange("firstName", e.target.value)}
								disabled={!isEditing}
								className={inputCls}
							/>
						</div>
						<div>
							<label className={labelCls}>Last Name</label>
							<input
								type="text"
								value={formData.lastName}
								onChange={(e) => handleInputChange("lastName", e.target.value)}
								disabled={!isEditing}
								className={inputCls}
							/>
						</div>
					</div>

					{/* County & Age Row */}
					<div className="grid grid-cols-2 gap-4">
						<div>
							<label className={labelCls}>County</label>
							<select
								value={formData.region}
								onChange={(e) => handleInputChange("region", e.target.value)}
								disabled={!isEditing}
								className={inputCls}
							>
								<option value="">Select a county</option>
								{COUNTY_NAMES.map((county) => (
									<option key={county} value={county}>
										{county}
									</option>
								))}
							</select>
						</div>
						<div>
							<label className={labelCls}>Age</label>
							<input
								type="number"
								value={formData.age}
								onChange={(e) => handleInputChange("age", parseInt(e.target.value, 10))}
								disabled={!isEditing}
								min={1}
								max={120}
								className={inputCls}
							/>
						</div>
					</div>

					{/* Phone Number */}
					<div>
						<label className={labelCls}>Phone Number</label>
						<input
							type="tel"
							value={formData.phoneNumber}
							onChange={(e) => handleInputChange("phoneNumber", e.target.value)}
							disabled={!isEditing}
							className={inputCls}
						/>
					</div>

					{/* Interests */}
					<div>
						<label className={labelCls}>Interests</label>
						<div className="space-y-3">
							{interestOptions.map((option) => (
								<label
									key={option.value}
									className={`flex items-center gap-3 cursor-pointer p-2 rounded transition-colors ${isEditing ? "hover:bg-gray-50" : "opacity-60 cursor-default"
										}`}
								>
									<input
										type="checkbox"
										checked={formData.interests.includes(option.value)}
										onChange={() => toggleInterest(option.value)}
										disabled={!isEditing}
										className="!w-4 !h-4 rounded accent-indigo-500 shrink-0"
									/>
									<span className="text-sm text-gray-700">{option.label}</span>
								</label>
							))}
						</div>
					</div>

					{/* Action Buttons */}
					<div className="flex gap-4 pt-6 border-t border-gray-200">
						{isEditing ? (
							<>
								<button
									type="button"
									onClick={handleSave}
									disabled={isSaving}
									className="cursor-pointer flex items-center gap-2 !bg-[#9ba2ff] text-white px-6 py-2 rounded-lg hover:!bg-[#9ba2ff]/[0.8] transition-colors disabled:opacity-50 focus:outline-none"
								>
									<Save className="w-4 h-4" />
									{isSaving ? "Saving..." : "Save Changes"}
								</button>
								<button
									type="button"
									onClick={handleCancel}
									className="cursor-pointer flex items-center gap-2 !bg-gray-400 text-white px-6 py-2 rounded-lg hover:bg-gray-500 transition-colors focus:outline-none"
								>
									<X className="w-4 h-4" />
									Cancel
								</button>
							</>
						) : (
							<button
								type="button"
								onClick={() => setShowDeleteConfirm(true)}
								className="cursor-pointer flex items-center gap-2 !bg-[#e85d75] text-white px-6 py-2 rounded-lg hover:!bg-[#e85d75]/[0.8] transition-colors ml-auto focus:outline-none"
							>
								<Trash2 className="w-4 h-4" />
								Delete Account
							</button>
						)}
					</div>
				</form>
			</div>

			{/* Delete Confirmation Modal */}
			{showDeleteConfirm && (
				<div className="fixed inset-0 flex items-center justify-center p-4 z-50">
					<div className="bg-white rounded-lg shadow-xl p-6 max-w-sm">
						<h3 className="text-lg font-bold text-gray-900 mb-4">Delete Account?</h3>
						<p className="text-gray-600 mb-6">
							This action cannot be undone. All your data will be permanently deleted.
						</p>
						<div className="flex gap-4">
							<button
								onClick={() => setShowDeleteConfirm(false)}
								className="cursor-pointer flex-1 bg-gray-300 text-gray-900 px-4 py-2 rounded-lg hover:bg-gray-400 transition-colors focus:outline-none"
							>
								Cancel
							</button>
							<button
								onClick={handleDelete}
								disabled={isDeleting}
								className="cursor-pointer flex-1 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50 focus:outline-none"
							>
								{isDeleting ? "Deleting..." : "Delete"}
							</button>
						</div>
					</div>
				</div>
			)}
		</div>
	);
}

export default ClientProfile;
