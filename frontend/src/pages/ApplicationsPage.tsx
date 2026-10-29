import { useEffect, useState, type FormEvent } from 'react';
import { applicationService } from '../services/applicationService';
import type { Application, ApplicationStatus } from '../types';
import {
  Plus, Search, Filter, Trash2, Edit3, ChevronDown, Clock, Building, MapPin,
  ExternalLink, X, ArrowRight
} from 'lucide-react';

const STATUSES: ApplicationStatus[] = [
  'SAVED', 'APPLIED', 'ASSESSMENT', 'INTERVIEW', 'OFFER_RECEIVED', 'REJECTED', 'WITHDRAWN'
];
const STATUS_DISPLAY: Record<string, { label: string; color: string; bg: string }> = {
  SAVED: { label: 'Saved', color: 'text-slate-600', bg: 'bg-slate-100 border-slate-200' },
  APPLIED: { label: 'Applied', color: 'text-blue-700', bg: 'bg-blue-50 border-blue-200' },
  ASSESSMENT: { label: 'Assessment', color: 'text-amber-700', bg: 'bg-amber-50 border-amber-200' },
  INTERVIEW: { label: 'Interview', color: 'text-violet-700', bg: 'bg-violet-50 border-violet-200' },
  OFFER_RECEIVED: { label: 'Offer 🎉', color: 'text-emerald-700', bg: 'bg-emerald-50 border-emerald-200' },
  REJECTED: { label: 'Rejected', color: 'text-red-700', bg: 'bg-red-50 border-red-200' },
  WITHDRAWN: { label: 'Withdrawn', color: 'text-gray-600', bg: 'bg-gray-50 border-gray-200' },
};

