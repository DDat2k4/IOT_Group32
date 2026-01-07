import { useState, useEffect, useRef } from "react";
import deviceapi from "../api/deviceapi";
import useradminapi from "../api/useradminapi";
import SensorCard from "../components/SensorCard";

export default function Devices() {
  const [devices, setDevices] = useState([]);
  const [users, setUsers] = useState([]);
  const [userSearch, setUserSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [filters, setFilters] = useState({ deviceCode: "", name: "", location: "", status: "", userId: "" });
  const [page, setPage] = useState(0);
  const [newDevice, setNewDevice] = useState({ deviceCode: "", name: "", location: "", status: "active" });
  const [isUserDropdownOpen, setIsUserDropdownOpen] = useState(false);
  const userDropdownRef = useRef(null);

  // track expanded devices (show sensors when expanded)
  const [expandedDeviceIds, setExpandedDeviceIds] = useState([]);

  // Fetch devices on mount and when filters/page change
  useEffect(() => {
    fetchDevices();
  }, [filters, page]);

  // Fetch users for filter dropdown
  useEffect(() => {
    fetchUsers();
  }, []);


  
  // close dropdown when click outside
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (userDropdownRef.current && !userDropdownRef.current.contains(e.target)) {
        setIsUserDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await useradminapi.filterUsers({ page: 0, size: 100 });
      setUsers(res.data.content || res.data);
    } catch (err) {
      console.error("Error fetching users:", err);
    }
  };

  const fetchDevices = async () => {
    setLoading(true);
    try {
      const res = await deviceapi.filterDevices({ ...filters, page, size: 10 });
      setDevices(res.data.content || res.data);
    } catch (err) {
      console.error("Error fetching devices:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddDevice = async () => {
    if (!newDevice.deviceCode || !newDevice.name || !newDevice.location) return;
    try {
      if (editingId) {
        await deviceapi.updateDevice(editingId, newDevice);
      } else {
        await deviceapi.updateDevice(0, newDevice);
      }
      fetchDevices();
      setNewDevice({ deviceCode: "", name: "", location: "", status: "active" });
      setShowModal(false);
      setEditingId(null);
    } catch (err) {
      console.error("Error adding/updating device:", err);
    }
  };

  const handleEdit = async (device) => {
    setEditingId(device.id);
    setNewDevice(device);
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc muốn xóa thiết bị này?")) {
      try {
        await deviceapi.deleteDevice(id);
        fetchDevices();
      } catch (err) {
        console.error("Error deleting device:", err);
      }
    }
  };

  const handleTestAlert = (device) => {
    alert(`Test cảnh báo cho ${device.name}`);
  };

  const handleToggleDetails = (id) => {
    setExpandedDeviceIds(prev => {
      if (prev.includes(id)) return prev.filter(x => x !== id);
      return [...prev, id];
    });
  };

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">Quản lý thiết bị</h2>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 mb-4">
        <input
          type="text"
          placeholder="Tìm theo mã thiết bị"
          className="border p-2 rounded"
          value={filters.deviceCode}
          onChange={(e) => { setFilters({ ...filters, deviceCode: e.target.value }); setPage(0); }}
        />
        <input
          type="text"
          placeholder="Tìm theo phòng"
          className="border p-2 rounded"
          value={filters.name}
          onChange={(e) => { setFilters({ ...filters, name: e.target.value }); setPage(0); }}
        />
        {/* Searchable user combobox */}
        <div className="relative" ref={userDropdownRef}>
          <input
            type="text"
            placeholder={filters.userId ? `${users.find(u=>u.id==filters.userId)?.username || "Người dùng"}` : "Tất cả người dùng"}
            className="border p-2 rounded w-64"
            value={userSearch}
            onChange={(e) => { setUserSearch(e.target.value); setIsUserDropdownOpen(true); }}
            onFocus={() => setIsUserDropdownOpen(true)}
          />
          <div className={`absolute z-20 mt-1 w-64 bg-white border rounded shadow max-h-48 overflow-auto ${isUserDropdownOpen ? "" : "hidden"}`}>
            <div
              className="px-2 py-1 hover:bg-gray-100 cursor-pointer"
              onClick={() => { setFilters({ ...filters, userId: "" }); setUserSearch(""); setIsUserDropdownOpen(false); setPage(0); }}
            >
              Tất cả người dùng
            </div>
            {users
              .filter(user =>
                (user.username || "").toLowerCase().includes(userSearch.toLowerCase()) ||
                (user.fullName || "").toLowerCase().includes(userSearch.toLowerCase())
              )
              .map(user => (
                <div
                  key={user.id}
                  className="px-2 py-1 hover:bg-gray-100 cursor-pointer"
                  onClick={() => {
                    setFilters({ ...filters, userId: user.id });
                    setUserSearch(`${user.username} (${user.fullName})`);
                    setIsUserDropdownOpen(false);
                    setPage(0);
                  }}
                >
                  {user.username} ({user.fullName})
                </div>
              ))}
          </div>
        </div>
        <select
          className="border p-2 rounded"
          value={filters.status}
          onChange={(e) => { setFilters({ ...filters, status: e.target.value }); setPage(0); }}
        >
          <option value="">Tất cả trạng thái</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>
      </div>

      {/* Devices List */}
      {loading ? (
        <p>Đang tải...</p>
      ) : (
        devices.map((device) => (
          <div key={device.id} className="mb-6 p-4 bg-white rounded shadow">
            <div className="flex justify-between items-center mb-2">
              <div>
                <h3 className="font-bold text-lg">{device.name}</h3>
                <p className="text-sm text-gray-600">Mã: {device.deviceCode} | Vị trí: {device.location}</p>
                {device.users && device.users.length > 0 && (
                  <p className="text-sm text-gray-700 mt-1">
                    Người dùng: {device.users.map(u => `${u.fullName} (${u.email})`).join(', ')}
                  </p>
                )}
              </div>
              <span className={`px-2 py-1 rounded text-white ${device.status === "ACTIVE" ? "bg-green-500" : "bg-gray-500"}`}>
                {device.status}
              </span>
            </div>
            <div className="flex space-x-2 mb-2">
              <button
                className="px-3 py-1 bg-yellow-500 text-white rounded"
                onClick={() => handleEdit(device)}
              >
                Cập nhật
              </button>
              <button
                className="px-3 py-1 bg-red-500 text-white rounded"
                onClick={() => handleDelete(device.id)}
              >
                Xóa
              </button>
              <button
                className="px-3 py-1 bg-blue-600 text-white rounded"
                onClick={() => handleToggleDetails(device.id)}
              >
                {expandedDeviceIds.includes(device.id) ? "Đóng chi tiết" : "Chi tiết"}
              </button>
            </div>

            {/* Sensor Cards - show only when device is expanded */}
            {expandedDeviceIds.includes(device.id) && device.sensors && device.sensors.length > 0 && (
              <div className="grid grid-cols-2 gap-4 mt-2">
                {device.sensors.map((sensor, idx) => (
                  <SensorCard key={idx} sensor={sensor} />
                ))}
              </div>
            )}
          </div>
        ))
      )}

      {/* Modal Thêm/Sửa thiết bị */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center">
          <div className="bg-white p-6 rounded shadow w-96">
            <h3 className="text-xl font-bold mb-4">{editingId ? "Sửa thiết bị" : "Thêm thiết bị"}</h3>
            <input
              type="text"
              placeholder="Mã thiết bị"
              className="w-full border p-2 mb-3 rounded"
              value={newDevice.deviceCode}
              onChange={(e) => setNewDevice({ ...newDevice, deviceCode: e.target.value })}
              disabled={editingId ? true : false}
            />
            <input
              type="text"
              placeholder="Tên thiết bị"
              className="w-full border p-2 mb-3 rounded"
              value={newDevice.name}
              onChange={(e) => setNewDevice({ ...newDevice, name: e.target.value })}
            />
            <input
              type="text"
              placeholder="Vị trí"
              className="w-full border p-2 mb-3 rounded"
              value={newDevice.location}
              onChange={(e) => setNewDevice({ ...newDevice, location: e.target.value })}
            />
            <select
              className="w-full border p-2 mb-3 rounded"
              value={newDevice.status}
              onChange={(e) => setNewDevice({ ...newDevice, status: e.target.value })}
            >
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
            </select>
            <div className="flex justify-end space-x-2">
              <button
                className="px-4 py-2 bg-gray-500 text-white rounded"
                onClick={() => setShowModal(false)}
              >
                Hủy
              </button>
              <button
                className="px-4 py-2 bg-blue-600 text-white rounded"
                onClick={handleAddDevice}
              >
                {editingId ? "Cập nhật" : "Thêm"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
