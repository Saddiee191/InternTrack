import { useEffect, useState, type FormEvent } from 'react';
import { referralService } from '../services/referralService';
import { authService } from '../services/authService';
import { useAuth } from '../context/AuthContext';
import type { ReferralRequest, MentorProfile } from '../types';
import {
  Users, Plus, ArrowRight, X, User, Building,
  Send, MessageSquare
} from 'lucide-react';

const STATUS_STYLES: Record<string, string> = {
  PENDING: 'bg-amber-100 text-amber-700 border-amber-200',
  ACCEPTED: 'bg-emerald-100 text-emerald-700 border-emerald-200',
  DECLINED: 'bg-red-100 text-red-700 border-red-200',
  CLOSED: 'bg-slate-100 text-slate-600 border-slate-200',
};

export default function ReferralsPage() {
  const { user } = useAuth();
  const isStudent = user?.role === 'STUDENT';
  const isMentor = user?.role === 'MENTOR';

  const [requests, setRequests] = useState<ReferralRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [mentors, setMentors] = useState<MentorProfile[]>([]);
  const [mentorSearch, setMentorSearch] = useState('');

  // Create form
  const [selectedMentorId, setSelectedMentorId] = useState<number | null>(null);
  const [oppTitle, setOppTitle] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [note, setNote] = useState('');
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState('');

  // Mentor response
  const [respondingId, setRespondingId] = useState<number | null>(null);
  const [mentorResponse, setMentorResponse] = useState('');

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    setLoading(true);
    try {
      const data = isStudent
        ? await referralService.listMyRequests()
        : await referralService.listReceivedRequests();
      setRequests(data);
    } catch { /* empty */ }
    setLoading(false);
  };

  const openCreateForm = async () => {
    setShowCreate(true);
    try {
      const data = await authService.listMentors(mentorSearch || undefined);
      setMentors(data);
    } catch { setMentors([]); }
  };

  const searchMentors = async () => {
    try {
      const data = await authService.listMentors(mentorSearch || undefined);
      setMentors(data);
    } catch { /* empty */ }
  };

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    if (!selectedMentorId) { setCreateError('Please select a mentor'); return; }
    setCreating(true);
    setCreateError('');
    try {
      const req = await referralService.createRequest({
        mentorId: selectedMentorId, opportunityTitle: oppTitle, companyName, note: note || undefined,
      });
      setRequests([req, ...requests]);
      setShowCreate(false);
      setSelectedMentorId(null); setOppTitle(''); setCompanyName(''); setNote('');
    } catch (err) {
      setCreateError(err instanceof Error ? err.message : 'Failed');
    }
    setCreating(false);
  };

  const handleMentorAction = async (id: number, status: 'ACCEPTED' | 'DECLINED') => {
    try {
      await referralService.updateStatus(id, status, mentorResponse || undefined);
      setRequests(requests.map(r => r.id === id ? { ...r, status, mentorResponse } : r));
      setRespondingId(null);
      setMentorResponse('');
    } catch { /* empty */ }
  };

  return (
    <div id="referrals-page" className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-800">Referrals</h1>
          <p className="text-slate-500 mt-1">
            {isStudent ? 'Request referrals from verified mentors' : 'Manage referral requests from students'}
          </p>
        </div>
        {isStudent && (
          <button onClick={openCreateForm}
            className="flex items-center gap-2 px-5 py-2.5 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 transition-all shadow-lg shadow-indigo-500/25 font-medium text-sm">
            <Plus className="w-4 h-4" /> Request Referral
          </button>
        )}
      </div>

      {loading ? (
        <div className="flex justify-center py-16">
          <div className="w-8 h-8 border-3 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div>
        </div>
      ) : requests.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-100 p-16 text-center shadow-sm">
          <Users className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-lg font-semibold text-slate-700 mb-1">
            {isStudent ? 'No referral requests yet' : 'No incoming requests'}
          </h3>
          <p className="text-sm text-slate-500">
            {isStudent ? 'Request a referral from a mentor to get started' : 'Students will send referral requests here'}
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {requests.map(req => (
            <div key={req.id} className="bg-white rounded-2xl border border-slate-100 p-5 shadow-sm hover:shadow-md transition-shadow">
              <div className="flex items-start justify-between">
                <div className="flex items-start gap-3 flex-1">
                  <div className="w-10 h-10 bg-violet-100 rounded-full flex items-center justify-center flex-shrink-0">
                    <User className="w-5 h-5 text-violet-600" />
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2 mb-1 flex-wrap">
                      <h3 className="text-base font-semibold text-slate-800">
                        {isStudent ? `To: ${req.mentorName || 'Mentor'}` : `From: ${req.studentName || 'Student'}`}
                      </h3>
                      <span className={`px-2 py-0.5 rounded-full text-xs font-medium border ${STATUS_STYLES[req.status] || ''}`}>
                        {req.status}
                      </span>
                    </div>
                    <div className="flex items-center gap-3 text-sm text-slate-500 mb-1">
                      <span className="flex items-center gap-1"><Building className="w-3.5 h-3.5" /> {req.companyName}</span>
                      <span className="font-medium text-slate-700">{req.opportunityTitle}</span>
                    </div>
                    {req.note && <p className="text-sm text-slate-600 bg-slate-50 rounded-lg p-2 mt-2">{req.note}</p>}
                    {req.mentorResponse && (
                      <div className="mt-2 p-2 bg-indigo-50 border border-indigo-100 rounded-lg">
                        <p className="text-xs font-medium text-indigo-700 mb-0.5">Mentor Response:</p>
                        <p className="text-sm text-indigo-800">{req.mentorResponse}</p>
                      </div>
                    )}
                    <p className="text-xs text-slate-400 mt-2">{new Date(req.createdAt).toLocaleDateString()}</p>
                  </div>
                </div>

                {/* Mentor Actions */}
                {isMentor && req.status === 'PENDING' && (
                  <div className="flex flex-col gap-2 ml-4">
                    {respondingId === req.id ? (
                      <div className="space-y-2">
                        <textarea value={mentorResponse} onChange={(e) => setMentorResponse(e.target.value)}
                          placeholder="Optional message..."
                          className="w-48 px-2 py-1.5 text-xs border border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-500 resize-none" rows={2} />
                        <div className="flex gap-1">
                          <button onClick={() => handleMentorAction(req.id, 'ACCEPTED')}
                            className="flex-1 px-2 py-1.5 bg-emerald-600 text-white text-xs rounded-lg hover:bg-emerald-700">Accept</button>
                          <button onClick={() => handleMentorAction(req.id, 'DECLINED')}
                            className="flex-1 px-2 py-1.5 bg-red-600 text-white text-xs rounded-lg hover:bg-red-700">Decline</button>
                        </div>
                      </div>
                    ) : (
                      <button onClick={() => setRespondingId(req.id)}
                        className="px-3 py-1.5 bg-indigo-600 text-white text-xs rounded-lg hover:bg-indigo-700 flex items-center gap-1">
                        <MessageSquare className="w-3 h-3" /> Respond
                      </button>
                    )}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create Referral Request Modal */}
      {showCreate && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full max-h-[85vh] overflow-y-auto p-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold text-slate-800">Request Referral</h2>
              <button onClick={() => setShowCreate(false)}><X className="w-5 h-5 text-slate-400" /></button>
            </div>
            {createError && <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">⚠ {createError}</div>}
            <form onSubmit={handleCreate} className="space-y-4">
              {/* Mentor Selection */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Select Mentor *</label>
                <div className="flex gap-2 mb-2">
                  <input placeholder="Search by name or company..." value={mentorSearch} onChange={(e) => setMentorSearch(e.target.value)}
                    className="flex-1 px-3 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" />
                  <button type="button" onClick={searchMentors} className="px-3 py-2 bg-slate-100 rounded-xl text-sm hover:bg-slate-200">Search</button>
                </div>
                <div className="max-h-36 overflow-y-auto space-y-1 border border-slate-200 rounded-xl p-2">
                  {mentors.length === 0 ? (
                    <p className="text-xs text-slate-400 text-center py-2">No mentors found</p>
                  ) : mentors.map(m => (
                    <button key={m.userId} type="button" onClick={() => setSelectedMentorId(m.userId)}
                      className={`w-full flex items-center gap-2 p-2 rounded-lg text-left transition-colors ${selectedMentorId === m.userId ? 'bg-indigo-50 border border-indigo-200' : 'hover:bg-slate-50'}`}>
                      <div className="w-7 h-7 bg-violet-100 rounded-full flex items-center justify-center flex-shrink-0">
                        <User className="w-3.5 h-3.5 text-violet-600" />
                      </div>
                      <div className="min-w-0">
                        <p className="text-sm font-medium text-slate-700 truncate">{m.fullName || m.email}</p>
                        <p className="text-xs text-slate-400">{m.currentCompany} • {m.currentRole}</p>
                      </div>
                    </button>
                  ))}
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Company *</label>
                  <input required value={companyName} onChange={(e) => setCompanyName(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="Google" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Position *</label>
                  <input required value={oppTitle} onChange={(e) => setOppTitle(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="SDE Intern" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Note to Mentor</label>
                <textarea rows={3} value={note} onChange={(e) => setNote(e.target.value)}
                  className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50 resize-none"
                  placeholder="Why this opportunity matters to you..." />
              </div>
              <button type="submit" disabled={creating}
                className="w-full py-3 bg-indigo-600 text-white font-semibold rounded-xl hover:bg-indigo-700 transition-all flex items-center justify-center gap-2 disabled:opacity-60">
                {creating ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div> :
                  <><Send className="w-4 h-4" /> Send Request <ArrowRight className="w-4 h-4" /></>}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
