import { BrowserRouter, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import { ToastProvider } from "./hooks/useToast";
import {
  CustomersPage,
  CreateCustomerPage,
  EditCustomerPage,
  BulkUploadPage,
} from "./pages/index";
import "./app.css";

function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
        <Layout>
          <Routes>
            <Route path="/" element={<CustomersPage />} />
            <Route path="/create" element={<CreateCustomerPage />} />
            <Route path="/edit/:id" element={<EditCustomerPage />} />
            <Route path="/bulk" element={<BulkUploadPage />} />
          </Routes>
        </Layout>
      </ToastProvider>
    </BrowserRouter>
  );
}

export default App;
