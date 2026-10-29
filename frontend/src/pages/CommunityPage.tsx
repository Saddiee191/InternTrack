import { useEffect, useState, type FormEvent } from 'react';
import { communityService } from '../services/communityService';
import { useAuth } from '../context/AuthContext';
import type { CommunityPost, PostComment } from '../types';
import {
  Plus, Search, ThumbsUp, MessageSquare, Bookmark, BookmarkCheck, Send, X,
  CheckCircle2, User
} from 'lucide-react';

const CATEGORIES = ['ALL', 'INTERVIEW_EXPERIENCE', 'CAREER_GUIDANCE', 'GENERAL', 'COMPANY_REVIEW', 'RESOURCE'];
const CATEGORY_LABELS: Record<string, string> = {
  ALL: 'All', INTERVIEW_EXPERIENCE: 'Interview', CAREER_GUIDANCE: 'Career', GENERAL: 'General',
  COMPANY_REVIEW: 'Company Review', RESOURCE: 'Resources',
};
const CATEGORY_COLORS: Record<string, string> = {
  INTERVIEW_EXPERIENCE: 'bg-violet-50 text-violet-700',
  CAREER_GUIDANCE: 'bg-blue-50 text-blue-700',
  GENERAL: 'bg-slate-100 text-slate-600',
  COMPANY_REVIEW: 'bg-amber-50 text-amber-700',
  RESOURCE: 'bg-emerald-50 text-emerald-700',
};

