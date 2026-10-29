import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { applicationService } from '../services/applicationService';
import type { ApplicationStats, ApplicationReminder } from '../types';
import {
  Briefcase, TrendingUp, Award, Target, Bell, ArrowRight, BarChart3, CalendarClock,
  Users, MessageCircle, ShieldAlert
} from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { Link } from 'react-router-dom';

const STATUS_COLORS: Record<string, string> = {
  SAVED: '#94a3b8',
  APPLIED: '#3b82f6',
  ASSESSMENT: '#f59e0b',
  INTERVIEW: '#8b5cf6',
  OFFER_RECEIVED: '#10b981',
  REJECTED: '#ef4444',
  WITHDRAWN: '#6b7280',
};

const PILL_STYLES: Record<string, string> = {
  SAVED: 'bg-slate-100 text-slate-600',
  APPLIED: 'bg-blue-100 text-blue-700',
  ASSESSMENT: 'bg-amber-100 text-amber-700',
  INTERVIEW: 'bg-violet-100 text-violet-700',
  OFFER_RECEIVED: 'bg-emerald-100 text-emerald-700',
  REJECTED: 'bg-red-100 text-red-700',
  WITHDRAWN: 'bg-gray-100 text-gray-600',
};

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<ApplicationStats | null>(null);
  const [reminders, setReminders] = useState<ApplicationReminder[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user?.role === 'STUDENT') {
      Promise.all([
        applicationService.getStats().catch(() => null),
        applicationService.getReminders().catch(() => []),
      ]).then(([s, r]) => {
        setStats(s);
        setReminders(r);
        setLoading(false);
      });
    } else {
      setLoading(false);
    }
  }, [user]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="w-8 h-8 border-3 border-indigo-200 border-t-indigo-600 rounded-full animate-spin"></div>
      </div>
    );
  }

  const greeting = () => {
    const h = new Date().getHours();
    if (h < 12) return 'Good morning';
    if (h < 17) return 'Good afternoon';
    return 'Good evening';
  };

  const pieData = stats
    ? Object.entries(stats.statusDistribution || {}).map(([name, value]) => ({ name, value }))
    : [];

  const statCards = stats
    ? [
        { label: 'Total Applications', value: stats.totalApplications, icon: Briefcase, color: 'bg-blue-500', lightColor: 'bg-blue-50 text-blue-600' },
        { label: 'In Progress', value: stats.inProgressCount, icon: TrendingUp, color: 'bg-violet-500', lightColor: 'bg-violet-50 text-violet-600' },
        { label: 'Interviews', value: stats.interviewsScheduled, icon: CalendarClock, color: 'bg-amber-500', lightColor: 'bg-amber-50 text-amber-600' },
        { label: 'Offers', value: stats.offersReceived, icon: Award, color: 'bg-emerald-500', lightColor: 'bg-emerald-50 text-emerald-600' },
        { label: 'Assessments', value: stats.assessmentsScheduled, icon: Target, color: 'bg-cyan-500', lightColor: 'bg-cyan-50 text-cyan-600' },
        { label: 'Conversion Rate', value: `${stats.conversionRate}%`, icon: BarChart3, color: 'bg-rose-500', lightColor: 'bg-rose-50 text-rose-600' },
      ]
    : [];

  return (
    <div id="dashboard-page" className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-800">
            {greeting()}, <span className="text-indigo-600">{user?.fullName?.split(' ')[0]}</span>
          </h1>
          <p className="text-slate-500 mt-1">
            {user?.role === 'STUDENT'
              ? "Here's your application tracking overview"
              : user?.role === 'MENTOR'
                ? 'Help students land their dream internships'
                : 'Platform administration overview'}
          </p>
        </div>
        {user?.role === 'STUDENT' && (
          <Link
            to="/applications"
            className="flex items-center gap-2 px-5 py-2.5 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 transition-all shadow-lg shadow-indigo-500/25 font-medium text-sm"
          >
            <Briefcase className="w-4 h-4" />
            Track Application
            <ArrowRight className="w-4 h-4" />
          </Link>
        )}
      </div>

      {/* Student Dashboard */}
      {user?.role === 'STUDENT' && stats && (
        <>
          {/* Stat Cards */}
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {statCards.map((card, i) => (
              <div
                key={i}
                className="bg-white rounded-2xl border border-slate-100 p-5 shadow-sm hover:shadow-md transition-shadow group"
              >
                <div className={`w-10 h-10 ${card.lightColor} rounded-xl flex items-center justify-center mb-3 group-hover:scale-110 transition-transform`}>
                  <card.icon className="w-5 h-5" />
                </div>
                <p className="text-2xl font-bold text-slate-800">{card.value}</p>
                <p className="text-xs text-slate-500 mt-0.5">{card.label}</p>
              </div>
            ))}
          </div>

          {/* Charts Row */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Bar Chart */}
            <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
              <h3 className="text-lg font-semibold text-slate-800 mb-4 flex items-center gap-2">
                <BarChart3 className="w-5 h-5 text-indigo-500" />
                Applications Over Time
              </h3>
              {(stats.applicationsOverTime?.length ?? 0) > 0 ? (
                <ResponsiveContainer width="100%" height={280}>
                  <BarChart data={stats.applicationsOverTime}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
                    <XAxis dataKey="month" tick={{ fontSize: 12 }} stroke="#94a3b8" />
                    <YAxis tick={{ fontSize: 12 }} stroke="#94a3b8" />
                    <Tooltip
                      contentStyle={{ borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}
                    />
                    <Bar dataKey="count" fill="#6366f1" radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              ) : (
                <div className="h-72 flex items-center justify-center text-slate-400 text-sm">
                  No application data yet. Start tracking to see trends!
                </div>
              )}
            </div>

            {/* Pie Chart */}
            <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
              <h3 className="text-lg font-semibold text-slate-800 mb-4">Status Distribution</h3>
              {pieData.length > 0 ? (
                <>
                  <ResponsiveContainer width="100%" height={200}>
                    <PieChart>
                      <Pie data={pieData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} innerRadius={40}>
                        {pieData.map((entry, i) => (
                          <Cell key={i} fill={STATUS_COLORS[entry.name] || '#94a3b8'} />
                        ))}
                      </Pie>
                      <Tooltip />
                    </PieChart>
                  </ResponsiveContainer>
                  <div className="flex flex-wrap gap-2 mt-3">
                    {pieData.map((entry) => (
                      <span key={entry.name} className={`px-2 py-1 rounded-lg text-xs font-medium ${PILL_STYLES[entry.name] || 'bg-slate-100 text-slate-600'}`}>
                        {entry.name.replace('_', ' ')}: {entry.value}
                      </span>
                    ))}
                  </div>
                </>
              ) : (
                <div className="h-52 flex items-center justify-center text-slate-400 text-sm">
                  No data
                </div>
              )}
            </div>
          </div>

          {/* Reminders */}
          {reminders.length > 0 && (
            <div className="bg-white rounded-2xl border border-slate-100 p-6 shadow-sm">
              <h3 className="text-lg font-semibold text-slate-800 mb-4 flex items-center gap-2">
                <Bell className="w-5 h-5 text-amber-500" />
                Upcoming Reminders
              </h3>
              <div className="space-y-3">
                {reminders.slice(0, 5).map((r) => (
                  <div key={r.id} className="flex items-center justify-between p-3 bg-slate-50 rounded-xl">
                    <div className="flex items-center gap-3">
                      <div className="w-2 h-2 bg-amber-500 rounded-full"></div>
                      <div>
                        <p className="text-sm font-medium text-slate-700">
                          {r.companyName} — {r.role}
                        </p>
                        <p className="text-xs text-slate-500">{r.reminderType} • {new Date(r.reminderDate).toLocaleDateString()}</p>
                      </div>
                    </div>
                    {r.notes && <span className="text-xs text-slate-400">{r.notes}</span>}
                  </div>
                ))}
              </div>
            </div>
          )}
        </>
      )}

      {/* Mentor Dashboard */}
      {user?.role === 'MENTOR' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Link to="/referrals" className="bg-white rounded-2xl border border-slate-100 p-8 shadow-sm hover:shadow-md transition-shadow group">
            <div className="w-14 h-14 bg-violet-50 rounded-2xl flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
              <Users className="w-7 h-7 text-violet-600" />
            </div>
            <h3 className="text-xl font-bold text-slate-800 mb-2">Referral Requests</h3>
            <p className="text-slate-500 text-sm">Review and manage referral requests from students</p>
          </Link>
          <Link to="/community" className="bg-white rounded-2xl border border-slate-100 p-8 shadow-sm hover:shadow-md transition-shadow group">
            <div className="w-14 h-14 bg-blue-50 rounded-2xl flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
              <MessageCircle className="w-7 h-7 text-blue-600" />
            </div>
            <h3 className="text-xl font-bold text-slate-800 mb-2">Community</h3>
            <p className="text-slate-500 text-sm">Share interview experiences and career guidance</p>
          </Link>
        </div>
      )}

      {/* Admin Dashboard */}
      {user?.role === 'ADMIN' && (
        <div className="bg-white rounded-2xl border border-slate-100 p-8 shadow-sm text-center">
          <div className="w-16 h-16 bg-red-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <ShieldAlert className="w-8 h-8 text-red-600" />
          </div>
          <h3 className="text-xl font-bold text-slate-800 mb-2">Admin Dashboard</h3>
          <p className="text-slate-500 mb-4">Manage users, opportunities, and content moderation</p>
          <Link to="/admin" className="inline-flex items-center gap-2 px-6 py-3 bg-red-600 text-white rounded-xl hover:bg-red-700 transition-all font-medium">
            Go to Admin Panel <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      )}
    </div>
  );
}
