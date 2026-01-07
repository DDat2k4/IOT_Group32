import axiosClient from "./axiosClient";

const authApi = {
  register: (data) => {

    return axiosClient.post("/auth/register", data);
  },

  login: (data) => {
    return axiosClient.post("/auth/login", data, { withCredentials: true })
      .catch(error => {
        // Handle 403 Forbidden (non-admin user)
        if (error.response?.status === 403) {
          throw new Error(error.response?.data?.message || "Only admin users can log in");
        }
        throw error;
      });
  },

  logout: (data) => {
    return axiosClient.post("/auth/logout", data, { withCredentials: true });
  },
  changePassword: (data) => {
    return axiosClient.post("/auth/change-password", data);
  }
};

export default authApi;
