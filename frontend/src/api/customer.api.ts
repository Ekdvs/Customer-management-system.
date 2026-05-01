import type { CustomerRequest } from "../types";
import api from "./Axios";


export const getCustomers = (search?: string) =>
  api.get("/customers", { params: search ? { search } : {} });

export const getCustomersPaged = (page: number, size = 20) =>
  api.get("/customers", { params: { page, size } });

export const getCustomerById = (id: number) => api.get(`/customers/${id}`);

export const createCustomer = (data: CustomerRequest) =>
  api.post("/customers", data);

export const updateCustomer = (id: number, data: CustomerRequest) =>
  api.put(`/customers/${id}`, data);
