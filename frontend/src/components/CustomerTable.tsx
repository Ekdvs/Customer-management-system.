import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCustomers, getCustomerById } from "../api/customer.api";

import CustomerDetail from "./CustomerDetail";
import { useToast } from "../hooks/useToast";
import type { CustomerResponse, CustomerSummary } from "../types";
import { Badge, Button, Modal, Spinner } from "./ui";

export default function CustomerTable() {
    const [customers, setCustomers] = useState<CustomerSummary[]>([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState("");
    const [debouncedSearch, setDebouncedSearch] = useState("");
    const [viewCustomer, setViewCustomer] = useState<CustomerResponse | null>(null);
    const [viewLoading, setViewLoading] = useState(false);
    const debounceRef = useRef<ReturnType<typeof setTimeout>>();
    const { showToast } = useToast();
    const navigate = useNavigate();

    // Debounce search
    useEffect(() => {
        clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(() => setDebouncedSearch(search), 350);
        return () => clearTimeout(debounceRef.current);
    }, [search]);

    const loadCustomers = useCallback(async () => {
        setLoading(true);
        try {
            const res = await getCustomers(debouncedSearch || undefined);
            console.log(res.data);
            const list: CustomerSummary[] = Array.isArray(res.data)
                ? res.data
                : res.data.content ?? [];
            setCustomers(list);
        } catch {
            showToast("Failed to load customers", "error");
        } finally {
            setLoading(false);
        }
    }, [debouncedSearch, showToast]);

    useEffect(() => {
        loadCustomers();
    }, [loadCustomers]);

    const handleView = async (id: number) => {
        setViewLoading(true);
        try {
            const res = await getCustomerById(id);
            setViewCustomer(res.data);
        } catch {
            showToast("Failed to load customer details", "error");
        } finally {
            setViewLoading(false);
        }
    };

    return (
        <>
            <div className="p-8">
                {/* Page header */}
                <div className="flex items-center justify-between mb-6">
                    <div>
                        <h1 className="text-2xl font-bold text-slate-800">Customers</h1>
                        <p className="text-sm text-slate-400 mt-0.5">
                            {loading ? "Loading…" : `${customers.length} record${customers.length !== 1 ? "s" : ""}`}
                        </p>
                    </div>
                    <Button onClick={() => navigate("/create")}>
                        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                        </svg>
                        Add Customer
                    </Button>
                </div>

                {/* Search bar */}
                <div className="relative mb-4">
                    <svg
                        className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400"
                        fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}
                    >
                        <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-4.35-4.35M17 11A6 6 0 105 11a6 6 0 0012 0z" />
                    </svg>
                    <input
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        placeholder="Search by name or NIC…"
                        className="w-full max-w-sm pl-9 pr-4 py-2.5 rounded-lg border border-slate-200 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 bg-white"
                    />
                </div>

                {/* Table */}
                <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
                    {loading ? (
                        <div className="flex items-center justify-center py-20">
                            <Spinner size="lg" />
                        </div>
                    ) : customers.length === 0 ? (
                        <div className="flex flex-col items-center justify-center py-20 text-center">
                            <div className="w-14 h-14 rounded-full bg-slate-100 flex items-center justify-center mb-3">
                                <svg className="w-7 h-7 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
                                </svg>
                            </div>
                            <p className="text-slate-500 font-medium">No customers found</p>
                            <p className="text-slate-400 text-sm mt-1">
                                {search ? "Try a different search term" : "Add your first customer to get started"}
                            </p>
                        </div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-sm">
                                <thead>
                                    <tr className="border-b border-slate-100 bg-slate-50">
                                        <th className="px-5 py-3 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">
                                            Customer
                                        </th>
                                        <th className="px-5 py-3 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">
                                            NIC
                                        </th>
                                        <th className="px-5 py-3 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">
                                            Date of Birth
                                        </th>
                                        <th className="px-5 py-3 text-left text-xs font-semibold text-slate-400 uppercase tracking-wider">
                                            Mobiles
                                        </th>
                                        <th className="px-5 py-3 text-right text-xs font-semibold text-slate-400 uppercase tracking-wider">
                                            Actions
                                        </th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-50">
                                    {customers.map((c) => (
                                        <tr
                                            key={c.id}
                                            className="hover:bg-slate-50 transition-colors group"
                                        >
                                            <td className="px-5 py-3.5">
                                                <div className="flex items-center gap-3">
                                                    <div className="w-9 h-9 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 text-sm font-bold shrink-0">
                                                        {c.name.charAt(0).toUpperCase()}
                                                    </div>
                                                    <span className="font-medium text-slate-800">{c.name}</span>
                                                </div>
                                            </td>
                                            <td className="px-5 py-3.5 text-slate-600 font-mono text-xs">
                                                {c.nic}
                                            </td>
                                            <td className="px-5 py-3.5 text-slate-600">{c.dob}</td>
                                            <td className="px-5 py-3.5">
                                                {c.mobileCount > 0 ? (
                                                    <Badge
                                                        label={`${c.mobileCount} number${c.mobileCount > 1 ? "s" : ""}`}
                                                        variant="gray"
                                                    />
                                                ) : (
                                                    <span className="text-slate-300 text-xs">—</span>
                                                )}
                                            </td>
                                            <td className="px-5 py-3.5">
                                                <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                                    <button
                                                        onClick={() => handleView(c.id)}
                                                        className="rounded-lg bg-slate-100 hover:bg-slate-200 px-3 py-1.5 text-xs font-medium text-slate-600 transition-colors"
                                                    >
                                                        View
                                                    </button>
                                                    <button
                                                        onClick={() => navigate(`/edit/${c.id}`)}
                                                        className="rounded-lg bg-blue-50 hover:bg-blue-100 px-3 py-1.5 text-xs font-medium text-blue-600 transition-colors"
                                                    >
                                                        Edit
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>

            {/* View Modal */}
            {(viewCustomer || viewLoading) && (
                <Modal
                    title={viewLoading ? "Loading…" : viewCustomer!.name}
                    onClose={() => setViewCustomer(null)}
                    size="lg"
                >
                    {viewLoading ? (
                        <div className="flex justify-center py-10">
                            <Spinner size="lg" />
                        </div>
                    ) : (
                        viewCustomer && <CustomerDetail customer={viewCustomer} />
                    )}
                </Modal>
            )}
        </>
    );
}
