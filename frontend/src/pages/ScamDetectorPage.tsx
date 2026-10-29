import { useState, type FormEvent } from 'react';
import { scamDetectorService } from '../services/scamDetectorService';
import type { ScamScan } from '../types';
import {
  ShieldAlert, ShieldCheck, AlertTriangle, Search, ArrowRight, ChevronDown, ChevronUp,
  Flag, X, Info
} from 'lucide-react';

const RISK_CONFIG: Record<string, { color: string; bg: string; icon: typeof ShieldCheck }> = {
  LOW: { color: 'text-emerald-700', bg: 'bg-emerald-50 border-emerald-200', icon: ShieldCheck },
  MEDIUM: { color: 'text-amber-700', bg: 'bg-amber-50 border-amber-200', icon: AlertTriangle },
  HIGH: { color: 'text-red-700', bg: 'bg-red-50 border-red-200', icon: ShieldAlert },
};

export default function ScamDetectorPage() {
  const [companyName, setCompanyName] = useState('');
  const [jobTitle, setJobTitle] = useState('');
  const [recruiterContact, setRecruiterContact] = useState('');
  const [applicationUrl, setApplicationUrl] = useState('');
  const [stipendSalary, setStipendSalary] = useState('');
  const [jobDescription, setJobDescription] = useState('');
  const [recruiterMessage, setRecruiterMessage] = useState('');
  const [scanning, setScanning] = useState(false);
  const [result, setResult] = useState<ScamScan | null>(null);
  const [history, setHistory] = useState<ScamScan[]>([]);
  const [historyLoaded, setHistoryLoaded] = useState(false);
  const [expandedHistory, setExpandedHistory] = useState<number | null>(null);
  const [error, setError] = useState('');

  const loadHistory = async () => {
    if (historyLoaded) return;
    try {
      const data = await scamDetectorService.getHistory();
      setHistory(data);
      setHistoryLoaded(true);
    } catch { /* empty */ }
  };

  useState(() => { loadHistory(); });

  const handleScan = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setScanning(true);
    setResult(null);
    try {
      const scan = await scamDetectorService.scan({
        companyName: companyName || undefined,
        jobTitle: jobTitle || undefined,
        recruiterContact: recruiterContact || undefined,
        applicationUrl: applicationUrl || undefined,
        stipendSalary: stipendSalary || undefined,
        jobDescription: jobDescription || undefined,
        recruiterMessage: recruiterMessage || undefined,
      });
      setResult(scan);
      setHistory([scan, ...history]);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Scan failed');
    } finally {
      setScanning(false);
    }
  };

  const handleReport = async (id: number) => {
    try {
      await scamDetectorService.reportScam(id);
      setHistory(history.map(h => h.id === id ? { ...h, isReported: true } : h));
      if (result?.id === id) setResult({ ...result, isReported: true });
    } catch { /* empty */ }
  };

  const renderScanResult = (scan: ScamScan) => {
    const config = RISK_CONFIG[scan.overallRiskLevel] || RISK_CONFIG.MEDIUM;
    const RiskIcon = config.icon;
    return (
      <div className="space-y-4">
        {/* Risk level badge */}
        <div className={`p-4 rounded-xl border ${config.bg} flex items-center gap-3`}>
          <RiskIcon className={`w-8 h-8 ${config.color}`} />
          <div>
            <p className={`text-lg font-bold ${config.color}`}>
              {scan.overallRiskLevel} RISK — Score: {scan.riskScore}/100
            </p>
            <p className="text-sm text-slate-600">
              {scan.overallRiskLevel === 'LOW' ? 'This opportunity appears legitimate' :
                scan.overallRiskLevel === 'MEDIUM' ? 'Some warning signs detected — proceed with caution' :
                  'Multiple red flags detected — high scam probability'}
            </p>
          </div>
        </div>

        {/* Findings */}
        {scan.findings && scan.findings.length > 0 && (
          <div className="space-y-2">
            <p className="text-sm font-semibold text-slate-700">Risk Indicators</p>
            {scan.findings.map((f, i) => (
              <div key={i} className="p-3 bg-slate-50 rounded-xl border border-slate-100">
                <div className="flex items-center justify-between mb-1">
                  <span className="text-sm font-medium text-slate-700 flex items-center gap-2">
                    <AlertTriangle className="w-3.5 h-3.5 text-amber-500" />
                    {f.indicatorName}
                  </span>
                  <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${f.riskWeight >= 20 ? 'bg-red-100 text-red-700' : f.riskWeight >= 10 ? 'bg-amber-100 text-amber-700' : 'bg-slate-100 text-slate-600'}`}>
                    +{f.riskWeight}
                  </span>
                </div>
                <p className="text-xs text-slate-600">{f.explanation}</p>
                {f.recommendation && <p className="text-xs text-indigo-600 mt-1 font-medium">💡 {f.recommendation}</p>}
              </div>
            ))}
          </div>
        )}

        {/* Recommended steps */}
        {scan.recommendedSteps && scan.recommendedSteps.length > 0 && (
          <div className="p-4 bg-blue-50 border border-blue-200 rounded-xl">
            <p className="text-sm font-semibold text-blue-800 mb-2 flex items-center gap-2">
              <Info className="w-4 h-4" /> Recommended Steps
            </p>
            <ul className="space-y-1">
              {scan.recommendedSteps.map((s, i) => (
                <li key={i} className="text-xs text-blue-700 flex items-start gap-2">
                  <span className="font-bold">{i + 1}.</span> {s}
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* Report button */}
        {!scan.isReported && (
          <button onClick={() => handleReport(scan.id)}
            className="flex items-center gap-2 text-sm text-red-600 hover:text-red-700 font-medium">
            <Flag className="w-4 h-4" /> Report this as a scam
          </button>
        )}
        {scan.isReported && (
          <span className="text-sm text-emerald-600 flex items-center gap-2 font-medium">
            <ShieldCheck className="w-4 h-4" /> Reported — thank you for helping others stay safe
          </span>
        )}
      </div>
    );
  };

  return (
    <div id="scam-detector-page" className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-800">Scam Detector</h1>
        <p className="text-slate-500 mt-1">Analyze job postings and recruiter messages for red flags</p>
      </div>

      {error && (
        <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm flex items-center gap-2">
          ⚠ {error}
          <button onClick={() => setError('')} className="ml-auto"><X className="w-4 h-4" /></button>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
        {/* Form */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-800 mb-4 flex items-center gap-2">
              <Search className="w-5 h-5 text-indigo-500" /> Scan Details
            </h3>
            <form onSubmit={handleScan} className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">Company Name</label>
                  <input value={companyName} onChange={(e) => setCompanyName(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="Acme Corp" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-700 mb-1">Job Title</label>
                  <input value={jobTitle} onChange={(e) => setJobTitle(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="SDE Intern" />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Recruiter Contact</label>
                <input value={recruiterContact} onChange={(e) => setRecruiterContact(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="hr@company.com or WhatsApp number" />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Application URL</label>
                <input type="url" value={applicationUrl} onChange={(e) => setApplicationUrl(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="https://..." />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Stipend / Salary Offered</label>
                <input value={stipendSalary} onChange={(e) => setStipendSalary(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50" placeholder="₹50,000/month" />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Job Description</label>
                <textarea rows={3} value={jobDescription} onChange={(e) => setJobDescription(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50 resize-none" placeholder="Paste the JD..." />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-700 mb-1">Recruiter Message</label>
                <textarea rows={3} value={recruiterMessage} onChange={(e) => setRecruiterMessage(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50 resize-none" placeholder="Paste any suspicious message from the recruiter..." />
              </div>
              <button type="submit" disabled={scanning}
                className="w-full py-3 bg-indigo-600 text-white font-semibold rounded-xl hover:bg-indigo-700 transition-all flex items-center justify-center gap-2 shadow-lg shadow-indigo-500/25 disabled:opacity-60">
                {scanning ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div> :
                  <><ShieldAlert className="w-5 h-5" /> Scan for Red Flags <ArrowRight className="w-4 h-4" /></>}
              </button>
            </form>
          </div>
        </div>

        {/* Results & History */}
        <div className="lg:col-span-3 space-y-4">
          {result && (
            <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
              <h3 className="text-lg font-semibold text-slate-800 mb-4">Scan Result</h3>
              {renderScanResult(result)}
            </div>
          )}

          <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-800 mb-4">Scan History</h3>
            {history.length === 0 ? (
              <div className="text-center py-12 text-slate-400">
                <ShieldAlert className="w-12 h-12 mx-auto mb-3 opacity-40" />
                <p className="text-sm">No scans yet. Enter details above to check.</p>
              </div>
            ) : (
              <div className="space-y-2">
                {history.filter(h => h.id !== result?.id).map((scan) => {
                  const cfg = RISK_CONFIG[scan.overallRiskLevel] || RISK_CONFIG.MEDIUM;
                  return (
                    <div key={scan.id} className="border border-slate-100 rounded-xl overflow-hidden">
                      <button onClick={() => setExpandedHistory(expandedHistory === scan.id ? null : scan.id)}
                        className="w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors">
                        <div className="flex items-center gap-3">
                          <cfg.icon className={`w-5 h-5 ${cfg.color}`} />
                          <div className="text-left">
                            <p className="text-sm font-medium text-slate-700">{scan.companyName || scan.jobTitle || 'Unknown'}</p>
                            <p className="text-xs text-slate-400">{scan.overallRiskLevel} Risk • {new Date(scan.createdAt).toLocaleDateString()}</p>
                          </div>
                        </div>
                        {expandedHistory === scan.id ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
                      </button>
                      {expandedHistory === scan.id && (
                        <div className="p-4 pt-0 border-t border-slate-100">
                          {renderScanResult(scan)}
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
