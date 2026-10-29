import api from './api';
import type { ApiResponse, ResumeFile, ResumeAnalysis } from '../types';

export const resumeService = {
  async upload(file: File): Promise<ResumeFile> {
    const formData = new FormData();
    formData.append('file', file);
    const res = await api.post<ApiResponse<ResumeFile>>('/resumes/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data.data;
  },

  async getLatest(): Promise<ResumeFile | null> {
    try {
      const res = await api.get<ApiResponse<ResumeFile>>('/resumes/latest');
      return res.data.data;
    } catch {
      return null;
    }
  },

  async listFiles(): Promise<ResumeFile[]> {
    const res = await api.get<ApiResponse<ResumeFile[]>>('/resumes/files');
    return res.data.data;
  },

  async deleteFile(id: number): Promise<void> {
    await api.delete(`/resumes/files/${id}`);
  },

  async analyze(resumeFileId: number, jobTitle?: string, jobDescription?: string): Promise<ResumeAnalysis> {
    const res = await api.post<ApiResponse<ResumeAnalysis>>('/resumes/analyze', {
      resumeFileId, jobTitle, jobDescription,
    });
    return res.data.data;
  },

  async listAnalyses(): Promise<ResumeAnalysis[]> {
    const res = await api.get<ApiResponse<ResumeAnalysis[]>>('/resumes/history');
    return res.data.data;
  },

  async getAnalysis(id: number): Promise<ResumeAnalysis> {
    const res = await api.get<ApiResponse<ResumeAnalysis>>(`/resumes/analyses/${id}`);
    return res.data.data;
  },
};
