import api from './api';
import type { ApiResponse, CommunityPost, PostComment } from '../types';

export const communityService = {
  async listPosts(category?: string, search?: string): Promise<CommunityPost[]> {
    const res = await api.get<ApiResponse<CommunityPost[]>>('/community/posts', {
      params: { category, search },
    });
    return res.data.data;
  },

  async getPost(id: number): Promise<CommunityPost> {
    const res = await api.get<ApiResponse<CommunityPost>>(`/community/posts/${id}`);
    return res.data.data;
  },

  async createPost(data: { title: string; category: string; content: string; companyName?: string; roleTitle?: string }): Promise<CommunityPost> {
    const res = await api.post<ApiResponse<CommunityPost>>('/community/posts', data);
    return res.data.data;
  },

  async toggleVote(id: number): Promise<boolean> {
    const res = await api.post<ApiResponse<{ hasUpvoted: boolean }>>(`/community/posts/${id}/vote`);
    return res.data.data.hasUpvoted;
  },

  async toggleBookmark(id: number): Promise<boolean> {
    const res = await api.post<ApiResponse<{ isSaved: boolean }>>(`/community/posts/${id}/bookmark`);
    return res.data.data.isSaved;
  },

  async getComments(postId: number): Promise<PostComment[]> {
    const res = await api.get<ApiResponse<PostComment[]>>(`/community/posts/${postId}/comments`);
    return res.data.data;
  },

  async addComment(postId: number, content: string): Promise<number> {
    const res = await api.post<ApiResponse<number>>(`/community/posts/${postId}/comments`, { content });
    return res.data.data;
  },

  async reportPost(postId: number, reason: string): Promise<void> {
    await api.post(`/community/posts/${postId}/report`, { reason });
  },
};
