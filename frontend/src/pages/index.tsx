import { useNavigate } from "react-router-dom";
import CustomerTable from "../components/CustomerTable";
import CustomerForm from "../components/CustomerForm";
import BulkUpload from "../components/BulkUpload";
import { createCustomer, updateCustomer, getCustomerById } from "../api/customer.api";

import { useState, useEffect } from "react";
import { useToast } from "../hooks/useToast";
import { Spinner } from "../components/ui";
import { useParams } from "react-router-dom";
import type { CustomerRequest } from "../types";

// ─── Customers List ──────────────────────────────────────────────────────────
export function CustomersPage() {
  return <CustomerTable />;
}

// ─── Create Customer ─────────────────────────────────────────────────────────
export function CreateCustomerPage() {
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const { showToast } = useToast();

  const handleSubmit = async (data: CustomerRequest) => {
    setSubmitting(true);
    try {
      await createCustomer(data);
      showToast("Customer created successfully!", "success");
      navigate("/");
    } catch (err: any) {
      const msg =
        err?.response?.data?.message ??
        err?.response?.data?.error ??
        "Failed to create customer";
      showToast(msg, "error");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="p-8 max-w-2xl">
      <div className="mb-6">
        <button
          onClick={() => navigate("/")}
          className="text-sm text-slate-400 hover:text-slate-600 flex items-center gap-1.5 mb-3"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
          Back to Customers
        </button>
        <h1 className="text-2xl font-bold text-slate-800">Add Customer</h1>
        <p className="text-sm text-slate-400 mt-0.5">Fill in the form below to create a new customer</p>
      </div>
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        <CustomerForm onSubmit={handleSubmit} submitting={submitting} />
      </div>
    </div>
  );
}

// ─── Edit Customer ────────────────────────────────────────────────────────────
export function EditCustomerPage() {
  const { id } = useParams<{ id: string }>();
  const [initial, setInitial] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const { showToast } = useToast();

  useEffect(() => {
    if (!id) return;
    getCustomerById(Number(id))
      .then((r) => {
        const c = r.data;
        setInitial({
          id: c.id,
          name: c.name,
          dob: c.dob,
          nic: c.nic,
          mobileNumbers: c.mobileNumbers ?? [],
          addresses: c.addresses ?? [],
          familyMemberIds: c.familyMembers?.map((fm: any) => fm.id) ?? [],
        });
      })
      .catch(() => showToast("Failed to load customer", "error"))
      .finally(() => setLoading(false));
  }, [id, showToast]);

  const handleSubmit = async (data: CustomerRequest) => {
    setSubmitting(true);
    try {
      await updateCustomer(Number(id), data);
      showToast("Customer updated successfully!", "success");
      navigate("/");
    } catch (err: any) {
      const msg =
        err?.response?.data?.message ??
        err?.response?.data?.error ??
        "Failed to update customer";
      showToast(msg, "error");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="p-8 max-w-2xl">
      <div className="mb-6">
        <button
          onClick={() => navigate("/")}
          className="text-sm text-slate-400 hover:text-slate-600 flex items-center gap-1.5 mb-3"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
          Back to Customers
        </button>
        <h1 className="text-2xl font-bold text-slate-800">Edit Customer</h1>
        <p className="text-sm text-slate-400 mt-0.5">Update the details below</p>
      </div>
      <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
        {initial && (
          <CustomerForm
            initial={initial}
            onSubmit={handleSubmit}
            submitting={submitting}
          />
        )}
      </div>
    </div>
  );
}

// ─── Bulk Upload ──────────────────────────────────────────────────────────────
export function BulkUploadPage() {
  return <BulkUpload />;
}
