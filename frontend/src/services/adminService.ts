import api from './api';
import type { ApiResponse, AdminMetrics, ContentReport, OpportunityReport, AuditLog, User, ScamScan } from '../types';

export const adminService = {
  async getMetrics(): Promise<AdminMetrics> {
    const res = await api.get<ApiResponse<AdminMetrics>>('/admin/metrics');
    return res.data.data;
  },

  async getContentReports(): Promise<ContentReport[]> {
    const res = await api.get<ApiResponse<ContentReport[]>>('/admin/reports/content');
    return res.data.data;
  },

  async resolveContentReport(id: number, status: string, resolutionNotes: string): Promise<void> {
    await api.patch(`/admin/reports/content/${id}/resolve`, { status, resolutionNotes });
  },

  async getOpportunityReports(): Promise<OpportunityReport[]> {
    const res = await api.get<ApiResponse<OpportunityReport[]>>('/admin/reports/opportunities');
    return res.data.data;
  },

  async resolveOpportunityReport(id: number, status: string): Promise<void> {
    await api.patch(`/admin/reports/opportunities/${id}/resolve`, { status });
  },

  async getScamReports(): Promise<ScamScan[]> {
    const res = await api.get<ApiResponse<ScamScan[]>>('/admin/scam-reports');
    return res.data.data;
  },

  async getUsers(search?: string): Promise<User[]> {
    const res = await api.get<ApiResponse<User[]>>('/admin/users', { params: { search, limit: 100 } });
    return res.data.data;
  },

  async updateUserStatus(userId: number, status: string): Promise<void> {
    await api.patch(`/admin/users/${userId}/status`, { status });
  },

  async setOpportunityVerification(opportunityId: number, status: string): Promise<void> {
    await api.patch(`/admin/opportunities/${opportunityId}/verification`, { status });
  },

  async getAuditLogs(): Promise<AuditLog[]> {
    const res = await api.get<ApiResponse<AuditLog[]>>('/admin/audit-logs');
    return res.data.data;
  },
};
