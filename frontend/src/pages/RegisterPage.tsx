import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserPlus, Eye, EyeOff, ArrowRight, GraduationCap, BookOpen, Briefcase } from 'lucide-react';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [role, setRole] = useState<'STUDENT' | 'MENTOR'>('STUDENT');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [collegeName, setCollegeName] = useState('');
  const [degree, setDegree] = useState('');
  const [branch, setBranch] = useState('');
  const [graduationYear, setGraduationYear] = useState('');
  const [currentCompany, setCurrentCompany] = useState('');
  const [currentRole, setCurrentRole] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register({
        fullName, email, password, role, collegeName,
        degree: role === 'STUDENT' ? degree : undefined,
        branch: role === 'STUDENT' ? branch : undefined,
        graduationYear: role === 'STUDENT' && graduationYear ? parseInt(graduationYear) : undefined,
        currentCompany: role === 'MENTOR' ? currentCompany : undefined,
        currentRole: role === 'MENTOR' ? currentRole : undefined,
      });
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex" id="register-page">
      {/* Left decorative panel */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-emerald-600 via-teal-600 to-cyan-700 relative overflow-hidden items-center justify-center p-12">
        <div className="absolute inset-0 opacity-10">
          <div className="absolute top-20 right-20 w-72 h-72 bg-white rounded-full blur-3xl"></div>
          <div className="absolute bottom-10 left-10 w-96 h-96 bg-teal-200 rounded-full blur-3xl"></div>
        </div>
        <div className="relative z-10 text-white max-w-lg">
          <div className="flex items-center gap-3 mb-8">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <GraduationCap className="w-7 h-7" />
            </div>
            <span className="text-2xl font-bold tracking-tight">InternTrack</span>
          </div>
          <h1 className="text-4xl font-extrabold mb-6 leading-tight">
            Start Your<br />
            <span className="text-emerald-200">Career Journey Today</span>
          </h1>
          <p className="text-emerald-100 text-lg leading-relaxed">
            Join thousands of students and mentors already using InternTrack to land dream internships and placements.
          </p>
        </div>
      </div>

      {/* Right form */}
      <div className="flex-1 flex items-center justify-center p-8 bg-slate-50 overflow-y-auto">
        <div className="w-full max-w-md">
          <div className="lg:hidden flex items-center gap-2 mb-6 justify-center">
            <div className="w-10 h-10 bg-emerald-600 rounded-xl flex items-center justify-center">
              <GraduationCap className="w-6 h-6 text-white" />
            </div>
            <span className="text-xl font-bold text-slate-800">InternTrack</span>
          </div>

          <div className="bg-white rounded-2xl shadow-xl shadow-slate-200/50 border border-slate-100 p-8">
            <h2 className="text-2xl font-bold text-slate-800 text-center mb-2">Create Account</h2>
            <p className="text-slate-500 text-center text-sm mb-6">Choose your role to get started</p>

            {/* Role Toggle */}
            <div className="flex bg-slate-100 rounded-xl p-1 mb-6" id="role-toggle">
              {([
                { value: 'STUDENT', label: 'Student', icon: BookOpen },
                { value: 'MENTOR', label: 'Mentor / Alumni', icon: Briefcase },
              ] as const).map((r) => (
                <button
                  key={r.value}
                  type="button"
                  onClick={() => setRole(r.value)}
                  className={`flex-1 py-2.5 px-3 rounded-lg text-sm font-medium flex items-center justify-center gap-2 transition-all ${role === r.value
                    ? 'bg-white text-slate-800 shadow-sm'
                    : 'text-slate-500 hover:text-slate-700'
                    }`}
                >
                  <r.icon className="w-4 h-4" />
                  {r.label}
                </button>
              ))}
            </div>

            {error && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm" id="register-error">
                ⚠ {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4" id="register-form">
              <div>
                <label htmlFor="reg-name" className="block text-sm font-medium text-slate-700 mb-1">Full Name</label>
                <input type="text" id="reg-name" required value={fullName} onChange={(e) => setFullName(e.target.value)}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all" placeholder="John Doe" />
              </div>
              <div>
                <label htmlFor="reg-email" className="block text-sm font-medium text-slate-700 mb-1">Email</label>
                <input type="email" id="reg-email" required value={email} onChange={(e) => setEmail(e.target.value)}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all" placeholder="you@university.edu" />
              </div>
              <div>
                <label htmlFor="reg-password" className="block text-sm font-medium text-slate-700 mb-1">Password</label>
                <div className="relative">
                  <input type={showPassword ? 'text' : 'password'} id="reg-password" required minLength={8} value={password} onChange={(e) => setPassword(e.target.value)}
                    className="w-full px-4 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all pr-12" placeholder="Min. 8 characters" />
                  <button type="button" onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>
              </div>
              <div>
                <label htmlFor="reg-college" className="block text-sm font-medium text-slate-700 mb-1">College / University</label>
                <input type="text" id="reg-college" value={collegeName} onChange={(e) => setCollegeName(e.target.value)}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all" placeholder="IIT Delhi" />
              </div>

              {role === 'STUDENT' && (
                <>
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label htmlFor="reg-degree" className="block text-sm font-medium text-slate-700 mb-1">Degree</label>
                      <input type="text" id="reg-degree" value={degree} onChange={(e) => setDegree(e.target.value)}
                        className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all" placeholder="B.Tech" />
                    </div>
                    <div>
                      <label htmlFor="reg-branch" className="block text-sm font-medium text-slate-700 mb-1">Branch</label>
                      <input type="text" id="reg-branch" value={branch} onChange={(e) => setBranch(e.target.value)}
                        className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all" placeholder="CSE" />
                    </div>
                  </div>
                  <div>
                    <label htmlFor="reg-grad-year" className="block text-sm font-medium text-slate-700 mb-1">Graduation Year</label>
                    <input type="number" id="reg-grad-year" value={graduationYear} onChange={(e) => setGraduationYear(e.target.value)}
                      className="w-full px-4 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all" placeholder="2026" min={2020} max={2035} />
                  </div>
                </>
              )}

              {role === 'MENTOR' && (
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label htmlFor="reg-company" className="block text-sm font-medium text-slate-700 mb-1">Company</label>
                    <input type="text" id="reg-company" value={currentCompany} onChange={(e) => setCurrentCompany(e.target.value)}
                      className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all" placeholder="Google" />
                  </div>
                  <div>
                    <label htmlFor="reg-role" className="block text-sm font-medium text-slate-700 mb-1">Role</label>
                    <input type="text" id="reg-role" value={currentRole} onChange={(e) => setCurrentRole(e.target.value)}
                      className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50 focus:bg-white transition-all" placeholder="SDE" />
                  </div>
                </div>
              )}

              <button type="submit" disabled={loading} id="register-submit-btn"
                className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-xl transition-all flex items-center justify-center gap-2 shadow-lg shadow-emerald-500/25 disabled:opacity-60">
                {loading ? (
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                ) : (
                  <>
                    <UserPlus className="w-5 h-5" />
                    Create Account
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </form>

            <div className="mt-5 text-center text-sm text-slate-500">
              Already have an account?{' '}
              <Link to="/login" className="text-emerald-600 hover:text-emerald-700 font-semibold">Sign In</Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
