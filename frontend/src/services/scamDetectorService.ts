import api from './api';
import type { ApiResponse, ScamScan } from '../types';

export const scamDetectorService = {
  async scan(data: {
    companyName?: string;
    jobTitle?: string;
    recruiterContact?: string;
    applicationUrl?: string;
    stipendSalary?: string;
    jobDescription?: string;
    recruiterMessage?: string;
  }): Promise<ScamScan> {
    const res = await api.post<ApiResponse<ScamScan>>('/scam-scans', data);
    return res.data.data;
  },

  async getHistory(): Promise<ScamScan[]> {
    const res = await api.get<ApiResponse<ScamScan[]>>('/scam-scans/history');
    return res.data.data;
  },

  async getById(id: number): Promise<ScamScan> {
    const res = await api.get<ApiResponse<ScamScan>>(`/scam-scans/${id}`);
    return res.data.data;
  },

  async reportScam(id: number): Promise<void> {
    await api.post(`/scam-scans/${id}/report`);
  },
};
