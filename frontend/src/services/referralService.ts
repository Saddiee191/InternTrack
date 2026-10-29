import api from './api';
import type { ApiResponse, ReferralRequest } from '../types';

export const referralService = {
  async listMyRequests(): Promise<ReferralRequest[]> {
    const res = await api.get<ApiResponse<ReferralRequest[]>>('/referrals/student');
    return res.data.data;
  },

  async listReceivedRequests(): Promise<ReferralRequest[]> {
    const res = await api.get<ApiResponse<ReferralRequest[]>>('/referrals/mentor');
    return res.data.data;
  },

  async createRequest(data: {
    mentorId: number;
    opportunityTitle: string;
    companyName: string;
    resumeFileId?: number;
    note?: string;
  }): Promise<ReferralRequest> {
    const res = await api.post<ApiResponse<ReferralRequest>>('/referrals', data);
    return res.data.data;
  },

  async updateStatus(id: number, status: string, mentorResponse?: string): Promise<void> {
    await api.patch(`/referrals/${id}/status`, { status, mentorResponse });
  },
};
