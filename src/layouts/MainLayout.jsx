import React from "react";
import { Outlet } from "react-router-dom";
import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";
import { useNavigate } from "react-router-dom";
import authApi from "../api/authApi";

export default function MainLayout() {
  const navigate = useNavigate();
  const username = localStorage.getItem("username");

  const handleLogout = async () => {
    try {
      const refreshToken = localStorage.getItem("refreshToken");
      if (refreshToken) {
        await authApi.logout({ refreshToken }); 
      }
    } catch (err) {
      console.error("Logout API error:", err);
    } finally {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("username");
      navigate("/login");
    }
  };

  return (
    <div className="flex h-screen relative">
      <Sidebar />
      <div className="flex-1 flex flex-col">
        <Navbar username={username} onLogout={handleLogout}/>
        <main className="flex-1 p-4 overflow-auto bg-gray-100">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
