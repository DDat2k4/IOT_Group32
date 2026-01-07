import { Link } from "react-router-dom";
import logo from "../assets/logo.png";

export default function Sidebar() {
  return (
    <div className="w-64 bg-gray-800 text-white flex flex-col">
      
      {/* Logo */}
      <div className="p-4 flex items-center justify-center border-b border-gray-700">
        <img
          src={logo}
          alt="Logo"
          className="h-12 w-auto"
        />
      </div>

      {/* Menu */}
      <Link to="/devices" className="p-4 hover:bg-gray-700">
        Devices
      </Link>
      <Link to="/users" className="p-4 hover:bg-gray-700">
        Users
      </Link>
    </div>
  );
}
