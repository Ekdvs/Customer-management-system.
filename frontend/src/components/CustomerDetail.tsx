import type { CustomerResponse } from "../types";
import { Badge } from "./ui";

interface Props {
  customer: CustomerResponse;
}

export default function CustomerDetail({ customer }: Props) {
  const age = customer.dob
    ? Math.floor(
        (Date.now() - new Date(customer.dob).getTime()) /
          (365.25 * 24 * 3600 * 1000)
      )
    : null;

  return (
    <div className="space-y-6">
      {/* Header card */}
      <div className="flex items-center gap-4 p-4 rounded-xl bg-blue-50">
        <div className="w-14 h-14 rounded-full bg-blue-600 flex items-center justify-center text-white text-xl font-bold shrink-0">
          {customer.name.charAt(0).toUpperCase()}
        </div>
        <div>
          <h2 className="text-lg font-semibold text-slate-800">{customer.name}</h2>
          <p className="text-sm text-slate-500">{customer.nic}</p>
          {age && (
            <Badge label={`${age} years old`} variant="blue" />
          )}
        </div>
      </div>

      {/* Details grid */}
      <div className="grid grid-cols-2 gap-4">
        <div className="p-3 rounded-lg bg-slate-50">
          <p className="text-xs text-slate-400 mb-1">Date of Birth</p>
          <p className="text-sm font-medium text-slate-800">{customer.dob}</p>
        </div>
        <div className="p-3 rounded-lg bg-slate-50">
          <p className="text-xs text-slate-400 mb-1">NIC Number</p>
          <p className="text-sm font-medium text-slate-800">{customer.nic}</p>
        </div>
      </div>

      {/* Mobile numbers */}
      {customer.mobileNumbers?.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            Mobile Numbers
          </p>
          <div className="flex flex-wrap gap-2">
            {customer.mobileNumbers.map((m, i) => (
              <span
                key={i}
                className="inline-flex items-center gap-1.5 rounded-full bg-slate-100 px-3 py-1 text-sm text-slate-700"
              >
                <svg className="w-3.5 h-3.5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                </svg>
                {m}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Addresses */}
      {customer.addresses?.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            Addresses
          </p>
          <div className="space-y-2">
            {customer.addresses.map((addr, i) => (
              <div key={i} className="rounded-lg border border-slate-200 p-3 text-sm">
                <p className="font-medium text-slate-800">{addr.line1}</p>
                {addr.line2 && <p className="text-slate-500">{addr.line2}</p>}
                {(addr.cityName || addr.countryName) && (
                  <p className="text-slate-400 mt-1 text-xs">
                    {[addr.cityName, addr.countryName].filter(Boolean).join(", ")}
                  </p>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Family members */}
      {customer.familyMembers?.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
            Family Members
          </p>
          <div className="space-y-2">
            {customer.familyMembers.map((fm) => (
              <div
                key={fm.id}
                className="flex items-center gap-3 rounded-lg bg-slate-50 px-3 py-2"
              >
                <div className="w-8 h-8 rounded-full bg-slate-300 flex items-center justify-center text-slate-600 text-sm font-bold shrink-0">
                  {fm.name.charAt(0).toUpperCase()}
                </div>
                <div>
                  <p className="text-sm font-medium text-slate-800">{fm.name}</p>
                  <p className="text-xs text-slate-400">{fm.nic}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
