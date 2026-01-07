import axiosClient from "./axiosClient";

const useradminapi = {
  // Filter users with pagination
  filterUsers: (params) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.get("/admin/users", { 
      params,
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Get single user by ID
  getUserById: (userId) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.get(`/admin/user/${userId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Update user
  updateUser: (userId, data) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.put(`/admin/user/${userId}`, data, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Delete user
  deleteUser: (userId) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.delete(`/admin/user/${userId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
};

export default useradminapi;
