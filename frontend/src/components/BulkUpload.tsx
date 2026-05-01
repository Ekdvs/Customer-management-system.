import { useRef, useState } from "react";
import { uploadExcel } from "../api/bulk.api";

import { Button, Badge, Spinner } from "./ui";
import { useToast } from "../hooks/useToast";
import type { BulkImportResult } from "../types";

export default function BulkUpload() {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [result, setResult] = useState<BulkImportResult | null>(null);
  const [dragOver, setDragOver] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const { showToast } = useToast();

  const handleFile = (f: File) => {
    if (!f.name.match(/\.(xlsx|xls)$/i)) {
      showToast("Only .xlsx and .xls files are supported", "error");
      return;
    }
    setFile(f);
    setResult(null);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(false);
    const f = e.dataTransfer.files[0];
    if (f) handleFile(f);
  };

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    setResult(null);
    try {
      const res = await uploadExcel(file);
      setResult(res.data);
      showToast(
        res.data.status === "SUCCESS"
          ? "Import completed successfully!"
          : "Import completed with some errors",
        res.data.status === "SUCCESS" ? "success" : "error"
      );
    } catch (err: any) {
      showToast(
        err?.response?.data?.message ?? "Upload failed. Please try again.",
        "error"
      );
    } finally {
      setUploading(false);
    }
  };

  const statusVariant = (status: string): "green" | "red" | "yellow" => {
    if (status === "SUCCESS") return "green";
    if (status === "FAILED") return "red";
    return "yellow";
  };

  return (
    <div className="p-8 max-w-2xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Bulk Import</h1>
        <p className="text-sm text-slate-400 mt-0.5">
          Upload an Excel file to import multiple customers at once
        </p>
      </div>

      {/* Template hint */}
      <div className="mb-6 rounded-xl bg-blue-50 border border-blue-100 p-4 flex gap-3">
        <svg className="w-5 h-5 text-blue-500 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <div className="text-sm text-blue-700">
          <p className="font-semibold">Excel Format</p>
          <p className="mt-1 text-blue-600">
            Column A: <strong>Name</strong> &nbsp;|&nbsp; Column B:{" "}
            <strong>Date of Birth</strong> (yyyy-MM-dd) &nbsp;|&nbsp; Column C:{" "}
            <strong>NIC</strong>
          </p>
          <p className="mt-0.5 text-blue-500">Row 1 is treated as a header and will be skipped.</p>
        </div>
      </div>

      {/* Drop zone */}
      <div
        onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
        onDragLeave={() => setDragOver(false)}
        onDrop={handleDrop}
        onClick={() => inputRef.current?.click()}
        className={`rounded-2xl border-2 border-dashed transition-colors cursor-pointer p-10 flex flex-col items-center text-center ${
          dragOver
            ? "border-blue-400 bg-blue-50"
            : file
            ? "border-emerald-400 bg-emerald-50"
            : "border-slate-200 bg-white hover:border-blue-300 hover:bg-blue-50"
        }`}
      >
        <input
          ref={inputRef}
          type="file"
          accept=".xlsx,.xls"
          className="hidden"
          onChange={(e) => {
            const f = e.target.files?.[0];
            if (f) handleFile(f);
          }}
        />
        {file ? (
          <>
            <div className="w-12 h-12 rounded-full bg-emerald-100 flex items-center justify-center mb-3">
              <svg className="w-6 h-6 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <p className="font-semibold text-slate-800">{file.name}</p>
            <p className="text-sm text-slate-400 mt-1">
              {(file.size / 1024).toFixed(1)} KB — click to change
            </p>
          </>
        ) : (
          <>
            <div className="w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center mb-3">
              <svg className="w-6 h-6 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
              </svg>
            </div>
            <p className="font-medium text-slate-600">Drop your file here</p>
            <p className="text-sm text-slate-400 mt-1">or click to browse</p>
            <p className="text-xs text-slate-300 mt-2">.xlsx and .xls accepted</p>
          </>
        )}
      </div>

      {/* Upload button */}
      <div className="mt-4 flex items-center gap-3">
        <Button onClick={handleUpload} disabled={!file || uploading}>
          {uploading ? (
            <>
              <Spinner size="sm" />
              Importing…
            </>
          ) : (
            <>
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
              Start Import
            </>
          )}
        </Button>
        {file && !uploading && (
          <Button
            variant="ghost"
            onClick={() => { setFile(null); setResult(null); }}
          >
            Clear
          </Button>
        )}
        {uploading && (
          <p className="text-sm text-slate-400">
            Large files may take several minutes. Please wait…
          </p>
        )}
      </div>

      {/* Result */}
      {result && (
        <div className="mt-8 rounded-2xl border border-slate-100 bg-white shadow-sm overflow-hidden">
          <div className="px-5 py-4 border-b border-slate-100 flex items-center justify-between">
            <h3 className="font-semibold text-slate-800">Import Result</h3>
            <Badge label={result.status} variant={statusVariant(result.status)} />
          </div>
          <div className="grid grid-cols-3 divide-x divide-slate-100">
            <div className="p-4 text-center">
              <p className="text-2xl font-bold text-slate-800">{result.totalRows}</p>
              <p className="text-xs text-slate-400 mt-1">Total Rows</p>
            </div>
            <div className="p-4 text-center">
              <p className="text-2xl font-bold text-emerald-600">{result.successCount}</p>
              <p className="text-xs text-slate-400 mt-1">Imported</p>
            </div>
            <div className="p-4 text-center">
              <p className="text-2xl font-bold text-red-500">{result.failureCount}</p>
              <p className="text-xs text-slate-400 mt-1">Failed</p>
            </div>
          </div>
          <div className="px-5 py-3 border-t border-slate-100 bg-slate-50">
            <p className="text-xs text-slate-400">
              Processed in {(result.processingTimeMs / 1000).toFixed(1)}s
            </p>
          </div>

          {/* Errors */}
          {result.errors?.length > 0 && (
            <div className="border-t border-slate-100">
              <div className="px-5 py-3 flex items-center justify-between">
                <p className="text-sm font-semibold text-red-600">
                  Errors ({result.errors.length})
                </p>
              </div>
              <div className="max-h-56 overflow-y-auto">
                {result.errors.map((e, i) => (
                  <div
                    key={i}
                    className="px-5 py-2 text-xs text-red-600 border-t border-slate-50 flex items-start gap-2"
                  >
                    <span className="text-red-300 mt-0.5 shrink-0">✕</span>
                    {e}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
