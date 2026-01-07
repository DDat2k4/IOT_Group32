import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "../pages/Login";
import ForgotPassword from "../pages/ForgotPassword";
import RoomDetail from "../pages/RoomDetail";
import Devices from "../pages/Devices";
import Users from "../pages/Users";
import MainLayout from "../layouts/MainLayout";
import { useParams } from "react-router-dom";
import Register from "../pages/Register";
import ChangePassword from "../pages/ChangePassword";
// Wrapper để RoomDetail nhận params
function RoomDetailWrapper() {
  const params = useParams();
  return <RoomDetail params={params} />;
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        {/* redirect root to login */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        {/* Auth */}
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/register" element={<Register />} />
        <Route path="/change-password" element={<ChangePassword />} />
        
        {/* App Layout */}
        <Route element={<MainLayout />}>
          <Route path="/rooms/:id" element={<RoomDetailWrapper />} />
          <Route path="/devices" element={<Devices />} />
          <Route path="/users" element={<Users />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

