import { ChevronLeft, ChevronRight } from "lucide-react";
import { Link } from "react-router-dom";

type CategoryCardProps = {
  title: string;
  subtitle?: string;
  className?: string;
  backgroundClassName?: string;
  image?: string;
};

type ProductCardProps = {
  category: string;
  title: string;
  description: string;
  price: string;
  oldPrice?: string;
  image: string;
  badge?: string;
};

function CategoryCard({
  title,
  subtitle,
  className = "",
  backgroundClassName = "bg-[#F3F3F3]",
  image,
}: CategoryCardProps) {
  return (
    <div
      className={`relative overflow-hidden rounded-2xl ${backgroundClassName} ${className}`}
    >
      {image && (
        <img
          src={image}
          alt={title}
          className="absolute inset-0 h-full w-full object-cover"
        />
      )}

      <div className="absolute inset-0 bg-gradient-to-t from-black/10 to-transparent" />

      <div className="relative flex h-full flex-col justify-end p-5">
        {subtitle && (
          <p className="mb-1 text-[10px] font-semibold uppercase tracking-[0.12em] text-[#9BA2FF]">
            {subtitle}
          </p>
        )}
        <h3 className="text-[1.75rem] font-semibold leading-none text-[#000000]">
          {title}
        </h3>
      </div>
    </div>
  );
}

function ProductCard({
  category,
  title,
  description,
  price,
  oldPrice,
  image,
  badge,
}: ProductCardProps) {
  return (
    <article className="rounded-2xl bg-[#FFFFFF] p-3 shadow-[0_2px_10px_rgba(0,0,0,0.05)]">
      <div className="relative mb-3 h-[170px] overflow-hidden rounded-xl bg-[#F5F5F5]">
        {badge && (
          <span className="absolute left-3 top-3 z-10 rounded-full bg-[#E85D75] px-2 py-1 text-[10px] font-semibold uppercase tracking-wide text-[#FFFFFF]">
            {badge}
          </span>
        )}

        <img src={image} alt={title} className="h-full w-full object-cover" />
      </div>

      <p className="mb-1 text-[10px] font-semibold uppercase tracking-[0.12em] text-[#9BA2FF]">
        {category}
      </p>

      <h4 className="line-clamp-1 text-sm font-semibold text-[#000000]">
        {title}
      </h4>

      <p className="mt-1 line-clamp-2 text-xs leading-5 text-[#666666]">
        {description}
      </p>

      <div className="mt-3 flex items-center gap-2">
        <span className="text-lg font-bold text-[#000000]">{price}</span>
        {oldPrice && (
          <span className="text-sm text-[#999999] line-through">{oldPrice}</span>
        )}
      </div>
    </article>
  );
}

