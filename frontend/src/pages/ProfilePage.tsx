import { useEffect, useState, type FormEvent } from 'react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import {
  User, Save, Lock, Trash2, Globe, GraduationCap,
  Briefcase, Code, MapPin, AlertTriangle
} from 'lucide-react';

export default function ProfilePage() {
  const { user, refreshUser, logout } = useAuth();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  // Editable fields
  const [fullName, setFullName] = useState('');
  const [collegeName, setCollegeName] = useState('');
  const [degree, setDegree] = useState('');
  const [branch, setBranch] = useState('');
  const [graduationYear, setGraduationYear] = useState('');
  const [cgpa, setCgpa] = useState('');
  const [skills, setSkills] = useState('');
  const [interests, setInterests] = useState('');
  const [preferredRoles, setPreferredRoles] = useState('');
  const [preferredLocations, setPreferredLocations] = useState('');
  const [portfolioUrl, setPortfolioUrl] = useState('');
  const [githubUrl, setGithubUrl] = useState('');
  const [linkedinUrl, setLinkedinUrl] = useState('');
  // Mentor
  const [fieldOfWork, setFieldOfWork] = useState('');
  const [currentCompany, setCurrentCompany] = useState('');
  const [currentRole, setCurrentRole] = useState('');
  const [yearsOfExperience, setYearsOfExperience] = useState('');
  const [bio, setBio] = useState('');
  const [openForReferrals, setOpenForReferrals] = useState(true);
  // Password
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [passwordMsg, setPasswordMsg] = useState('');

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const data = await authService.getProfile();
      const u = data.user as Record<string, unknown>;
      setFullName((u?.fullName as string) || '');

      if (data.studentProfile) {
        const sp = data.studentProfile;
        setCollegeName(sp.collegeName || '');
        setDegree(sp.degree || '');
        setBranch(sp.branch || '');
        setGraduationYear(sp.graduationYear?.toString() || '');
        setCgpa(sp.cgpa?.toString() || '');
        setPortfolioUrl(sp.portfolioUrl || '');
        setGithubUrl(sp.githubUrl || '');
        setLinkedinUrl(sp.linkedinUrl || '');
        try { setSkills(JSON.parse(sp.skillsJson || '[]').join(', ')); } catch { setSkills(''); }
        try { setInterests(JSON.parse(sp.interestsJson || '[]').join(', ')); } catch { setInterests(''); }
        try { setPreferredRoles(JSON.parse(sp.preferredRolesJson || '[]').join(', ')); } catch { setPreferredRoles(''); }
        try { setPreferredLocations(JSON.parse(sp.preferredLocationsJson || '[]').join(', ')); } catch { setPreferredLocations(''); }
      }
      if (data.mentorProfile) {
        const mp = data.mentorProfile;
        setCollegeName(mp.collegeName || '');
        setFieldOfWork(mp.fieldOfWork || '');
        setCurrentCompany(mp.currentCompany || '');
        setCurrentRole(mp.currentRole || '');
        setYearsOfExperience(mp.yearsOfExperience?.toString() || '');
        setBio(mp.bio || '');
        setLinkedinUrl(mp.linkedinUrl || '');
        setOpenForReferrals(mp.openForReferrals ?? true);
        try { setSkills(JSON.parse(mp.skillsJson || '[]').join(', ')); } catch { setSkills(''); }
      }
    } catch { /* empty */ }
    setLoading(false);
  };

  const handleSave = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');
    try {
      const toArray = (s: string) => s.split(',').map(x => x.trim()).filter(Boolean);
      await authService.updateProfile({
        fullName,
        collegeName,
        degree: user?.role === 'STUDENT' ? degree : undefined,
        branch: user?.role === 'STUDENT' ? branch : undefined,
        graduationYear: user?.role === 'STUDENT' && graduationYear ? parseInt(graduationYear) : undefined,
        cgpa: user?.role === 'STUDENT' && cgpa ? parseFloat(cgpa) : undefined,
        skillsJson: JSON.stringify(toArray(skills)),
        interestsJson: user?.role === 'STUDENT' ? JSON.stringify(toArray(interests)) : undefined,
        preferredRolesJson: user?.role === 'STUDENT' ? JSON.stringify(toArray(preferredRoles)) : undefined,
        preferredLocationsJson: user?.role === 'STUDENT' ? JSON.stringify(toArray(preferredLocations)) : undefined,
        portfolioUrl: user?.role === 'STUDENT' ? portfolioUrl : undefined,
        githubUrl: user?.role === 'STUDENT' ? githubUrl : undefined,
        linkedinUrl,
        fieldOfWork: user?.role === 'MENTOR' ? fieldOfWork : undefined,
        currentCompany: user?.role === 'MENTOR' ? currentCompany : undefined,
        currentRole: user?.role === 'MENTOR' ? currentRole : undefined,
        yearsOfExperience: user?.role === 'MENTOR' && yearsOfExperience ? parseInt(yearsOfExperience) : undefined,
        bio: user?.role === 'MENTOR' ? bio : undefined,
        openForReferrals: user?.role === 'MENTOR' ? openForReferrals : undefined,
      });
      setSuccess('Profile updated successfully!');
      await refreshUser();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Update failed');
    }
    setSaving(false);
  };

  const handleChangePassword = async (e: FormEvent) => {
    e.preventDefault();
    setPasswordMsg('');
    try {
      await authService.changePassword({ currentPassword, newPassword });
      setPasswordMsg('Password changed!');
      setCurrentPassword('');
      setNewPassword('');
    } catch (err) {
      setPasswordMsg(err instanceof Error ? err.message : 'Failed');
    }
  };

  const handleDeleteAccount = async () => {
    if (!confirm('This will permanently delete your account. Are you sure?')) return;
    try {
      await authService.deleteAccount();
      await logout();
    } catch { /* empty */ }
  };

  if (loading) {
    return <div className="flex justify-center py-16"><div className="w-8 h-8 border-3 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div></div>;
  }

  const inputClass = "w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-slate-50 focus:bg-white transition-all";

  return (
    <div id="profile-page" className="max-w-3xl mx-auto space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-slate-800">Profile</h1>
        <p className="text-slate-500 mt-1">Manage your personal information and preferences</p>
      </div>

      {success && <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl text-emerald-700 text-sm">✓ {success}</div>}
      {error && <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">⚠ {error}</div>}

      {/* Profile Form */}
      <form onSubmit={handleSave} className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm space-y-5">
        <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2"><User className="w-5 h-5 text-indigo-500" /> Personal Info</h3>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Full Name</label>
            <input value={fullName} onChange={(e) => setFullName(e.target.value)} className={inputClass} />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Email</label>
            <input value={user?.email || ''} disabled className={`${inputClass} opacity-60`} />
          </div>
        </div>
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1"><GraduationCap className="w-4 h-4 inline" /> College</label>
          <input value={collegeName} onChange={(e) => setCollegeName(e.target.value)} className={inputClass} placeholder="IIT Delhi" />
        </div>

        {user?.role === 'STUDENT' && (
          <>
            <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2 pt-2"><GraduationCap className="w-5 h-5 text-indigo-500" /> Academic</h3>
            <div className="grid grid-cols-3 gap-4">
              <div><label className="block text-xs font-medium text-slate-700 mb-1">Degree</label><input value={degree} onChange={(e) => setDegree(e.target.value)} className={inputClass} placeholder="B.Tech" /></div>
              <div><label className="block text-xs font-medium text-slate-700 mb-1">Branch</label><input value={branch} onChange={(e) => setBranch(e.target.value)} className={inputClass} placeholder="CSE" /></div>
              <div><label className="block text-xs font-medium text-slate-700 mb-1">Grad Year</label><input type="number" value={graduationYear} onChange={(e) => setGraduationYear(e.target.value)} className={inputClass} /></div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div><label className="block text-xs font-medium text-slate-700 mb-1">CGPA</label><input type="number" step="0.01" value={cgpa} onChange={(e) => setCgpa(e.target.value)} className={inputClass} placeholder="8.5" /></div>
            </div>
            <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2 pt-2"><Code className="w-5 h-5 text-indigo-500" /> Skills & Interests</h3>
            <div><label className="block text-xs font-medium text-slate-700 mb-1">Skills (comma-separated)</label><input value={skills} onChange={(e) => setSkills(e.target.value)} className={inputClass} placeholder="React, Java, Python" /></div>
            <div><label className="block text-xs font-medium text-slate-700 mb-1">Interests</label><input value={interests} onChange={(e) => setInterests(e.target.value)} className={inputClass} placeholder="ML, Web Dev" /></div>
            <div className="grid grid-cols-2 gap-4">
              <div><label className="block text-xs font-medium text-slate-700 mb-1"><MapPin className="w-3 h-3 inline" /> Preferred Roles</label><input value={preferredRoles} onChange={(e) => setPreferredRoles(e.target.value)} className={inputClass} placeholder="SDE, ML Engineer" /></div>
              <div><label className="block text-xs font-medium text-slate-700 mb-1"><MapPin className="w-3 h-3 inline" /> Preferred Locations</label><input value={preferredLocations} onChange={(e) => setPreferredLocations(e.target.value)} className={inputClass} placeholder="Bangalore, Remote" /></div>
            </div>
            <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2 pt-2"><Globe className="w-5 h-5 text-indigo-500" /> Links</h3>
            <div className="grid grid-cols-3 gap-4">
              <div><label className="block text-xs font-medium text-slate-700 mb-1"><Globe className="w-3 h-3 inline" /> Portfolio</label><input value={portfolioUrl} onChange={(e) => setPortfolioUrl(e.target.value)} className={inputClass} /></div>
              <div><label className="block text-xs font-medium text-slate-700 mb-1">GitHub</label><input value={githubUrl} onChange={(e) => setGithubUrl(e.target.value)} className={inputClass} /></div>
              <div><label className="block text-xs font-medium text-slate-700 mb-1">LinkedIn</label><input value={linkedinUrl} onChange={(e) => setLinkedinUrl(e.target.value)} className={inputClass} /></div>
            </div>
          </>
        )}

        {user?.role === 'MENTOR' && (
          <>
            <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2 pt-2"><Briefcase className="w-5 h-5 text-indigo-500" /> Professional</h3>
            <div className="grid grid-cols-2 gap-4">
              <div><label className="block text-xs font-medium text-slate-700 mb-1">Company</label><input value={currentCompany} onChange={(e) => setCurrentCompany(e.target.value)} className={inputClass} /></div>
              <div><label className="block text-xs font-medium text-slate-700 mb-1">Role</label><input value={currentRole} onChange={(e) => setCurrentRole(e.target.value)} className={inputClass} /></div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div><label className="block text-xs font-medium text-slate-700 mb-1">Field of Work</label><input value={fieldOfWork} onChange={(e) => setFieldOfWork(e.target.value)} className={inputClass} /></div>
              <div><label className="block text-xs font-medium text-slate-700 mb-1">Years of Experience</label><input type="number" value={yearsOfExperience} onChange={(e) => setYearsOfExperience(e.target.value)} className={inputClass} /></div>
            </div>
            <div><label className="block text-xs font-medium text-slate-700 mb-1">Skills</label><input value={skills} onChange={(e) => setSkills(e.target.value)} className={inputClass} placeholder="Java, System Design" /></div>
            <div><label className="block text-xs font-medium text-slate-700 mb-1">Bio</label><textarea rows={3} value={bio} onChange={(e) => setBio(e.target.value)} className={`${inputClass} resize-none`} /></div>
            <div><label className="block text-xs font-medium text-slate-700 mb-1">LinkedIn</label><input value={linkedinUrl} onChange={(e) => setLinkedinUrl(e.target.value)} className={inputClass} /></div>
            <label className="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" checked={openForReferrals} onChange={(e) => setOpenForReferrals(e.target.checked)} className="rounded border-slate-300" />
              <span className="text-sm text-slate-700">Open for referral requests</span>
            </label>
          </>
        )}

        <button type="submit" disabled={saving}
          className="px-8 py-3 bg-indigo-600 text-white font-semibold rounded-xl hover:bg-indigo-700 transition-all flex items-center gap-2 shadow-lg shadow-indigo-500/25 disabled:opacity-60">
          {saving ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div> :
            <><Save className="w-5 h-5" /> Save Profile</>}
        </button>
      </form>

      {/* Change Password */}
      <form onSubmit={handleChangePassword} className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm space-y-4">
        <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2"><Lock className="w-5 h-5 text-amber-500" /> Change Password</h3>
        {passwordMsg && <div className="p-2 bg-slate-50 rounded-lg text-sm text-slate-700">{passwordMsg}</div>}
        <div className="grid grid-cols-2 gap-4">
          <div><label className="block text-xs font-medium text-slate-700 mb-1">Current Password</label><input type="password" required value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} className={inputClass} /></div>
          <div><label className="block text-xs font-medium text-slate-700 mb-1">New Password</label><input type="password" required minLength={8} value={newPassword} onChange={(e) => setNewPassword(e.target.value)} className={inputClass} /></div>
        </div>
        <button type="submit" className="px-6 py-2.5 bg-amber-600 text-white font-medium rounded-xl hover:bg-amber-700 transition-all text-sm">Change Password</button>
      </form>

      {/* Danger Zone */}
      <div className="bg-white rounded-2xl border border-red-200 p-6 shadow-sm">
        <h3 className="text-lg font-semibold text-red-700 flex items-center gap-2 mb-2"><AlertTriangle className="w-5 h-5" /> Danger Zone</h3>
        <p className="text-sm text-slate-500 mb-4">Once deleted, your account cannot be recovered.</p>
        <button onClick={handleDeleteAccount} className="px-6 py-2.5 bg-red-600 text-white font-medium rounded-xl hover:bg-red-700 transition-all text-sm flex items-center gap-2">
          <Trash2 className="w-4 h-4" /> Delete Account
        </button>
      </div>
    </div>
  );
}