export default function CommunityPage() {
  const { isAuthenticated } = useAuth();
  const [posts, setPosts] = useState<CommunityPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [category, setCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const [selectedPost, setSelectedPost] = useState<CommunityPost | null>(null);
  const [comments, setComments] = useState<PostComment[]>([]);
  const [newComment, setNewComment] = useState('');
  const [sendingComment, setSendingComment] = useState(false);

  // Create form
  const [createTitle, setCreateTitle] = useState('');
  const [createCategory, setCreateCategory] = useState('GENERAL');
  const [createContent, setCreateContent] = useState('');
  const [createCompany, setCreateCompany] = useState('');
  const [createRole, setCreateRole] = useState('');
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState('');

  const fetchPosts = async () => {
    setLoading(true);
    try {
      const data = await communityService.listPosts(
        category !== 'ALL' ? category : undefined,
        searchQuery || undefined
      );
      setPosts(data);
    } catch { /* empty */ }
    setLoading(false);
  };

  useEffect(() => { fetchPosts(); }, [category]);

  const toggleUpvote = async (post: CommunityPost) => {
    if (!isAuthenticated) return;
    try {
      const hasUpvoted = await communityService.toggleVote(post.id);
      setPosts(posts.map(p => p.id === post.id ? {
        ...p, hasUpvoted, upvoteCount: p.upvoteCount + (hasUpvoted ? 1 : -1)
      } : p));
    } catch { /* empty */ }
  };

  const toggleSave = async (post: CommunityPost) => {
    if (!isAuthenticated) return;
    try {
      const isSaved = await communityService.toggleBookmark(post.id);
      setPosts(posts.map(p => p.id === post.id ? { ...p, isSaved } : p));
    } catch { /* empty */ }
  };

  const openPost = async (post: CommunityPost) => {
    setSelectedPost(post);
    try {
      const data = await communityService.getComments(post.id);
      setComments(data);
    } catch { setComments([]); }
  };

  const submitComment = async (e: FormEvent) => {
    e.preventDefault();
    if (!newComment.trim() || !selectedPost) return;
    setSendingComment(true);
    try {
      await communityService.addComment(selectedPost.id, newComment);
      setComments(await communityService.getComments(selectedPost.id));
      setNewComment('');
      setPosts(posts.map(p => p.id === selectedPost.id ? { ...p, commentCount: p.commentCount + 1 } : p));
    } catch { /* empty */ }
    setSendingComment(false);
  };

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    setCreateError('');
    setCreating(true);
    try {
      const post = await communityService.createPost({
        title: createTitle, category: createCategory, content: createContent,
        companyName: createCompany || undefined, roleTitle: createRole || undefined,
      });
      setPosts([post, ...posts]);
      setShowCreate(false);
      setCreateTitle(''); setCreateContent(''); setCreateCompany(''); setCreateRole('');
    } catch (err) {
      setCreateError(err instanceof Error ? err.message : 'Failed');
    }
    setCreating(false);
  };

  return (
    <div id="community-page" className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-800">Community</h1>
          <p className="text-slate-500 mt-1">Interview experiences, career guidance, and peer discussions</p>
        </div>
        {isAuthenticated && (
          <button onClick={() => setShowCreate(true)}
            className="flex items-center gap-2 px-5 py-2.5 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 transition-all shadow-lg shadow-indigo-500/25 font-medium text-sm">
            <Plus className="w-4 h-4" /> New Post
          </button>
        )}
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-2 items-center">
        {CATEGORIES.map(c => (
          <button key={c} onClick={() => setCategory(c)}
            className={`px-4 py-2 rounded-xl text-sm font-medium transition-all ${category === c
              ? 'bg-indigo-600 text-white shadow-sm' : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'}`}>
            {CATEGORY_LABELS[c] || c}
          </button>
        ))}
        <div className="flex-1 min-w-[200px] relative ml-2">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input type="text" placeholder="Search posts..."
            value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && fetchPosts()}
            className="w-full pl-10 pr-4 py-2 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500" />
        </div>
      </div>

      {/* Posts */}
      {loading ? (
        <div className="flex justify-center py-16">
          <div className="w-8 h-8 border-3 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div>
        </div>
      ) : posts.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-100 p-16 text-center shadow-sm">
          <MessageSquare className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-lg font-semibold text-slate-700 mb-1">No posts yet</h3>
          <p className="text-sm text-slate-500">Be the first to share!</p>
        </div>
      ) : (
        <div className="space-y-3">
          {posts.map(post => (
            <div key={post.id} className="bg-white rounded-2xl border border-slate-100 p-5 shadow-sm hover:shadow-md transition-shadow">
              <div className="flex items-start gap-3">
                {/* Avatar */}
                <div className="w-10 h-10 bg-indigo-100 rounded-full flex items-center justify-center flex-shrink-0">
                  {post.authorAvatarUrl ? (
                    <img src={post.authorAvatarUrl} className="w-10 h-10 rounded-full object-cover" alt="" />
                  ) : (
                    <User className="w-5 h-5 text-indigo-600" />
                  )}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap mb-1">
                    <span className="text-sm font-semibold text-slate-800">{post.authorName || 'Anonymous'}</span>
                    {post.isVerifiedMentor && <span title="Verified Mentor"><CheckCircle2 className="w-4 h-4 text-indigo-500" /></span>}
                    {post.authorCompany && <span className="text-xs text-slate-400">at {post.authorCompany}</span>}
                    <span className={`px-2 py-0.5 rounded-lg text-xs font-medium ${CATEGORY_COLORS[post.category] || 'bg-slate-100 text-slate-600'}`}>
                      {CATEGORY_LABELS[post.category] || post.category}
                    </span>
                  </div>
                  <h3 className="text-base font-semibold text-slate-800 cursor-pointer hover:text-indigo-600 transition-colors" onClick={() => openPost(post)}>
                    {post.title}
                  </h3>
                  {post.companyName && <span className="text-xs text-indigo-500 font-medium">{post.companyName}{post.roleTitle ? ` • ${post.roleTitle}` : ''}</span>}
                  <p className="text-sm text-slate-600 mt-1 line-clamp-2">{post.content}</p>

                  {/* Actions */}
                  <div className="flex items-center gap-4 mt-3">
                    <button onClick={() => toggleUpvote(post)}
                      className={`flex items-center gap-1.5 text-xs font-medium transition-colors ${post.hasUpvoted ? 'text-indigo-600' : 'text-slate-400 hover:text-slate-600'}`}>
                      <ThumbsUp className="w-4 h-4" /> {post.upvoteCount}
                    </button>
                    <button onClick={() => openPost(post)}
                      className="flex items-center gap-1.5 text-xs text-slate-400 hover:text-slate-600 font-medium transition-colors">
                      <MessageSquare className="w-4 h-4" /> {post.commentCount}
                    </button>
                    <button onClick={() => toggleSave(post)}
                      className="text-slate-400 hover:text-indigo-600 transition-colors">
                      {post.isSaved ? <BookmarkCheck className="w-4 h-4 text-indigo-600" /> : <Bookmark className="w-4 h-4" />}
                    </button>
                    <span className="text-xs text-slate-300 ml-auto">{new Date(post.createdAt).toLocaleDateString()}</span>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Post Detail Modal */}
      {selectedPost && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[85vh] overflow-y-auto p-8">
            <div className="flex items-center justify-between mb-4">
              <span className={`px-2 py-0.5 rounded-lg text-xs font-medium ${CATEGORY_COLORS[selectedPost.category] || ''}`}>
                {CATEGORY_LABELS[selectedPost.category]}
              </span>
              <button onClick={() => setSelectedPost(null)}><X className="w-5 h-5 text-slate-400" /></button>
            </div>
            <h2 className="text-xl font-bold text-slate-800 mb-2">{selectedPost.title}</h2>
            <div className="flex items-center gap-2 mb-4 text-sm text-slate-500">
              <span className="font-medium text-slate-700">{selectedPost.authorName}</span>
              {selectedPost.isVerifiedMentor && <CheckCircle2 className="w-4 h-4 text-indigo-500" />}
              <span>•</span>
              <span>{new Date(selectedPost.createdAt).toLocaleDateString()}</span>
            </div>
            <div className="text-sm text-slate-700 whitespace-pre-line mb-6 leading-relaxed">{selectedPost.content}</div>

            {/* Comments */}
            <div className="border-t border-slate-100 pt-4">
              <h3 className="text-sm font-semibold text-slate-700 mb-3">Comments ({comments.length})</h3>
              <div className="space-y-3 mb-4 max-h-60 overflow-y-auto">
                {comments.map(c => (
                  <div key={c.id} className="flex gap-2">
                    <div className="w-7 h-7 bg-slate-100 rounded-full flex items-center justify-center flex-shrink-0">
                      <User className="w-3.5 h-3.5 text-slate-500" />
                    </div>
                    <div className="bg-slate-50 rounded-xl p-3 flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <span className="text-xs font-semibold text-slate-700">{c.authorName}</span>
                        <span className="text-xs text-slate-400">{new Date(c.createdAt).toLocaleDateString()}</span>
                      </div>
                      <p className="text-sm text-slate-600">{c.content}</p>
                    </div>
                  </div>
                ))}
                {comments.length === 0 && <p className="text-xs text-slate-400 text-center py-4">No comments yet</p>}
              </div>
              {isAuthenticated && (
                <form onSubmit={submitComment} className="flex gap-2">
                  <input value={newComment} onChange={(e) => setNewComment(e.target.value)} placeholder="Write a comment..."
                    className="flex-1 px-4 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" />
                  <button type="submit" disabled={sendingComment || !newComment.trim()}
                    className="p-2.5 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 transition-all disabled:opacity-50">
                    <Send className="w-4 h-4" />
                  </button>
                </form>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Create Post Modal */}
      {showCreate && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full p-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold text-slate-800">Create Post</h2>
              <button onClick={() => setShowCreate(false)}><X className="w-5 h-5 text-slate-400" /></button>
            </div>
            {createError && <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">⚠ {createError}</div>}
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Title *</label>
                <input required value={createTitle} onChange={(e) => setCreateTitle(e.target.value)}
                  className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Category</label>
                <select value={createCategory} onChange={(e) => setCreateCategory(e.target.value)}
                  className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50">
                  {CATEGORIES.filter(c => c !== 'ALL').map(c => <option key={c} value={c}>{CATEGORY_LABELS[c]}</option>)}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Company</label>
                  <input value={createCompany} onChange={(e) => setCreateCompany(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="Google" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Role</label>
                  <input value={createRole} onChange={(e) => setCreateRole(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="SDE Intern" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Content *</label>
                <textarea required rows={6} value={createContent} onChange={(e) => setCreateContent(e.target.value)}
                  className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50 resize-none"
                  placeholder="Share your experience, tips, or questions..." />
              </div>
              <button type="submit" disabled={creating}
                className="w-full py-3 bg-indigo-600 text-white font-semibold rounded-xl hover:bg-indigo-700 transition-all flex items-center justify-center gap-2 disabled:opacity-60">
                {creating ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div> :
                  <>Publish Post</>}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
