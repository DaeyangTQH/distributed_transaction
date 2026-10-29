import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { SketchEmptyState } from 'blackchalk'
import { useAuth } from '../context/AuthContext'

export default function ProtectedRoute({ adminOnly = false }) {
  const { user, isAdmin } = useAuth()
  const location = useLocation()

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  if (adminOnly && !isAdmin) {
    return (
      <SketchEmptyState
        icon="lock"
        title="Administrators only"
        description="Log in with an administrator account to open this page."
      />
    )
  }

  return <Outlet />
}
