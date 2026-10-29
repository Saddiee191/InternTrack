import api from './api';
import type { ApiResponse, Opportunity, EligibilityResult, Application } from '../types';

export const opportunityService = {
  async list(filters?: {
    search?: string;
    location?: string;
    workplaceType?: string;
    employmentType?: string;
    verificationStatus?: string;
    onlyActive?: boolean;
  }): Promise<Opportunity[]> {
    const res = await api.get<ApiResponse<Opportunity[]>>('/opportunities', { params: filters });
    return res.data.data;
  },

  async getById(id: number): Promise<Opportunity> {
    const res = await api.get<ApiResponse<Opportunity>>(`/opportunities/${id}`);
    return res.data.data;
  },

  async checkEligibility(id: number): Promise<EligibilityResult> {
    const res = await api.get<ApiResponse<EligibilityResult>>(`/opportunities/${id}/eligibility`);
    return res.data.data;
  },

  async apply(id: number): Promise<Application> {
    const res = await api.post<ApiResponse<Application>>(`/opportunities/${id}/apply`);
    return res.data.data;
  },

  async save(id: number): Promise<void> {
    await api.post(`/opportunities/${id}/save`);
  },

  async unsave(id: number): Promise<void> {
    await api.delete(`/opportunities/${id}/save`);
  },

  async getSaved(): Promise<Opportunity[]> {
    const res = await api.get<ApiResponse<Opportunity[]>>('/opportunities/saved');
    return res.data.data;
  },

  async report(id: number, reason: string): Promise<void> {
    await api.post(`/opportunities/${id}/report`, { reason });
  },

  async create(data: Partial<Opportunity>): Promise<Opportunity> {
    const res = await api.post<ApiResponse<Opportunity>>('/opportunities', data);
    return res.data.data;
  },

  async update(id: number, data: Partial<Opportunity>): Promise<Opportunity> {
    const res = await api.put<ApiResponse<Opportunity>>(`/opportunities/${id}`, data);
    return res.data.data;
  },

  async delete(id: number): Promise<void> {
    await api.delete(`/opportunities/${id}`);
  },
};
