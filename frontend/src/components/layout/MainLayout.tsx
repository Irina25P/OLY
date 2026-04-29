import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import ClientTopBar from "./ClientTopBar";

function MainLayout() {
  return (
    <div className="flex min-h-screen w-full flex-col bg-[#FFFFFF] text-[#000000]">
      <ClientTopBar />

      <main className="w-full flex-1 bg-[#FFFFFF]">
        <Outlet />
      </main>

      <Footer />
    </div>
  );
}

export default MainLayout;