import { useEffect, useState } from 'react';
import { adminService } from '../services/adminService';
import type { AdminMetrics, ContentReport, OpportunityReport } from '../types';
import {
  Users, Briefcase, ShieldAlert, FileText, AlertTriangle, CheckCircle2,
  Flag
} from 'lucide-react';

export default function AdminPage() {
  const [metrics, setMetrics] = useState<AdminMetrics | null>(null);
  const [contentReports, setContentReports] = useState<ContentReport[]>([]);
  const [oppReports, setOppReports] = useState<OpportunityReport[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'content' | 'opportunities'>('overview');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [m, cr, or] = await Promise.all([
        adminService.getMetrics().catch(() => null),
        adminService.getContentReports().catch(() => []),
        adminService.getOpportunityReports().catch(() => []),
      ]);
      setMetrics(m);
      setContentReports(cr);
      setOppReports(or);
    } catch { /* empty */ }
    setLoading(false);
  };

  const resolveContent = async (id: number) => {
    const notes = prompt('Resolution notes:');
    if (!notes) return;
    try {
      await adminService.resolveContentReport(id, 'RESOLVED', notes);
      setContentReports(contentReports.map(r => r.id === id ? { ...r, status: 'RESOLVED', resolutionNotes: notes } : r));
    } catch { /* empty */ }
  };

  const resolveOpp = async (id: number) => {
    const notes = prompt('Resolution notes:');
    if (!notes) return;
    try {
      await adminService.resolveOpportunityReport(id, 'RESOLVED');
      setOppReports(oppReports.map(r => r.id === id ? { ...r, status: 'RESOLVED' } : r));
    } catch { /* empty */ }
  };

  if (loading) {
    return <div className="flex justify-center py-16"><div className="w-8 h-8 border-3 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div></div>;
  }

  const tabs = [
    { key: 'overview', label: 'Overview' },
    { key: 'content', label: `Content Reports (${contentReports.filter(r => r.status === 'PENDING').length})` },
    { key: 'opportunities', label: `Opp Reports (${oppReports.filter(r => r.status === 'PENDING').length})` },
  ];

  return (
    <div id="admin-page" className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-800">Admin Panel</h1>
        <p className="text-slate-500 mt-1">Platform management and moderation</p>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 bg-white rounded-xl border border-slate-100 p-1 w-fit shadow-sm">
        {tabs.map(t => (
          <button key={t.key} onClick={() => setActiveTab(t.key as typeof activeTab)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${activeTab === t.key
              ? 'bg-indigo-600 text-white shadow-sm' : 'text-slate-600 hover:bg-slate-50'}`}>
            {t.label}
          </button>
        ))}
      </div>

      {/* Overview */}
      {activeTab === 'overview' && metrics && (
        <div className="space-y-6">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              { label: 'Students', value: metrics.totalStudents, icon: Users, color: 'bg-blue-50 text-blue-600' },
              { label: 'Mentors', value: metrics.totalMentors, icon: Users, color: 'bg-violet-50 text-violet-600' },
              { label: 'Active Opps', value: metrics.totalActiveOpportunities, icon: Briefcase, color: 'bg-emerald-50 text-emerald-600' },
              { label: 'Applications', value: metrics.totalApplicationsRecorded, icon: FileText, color: 'bg-amber-50 text-amber-600' },
              { label: 'Content Reports', value: metrics.pendingContentReports, icon: Flag, color: 'bg-red-50 text-red-600' },
              { label: 'Opp Reports', value: metrics.pendingOpportunityReports, icon: AlertTriangle, color: 'bg-orange-50 text-orange-600' },
              { label: 'Scam Reports', value: metrics.reportedScamScans, icon: ShieldAlert, color: 'bg-rose-50 text-rose-600' },
            ].map((card, i) => (
              <div key={i} className="bg-white rounded-2xl border border-slate-100 p-5 shadow-sm">
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-3 ${card.color}`}>
                  <card.icon className="w-5 h-5" />
                </div>
                <p className="text-2xl font-bold text-slate-800">{card.value}</p>
                <p className="text-xs text-slate-500">{card.label}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Content Reports */}
      {activeTab === 'content' && (
        <div className="space-y-3">
          {contentReports.length === 0 ? (
            <div className="bg-white rounded-2xl border border-slate-100 p-12 text-center shadow-sm">
              <CheckCircle2 className="w-12 h-12 text-emerald-300 mx-auto mb-3" />
              <p className="text-slate-500">No content reports</p>
            </div>
          ) : contentReports.map(r => (
            <div key={r.id} className="bg-white rounded-2xl border border-slate-100 p-5 shadow-sm">
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <Flag className="w-4 h-4 text-red-500" />
                    <span className="text-sm font-semibold text-slate-800">{r.targetType} #{r.targetId}</span>
                    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${r.status === 'PENDING' ? 'bg-amber-100 text-amber-700' : 'bg-emerald-100 text-emerald-700'}`}>
                      {r.status}
                    </span>
                  </div>
                  <p className="text-sm text-slate-600">Reason: {r.reason}</p>
                  <p className="text-xs text-slate-400 mt-1">By: {r.reporterName} • {new Date(r.createdAt).toLocaleDateString()}</p>
                  {r.resolutionNotes && <p className="text-xs text-emerald-600 mt-1">Resolution: {r.resolutionNotes}</p>}
                </div>
                {r.status === 'PENDING' && (
                  <button onClick={() => resolveContent(r.id)}
                    className="px-3 py-1.5 bg-emerald-600 text-white text-xs rounded-lg hover:bg-emerald-700">
                    Resolve
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Opportunity Reports */}
      {activeTab === 'opportunities' && (
        <div className="space-y-3">
          {oppReports.length === 0 ? (
            <div className="bg-white rounded-2xl border border-slate-100 p-12 text-center shadow-sm">
              <CheckCircle2 className="w-12 h-12 text-emerald-300 mx-auto mb-3" />
              <p className="text-slate-500">No opportunity reports</p>
            </div>
          ) : oppReports.map(r => (
            <div key={r.id} className="bg-white rounded-2xl border border-slate-100 p-5 shadow-sm">
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <AlertTriangle className="w-4 h-4 text-amber-500" />
                    <span className="text-sm font-semibold text-slate-800">{r.opportunityTitle} at {r.companyName}</span>
                    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${r.status === 'PENDING' ? 'bg-amber-100 text-amber-700' : 'bg-emerald-100 text-emerald-700'}`}>
                      {r.status}
                    </span>
                  </div>
                  <p className="text-sm text-slate-600">Reason: {r.reason}</p>
                  <p className="text-xs text-slate-400 mt-1">Reported by: {r.reporterName} • {new Date(r.createdAt).toLocaleDateString()}</p>
                </div>
                {r.status === 'PENDING' && (
                  <button onClick={() => resolveOpp(r.id)}
                    className="px-3 py-1.5 bg-emerald-600 text-white text-xs rounded-lg hover:bg-emerald-700">
                    Resolve
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
