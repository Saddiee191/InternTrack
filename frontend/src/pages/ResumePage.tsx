import { useEffect, useState, useRef, type FormEvent } from 'react';
import { resumeService } from '../services/resumeService';
import type { ResumeFile, ResumeAnalysis } from '../types';
import {
  Upload, FileText, Trash2, Search, CheckCircle2, AlertTriangle, XCircle, Info,
  ArrowRight, Sparkles, X, ChevronDown, ChevronUp
} from 'lucide-react';

const FINDING_ICONS: Record<string, typeof CheckCircle2> = {
  STRENGTH: CheckCircle2,
  IMPROVEMENT: AlertTriangle,
  MISSING: XCircle,
  WARNING: Info,
};
const FINDING_COLORS: Record<string, string> = {
  STRENGTH: 'text-emerald-600 bg-emerald-50 border-emerald-200',
  IMPROVEMENT: 'text-amber-600 bg-amber-50 border-amber-200',
  MISSING: 'text-red-600 bg-red-50 border-red-200',
  WARNING: 'text-blue-600 bg-blue-50 border-blue-200',
};

export default function ResumePage() {
  const [files, setFiles] = useState<ResumeFile[]>([]);
  const [analyses, setAnalyses] = useState<ResumeAnalysis[]>([]);
  const [uploading, setUploading] = useState(false);
  const [analyzing, setAnalyzing] = useState(false);
  const [selectedFileId, setSelectedFileId] = useState<number | null>(null);
  const [jobTitle, setJobTitle] = useState('');
  const [jobDescription, setJobDescription] = useState('');
  const [activeAnalysis, setActiveAnalysis] = useState<ResumeAnalysis | null>(null);
  const [loaded, setLoaded] = useState(false);
  const [expandedAnalysis, setExpandedAnalysis] = useState<number | null>(null);
  const [error, setError] = useState('');
  const fileRef = useRef<HTMLInputElement>(null);

  const loadData = async () => {
    if (loaded) return;
    try {
      const [f, a] = await Promise.all([
        resumeService.listFiles().catch(() => []),
        resumeService.listAnalyses().catch(() => []),
      ]);
      setFiles(f);
      setAnalyses(a);
      setLoaded(true);
    } catch { /* empty */ }
  };

  // Load on mount
  useEffect(() => { void loadData(); }, []);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    if (file.type !== 'application/pdf') {
      setError('Only PDF files are accepted');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setError('File must be under 5MB');
      return;
    }
    setError('');
    setUploading(true);
    try {
      const uploaded = await resumeService.upload(file);
      setFiles([uploaded, ...files]);
      setSelectedFileId(uploaded.id);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Upload failed');
    } finally {
      setUploading(false);
      if (fileRef.current) fileRef.current.value = '';
    }
  };

  const handleAnalyze = async (e: FormEvent) => {
    e.preventDefault();
    if (!selectedFileId) return;
    setError('');
    setAnalyzing(true);
    try {
      const result = await resumeService.analyze(selectedFileId, jobTitle || undefined, jobDescription || undefined);
      setActiveAnalysis(result);
      setAnalyses([result, ...analyses]);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Analysis failed');
    } finally {
      setAnalyzing(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this resume?')) return;
    await resumeService.deleteFile(id);
    setFiles(files.filter(f => f.id !== id));
    if (selectedFileId === id) setSelectedFileId(null);
  };

  const scoreColor = (score: number) => {
    if (score >= 80) return 'text-emerald-600';
    if (score >= 60) return 'text-amber-600';
    return 'text-red-600';
  };

  const renderAnalysisDetail = (analysis: ResumeAnalysis) => (
    <div className="space-y-4">
      {/* Scores */}
      <div className="grid grid-cols-2 gap-4">
        <div className="bg-slate-50 rounded-xl p-4 text-center">
          <p className="text-xs text-slate-500 mb-1">Completeness Score</p>
          <p className={`text-3xl font-bold ${scoreColor(analysis.completenessScore)}`}>
            {analysis.completenessScore}%
          </p>
        </div>
        <div className="bg-slate-50 rounded-xl p-4 text-center">
          <p className="text-xs text-slate-500 mb-1">Match Percentage</p>
          <p className={`text-3xl font-bold ${scoreColor(analysis.matchPercentage)}`}>
            {analysis.matchPercentage}%
          </p>
        </div>
      </div>

      {/* Skills */}
      {analysis.matchedSkillsJson && (() => {
        try {
          const matched = JSON.parse(analysis.matchedSkillsJson) as string[];
          const missing = analysis.missingSkillsJson ? JSON.parse(analysis.missingSkillsJson) as string[] : [];
          return (
            <div className="grid grid-cols-2 gap-4">
              {matched.length > 0 && (
                <div>
                  <p className="text-xs font-medium text-emerald-700 mb-2">✓ Matched Skills</p>
                  <div className="flex flex-wrap gap-1.5">
                    {matched.map((s, i) => (
                      <span key={i} className="text-xs bg-emerald-50 text-emerald-700 px-2 py-0.5 rounded-md">{s}</span>
                    ))}
                  </div>
                </div>
              )}
              {missing.length > 0 && (
                <div>
                  <p className="text-xs font-medium text-red-600 mb-2">✗ Missing Skills</p>
                  <div className="flex flex-wrap gap-1.5">
                    {missing.map((s, i) => (
                      <span key={i} className="text-xs bg-red-50 text-red-600 px-2 py-0.5 rounded-md">{s}</span>
                    ))}
                  </div>
                </div>
              )}
            </div>
          );
        } catch { return null; }
      })()}

      {/* Summary */}
      <div className="bg-indigo-50 border border-indigo-200 rounded-xl p-4">
        <p className="text-sm text-indigo-800 whitespace-pre-line">{analysis.summary}</p>
      </div>

      {/* Findings */}
      {analysis.findings && analysis.findings.length > 0 && (
        <div className="space-y-2">
          <p className="text-sm font-semibold text-slate-700">Detailed Findings</p>
          {analysis.findings.map((f, i) => {
            const Icon = FINDING_ICONS[f.findingType] || Info;
            const colors = FINDING_COLORS[f.findingType] || 'text-slate-600 bg-slate-50 border-slate-200';
            return (
              <div key={i} className={`p-3 rounded-xl border ${colors}`}>
                <div className="flex items-center gap-2 mb-1">
                  <Icon className="w-4 h-4 flex-shrink-0" />
                  <span className="text-sm font-medium">{f.title}</span>
                  <span className="text-xs opacity-60 ml-auto">{f.sectionName}</span>
                </div>
                <p className="text-xs opacity-80">{f.description}</p>
                {f.suggestion && <p className="text-xs mt-1 font-medium">💡 {f.suggestion}</p>}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );

  return (
    <div id="resume-page" className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-800">Resume Analyzer</h1>
        <p className="text-slate-500 mt-1">Upload your resume for ATS scoring and job-match analysis</p>
      </div>

      {error && (
        <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm flex items-center gap-2">
          <XCircle className="w-4 h-4" /> {error}
          <button onClick={() => setError('')} className="ml-auto"><X className="w-4 h-4" /></button>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left: Upload & Files */}
        <div className="space-y-4">
          {/* Upload Area */}
          <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-800 mb-4 flex items-center gap-2">
              <Upload className="w-5 h-5 text-indigo-500" /> Upload Resume
            </h3>
            <label
              className="flex flex-col items-center justify-center h-32 border-2 border-dashed border-slate-200 rounded-xl cursor-pointer hover:border-indigo-400 hover:bg-indigo-50/50 transition-all"
            >
              <input type="file" ref={fileRef} accept=".pdf" onChange={handleUpload} className="hidden" />
              {uploading ? (
                <div className="w-6 h-6 border-2 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div>
              ) : (
                <>
                  <FileText className="w-8 h-8 text-slate-400 mb-2" />
                  <span className="text-sm text-slate-500">Drop PDF here or click</span>
                  <span className="text-xs text-slate-400 mt-1">Max 5MB</span>
                </>
              )}
            </label>
          </div>

          {/* File List */}
          <div className="bg-white rounded-2xl border border-slate-100 p-4 shadow-sm">
            <h3 className="text-sm font-semibold text-slate-700 mb-3">Your Resumes</h3>
            {files.length === 0 ? (
              <p className="text-xs text-slate-400 text-center py-4">No resumes uploaded yet</p>
            ) : (
              <div className="space-y-2">
                {files.map((f) => (
                  <div key={f.id}
                    className={`flex items-center justify-between p-3 rounded-xl cursor-pointer transition-all ${selectedFileId === f.id ? 'bg-indigo-50 border border-indigo-200' : 'bg-slate-50 hover:bg-slate-100'}`}
                    onClick={() => setSelectedFileId(f.id)}>
                    <div className="flex items-center gap-2 min-w-0">
                      <FileText className="w-4 h-4 text-indigo-500 flex-shrink-0" />
                      <div className="min-w-0">
                        <p className="text-sm text-slate-700 truncate">{f.fileName}</p>
                        <p className="text-xs text-slate-400">{(f.fileSize / 1024).toFixed(0)} KB</p>
                      </div>
                    </div>
                    <button onClick={(e) => { e.stopPropagation(); handleDelete(f.id); }}
                      className="p-1 hover:bg-red-50 rounded-lg"><Trash2 className="w-3.5 h-3.5 text-red-400" /></button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Analyze Form */}
          {selectedFileId && (
            <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
              <h3 className="text-lg font-semibold text-slate-800 mb-4 flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-amber-500" /> Analyze Resume
              </h3>
              <form onSubmit={handleAnalyze} className="space-y-3">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Target Job Title</label>
                  <input type="text" value={jobTitle} onChange={(e) => setJobTitle(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50"
                    placeholder="SDE Intern" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Job Description (optional)</label>
                  <textarea rows={4} value={jobDescription} onChange={(e) => setJobDescription(e.target.value)}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50 resize-none"
                    placeholder="Paste the JD for targeted analysis..." />
                </div>
                <button type="submit" disabled={analyzing}
                  className="w-full py-2.5 bg-indigo-600 text-white font-semibold rounded-xl hover:bg-indigo-700 transition-all flex items-center justify-center gap-2 shadow-lg shadow-indigo-500/25 disabled:opacity-60 text-sm">
                  {analyzing ? <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div> :
                    <><Search className="w-4 h-4" /> Analyze <ArrowRight className="w-4 h-4" /></>}
                </button>
              </form>
            </div>
          )}
        </div>

        {/* Right: Analysis Results */}
        <div className="lg:col-span-2 space-y-4">
          {/* Active Analysis (just completed) */}
          {activeAnalysis && (
            <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
                  <Sparkles className="w-5 h-5 text-amber-500" /> Latest Analysis
                </h3>
                <span className="text-xs text-slate-400">{new Date(activeAnalysis.createdAt).toLocaleDateString()}</span>
              </div>
              {renderAnalysisDetail(activeAnalysis)}
            </div>
          )}

          {/* History */}
          <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-800 mb-4">Analysis History</h3>
            {analyses.length === 0 ? (
              <div className="text-center py-12 text-slate-400">
                <FileText className="w-12 h-12 mx-auto mb-3 opacity-40" />
                <p className="text-sm">No analyses yet. Upload a resume and analyze it!</p>
              </div>
            ) : (
              <div className="space-y-3">
                {analyses.filter(a => a.id !== activeAnalysis?.id).map((a) => (
                  <div key={a.id} className="border border-slate-100 rounded-xl overflow-hidden">
                    <button
                      onClick={() => setExpandedAnalysis(expandedAnalysis === a.id ? null : a.id)}
                      className="w-full flex items-center justify-between p-4 hover:bg-slate-50 transition-colors"
                    >
                      <div className="flex items-center gap-3">
                        <div className={`text-lg font-bold ${scoreColor(a.completenessScore)}`}>{a.completenessScore}%</div>
                        <div className="text-left">
                          <p className="text-sm font-medium text-slate-700">{a.jobTitle || 'General Analysis'}</p>
                          <p className="text-xs text-slate-400">{a.resumeFileName} • {new Date(a.createdAt).toLocaleDateString()}</p>
                        </div>
                      </div>
                      {expandedAnalysis === a.id ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
                    </button>
                    {expandedAnalysis === a.id && (
                      <div className="p-4 pt-0 border-t border-slate-100">
                        {renderAnalysisDetail(a)}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
