import axiosClient from "./axiosClient";

const deviceapi = {
  // Get all devices
  getAll: () => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.get("/admin/devices", {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Filter devices with pagination
  filterDevices: (params) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.get("/admin/devices/filter", {
      params,
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Get single device by ID
  getDeviceById: (id) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.get(`/admin/devices/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Get users assigned to device
  getUsersOfDevice: (id) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.get(`/admin/devices/${id}/users`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Update device
  updateDevice: (id, data) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.put(`/admin/devices/${id}`, data, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Delete device
  deleteDevice: (id) => {
    const token = localStorage.getItem("accessToken");
    return axiosClient.delete(`/admin/devices/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
};

export default deviceapi;
