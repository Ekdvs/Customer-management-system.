export interface AddressDTO {
  id?: number;
  line1: string;
  line2?: string;
  cityId?: number;
  countryId?: number;
  cityName?: string;
  countryName?: string;
}

export interface CustomerSummary {
  id: number;
  name: string;
  nic: string;
  dob: string;
  mobileNumbers?: string[];
}

export interface CustomerResponse {
  id: number;
  name: string;
  nic: string;
  dob: string;
  mobileNumbers: string[];
  addresses: AddressDTO[];
  familyMembers: CustomerSummary[];
}

export interface CustomerRequest {
  name: string;
  dob: string;
  nic: string;
  mobileNumbers?: string[];
  addresses?: AddressDTO[];
  familyMemberIds?: number[];
}

export interface BulkImportResult {
  status: string;
  totalRows: number;
  successCount: number;
  failureCount: number;
  processingTimeMs: number;
  errors: string[];
}
