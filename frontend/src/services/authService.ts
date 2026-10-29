import api from './api';
import type { ApiResponse, AuthResponse, StudentProfile, MentorProfile } from '../types';

export const authService = {
  async register(data: Record<string, unknown>): Promise<AuthResponse> {
    const res = await api.post<ApiResponse<AuthResponse>>('/auth/register', data);
    return res.data.data;
  },

  async login(credentials: { email: string; password: string }): Promise<AuthResponse> {
    const res = await api.post<ApiResponse<AuthResponse>>('/auth/login', credentials);
    return res.data.data;
  },

  async logout(): Promise<void> {
    await api.post('/auth/logout');
  },

  async getCurrentUser(): Promise<AuthResponse> {
    const res = await api.get<ApiResponse<AuthResponse>>('/auth/me');
    return res.data.data;
  },

  async changePassword(data: { currentPassword: string; newPassword: string }): Promise<void> {
    await api.post('/auth/change-password', data);
  },

  async deleteAccount(): Promise<void> {
    await api.delete('/auth/account');
  },

  async getProfile(): Promise<{ user: unknown; studentProfile?: StudentProfile; mentorProfile?: MentorProfile }> {
    const res = await api.get<ApiResponse<{ user: unknown; studentProfile?: StudentProfile; mentorProfile?: MentorProfile }>>('/profile');
    return res.data.data;
  },

  async updateProfile(data: Record<string, unknown>): Promise<{ user: unknown; studentProfile?: StudentProfile; mentorProfile?: MentorProfile }> {
    const res = await api.put<ApiResponse<{ user: unknown; studentProfile?: StudentProfile; mentorProfile?: MentorProfile }>>('/profile', data);
    return res.data.data;
  },

  async listMentors(search?: string): Promise<MentorProfile[]> {
    const res = await api.get<ApiResponse<MentorProfile[]>>('/mentors', { params: { search } });
    return res.data.data;
  },

  async getMentorById(id: number): Promise<MentorProfile> {
    const res = await api.get<ApiResponse<MentorProfile>>(`/mentors/${id}`);
    return res.data.data;
  },
};