function HomePage() {
  const products = [
    {
      category: "Audio",
      title: "Auralis Pro Wireless Headphones",
      description:
        "Noise-canceling over-ear headphones with studio-quality sound.",
      price: "$299",
      image:
        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80",
    },
    {
      category: "Wearables",
      title: "Tempo Smartwatch",
      description: "Sleek fitness tracking and notifications on your wrist.",
      price: "$149",
      image:
        "https://images.unsplash.com/photo-1546868871-7041f2a55e12?auto=format&fit=crop&w=900&q=80",
    },
    {
      category: "Photography",
      title: "RetroSnap Instant Camera",
      description: "Capture moments instantly with vintage charm.",
      price: "$89",
      oldPrice: "$120",
      badge: "Sale",
      image:
        "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=80",
    },
    {
      category: "Footwear",
      title: "Urban Classic Sneakers",
      description: "Everyday comfort meets timeless minimalist design.",
      price: "$110",
      image:
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80",
    },
  ];

  return (
    <div className="w-full bg-[#F8F8F8] text-[#000000]">

      <main className="w-full">
        <section className="w-full px-8 py-20 xl:px-12">
          <div className="mx-auto flex max-w-[1200px] flex-col items-center justify-center text-center">
            <h1 className="text-7xl font-extrabold tracking-tight text-[#000000]">
              OLY
            </h1>

            <p className="mt-6 max-w-[720px] text-[1.65rem] leading-tight text-[#4A4A4A]">
              Magazin Virtual – Shop &amp; More. The curated canvas for your
              digital lifestyle.
            </p>

            <div className="mt-10 flex items-center gap-4">
              <Link
                to="/products"
                className="rounded-xl bg-[#54457F] px-8 py-3 text-sm font-semibold text-[#FFFFFF] shadow-[0_8px_16px_rgba(84,69,127,0.18)] transition hover:opacity-90"
              >
                Browse Products
              </Link>
              <Link
                to="/register"
                className="rounded-xl border border-[#54457F] bg-[#FFFFFF] px-8 py-3 text-sm font-semibold text-[#54457F] transition hover:bg-[#F5F2FB]"
              >
                Sign Up
              </Link>
            </div>
          </div>
        </section>

        <section className="w-full px-8 pb-14 xl:px-12">
          <div className="mb-6 flex items-center justify-between">
            <h2 className="text-[2rem] font-bold text-[#000000]">
              Curated Spaces
            </h2>
            <Link
              to="/categories"
              className="text-sm font-medium text-[#54457F] transition hover:opacity-80"
            >
              View all categories
            </Link>
          </div>

          <div className="grid w-full grid-cols-12 gap-5">
            <CategoryCard
              title="Electronics"
              subtitle="Tech Lifestyle"
              className="col-span-6 h-[240px]"
              backgroundClassName="bg-gradient-to-r from-[#8F959B] via-[#D6D8DB] to-[#8E969D]"
              image="https://images.unsplash.com/photo-1527443154391-507e9dc6c5cc?auto=format&fit=crop&w=1200&q=80"
            />

            <CategoryCard
              title="Fashion"
              className="col-span-3 h-[240px]"
              backgroundClassName="bg-gradient-to-br from-[#F9A3A7] to-[#E85D75]"
              image="https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=900&q=80"
            />

            <CategoryCard
              title="Home"
              className="col-span-3 h-[240px]"
              backgroundClassName="bg-gradient-to-br from-[#ECECEC] to-[#CFCFCF]"
              image="https://images.unsplash.com/photo-1518780664697-55e3ad937233?auto=format&fit=crop&w=900&q=80"
            />

            <div className="col-span-12 rounded-2xl bg-[#D6C9F2] px-10 py-10">
              <div className="flex min-h-[170px] flex-col items-center justify-center text-center">
                <span className="mb-4 text-xl text-[#54457F]">★</span>
                <h3 className="text-[2rem] font-bold text-[#54457F]">
                  New Arrivals
                </h3>
                <Link
                  to="/products"
                  className="mt-5 rounded-full bg-[#FFFFFF] px-6 py-2 text-sm font-medium text-[#54457F] transition hover:bg-[#F6F6F6]"
                >
                  Explore Now
                </Link>
              </div>
            </div>
          </div>
        </section>

        <section className="w-full px-8 pb-20 xl:px-12">
          <div className="rounded-[28px] bg-[#F1F1F1] p-6 md:p-8">
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-[2rem] font-bold text-[#000000]">
                Editor&apos;s Picks
              </h2>

              <div className="flex items-center gap-2">
                <button className="rounded-full bg-[#FFFFFF] p-2 text-[#666666] shadow-sm transition hover:text-[#54457F]">
                  <ChevronLeft size={18} />
                </button>
                <button className="rounded-full bg-[#FFFFFF] p-2 text-[#666666] shadow-sm transition hover:text-[#54457F]">
                  <ChevronRight size={18} />
                </button>
              </div>
            </div>

            <div className="grid grid-cols-4 gap-4">
              {products.map((product) => (
                <ProductCard key={product.title} {...product} />
              ))}
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}

export default HomePage;