export default function ApplicationsPage() {
  const [apps, setApps] = useState<Application[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterStatus, setFilterStatus] = useState<string>('');
  const [showForm, setShowForm] = useState(false);
  const [editingApp, setEditingApp] = useState<Application | null>(null);

  // Form fields
  const [companyName, setCompanyName] = useState('');
  const [role, setRole] = useState('');
  const [sourceUrl, setSourceUrl] = useState('');
  const [location, setLocation] = useState('');
  const [stipendSalary, setStipendSalary] = useState('');
  const [status, setStatus] = useState<ApplicationStatus>('SAVED');
  const [deadline, setDeadline] = useState('');
  const [notes, setNotes] = useState('');
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);

  const fetchApps = async () => {
    try {
      const data = await applicationService.list(filterStatus || undefined, searchQuery || undefined);
      setApps(data);
    } catch {
      /* empty */
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchApps();
  }, [filterStatus]);

  const filteredApps = apps.filter((a) =>
    !searchQuery ||
    a.companyName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    a.role.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const openCreateForm = () => {
    setEditingApp(null);
    setCompanyName(''); setRole(''); setSourceUrl(''); setLocation('');
    setStipendSalary(''); setStatus('SAVED'); setDeadline(''); setNotes('');
    setFormError('');
    setShowForm(true);
  };

  const openEditForm = (app: Application) => {
    setEditingApp(app);
    setCompanyName(app.companyName);
    setRole(app.role);
    setSourceUrl(app.sourceUrl || '');
    setLocation(app.location || '');
    setStipendSalary(app.stipendSalary || '');
    setStatus(app.status);
    setDeadline(app.deadline?.split('T')[0] || '');
    setNotes(app.notes || '');
    setFormError('');
    setShowForm(true);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setFormError('');
    setSaving(true);
    try {
      const payload: Partial<Application> = {
        companyName, role, sourceUrl, location, stipendSalary, status, notes,
        deadline: deadline || undefined,
      };
      if (editingApp) {
        await applicationService.update(editingApp.id, payload);
      } else {
        await applicationService.create(payload);
      }
      setShowForm(false);
      await fetchApps();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Failed to save');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this application?')) return;
    await applicationService.delete(id);
    setApps(apps.filter((a) => a.id !== id));
  };

  const handleStatusChange = async (id: number, newStatus: string) => {
    try {
      await applicationService.updateStatus(id, newStatus);
      await fetchApps();
    } catch {
      /* empty */
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="w-8 h-8 border-3 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <div id="applications-page" className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-800">Applications</h1>
          <p className="text-slate-500 mt-1">Track and manage your internship applications</p>
        </div>
        <button onClick={openCreateForm} id="add-application-btn"
          className="flex items-center gap-2 px-5 py-2.5 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 transition-all shadow-lg shadow-indigo-500/25 font-medium text-sm">
          <Plus className="w-4 h-4" /> Add Application
        </button>
      </div>

      {/* Search & Filter Bar */}
      <div className="flex gap-3 bg-white rounded-2xl border border-slate-100 p-3 shadow-sm">
        <div className="flex-1 relative">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input type="text" placeholder="Search by company or role..."
            value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent" />
        </div>
        <div className="relative">
          <Filter className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}
            className="pl-10 pr-8 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm appearance-none focus:outline-none focus:ring-2 focus:ring-indigo-500 cursor-pointer">
            <option value="">All Status</option>
            {STATUSES.map((s) => (
              <option key={s} value={s}>{STATUS_DISPLAY[s]?.label || s}</option>
            ))}
          </select>
          <ChevronDown className="w-4 h-4 absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
        </div>
      </div>

      {/* Applications List */}
      {filteredApps.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-100 p-16 text-center shadow-sm">
          <div className="w-16 h-16 bg-indigo-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <Building className="w-8 h-8 text-indigo-400" />
          </div>
          <h3 className="text-lg font-semibold text-slate-700 mb-1">No applications found</h3>
          <p className="text-slate-500 text-sm mb-4">Start tracking your internship applications</p>
          <button onClick={openCreateForm} className="text-indigo-600 font-medium text-sm hover:text-indigo-700">
            + Add your first application
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredApps.map((app) => (
            <div key={app.id}
              className="bg-white rounded-2xl border border-slate-100 p-5 shadow-sm hover:shadow-md transition-shadow group">
              <div className="flex items-start justify-between">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-3 mb-1">
                    <h3 className="text-lg font-semibold text-slate-800 truncate">{app.companyName}</h3>
                    <span className={`px-2.5 py-0.5 rounded-full text-xs font-medium border ${STATUS_DISPLAY[app.status]?.bg} ${STATUS_DISPLAY[app.status]?.color}`}>
                      {STATUS_DISPLAY[app.status]?.label || app.status}
                    </span>
                  </div>
                  <p className="text-sm text-slate-600 font-medium">{app.role}</p>
                  <div className="flex items-center gap-4 mt-2 text-xs text-slate-400">
                    {app.location && (
                      <span className="flex items-center gap-1"><MapPin className="w-3 h-3" />{app.location}</span>
                    )}
                    {app.deadline && (
                      <span className="flex items-center gap-1"><Clock className="w-3 h-3" />Due: {new Date(app.deadline).toLocaleDateString()}</span>
                    )}
                    {app.stipendSalary && (
                      <span className="font-medium text-emerald-600">₹ {app.stipendSalary}</span>
                    )}
                    {app.sourceUrl && (
                      <a href={app.sourceUrl} target="_blank" rel="noopener" className="flex items-center gap-1 text-indigo-500 hover:text-indigo-700">
                        <ExternalLink className="w-3 h-3" /> Link
                      </a>
                    )}
                  </div>
                </div>

                <div className="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  {/* Quick status change */}
                  <select
                    value={app.status}
                    onChange={(e) => handleStatusChange(app.id, e.target.value)}
                    className="text-xs border border-slate-200 rounded-lg px-2 py-1.5 bg-slate-50 focus:outline-none"
                  >
                    {STATUSES.map((s) => (
                      <option key={s} value={s}>{STATUS_DISPLAY[s]?.label || s}</option>
                    ))}
                  </select>
                  <button onClick={() => openEditForm(app)}
                    className="p-2 hover:bg-slate-100 rounded-lg transition-colors" title="Edit">
                    <Edit3 className="w-4 h-4 text-slate-500" />
                  </button>
                  <button onClick={() => handleDelete(app.id)}
                    className="p-2 hover:bg-red-50 rounded-lg transition-colors" title="Delete">
                    <Trash2 className="w-4 h-4 text-red-400" />
                  </button>
                </div>
              </div>
              {app.notes && (
                <p className="mt-3 text-xs text-slate-500 bg-slate-50 rounded-lg p-2">{app.notes}</p>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Modal Form */}
      {showForm && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4" id="application-form-modal">
          <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full max-h-[90vh] overflow-y-auto p-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold text-slate-800">{editingApp ? 'Edit Application' : 'New Application'}</h2>
              <button onClick={() => setShowForm(false)} className="p-1 hover:bg-slate-100 rounded-lg"><X className="w-5 h-5 text-slate-500" /></button>
            </div>

            {formError && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">⚠ {formError}</div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Company *</label>
                  <input required value={companyName} onChange={(e) => setCompanyName(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="Google" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Role *</label>
                  <input required value={role} onChange={(e) => setRole(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="SDE Intern" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Location</label>
                  <input value={location} onChange={(e) => setLocation(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="Bangalore" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Stipend / Salary</label>
                  <input value={stipendSalary} onChange={(e) => setStipendSalary(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="₹40,000/month" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Source URL</label>
                <input type="url" value={sourceUrl} onChange={(e) => setSourceUrl(e.target.value)}
                  className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="https://..." />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Status</label>
                  <select value={status} onChange={(e) => setStatus(e.target.value as ApplicationStatus)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50">
                    {STATUSES.map((s) => <option key={s} value={s}>{STATUS_DISPLAY[s]?.label || s}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Deadline</label>
                  <input type="date" value={deadline} onChange={(e) => setDeadline(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Notes</label>
                <textarea rows={3} value={notes} onChange={(e) => setNotes(e.target.value)}
                  className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50 resize-none" placeholder="Any additional notes..." />
              </div>
              <button type="submit" disabled={saving}
                className="w-full py-3 bg-indigo-600 text-white font-semibold rounded-xl hover:bg-indigo-700 transition-all flex items-center justify-center gap-2 shadow-lg shadow-indigo-500/25 disabled:opacity-60">
                {saving ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div> :
                  <>{editingApp ? 'Update' : 'Create'} Application <ArrowRight className="w-4 h-4" /></>}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
