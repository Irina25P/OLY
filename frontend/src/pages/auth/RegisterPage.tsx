import { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ShoppingBag, Store, Upload, X } from "lucide-react";
import { api } from '../../services/api';
import { useAuth } from "../../context/AuthContext";import { COUNTY_NAMES, formatCountyForBackend } from "../../models/romaniaData";import logo from "../../assets/logo.png";

type Role = "client" | "seller";

const inputCls =
  "w-full bg-gray-100 rounded-lg px-4 py-3 text-sm text-gray-700 outline-none placeholder-gray-400 focus:ring-2 focus:ring-indigo-300";
const labelCls = "block text-xs font-semibold text-gray-600 uppercase tracking-wider mb-1";

function Field({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div>
      <label className={labelCls}>{label}</label>
      {children}
    </div>
  );
}

function RegisterPage() {
  const { registerClient, registerSeller } = useAuth();
  const navigate = useNavigate();

  const [role, setRole] = useState<Role>("client");

  // Shared
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  // Client-only
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [county, setCounty] = useState("");
  const [age, setAge] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [interests, setInterests] = useState<string[]>([]);

  const interestOptions = [
    { value: "ELECTRONICS_IT", label: "Electronics & IT" },
    { value: "FASHION_ACCESSORIES", label: "Fashion & Accessories" },
    { value: "HOME_GARDEN", label: "Home & Garden" },
    { value: "AUTO_MOTO", label: "Auto & Moto" },
    { value: "SPORTS_OUTDOOR", label: "Sports & Outdoor" },
    { value: "GAMES_GAMING", label: "Games & Gaming" },
  ];

  const toggleInterest = (interestValue: string) => {
    setInterests((prev) =>
      prev.includes(interestValue)
        ? prev.filter((i) => i !== interestValue)
        : [...prev, interestValue]
    );
  };

  // Seller-only
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [sellerCounty, setSellerCounty] = useState("");
  const [logoFile, setLogoFile] = useState<File | null>(null);
  const [logoPreview, setLogoPreview] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setLogoFile(file);
    setLogoPreview(URL.createObjectURL(file));
  };

  const removeLogo = () => {
    setLogoFile(null);
    if (logoPreview) URL.revokeObjectURL(logoPreview);
    setLogoPreview(null);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      if (role === "client") {
        await registerClient({
          firstName,
          lastName,
          email,
          username,
          password,
          county: formatCountyForBackend(county),
          age: age ? parseInt(age) : 18,
          phoneNumber: phoneNumber,
          interests: interests.length > 0 ? interests : [],
        });
      } else {
        let imageName: string;

        if (logoFile === null) {
          setError("Please upload a logo for your store.");
          return;
        }
        
        const formData = new FormData();
        formData.append("file", logoFile);
        const uploadRes = await api.post<{ objectName: string }>(
          `v1/images`,
          formData,
          {
            headers: {
              "Content-Type": undefined,
            }
        });
        imageName = uploadRes.data.objectName;

        await registerSeller({
          name,
          email,
          username,
          password,
          imageName,
          description: description,
          county: formatCountyForBackend(sellerCounty),
        });
      }
      navigate("/login");
    } catch {
      setError("Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-100 via-purple-50 to-blue-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl p-8">
        {/* Logo */}
        <div className="flex flex-col items-center mb-6">
          <img src={logo} alt="OLY Marketplace" className="h-14 w-auto mb-3" />
          <p className="text-sm text-gray-500 mt-1">Select your role and start your journey.</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
              {error}
            </div>
          )}

          {/* Role selector */}
          <div>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => setRole("client")}
                className={`flex flex-col items-center gap-1 py-3 rounded-xl border-2 text-sm font-medium transition-all ${role === "client"
                  ? "border-indigo-400 bg-indigo-50 text-indigo-700"
                  : "border-gray-200 bg-gray-50 text-gray-500 hover:border-gray-300"
                  }`}
              >
                <ShoppingBag className="w-5 h-5" />
                Client
              </button>
              <button
                type="button"
                onClick={() => setRole("seller")}
                className={`flex flex-col items-center gap-1 py-3 rounded-xl border-2 text-sm font-medium transition-all ${role === "seller"
                  ? "border-indigo-400 bg-indigo-50 text-indigo-700"
                  : "border-gray-200 bg-gray-50 text-gray-500 hover:border-gray-300"
                  }`}
              >
                <Store className="w-5 h-5" />
                Seller
              </button>
            </div>
          </div>

          {/* ── Client fields ── */}
          {role === "client" && (
            <>
              <div className="grid grid-cols-2 gap-3">
                <Field label="First Name">
                  <input
                    type="text"
                    placeholder="John"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    required
                    className={inputCls}
                  />
                </Field>
                <Field label="Last Name">
                  <input
                    type="text"
                    placeholder="Doe"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    required
                    className={inputCls}
                  />
                </Field>
              </div>

              <Field label="Email Address">
                <input
                  type="email"
                  placeholder="name@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className={inputCls}
                />
              </Field>

              <Field label="Username">
                <input
                  type="text"
                  placeholder="johndoe"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                  className={inputCls}
                />
              </Field>

              <Field label="Password">
                <input
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  minLength={8}
                  className={inputCls}
                />
              </Field>

              <Field label="Confirm Password">
                <input
                  type="password"
                  placeholder="••••••••"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  minLength={8}
                  className={inputCls}
                />
              </Field>

              <div className="grid grid-cols-2 gap-3">
                <Field label="County">
                  <select
                    value={county}
                    onChange={(e) => setCounty(e.target.value)}
                    required
                    className={inputCls}
                  >
                    <option value="">Select a county</option>
                    {COUNTY_NAMES.map((countyName) => (
                      <option key={countyName} value={countyName}>
                        {countyName}
                      </option>
                    ))}
                  </select>
                </Field>
                <Field label="Age">
                  <input
                    type="number"
                    placeholder="25"
                    min={1}
                    max={120}
                    value={age}
                    onChange={(e) => setAge(e.target.value)}
                    className={inputCls}
                  />
                </Field>
              </div>

              <Field label="Phone Number">
                <input
                  type="tel"
                  placeholder="+1 234 567 8900"
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  className={inputCls}
                />
              </Field>

              <Field label="Interests">
                <div>
                  {interestOptions.map((option) => (
                    <label key={option.value} className="flex items-center gap-3 cursor-pointer hover:bg-gray-50 p-2 rounded transition-colors">
                      <input
                        type="checkbox"
                        checked={interests.includes(option.value)}
                        onChange={() => toggleInterest(option.value)}
                        className="!w-4 !h-4 rounded accent-indigo-500 shrink-0"
                      />
                      <span className="text-sm text-gray-700">{option.label}</span>
                    </label>
                  ))}
                </div>
              </Field>
            </>
          )}

          {/* ── Seller fields ── */}
          {role === "seller" && (
            <>
              {/* Logo upload */}
              <Field label="Store Logo">
                <div
                  role="button"
                  tabIndex={0}
                  onClick={() => fileInputRef.current?.click()}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter' || e.key === ' ') {
                      e.preventDefault();
                      fileInputRef.current?.click();
                    }
                  }}
                  // Added custom focus ring and hover border using your hex code
                  className={`
      relative flex flex-col items-center justify-center w-full h-36 
      rounded-xl cursor-pointer transition-all border-2 border-dashed
      ${logoPreview
                      ? 'bg-white border-gray-200'
                      : 'bg-gray-50 border-gray-300 hover:border-[#54457f] hover:!bg-[#54457f]/[.15]'
                    } 
      focus:outline-none focus:ring-2 focus:ring-[#54457f] focus:ring-offset-2
    `}
                >
                  {logoPreview ? (
                    <div className="relative group w-full h-full p-2">
                      <img
                        src={logoPreview}
                        alt="Logo preview"
                        className="h-full w-full object-contain" 
                      />
                      <div className="absolute inset-0 bg-black/5 opacity-0 group-hover:opacity-100 transition-opacity" />

                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          removeLogo();
                        }}
                        className="absolute top-2 right-2 z-10 bg-white/90 backdrop-blur-sm rounded-full p-1.5 shadow-sm text-gray-500 hover:text-red-600 hover:bg-white transition-colors"
                        aria-label="Remove logo"
                      >
                        <X className="w-4 h-4" />
                      </button>
                    </div>
                  ) : (
                    <div className="flex flex-col items-center gap-2 pointer-events-none">
                      {/* Icon now uses your custom color */}
                      <div className="p-3 bg-white rounded-full shadow-sm mb-1">
                        <Upload className="w-6 h-6 text-[#54457f]" />
                      </div>
                      <div className="text-center">
                        <p className="text-sm font-medium text-gray-700">Click to upload</p>
                        <p className="text-xs text-gray-400">PNG, JPG, WEBP</p>
                      </div>
                    </div>
                  )}
                </div>

                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleLogoChange}
                  className="hidden"
                />
              </Field>

              <Field label="Full Name">
                <input
                  type="text"
                  placeholder="Your store or full name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                  className={inputCls}
                />
              </Field>

              <Field label="Email Address">
                <input
                  type="email"
                  placeholder="name@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className={inputCls}
                />
              </Field>

              <Field label="Username">
                <input
                  type="text"
                  placeholder="storename"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                  className={inputCls}
                />
              </Field>

              <Field label="Password">
                <input
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  minLength={8}
                  className={inputCls}
                />
              </Field>

              <Field label="Confirm Password">
                <input
                  type="password"
                  placeholder="••••••••"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  minLength={8}
                  className={inputCls}
                />
              </Field>

              <Field label="Description">
                <textarea
                  placeholder="Tell us about your store..."
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  rows={3}
                  className={`${inputCls} resize-none`}
                />
              </Field>

              <Field label="County">
                <select
                  value={sellerCounty}
                  onChange={(e) => setSellerCounty(e.target.value)}
                  required
                  className={inputCls}
                >
                  <option value="">Select a county</option>
                  {COUNTY_NAMES.map((countyName) => (
                    <option key={countyName} value={countyName}>
                      {countyName}
                    </option>
                  ))}
                </select>
              </Field>
            </>
          )}

          {/* Submit */}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-xl text-white font-semibold text-sm !bg-gradient-to-r from-[#54457f] to-[#9ba2ff] hover:from-[#54457f]/[0.9] hover:to-[#9ba2ff]/[0.9] transition-all disabled:opacity-60"
          >
            {loading ? "Creating account..." : "Create Account"}
          </button>

          <div className="border-t border-gray-200 pt-4 text-center text-sm text-gray-500">
            Already have an account?{" "}
            <Link to="/login" className="font-semibold text-indigo-500 hover:underline">
              Sign in →
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default RegisterPage;
