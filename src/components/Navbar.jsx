import React from "react";
import { useNavigate } from "react-router-dom";

export default function Navbar({ username, onLogout }) {
  const navigate = useNavigate();
  const handleChangePassword = () => {
    navigate("/change-password");
  };
  return (
    <div className="h-16 bg-white shadow flex items-center justify-between px-4">
      <h1 className="text-xl font-bold">Fire Alert System</h1>

      <div className="flex items-center gap-4">
        {username && <span className="font-medium">Xin chào, {username}</span>}
        {username && (
          <button
            onClick={handleChangePassword}
            className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
          >
            Đổi mật khẩu
          </button>
        )}
        {onLogout && (
          <button
            onClick={onLogout}
            className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
          >
            Logout
          </button>
        )}
      </div>
    </div>
  );
}
