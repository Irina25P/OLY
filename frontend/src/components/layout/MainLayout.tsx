import { Outlet } from "react-router-dom";
import Footer from "./Footer";
import ClientTopBar from "./ClientTopBar";
import { Toaster } from "react-hot-toast";
import WebSocketConnection from "../../websocket/WebsocketConnection";

function MainLayout() {
  return (
    <div className="flex min-h-screen w-full flex-col bg-[#FFFFFF] text-[#000000]">

      <Toaster position="top-right" />
      <WebSocketConnection />

      <ClientTopBar />

      <main className="w-full flex-1 bg-[#FFFFFF]">
        <Outlet />
      </main>

      <Footer />
    </div>
  );
}

export default MainLayout;