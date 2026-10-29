import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, Briefcase, Search, FileText, ShieldAlert, Users, MessageCircle,
  Settings, LogOut, GraduationCap, ChevronLeft, ChevronRight
} from 'lucide-react';
import { useState } from 'react';

const studentLinks = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/applications', icon: Briefcase, label: 'Applications' },
  { to: '/opportunities', icon: Search, label: 'Opportunities' },
  { to: '/resume', icon: FileText, label: 'Resume Analyzer' },
  { to: '/scam-detector', icon: ShieldAlert, label: 'Scam Detector' },
  { to: '/community', icon: MessageCircle, label: 'Community' },
  { to: '/referrals', icon: Users, label: 'Referrals' },
  { to: '/profile', icon: Settings, label: 'Profile' },
];

const mentorLinks = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/opportunities', icon: Search, label: 'Opportunities' },
  { to: '/community', icon: MessageCircle, label: 'Community' },
  { to: '/referrals', icon: Users, label: 'Referrals' },
  { to: '/profile', icon: Settings, label: 'Profile' },
];

const adminLinks = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/admin', icon: ShieldAlert, label: 'Admin Panel' },
  { to: '/opportunities', icon: Search, label: 'Opportunities' },
  { to: '/community', icon: MessageCircle, label: 'Community' },
  { to: '/profile', icon: Settings, label: 'Profile' },
];

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);

  const links = user?.role === 'ADMIN' ? adminLinks : user?.role === 'MENTOR' ? mentorLinks : studentLinks;

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <aside
      className={`fixed left-0 top-0 h-screen bg-slate-900 text-white flex flex-col z-40 transition-all duration-300 ${collapsed ? 'w-[72px]' : 'w-60'}`}
      id="main-sidebar"
    >
      {/* Logo */}
      <div className={`h-16 flex items-center border-b border-slate-700/50 px-4 ${collapsed ? 'justify-center' : 'gap-3'}`}>
        <div className="w-9 h-9 bg-indigo-600 rounded-xl flex items-center justify-center flex-shrink-0">
          <GraduationCap className="w-5 h-5" />
        </div>
        {!collapsed && <span className="text-lg font-bold tracking-tight">InternTrack</span>}
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-4 px-2 space-y-1 overflow-y-auto">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 group
              ${isActive
                ? 'bg-indigo-600/20 text-indigo-300'
                : 'text-slate-400 hover:bg-slate-800 hover:text-white'
              } ${collapsed ? 'justify-center' : ''}`
            }
            title={collapsed ? link.label : ''}
          >
            <link.icon className="w-5 h-5 flex-shrink-0" />
            {!collapsed && <span>{link.label}</span>}
          </NavLink>
        ))}
      </nav>

      {/* User info & Logout */}
      <div className="p-3 border-t border-slate-700/50">
        {!collapsed && user && (
          <div className="px-3 py-2 mb-2">
            <p className="text-sm font-medium text-slate-200 truncate">{user.fullName}</p>
            <p className="text-xs text-slate-500 truncate">{user.email}</p>
            <span className="inline-block mt-1 px-2 py-0.5 bg-indigo-600/20 text-indigo-300 text-xs rounded-full font-medium">
              {user.role}
            </span>
          </div>
        )}
        <button
          onClick={handleLogout}
          className={`flex items-center gap-2 px-3 py-2.5 rounded-xl text-sm text-red-400 hover:bg-red-500/10 hover:text-red-300 transition-all w-full ${collapsed ? 'justify-center' : ''}`}
          id="logout-btn"
          title="Logout"
        >
          <LogOut className="w-5 h-5 flex-shrink-0" />
          {!collapsed && <span>Logout</span>}
        </button>
      </div>

      {/* Collapse toggle */}
      <button
        onClick={() => setCollapsed(!collapsed)}
        className="absolute -right-3 top-20 w-6 h-6 bg-slate-700 rounded-full flex items-center justify-center border border-slate-600 hover:bg-slate-600 transition-colors"
        id="sidebar-toggle"
      >
        {collapsed ? <ChevronRight className="w-3 h-3" /> : <ChevronLeft className="w-3 h-3" />}
      </button>
    </aside>
  );
}
