import { useEffect, useState } from "react";
import type { AddressDTO, CustomerRequest, CustomerSummary } from "../types";

import { Button, Input } from "./ui";
import { getCustomers } from "../api/customer.api";

interface Props {
  initial?: Partial<CustomerRequest> & { id?: number };
  onSubmit: (data: CustomerRequest) => Promise<void>;
  submitting?: boolean;
}

const emptyAddress = (): AddressDTO => ({
  line1: "",
  line2: "",
  cityId: undefined,
  countryId: undefined,
});

export default function CustomerForm({ initial, onSubmit, submitting }: Props) {
  const [name, setName] = useState(initial?.name ?? "");
  const [dob, setDob] = useState(initial?.dob ?? "");
  const [nic, setNic] = useState(initial?.nic ?? "");
  const [mobiles, setMobiles] = useState<string[]>(initial?.mobileNumbers ?? [""]);
  const [addresses, setAddresses] = useState<AddressDTO[]>(
    initial?.addresses?.length ? initial.addresses : [emptyAddress()]
  );
  const [familyIds, setFamilyIds] = useState<number[]>(initial?.familyMemberIds ?? []);
  const [allCustomers, setAllCustomers] = useState<CustomerSummary[]>([]);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [searchFamily, setSearchFamily] = useState("");

  useEffect(() => {
    getCustomers().then((r) => {
      const list: CustomerSummary[] = Array.isArray(r.data) ? r.data : r.data.content ?? [];
      console.log("Loaded customers for family selection:", list);
      setAllCustomers(list.filter((c) => c.id !== initial?.id));
    });
  }, [initial?.id]);

  // ── Validation ────────────────────────────────────────────────────
  const validate = () => {
    const e: Record<string, string> = {};
    if (!name.trim()) e.name = "Name is required";
    if (!dob) e.dob = "Date of birth is required";
    if (!nic.trim()) e.nic = "NIC is required";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    const payload: CustomerRequest = {
      name: name.trim(),
      dob,
      nic: nic.trim(),
      mobileNumbers: mobiles.filter((m) => m.trim()),
      addresses: addresses.filter((a) => a.line1.trim()),
      familyMemberIds: familyIds,
    };
    await onSubmit(payload);
  };

  // ── Mobile helpers ────────────────────────────────────────────────
  const addMobile = () => setMobiles([...mobiles, ""]);
  const removeMobile = (i: number) => setMobiles(mobiles.filter((_, idx) => idx !== i));
  const updateMobile = (i: number, v: string) =>
    setMobiles(mobiles.map((m, idx) => (idx === i ? v : m)));

  // ── Address helpers ───────────────────────────────────────────────
  const addAddress = () => setAddresses([...addresses, emptyAddress()]);
  const removeAddress = (i: number) =>
    setAddresses(addresses.filter((_, idx) => idx !== i));
  const updateAddress = (i: number, field: keyof AddressDTO, value: string | number) =>
    setAddresses(addresses.map((a, idx) => (idx === i ? { ...a, [field]: value } : a)));

  // ── Family ────────────────────────────────────────────────────────
  const toggleFamily = (id: number) => {
    setFamilyIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const filteredCustomers = allCustomers.filter(
    (c) =>
      c.name.toLowerCase().includes(searchFamily.toLowerCase()) ||
      c.nic.toLowerCase().includes(searchFamily.toLowerCase())
  );

  // ── Render ────────────────────────────────────────────────────────
  return (
    <div className="space-y-8">
      {/* Basic Info */}
      <section>
        <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">
          Basic Information
        </h3>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div className="sm:col-span-2">
            <Input
              label="Full Name"
              required
              placeholder="John Doe"
              value={name}
              onChange={(e) => setName(e.target.value)}
              error={errors.name}
            />
          </div>
          <Input
            label="Date of Birth"
            required
            type="date"
            value={dob}
            onChange={(e) => setDob(e.target.value)}
            error={errors.dob}
          />
          <Input
            label="NIC Number"
            required
            placeholder="123456789V"
            value={nic}
            onChange={(e) => setNic(e.target.value)}
            error={errors.nic}
          />
        </div>
      </section>

      {/* Mobile Numbers */}
      <section>
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">
            Mobile Numbers
          </h3>
          <button
            type="button"
            onClick={addMobile}
            className="text-xs text-blue-600 hover:text-blue-800 font-medium flex items-center gap-1"
          >
            <span className="text-base leading-none">+</span> Add
          </button>
        </div>
        <div className="space-y-2">
          {mobiles.map((m, i) => (
            <div key={i} className="flex gap-2">
              <input
                type="tel"
                placeholder="+94 77 000 0000"
                value={m}
                onChange={(e) => updateMobile(i, e.target.value)}
                className="flex-1 rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
              />
              {mobiles.length > 1 && (
                <button
                  type="button"
                  onClick={() => removeMobile(i)}
                  className="text-slate-400 hover:text-red-500 px-2 text-lg leading-none"
                >
                  ×
                </button>
              )}
            </div>
          ))}
        </div>
      </section>

      {/* Addresses */}
      <section>
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">
            Addresses
          </h3>
          <button
            type="button"
            onClick={addAddress}
            className="text-xs text-blue-600 hover:text-blue-800 font-medium flex items-center gap-1"
          >
            <span className="text-base leading-none">+</span> Add
          </button>
        </div>
        <div className="space-y-4">
          {addresses.map((addr, i) => (
            <div
              key={i}
              className="rounded-xl border border-slate-200 p-4 space-y-3 relative bg-slate-50"
            >
              {addresses.length > 1 && (
                <button
                  type="button"
                  onClick={() => removeAddress(i)}
                  className="absolute top-3 right-3 text-slate-300 hover:text-red-500 text-xl leading-none"
                >
                  ×
                </button>
              )}
              <p className="text-xs font-semibold text-slate-400">
                Address {i + 1}
              </p>
              <input
                placeholder="Address Line 1"
                value={addr.line1}
                onChange={(e) => updateAddress(i, "line1", e.target.value)}
                className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 bg-white"
              />
              <input
                placeholder="Address Line 2 (optional)"
                value={addr.line2 ?? ""}
                onChange={(e) => updateAddress(i, "line2", e.target.value)}
                className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 bg-white"
              />
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs text-slate-500 mb-1 block">City ID</label>
                  <input
                    type="number"
                    placeholder="e.g. 1"
                    value={addr.cityId ?? ""}
                    onChange={(e) =>
                      updateAddress(i, "cityId", Number(e.target.value))
                    }
                    className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 bg-white"
                  />
                </div>
                <div>
                  <label className="text-xs text-slate-500 mb-1 block">Country ID</label>
                  <input
                    type="number"
                    placeholder="e.g. 1"
                    value={addr.countryId ?? ""}
                    onChange={(e) =>
                      updateAddress(i, "countryId", Number(e.target.value))
                    }
                    className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 bg-white"
                  />
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Family Members */}
      <section>
        <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-3">
          Family Members
        </h3>
        <input
          placeholder="Search by name or NIC…"
          value={searchFamily}
          onChange={(e) => setSearchFamily(e.target.value)}
          className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 mb-3"
        />
        <div className="max-h-48 overflow-y-auto rounded-xl border border-slate-200 divide-y divide-slate-100">
          {filteredCustomers.length === 0 ? (
            <p className="text-sm text-slate-400 text-center py-4">
              No customers found
            </p>
          ) : (
            filteredCustomers.map((c) => (
              <label
                key={c.id}
                className="flex items-center gap-3 px-4 py-2.5 hover:bg-slate-50 cursor-pointer"
              >
                <input
                  type="checkbox"
                  checked={familyIds.includes(c.id)}
                  onChange={() => toggleFamily(c.id)}
                  className="rounded text-blue-600"
                />
                <div>
                  <p className="text-sm font-medium text-slate-800">{c.name}</p>
                  <p className="text-xs text-slate-400">{c.nic}</p>
                </div>
              </label>
            ))
          )}
        </div>
        {familyIds.length > 0 && (
          <p className="text-xs text-blue-600 mt-2">
            {familyIds.length} family member{familyIds.length > 1 ? "s" : ""} selected
          </p>
        )}
      </section>

      {/* Submit */}
      <div className="flex justify-end pt-2 border-t border-slate-100">
        <Button onClick={handleSubmit} disabled={submitting}>
          {submitting ? (
            <span className="flex items-center gap-2">
              <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2}>
                <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" />
              </svg>
              Saving…
            </span>
          ) : (
            "Save Customer"
          )}
        </Button>
      </div>
    </div>
  );
}
