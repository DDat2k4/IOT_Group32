import { useState } from "react";
import authApi from "../api/authApi"; // thêm hàm changePassword
import { useNavigate } from "react-router-dom";

export default function ChangePassword() {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await authApi.changePassword({ oldPassword, newPassword });
      alert("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
      // Xóa token cũ
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("username");
      navigate("/login");
    } catch (err) {
      console.error(err);
      alert("Đổi mật khẩu thất bại!");
    }
  };

  

  return (
    <div className="h-screen flex items-center justify-center bg-gray-100">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded shadow w-96">
        <h2 className="text-2xl font-bold mb-6 text-center">Đổi mật khẩu</h2>
        <div className="mb-4">
          <label className="block mb-1">Mật khẩu hiện tại</label>
          <input
            type="password"
            className="w-full border p-2 rounded"
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
            required
          />
        </div>
        <div className="mb-4">
          <label className="block mb-1">Mật khẩu mới</label>
          <input
            type="password"
            className="w-full border p-2 rounded"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            required
          />
        </div>
        <button className="w-full bg-blue-600 text-white py-2 rounded font-bold hover:bg-blue-700">
          Đổi mật khẩu
        </button>
      </form>
    </div>
  );
}
