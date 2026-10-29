import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';

export default function AppLayout() {
  return (
    <div className="flex min-h-screen bg-slate-50" id="app-layout">
      <Sidebar />
      <main className="flex-1 ml-60 p-8 transition-all duration-300">
        <Outlet />
      </main>
    </div>
  );
}
