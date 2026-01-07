import { useState, useEffect } from "react";
import useradminapi from "../api/useradminapi";

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [filters, setFilters] = useState({ username: "", fullName: "", email: "", role: "" });
  const [page, setPage] = useState(0);
  const [newUser, setNewUser] = useState({ username: "", fullName: "", email: "", role: "USER" });

  // Fetch users on mount and when filters/page change
  useEffect(() => {
    fetchUsers();
  }, [filters, page]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await useradminapi.filterUsers({ ...filters, page, size: 10 });
      setUsers(res.data.content || res.data);
    } catch (err) {
      console.error("Error fetching users:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddUser = async () => {
    if (!newUser.username || !newUser.fullName || !newUser.email) return;
    try {
      await useradminapi.updateUser(newUser.id || 0, newUser);
      fetchUsers();
      setNewUser({ username: "", fullName: "", email: "", role: "USER" });
      setShowModal(false);
      setEditingId(null);
    } catch (err) {
      console.error("Error adding/updating user:", err);
    }
  };

  const handleEdit = async (user) => {
    setEditingId(user.id);
    setNewUser(user);
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc muốn xóa người dùng này?")) {
      try {
        await useradminapi.deleteUser(id);
        fetchUsers();
      } catch (err) {
        console.error("Error deleting user:", err);
      }
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">Quản lý người dùng</h2>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 mb-4">
        <input
          type="text"
          placeholder="Tìm theo username"
          className="border p-2 rounded"
          value={filters.username}
          onChange={(e) => { setFilters({ ...filters, username: e.target.value }); setPage(0); }}
        />
        <input
          type="text"
          placeholder="Tìm theo tên đầy đủ"
          className="border p-2 rounded"
          value={filters.fullName}
          onChange={(e) => { setFilters({ ...filters, fullname: e.target.value }); setPage(0); }}
        />
        <input
          type="email"
          placeholder="Tìm theo email"
          className="border p-2 rounded"
          value={filters.email}
          onChange={(e) => { setFilters({ ...filters, email: e.target.value }); setPage(0); }}
        />
        <select
          className="border p-2 rounded"
          value={filters.role}
          onChange={(e) => { setFilters({ ...filters, role: e.target.value }); setPage(0); }}
        >
          <option value="">Tất cả role</option>
          <option value="ADMIN">ADMIN</option>
          <option value="USER">USER</option>
        </select>
      </div>

      {/* Table user */}
      {loading ? (
        <p>Đang tải...</p>
      ) : (
        <table className="w-full table-auto bg-white rounded shadow overflow-hidden">
          <thead className="bg-gray-200">
            <tr>
              <th className="px-4 py-2">Username</th>
              <th className="px-4 py-2">Tên đầy đủ</th>
              <th className="px-4 py-2">Email</th>
              <th className="px-4 py-2">Role</th>
              <th className="px-4 py-2">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id} className="text-center border-b">
                <td className="px-4 py-2">{user.username}</td>
                <td className="px-4 py-2">{user.fullName}</td>
                <td className="px-4 py-2">{user.email}</td>
                <td className="px-4 py-2">{user.role}</td>
                <td className="px-4 py-2 space-x-2">
                  <button className="px-2 py-1 bg-yellow-500 text-white rounded" onClick={() => handleEdit(user)}>Edit</button>
                  <button className="px-2 py-1 bg-red-500 text-white rounded" onClick={() => handleDelete(user.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Modal thêm/sửa người dùng */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center">
          <div className="bg-white p-6 rounded shadow w-96">
            <h3 className="text-xl font-bold mb-4">{editingId ? "Sửa người dùng" : "Thêm người dùng"}</h3>
            <input
              type="text"
              placeholder="Username"
              className="w-full border p-2 mb-3 rounded"
              value={newUser.username}
              onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
            />
            <input
              type="text"
              placeholder="Tên đầy đủ"
              className="w-full border p-2 mb-3 rounded"
              value={newUser.fullName}
              onChange={(e) => setNewUser({ ...newUser, fullName: e.target.value })}
            />
            <input
              type="email"
              placeholder="Email"
              className="w-full border p-2 mb-3 rounded"
              value={newUser.email}
              onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
            />
            <select
              className="w-full border p-2 mb-3 rounded"
              value={newUser.role}
              onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
            >
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
            <div className="flex justify-end space-x-2">
              <button className="px-4 py-2 bg-gray-500 text-white rounded" onClick={() => setShowModal(false)}>Hủy</button>
              <button className="px-4 py-2 bg-blue-600 text-white rounded" onClick={handleAddUser}>{editingId ? "Cập nhật" : "Thêm"}</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
