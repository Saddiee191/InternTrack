import { useEffect, useState } from 'react';
import { opportunityService } from '../services/opportunityService';
import { useAuth } from '../context/AuthContext';
import type { Opportunity, EligibilityResult } from '../types';
import {
  Search, Filter, MapPin, Clock, Bookmark, BookmarkCheck, Building, ExternalLink,
  ChevronDown, ShieldCheck, CheckCircle2, XCircle, Briefcase, Globe
} from 'lucide-react';

const WORKPLACE = [
  { value: '', label: 'All Workplace' },
  { value: 'ON_SITE', label: 'On-site' },
  { value: 'HYBRID', label: 'Hybrid' },
  { value: 'REMOTE', label: 'Remote' },
];
const EMPLOYMENT = [
  { value: '', label: 'All Types' },
  { value: 'INTERNSHIP', label: 'Internship' },
  { value: 'FULL_TIME', label: 'Full-time' },
  { value: 'CO_OP', label: 'Co-op' },
];

export default function OpportunitiesPage() {
  const { user, isAuthenticated } = useAuth();
  const [opps, setOpps] = useState<Opportunity[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [workplaceFilter, setWorkplaceFilter] = useState('');
  const [employmentFilter, setEmploymentFilter] = useState('');
  const [eligibility, setEligibility] = useState<Record<number, EligibilityResult>>({});
  const [checkingEligibility, setCheckingEligibility] = useState<number | null>(null);

  useEffect(() => {
    fetchOpps();
  }, [workplaceFilter, employmentFilter]);

  const fetchOpps = async () => {
    setLoading(true);
    try {
      const data = await opportunityService.list({
        search: searchQuery || undefined,
        workplaceType: workplaceFilter || undefined,
        employmentType: employmentFilter || undefined,
        onlyActive: true,
      });
      setOpps(data);
    } catch { /* empty */ }
    setLoading(false);
  };

  const handleSearch = () => fetchOpps();

  const toggleSave = async (opp: Opportunity) => {
    if (!isAuthenticated) return;
    try {
      if (opp.isSaved) {
        await opportunityService.unsave(opp.id);
      } else {
        await opportunityService.save(opp.id);
      }
      setOpps(opps.map(o => o.id === opp.id ? { ...o, isSaved: !o.isSaved } : o));
    } catch { /* empty */ }
  };

  const checkEligibility = async (id: number) => {
    if (eligibility[id]) return;
    setCheckingEligibility(id);
    try {
      const result = await opportunityService.checkEligibility(id);
      setEligibility({ ...eligibility, [id]: result });
    } catch { /* empty */ }
    setCheckingEligibility(null);
  };

  const applyToOpp = async (id: number) => {
    try {
      await opportunityService.apply(id);
      setOpps(opps.map(o => o.id === id ? { ...o, hasApplied: true } : o));
    } catch { /* empty */ }
  };

  return (
    <div id="opportunities-page" className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-800">Opportunities</h1>
        <p className="text-slate-500 mt-1">Browse verified internships and placements</p>
      </div>

      {/* Search & Filters */}
      <div className="bg-white rounded-2xl border border-slate-100 p-4 shadow-sm flex flex-wrap gap-3 items-center">
        <div className="flex-1 min-w-[200px] relative">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input type="text" placeholder="Search company, role, skills..."
            value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500" />
        </div>
        <div className="relative">
          <Globe className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <select value={workplaceFilter} onChange={(e) => setWorkplaceFilter(e.target.value)}
            className="pl-10 pr-8 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm appearance-none focus:outline-none focus:ring-2 focus:ring-indigo-500">
            {WORKPLACE.map(w => <option key={w.value} value={w.value}>{w.label}</option>)}
          </select>
          <ChevronDown className="w-4 h-4 absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
        </div>
        <div className="relative">
          <Briefcase className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <select value={employmentFilter} onChange={(e) => setEmploymentFilter(e.target.value)}
            className="pl-10 pr-8 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm appearance-none focus:outline-none focus:ring-2 focus:ring-indigo-500">
            {EMPLOYMENT.map(e => <option key={e.value} value={e.value}>{e.label}</option>)}
          </select>
          <ChevronDown className="w-4 h-4 absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
        </div>
        <button onClick={handleSearch} className="px-5 py-2.5 bg-indigo-600 text-white rounded-xl text-sm font-medium hover:bg-indigo-700 transition-all flex items-center gap-2">
          <Filter className="w-4 h-4" /> Search
        </button>
      </div>

      {/* Results */}
      {loading ? (
        <div className="flex items-center justify-center h-64">
          <div className="w-8 h-8 border-3 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div>
        </div>
      ) : opps.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-100 p-16 text-center shadow-sm">
          <div className="w-16 h-16 bg-indigo-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <Search className="w-8 h-8 text-indigo-400" />
          </div>
          <h3 className="text-lg font-semibold text-slate-700 mb-1">No opportunities found</h3>
          <p className="text-slate-500 text-sm">Try adjusting your search filters</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          {opps.map((opp) => (
            <div key={opp.id} className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm hover:shadow-md transition-shadow">
              {/* Header */}
              <div className="flex items-start justify-between mb-3">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <h3 className="text-lg font-semibold text-slate-800 truncate">{opp.title}</h3>
                    {opp.verificationStatus === 'VERIFIED' && (
                      <span title="Verified"><ShieldCheck className="w-4 h-4 text-emerald-500 flex-shrink-0" /></span>
                    )}
                  </div>
                  <div className="flex items-center gap-2 text-sm text-slate-500">
                    <Building className="w-4 h-4" />
                    <span className="font-medium">{opp.companyName}</span>
                  </div>
                </div>
                {isAuthenticated && (
                  <button onClick={() => toggleSave(opp)} className="p-2 hover:bg-slate-100 rounded-lg transition-colors" title={opp.isSaved ? 'Unsave' : 'Save'}>
                    {opp.isSaved ? <BookmarkCheck className="w-5 h-5 text-indigo-600" /> : <Bookmark className="w-5 h-5 text-slate-400" />}
                  </button>
                )}
              </div>

              {/* Meta */}
              <div className="flex flex-wrap gap-2 mb-3">
                {opp.location && (
                  <span className="flex items-center gap-1 text-xs bg-slate-100 text-slate-600 px-2 py-1 rounded-lg">
                    <MapPin className="w-3 h-3" /> {opp.location}
                  </span>
                )}
                {opp.workplaceType && (
                  <span className="text-xs bg-blue-50 text-blue-700 px-2 py-1 rounded-lg">{opp.workplaceType}</span>
                )}
                {opp.employmentType && (
                  <span className="text-xs bg-violet-50 text-violet-700 px-2 py-1 rounded-lg">{opp.employmentType}</span>
                )}
                {opp.stipendSalary && (
                  <span className="text-xs bg-emerald-50 text-emerald-700 px-2 py-1 rounded-lg font-medium">₹ {opp.stipendSalary}</span>
                )}
                {opp.deadline && (
                  <span className="flex items-center gap-1 text-xs bg-amber-50 text-amber-700 px-2 py-1 rounded-lg">
                    <Clock className="w-3 h-3" /> {new Date(opp.deadline).toLocaleDateString()}
                  </span>
                )}
              </div>

              {/* Description */}
              <p className="text-sm text-slate-600 line-clamp-2 mb-4">{opp.description}</p>

              {/* Skills */}
              {opp.requiredSkillsJson && (() => {
                try {
                  const skills = JSON.parse(opp.requiredSkillsJson) as string[];
                  return skills.length > 0 ? (
                    <div className="flex flex-wrap gap-1.5 mb-4">
                      {skills.slice(0, 5).map((s, i) => (
                        <span key={i} className="text-xs bg-indigo-50 text-indigo-600 px-2 py-0.5 rounded-md font-medium">{s}</span>
                      ))}
                      {skills.length > 5 && <span className="text-xs text-slate-400">+{skills.length - 5} more</span>}
                    </div>
                  ) : null;
                } catch { return null; }
              })()}

              {/* Eligibility */}
              {eligibility[opp.id] && (
                <div className={`p-3 rounded-xl mb-4 text-sm ${eligibility[opp.id].eligible ? 'bg-emerald-50 border border-emerald-200' : 'bg-red-50 border border-red-200'}`}>
                  <div className="flex items-center gap-2 font-medium mb-1">
                    {eligibility[opp.id].eligible ? (
                      <><CheckCircle2 className="w-4 h-4 text-emerald-600" /> <span className="text-emerald-700">You're eligible!</span></>
                    ) : (
                      <><XCircle className="w-4 h-4 text-red-500" /> <span className="text-red-700">Not eligible</span></>
                    )}
                  </div>
                  <p className="text-xs text-slate-600">{eligibility[opp.id].summary}</p>
                </div>
              )}

              {/* Actions */}
              <div className="flex items-center gap-2 pt-2 border-t border-slate-100">
                {isAuthenticated && user?.role === 'STUDENT' && (
                  <>
                    <button onClick={() => checkEligibility(opp.id)}
                      disabled={checkingEligibility === opp.id || !!eligibility[opp.id]}
                      className="text-xs px-3 py-1.5 bg-slate-100 text-slate-600 rounded-lg hover:bg-slate-200 transition-colors disabled:opacity-50">
                      {checkingEligibility === opp.id ? 'Checking...' : eligibility[opp.id] ? 'Checked' : 'Check Eligibility'}
                    </button>
                    {!opp.hasApplied ? (
                      <button onClick={() => applyToOpp(opp.id)}
                        className="text-xs px-3 py-1.5 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors">
                        Quick Apply
                      </button>
                    ) : (
                      <span className="text-xs text-emerald-600 font-medium px-3 py-1.5">✓ Applied</span>
                    )}
                  </>
                )}
                {opp.applyUrl && (
                  <a href={opp.applyUrl} target="_blank" rel="noopener"
                    className="ml-auto text-xs px-3 py-1.5 text-indigo-600 hover:text-indigo-700 flex items-center gap-1">
                    Apply <ExternalLink className="w-3 h-3" />
                  </a>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
