import api from "./Axios";

export const uploadExcel = (file: File) => {
  const formData = new FormData();
  formData.append("file", file);

  return api.post("/bulk/upload-async", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};