import { useState } from "react";
import { useNavigate } from "react-router-dom";
import authApi from "../api/authApi";

export default function Register() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = { username, password, fullName, email };

    try {
      const res = await authApi.register(data);
      console.log("Backend response:", res.data);

      if (res.data.success) {
        console.log(`Đăng ký thành công: ${res.data.message}`);
        navigate("/login");
      } else {
        console.log(`Đăng ký thất bại: ${res.data.message}`);
      }
    } catch (err) {
      // Nếu backend trả lỗi 4xx hoặc 5xx
      if (err.response) {
        console.error("Error response:", err.response.data);
      } else {
        console.error(err);
      }
    }
  };

  return (
    <div className="h-screen flex items-center justify-center bg-gray-100">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded shadow w-96">
        <h2 className="text-2xl font-bold mb-6 text-center">Đăng ký</h2>
        <div className="mb-4">
          <label className="block mb-1">Username</label>
          <input
            type="text"
            className="w-full border p-2 rounded"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div className="mb-4">
          <label className="block mb-1">Full Name</label>
          <input
            type="text"
            className="w-full border p-2 rounded"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            required
          />
        </div>
        <div className="mb-4">
          <label className="block mb-1">Email</label>
          <input
            type="email"
            className="w-full border p-2 rounded"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="mb-4">
          <label className="block mb-1">Password</label>
          <input
            type="password"
            className="w-full border p-2 rounded"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button className="w-full bg-blue-600 text-white py-2 rounded font-bold hover:bg-blue-700">
          Đăng ký
        </button>
      </form>
    </div>
  );
}
