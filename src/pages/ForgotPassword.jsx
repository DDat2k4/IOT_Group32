import { useState } from "react";
import { Link } from "react-router-dom";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    alert(`Reset link sent to: ${email}`);
  };

  return (
    <div className="h-screen flex items-center justify-center bg-gray-100">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded shadow w-96">
        <h2 className="text-2xl font-bold mb-6 text-center">Quên mật khẩu</h2>
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
        <button className="w-full bg-blue-600 text-white py-2 rounded font-bold hover:bg-blue-700">
          Gửi link đặt lại mật khẩu
        </button>
        <div className="mt-4 text-center">
          <Link to="/login" className="text-blue-600 hover:underline">
            Quay lại đăng nhập
          </Link>
        </div>
      </form>
    </div>
  );
}
