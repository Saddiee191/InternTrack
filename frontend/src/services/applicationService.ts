import api from './api';
import type { ApiResponse, Application, ApplicationStats, ApplicationStatusHistory, ApplicationReminder } from '../types';

export const applicationService = {
  async list(status?: string, search?: string, sortBy?: string): Promise<Application[]> {
    const res = await api.get<ApiResponse<Application[]>>('/applications', {
      params: { status, search, sortBy },
    });
    return res.data.data;
  },

  async getById(id: number): Promise<Application> {
    const res = await api.get<ApiResponse<Application>>(`/applications/${id}`);
    return res.data.data;
  },

  async getHistory(id: number): Promise<ApplicationStatusHistory[]> {
    const res = await api.get<ApiResponse<ApplicationStatusHistory[]>>(`/applications/${id}/history`);
    return res.data.data;
  },

  async create(data: Partial<Application>): Promise<Application> {
    const res = await api.post<ApiResponse<Application>>('/applications', data);
    return res.data.data;
  },

  async update(id: number, data: Partial<Application>): Promise<Application> {
    const res = await api.put<ApiResponse<Application>>(`/applications/${id}`, data);
    return res.data.data;
  },

  async updateStatus(id: number, status: string, notes?: string): Promise<Application> {
    const res = await api.patch<ApiResponse<Application>>(`/applications/${id}/status`, { status, notes });
    return res.data.data;
  },

  async delete(id: number): Promise<void> {
    await api.delete(`/applications/${id}`);
  },

  async getStats(): Promise<ApplicationStats> {
    const res = await api.get<ApiResponse<ApplicationStats>>('/applications/stats');
    return res.data.data;
  },

  async getReminders(): Promise<ApplicationReminder[]> {
    const res = await api.get<ApiResponse<ApplicationReminder[]>>('/applications/reminders');
    return res.data.data;
  },

  async createReminder(data: { applicationId: number; reminderType: string; reminderDate: string; notes?: string }): Promise<number> {
    const res = await api.post<ApiResponse<number>>('/applications/reminders', data);
    return res.data.data;
  },

  async dismissReminder(id: number): Promise<void> {
    await api.patch(`/applications/reminders/${id}/dismiss`);
  },
};
