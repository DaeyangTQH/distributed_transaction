import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import {
  SketchAvatar,
  SketchBadge,
  SketchButton,
  SketchNavBar,
  SketchPaper,
  SketchText,
  SketchThemeToggle,
  space,
} from 'blackchalk'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'

function NavItem({ to, children }) {
  const navigate = useNavigate()
  const { pathname } = useLocation()
  const isActive = to === '/' ? pathname === '/' : pathname.startsWith(to)
  return (
    <SketchButton
      variant={isActive ? 'secondary' : 'tertiary'}
      onClick={() => navigate(to)}
      aria-current={isActive ? 'page' : undefined}
    >
      {children}
    </SketchButton>
  )
}

export default function Layout() {
  const { user, isAdmin, logout } = useAuth()
  const { count } = useCart()
  const navigate = useNavigate()

  const initials = user?.username?.slice(0, 2).toUpperCase() ?? ''

  return (
    <SketchPaper style={{ minHeight: '100vh' }}>
      <div style={{ width: '100%', padding: `${space[6]} ${space[10]}` }}>
        <SketchNavBar
          title={
            <button
              type="button"
              className="sketch-interactive"
              onClick={() => navigate('/')}
              aria-label="Watch Store home"
              style={{
                border: 'none',
                background: 'none',
                padding: 0,
                font: 'inherit',
                color: 'inherit',
                cursor: 'pointer',
              }}
            >
              Watch Store
            </button>
          }
        >
          <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: space[2] }}>
            <NavItem to="/">Catalog</NavItem>
            {user && <NavItem to="/orders">My orders</NavItem>}
            {isAdmin && <NavItem to="/admin/watches">Manage watches</NavItem>}
            {isAdmin && <NavItem to="/admin/orders">All orders</NavItem>}
            <NavItem to="/cart">{`Cart (${count})`}</NavItem>
            {user ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: space[2] }}>
                <SketchAvatar initials={initials} size={36} />
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                  <SketchText variant="label">{user.username}</SketchText>
                  <SketchBadge variant="muted">{user.roleName ?? `Role ${user.role}`}</SketchBadge>
                </div>
                <SketchButton
                  variant="tertiary"
                  onClick={() => {
                    logout()
                    navigate('/login')
                  }}
                >
                  Log out
                </SketchButton>
              </div>
            ) : (
              <SketchButton variant="primary" onClick={() => navigate('/login')}>
                Log in
              </SketchButton>
            )}
            <SketchThemeToggle label="Dark" />
          </div>
        </SketchNavBar>
        <main style={{ paddingTop: space[6], paddingBottom: space[10] }}>
          <Outlet />
        </main>
      </div>
    </SketchPaper>
  )
}
